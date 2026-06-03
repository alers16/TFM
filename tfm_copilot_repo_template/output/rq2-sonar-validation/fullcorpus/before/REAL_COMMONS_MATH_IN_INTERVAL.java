public class REAL_COMMONS_MATH_IN_INTERVAL {
boolean inInterval(double x, double low, double high) {
    if (x >= low) {
        if (x <= high) {
            return true;
        }
    }
    return false;
}
}

