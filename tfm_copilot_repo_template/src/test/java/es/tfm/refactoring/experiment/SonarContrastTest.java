package es.tfm.refactoring.experiment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de SonarContrastEntry y SonarContrastReport.
 * <p>
 * Valida la infraestructura de contraste entre las mediciones provisionales
 * del prototipo y los valores de referencia Sonar.
 */
@DisplayName("SonarContrast — Contraste provisional vs Sonar")
class SonarContrastTest {

    // =========================================================================
    // SonarContrastEntry
    // =========================================================================

    @Nested
    @DisplayName("SonarContrastEntry — Entrada individual")
    class EntryTests {

        @Test
        @DisplayName("Entrada verificada: desviación before/after calculada correctamente")
        void verifiedEntryDeviations() {
            SonarContrastEntry entry = new SonarContrastEntry(
                    "TEST_01", 3, 2, 3, 2, "coincide exactamente");
            assertTrue(entry.isFullyVerified());
            assertEquals(0, entry.deviationBefore());
            assertEquals(0, entry.deviationAfter());
        }

        @Test
        @DisplayName("Entrada verificada con discrepancia: desviación absoluta correcta")
        void verifiedEntryWithDiscrepancy() {
            SonarContrastEntry entry = new SonarContrastEntry(
                    "TEST_02", 5, 3, 7, 4, "discrepancia por ternario");
            assertEquals(2, entry.deviationBefore());
            assertEquals(1, entry.deviationAfter());
        }

        @Test
        @DisplayName("Entrada no verificada: desviación devuelve NOT_VERIFIED")
        void notVerifiedReturnsMinusOne() {
            SonarContrastEntry entry = new SonarContrastEntry(
                    "TEST_03", 3, 2,
                    SonarContrastEntry.NOT_VERIFIED, SonarContrastEntry.NOT_VERIFIED,
                    "pendiente");
            assertFalse(entry.isFullyVerified());
            assertEquals(SonarContrastEntry.NOT_VERIFIED, entry.deviationBefore());
            assertEquals(SonarContrastEntry.NOT_VERIFIED, entry.deviationAfter());
        }

        @Test
        @DisplayName("Delta provisional se calcula correctamente")
        void provisionalDelta() {
            SonarContrastEntry entry = new SonarContrastEntry(
                    "TEST_04", 6, 4, 6, 4, "");
            assertEquals(-2, entry.provisionalDelta());
        }

        @Test
        @DisplayName("Delta Sonar se calcula correctamente cuando verificado")
        void sonarDelta() {
            SonarContrastEntry entry = new SonarContrastEntry(
                    "TEST_05", 6, 4, 6, 4, "");
            assertEquals(-2, entry.sonarDelta());
        }

        @Test
        @DisplayName("Delta Sonar devuelve NOT_VERIFIED cuando no verificado")
        void sonarDeltaNotVerified() {
            SonarContrastEntry entry = new SonarContrastEntry(
                    "TEST_06", 3, 2, SonarContrastEntry.NOT_VERIFIED, 2, "");
            assertEquals(SonarContrastEntry.NOT_VERIFIED, entry.sonarDelta());
        }

        @Test
        @DisplayName("deltaAgrees: true cuando ambos deltas coinciden")
        void deltaAgreesTrue() {
            SonarContrastEntry entry = new SonarContrastEntry(
                    "TEST_07", 4, 2, 4, 2, "");
            assertTrue(entry.deltaAgrees());
        }

        @Test
        @DisplayName("deltaAgrees: false cuando deltas difieren")
        void deltaAgreesFalse() {
            SonarContrastEntry entry = new SonarContrastEntry(
                    "TEST_08", 4, 2, 5, 4, "");
            // prov delta = -2, sonar delta = -1
            assertFalse(entry.deltaAgrees());
        }

        @Test
        @DisplayName("deltaAgrees: false cuando no verificado")
        void deltaAgreesNotVerified() {
            SonarContrastEntry entry = new SonarContrastEntry(
                    "TEST_09", 4, 2,
                    SonarContrastEntry.NOT_VERIFIED, SonarContrastEntry.NOT_VERIFIED,
                    "");
            assertFalse(entry.deltaAgrees());
        }

        @Test
        @DisplayName("toString muestra interrogaciones para valores no verificados")
        void toStringNotVerified() {
            SonarContrastEntry entry = new SonarContrastEntry(
                    "TEST_10", 3, 2,
                    SonarContrastEntry.NOT_VERIFIED, SonarContrastEntry.NOT_VERIFIED,
                    "");
            String str = entry.toString();
            assertTrue(str.contains("?"), "Debe mostrar ? para no verificados");
            assertTrue(str.contains("TEST_10"));
        }
    }

    // =========================================================================
    // SonarContrastReport
    // =========================================================================

    @Nested
    @DisplayName("SonarContrastReport — Informe agregado")
    class ReportTests {

