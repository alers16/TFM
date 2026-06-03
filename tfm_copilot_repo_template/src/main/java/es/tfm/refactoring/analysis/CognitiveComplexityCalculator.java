package es.tfm.refactoring.analysis;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import java.util.ArrayList;
import java.util.List;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.LabeledStmt;
import com.github.javaparser.ast.stmt.LocalClassDeclarationStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
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
 *       do-while, switch, catch, break/continue con etiqueta, operador ternario.</li>
 *   <li>Incremento de anidamiento (+nivel): if, operador ternario, for,
 *       for-each, while, do-while, switch, catch. No aplica a else-if ni else.</li>
 *   <li>Operadores lógicos: +1 por cada secuencia de operadores del mismo tipo
 *       ({@code &&} o {@code ||}) en cualquier expresión (condiciones, return,
 *       asignaciones, argumentos de método, etc.).</li>
 *   <li>Lambda: el cuerpo se procesa a nestingLevel+1.</li>
 *   <li>Clase anónima ({@code new Foo() {...}}): los métodos del cuerpo se procesan
 *       a nestingLevel+1.</li>
 *   <li>Clase local ({@code class Foo {...}} dentro de un método): los métodos del
 *       cuerpo se procesan a nestingLevel+1.</li>
 * </ul>
 * <p>
 * Limitaciones conocidas (no cubierto en esta versión):
 * <ul>
 *   <li>Detección de recursión.</li>
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
        if (stmt.isReturnStmt()) {
            ReturnStmt ret = stmt.asReturnStmt();
            return ret.getExpression().map(e -> processExpression(e, nestingLevel)).orElse(0);
        }
        if (stmt.isExpressionStmt()) {
            return processExpression(stmt.asExpressionStmt().getExpression(), nestingLevel);
        }
        if (stmt.isThrowStmt()) {
            return processExpression(stmt.asThrowStmt().getExpression(), nestingLevel);
        }
        if (stmt.isLocalClassDeclarationStmt()) {
            int c = 0;
            for (BodyDeclaration<?> member :
                    stmt.asLocalClassDeclarationStmt().getClassDeclaration().getMembers()) {
                if (member instanceof MethodDeclaration) {
                    MethodDeclaration md = (MethodDeclaration) member;
                    if (md.getBody().isPresent()) {
                        c += processStatements(md.getBody().get().getStatements(), nestingLevel + 1);
                    }
                }
            }
            return c;
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

        // Operadores lógicos, ternario y lambdas en la condición
        complexity += processExpression(ifStmt.getCondition(), nestingLevel);

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
            complexity += processExpression(stmt.getCompare().get(), nestingLevel);
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
        complexity += processExpression(stmt.getCondition(), nestingLevel);
        complexity += processStatement(stmt.getBody(), nestingLevel + 1);
        return complexity;
    }

    private int processDo(DoStmt stmt, int nestingLevel) {
        int complexity = 1 + nestingLevel;
        complexity += processExpression(stmt.getCondition(), nestingLevel);
        complexity += processStatement(stmt.getBody(), nestingLevel + 1);
        return complexity;
    }

    private int processSwitch(SwitchStmt stmt, int nestingLevel) {
        int complexity = 1 + nestingLevel;
        complexity += processExpression(stmt.getSelector(), nestingLevel);
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
            // A new sequence starts when the parent is NOT the same logical operator.
            // EnclosedExpr (paréntesis explícitos) se ignoran al subir por el árbol,
            // siguiendo la especificación SonarSource: «parentheses completely ignored».
            boolean parentIsSameLogicalOp = effectiveParentOp(binExpr) == op;
            if (!parentIsSameLogicalOp) {
                count++;
            }
        }
        return count;
    }

    /**
     * Devuelve el operador del ancestro BinaryExpr efectivo de {@code binExpr},
     * ignorando nodos EnclosedExpr intermedios (paréntesis explícitos).
     * Devuelve {@code null} si no hay ancestro BinaryExpr.
     */
    private BinaryExpr.Operator effectiveParentOp(BinaryExpr binExpr) {
        Node parent = binExpr.getParentNode().orElse(null);
        while (parent instanceof EnclosedExpr) {
            parent = parent.getParentNode().orElse(null);
        }
        if (parent instanceof BinaryExpr) {
            return ((BinaryExpr) parent).getOperator();
        }
        return null;
    }

    /**
     * Devuelve {@code true} si {@code bin} es la raíz de un grupo de operadores
     * lógicos, es decir, su ancestro BinaryExpr efectivo no es AND ni OR.
     */
    private boolean isLogicalRoot(BinaryExpr bin) {
        BinaryExpr.Operator parentOp = effectiveParentOp(bin);
        return parentOp != BinaryExpr.Operator.AND && parentOp != BinaryExpr.Operator.OR;
    }

    /**
     * Recopila, en orden de aparición en el código fuente (recorrido en inorden),
     * todos los operadores lógicos (&&/||) que forman parte del mismo grupo plano.
     * <p>
     * Reglas de recorrido:
     * <ul>
     *   <li>{@link BinaryExpr}: se visita hijo izquierdo, luego el operador (si es
     *       lógico), luego hijo derecho.</li>
     *   <li>{@link EnclosedExpr}: transparente (los paréntesis son completamente
     *       ignorados, igual que SonarSource).</li>
     *   <li>Cualquier otro nodo: se detiene (scope boundary). Los operadores lógicos
     *       dentro de lambdas, ternarios, clases anónimas, argumentos de métodos, etc.
     *       serán contados por la llamada recursiva de {@link #processExpression}.</li>
     * </ul>
     */
    private void collectFlatLogicalOps(Node node, List<BinaryExpr.Operator> ops) {
        if (node instanceof BinaryExpr) {
            BinaryExpr bin = (BinaryExpr) node;
            collectFlatLogicalOps(bin.getLeft(), ops);
            BinaryExpr.Operator op = bin.getOperator();
            if (op == BinaryExpr.Operator.AND || op == BinaryExpr.Operator.OR) {
                ops.add(op);
            }
            collectFlatLogicalOps(bin.getRight(), ops);
        } else if (node instanceof EnclosedExpr) {
            collectFlatLogicalOps(((EnclosedExpr) node).getInner(), ops);
        }
        // Para todo lo demás (llamadas a métodos, ternarios, lambdas, terminales):
        // se detiene. processExpression los procesará por separado.
    }

    /**
     * Dada una lista de operadores lógicos en orden plano, devuelve el número de
     * secuencias distintas (grupos de operadores consecutivos del mismo tipo).
     */
    private int countLogicalSequences(List<BinaryExpr.Operator> ops) {
        int count = 0;
        BinaryExpr.Operator prev = null;
        for (BinaryExpr.Operator op : ops) {
            if (op != prev) {
                count++;
            }
            prev = op;
        }
        return count;
    }

    /**
     * Calcula la complejidad cognitiva aportada por una expresión y todos sus
     * descendientes, respetando los límites de scope de lambdas y clases anónimas.
     *
     * <ul>
     *   <li>Operador ternario ({@code ?:}): +1 estructural y +nivel de anidamiento;
     *       las ramas {@code then}/{@code else} se procesan a {@code nestingLevel+1}.</li>
     *   <li>Operador lógico ({@code &&}/{@code ||}): +1 si inicia una nueva secuencia
     *       (el padre efectivo no es del mismo tipo).</li>
     *   <li>Lambda: el cuerpo se procesa a {@code nestingLevel+1}.</li>
     *   <li>Clase anónima: los métodos del cuerpo se procesan a {@code nestingLevel+1}.</li>
     * </ul>
     *
     * @param node         nodo raíz a procesar
     * @param nestingLevel nivel de anidamiento actual
     * @return complejidad cognitiva aportada
     */
    private int processExpression(Node node, int nestingLevel) {
        if (node instanceof ConditionalExpr) {
            ConditionalExpr ternary = (ConditionalExpr) node;
            // El operador ternario recibe, según el modelo SonarSource, un
            // incremento estructural (+1) Y un incremento de anidamiento
            // (+nivel actual); además eleva el nivel de anidamiento para sus
            // ramas (then/else). La condición se procesa al nivel actual, igual
            // que en el if (sus operadores lógicos se cuentan por secuencias).
            int c = 1 + nestingLevel;
            c += processExpression(ternary.getCondition(), nestingLevel);
            c += processExpression(ternary.getThenExpr(), nestingLevel + 1);
            c += processExpression(ternary.getElseExpr(), nestingLevel + 1);
            return c;
        }
        if (node instanceof BinaryExpr) {
            BinaryExpr binExpr = (BinaryExpr) node;
            BinaryExpr.Operator op = binExpr.getOperator();
            int c = 0;
            if (op == BinaryExpr.Operator.AND || op == BinaryExpr.Operator.OR) {
                // Si es la raíz del grupo lógico, contar secuencias con recorrido plano
                // (igual que SonarSource: paréntesis completamente ignorados).
                if (isLogicalRoot(binExpr)) {
                    List<BinaryExpr.Operator> ops = new ArrayList<>();
                    collectFlatLogicalOps(binExpr, ops);
                    c = countLogicalSequences(ops);
                }
                // Siempre recursamos para procesar ternarios/lambdas/clases que pudiera
                // haber dentro del árbol lógico.
            }
            c += processExpression(binExpr.getLeft(), nestingLevel);
            c += processExpression(binExpr.getRight(), nestingLevel);
            return c;
        }
        if (node instanceof LambdaExpr) {
            // El cuerpo de la lambda se procesa a nestingLevel+1.
            // getBody() devuelve Statement (ExpressionStmt para lambdas de una sola
            // expresión, BlockStmt para lambdas con llaves).
            return processStatement(((LambdaExpr) node).getBody(), nestingLevel + 1);
        }
        if (node instanceof ObjectCreationExpr) {
            ObjectCreationExpr oce = (ObjectCreationExpr) node;
            if (oce.getAnonymousClassBody().isPresent()) {
                // Clase anónima: los métodos del cuerpo a nestingLevel+1.
                int c = 0;
                for (BodyDeclaration<?> member : oce.getAnonymousClassBody().get()) {
                    if (member instanceof MethodDeclaration) {
                        MethodDeclaration md = (MethodDeclaration) member;
                        if (md.getBody().isPresent()) {
                            c += processStatements(
                                    md.getBody().get().getStatements(), nestingLevel + 1);
                        }
                    }
                }
                return c;
            }
            // Construcción normal: recorrer argumentos.
            int c = 0;
            for (Expression arg : oce.getArguments()) {
                c += processExpression(arg, nestingLevel);
            }
            return c;
        }
        // Caso general: recorrer todos los hijos.
        int c = 0;
        for (Node child : node.getChildNodes()) {
            c += processExpression(child, nestingLevel);
        }
        return c;
    }
}
