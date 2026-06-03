public class POI_OPERATION_EVALUATOR_FACTORY_EVALUATE {
/**
 * returns the OperationEval concrete impl instance corresponding
 * to the supplied operationPtg
 */
public static ValueEval evaluate(OperationPtg ptg, ValueEval[] args, OperationEvaluationContext ec) {
    if (ptg == null) {
        throw new IllegalArgumentException("ptg must not be null");
    }
    Function result = _instancesByPtgClass.get(ptg.getSid());
    FreeRefFunction udfFunc = null;
    if (result == null) {
        if (ptg instanceof AbstractFunctionPtg) {
            AbstractFunctionPtg fptg = (AbstractFunctionPtg) ptg;
            int functionIndex = fptg.getFunctionIndex();
            switch(functionIndex) {
                case FunctionMetadataRegistry.FUNCTION_INDEX_INDIRECT:
                    udfFunc = Indirect.instance;
                    break;
                case FunctionMetadataRegistry.FUNCTION_INDEX_EXTERNAL:
                    udfFunc = UserDefinedFunction.instance;
                    break;
                default:
                    result = FunctionEval.getBasicFunction(functionIndex);
                    break;
            }
        }
    }
    if (result != null) {
        if (result instanceof ArrayFunction) {
            ArrayFunction func = (ArrayFunction) result;
            ValueEval eval = evaluateArrayFunction(func, args, ec);
            if (eval != null) {
                return eval;
            }
        }
        return result.evaluate(args, ec.getRowIndex(), ec.getColumnIndex());
    } else if (udfFunc != null) {
        return udfFunc.evaluate(args, ec);
    }
    throw new IllegalStateException("Unexpected operation ptg class (" + ptg.getClass().getName() + ")");
}
}

