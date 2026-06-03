public class REAL_COMMONS_LANG_DECREMENT_COND {
boolean checkBound(int count, int max) {
    if (count > 0) {
        if (--count < max) {
            return true;
        }
    }
    return false;
}
}

