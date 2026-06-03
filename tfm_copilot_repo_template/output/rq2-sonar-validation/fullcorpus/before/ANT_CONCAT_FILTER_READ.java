public class ANT_CONCAT_FILTER_READ {
/**
 * Returns the next character in the filtered stream. If the desired
 * number of lines have already been read, the resulting stream is
 * effectively at an end. Otherwise, the next character from the
 * underlying stream is read and returned.
 *
 * @return the next character in the resulting stream, or -1
 * if the end of the resulting stream has been reached
 *
 * @exception IOException if the underlying stream throws an IOException
 * during reading
 */
@Override
public int read() throws IOException {
    // do the "singleton" initialization
    if (!getInitialized()) {
        initialize();
        setInitialized(true);
    }
    int ch = -1;
    // The readers return -1 if they end. So simply read the "prepend"
    // after that the "content" and at the end the "append" file.
    if (prependReader != null) {
        ch = prependReader.read();
        if (ch == -1) {
            // I am the only one so I have to close the reader
            prependReader.close();
            prependReader = null;
        }
    }
    if (ch == -1) {
        ch = super.read();
    }
    if (ch == -1) {
        // don't call super.close() because that reader is used
        // on other places ...
        if (appendReader != null) {
            ch = appendReader.read();
            if (ch == -1) {
                // I am the only one so I have to close the reader
                appendReader.close();
                appendReader = null;
            }
        }
    }
    return ch;
}
}

