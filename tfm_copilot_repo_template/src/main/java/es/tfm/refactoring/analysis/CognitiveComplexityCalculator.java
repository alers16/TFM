package es.tfm.refactoring.analysis;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.LabeledStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

/**
 * Estimación provisional de la complejidad cognitiva de un método Java,
 * basada en el modelo publicado por SonarSource.
 * <p>
 * <strong>AVISO: esta implementación es una aproximación parcial para uso interno
 * del prototipo del TFM.</strong> No reemplaza la medición oficial de SonarQube/SonarLint.
 * Los resultados finales del experimento deberán validarse contra la herramienta oficial.
 * <p>
 * Reglas implementadas (basadas en el modelo SonarSource):
 * <ul>
 *   <li>Incremento estructural (+1): if, else if, else, for, for-each, while,
 *       do-while, switch, catch, break/continue con etiqueta.</li>
 *   <li>Incremento de anidamiento (+nivel): if, for, for-each, while, do-while,
 *       switch, catch. No aplica a else-if ni else.</li>
 *   <li>Operadores lógicos: +1 por cada secuencia de operadores del mismo tipo
 *       ({@code &&} o {@code ||}) en condiciones de control de flujo.</li>
 * </ul>
 * <p>
 * Limitaciones conocidas (no cubierto en esta versión):
 * <ul>
 *   <li>Operador ternario ({@code ?:}) en expresiones.</li>
 *   <li>Operadores lógicos fuera de condiciones de control de flujo.</li>
 *   <li>Detección de recursión.</li>
 *   <li>Lambdas como incremento de anidamiento.</li>
 *   <li>Clases anónimas o internas como incremento de anidamiento.</li>
 * </ul>
 * <p>
 * Referencia del modelo: G. Ann Campbell, "Cognitive Complexity — A new way of measuring
 * understandability", SonarSource. [PENDIENTE DE CITA EXACTA — versión del documento]
 */
public class CognitiveComplexityCalculator {

    /**
     * Calcula la complejidad cognitiva estimada de un método.
     *
     * @param method el método a analizar
     * @return valor estimado de complejidad cognitiva (≥ 0), o 0 si no tiene cuerpo
     */
    public int calculate(MethodDeclaration method) {
        if (method.getBody().isEmpty()) {
            return 0;
        }
        return processStatements(method.getBody().get().getStatements(), 0);
    }

    /**
     * Calcula la complejidad cognitiva estimada del primer método encontrado
     * en el código fuente proporcionado.
     *
     * @param sourceCode código fuente Java (clase completa con al menos un método)
     * @return valor estimado de complejidad cognitiva
     */
    public int calculate(String sourceCode) {
        CompilationUnit cu = StaticJavaParser.parse(sourceCode);
        return cu.findFirst(MethodDeclaration.class)
                .map(this::calculate)
                .orElse(0);
    }

    // =========================================================================
    // Procesamiento de sentencias
    // =========================================================================

    private int processStatements(NodeList<Statement> statements, int nestingLevel) {
        int complexity = 0;
        for (Statement stmt : statements) {
            complexity += processStatement(stmt, nestingLevel);
        }
        return complexity;
    }

    private int processStatement(Statement stmt, int nestingLevel) {
        if (stmt.isBlockStmt()) {
            return processStatements(stmt.asBlockStmt().getStatements(), nestingLevel);
        }
        if (stmt.isIfStmt()) {
            return processIf(stmt.asIfStmt(), nestingLevel, false);
        }
        if (stmt.isForStmt()) {
            return processFor(stmt.asForStmt(), nestingLevel);
        }
        if (stmt.isForEachStmt()) {
            return processForEach(stmt.asForEachStmt(), nestingLevel);
        }
        if (stmt.isWhileStmt()) {
            return processWhile(stmt.asWhileStmt(), nestingLevel);
        }
        if (stmt.isDoStmt()) {
            return processDo(stmt.asDoStmt(), nestingLevel);
        }
        if (stmt.isSwitchStmt()) {
            return processSwitch(stmt.asSwitchStmt(), nestingLevel);
        }
        if (stmt.isTryStmt()) {
            return processTry(stmt.asTryStmt(), nestingLevel);
        }
        if (stmt.isLabeledStmt()) {
            return processStatement(stmt.asLabeledStmt().getStatement(), nestingLevel);
        }
        if (stmt.isBreakStmt() && stmt.asBreakStmt().getLabel().isPresent()) {
            return 1;
        }
        if (stmt.isContinueStmt() && stmt.asContinueStmt().getLabel().isPresent()) {
            return 1;
        }
        return 0;
    }

    // =========================================================================
    // Estructuras de control
    // =========================================================================

