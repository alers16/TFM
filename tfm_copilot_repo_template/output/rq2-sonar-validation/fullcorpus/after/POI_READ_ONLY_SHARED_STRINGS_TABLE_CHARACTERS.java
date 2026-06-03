public class POI_READ_ONLY_SHARED_STRINGS_TABLE_CHARACTERS {
/**
 * Captures characters only if a t(ext) element is open.
 */
public void characters(char[] ch, int start, int length) throws SAXException {
    if (tIsOpen) {
        if (inRPh && includePhoneticRuns) {
            if (characters != null) {
                characters.append(ch, start, length);
            }
        } else if (!inRPh && characters != null) {
            characters.append(ch, start, length);
        }
    }
}
}

