package heuristic;

import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import utils.Data;

public class HeuristicRostering {
    VarIntLS[][] x;
    Data data;
    LocalSearchManager localSearchManager;
    ConstraintSystem S;
    VarIntLS s;
    private static int[] sum;

    public HeuristicRostering(String filename) {
        this.data = new Data(filename);
        this.x = new VarIntLS[data.D][data.N];
    }

    private void init() {
        localSearchManager = new LocalSearchManager();
        for (int i = 0; i < data.D; i++) {
            for (int k = 0; k < data.N; k++) {
                this.x[i][k] = new VarIntLS(localSearchManager, 0, 4);
            }
        }
        this.S = new ConstraintSystem(this.localSearchManager);
        this.s = new VarIntLS(localSearchManager, 0, Integer.MAX_VALUE);
    }

    private void makeConstraint() {
        for (int i = 1; i < data.D; i++) {
            for (int k = 0; k < data.N; k++) {
                this.S.post(new Implicate(new IsEqual(this.x[i - 1][k], 3), new IsEqual(this.x[i][k], 4)));
            }
        }
        sum = new int[data.N];
        for (int k = 0; k < data.N; k++) { sum[k] = 1; }
        for (int i = 0; i < data.D; i++) {
            for (int j = 1; j < 4; j++) {
                IFunction iFunction = new ConditionalSum(this.x[i], sum, j);
                this.S.post(new LessOrEqual(iFunction, data.bet));
                this.S.post(new LessOrEqual(data.alp, iFunction));
            }
        }
        for (int i = 0; i < data.D; i++) {
            for (int k = 0; k < data.N; k++) {
                if (data.dayOff[k].contains(i)) {
                    this.S.post(new IsEqual(this.x[i][k], 4));
                }
            }
        }
    }

    private void makeObjective() {
        for (int k = 0; k < data.N; k++) {
            VarIntLS[] arr = new VarIntLS[data.D];
            for (int i = 0; i < data.D; i++) {
                arr[i] = this.x[i][k];
            }
            IFunction night_shift = new ConditionalSum(arr, sum, 3);
            this.S.post(new LessOrEqual(night_shift, s));
        }

    }

}
