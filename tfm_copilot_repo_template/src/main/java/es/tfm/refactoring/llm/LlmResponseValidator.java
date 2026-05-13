package es.tfm.refactoring.llm;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import es.tfm.refactoring.analysis.CognitiveComplexityCalculator;
import es.tfm.refactoring.experiment.ExperimentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Oráculo de validación para respuestas de LLM en el protocolo experimental de RQ3.
 * <p>
 * Evalúa la salida de un LLM según los siguientes criterios:
 * <ol>
 *   <li><strong>Parseabilidad:</strong> ¿el código extraído es Java válido?</li>
 *   <li><strong>Preservación estructural:</strong> ¿mantiene la firma del método
 *       y la estructura de la clase?</li>
 *   <li><strong>Guarda de elegibilidad:</strong> si el baseline declara el caso
 *       como no elegible (por precondiciones P1–P5), cualquier transformación
 *       del LLM que modifique la CC se clasifica como INCORRECT. Solo el rechazo
 *       explícito o la no-transformación son aceptables.</li>
 *   <li><strong>Reducción de CC:</strong> ¿mejora la complejidad cognitiva estimada?</li>
 *   <li><strong>Coherencia con baseline:</strong> ¿el delta coincide con el del
 *       prototipo determinista?</li>
 *   <li><strong>Rechazo correcto:</strong> ¿rechaza adecuadamente los casos
 *       no elegibles?</li>
 * </ol>
 * <p>
 * <strong>Política de SUCCESS (v1.1):</strong> una salida solo puede ser SUCCESS si:
 * (a) es parseable, (b) no viola precondiciones de elegibilidad del baseline,
 * (c) no aplica transformaciones en casos no elegibles, y (d) resulta válida
 * frente al baseline/prototipo.
 * <p>
 * <strong>RQ3:</strong> ¿Pueden los grandes modelos de lenguaje realizar
 * esta refactorización de forma correcta y automática?
 */
public class LlmResponseValidator {

    /** Marcador de rechazo explícito del LLM. */
    public static final String REFUSAL_MARKER = "NO_REFACTORING_APPLICABLE";

    private static final Pattern CODE_BLOCK_PATTERN =
            Pattern.compile("```java\\s*\\n(.*?)\\n\\s*```", Pattern.DOTALL);

    private final CognitiveComplexityCalculator calculator;

    public LlmResponseValidator() {
        this.calculator = new CognitiveComplexityCalculator();
    }

    public LlmResponseValidator(CognitiveComplexityCalculator calculator) {
        this.calculator = calculator;
    }

