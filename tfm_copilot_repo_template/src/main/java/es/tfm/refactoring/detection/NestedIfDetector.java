package es.tfm.refactoring.detection;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Detecta oportunidades de combinación de sentencias condicionales anidadas.
 * <p>
 * Patrón buscado (caso básico MVP):
 * <pre>
 * if (A) {
 *     if (B) {
 *         S
 *     }
 * }
 * </pre>
 * <p>
 * Precondiciones de aplicabilidad (todas deben cumplirse):
 * <ol>
 *   <li>El if externo no tiene rama else ni else-if.</li>
 *   <li>El bloque then del if externo contiene exactamente una sentencia.</li>
 *   <li>Esa sentencia única es otro if.</li>
 *   <li>El if interno no tiene rama else ni else-if.</li>
 *   <li>Ninguna de las dos condiciones contiene side effects detectables:
 *       llamadas a métodos, asignaciones, operadores de incremento/decremento,
 *       expresiones lambda ni instanciaciones con new.</li>
 * </ol>
 * <p>
 * Casos excluidos explícitamente en esta versión MVP:
 * <ul>
 *   <li>if con else o else-if (externo o interno).</li>
 *   <li>Bloque externo con más de una sentencia.</li>
 *   <li>Condiciones con llamadas a métodos (posible side effect).</li>
 *   <li>Condiciones con asignaciones.</li>
 *   <li>Condiciones con operadores ++ o --.</li>
 *   <li>Condiciones con lambdas o instanciaciones de objetos.</li>
 * </ul>
 */
public class NestedIfDetector {

    private final DetectionMode mode;

    /**
     * Constructor por defecto: modo {@link DetectionMode#STRICT} (P1–P5 completas).
     */
    public NestedIfDetector() {
        this(DetectionMode.STRICT);
    }

    /**
     * Constructor con modo explícito.
     *
     * @param mode {@link DetectionMode#STRICT} para P1–P5 originales;
     *             {@link DetectionMode#RELAXED} para P1–P4 + P5' con allowlist.
     */
    public NestedIfDetector(DetectionMode mode) {
        this.mode = mode;
    }

    /**
     * Devuelve el modo de detección activo.
     */
    public DetectionMode getMode() {
        return mode;
    }

