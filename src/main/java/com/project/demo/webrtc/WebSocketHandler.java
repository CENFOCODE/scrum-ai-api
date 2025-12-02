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
            case "camera-toggle" -> broadcastToRoom(session, payload); // ← NUEVO
            case "end-call" -> handleEndCall(payload);
            case "leave-room" -> handleLeaveRoom(session, payload);
            case "ping" -> handlePing(session);
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

        rooms.put(room, new ArrayList<>(List.of(session)));
        usernames.put(session, user);
        roles.put(session, role);

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
        boolean camOn = payload.containsKey("camOn") ? (Boolean) payload.get("camOn") : true;

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

        broadcastToRoom(session, Map.of(
                "type", "joinSuccess",
                "message", user + " se ha unido a la sala " + room,
                "user", user,
                "room", room,
                "role", role
        ));

        broadcastToOthersInRoom(room, session, Map.of(
                "type", "camera-toggle",
                "user", user,
                "camOn", camOn
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
    /**
     * Maneja el cambio de estado de cámara de un usuario.
     * Retransmite el cambio a todos los demás en la sala.
     *
     * @param session sesión del usuario que cambió su cámara
     * @param payload JSON con: room, user, camOn
     */
    private void handleCameraToggle(WebSocketSession session, Map<String, Object> payload) throws IOException {
        String room = (String) payload.get("room");
        String user = (String) payload.get("user");
        Boolean camOn = (Boolean) payload.get("camOn");

        if (room == null || !rooms.containsKey(room)) {
            System.out.println("⚠️ Intento de camera-toggle en sala inexistente: " + room);
            return;
        }

        // Mensaje a enviar
        Map<String, Object> cameraMsg = Map.of(
                "type", "camera-toggle",
                "user", user,
                "camOn", camOn
        );

        // Enviar a todos en la sala EXCEPTO al remitente
        broadcastToOthersInRoom(room, session, cameraMsg);

        System.out.println("📹 " + user + " cambió cámara a " + (camOn ? "ON" : "OFF") + " en sala " + room);
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

        if (room == null || !rooms.containsKey(room)) {
            System.out.println("⚠️ Intento de end-call en sala inexistente: " + room);
            return;
        }

        System.out.println("🔴 Finalizando llamada en sala: " + room);

        // Notificar a TODOS los participantes (incluido el host)
        Map<String, Object> endMsg = Map.of(
                "type", "endCall",
                "message", "La llamada fue finalizada por el organizador.",
                "room", room
        );

        String json = mapper.writeValueAsString(endMsg);

        // Enviar mensaje a todos
        for (WebSocketSession s : rooms.get(room)) {
            if (s.isOpen()) {
                try {
                    s.sendMessage(new TextMessage(json));
                } catch (IOException e) {
                    System.err.println("Error enviando endCall a sesión: " + e.getMessage());
                }
            }
        }

        // Limpiar la sala completamente
        List<WebSocketSession> sessionsToRemove = new ArrayList<>(rooms.get(room));
        for (WebSocketSession s : sessionsToRemove) {
            usernames.remove(s);
            roles.remove(s);
        }

        rooms.remove(room);
        System.out.println("✅ Sala " + room + " eliminada completamente");
    }

    /**
     * Maneja cuando un usuario abandona la sala voluntariamente.
     * Notifica a los demás participantes que este usuario se fue.
     *
     * @param session sesión del usuario que se va
     * @param payload JSON con: room, user
     */
    private void handleLeaveRoom(WebSocketSession session, Map<String, Object> payload) throws IOException {
        String room = (String) payload.get("room");
        String user = (String) payload.get("user");

        if (room == null || !rooms.containsKey(room)) {
            return;
        }

        // Notificar a todos los demás en la sala que este usuario se fue
        Map<String, Object> leaveMsg = Map.of(
                "type", "user-left",
                "user", user,
                "room", room,
                "message", user + " ha salido de la sala"
        );

        String json = mapper.writeValueAsString(leaveMsg);

        for (WebSocketSession s : rooms.get(room)) {
            if (s.isOpen() && s != session) {
                s.sendMessage(new TextMessage(json));
            }
        }

        // Remover al usuario de la sala
        rooms.get(room).remove(session);
        usernames.remove(session);
        roles.remove(session);

        // Si la sala queda vacía, se elimina
        if (rooms.get(room).isEmpty()) {
            rooms.remove(room);
            System.out.println("Sala " + room + " eliminada (sin participantes)");
        }

        System.out.println(user + " salió de la sala " + room);
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
     * Envía un mensaje a todos en una sala EXCEPTO a una sesión específica.
     *
     * @param room ID de la sala
     * @param excludeSession sesión que NO debe recibir el mensaje
     * @param message mensaje a enviar
     */
    private void broadcastToOthersInRoom(String room, WebSocketSession excludeSession, Map<String, Object> message) throws IOException {
        if (!rooms.containsKey(room)) return;

        String json = mapper.writeValueAsString(message);

        for (WebSocketSession s : rooms.get(room)) {
            if (s.isOpen() && !s.equals(excludeSession)) {
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
