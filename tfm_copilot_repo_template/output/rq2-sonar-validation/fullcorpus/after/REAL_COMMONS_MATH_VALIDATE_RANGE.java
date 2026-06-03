public class REAL_COMMONS_MATH_VALIDATE_RANGE {
String validateRange(double value, double lower, double upper) {
    if (value >= lower) {
        if (value <= upper) {
            return "in range";
        }
    } else {
        return "below minimum: " + lower;
    }
    return "above maximum: " + upper;
}
}

