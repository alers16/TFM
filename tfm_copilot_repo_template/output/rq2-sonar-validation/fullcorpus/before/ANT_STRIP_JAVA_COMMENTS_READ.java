public class ANT_STRIP_JAVA_COMMENTS_READ {
/**
 * Returns the next character in the filtered stream, not including
 * Java comments.
 *
 * @return the next character in the resulting stream, or -1
 * if the end of the resulting stream has been reached
 *
 * @exception IOException if the underlying stream throws an IOException
 * during reading
 */
public int read() throws IOException {
    int ch = -1;
    if (readAheadCh != -1) {
        ch = readAheadCh;
        readAheadCh = -1;
    } else {
        ch = in.read();
        if (ch == '"' && !quoted) {
            inString = !inString;
            quoted = false;
        } else if (ch == '\\') {
            quoted = !quoted;
        } else {
            quoted = false;
            if (!inString) {
                if (ch == '/') {
                    ch = in.read();
                    if (ch == '/') {
                        while (ch != '\n' && ch != -1 && ch != '\r') {
                            ch = in.read();
                        }
                    } else if (ch == '*') {
                        while (ch != -1) {
                            ch = in.read();
                            if (ch == '*') {
                                ch = in.read();
                                while (ch == '*') {
                                    ch = in.read();
                                }
                                if (ch == '/') {
                                    ch = read();
                                    break;
                                }
                            }
                        }
                    } else {
                        readAheadCh = ch;
                        ch = '/';
                    }
                }
            }
        }
    }
    return ch;
}
}

