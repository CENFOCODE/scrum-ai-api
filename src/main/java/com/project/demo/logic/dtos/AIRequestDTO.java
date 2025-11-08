package com.project.demo.logic.dtos;

/**
 * DTO utilizado para recibir solicitudes dirigidas al motor de IA (Groq).
 *
 * <p>Este objeto se envía desde el frontend y contiene el prompt o mensaje
 * que la IA deberá procesar. Mantiene la entrada limpia y separada del modelo
 * interno, cumpliendo buenas prácticas de encapsulación.</p>
 *
 * <p><b>Uso típico:</b></p>
 * <pre>
 *   POST /api/ai/evaluate
 *   {
 *       "prompt": "Evalúa esta historia de usuario..."
 *   }
 * </pre>
 *
 * <p>El servicio correspondiente leerá el campo {@code prompt}
 * para construir el mensaje enviado al modelo Groq/LLaMA.</p>
 */
public class AIRequestDTO {

    /** Prompt enviado desde el frontend hacia el motor de IA. */
    private String prompt;

    /**
     * Obtiene el prompt enviado por el usuario.
     *
     * @return el contenido del prompt en formato texto
     */
    public String getPrompt() {
        return prompt;
    }

    /**
     * Establece el prompt que será evaluado por la IA.
     *
     * @param prompt texto a procesar
     */
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
