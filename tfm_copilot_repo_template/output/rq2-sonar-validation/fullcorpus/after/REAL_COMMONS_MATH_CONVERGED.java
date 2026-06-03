public class REAL_COMMONS_MATH_CONVERGED {
boolean hasConverged(double absoluteError, double relativeError, double absoluteTolerance, double relativeTolerance) {
    if (absoluteError <= absoluteTolerance && relativeError <= relativeTolerance) {
        return true;
    }
    return false;
}
}

