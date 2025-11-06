package com.project.demo.webrtc;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    private final Map<String, List<WebSocketSession>> rooms = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, String> usernames = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, String> roles = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        System.out.println("🟢 Nueva conexión WebSocket: " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        Map<String, Object> payload = mapper.readValue(message.getPayload(), Map.class);
        String type = (String) payload.get("type");

        switch (type) {
            case "register-user" -> handleRegisterUser(session, payload);
            case "create-room" -> handleCreateRoom(session, payload);
            case "join" -> handleJoinRoom(session, payload);
            case "invite" -> handleInvite(session, payload);
            case "offer", "answer", "ice" -> broadcastToRoom(session, payload);
            case "end-call" -> handleEndCall(payload);
            case "ping" -> handlePing(session);
            default -> System.out.println("⚠️ Tipo de mensaje no reconocido: " + type);
        }
    }

    private void handleRegisterUser(WebSocketSession session, Map<String, Object> payload) throws IOException {
        String username = (String) payload.get("username");
        usernames.put(session, username);

        session.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                "type", "register-success",
                "message", "Usuario registrado correctamente como " + username
        ))));
        System.out.println("👤 Usuario registrado: " + username);
    }

    private void handleCreateRoom(WebSocketSession session, Map<String, Object> payload) throws IOException {
        String room = (String) payload.get("room");
        String user = (String) payload.get("host");
        String role = (String) payload.getOrDefault("role", "Host");

        rooms.put(room, new ArrayList<>(List.of(session)));
        usernames.put(session, user);
        roles.put(session, role);

        session.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                "type", "joinSuccess",
                "message", "Sala creada con éxito: " + room
        ))));

        System.out.println("🏗️ Sala creada: " + room + " por " + user);
    }

    private void handleJoinRoom(WebSocketSession session, Map<String, Object> payload) throws IOException {
        String room = (String) payload.get("room");
        String user = (String) payload.get("user");
        String role = (String) payload.getOrDefault("role", "Invitado");

        if (!rooms.containsKey(room)) {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                    "type", "joinError",
                    "message", "La sala " + room + " no existe."
            ))));
            return;
        }

        rooms.get(room).add(session);
        usernames.put(session, user);
        roles.put(session, role);

        broadcastToRoom(session, Map.of(
                "type", "joinSuccess",
                "message", user + " se ha unido a la sala " + room,
                "user", user,
                "role", role
        ));

        System.out.println("👋 " + user + " se unió a la sala " + room);
    }

    private void handleInvite(WebSocketSession session, Map<String, Object> payload) throws IOException {
        String to = (String) payload.get("to");
        String from = (String) payload.get("from");
        String room = (String) payload.get("room");

        Map<String, Object> inviteMsg = Map.of(
                "type", "invite",
                "message", from + " te ha invitado a la sala " + room,
                "to", to,
                "room", room
        );

        boolean sent = false;

        for (Map.Entry<WebSocketSession, String> entry : usernames.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(to)) {
                entry.getKey().sendMessage(new TextMessage(mapper.writeValueAsString(inviteMsg)));
                System.out.println("💌 Invitación enviada a " + to + " por " + from);
                sent = true;
                break;
            }
        }

        if (!sent) {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                    "type", "error",
                    "message", "Usuario " + to + " no encontrado o no conectado."
            ))));
        }
    }

    private void handlePing(WebSocketSession session) {
        // No hace nada: solo evita desconexiones
    }

    private void handleEndCall(Map<String, Object> payload) throws IOException {
        String room = (String) payload.get("room");
        if (rooms.containsKey(room)) {
            for (WebSocketSession s : rooms.get(room)) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                            "type", "endCall",
                            "message", "La llamada fue finalizada por el organizador."
                    ))));
                }
            }
            rooms.remove(room);
            System.out.println("🔴 Llamada finalizada y sala eliminada: " + room);
        }
    }

    private void broadcastToRoom(WebSocketSession sender, Map<String, Object> message) throws IOException {
        String room = null;
        for (Map.Entry<String, List<WebSocketSession>> entry : rooms.entrySet()) {
            if (entry.getValue().contains(sender)) {
                room = entry.getKey();
                break;
            }
        }
        if (room == null) return;

        String toUser = (String) message.get("to");
        String json = mapper.writeValueAsString(message);

        // Si el mensaje tiene "to", enviarlo solo a esa persona
        if (toUser != null) {
            for (Map.Entry<WebSocketSession, String> entry : usernames.entrySet()) {
                if (entry.getValue().equalsIgnoreCase(toUser)) {
                    WebSocketSession targetSession = entry.getKey();
                    if (targetSession.isOpen()) {
                        targetSession.sendMessage(new TextMessage(json));
                        System.out.println("📤 Mensaje dirigido a " + toUser + ": " + message.get("type"));
                    }
                    return;
                }
            }
            System.out.println("⚠️ Usuario destino no encontrado: " + toUser);
            return;
        }

        // Si no tiene "to", enviarlo a todos menos al emisor (mensajes generales)
        for (WebSocketSession s : rooms.get(room)) {
            if (s.isOpen() && s != sender) {
                s.sendMessage(new TextMessage(json));
            }
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String user = usernames.getOrDefault(session, session.getId());
        boolean isInRoom = rooms.values().stream().anyMatch(list -> list.contains(session));

        if (isInRoom) {
            System.out.println("⚠️ " + user + " parece desconectarse temporalmente. Esperando antes de cerrar...");

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    boolean stillInRoom = rooms.values().stream().anyMatch(list -> list.contains(session));
                    if (!session.isOpen() && stillInRoom) {
                        usernames.remove(session);
                        roles.remove(session);
                        rooms.values().forEach(list -> list.remove(session));
                        sessions.remove(session.getId());
                        System.out.println("❌ Sesión cerrada (expirada): " + user);
                    } else {
                        System.out.println("✅ " + user + " se reconectó a tiempo, sesión conservada.");
                    }
                }
            }, 60000); // 🕒 1 minuto
        } else {
            usernames.remove(session);
            roles.remove(session);
            rooms.values().forEach(list -> list.remove(session));
            sessions.remove(session.getId());
            System.out.println("❌ Sesión cerrada (sin sala): " + user);
        }
    }
}
