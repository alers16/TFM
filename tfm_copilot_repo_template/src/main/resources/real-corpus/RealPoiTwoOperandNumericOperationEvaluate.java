// @caseId POI_TWO_OPERAND_NUMERIC_OPERATION_EVALUATE
// @origin poi
// @project poi
// @file TwoOperandNumericOperation.java
// @method evaluate(int, int, ValueEval, ValueEval)
// @license [PENDIENTE]
// @sonarCCBefore 6
// @sonarCCAfter 5   (estimado; CC delta=-1)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class PoiTwoOperandNumericOperationEvaluate {
    @Override
    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval arg0, ValueEval arg1) {
        double result;
        try {
            double d0 = singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
            double d1 = singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
            result = evaluate(d0, d1);
            if (result == 0.0) {
                // this '==' matches +0.0 and -0.0
                // Excel converts -0.0 to +0.0 for '*', '/', '%', '+' and '^'
                if (!(this instanceof SubtractEvalClass)) {
                    return NumberEval.ZERO;
                }
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
