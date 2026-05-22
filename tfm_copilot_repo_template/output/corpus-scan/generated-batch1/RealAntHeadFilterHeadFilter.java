// @caseId ANT_HEAD_FILTER_HEAD_FILTER
// @origin ant-1.10.14
// @project Apache Ant
// @file HeadFilter.java
// @method headFilter(String)
// @license Apache-2.0
// @sonarCCBefore 6
// @sonarCCAfter 4   (estimado; CC delta=-2)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class AntHeadFilterHeadFilter {
    /**
     * implements a head filter on the input stream
     */
    private String headFilter(String line) {
        linesRead++;
        if (skip > 0) {
            if ((linesRead - 1) < skip) {
                return null;
            }
        }
        if (lines > 0) {
            if (linesRead > (lines + skip)) {
                eof = true;
                return null;
            }
        }
        return line;
    }
}
