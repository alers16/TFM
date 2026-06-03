public class REAL_COMMONS_IO_ARRAY_SUBRANGE {
byte[] subarray(byte[] buf, int len) {
    if (buf != null) {
        if (buf.length >= len) {
            byte[] result = new byte[len];
            System.arraycopy(buf, 0, result, 0, len);
            return result;
        }
    }
    return new byte[0];
}
}

