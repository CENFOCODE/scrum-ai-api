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
 * Manejador central de WebSocket para la señalización de WebRTC
 * dentro de ScrumAI. Este componente NO transmite audio/video,
 * solamente envía y recibe mensajes de control entre usuarios:
 *
 *  - Crear salas
 *  - Unirse a salas
 *  - Invitar usuarios
 *  - Enviar/recibir Offer, Answer, ICE candidates
 *  - Manejar desconexiones y reconexiones
 *
 * Funciona como un "router" de mensajes basado en:
 *  - Sala (room)
 *  - Usuario objetivo (to)
 *  - Usuario emisor (from)
 *
 * Es completamente stateless respecto a medios multimedia,
 * simplemente reenvía mensajes JSON a los WebSocketSession correctos.
 *
 * Es parte esencial del sistema de videollamadas grupales P2P.
 */
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    /** Convertidor JSON <-> Map */
    private final ObjectMapper mapper = new ObjectMapper();

    /** Mapa de salas: roomId -> lista de sesiones */
    private final Map<String, List<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    /** Mapeo de sala -> ceremonySessionId */
    private final Map<String, Long> roomCeremonySessions = new ConcurrentHashMap<>();

    /** Mapeo de sesión -> username */
    private final Map<WebSocketSession, String> usernames = new ConcurrentHashMap<>();

    /** Mapeo de sesión -> rol (Scrum Master, Developer, etc.) */
    private final Map<WebSocketSession, String> roles = new ConcurrentHashMap<>();

    /** Mapeo de sessionId -> sesión */
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    /**
     * Se ejecuta cuando un cliente abre la conexión WebSocket.
     * No asigna usuario todavía; eso se hace con "register-user".
     *
     * @param session sesión WebSocket recién iniciada.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
        System.out.println("🟢 Nueva conexión WebSocket: " + session.getId());
    }

    /**
     * Procesa mensajes entrantes de WebSocket. Cada mensaje debe
     * incluir un campo "type" que determina la acción a ejecutar.
     *
     * Tipos soportados:
     *   - register-user
     *   - create-room
     *   - join
     *   - invite
     *   - offer / answer / ice
     *   - end-call
     *   - ping
     *
     * @param session sesión remitente.
     * @param message mensaje recibido.
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
            case "offer", "answer", "ice" -> broadcastToRoom(session, payload);
            case "end-call" -> handleEndCall(payload);
            case "ping" -> handlePing(session);
            case "transcript"-> handleTranscript(session, payload);
            default -> System.out.println("⚠️ Tipo de mensaje no reconocido: " + type);
        }
    }

    /**
     * Registra un usuario luego de que abre el WebSocket.
     *
     * @param session sesión del usuario.
     * @param payload JSON con: username
     */
    private void handleRegisterUser(WebSocketSession session, Map<String, Object> payload) throws IOException {
        String username = (String) payload.get("username");
        usernames.put(session, username);

        session.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                "type", "register-success",
                "message", "Usuario registrado correctamente como " + username
        ))));

        System.out.println("👤 Usuario registrado: " + username);
    }

    /**
     * Crea una nueva sala WebRTC.
     *
     * @param session sesión que crea la sala (host).
     * @param payload JSON con: room, host, role
     */
    private void handleCreateRoom(WebSocketSession session, Map<String, Object> payload) throws IOException {
        String room = (String) payload.get("room");
        String user = (String) payload.get("host");
        String role = (String) payload.getOrDefault("role", "Host");

        Object ceremonySessionIdObj = payload.get("ceremonySessionId");
        Long ceremonySessionId = null;

        if (ceremonySessionIdObj instanceof Number) {
            ceremonySessionId = ((Number) ceremonySessionIdObj).longValue();
        }

        rooms.put(room, new ArrayList<>(List.of(session)));
        usernames.put(session, user);
        roles.put(session, role);


        if (ceremonySessionId != null) {
            roomCeremonySessions.put(room, ceremonySessionId);

        }

        session.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                "type", "joinSuccess",
                "message", "Sala creada con éxito: " + room
        ))));

        System.out.println("🏗️ Sala creada: " + room + " por " + user);
    }

    /**
     * Un usuario intenta unirse a una sala existente.
     *
     * @param session sesión del usuario.
     * @param payload JSON con: room, user, role
     */
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

        // ✅ REGLA DE NEGOCIO: Un solo rol por sala, excepto Developer
        if (!role.equalsIgnoreCase("Developer")) {
            boolean roleUsed = rooms.get(room).stream().anyMatch(s ->
                    role.equalsIgnoreCase(roles.get(s))
            );

            if (roleUsed) {
                session.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                        "type", "roleError",
                        "message", "El rol '" + role + "' ya está siendo utilizado en esta sala. Solo puede repetirse Developer."
                ))));
                return;
            }
        }

        // ✅ Si pasa la validación, agregar a la sala
        rooms.get(room).add(session);
        usernames.put(session, user);
        roles.put(session, role);

        Long ceremonySessionId = roomCeremonySessions.get(room);

        broadcastToRoom(session, Map.of(
                "type", "joinSuccess",
                "message", user + " se ha unido a la sala " + room,
                "user", user,
                "role", role,
                "ceremonySessionId", ceremonySessionId

        ));

        System.out.println("👋 " + user + " se unió a la sala " + room + " como " + role);
    }


    /**
     * Envía una invitación a un usuario específico.
     *
     * @param session sesión del remitente.
     * @param payload JSON con: to, from, room
     */
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
    private void handleTranscript(WebSocketSession sender, Map<String, Object> payload) throws IOException {
        String room = (String) payload.get("room");

        if (room == null || !rooms.containsKey(room)) {
            return;
        }
        String json = mapper.writeValueAsString(payload);

        for (WebSocketSession session : rooms.get(room)) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(json));
            }
        }
    }
    /**
     * Mantiene viva la conexión WebSocket (ping/pong).
     */
    private void handlePing(WebSocketSession session) {
        // Intencionalmente vacío
    }

    /**
     * Finaliza una llamada y elimina la sala.
     *
     * @param payload JSON con: room
     */
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
            roomCeremonySessions.remove(room);
            System.out.println("🔴 Llamada finalizada y sala eliminada: " + room);
        }
    }

    /**
     * Reenvía señales WebRTC dentro de una sala.
     *
     * Si el mensaje contiene "to": envío directo.
     * Si no: broadcast a todos menos el remitente.
     *
     * @param sender sesión que envía la señal.
     * @param message JSON que contiene offer/answer/ice.
     */
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

        if (toUser != null) {
            for (Map.Entry<WebSocketSession, String> entry : usernames.entrySet()) {
                if (entry.getValue().equalsIgnoreCase(toUser)) {
                    WebSocketSession target = entry.getKey();
                    if (target.isOpen()) {
                        target.sendMessage(new TextMessage(json));
                        System.out.println("📤 Mensaje dirigido a " + toUser + ": " + message.get("type"));
                    }
                    return;
                }
            }
            System.out.println("⚠️ Usuario destino no encontrado: " + toUser);
            return;
        }

        for (WebSocketSession s : rooms.get(room)) {
            if (s.isOpen() && s != sender) {
                s.sendMessage(new TextMessage(json));
            }
        }
    }

    /**
     * Maneja desconexiones. Si un usuario ya estaba en sala,
     * se le da 1 minuto para reconectar antes de eliminarlo.
     *
     * @param session sesión cerrada.
     * @param status motivo del cierre.
     */
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
            }, 60000); // 1 min
        } else {
            usernames.remove(session);
            roles.remove(session);
            rooms.values().forEach(list -> list.remove(session));
            sessions.remove(session.getId());
            System.out.println("❌ Sesión cerrada (sin sala): " + user);
        }
    }
}
