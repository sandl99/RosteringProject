package test;

import com.google.ortools.linearsolver.MPVariable;
import cp.CP;
import localsearch.model.VarIntLS;
import utils.Data;
import com.google.ortools.sat.*;

import java.util.logging.Logger;

public class TestCase {
    int[][][] x;
    Data data;
    final static Logger log = Logger.getGlobal();
    int s = -1;
    public TestCase(int[][][] x, Data data) {
        this.x = x;
        this.data = data;
    }
    public TestCase(int[][][] x, Data data, int s) {
        this.x = x;
        this.data = data;
        this.s = s;
    }

    public static int[][][] convertSol(IntVar[][][] x, CpSolver cpSolver, Data data) {
        int[][][] tmp = new int[data.D][4][data.N];
        for (int i = 0; i < data.D; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < data.N; k++) {
                    tmp[i][j][k] = (int) cpSolver.value(x[i][j][k]);
                }
            }
        }
        return tmp;
    }

    public static int[][][] convertSol(MPVariable[][][] x, Data data) {
        int[][][] tmp = new int[data.D][4][data.N];
        for (int i = 0; i < data.D; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < data.N; k++) {
                    tmp[i][j][k] = (int) x[i][j][k].solutionValue();
                }
            }
        }
        return tmp;
    }

    public static int[][][] convertSol(Data data) {
        int[][][] tmp = new int[data.D][4][data.N];
        return tmp;
    }

    public static int[][][] convertSol(int[][] x, Data data) {
        int[][][] tmp = new int[data.D][4][data.N];
        for (int i = 0; i < data.D; i++) {
            for (int k = 0; k < data.N; k++) {
                if (x[i][k] < 4) {
                    tmp[i][x[i][k] % 4][k] = 1;
                }
            }
        }
        return tmp;
    }
    public static int[][][] convertSol(VarIntLS[][][] x, Data data) {
        int[][][] tmp = new int[data.D][4][data.N];
        for (int i = 0; i < data.D; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < data.N; k++) {
                    tmp[i][j][k] = (int) x[i][j][k].getValue();
                }
            }
        }
        return tmp;
    }
    public int check() {
        int err = 0;
        err += this.checkC1();
        err += this.checkC2();
        err += this.checkC3();
        err += this.checkC4();
        if (s > -1) {
            err += this.checkC5();
        }
        System.out.println("Solution:");
        System.out.println("Objective value = " + s);
        for (int i = 0; i < data.D; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print("Day[" + (i) + "] --- Part[" + (j) + "] = {");
                for (int k = 0; k < data.N; k++) {
                    if (this.x[i][j][k] == 1)
                        System.out.print((k) + " ");
                }
                System.out.print("}\n");
            }
            System.out.println("--------------------------------------------");
        }
        return err;
    }

    private int checkC1() {
        int err = 0;
        for (int i = 0; i < data.D; i++) {
            for (int k = 0; k < data.N; k++) {
                int shift_of_day = 0;
                for (int j = 0; j < 4; j++) {
                    shift_of_day += this.x[i][j][k];
                }
                if (shift_of_day > 1) {
                    log.warning("Worker[" + k + "] do more than 1 shift in day[" + i  + "]");
                    err += 1;
                }
            }
        }
        return err;
    }

    private int checkC2() {
        int err = 0;
        for (int i = 0; i < data.D; i++) {
            for (int k = 0; k < data.N; k++) {
                int shift_of_day = 0;
                for (int j = 0; j < 4; j++) {
                    shift_of_day += this.x[i][j][k];
                }
                if (shift_of_day > 0 && data.dayOff[k].contains(i)) {
                    log.warning("Worker[" + k + "] want day[" + i  + "] off");
                    err += 1;
                }
            }
        }
        return err;
    }

    private int checkC3() {
        int err = 0;
        for (int i = 0; i < data.D; i++) {
            for (int j = 0; j < 4; j++) {
                int total = 0;
                for (int k = 0; k < data.N; k++) {
                    total += this.x[i][j][k];
                }
                if (total < data.alp || total > data.bet) {
                    log.warning("The shift[" + j + "] of day[" + i + "] must have alpha to beta workers");
                    err += 1;
                }
            }
        }
        return err;
    }

    private int checkC4() {
        int err = 0;
        for (int i = 1; i < data.D; i++) {
            for (int k = 0; k < data.N; k++) {
                if (this.x[i - 1][3][k] == 1) {
                    int shift_of_day = 0;
                    for (int j = 0; j < 4; j++) {
                        shift_of_day += this.x[i][j][k];
                    }
                    if (shift_of_day > 0) {
                        log.warning("Worker[" + k + "] worked at night in day[" + (i - 1)  + "] off");
                        err += 1;
                    }
                }
            }
        }
        return err;
    }

    public static int[][][] convertSol(int[] y, Data data) {
        int[][] tmp = new int[data.D][data.N];
        for (int i = 0; i < data.D; i++) {
            for (int k = 0; k < data.N; k++) {
                tmp[i][k] = y[i * data.N + k];
            }
        }
        int[][][] x = new int[data.D][4][data.N];
        for (int i = 0; i < data.D; i++) {
            for (int k = 0; k < data.N; k++) {
                if (tmp[i][k] < 4) {
                    x[i][tmp[i][k] % 4][k] = 1;
                }
            }
        }
        return x;
    }

    private int checkC5() {
        int err = 0;
        for (int k = 0; k < data.N; k++) {
            int sum = 0;
            for (int i = 0; i < data.D; i++) {
                sum += this.x[i][3][k];
            }
            if (sum > s) {
                log.warning("Worker[" + k + "] conflict night!");
            }
        }
        return err;
    }

    public static void main(String[] args) {
        CP cp = new CP("./data/sample.txt");
        cp.run();
        TestCase testCase = new TestCase(TestCase.convertSol(cp.x, cp.cpSolver, cp.data), cp.data);
        testCase.check();
    }
}
