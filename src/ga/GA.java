package ga;

import localsearch.model.VarIntLS;
import test.TestCase;
import utils.Data;

import java.lang.reflect.Array;
import java.util.*;

public class GA {
    List<int[]> pop;
    List<int[]> crossed;
    List<int[]> mutated;
    List<int[]> all;
    Data data;
    int numPop;
    Random rd;
    int[] err;

    public static Fitness calFitness = null;

    static class ArrayIndexComparator implements Comparator<Integer> {
        private Integer[] arr;
        public ArrayIndexComparator(int[] arr) {
            this.arr = new Integer[arr.length];
            for (int i = 0; i < arr.length; i++) {
                this.arr[i] = new Integer(arr[i]);
            }
//            this.arr = arr;
        }
        public Integer[] createIndex() {
            Integer[] index = new Integer[arr.length];
            for (int i = 0; i < arr.length; i++) {
                index[i] = i;
            }
            return index;
        }

        @Override
        public int compare(Integer o1, Integer o2) {
//            Integer i1 = new Integer(Ả)
            return arr[o1].compareTo(arr[o2]);
        }
    }
    public GA (String filename, int numPop) {
        this.data = new Data(filename);
        this.pop = new ArrayList<>(numPop);
        this.crossed = new ArrayList<>(numPop / 2);
        this.mutated = new ArrayList<>(numPop);
        this.numPop = numPop;
        this.rd = new Random();
        this.err = new int[numPop];
        calFitness = new Fitness(data);
    }

