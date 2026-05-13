package es.tfm.refactoring.transformation;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import es.tfm.refactoring.detection.RefactoringOpportunity;

/**
 * Aplica la transformación de combinación de condicionales anidados.
 * <p>
 * Transformación objetivo:
 * <pre>
 * if (A) {             if (A &amp;&amp; B) {
 *     if (B) {    →        S
 *         S             }
 *     }
 * }
 * </pre>
 * <p>
 * Garantías de esta versión MVP:
 * <ul>
 *   <li>El orden de evaluación se preserva: A && B, nunca B && A.</li>
 *   <li>El cuerpo S se reubica tal cual bajo el nuevo if combinado.</li>
 *   <li>No se simplifican ni reordenan expresiones.</li>
 *   <li>Las condiciones originales se envuelven en paréntesis si son expresiones
 *       binarias con operadores de menor precedencia que &&, para evitar cambios
 *       de semántica por precedencia.</li>
 *   <li>El resultado es sintácticamente válido en el AST de JavaParser.</li>
 * </ul>
 * <p>
 * Lo que NO garantiza esta versión MVP:
 * <ul>
 *   <li>Equivalencia semántica completa (no se analiza flujo de datos ni alias).</li>
 *   <li>Compilabilidad del resultado en contextos complejos fuera del AST.</li>
 *   <li>Corrección si la oportunidad no fue validada por el detector.</li>
 * </ul>
 */
public class NestedIfTransformer {

    /**
     * Aplica la refactorización sobre una oportunidad detectada.
     * <p>
     * Modifica el AST in-place: reemplaza el nodo del if externo por un nuevo if
     * con la condición combinada y el cuerpo del if interno.
     *
     * @param opportunity la oportunidad de refactorización (ya validada por el detector)
     * @return true si la transformación se aplicó con éxito, false si se abortó
     */
    public boolean apply(RefactoringOpportunity opportunity) {
        IfStmt outerIf = opportunity.getOuterIf();
        IfStmt innerIf = opportunity.getInnerIf();

        // Verificación defensiva: no transformar si alguno de los dos tiene else
        if (outerIf.hasElseBranch() || innerIf.hasElseBranch()) {
            return false;
        }

        // 1. Construir la condición combinada: A && B
        Expression outerCondition = outerIf.getCondition().clone();
        Expression innerCondition = innerIf.getCondition().clone();

        // Envolver en paréntesis si la condición es un OR binario (menor precedencia que &&)
        // para preservar la semántica original.
        Expression left = parenthesizeIfNeeded(outerCondition);
        Expression right = parenthesizeIfNeeded(innerCondition);

        BinaryExpr combinedCondition = new BinaryExpr(left, right, BinaryExpr.Operator.AND);

        // 2. Extraer el cuerpo S del if interno
        Statement innerBody = innerIf.getThenStmt().clone();

        // Normalizar a bloque con llaves si no lo es
        if (!innerBody.isBlockStmt()) {
            BlockStmt block = new BlockStmt();
            block.addStatement(innerBody);
            innerBody = block;
        }

        // 3. Reemplazar el if externo in-place
        outerIf.setCondition(combinedCondition);
        outerIf.setThenStmt(innerBody);
        // Asegurar que no queda rama else residual (defensivo)
        outerIf.removeElseStmt();

        return true;
    }

    /**
     * Envuelve una expresión en paréntesis si es un operador binario OR,
     * para evitar cambios de precedencia al combinarla con &&.
     * <p>
     * Ejemplo: {@code (a > 0 || c)} como parte de {@code (a > 0 || c) && B}
     * necesita paréntesis; sin ellos, {@code a > 0 || c && B} cambia la semántica.
     *
     * @param expr la expresión a evaluar
     * @return la expresión original o envuelta en paréntesis
     */
    private Expression parenthesizeIfNeeded(Expression expr) {
        if (expr.isBinaryExpr()) {
            BinaryExpr.Operator op = expr.asBinaryExpr().getOperator();
            if (op == BinaryExpr.Operator.OR) {
                return new EnclosedExpr(expr);
            }
        }
        return expr;
    }
}