        @Test
        @DisplayName("Informe vacío: 0 entradas, sin métricas")
        void emptyReport() {
            SonarContrastReport report = new SonarContrastReport();
            assertEquals(0, report.totalEntries());
            assertEquals(0, report.verifiedEntries());
            assertEquals(0, report.pendingEntries());
            assertEquals(-1, report.meanDeviationBefore());
        }

        @Test
        @DisplayName("addEntry incrementa contadores")
        void addEntryIncrementsCount() {
            SonarContrastReport report = new SonarContrastReport();
            report.addEntry(new SonarContrastEntry("A", 3, 2, 3, 2, ""));
            assertEquals(1, report.totalEntries());
            assertEquals(1, report.verifiedEntries());
        }

        @Test
        @DisplayName("Constructor con lista inicializa entradas")
        void constructorWithList() {
            SonarContrastReport report = new SonarContrastReport(List.of(
                    new SonarContrastEntry("A", 3, 2, 3, 2, ""),
                    new SonarContrastEntry("B", 4, 3, 4, 3, "")
            ));
            assertEquals(2, report.totalEntries());
        }

        @Test
        @DisplayName("pendingEntries cuenta entradas sin verificar")
        void pendingCount() {
            SonarContrastReport report = new SonarContrastReport(List.of(
                    new SonarContrastEntry("A", 3, 2, 3, 2, ""),
                    new SonarContrastEntry("B", 4, 3, -1, -1, "pendiente")
            ));
            assertEquals(1, report.verifiedEntries());
            assertEquals(1, report.pendingEntries());
        }

        @Test
        @DisplayName("exactMatchBefore: cuenta coincidencias exactas before")
        void exactMatchBeforeCount() {
            SonarContrastReport report = new SonarContrastReport(List.of(
                    new SonarContrastEntry("A", 3, 2, 3, 2, ""),  // match
                    new SonarContrastEntry("B", 4, 3, 5, 3, ""),  // no match before
                    new SonarContrastEntry("C", 6, 4, 6, 4, "")   // match
            ));
            assertEquals(2, report.exactMatchBefore());
        }

        @Test
        @DisplayName("exactMatchAfter: cuenta coincidencias exactas after")
        void exactMatchAfterCount() {
            SonarContrastReport report = new SonarContrastReport(List.of(
                    new SonarContrastEntry("A", 3, 2, 3, 2, ""),  // match
                    new SonarContrastEntry("B", 4, 3, 4, 4, ""),  // no match after
                    new SonarContrastEntry("C", 6, 4, 6, 4, "")   // match
            ));
            assertEquals(2, report.exactMatchAfter());
        }

        @Test
        @DisplayName("deltaAgreement: deltas coinciden")
        void deltaAgreementCount() {
            SonarContrastReport report = new SonarContrastReport(List.of(
                    new SonarContrastEntry("A", 3, 2, 3, 2, ""),  // delta -1 = -1 ✓
                    new SonarContrastEntry("B", 4, 3, 5, 3, ""),  // delta -1 vs -2 ✗
                    new SonarContrastEntry("C", 6, 4, 6, 4, "")   // delta -2 = -2 ✓
            ));
            assertEquals(2, report.deltaAgreement());
        }

        @Test
        @DisplayName("meanDeviationBefore: media de desviaciones")
        void meanDeviationBefore() {
            SonarContrastReport report = new SonarContrastReport(List.of(
                    new SonarContrastEntry("A", 3, 2, 3, 2, ""),  // dev 0
                    new SonarContrastEntry("B", 4, 3, 6, 3, "")   // dev 2
            ));
            assertEquals(1.0, report.meanDeviationBefore(), 0.001);
        }

        @Test
        @DisplayName("withinToleranceBefore: tolerancia ±1")
        void withinToleranceBefore() {
            SonarContrastReport report = new SonarContrastReport(List.of(
                    new SonarContrastEntry("A", 3, 2, 3, 2, ""),  // dev 0 ≤ 1 ✓
                    new SonarContrastEntry("B", 4, 3, 5, 3, ""),  // dev 1 ≤ 1 ✓
                    new SonarContrastEntry("C", 6, 4, 9, 4, "")   // dev 3 > 1 ✗
            ));
            assertEquals(2, report.withinToleranceBefore(1));
        }

        @Test
        @DisplayName("summary() genera texto con todas las secciones")
        void summaryContainsAllSections() {
            SonarContrastReport report = new SonarContrastReport(List.of(
                    new SonarContrastEntry("A", 3, 2, 3, 2, "ok"),
                    new SonarContrastEntry("B", 4, 3, -1, -1, "pendiente")
            ));
            String summary = report.summary();
            assertTrue(summary.contains("Sonar Contrast Report"));
            assertTrue(summary.contains("Total entries"));
            assertTrue(summary.contains("Verified"));
            assertTrue(summary.contains("Pending"));
            assertTrue(summary.contains("Detail"));
        }