    public void init() {
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < numPop; i++) {
            int[] chrome = new int[data.D * data.N];
            for (int j = 0; j < data.D * data.N; j++) chrome[j] = rd.nextInt(5);
            this.pop.add(chrome);
        }
        for (int i = 0; i < pop.size(); i++) {
            int[] chrome = new int[data.D * data.N];
            this.crossed.add(chrome);
        }
        for (int i = 0; i < pop.size(); i++) {
            int[] chrome = new int[data.D * data.N];
            this.mutated.add(chrome);
        }
        for (int i = 0; i < numPop; i++) {
            calFitness.convertSol(pop.get(i));
            err[i] = calFitness.check();
        }
        long t2 = System.currentTimeMillis();
        System.out.println("GA init in " + ((float) (t2 - t1)) / 1000 + " !");
    }

    public int[] cloneChrome(int[] inp) {
        int[] tmp = new int[inp.length];
        for (int i = 0; i < inp.length; i++) tmp[i] = inp[i];
        return tmp;
    }

    public void sortAndCut(ArrayList<int[]> inp) {
        int[] err_all = new int[inp.size()];
        for (int i = 0; i < numPop; i++) {
            err_all[i] = this.err[i];
        }
        for (int i = numPop; i < inp.size(); i++) {
            calFitness.convertSol(inp.get(i));
            err_all[i] = calFitness.check();
        }
        ArrayIndexComparator comparator = new ArrayIndexComparator(err_all);
        Integer[] index = comparator.createIndex();
        Arrays.sort(index, comparator);
        pop.clear();
        for (int i = 0; i < numPop; i++) {
            pop.add(inp.get(index[i]));
            err[i] = err_all[index[i]];
        }
    }

    private int[] crossOver0(int[] tmp1, int[] tmp2) {
        if (rd.nextBoolean()) {
            int[] tmp = tmp2;
            tmp2 = tmp1;
            tmp1 = tmp2;
        }
        int[] res = new int[data.D * data.N];
        int numDay = rd.nextInt(data.D);
        for (int i = 0; i < numDay * data.N; i++) {
            res[i] = tmp1[i];
        }
        for (int i = numDay * data.N; i < data.N * data.D; i++) {
            res[i] = tmp2[i];
        }
        return res;
    }

    private int[] crossOver1(int[] tmp1, int[] tmp2) {
        if (rd.nextBoolean()) {
            int[] tmp = tmp2;
            tmp2 = tmp1;
            tmp1 = tmp2;
        }
        int[] res = new int[data.D * data.N];
        int numWorker = rd.nextInt(data.N);
        for (int i = 0; i < data.D; i++) {
            for (int k = 0; k < numWorker; k++) {
                res[i * data.N + k] = tmp1[i * data.N + k];
            }
        }
        for (int i = 0; i < data.D; i++) {
            for (int k = numWorker; k < data.N; k++) {
                res[i * data.N + k] = tmp2[i * data.N + k];
            }
        }
        return res;
    }

    public int[] mutation_1(int[] tmp) {
        int[] res = new int[data.D * data.N];
        int numDay = rd.nextInt(data.D * data.N);
//        for (int i = 0; i < numDay * data.N; i++) {
//            res[i + (data.D - numDay) * data.N] = tmp[i];
//        }
//        for (int i = numDay * data.N; i < data.N * data.D; i++) {
//            res[i - numDay * data.N] = tmp[i];
//        }
        for (int i = 0; i < data.D * data.N; i++) {
            if (i != numDay) {
                res[i] = tmp[i];
            } else {
                int rd_tmp = rd.nextInt(5);
                while (rd_tmp == tmp[i]) rd_tmp = rd.nextInt(5);
                res[i] = rd_tmp;
            }
        }
        return res;
    }

    public int[] mutation_2(int[] tmp) {
        int[] res = new int[data.D * data.N];
        int numDay1 = rd.nextInt(data.D);
        int numDay2 = rd.nextInt(data.D);

        if (numDay1 > numDay2) {
            int t = numDay1;
            numDay1 = numDay2;
            numDay2 = t;
        }

        for (int i = numDay1 * data.N; i < numDay2 * data.N; i++) {
            res[i] = tmp[i];
        }
        for (int i = 0; i < data.N * numDay1; i++) {
            res[i] = rd.nextInt(5);
        }
        for (int i = data.N * numDay2; i < data.N * data.D; i++) {
            res[i] = rd.nextInt(5);
        }
        return res;
    }

    public void searchPerIterForCrossover(float crossOverProb) {
        crossed.clear();
        for (int pop = 0; pop < numPop / 2; pop++) {
            if (rd.nextFloat() > crossOverProb) {
                continue;
            }
            if (rd.nextBoolean())
                crossed.add(crossOver0(this.pop.get(pop), this.pop.get(pop + numPop / 2)));
            else
                crossed.add(crossOver1(this.pop.get(pop), this.pop.get(pop + numPop / 2)));

        }
        ArrayList<int[]> tmp = new ArrayList<>();
        tmp.addAll(this.pop);
        tmp.addAll(this.crossed);
        this.sortAndCut(tmp);
    }

    public void searchPerIterForMutation(float rateMutation, float mutationProb) {
        mutated.clear();
        for (int pop = 0; pop < numPop; pop++) {
            if (rd.nextFloat() > mutationProb) continue;
            if (rd.nextFloat() < rateMutation) {
                mutated.add(mutation_1(this.pop.get(pop)));
            } else {
                mutated.add(mutation_2(this.pop.get(pop)));
            }
        }
        ArrayList<int[]> tmp = new ArrayList<>();
        tmp.addAll(this.pop);
        tmp.addAll(this.mutated);
        this.sortAndCut(tmp);
    }

    public void search(int maxIter, float mutationProb, float crossOverProb) {
        for (int it = 0; it < maxIter; it++) {
            System.out.println("Iter " + (it + 1) + " with best err = " + err[0]);
            if (err[0] < 1000) {
                break;
            }
//            if (rd.nextFloat())
            this.searchPerIterForCrossover(crossOverProb);
            this.searchPerIterForMutation(0.85f, mutationProb);
        }
    }

    public static void main(String[] args) {
        GA ga = new GA("./data/sample1.txt", 200);
        ga.init();
        long t1 = System.currentTimeMillis();
        ga.search(20000, 0.5F, 0.5F);
        long t2 = System.currentTimeMillis();
        System.out.println("GA Search Time: " + ((float) (t2 - t1) / 1000));
        TestCase testCase = new TestCase(TestCase.convertSol(ga.pop.get(0), ga.data), ga.data);
        testCase.check();
    }
}
