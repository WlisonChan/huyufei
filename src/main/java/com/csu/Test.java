package com.csu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Test {
    public static void main(String[] args) {
        Test test = new Test();
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);
        list.add(8);
        list.add(9);
        System.out.println(list.subList(0,1));
       /* System.out.println(list);
        Collections.shuffle(list);
        System.out.println(list);*/
        list.subList(0, 1);
        System.out.println(list);
        List<Integer> temp = new ArrayList<>();
        temp.add(10);
        temp.add(11);
        temp.add(12);
        temp.add(13);
        temp.add(14);
        temp.add(15);
        temp.add(16);
        int size = list.size() > temp.size() ? temp.size() :list.size();
        List<Integer> cur = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            if (random.nextBoolean()){
                cur.add(list.get(i));
            }else {
                cur.add(temp.get(i));
            }
        }
        System.out.println(cur);
    }
}
