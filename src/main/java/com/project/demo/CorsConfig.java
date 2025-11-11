package com.project.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * Configuración global de CORS para la aplicación.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    /**
     * Lista de orígenes permitidos para CORS.
     * Se carga desde application.properties y soporta valores separados por coma.
     */
    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;
    /**
     * Registra las reglas globales de CORS para todos los endpoints.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // :white_check_mark: Importante: permite CORS en TODAS las rutas
                .allowedOrigins(allowedOrigins.split(",")) // :white_check_mark: Lee múltiples túneles

/**
 * Configuración global de CORS para la aplicación.
 *
 * <p>Este archivo controla qué dominios externos tienen permitido llamar al backend.
 * Es especialmente importante cuando el frontend se ejecuta desde URLs distintas, como:
 * <ul>
 *     <li>Angular en localhost</li>
 *     <li>Cloudflare Tunnel</li>
 *     <li>Ngrok</li>
 * </ul>
 *
 * <p>Los orígenes permitidos se cargan desde el archivo
 * <code>application.properties</code> usando la propiedad:
 *
 * <pre>
 * app.cors.allowed-origins=http://localhost:4200,https://frontend-1.com,...
 * </pre>
 *
 * <p><b>Nota:</b> En producción, estos orígenes deben limitarse a dominios confiables.</p>
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Lista de orígenes permitidos para CORS.
     *
     * <p>Se inyecta automáticamente desde application.properties y puede contener
     * múltiples URLs separadas por coma.</p>
     *
     * Ejemplo:
     * <pre>
     * app.cors.allowed-origins=http://localhost:4200,https://mi-tunnel.ngrok.io
     * </pre>
     */
    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    /**
     * Registra las reglas globales de CORS usadas por todos los endpoints REST.
     *
     * @param registry manejador de reglas CORS proporcionado por Spring
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // ✅ Separa automáticamente la propiedad CSV en un arreglo de Strings
                .allowedOrigins(allowedOrigins.split(","))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
