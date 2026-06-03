public class POI_X_Y_NUMERIC_FUNCTION_EVALUATE_INTERNAL {
private double evaluateInternal(ValueVector x, ValueVector y, int size) throws EvaluationException {
    Accumulator acc = createAccumulator();
    // error handling is as if the x is fully evaluated before y
    ErrorEval firstXerr = null;
    ErrorEval firstYerr = null;
    boolean accumlatedSome = false;
    double result = 0.0;
    for (int i = 0; i < size; i++) {
        ValueEval vx = x.getItem(i);
        ValueEval vy = y.getItem(i);
        if (vx instanceof ErrorEval && firstXerr == null) {
            firstXerr = (ErrorEval) vx;
            continue;
        }
        if (vy instanceof ErrorEval && firstYerr == null) {
            firstYerr = (ErrorEval) vy;
            continue;
        }
        // only count pairs if both elements are numbers
        if (vx instanceof NumberEval && vy instanceof NumberEval) {
            accumlatedSome = true;
            NumberEval nx = (NumberEval) vx;
            NumberEval ny = (NumberEval) vy;
            result += acc.accumulate(nx.getNumberValue(), ny.getNumberValue());
        } else {
            // all other combinations of value types are silently ignored
        }
    }
    if (firstXerr != null) {
        throw new EvaluationException(firstXerr);
    }
    if (firstYerr != null) {
        throw new EvaluationException(firstYerr);
    }
    if (!accumlatedSome) {
        throw new EvaluationException(ErrorEval.DIV_ZERO);
    }
    return result;
}
}