    /**
     * Analiza un método y devuelve las oportunidades de refactorización encontradas.
     * <p>
     * Recorre recursivamente todos los {@link IfStmt} del método. Para cada uno,
     * verifica si cumple las precondiciones del patrón MVP.
     *
     * @param method el método a analizar
     * @return lista de oportunidades detectadas (vacía si no hay ninguna)
     */
    public List<RefactoringOpportunity> detect(MethodDeclaration method) {
        List<RefactoringOpportunity> opportunities = new ArrayList<>();

        if (method.getBody().isEmpty()) {
            return opportunities;
        }

        method.getBody().get().accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(IfStmt outerIf, Void arg) {
                evaluateCandidate(outerIf, opportunities);
                // Continuar visitando nodos hijos para detectar patrones a mayor profundidad
                super.visit(outerIf, arg);
            }
        }, null);

        return opportunities;
    }

    /**
     * Analiza un método y devuelve resultados detallados de detección, incluyendo
     * motivos de descarte específicos para cada candidato rechazado.
     * <p>
     * A diferencia de {@link #detect(MethodDeclaration)}, este método reporta
     * también los candidatos que no cumplen las precondiciones, permitiendo
     * trazabilidad experimental completa.
     *
     * @param method el método a analizar
     * @return lista de resultados de detección (aceptados y rechazados)
     */
    public List<DetectionResult> detectWithReasons(MethodDeclaration method) {
        List<DetectionResult> results = new ArrayList<>();

        if (method.getBody().isEmpty()) {
            return results;
        }

        method.getBody().get().accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(IfStmt outerIf, Void arg) {
                results.add(evaluateCandidateWithReasons(outerIf));
                super.visit(outerIf, arg);
            }
        }, null);

        return results;
    }

    /**
     * Evalúa si un {@link IfStmt} cumple todas las precondiciones del patrón MVP.
     */
    private void evaluateCandidate(IfStmt outerIf, List<RefactoringOpportunity> opportunities) {
        // P1: el if externo no tiene else
        if (outerIf.hasElseBranch()) {
            return;
        }

        // P2-P3: extraer el if interno como única sentencia del bloque then
        IfStmt innerIf = extractSingleInnerIf(outerIf.getThenStmt());
        if (innerIf == null) {
            return;
        }

        // P4: el if interno no tiene else (en RELAXED: else vacío se acepta — P4')
        if (innerIf.hasElseBranch()) {
            if (mode == DetectionMode.STRICT || !isEmptyElse(innerIf)) {
                return;
            }
        }

        // P5: ninguna condición contiene side effects detectables
        // En RELAXED: patrón null-guard exime las llamadas del if interno (P5''')
        Optional<String> nullGuardedVar = extractNullCheckedName(outerIf.getCondition());
        if (containsSideEffects(outerIf.getCondition(), Optional.empty())) {
            return;
        }
        if (containsSideEffects(innerIf.getCondition(), nullGuardedVar)) {
            return;
        }

        opportunities.add(new RefactoringOpportunity(outerIf, innerIf));
    }

    /**
     * Extrae el if interno si el bloque then contiene exactamente una sentencia
     * y esa sentencia es un {@link IfStmt}.
     *
     * @param thenStmt la rama then del if externo
     * @return el if interno, o null si no cumple el patrón
     */
    private IfStmt extractSingleInnerIf(Statement thenStmt) {
        // Caso 1: bloque con llaves { ... }
        if (thenStmt.isBlockStmt()) {
            BlockStmt block = thenStmt.asBlockStmt();
            if (block.getStatements().size() != 1) {
                return null;
            }
            Statement single = block.getStatements().get(0);
            return single.isIfStmt() ? single.asIfStmt() : null;
        }

        // Caso 2: sentencia directa sin llaves (e.g., if (A) if (B) S;)
        if (thenStmt.isIfStmt()) {
            return thenStmt.asIfStmt();
        }

        return null;
    }

    /**
     * Comprueba si una expresión contiene indicios de side effects.
     * <p>
     * Criterio conservador: cualquier presencia de los siguientes nodos
     * en el árbol de la expresión se considera potencialmente inseguro.
     * <ul>
     *   <li>{@link MethodCallExpr} — llamadas a métodos</li>
     *   <li>{@link AssignExpr} — asignaciones</li>
     *   <li>{@link UnaryExpr} con ++/-- — incrementos y decrementos</li>
     *   <li>{@link LambdaExpr} — expresiones lambda</li>
     *   <li>{@link ObjectCreationExpr} — instanciaciones con new</li>
     * </ul>
     *
     * @param condition la expresión a analizar
     * @return true si se detectan indicios de side effects
     */
    private boolean containsSideEffects(Expression condition) {
        return containsSideEffects(condition, Optional.empty());
    }

    /**
     * Variante con patrón null-guard: si {@code nullGuardedVar} está presente,
     * las llamadas a métodos cuyo receptor sea esa variable se aceptan en modo
     * RELAXED incluso si el nombre no figura en el allowlist (P5''').
     */
    private boolean containsSideEffects(Expression condition, Optional<String> nullGuardedVar) {
        List<MethodCallExpr> calls = condition.findAll(MethodCallExpr.class);
        if (!calls.isEmpty()) {
            if (mode == DetectionMode.STRICT) {
                return true;
            }
            // RELAXED: acepta si el método está en el allowlist O si el receptor
            // es la variable null-guardada (P5''')
            boolean allAccepted = calls.stream().allMatch(call -> {
                if (MethodCallAllowlist.isPresumablyPure(call.getNameAsString())) return true;
                return nullGuardedVar.isPresent() && call.getScope()
                        .map(s -> s.isNameExpr()
                                && s.asNameExpr().getNameAsString().equals(nullGuardedVar.get()))
                        .orElse(false);
            });
            if (!allAccepted) {
                return true;
            }
        }
        if (!condition.findAll(AssignExpr.class).isEmpty()) {
            return true;
        }
        for (UnaryExpr unary : condition.findAll(UnaryExpr.class)) {
            UnaryExpr.Operator op = unary.getOperator();
            if (op == UnaryExpr.Operator.POSTFIX_INCREMENT
                    || op == UnaryExpr.Operator.POSTFIX_DECREMENT
                    || op == UnaryExpr.Operator.PREFIX_INCREMENT
                    || op == UnaryExpr.Operator.PREFIX_DECREMENT) {
                return true;
            }
        }
        if (!condition.findAll(LambdaExpr.class).isEmpty()) {
            return true;
        }
        if (!condition.findAll(ObjectCreationExpr.class).isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Evalúa un candidato y devuelve un resultado detallado con motivos de descarte.
     */
    private DetectionResult evaluateCandidateWithReasons(IfStmt outerIf) {
        int line = outerIf.getBegin().map(pos -> pos.line).orElse(-1);

        // P1: el if externo no tiene else
        if (outerIf.hasElseBranch()) {
            return DetectionResult.rejected(
                    Collections.singletonList(DiscardReason.OUTER_HAS_ELSE), line);
        }

        // P2-P3: extraer el if interno como única sentencia del bloque then
        Statement thenStmt = outerIf.getThenStmt();
        IfStmt innerIf = extractSingleInnerIf(thenStmt);

        if (innerIf == null) {
            // Distinguish: multiple statements vs single non-if statement
            DiscardReason reason;
            if (thenStmt.isBlockStmt()
                    && thenStmt.asBlockStmt().getStatements().size() > 1) {
                reason = DiscardReason.OUTER_BLOCK_MULTIPLE_STATEMENTS;
            } else if (thenStmt.isBlockStmt()
                    && thenStmt.asBlockStmt().getStatements().size() == 1
                    && !thenStmt.asBlockStmt().getStatements().get(0).isIfStmt()) {
                reason = DiscardReason.SINGLE_STATEMENT_NOT_IF;
            } else if (!thenStmt.isBlockStmt() && !thenStmt.isIfStmt()) {
                reason = DiscardReason.SINGLE_STATEMENT_NOT_IF;
            } else {
                reason = DiscardReason.NO_NESTED_IF_PATTERN;
            }
            return DetectionResult.rejected(
                    Collections.singletonList(reason), line);
        }

        // P4: el if interno no tiene else (en RELAXED: else vacío se acepta — P4')
        if (innerIf.hasElseBranch()) {
            if (mode == DetectionMode.STRICT || !isEmptyElse(innerIf)) {
                return DetectionResult.rejected(
                        Collections.singletonList(DiscardReason.INNER_HAS_ELSE), line);
            }
        }

        // P5: side effects en condiciones (recopilar todos los motivos)
        // En RELAXED: patrón null-guard exime las llamadas del if interno (P5''')
        Optional<String> nullGuardedVar = extractNullCheckedName(outerIf.getCondition());
        List<DiscardReason> sideEffectReasons = new ArrayList<>();
        collectSideEffectReasons(outerIf.getCondition(), sideEffectReasons, Optional.empty());
        collectSideEffectReasons(innerIf.getCondition(), sideEffectReasons, nullGuardedVar);

        if (!sideEffectReasons.isEmpty()) {
            return DetectionResult.rejected(sideEffectReasons, line);
        }

        // Todas las precondiciones se cumplen
        RefactoringOpportunity opp = new RefactoringOpportunity(outerIf, innerIf);
        return DetectionResult.accepted(opp);
    }

    /**
     * Recopila motivos de descarte específicos por tipo de side effect
     * encontrado en una expresión.
     */
    private void collectSideEffectReasons(Expression condition,
                                          List<DiscardReason> reasons) {
        collectSideEffectReasons(condition, reasons, Optional.empty());
    }

    private void collectSideEffectReasons(Expression condition,
                                          List<DiscardReason> reasons,
                                          Optional<String> nullGuardedVar) {
        List<MethodCallExpr> calls = condition.findAll(MethodCallExpr.class);
        if (!calls.isEmpty()) {
            if (mode == DetectionMode.STRICT) {
                reasons.add(DiscardReason.METHOD_CALL_IN_CONDITION);
            } else {
                // RELAXED: motivo solo si alguna llamada no está en el allowlist
                // ni está protegida por null-guard (P5''')
                boolean anyUnaccepted = calls.stream().anyMatch(call -> {
                    if (MethodCallAllowlist.isPresumablyPure(call.getNameAsString())) return false;
                    return !(nullGuardedVar.isPresent() && call.getScope()
                            .map(s -> s.isNameExpr()
                                    && s.asNameExpr().getNameAsString().equals(nullGuardedVar.get()))
                            .orElse(false));
                });
                if (anyUnaccepted) {
                    reasons.add(DiscardReason.METHOD_CALL_IN_CONDITION);
                }
            }
        }
        if (!condition.findAll(AssignExpr.class).isEmpty()) {
            reasons.add(DiscardReason.ASSIGNMENT_IN_CONDITION);
        }
        for (UnaryExpr unary : condition.findAll(UnaryExpr.class)) {
            UnaryExpr.Operator op = unary.getOperator();
            if (op == UnaryExpr.Operator.POSTFIX_INCREMENT
                    || op == UnaryExpr.Operator.POSTFIX_DECREMENT
                    || op == UnaryExpr.Operator.PREFIX_INCREMENT
                    || op == UnaryExpr.Operator.PREFIX_DECREMENT) {
                reasons.add(DiscardReason.INCREMENT_OR_DECREMENT_IN_CONDITION);
                break; // one reason per category is enough
            }
        }
        if (!condition.findAll(LambdaExpr.class).isEmpty()) {
            reasons.add(DiscardReason.LAMBDA_IN_CONDITION);
        }
        if (!condition.findAll(ObjectCreationExpr.class).isEmpty()) {
            reasons.add(DiscardReason.OBJECT_CREATION_IN_CONDITION);
        }
    }

    /**
     * Comprueba si la rama else del if proporcionado es un bloque vacío ({@code else {}}).
     * Se usa en modo RELAXED para implementar P4' (else vacío aceptado).
     */
    private boolean isEmptyElse(IfStmt ifStmt) {
        return ifStmt.getElseStmt()
                .map(e -> e.isBlockStmt() && e.asBlockStmt().isEmpty())
                .orElse(false);
    }

    /**
     * Si la condición es de la forma {@code x != null} o {@code null != x},
     * devuelve el nombre de la variable null-comprobada.
     * Se usa en modo RELAXED para implementar P5''' (patrón null-guard).
     *
     * @param condition expresión a analizar
     * @return el nombre de la variable, o {@code Optional.empty()} si no aplica
     */
    private Optional<String> extractNullCheckedName(Expression condition) {
        if (!condition.isBinaryExpr()) return Optional.empty();
        BinaryExpr bin = condition.asBinaryExpr();
        if (bin.getOperator() != BinaryExpr.Operator.NOT_EQUALS) return Optional.empty();
        if (bin.getRight().isNullLiteralExpr() && bin.getLeft().isNameExpr()) {
            return Optional.of(bin.getLeft().asNameExpr().getNameAsString());
        }
        if (bin.getLeft().isNullLiteralExpr() && bin.getRight().isNameExpr()) {
            return Optional.of(bin.getRight().asNameExpr().getNameAsString());
        }
        return Optional.empty();
    }
}
