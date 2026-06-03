public class POI_V_B_A_MACRO_READER_FIND_COMPRESSED_STREAM_W_BRUTE_FORCE {
/**
 * Sometimes the offset record in the dirstream is incorrect, but the macro can still be found.
 * This will try to find the first RLEDecompressing stream that starts with "Attribute".
 * This relies on some, er, heuristics, admittedly.
 *
 * @param is full module inputstream to read
 * @return uncompressed bytes if found, {@code null} otherwise
 * @throws IOException for a true IOException copying the is to a byte array
 */
private static byte[] findCompressedStreamWBruteForce(InputStream is) throws IOException {
    //buffer to memory for multiple tries
    byte[] compressed = IOUtils.toByteArray(is);
    byte[] decompressed = null;
    for (int i = 0; i < compressed.length; i++) {
        if (compressed[i] == 0x01 && i < compressed.length - 1) {
            int w = LittleEndian.getUShort(compressed, i + 1);
            if (w <= 0 || (w & 0x7000) != 0x3000) {
                continue;
            }
            decompressed = tryToDecompress(UnsynchronizedByteArrayInputStream.builder().setByteArray(compressed).setOffset(i).setLength(compressed.length - i).get());
            if (decompressed != null) {
                if (decompressed.length > 9) {
                    //this is a complete hack.  The challenge is that there
                    //can be many 0 length or junk streams that are uncompressed
                    //look in the first 20 characters for "Attribute"
                    int firstX = Math.min(20, decompressed.length);
                    String start = new String(decompressed, 0, firstX, StringUtil.WIN_1252);
                    if (start.contains("Attribute")) {
                        return decompressed;
                    }
                }
            }
        }
    }
    return decompressed;
}
}

