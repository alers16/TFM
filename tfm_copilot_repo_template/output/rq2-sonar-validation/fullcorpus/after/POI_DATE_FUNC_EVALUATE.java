public class POI_DATE_FUNC_EVALUATE {
public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1, ValueEval arg2) {
    double result;
    try {
        double d0 = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
        double d1 = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
        double d2 = NumericFunction.singleOperandEvaluate(arg2, srcRowIndex, srcColumnIndex);
        result = evaluate(getYear(d0), (int) (d1 - 1), (int) d2);
        NumericFunction.checkValue(result);
    } catch (EvaluationException e) {
        return e.getErrorEval();
    }
    return new NumberEval(result);
}
}

