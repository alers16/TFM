public class JACKSON_CORE_U_T_F8_STREAM_JSON_PARSER_RELEASE_BUFFERS {
/**
 * Method called to release internal buffers owned by the base
 * reader. This may be called along with {@link #_closeInput} (for
 * example, when explicitly closing this reader instance), or
 * separately (if need be).
 */
@Override
protected void _releaseBuffers() {
    super._releaseBuffers();
    // Merge found symbols, if any:
    _symbols.release();
    if (_bufferRecyclable) {
        byte[] buf = _inputBuffer;
        if (buf != null) {
            // Let's not set it to null; this way should get slightly more meaningful
            // error messages in case someone closes parser indirectly, without realizing.
            if (buf != NO_BYTES) {
                _inputBuffer = NO_BYTES;
                _ioContext.releaseReadIOBuffer(buf);
            }
        }
    }
}
}

