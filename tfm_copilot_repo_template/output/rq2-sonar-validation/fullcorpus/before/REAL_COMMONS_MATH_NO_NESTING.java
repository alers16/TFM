public class REAL_COMMONS_MATH_NO_NESTING {
int signum(double value) {
    if (value < 0) {
        return -1;
    }
    if (value > 0) {
        return 1;
    }
    return 0;
}
}

