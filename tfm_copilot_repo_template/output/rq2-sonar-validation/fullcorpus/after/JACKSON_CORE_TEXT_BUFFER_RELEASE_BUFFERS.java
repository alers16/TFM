public class JACKSON_CORE_TEXT_BUFFER_RELEASE_BUFFERS {
/**
 *  Method called to indicate that the underlying buffers should now
 *  be recycled if they haven't yet been recycled. Although caller
 *  can still use this text buffer, it is not advisable to call this
 *  method if that is likely, since next time a buffer is needed,
 *  buffers need to reallocated.
 * <p>
 *  Note: since Jackson 2.11, calling this method will NOT clear already
 *  aggregated contents (that is, {@code _currentSegment}, to retain
 *  current token text if (but only if!) already aggregated.
 */
public void releaseBuffers() {
    // inlined `resetWithEmpty()` (except leaving `_resultString` as-is
    {
        _inputStart = -1;
        _currentSize = 0;
        _inputLen = 0;
        _inputBuffer = null;
        // note: _resultString retained (see https://github.com/FasterXML/jackson-databind/issues/2635
        // for reason)
        // should this be retained too?
        _resultArray = null;
        if (_hasSegments) {
            clearSegments();
        }
    }
    if (_allocator != null && _currentSegment != null) {
        // And then return that array
        char[] buf = _currentSegment;
        _currentSegment = null;
        _allocator.releaseCharBuffer(BufferRecycler.CHAR_TEXT_BUFFER, buf);
    }
}
}

