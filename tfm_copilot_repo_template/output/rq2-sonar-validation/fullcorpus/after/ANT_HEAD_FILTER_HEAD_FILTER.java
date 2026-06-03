public class ANT_HEAD_FILTER_HEAD_FILTER {
/**
 * implements a head filter on the input stream
 */
private String headFilter(String line) {
    linesRead++;
    if (skip > 0 && (linesRead - 1) < skip) {
        return null;
    }
    if (lines > 0 && linesRead > (lines + skip)) {
        eof = true;
        return null;
    }
    return line;
}
}

