package localsearch.search;

import localsearch.model.ConstraintSystem;
import localsearch.model.VarIntLS;

import java.util.ArrayList;
import java.util.Random;

public class HillClimbingWithObjective extends HillClimbing {
    int objective = Integer.MAX_VALUE;

    public void hillClimbing(ConstraintSystem c, int maxIter) {
        VarIntLS[] y = c.getVariables();
        ArrayList<AssignMove> cand = new ArrayList<>();
        Random rd = new Random();
        int it = 0, s_idx = 0;
        VarIntLS s;
        for (int i = 0; i < y.length; i++) {
            if (y[i].getName().charAt(0) == 's') {
                s = y[i];
                s_idx = i;
                break;
            }
        }
        while (it < maxIter) {
            cand.clear();
            int minDelta = Integer.MAX_VALUE;
            for (int i = 0; i < y.length; i++) {
                if (i == s_idx) { continue; }
                for (int v = y[i].getMinValue(); v <= y[i].getMaxValue(); v++) {
                    int d = c.getAssignDelta(y[i], v);
                    if (d < minDelta) {
                        cand.clear();
                        cand.add(new AssignMove(i, v));
                        minDelta = d;
                    } else if (d == minDelta) {
                        cand.add(new AssignMove(i, v));
                    }
                }
            }
        }
    }
}
