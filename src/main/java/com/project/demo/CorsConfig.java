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
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
