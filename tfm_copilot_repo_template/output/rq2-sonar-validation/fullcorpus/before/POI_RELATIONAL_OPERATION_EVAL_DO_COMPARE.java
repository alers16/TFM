public class POI_RELATIONAL_OPERATION_EVAL_DO_COMPARE {
private static int doCompare(ValueEval va, ValueEval vb) {
    // special cases when one operand is blank or missing
    if (va == BlankEval.instance || va instanceof MissingArgEval) {
        return compareBlank(vb);
    }
    if (vb == BlankEval.instance || vb instanceof MissingArgEval) {
        return -compareBlank(va);
    }
    if (va instanceof BoolEval) {
        if (vb instanceof BoolEval) {
            BoolEval bA = (BoolEval) va;
            BoolEval bB = (BoolEval) vb;
            if (bA.getBooleanValue() == bB.getBooleanValue()) {
                return 0;
            }
            return bA.getBooleanValue() ? 1 : -1;
        }
        return 1;
    }
    if (vb instanceof BoolEval) {
        return -1;
    }
    if (va instanceof StringEval) {
        if (vb instanceof StringEval) {
            StringEval sA = (StringEval) va;
            StringEval sB = (StringEval) vb;
            return sA.getStringValue().compareToIgnoreCase(sB.getStringValue());
        }
        return 1;
    }
    if (vb instanceof StringEval) {
        return -1;
    }
    if (va instanceof NumberEval) {
        if (vb instanceof NumberEval) {
            NumberEval nA = (NumberEval) va;
            NumberEval nB = (NumberEval) vb;
            return NumberComparer.compare(nA.getNumberValue(), nB.getNumberValue());
        }
    }
    throw new IllegalArgumentException("Bad operand types (" + va.getClass().getName() + "), (" + vb.getClass().getName() + ")");
}
}