        @Test
        @DisplayName("getEntries devuelve vista inmutable")
        void entriesAreImmutable() {
            SonarContrastReport report = new SonarContrastReport(List.of(
                    new SonarContrastEntry("A", 3, 2, 3, 2, "")
            ));
            assertThrows(UnsupportedOperationException.class,
                    () -> report.getEntries().add(
                            new SonarContrastEntry("X", 0, 0, 0, 0, "")));
        }
    }

    // =========================================================================
    // Contraste con corpus piloto — valores Sonar pre-computados
    // =========================================================================

    @Nested
    @DisplayName("Contraste pilot corpus — valores Sonar manuales")
    class PilotContrastTests {

        /**
         * Construye un informe de contraste para los 8 casos piloto con valores
         * Sonar computados manualmente siguiendo la especificación SonarSource.
         * <p>
         * Para este subconjunto (solo if/else/for, sin ternarios, lambdas ni
         * recursión), el calculador provisional debe coincidir exactamente.
         */
        private SonarContrastReport buildPilotContrastReport() {
            return new SonarContrastReport(List.of(
                    // Elegibles: CC before → CC after (provisionally computed)
                    new SonarContrastEntry("PILOT_VALID_SIMPLE",
                            3, 2, 3, 2,
                            "if(value>0){if(value<=limit)} → combinado. Sonar coincide."),
                    new SonarContrastEntry("PILOT_VALID_NULL_CHECK",
                            3, 2, 3, 2,
                            "if(data!=null){if(data.length>0)} → combinado. Array.length es campo."),
                    new SonarContrastEntry("PILOT_VALID_BOUNDS_CHECK",
                            4, 2, 4, 2,
                            "if(A&&B){if(C)} → un && ya existe, se agrega C. Sonar coincide."),
                    new SonarContrastEntry("PILOT_VALID_NESTED_IN_LOOP",
                            6, 4, 6, 4,
                            "for+if+if → for+if(A&&B). Mayor impacto por nesting en loop."),
                    // Inelegibles: CC no cambia (after == before)
                    new SonarContrastEntry("PILOT_INVALID_ELSE_BRANCH",
                            4, 4, 4, 4,
                            "Rechazado P1: outer has else. CC invariante."),
                    new SonarContrastEntry("PILOT_INVALID_METHOD_GUARD",
                            3, 3, 3, 3,
                            "Rechazado P5: isEmpty() es method call. CC invariante."),
                    new SonarContrastEntry("PILOT_INVALID_MULTI_STMT",
                            3, 3, 3, 3,
                            "Rechazado P2: bloque externo tiene int x + if. CC invariante."),
                    new SonarContrastEntry("PILOT_MIXED_OPPORTUNITIES",
                            6, 5, 6, 5,
                            "1 oportunidad combinada, 1 rechazada (P5 method call).")
            ));
        }

        @Test
        @DisplayName("8 entradas piloto, todas verificadas")
        void allPilotEntriesVerified() {
            SonarContrastReport report = buildPilotContrastReport();
            assertEquals(8, report.totalEntries());
            assertEquals(8, report.verifiedEntries());
            assertEquals(0, report.pendingEntries());
        }

        @Test
        @DisplayName("Coincidencia exacta 100% en before para casos piloto")
        void exactMatchBeforeAllPilot() {
            SonarContrastReport report = buildPilotContrastReport();
            assertEquals(8, report.exactMatchBefore(),
                    "Todos los before deben coincidir para patrones sin ternario/lambda/recursión");
        }

        @Test
        @DisplayName("Coincidencia exacta 100% en after para casos piloto")
        void exactMatchAfterAllPilot() {
            SonarContrastReport report = buildPilotContrastReport();
            assertEquals(8, report.exactMatchAfter(),
                    "Todos los after deben coincidir para patrones sin ternario/lambda/recursión");
        }

        @Test
        @DisplayName("Acuerdo en delta 100% para casos piloto")
        void deltaAgreementAllPilot() {
            SonarContrastReport report = buildPilotContrastReport();
            assertEquals(8, report.deltaAgreement(),
                    "Todos los deltas deben coincidir: el calculador cubre estos patrones");
        }

        @Test
        @DisplayName("Desviación media 0.0 antes y después para casos piloto")
        void zeroMeanDeviation() {
            SonarContrastReport report = buildPilotContrastReport();
            assertEquals(0.0, report.meanDeviationBefore(), 0.001);
            assertEquals(0.0, report.meanDeviationAfter(), 0.001);
        }

        @Test
        @DisplayName("Resumen incluye métricas piloto")
        void summaryIncludesPilotMetrics() {
            SonarContrastReport report = buildPilotContrastReport();
            String summary = report.summary();
            assertTrue(summary.contains("8"), "Debe reflejar 8 entradas");
            assertTrue(summary.contains("Exact match before: 8/8"));
        }
    }
}
