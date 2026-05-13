package es.tfm.refactoring.experiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Informe de contraste entre la complejidad cognitiva provisional (prototipo)
 * y la medida oficial de SonarQube/SonarLint.
 * <p>
 * Acumula entradas {@link SonarContrastEntry} y calcula métricas de acuerdo
 * entre ambas mediciones.
 * <p>
 * <strong>Propósito metodológico:</strong> documentar de forma transparente
 * las diferencias entre la estimación provisional utilizada durante el desarrollo
 * y la medición de referencia, como parte de las amenazas a la validez interna
 * del estudio (RQ2).
 * <p>
 * <strong>Procedimiento de obtención de valores Sonar:</strong> véase
 * {@code docs/sonar-contrast-procedure.md}.
 */
public class SonarContrastReport {

    private final List<SonarContrastEntry> entries;

    public SonarContrastReport() {
        this.entries = new ArrayList<>();
    }

    public SonarContrastReport(List<SonarContrastEntry> entries) {
        this.entries = new ArrayList<>(entries);
    }

    /** Añade una entrada de contraste. */
    public void addEntry(SonarContrastEntry entry) {
        entries.add(entry);
    }

    /** Devuelve todas las entradas (vista inmutable). */
    public List<SonarContrastEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    /** Número total de entradas. */
    public int totalEntries() {
        return entries.size();
    }

    /** Número de entradas con verificación Sonar completa. */
    public int verifiedEntries() {
        return (int) entries.stream()
                .filter(SonarContrastEntry::isFullyVerified)
                .count();
    }

    /** Número de entradas pendientes de verificación Sonar. */
    public int pendingEntries() {
        return totalEntries() - verifiedEntries();
    }

    /**
     * Número de entradas verificadas donde el valor before coincide exactamente.
     */
    public int exactMatchBefore() {
        return (int) entries.stream()
                .filter(SonarContrastEntry::isFullyVerified)
                .filter(e -> e.deviationBefore() == 0)
                .count();
    }

    /**
     * Número de entradas verificadas donde el valor after coincide exactamente.
     */
    public int exactMatchAfter() {
        return (int) entries.stream()
                .filter(SonarContrastEntry::isFullyVerified)
                .filter(e -> e.deviationAfter() == 0)
                .count();
    }

    /**
     * Número de entradas verificadas donde el delta (after-before) coincide.
     */
    public int deltaAgreement() {
        return (int) entries.stream()
                .filter(SonarContrastEntry::deltaAgrees)
                .count();
    }

    /**
     * Desviación media absoluta en el valor before para entradas verificadas.
     * @return media o -1 si no hay entradas verificadas
     */
    public double meanDeviationBefore() {
        List<Integer> devs = entries.stream()
                .filter(SonarContrastEntry::isFullyVerified)
                .map(SonarContrastEntry::deviationBefore)
                .toList();
        if (devs.isEmpty()) return -1;
        return devs.stream().mapToInt(Integer::intValue).average().orElse(-1);
    }

    /**
     * Desviación media absoluta en el valor after para entradas verificadas.
     * @return media o -1 si no hay entradas verificadas
     */
    public double meanDeviationAfter() {
        List<Integer> devs = entries.stream()
                .filter(SonarContrastEntry::isFullyVerified)
                .map(SonarContrastEntry::deviationAfter)
                .toList();
        if (devs.isEmpty()) return -1;
        return devs.stream().mapToInt(Integer::intValue).average().orElse(-1);
    }

    /**
     * Número de entradas verificadas donde before coincide con tolerancia ±1.
     */
    public int withinToleranceBefore(int tolerance) {
        return (int) entries.stream()
                .filter(SonarContrastEntry::isFullyVerified)
                .filter(e -> e.deviationBefore() <= tolerance)
                .count();
    }

    /**
     * Genera un resumen textual del contraste para documentación.
     */
    public String summary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Sonar Contrast Report ===\n");
        sb.append(String.format("Total entries:      %d\n", totalEntries()));
        sb.append(String.format("Verified:           %d\n", verifiedEntries()));
        sb.append(String.format("Pending:            %d\n", pendingEntries()));

        if (verifiedEntries() > 0) {
            sb.append(String.format("Exact match before: %d/%d\n",
                    exactMatchBefore(), verifiedEntries()));
            sb.append(String.format("Exact match after:  %d/%d\n",
                    exactMatchAfter(), verifiedEntries()));
            sb.append(String.format("Delta agreement:    %d/%d\n",
                    deltaAgreement(), verifiedEntries()));
            sb.append(String.format("Mean dev before:    %.2f\n",
                    meanDeviationBefore()));
            sb.append(String.format("Mean dev after:     %.2f\n",
                    meanDeviationAfter()));
        }

        sb.append("\n--- Detail ---\n");
        for (SonarContrastEntry e : entries) {
            sb.append(e.toString()).append("\n");
        }
        return sb.toString();
    }
}
