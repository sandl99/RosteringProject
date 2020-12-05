package cp;

import com.google.ortools.sat.*;
import utils.Data;

public class CP {
    public Data data;
    CpModel cpModel;
    public IntVar[][][] x;
    IntVar s;
    public CpSolver cpSolver;

    static {
        System.loadLibrary("jniortools"); // Load the native library.
    }


    public CP (String filename) {
        this.data = new Data(filename);
        this.cpModel = new CpModel();
        this.x = new IntVar[data.D][4][data.N];
    }

    private void init() {
        for (int i = 0; i < data.D; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < data.N; k++) {
                    this.x[i][j][k] = this.cpModel.newIntVar(0, 1, "x[" + i + "][" + j + "][" + k + "]");
                }
            }
        }
        this.s = this.cpModel.newIntVar(0, Integer.MAX_VALUE, "s");
    }

    private void makeConstraint() {
//        Nhan vien lam nhieu nhat mot buoi va co the nghi mot so buoi
        for (int k = 0; k < data.N; k++) {
            for (int i = 0; i < data.D; i++) {
                if (data.dayOff[k].contains(i)) {
                    Constraint constraint = this.cpModel.addEquality(LinearExpr.sum(new IntVar[]{this.x[i][0][k], this.x[i][1][k], this.x[i][2][k], this.x[i][3][k]}),0);
                } else {
                    Constraint constraint = this.cpModel.addLinearConstraint(LinearExpr.sum(new IntVar[]{this.x[i][0][k], this.x[i][1][k], this.x[i][2][k], this.x[i][3][k]}), 0, 1);
                }
            }
        }
//         Mỗi ca cần có từ alpha đến beta nhân viên
        for (int i = 0; i < data.D; i++) {
            for (int j = 0; j < 4; j++) {
                Constraint constraint = this.cpModel.addLinearConstraint(LinearExpr.sum(this.x[i][j]), data.alp, data.bet);
            }
        }
//          Hôm nay làm ca đêm thì hôm sau được nghỉ
        for (int i = 1; i < data.D; i++) {
            for (int k = 0; k < data.N; k++) {
                IntVar b = this.cpModel.newBoolVar("b[" + i + "][" + k + "]");
                this.cpModel.addEquality(LinearExpr.sum(new IntVar[] {this.x[i - 1][3][k]}), 1).onlyEnforceIf(b);
                this.cpModel.addEquality(LinearExpr.sum(new IntVar[] {this.x[i - 1][3][k]}), 0).onlyEnforceIf(b.not());
                IntVar c = this.cpModel.newBoolVar("c[" + i + "][" + k + "]");
                this.cpModel.addEquality(LinearExpr.sum(new IntVar[]{this.x[i][0][k], this.x[i][1][k], this.x[i][2][k], this.x[i][3][k]}),0).onlyEnforceIf(c);
                this.cpModel.addImplication(b, c);
            }
        }
    }
    private void makeObjective() {
        //        make maximize objective
        for (int k = 0; k < data.N; k++) {
            IntVar[] arr = new IntVar[data.D];
            for (int i = 0; i < data.D; i++) {
                arr[i] = this.x[i][3][k];
            }
            this.cpModel.addLessOrEqual(LinearExpr.sum(arr), LinearExpr.sum(new IntVar[]{s}));
        }
        this.cpModel.minimize(LinearExpr.sum(new IntVar[] {s}));
    }
    public void run() {
        this.init();
        this.makeConstraint();
        this.makeObjective();
        this.cpSolver = new CpSolver();
        CpSolverStatus solverStatus = cpSolver.solve(this.cpModel);

        if (solverStatus == CpSolverStatus.OPTIMAL) {
            System.out.println("Solution:");
            System.out.println("Objective value = " + cpSolver.objectiveValue());
            for (int i = 0; i < data.D; i++) {
                for (int j = 0; j < 4; j++) {
                    System.out.print("Day[" + (i) + "] --- Part[" + (j) + "] = {");
                    for (int k = 0; k < data.N; k++) {
                        if (cpSolver.value(this.x[i][j][k]) == 1)
                            System.out.print((k) + " ");
                    }
                    System.out.print("}\n");
                }
                System.out.println("--------------------------------------------");
            }
            System.out.println("Problem solved in " + cpSolver.wallTime() + " milliseconds");
            System.out.println();
        } else {
            System.out.println(solverStatus);
        }
    }

    public static void main(String[] args) {
        CP cp = new CP("data/sample.txt");
        cp.run();
    }
}
