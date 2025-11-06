package com.project.demo.logic.service.rtc.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio que maneja la lógica de las sesiones WebRTC.
 * Administra las salas de conexión, usuarios y señalización entre pares.
 */
@Service
public class WebRTCService {

    // Mapa que simula las salas activas en memoria
    private final ConcurrentHashMap<String, String> activeRooms = new ConcurrentHashMap<>();

    /**
     * Crea o une un usuario a una sala existente.
     *
     * @param roomId Identificador de la sala.
     * @return Mensaje de confirmación.
     */
    public String joinRoom(String roomId) {
        activeRooms.putIfAbsent(roomId, "active");
        return "✅ Usuario unido a la sala: " + roomId;
    }

    /**
     * Envía un mensaje de señalización a los participantes de la sala.
     *
     * @param roomId Sala destino.
     * @param message Contenido del mensaje (offer, answer o ICE candidate).
     */
    public void sendSignal(String roomId, String message) {
        // Aquí luego se integrará el WebSocket para enviar a los demás usuarios
        System.out.println("📡 Señal enviada a sala " + roomId + ": " + message);
    }
}