    /**
     * Procesa un if/else if/else.
     * <ul>
     *   <li>if: +1 estructural + nivel de anidamiento.</li>
     *   <li>else if: +1 estructural (sin incremento de anidamiento).</li>
     *   <li>else: +1 estructural.</li>
     *   <li>El cuerpo de cada rama se procesa a nivel de anidamiento + 1.</li>
     * </ul>
     */
    private int processIf(IfStmt ifStmt, int nestingLevel, boolean isElseIf) {
        int complexity = 1; // structural increment
        if (!isElseIf) {
            complexity += nestingLevel; // nesting increment
        }

        // Logical operators in condition
        complexity += countLogicalOperatorSequences(ifStmt.getCondition());

        // Then body at nesting + 1
        complexity += processStatement(ifStmt.getThenStmt(), nestingLevel + 1);

        // Else / else-if
        if (ifStmt.hasElseBranch()) {
            Statement elseBranch = ifStmt.getElseStmt().get();
            if (elseBranch.isIfStmt()) {
                complexity += processIf(elseBranch.asIfStmt(), nestingLevel, true);
            } else {
                complexity += 1; // else: structural only
                complexity += processStatement(elseBranch, nestingLevel + 1);
            }
        }

        return complexity;
    }

    private int processFor(ForStmt stmt, int nestingLevel) {
        int complexity = 1 + nestingLevel;
        if (stmt.getCompare().isPresent()) {
            complexity += countLogicalOperatorSequences(stmt.getCompare().get());
        }
        complexity += processStatement(stmt.getBody(), nestingLevel + 1);
        return complexity;
    }

    private int processForEach(ForEachStmt stmt, int nestingLevel) {
        int complexity = 1 + nestingLevel;
        complexity += processStatement(stmt.getBody(), nestingLevel + 1);
        return complexity;
    }

    private int processWhile(WhileStmt stmt, int nestingLevel) {
        int complexity = 1 + nestingLevel;
        complexity += countLogicalOperatorSequences(stmt.getCondition());
        complexity += processStatement(stmt.getBody(), nestingLevel + 1);
        return complexity;
    }

    private int processDo(DoStmt stmt, int nestingLevel) {
        int complexity = 1 + nestingLevel;
        complexity += countLogicalOperatorSequences(stmt.getCondition());
        complexity += processStatement(stmt.getBody(), nestingLevel + 1);
        return complexity;
    }

    private int processSwitch(SwitchStmt stmt, int nestingLevel) {
        int complexity = 1 + nestingLevel;
        for (SwitchEntry entry : stmt.getEntries()) {
            complexity += processStatements(entry.getStatements(), nestingLevel + 1);
        }
        return complexity;
    }

    private int processTry(TryStmt stmt, int nestingLevel) {
        int complexity = 0;
        // try: no increment, process contents at same nesting
        complexity += processStatements(stmt.getTryBlock().getStatements(), nestingLevel);
        // Each catch: +1 structural + nesting
        for (CatchClause catchClause : stmt.getCatchClauses()) {
            complexity += 1 + nestingLevel;
            complexity += processStatements(
                    catchClause.getBody().getStatements(), nestingLevel + 1);
        }
        // finally: no increment, process at same nesting
        if (stmt.getFinallyBlock().isPresent()) {
            complexity += processStatements(
                    stmt.getFinallyBlock().get().getStatements(), nestingLevel);
        }
        return complexity;
    }

    // =========================================================================
    // Operadores lógicos
    // =========================================================================

    /**
     * Cuenta las secuencias de operadores lógicos ({@code &&}, {@code ||})
     * en una expresión.
     * <p>
     * Cada grupo de operadores consecutivos del mismo tipo cuenta +1.
     * Un cambio de operador inicia un nuevo grupo.
     * <ul>
     *   <li>{@code a && b} → +1</li>
     *   <li>{@code a && b && c} → +1</li>
     *   <li>{@code a && b || c} → +2</li>
     *   <li>{@code a || b && c || d} → +3</li>
     * </ul>
     *
     * @param expr la expresión a analizar
     * @return número de secuencias de operadores lógicos
     */
    int countLogicalOperatorSequences(Expression expr) {
        int count = 0;
        for (BinaryExpr binExpr : expr.findAll(BinaryExpr.class)) {
            BinaryExpr.Operator op = binExpr.getOperator();
            if (op != BinaryExpr.Operator.AND && op != BinaryExpr.Operator.OR) {
                continue;
            }
            // A new sequence starts when the parent is NOT the same logical operator
            boolean parentIsSameLogicalOp = binExpr.getParentNode()
                    .filter(parent -> parent instanceof BinaryExpr)
                    .map(parent -> ((BinaryExpr) parent).getOperator())
                    .filter(parentOp -> parentOp == op)
                    .isPresent();
            if (!parentIsSameLogicalOp) {
                count++;
            }
        }
        return count;
    }
}
