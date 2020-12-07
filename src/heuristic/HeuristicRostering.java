package heuristic;

import com.google.ortools.sat.Constraint;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import core.VarInt;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.MultiStageGreedySearch;
import test.TestCase;
import utils.Data;

public class HeuristicRostering {
    VarIntLS[][][] x;
    Data data;
    LocalSearchManager localSearchManager;
    ConstraintSystem S;
    VarIntLS s;
//    private static int[] sum;

    public HeuristicRostering(String filename) {
        this.data = new Data(filename);
        this.x = new VarIntLS[data.D][4][data.N];
    }

    public void init() {
        localSearchManager = new LocalSearchManager();
        for (int i = 0; i < data.D; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < data.N; k++) {
                    this.x[i][j][k] = new VarIntLS(localSearchManager, 0, 1, "x[" + i + "][" + j + "][" + k + "]");
                }
            }
        }
        this.S = new ConstraintSystem(this.localSearchManager);
        this.s = new VarIntLS(localSearchManager, 0, 5000, "s");
    }

    public void makeConstraint() {
//        Nhan vien lam nhieu nhat mot buoi va co the nghi mot so buoi
        for (int k = 0; k < data.N; k++) {
            for (int i = 0; i < data.D; i++) {
                Sum sum = new Sum(new VarIntLS[]{this.x[i][0][k], this.x[i][1][k], this.x[i][2][k], this.x[i][3][k]});
                if (data.dayOff[k].contains(i)) {
//                    Constraint constraint = this.cpModel.addEquality(LinearExpr.sum(new IntVar[]{this.x[i][0][k], this.x[i][1][k], this.x[i][2][k], this.x[i][3][k]}),0);
                    this.S.post(new IsEqual(sum, 0));
                } else {
//                    Constraint constraint = this.cpModel.addLinearConstraint(LinearExpr.sum(new IntVar[]{this.x[i][0][k], this.x[i][1][k], this.x[i][2][k], this.x[i][3][k]}), 0, 1);
                    this.S.post(new LessOrEqual(sum, 1));
                }
            }
        }
//         Mỗi ca cần có từ alpha đến beta nhân viên
        for (int i = 0; i < data.D; i++) {
            for (int j = 0; j < 4; j++) {
//                Constraint constraint = this.cpModel.addLinearConstraint(LinearExpr.sum(this.x[i][j]), data.alp, data.bet);
                Sum sum = new Sum(this.x[i][j]);
                this.S.post(new LessOrEqual(sum, data.bet));
                this.S.post(new LessOrEqual(data.alp, sum));
            }
        }
//          Hôm nay làm ca đêm thì hôm sau được nghỉ
        for (int i = 1; i < data.D; i++) {
            for (int k = 0; k < data.N; k++) {
//                IntVar b = this.cpModel.newBoolVar("b[" + i + "][" + k + "]");
//                this.cpModel.addEquality(LinearExpr.sum(new IntVar[] {this.x[i - 1][3][k]}), 1).onlyEnforceIf(b);
//                this.cpModel.addEquality(LinearExpr.sum(new IntVar[] {this.x[i - 1][3][k]}), 0).onlyEnforceIf(b.not());
//                IntVar c = this.cpModel.newBoolVar("c[" + i + "][" + k + "]");
//                this.cpModel.addEquality(LinearExpr.sum(new IntVar[]{this.x[i][0][k], this.x[i][1][k], this.x[i][2][k], this.x[i][3][k]}),0).onlyEnforceIf(c);
//                this.cpModel.addImplication(b, c);
                Sum sum = new Sum(new VarIntLS[] {this.x[i][0][k], this.x[i][1][k], this.x[i][2][k], this.x[i][3][k]});
                this.S.post(new Implicate(new IsEqual(this.x[i - 1][3][k], 1), new IsEqual(sum, 0)));
            }
        }
    }

    public void makeObjective() {
        //        make maximize objective
        for (int k = 0; k < data.N; k++) {
            VarIntLS[] arr = new VarIntLS[data.D];
            for (int i = 0; i < data.D; i++) {
                arr[i] = this.x[i][3][k];
            }
//            this.cpModel.addLessOrEqual(LinearExpr.sum(arr), LinearExpr.sum(new IntVar[]{s}));
            Sum sum = new Sum(arr);
            this.S.post(new LessOrEqual(sum, s));
        }
//        this.cpModel.minimize(LinearExpr.sum(new IntVar[] {s}));
        this.localSearchManager.close();
    }

    public static void main(String[] args) {
        HeuristicRostering heuristicRostering = new HeuristicRostering("./data/sample.txt");
        heuristicRostering.init();
        heuristicRostering.makeConstraint();
        heuristicRostering.makeObjective();
        heuristicRostering.localSearchManager.print();
        VarIntLS[] y = heuristicRostering.S.getVariables();
        for (int i = 0; i < y.length; i++) {
            System.out.println(y[i].getDomain()+ " " + y[i].getName());
        }
        MultiStageGreedySearch multiStageGreedySearch = new MultiStageGreedySearch();
        multiStageGreedySearch.search(heuristicRostering.S, 120, 4000, true);
        System.out.println(heuristicRostering.s.getValue());
        System.out.println(heuristicRostering.S.getAssignDelta(heuristicRostering.s, 0));
        TestCase testCase = new TestCase(TestCase.convertSol(heuristicRostering.x, heuristicRostering.data), heuristicRostering.data, heuristicRostering.s.getValue());
        testCase.check();
        int san = 1;
    }

}
