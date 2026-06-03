public class JACKSON_DATABIND_STD_DATE_FORMAT_PARSE_DATE {
protected Date _parseDate(String dateStr, ParsePosition pos) throws ParseException {
    if (looksLikeISO8601(dateStr)) {
        // also includes "plain"
        return parseAsISO8601(dateStr, pos);
    }
    // Also consider "stringified" simple time stamp
    int i = dateStr.length();
    while (--i >= 0) {
        char ch = dateStr.charAt(i);
        if ((ch < '0' || ch > '9') && (i > 0 || ch != '-')) {
            break;
        }
    }
    if (// let's just assume negative numbers are fine (can't be RFC-1123 anyway); check length for positive
    (i < 0) && (dateStr.charAt(0) == '-' || NumberInput.inLongRange(dateStr, false))) {
        return _parseDateFromLong(dateStr, pos);
    }
    // Otherwise, fall back to using RFC 1123. NOTE: call will NOT throw, just returns `null`
    return parseAsRFC1123(dateStr, pos);
}
}

