public class POI_LINEAR_REGRESSION_FUNCTION_EVALUATE_INTERNAL {
private double evaluateInternal(ValueVector x, ValueVector y, int size) throws EvaluationException {
    // error handling is as if the x is fully evaluated before y
    ErrorEval firstYerr = null;
    boolean accumlatedSome = false;
    // first pass: read in data, compute xbar and ybar
    double sumx = 0.0, sumy = 0.0;
    for (int i = 0; i < size; i++) {
        ValueEval vx = x.getItem(i);
        ValueEval vy = y.getItem(i);
        if (vx instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval) vx);
        }
        if (vy instanceof ErrorEval && firstYerr == null) {
            firstYerr = (ErrorEval) vy;
            continue;
        }
        // only count pairs if both elements are numbers
        // all other combinations of value types are silently ignored
        if (vx instanceof NumberEval && vy instanceof NumberEval) {
            accumlatedSome = true;
            NumberEval nx = (NumberEval) vx;
            NumberEval ny = (NumberEval) vy;
            sumx += nx.getNumberValue();
            sumy += ny.getNumberValue();
        }
    }
    if (firstYerr != null) {
        throw new EvaluationException(firstYerr);
    }
    if (!accumlatedSome) {
        throw new EvaluationException(ErrorEval.DIV_ZERO);
    }
    double xbar = sumx / size;
    double ybar = sumy / size;
    // second pass: compute summary statistics
    double xxbar = 0.0, xybar = 0.0;
    for (int i = 0; i < size; i++) {
        ValueEval vx = x.getItem(i);
        ValueEval vy = y.getItem(i);
        // only count pairs if both elements are numbers
        // all other combinations of value types are silently ignored
        if (vx instanceof NumberEval && vy instanceof NumberEval) {
            NumberEval nx = (NumberEval) vx;
            NumberEval ny = (NumberEval) vy;
            xxbar += (nx.getNumberValue() - xbar) * (nx.getNumberValue() - xbar);
            xybar += (nx.getNumberValue() - xbar) * (ny.getNumberValue() - ybar);
        }
    }
    if (xxbar == 0) {
        throw new EvaluationException(ErrorEval.DIV_ZERO);
    }
    double beta1 = xybar / xxbar;
    double beta0 = ybar - beta1 * xbar;
    return (function == FUNCTION.INTERCEPT) ? beta0 : beta1;
}
}

