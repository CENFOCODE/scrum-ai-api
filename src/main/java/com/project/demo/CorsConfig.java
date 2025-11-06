package com.project.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 🌐 Configuración Global de CORS (Spring Boot 3+)
 * ---------------------------------------------------------
 * Permite comunicación entre Angular (Cloudflare/ngrok)
 * y tu backend expuesto por ngrok.
 *
 * ⚠️ Solo para desarrollo: eliminar o restringir en producción.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:4200",
                        "https://jelsoft-binding-joins-pulse.trycloudflare.com", // frontend público
                        "https://frore-paz-comprehensibly.ngrok-free.dev"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
