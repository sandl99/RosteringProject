package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Data {
    public int D, N, alp, bet;
    public List<Integer>[] dayOff;

    public Data(String filename) {
        try {
            Scanner sc = new Scanner(new File(filename));
            this.N = sc.nextInt();
            this.D = sc.nextInt();
            this.alp = sc.nextInt();
            this.bet = sc.nextInt();
            sc.nextLine();
            if (sc.hasNext()) {
                this.dayOff = new ArrayList[N];
                for (int i = 0; i < N; i++) {
                    dayOff[i] = new ArrayList<>();
                    String line = sc.nextLine();
                    String[] l = line.trim().split(" ");
                    for (String s: l) {
                        dayOff[i].add(Integer.valueOf(s));
                    }
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "N = " + this.N + "; D = " + this.D + "; alpha = " + this.alp + "; beta = " + this.bet;
    }

    public static void main(String[] args) {
        Data data = new Data("./data/sample1.txt");
        System.out.println(data.toString());
    }
}
