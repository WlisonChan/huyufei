package com.csu;

import java.util.Random;
import java.util.Scanner;

public class Main {
    
    public static double a = 0.2;
    public static double b = 0.3;
    public static double r = 1-a-b;
    public static double cmax = 3;
    
    public static void main(String[] args) {
        
        double j1 = 0;
        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            int cj = 10;//random.nextInt(29)+1;
            double cur = Math.pow(r*cmax +a*cj*cj +b*j1*j1, 0.5);
            j1 = cur;
            System.out.println((i+1)+"\t "+ j1);
        }
    }
}
