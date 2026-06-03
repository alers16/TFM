public class JMETAL_L_Z09_OBJECTIVE {
void objective(List<Double> xVar, List<Double> yObj) {
    // 2-objective case
    if (nobj == 2) {
        if (ltype == 21 || ltype == 22 || ltype == 23 || ltype == 24 || ltype == 26) {
            double g = 0, h = 0, a, b;
            ArrayList<Double> aa = new ArrayList<Double>();
            ArrayList<Double> bb = new ArrayList<Double>();
            for (int n = 1; n < nvar; n++) {
                if (n % 2 == 0) {
                    // linkage
                    a = psfunc2(xVar.get(n), xVar.get(0), n, ltype, 1);
                    aa.add(a);
                } else {
                    b = psfunc2(xVar.get(n), xVar.get(0), n, ltype, 2);
                    bb.add(b);
                }
            }
            g = betaFunction(aa, dtype);
            h = betaFunction(bb, dtype);
            double[] alpha = new double[2];
            // shape function
            alphaFunction(alpha, xVar, 2, ptype);
            yObj.set(0, alpha[0] + h);
            yObj.set(1, alpha[1] + g);
            aa.clear();
            bb.clear();
        }
        if (ltype == 25) {
            double g = 0, h = 0, a, b;
            double /*e = 0,*/
            c;
            ArrayList<Double> aa = new ArrayList<Double>();
            ArrayList<Double> bb = new ArrayList<Double>();
            for (int n = 1; n < nvar; n++) {
                if (n % 3 == 0) {
                    a = psfunc2(xVar.get(n), xVar.get(0), n, ltype, 1);
                    aa.add(a);
                } else if (n % 3 == 1) {
                    b = psfunc2(xVar.get(n), xVar.get(0), n, ltype, 2);
                    bb.add(b);
                } else {
                    c = psfunc2(xVar.get(n), xVar.get(0), n, ltype, 3);
                    if (n % 2 == 0) {
                        aa.add(c);
                    } else {
                        bb.add(c);
                    }
                }
            }
            g = betaFunction(aa, dtype);
            h = betaFunction(bb, dtype);
            double[] alpha = new double[2];
            alphaFunction(alpha, xVar, 2, ptype);
            yObj.set(0, alpha[0] + h);
            yObj.set(1, alpha[1] + g);
            aa.clear();
            bb.clear();
        }
    }
    // 3-objective case
    if (nobj == 3 && (ltype == 31 || ltype == 32)) {
        double g = 0, h = 0, e = 0, a;
        ArrayList<Double> aa = new ArrayList<Double>();
        ArrayList<Double> bb = new ArrayList<Double>();
        ArrayList<Double> cc = new ArrayList<Double>();
        for (int n = 2; n < nvar; n++) {
            a = psfunc3(xVar.get(n), xVar.get(0), xVar.get(1), n, ltype);
            if (n % 3 == 0) {
                aa.add(a);
            } else if (n % 3 == 1) {
                bb.add(a);
            } else {
                cc.add(a);
            }
        }
        g = betaFunction(aa, dtype);
        h = betaFunction(bb, dtype);
        e = betaFunction(cc, dtype);
        double[] alpha = new double[3];
        alphaFunction(alpha, xVar, 3, ptype);
        yObj.set(0, alpha[0] + h);
        yObj.set(1, alpha[1] + g);
        yObj.set(2, alpha[2] + e);
        aa.clear();
        bb.clear();
        cc.clear();
    }
}
}

