public class POI_VALUE_CONVERT_TEXT_TO_NUMBER {
/**
 * TODO see if the same functionality is needed in {@link OperandResolver#parseDouble(String)}
 *
 * @return <code>null</code> if there is any problem converting the text
 */
public static Double convertTextToNumber(String strText) {
    boolean foundCurrency = false;
    boolean foundUnaryPlus = false;
    boolean foundUnaryMinus = false;
    boolean foundPercentage = false;
    int len = strText.length();
    int i;
    for (i = 0; i < len; i++) {
        char ch = strText.charAt(i);
        if (Character.isDigit(ch) || ch == '.') {
            break;
        }
        switch(ch) {
            case ' ':
                // intervening spaces between '$', '-', '+' are OK
                continue;
            case '$':
                if (foundCurrency) {
                    // only one currency symbols is allowed
                    return null;
                }
                foundCurrency = true;
                continue;
            case '+':
                if (foundUnaryMinus || foundUnaryPlus) {
                    return null;
                }
                foundUnaryPlus = true;
                continue;
            case '-':
                if (foundUnaryMinus || foundUnaryPlus) {
                    return null;
                }
                foundUnaryMinus = true;
                continue;
            default:
                // all other characters are illegal
                return null;
        }
    }
    if (i >= len) {
        // didn't find digits or '.'
        if (foundCurrency || foundUnaryMinus || foundUnaryPlus) {
            return null;
        }
        return ZERO;
    }
    // remove thousands separators
    boolean foundDecimalPoint = false;
    int lastThousandsSeparatorIndex = Short.MIN_VALUE;
    StringBuilder sb = new StringBuilder(len);
    for (; i < len; i++) {
        char ch = strText.charAt(i);
        if (Character.isDigit(ch)) {
            sb.append(ch);
            continue;
        }
        switch(ch) {
            case ' ':
                String remainingTextTrimmed = strText.substring(i).trim();
                // support for value[space]%
                if (remainingTextTrimmed.equals("%")) {
                    foundPercentage = true;
                    break;
                }
                if (!remainingTextTrimmed.isEmpty()) {
                    // intervening spaces not allowed once the digits start
                    return null;
                }
                break;
            case '.':
                if (foundDecimalPoint) {
                    return null;
                }
                if (i - lastThousandsSeparatorIndex < MIN_DISTANCE_BETWEEN_THOUSANDS_SEPARATOR) {
                    return null;
                }
                foundDecimalPoint = true;
                sb.append('.');
                continue;
            case ',':
                if (foundDecimalPoint) {
                    // thousands separators not allowed after '.' or 'E'
                    return null;
                }
                int distanceBetweenThousandsSeparators = i - lastThousandsSeparatorIndex;
                // as long as there are 3 or more digits between
                if (distanceBetweenThousandsSeparators < MIN_DISTANCE_BETWEEN_THOUSANDS_SEPARATOR) {
                    return null;
                }
                lastThousandsSeparatorIndex = i;
                // don't append ','
                continue;
            case 'E':
            case 'e':
                if (i - lastThousandsSeparatorIndex < MIN_DISTANCE_BETWEEN_THOUSANDS_SEPARATOR) {
                    return null;
                }
                // append rest of strText and skip to end of loop
                sb.append(strText.substring(i));
                i = len;
                break;
            case '%':
                foundPercentage = true;
                break;
            default:
                // all other characters are illegal
                return null;
        }
    }
    if (!foundDecimalPoint && i - lastThousandsSeparatorIndex < MIN_DISTANCE_BETWEEN_THOUSANDS_SEPARATOR) {
        return null;
    }
    double d;
    try {
        d = Double.parseDouble(sb.toString());
    } catch (NumberFormatException e) {
        // still a problem parsing the number - probably out of range
        return null;
    }
    double result = foundUnaryMinus ? -d : d;
    return foundPercentage ? result / 100. : result;
}
}

