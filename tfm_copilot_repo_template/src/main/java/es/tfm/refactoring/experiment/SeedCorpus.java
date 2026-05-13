package es.tfm.refactoring.experiment;

import java.util.ArrayList;
import java.util.List;

/**
 * Corpus inicial controlado para validación del pipeline experimental.
 * <p>
 * Contiene 10 casos sintéticos trazables que cubren:
 * <ul>
 *   <li>5 casos válidos (se espera detección y transformación)</li>
 *   <li>5 casos inválidos (se espera rechazo por distintas precondiciones)</li>
 * </ul>
 * <p>
 * Todos los casos son sintéticos. El corpus real con código de proyectos
 * open-source se incorporará en fases posteriores del TFM.
 * <p>
 * Contribuye a RQ1 (¿en qué casos se pueden combinar condicionales anidados?)
 * demostrando los tipos de casos que el detector acepta y rechaza.
 */
public class SeedCorpus {

    private SeedCorpus() {
        // Utilidad: no instanciar
    }

    /**
     * Carga todos los casos del corpus semilla.
     *
     * @return lista con los 10 casos controlados
     */
    public static List<ExperimentCase> loadAll() {
        List<ExperimentCase> cases = new ArrayList<>();
        cases.add(validSimple());
        cases.add(validDeepNesting());
        cases.add(validInsideLoop());
        cases.add(validOrCondition());
        cases.add(validTwoIndependent());
        cases.add(invalidOuterElse());
        cases.add(invalidInnerElse());
        cases.add(invalidMultipleStatements());
        cases.add(invalidMethodCall());
        cases.add(invalidNoNesting());
        return cases;
    }

    // =========================================================================
    // Casos válidos (se espera detección y transformación)
    // =========================================================================

    /** Patrón canónico if(A){if(B){S}} — caso base del MVP. */
    public static ExperimentCase validSimple() {
        return new ExperimentCase(
                "VALID_SIMPLE",
                "synthetic",
                "Patrón básico if(A){if(B){S}} — caso canónico del MVP",
                wrapInClass("""
                        void process(int a, int b) {
                            if (a > 0) {
                                if (b > 0) {
                                    System.out.println(a + b);
                                }
                            }
                        }
                        """)
        );
    }

    /** Triple anidamiento if-if-if — requiere múltiples pases de punto fijo. */
    public static ExperimentCase validDeepNesting() {
        return new ExperimentCase(
                "VALID_DEEP_NESTING",
                "synthetic",
                "Triple anidamiento if-if-if — requiere múltiples pases de punto fijo",
                wrapInClass("""
                        void deepCheck(int a, int b, int c) {
                            if (a > 0) {
                                if (b > 0) {
                                    if (c > 0) {
                                        System.out.println(a + b + c);
                                    }
                                }
                            }
                        }
                        """)
        );
    }

    /** Patrón anidado dentro de un bucle for — mayor impacto en complejidad. */
    public static ExperimentCase validInsideLoop() {
        return new ExperimentCase(
                "VALID_INSIDE_LOOP",
                "synthetic",
                "Patrón anidado dentro de un bucle for-each — mayor impacto en complejidad",
                wrapInClass("""
                        void filterPositives(int[] values) {
                            for (int v : values) {
                                if (v > 0) {
                                    if (v < 100) {
                                        System.out.println(v);
                                    }
                                }
                            }
                        }
                        """)
        );
    }

    /** Condición con OR — verifica parentización al combinar con &&. */
    public static ExperimentCase validOrCondition() {
        return new ExperimentCase(
                "VALID_OR_CONDITION",
                "synthetic",
                "Condición con OR — requiere parentización al combinar con &&",
                wrapInClass("""
                        void checkRange(int x, int y) {
                            if (x > 0 || x < -10) {
                                if (y > 0) {
                                    System.out.println(x + y);
                                }
                            }
                        }
                        """)
        );
    }

    /** Dos patrones independientes en el mismo método. */
    public static ExperimentCase validTwoIndependent() {
        return new ExperimentCase(
                "VALID_TWO_INDEPENDENT",
                "synthetic",
                "Dos patrones independientes en el mismo método",
                wrapInClass("""
                        void twoPatterns(int a, int b, int c, int d) {
                            if (a > 0) {
                                if (b > 0) {
                                    System.out.println(a + b);
                                }
                            }
                            if (c > 0) {
                                if (d > 0) {
                                    System.out.println(c + d);
                                }
                            }
                        }
                        """)
        );
    }

    // =========================================================================
    // Casos inválidos (se espera rechazo por precondiciones del detector)
    // =========================================================================

    /** If externo con else — excluido por precondición P1. */
    public static ExperimentCase invalidOuterElse() {
        return new ExperimentCase(
                "INVALID_OUTER_ELSE",
                "synthetic",
                "If externo con else — excluido por precondición P1",
                wrapInClass("""
                        void withElse(int a, int b) {
                            if (a > 0) {
                                if (b > 0) {
                                    System.out.println("positive");
                                }
                            } else {
                                System.out.println("negative");
                            }
                        }
                        """)
        );
    }

    /** If interno con else — excluido por precondición P4. */
    public static ExperimentCase invalidInnerElse() {
        return new ExperimentCase(
                "INVALID_INNER_ELSE",
                "synthetic",
                "If interno con else — excluido por precondición P4",
                wrapInClass("""
                        void innerElse(int a, int b) {
                            if (a > 0) {
                                if (b > 0) {
                                    System.out.println("both positive");
                                } else {
                                    System.out.println("a positive, b not");
                                }
                            }
                        }
                        """)
        );
    }

    /** Bloque externo con múltiples sentencias — excluido por precondición P2. */
    public static ExperimentCase invalidMultipleStatements() {
        return new ExperimentCase(
                "INVALID_MULTI_STMT",
                "synthetic",
                "Bloque externo con múltiples sentencias — excluido por precondición P2",
                wrapInClass("""
                        void multiStmt(int a, int b) {
                            if (a > 0) {
                                int x = a * 2;
                                if (b > 0) {
                                    System.out.println(x + b);
                                }
                            }
                        }
                        """)
        );
    }

    /** Condición con llamada a método — excluido por precondición P5 (side effects). */
    public static ExperimentCase invalidMethodCall() {
        return new ExperimentCase(
                "INVALID_METHOD_CALL",
                "synthetic",
                "Condición con llamada a método — excluido por precondición P5 (side effects)",
                wrapInClass("""
                        void methodCallCondition(String s, int b) {
                            if (s.isEmpty()) {
                                if (b > 0) {
                                    System.out.println("empty and positive");
                                }
                            }
                        }
                        """)
        );
    }

    /** If simple sin anidamiento — no hay patrón combinable. */
    public static ExperimentCase invalidNoNesting() {
        return new ExperimentCase(
                "INVALID_NO_NESTING",
                "synthetic",
                "If simple sin anidamiento — no hay patrón combinable",
                wrapInClass("""
                        void simpleIf(int a) {
                            if (a > 0) {
                                System.out.println("positive");
                            }
                        }
                        """)
        );
    }

    // =========================================================================
    // Utilidades
    // =========================================================================

    private static String wrapInClass(String methodBody) {
        return "class TestCase {\n" + methodBody + "}\n";
    }
}
