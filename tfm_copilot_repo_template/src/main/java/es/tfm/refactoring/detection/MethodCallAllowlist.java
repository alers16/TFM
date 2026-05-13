package es.tfm.refactoring.detection;

import java.util.Set;

/**
 * Allowlist de nombres de métodos considerados presumiblemente puros
 * (sin side effects observables) bajo la convención JDK estándar.
 * <p>
 * Se aplica únicamente en modo {@link DetectionMode#RELAXED}.
 * La pureza se asume por nombre, no por análisis interprocedural;
 * es una heurística conservadora documentada como tal en la memoria del TFM.
 * <p>
 * Categorías incluidas:
 * <ul>
 *   <li>Inspectores de colecciones: {@code size}, {@code isEmpty},
 *       {@code contains}, {@code containsKey}, {@code containsValue}.</li>
 *   <li>Inspectores de {@code String}: {@code length}, {@code isEmpty},
 *       {@code isBlank}, {@code equals}, {@code equalsIgnoreCase},
 *       {@code startsWith}, {@code endsWith}, {@code contains},
 *       {@code matches}, {@code regionMatches}.</li>
 *   <li>Inspectores de {@code Optional}: {@code isPresent}, {@code isEmpty}.</li>
 *   <li>Getters convencionales: métodos con prefijo {@code get} o {@code is}
 *       sin argumentos (verificado en contexto por el detector).</li>
 *   <li>Comparadores de objetos: {@code equals}, {@code compareTo}.</li>
 * </ul>
 * <p>
 * Quedan explícitamente fuera del allowlist:
 * <ul>
 *   <li>Métodos con parámetros complejos que puedan producir efectos.</li>
 *   <li>Cualquier método no listado aquí (fallo cerrado por defecto).</li>
 *   <li>Asignaciones, {@code ++}/{@code --}, lambdas e instanciaciones
 *       con {@code new}: siguen siendo descarte incluso en modo RELAXED.</li>
 * </ul>
 */
public final class MethodCallAllowlist {

    private MethodCallAllowlist() {
        // Utility class
    }

    /**
     * Nombres de métodos aceptados como presumiblemente puros en modo RELAXED.
     */
    public static final Set<String> PURE_METHOD_NAMES = Set.of(
            // Collection inspectors
            "size",
            "isEmpty",
            "contains",
            "containsKey",
            "containsValue",
            // String inspectors
            "length",
            "isBlank",
            "equals",
            "equalsIgnoreCase",
            "startsWith",
            "endsWith",
            "matches",
            "regionMatches",
            // Optional inspectors
            "isPresent",
            // Object comparison
            "compareTo",
            "compareToIgnoreCase"
    );

    /**
     * Prefijos de getter convencional (bean pattern).
     * Un método cuyo nombre empieza por alguno de estos prefijos se acepta
     * en modo RELAXED independientemente de si figura en {@link #PURE_METHOD_NAMES}.
     */
    public static final Set<String> GETTER_PREFIXES = Set.of("get", "is", "has");

    /**
     * Devuelve {@code true} si el nombre de método se considera presumiblemente
     * puro bajo la heurística del allowlist.
     *
     * @param methodName nombre del método (sin paréntesis ni argumentos)
     * @return {@code true} si el método está en el allowlist o sigue convención getter
     */
    public static boolean isPresumablyPure(String methodName) {
        if (PURE_METHOD_NAMES.contains(methodName)) {
            return true;
        }
        for (String prefix : GETTER_PREFIXES) {
            if (methodName.startsWith(prefix) && methodName.length() > prefix.length()) {
                return true;
            }
        }
        return false;
    }
}
