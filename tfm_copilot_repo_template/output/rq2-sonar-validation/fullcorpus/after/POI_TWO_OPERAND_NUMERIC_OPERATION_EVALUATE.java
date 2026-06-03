public class POI_TWO_OPERAND_NUMERIC_OPERATION_EVALUATE {
@Override
public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
    double result;
    try {
        double d0 = singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
        double d1 = singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
        result = evaluate(d0, d1);
        if (result == 0.0 && !(this instanceof SubtractEvalClass)) {
            return NumberEval.ZERO;
        }
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            return ErrorEval.NUM_ERROR;
        }
    } catch (EvaluationException e) {
        return e.getErrorEval();
    }
    return new NumberEval(result);
}
}