    /**
     * Evalúa una respuesta de LLM contra el caso original y el resultado baseline.
     *
     * @param caseId        identificador del caso
     * @param model         modelo LLM evaluado
     * @param promptVersion versión del prompt
     * @param attemptNumber número de intento
     * @param rawOutput     salida completa del LLM
     * @param originalCode  código fuente original del caso
     * @param baseline      resultado del prototipo determinista para el mismo caso
     * @return resultado de evaluación con veredicto y métricas
     */
    public LlmEvaluationResult evaluate(String caseId, String model,
                                        String promptVersion, int attemptNumber,
                                        String rawOutput,
                                        String originalCode,
                                        ExperimentResult baseline) {
        List<String> errors = new ArrayList<>();
        int ccBefore = baseline.getComplexityBefore();

        // 1. Detectar rechazo explícito
        if (isRefusal(rawOutput)) {
            return buildRefusalResult(caseId, model, promptVersion, attemptNumber,
                    rawOutput, ccBefore, baseline);
        }

        // 2. Extraer código Java de la respuesta
        String extracted = extractCode(rawOutput);
        if (extracted == null) {
            errors.add("No se encontró bloque de código ```java en la respuesta");
            return buildErrorResult(caseId, model, promptVersion, attemptNumber,
                    rawOutput, null, LlmVerdict.INVALID_OUTPUT,
                    "Sin bloque de código Java", ccBefore, baseline, errors);
        }

        // 3. Verificar parseabilidad
        CompilationUnit cu;
        try {
            cu = StaticJavaParser.parse(extracted);
        } catch (Exception e) {
            errors.add("Error de parseo: " + e.getMessage());
            return buildErrorResult(caseId, model, promptVersion, attemptNumber,
                    rawOutput, extracted, LlmVerdict.INVALID_OUTPUT,
                    "Código no parseable", ccBefore, baseline, errors);
        }

        // 4. Verificar que contiene al menos un método
        Optional<MethodDeclaration> methodOpt = cu.findFirst(MethodDeclaration.class);
        if (methodOpt.isEmpty()) {
            errors.add("No se encontró ningún método en el código extraído");
            return buildErrorResult(caseId, model, promptVersion, attemptNumber,
                    rawOutput, extracted, LlmVerdict.INVALID_OUTPUT,
                    "Sin método", ccBefore, baseline, errors);
        }

        // 5. Verificar preservación de firma del método
        MethodDeclaration llmMethod = methodOpt.get();
        Optional<MethodDeclaration> origMethodOpt = parseOriginalMethod(originalCode);
        if (origMethodOpt.isPresent()) {
            String origSig = origMethodOpt.get()
                    .getDeclarationAsString(false, false, false);
            String llmSig = llmMethod
                    .getDeclarationAsString(false, false, false);
            if (!origSig.equals(llmSig)) {
                errors.add("Firma de método modificada: original='"
                        + origSig + "' llm='" + llmSig + "'");
            }
        }

        // 6. Medir complejidad after
        int ccAfter = calculator.calculate(llmMethod);
        int delta = ccAfter - ccBefore;

        // 7. Comparar con baseline
        boolean matchesBaseline = (delta == baseline.getDelta());

        // 8. Asignar veredicto
        LlmVerdict verdict;
        String reason;

        if (!errors.isEmpty()) {
            verdict = LlmVerdict.INCORRECT;
            reason = "Errores de validación: " + String.join("; ", errors);
        } else if (!baseline.isEligible()) {
            // Guarda de elegibilidad (v1.1): si el baseline rechaza el caso
            // por precondiciones, el LLM NO debe aplicar transformaciones.
            if (delta != 0) {
                verdict = LlmVerdict.INCORRECT;
                reason = "Caso inelegible según baseline transformado por el LLM"
                        + " (delta=" + delta + ")";
            } else {
                verdict = LlmVerdict.SUCCESS;
                reason = "Correctamente no transformado (caso inelegible)";
            }
        } else if (delta < 0) {
            if (matchesBaseline) {
                verdict = LlmVerdict.SUCCESS;
                reason = "Refactorización correcta, coincide con baseline";
            } else if (delta < baseline.getDelta()) {
                verdict = LlmVerdict.SUCCESS;
                reason = "Reducción mayor que baseline (delta=" + delta
                        + " vs baseline=" + baseline.getDelta() + ")";
            } else {
                verdict = LlmVerdict.PARTIAL;
                reason = "Reducción menor que baseline (delta=" + delta
                        + " vs baseline=" + baseline.getDelta() + ")";
            }
        } else if (delta == 0 && baseline.getDelta() < 0) {
            verdict = LlmVerdict.INCORRECT;
            reason = "No redujo CC cuando el baseline sí lo hace";
        } else if (delta == 0 && baseline.getDelta() == 0) {
            verdict = LlmVerdict.SUCCESS;
            reason = "Correctamente no transformado (sin oportunidades)";
        } else {
            verdict = LlmVerdict.INCORRECT;
            reason = "CC aumentó o no mejoró (delta=" + delta + ")";
        }

        return new LlmEvaluationResult(
                caseId, model, promptVersion, attemptNumber,
                rawOutput, extracted, verdict, reason,
                true, ccBefore, ccAfter, delta,
                baseline.getDelta(), matchesBaseline,
                errors, null);
    }

    /**
     * Extrae el código Java de un bloque ```java ... ``` en la respuesta del LLM.
     *
     * @param rawOutput salida completa del LLM
     * @return código extraído, o null si no se encontró bloque
     */
    public String extractCode(String rawOutput) {
        if (rawOutput == null) return null;
        Matcher matcher = CODE_BLOCK_PATTERN.matcher(rawOutput);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    /**
     * Determina si la respuesta del LLM es un rechazo explícito.
     *
     * @param rawOutput salida completa del LLM
     * @return true si contiene el marcador de rechazo
     */
    public boolean isRefusal(String rawOutput) {
        return rawOutput != null && rawOutput.contains(REFUSAL_MARKER);
    }

    private LlmEvaluationResult buildRefusalResult(String caseId, String model,
                                                    String promptVersion,
                                                    int attemptNumber,
                                                    String rawOutput,
                                                    int ccBefore,
                                                    ExperimentResult baseline) {
        boolean correctRefusal = !baseline.isEligible();
        LlmVerdict verdict = correctRefusal
                ? LlmVerdict.SUCCESS
                : LlmVerdict.REFUSED;
        String reason = correctRefusal
                ? "Rechazo correcto: caso inelegible según baseline"
                : "Rechazo incorrecto: caso elegible según baseline";

        return new LlmEvaluationResult(
                caseId, model, promptVersion, attemptNumber,
                rawOutput, null, verdict, reason,
                false, ccBefore, ccBefore, 0,
                baseline.getDelta(),
                baseline.getDelta() == 0,
                correctRefusal ? List.of() : List.of(reason),
                null);
    }

    private LlmEvaluationResult buildErrorResult(String caseId, String model,
                                                  String promptVersion,
                                                  int attemptNumber,
                                                  String rawOutput,
                                                  String extractedCode,
                                                  LlmVerdict verdict,
                                                  String reason,
                                                  int ccBefore,
                                                  ExperimentResult baseline,
                                                  List<String> errors) {
        return new LlmEvaluationResult(
                caseId, model, promptVersion, attemptNumber,
                rawOutput, extractedCode, verdict, reason,
                false, ccBefore, -1, 0,
                baseline.getDelta(), false,
                errors, null);
    }

    private Optional<MethodDeclaration> parseOriginalMethod(String sourceCode) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(sourceCode);
            return cu.findFirst(MethodDeclaration.class);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
