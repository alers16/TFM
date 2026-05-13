package es.tfm.refactoring.detection;

import com.github.javaparser.ast.stmt.IfStmt;

/**
 * Representa una oportunidad de refactorización detectada.
 * <p>
 * Contiene la referencia al if externo y al if interno que pueden combinarse,
 * junto con metadatos para la trazabilidad (línea, condiciones, etc.).
 */
public class RefactoringOpportunity {

    private final IfStmt outerIf;
    private final IfStmt innerIf;
    private final int startLine;
    private final String outerCondition;
    private final String innerCondition;

    public RefactoringOpportunity(IfStmt outerIf, IfStmt innerIf) {
        this.outerIf = outerIf;
        this.innerIf = innerIf;
        this.startLine = outerIf.getBegin().map(pos -> pos.line).orElse(-1);
        this.outerCondition = outerIf.getCondition().toString();
        this.innerCondition = innerIf.getCondition().toString();
    }

    public IfStmt getOuterIf() {
        return outerIf;
    }

    public IfStmt getInnerIf() {
        return innerIf;
    }

    public int getStartLine() {
        return startLine;
    }

    public String getOuterCondition() {
        return outerCondition;
    }

    public String getInnerCondition() {
        return innerCondition;
    }

    @Override
    public String toString() {
        return "RefactoringOpportunity[line=" + startLine
                + ", outer=(" + outerCondition + ")"
                + ", inner=(" + innerCondition + ")]";
    }
}
