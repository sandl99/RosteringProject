package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
            if (sc.hasNext())
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

    public static void writeToFile(String saveDir, int N, int D, int alpha, int beta, List<Integer>[] dayOff){
        try {
            File file = new File(saveDir);
            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(saveDir, false));

            writer.write(N + " " + D + " " + alpha + " " + beta);
            for (int i = 0; i < N; i++){
                writer.newLine();
                for(int j = 0; j<dayOff[i].size(); ++j){
                    writer.write(dayOff[i].get(j) + " ");
                }
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void writeToFile(String saveDir, int N, int D, int alpha, int beta){
        try {
            File file = new File(saveDir);
            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(saveDir, false));

            writer.write(N + " " + D + " " + alpha + " " + beta);
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static Data generateTotallyRandomData(long seed, String saveDir, boolean hasDayOff){
        Random rd = new Random();
        int N = rd.nextInt(100) + 1;
        int D = rd.nextInt(100) + 1;
        int alpha = rd.nextInt((int) N/5) + 1;
        int beta = rd.nextInt(N-alpha + 1) + alpha;
        List<Integer>[] dayOff ;

        if (hasDayOff)
        {
            dayOff = new ArrayList[N];
            for (int i = 0; i < N; i++) {
                int dayOffLength_i = rd.nextInt(D/10) + 1;
                dayOff[i] = new ArrayList<>();
//                if (dayOffLength_i ==0)
//                {
//                    dayOff[i].add(0);
//                }
//                else
                for (int j = 0; j < dayOffLength_i; ++j) {
                    dayOff[i].add(rd.nextInt(D));
                }
            }
            writeToFile(saveDir, N, D, alpha, beta, dayOff);
        }
        else writeToFile(saveDir, N, D, alpha, beta);

        return new Data(saveDir);
    }

    public static void main(String[] args) {
//        Data data = new Data("./data/sample.txt");
//        System.out.println(data.toString());

        Data data1 = generateTotallyRandomData(1, "./data/sample1.txt", true);
        System.out.println(data1.toString());
    }
}
