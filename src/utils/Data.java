package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Data {
    public int D, N, alp, bet;

    public Data(String filename) {
        try {
            Scanner sc = new Scanner(new File(filename));
            this.N = sc.nextInt();
            this.D = sc.nextInt();
            this.alp = sc.nextInt();
            this.bet = sc.nextInt();
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
        Data data = new Data("./data/sample.txt");
        System.out.println(data.toString());
    }
}