package es.tfm.refactoring.detection;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.IfStmt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de DetectionResult — wrapper de aceptación/rechazo con motivos.
 */
@DisplayName("DetectionResult — Resultado de análisis de candidato")
class DetectionResultTest {

    /** Parsea un patrón canónico y devuelve una oportunidad válida. */
    private RefactoringOpportunity makeOpportunity() {
        CompilationUnit cu = StaticJavaParser.parse(
                "class X { void f(int a, int b) { if (a > 0) { if (b > 0) { } } } }");
        List<IfStmt> ifs = cu.findAll(IfStmt.class);
        return new RefactoringOpportunity(ifs.get(0), ifs.get(1));
    }

    @Test
    @DisplayName("accepted() produce resultado aceptado con oportunidad")
    void acceptedResult() {
        RefactoringOpportunity opp = makeOpportunity();
        DetectionResult result = DetectionResult.accepted(opp);

        assertTrue(result.isAccepted());
        assertSame(opp, result.getOpportunity());
        assertTrue(result.getDiscardReasons().isEmpty());
        assertTrue(result.getCandidateLine() > 0);
    }

    @Test
    @DisplayName("rejected() produce resultado rechazado con motivos")
    void rejectedResult() {
        List<DiscardReason> reasons = List.of(
                DiscardReason.OUTER_HAS_ELSE,
                DiscardReason.METHOD_CALL_IN_CONDITION);
        DetectionResult result = DetectionResult.rejected(reasons, 42);

        assertFalse(result.isAccepted());
        assertNull(result.getOpportunity());
        assertEquals(2, result.getDiscardReasons().size());
        assertTrue(result.getDiscardReasons().contains(DiscardReason.OUTER_HAS_ELSE));
        assertTrue(result.getDiscardReasons().contains(DiscardReason.METHOD_CALL_IN_CONDITION));
        assertEquals(42, result.getCandidateLine());
    }

    @Test
    @DisplayName("rejected() con lista vacía es rechazado sin motivos específicos")
    void rejectedEmptyReasons() {
        DetectionResult result = DetectionResult.rejected(List.of(), 5);

        assertFalse(result.isAccepted());
        assertTrue(result.getDiscardReasons().isEmpty());
    }

    @Test
    @DisplayName("toString() de aceptado contiene ACCEPTED")
    void toStringAccepted() {
        RefactoringOpportunity opp = makeOpportunity();
        DetectionResult result = DetectionResult.accepted(opp);

        assertTrue(result.toString().contains("ACCEPTED"));
    }

    @Test
    @DisplayName("toString() de rechazado contiene REJECTED y motivos")
    void toStringRejected() {
        DetectionResult result = DetectionResult.rejected(
                List.of(DiscardReason.INNER_HAS_ELSE), 20);

        assertTrue(result.toString().contains("REJECTED"));
        assertTrue(result.toString().contains("INNER_HAS_ELSE"));
    }

    @Test
    @DisplayName("discardReasons es copia inmutable")
    void discardReasonsImmutable() {
        DetectionResult result = DetectionResult.rejected(
                List.of(DiscardReason.LAMBDA_IN_CONDITION), 1);

        assertThrows(UnsupportedOperationException.class,
                () -> result.getDiscardReasons().add(DiscardReason.OUTER_HAS_ELSE));
    }
}
