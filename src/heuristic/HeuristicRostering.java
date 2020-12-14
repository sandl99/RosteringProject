package heuristic;

import com.google.ortools.sat.Constraint;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import core.VarInt;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.functions.max_min.Max;
import localsearch.functions.sum.Sum;
import localsearch.model.*;
import localsearch.search.MultiStageGreedySearch;
import localsearch.search.TabuSearch;
import test.TestCase;
import utils.Data;

public class HeuristicRostering {
    VarIntLS[][] x;
    Data data;
    LocalSearchManager localSearchManager;
    ConstraintSystem S;
    VarIntLS s;
    IFunction obj;
//    private static int[] sum;

    public HeuristicRostering(String filename) {
        this.data = new Data(filename);
        this.x = new VarIntLS[data.D][data.N];
    }

    public void init() {
        localSearchManager = new LocalSearchManager();
        for (int i = 0; i < data.D; i++) {
//            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < data.N; k++) {
                    this.x[i][k] = new VarIntLS(localSearchManager, 0, 4, "x[" + i + "][" + k + "]");
                }
//            }
        }
        this.S = new ConstraintSystem(this.localSearchManager);
        this.s = new VarIntLS(localSearchManager, 0, 1000, "s");
    }

    public void makeConstraint() {
//        Nhan vien lam nhieu nhat mot buoi va co the nghi mot so buoi
        for (int k = 0; k < data.N; k++) {
            for (int i = 0; i < data.D; i++) {
//                Sum sum = new Sum(new VarIntLS[]{this.x[i][0][k], this.x[i][1][k], this.x[i][2][k], this.x[i][3][k]});
                if (data.dayOff[k].contains(i)) {
//                    Constraint constraint = this.cpModel.addEquality(LinearExpr.sum(new IntVar[]{this.x[i][0][k], this.x[i][1][k], this.x[i][2][k], this.x[i][3][k]}),0);
//                    this.S.post(new IsEqual(sum, 0));
                    this.S.post(new IsEqual(this.x[i][k], 4));
//                } else {
//                    Constraint constraint = this.cpModel.addLinearConstraint(LinearExpr.sum(new IntVar[]{this.x[i][0][k], this.x[i][1][k], this.x[i][2][k], this.x[i][3][k]}), 0, 1);
//                    this.S.post(new LessOrEqual(sum, 1));
                }
            }
        }
//         Mỗi ca cần có từ alpha đến beta nhân viên
        int[] sum = new int[data.N];
        for (int i = 0; i < data.N; i++) {
            sum[i] = 1;
        }
        for (int i = 0; i < data.D; i++) {
            for (int j = 0; j < 4; j++) {
                ConditionalSum conditionalSum = new ConditionalSum(this.x[i], sum, j);
                this.S.post(new LessOrEqual(conditionalSum, data.bet));
                this.S.post(new LessOrEqual(data.alp, conditionalSum));
            }
        }
//          Hôm nay làm ca đêm thì hôm sau được nghỉ
        for (int i = 1; i < data.D; i++) {
            for (int k = 0; k < data.N; k++) {
                this.S.post(new Implicate(new IsEqual(this.x[i - 1][k], 3), new IsEqual(this.x[i][k], 4)));
            }
        }
    }

    public void makeObjective() {
        IFunction[] xT = new IFunction[data.N];
        int[] sum = new int[data.D];
        for (int i = 0; i < data.D; i++) sum[i] = 1;
        for (int k = 0; k < data.N; k++) {
            VarIntLS[] arr = new VarIntLS[data.D];
            for (int i = 0; i < data.D; i++) {
                arr[i] = this.x[i][k];
            }
            ConditionalSum conditionalSum = new ConditionalSum(arr, sum, 3);
            xT[k] = conditionalSum;
        }
        this.obj = new Max(xT);
        this.localSearchManager.close();
    }

    public static void main(String[] args) {
        HeuristicRostering heuristicRostering = new HeuristicRostering("./data/sample3.txt");
        heuristicRostering.init();
        heuristicRostering.makeConstraint();
        heuristicRostering.makeObjective();

        TabuSearch tabuSearch = new TabuSearch();
        tabuSearch.search(heuristicRostering.S, 50, 600, 3000, 10);
        tabuSearch.searchMaintainConstraintsMinimize(heuristicRostering.obj, heuristicRostering.S,50, 600, 3000, 10);
    }

}
