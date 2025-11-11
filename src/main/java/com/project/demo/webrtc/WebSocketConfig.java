package com.project.demo.webrtc;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.project.demo.webrtc.WebSocketHandler;

/**
 * Configuración del endpoint WebSocket utilizado por la aplicación.
 *
 * <p>
 * Esta clase registra el manejador WebSocket y expone el endpoint
 * <strong>/webrtc</strong>, necesario para la señalización WebRTC entre los clientes Angular.
 * </p>
 *
 * <p>
 * La señalización es el mecanismo mediante el cual los navegadores intercambian:
 * <ul>
 *     <li>offers</li>
 *     <li>answers</li>
 *     <li>ICE candidates</li>
 *     <li>invites</li>
 *     <li>mensajes de sala</li>
 * </ul>
 * antes de crear la conexión P2P real entre los usuarios.
 * </p>
 *
 * <p>
 * El backend NO transmite video ni audio, únicamente reenvía mensajes entre usuarios.
 * </p>
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    /** Manejador principal encargado de procesar y reenviar los mensajes de señalización. */
    private final WebSocketHandler webSocketHandler;

    /**
     * Constructor que inyecta el WebSocketHandler requerido.
     *
     * @param webSocketHandler manejador encargado de procesar mensajes WebRTC
     */
    public WebSocketConfig(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    /**
     * Registra los endpoints WebSocket disponibles para los clientes.
     *
     * <p>
     * En este proyecto solo se expone un endpoint: <strong>/webrtc</strong>.
     * </p>
     *
     * @param registry registro global donde se agregan los handlers WebSocket
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(webSocketHandler, "/webrtc")
                .setAllowedOrigins("*"); // En producción, reemplazar por dominios permitidos
    }
}
