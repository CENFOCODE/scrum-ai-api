package com.project.demo.logic.dtos;

/**
 * Data Transfer Object que representa la respuesta devuelta por el servicio de IA.
 *
 * <p>
 * Este objeto contiene:
 * <ul>
 *     <li>La respuesta generada por el modelo de IA.</li>
 *     <li>O un mensaje opcional que puede indicar un error u otro tipo de información.</li>
 * </ul>
 * </p>
 */
public class AIResponseDTO {

    /** Respuesta textual generada por el modelo de IA. */
    private String answer;

    /** Mensaje adicional, generalmente usado para errores o avisos del sistema. */
    private String message;

    /**
     * Obtiene la respuesta generada por la IA.
     *
     * @return texto de la respuesta generada
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Establece la respuesta generada por la IA.
     *
     * @param answer texto de respuesta generado por el modelo
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * Obtiene el mensaje asociado a la respuesta.
     *
     * @return mensaje informativo o de error
     */
    public String getMessage() {
        return message;
    }

    /**
     * Establece el mensaje asociado a la respuesta.
     *
     * @param message mensaje informativo o de error
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
