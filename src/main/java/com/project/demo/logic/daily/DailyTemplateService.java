package com.project.demo.logic.daily;

import com.project.demo.logic.entity.scenarioTemplate.ScenarioTemplate;
import com.project.demo.logic.entity.scenarioTemplate.ScenarioTemplateRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de obtener el prompt base (plantilla) de la ceremonia Daily
 * desde la tabla {@code scenario_templates}.
 *
 * <p>
 * Este servicio es utilizado por {@link com.project.demo.logic.service.rtc.service.GroqService}
 * para construir el prompt final que será enviado a la IA.
 * </p>
 *
 * <h3>¿Cómo funciona?</h3>
 * <ul>
 *     <li>Cada plantilla en la base de datos está asociada a un escenario (Daily = ID 2).</li>
 *     <li>Las plantillas se generan con un {@code stepOrder} calculado así:
 *          <pre>(dificultad * 1000) + rolIndex</pre>
 *     </li>
 *     <li>El rolIndex es:
 *         <ul>
 *             <li>1 → Scrum Master</li>
 *             <li>2 → Developer</li>
 *             <li>3 → Product Owner</li>
 *             <li>4 → QA</li>
 *         </ul>
 *     </li>
 *     <li>Este servicio combina dificultad + rol para encontrar exactamente la plantilla correcta.</li>
 * </ul>
 *
 * <p>
 * Ejemplo:
 * <ul>
 *     <li>Dificultad: 2 (Media)</li>
 *     <li>Rol: Developer</li>
 *     <li>stepOrder = 2000 + 2 = 2002</li>
 *     <li>Consulta en BD: scenarioId = 2, stepOrder = 2002</li>
 * </ul>
 * </p>
 */
@Service
public class DailyTemplateService {

    /** Repositorio JPA que permite buscar plantillas en la BD. */
    private final ScenarioTemplateRepository repository;

    /**
     * Constructor por inyección de dependencias.
     *
     * @param repository Repositorio que accede a scenario_templates.
     */
    public DailyTemplateService(ScenarioTemplateRepository repository) {
        this.repository = repository;
    }

    /**
     * Obtiene la plantilla (prompt base) de la ceremonia Daily desde la BD
     * según la dificultad elegida y el rol actual del usuario.
     *
     * <p>
     *   Este método SIEMPRE usa el escenario con ID 2 (Daily),
     *   que fue insertado por el Seeder automáticamente.
     * </p>
     *
     * @param difficulty Dificultad seleccionada (1=Fácil, 2=Media, 3=Alta).
     * @param role Rol actual del usuario:
     *             <ul>
     *                 <li>Scrum Master</li>
     *                 <li>Developer</li>
     *                 <li>Product Owner</li>
     *                 <li>QA</li>
     *             </ul>
     * @return El {@link ScenarioTemplate} encontrado, o {@code null} si no existe.
     */
    public ScenarioTemplate getDailyTemplate(int difficulty, String role) {
        int stepOrder = buildStepOrder(difficulty, role);
        return repository.findByScenarioIdAndStepOrder(2L, stepOrder).orElse(null);
    }

    /**
     * Construye el stepOrder en el formato:
     * <pre>(dificultad * 1000) + rolIndex</pre>
     *
     * @param difficulty Dificultad (1–3).
     * @param role Rol humano seleccionando.
     * @return Valor único para buscar la plantilla correctamente.
     */
    private int buildStepOrder(int difficulty, String role) {
        return (difficulty * 1000) + getRoleIndex(role);
    }

    /**
     * Devuelve el índice numérico asociado a cada rol.
     *
     * @param role Nombre del rol.
     * @return Índice interno (1–4).
     */
    private int getRoleIndex(String role) {
        switch (role) {
            case "Scrum Master": return 1;
            case "Developer": return 2;
            case "Product Owner": return 3;
            case "QA": return 4;
            default: return 1;
        }
    }
}
