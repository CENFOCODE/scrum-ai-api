package com.project.demo.webrtc;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocketHandler
 * -------------------------------------------------------------
 * Manejador central de WebSocket para la señalización WebRTC en ScrumAI.
 *
 * Funciones principales:
 *  • Crear y gestionar salas WebRTC.
 *  • Validar roles Scrum únicos por sala.
 *  • Enrutar mensajes de señalización (offer/answer/ice).
 *  • Enviar snapshots de la sala al nuevo usuario para conexión P2P completa.
 *  • Manejar invitaciones, desconexiones y finalización de sala.
 *
 * Este handler no transmite medios, únicamente reenvía mensajes JSON.
 */
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    private final Map<String, List<WebSocketSession>> rooms = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, String> usernames = new ConcurrentHashMap<>();
    private final Map<WebSocketSession, String> roles = new ConcurrentHashMap<>();
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    /**
     * Se ejecuta cuando un cliente abre una nueva conexión WebSocket.
     *
     * @param session sesión creada.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        System.out.println("🟢 Nueva conexión WebSocket: " + session.getId());
    }

    /**
     * Procesa los mensajes entrantes según su tipo.
     *
     * Tipos soportados:
     *  - register-user
     *  - create-room
     *  - join
     *  - invite
     *  - offer / answer / ice
     *  - end-call
     *  - ping
     *
     * @param session sesión remitente.
     * @param message mensaje recibido en formato JSON.
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        Map<String, Object> payload = mapper.readValue(message.getPayload(), Map.class);
        String type = (String) payload.get("type");

        switch (type) {
            case "register-user" -> handleRegisterUser(session, payload);
            case "create-room" -> handleCreateRoom(session, payload);
            case "join" -> handleJoinRoom(session, payload);
            case "invite" -> handleInvite(session, payload);
            case "offer", "answer", "ice" -> broadcastSignal(session, payload);
            case "end-call" -> handleEndCall(payload);
            case "ping" -> {}
            default -> System.out.println("⚠️ Tipo desconocido: " + type);
        }
    }

    /**
     * Registra un usuario asociado a una sesión WebSocket.
     *
     * @param session sesión del usuario.
     * @param payload payload con el nombre de usuario.
     */
    private void handleRegisterUser(WebSocketSession session, Map<String, Object> payload) throws IOException {
        String username = (String) payload.get("username");
        usernames.put(session, username);

        session.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                "type", "register-success",
                "message", "Registrado como " + username
        ))));

        System.out.println("👤 Usuario registrado: " + username);
    }

    /**
     * Crea una sala nueva y agrega al usuario como host.
     *
     * @param session sesión del host.
     * @param payload información de la sala y rol.
     */
    private void handleCreateRoom(WebSocketSession session, Map<String, Object> payload) throws IOException {
        String room = (String) payload.get("room");
        String username = (String) payload.get("host");
        String role = (String) payload.getOrDefault("role", "Host");

        rooms.put(room, new ArrayList<>(List.of(session)));
        usernames.put(session, username);
        roles.put(session, role);

        session.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                "type", "joinSuccess",
                "message", "Sala creada",
                "user", username
        ))));

        sendRoomSnapshot(room, session);

        System.out.println("🏗️ Sala creada: " + room + " por " + username);
    }

    /**
     * Permite que un usuario se una a una sala existente.
     * Valida roles únicos y envía snapshot al nuevo usuario.
     *
     * @param session sesión que se une.
     * @param payload contenedor con room, user, role.
     */
    private void handleJoinRoom(WebSocketSession session, Map<String, Object> payload) throws IOException {
        String room = (String) payload.get("room");
        String username = (String) payload.get("user");
        String role = (String) payload.getOrDefault("role", "Invitado");

        if (!rooms.containsKey(room)) {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                    "type", "joinError",
                    "message", "La sala no existe"
            ))));
            return;
        }

        if (!role.equalsIgnoreCase("Developer")) {
            boolean roleUsed = rooms.get(room).stream().anyMatch(s ->
                    role.equalsIgnoreCase(roles.get(s))
            );
            if (roleUsed) {
                session.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                        "type", "roleError",
                        "message", "El rol ya está en uso"
                ))));
                return;
            }
        }

        rooms.get(room).add(session);
        usernames.put(session, username);
        roles.put(session, role);

        broadcastToAll(room, Map.of(
                "type", "joinSuccess",
                "user", username,
                "role", role
        ));

        sendRoomSnapshot(room, session);

        System.out.println("👋 " + username + " se unió a " + room + " como " + role);
    }

    /**
     * Envía un snapshot completo de los usuarios en la sala al nuevo usuario.
     *
     * @param room sala a la que se unió.
     * @param newUser sesión del usuario nuevo.
     */
    private void sendRoomSnapshot(String room, WebSocketSession newUser) throws IOException {
        List<WebSocketSession> participants = rooms.get(room);
        if (participants == null) return;

        List<Map<String, String>> list = new ArrayList<>();

        for (WebSocketSession s : participants) {
            list.add(Map.of(
                    "username", usernames.get(s),
                    "role", roles.get(s)
            ));
        }

        newUser.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                "type", "ROOM_SNAPSHOT",
                "room", room,
                "users", list
        ))));
    }

    /**
     * Envía una invitación a un usuario por nombre.
     *
     * @param session sesión remitente.
     * @param payload datos de invitación.
     */
    private void handleInvite(WebSocketSession session, Map<String, Object> payload) throws IOException {
        String to = (String) payload.get("to");
        String from = (String) payload.get("from");
        String room = (String) payload.get("room");

        Map<String, Object> invite = Map.of(
                "type", "invite",
                "message", from + " te invita a " + room,
                "room", room,
                "to", to
        );

        boolean sent = false;

        for (Map.Entry<WebSocketSession, String> entry : usernames.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(to)) {
                entry.getKey().sendMessage(new TextMessage(mapper.writeValueAsString(invite)));
                sent = true;
                break;
            }
        }

        if (!sent) {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                    "type", "error",
                    "message", "Usuario no encontrado"
            ))));
        }
    }

    /**
     * Reenvía señales WebRTC dentro de una sala.
     *
     * Si contiene "to", es envío directo.
     * Si no contiene "to", es broadcast al resto.
     *
     * @param sender sesión que originó la señal.
     * @param message mapa con offer/answer/ice.
     */
    private void broadcastSignal(WebSocketSession sender, Map<String, Object> message) throws IOException {
        String room = getRoomOfSession(sender);
        if (room == null) return;

        String to = (String) message.get("to");
        String json = mapper.writeValueAsString(message);

        if (to != null) {
            for (Map.Entry<WebSocketSession, String> entry : usernames.entrySet()) {
                if (entry.getValue().equalsIgnoreCase(to)) {
                    entry.getKey().sendMessage(new TextMessage(json));
                    return;
                }
            }
            return;
        }

        for (WebSocketSession ws : rooms.get(room)) {
            if (ws != sender && ws.isOpen()) {
                ws.sendMessage(new TextMessage(json));
            }
        }
    }

    /**
     * Finaliza una llamada y elimina la sala.
     *
     * @param payload contiene el room a cerrar.
     */
    private void handleEndCall(Map<String, Object> payload) throws IOException {
        String room = (String) payload.get("room");

        if (rooms.containsKey(room)) {
            for (WebSocketSession s : rooms.get(room)) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                            "type", "endCall",
                            "message", "Llamada finalizada"
                    ))));
                }
            }

            rooms.remove(room);
            System.out.println("🔴 Sala eliminada: " + room);
        }
    }

    /**
     * Elimina una sesión de la sala y realiza limpieza
     * cuando un WebSocket se desconecta.
     *
     * @param session sesión cerrada.
     * @param status motivo del cierre.
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String room = getRoomOfSession(session);
        String user = usernames.getOrDefault(session, session.getId());

        usernames.remove(session);
        roles.remove(session);

        if (room != null) {
            rooms.get(room).remove(session);

            if (rooms.get(room).isEmpty()) {
                rooms.remove(room);
                System.out.println("🗑️ Sala vacía eliminada: " + room);
            }
        }

        System.out.println("❌ Sesión cerrada: " + user);
    }

    /**
     * Obtiene la sala a la que pertenece una sesión.
     *
     * @param sender sesión WebSocket.
     * @return roomId o null.
     */
    private String getRoomOfSession(WebSocketSession sender) {
        for (Map.Entry<String, List<WebSocketSession>> entry : rooms.entrySet()) {
            if (entry.getValue().contains(sender)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Envía un mensaje a todos los usuarios de una sala.
     *
     * @param room sala destino.
     * @param msg mapa JSON.
     */
    private void broadcastToAll(String room, Map<String, Object> msg) throws IOException {
        String json = mapper.writeValueAsString(msg);
        for (WebSocketSession s : rooms.get(room)) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(json));
            }
        }
    }
}
