package com.csu;

import com.csu.kmeans.Point;
import com.csu.mcs.Agent;
import com.csu.mcs.GeneticAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.csu.mcs.Main.*;

@Slf4j
public class Test {

    public static void main(String[] args) throws IOException {

        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.clear();
        Integer integer = list.get(0);
        System.out.println(integer);
/*        for (int i = 0; i < 1; i++) {
            // 任务初始化，聚类
            List<Point> points = kMeansForTasks();
            // 初始化参与者坐标
            List<Agent> agents = initAgents();
            //baselineAlgorithm(points, agents);
            System.out.println("参与者id 累计移动距离 移动距离 成本 奖励");
            GeneticAlgorithm.init(points,agents);
        }*/

        // get graph
        /*double k = 1.0;
        double u = 0;
        for (double x = 0.05;  x <= 10.1; x+=0.05) {
            double power = -1.0/(2*k*k)*(Math.pow(Math.log(x-1.0)-u,2));
            double y = 1.0/(Math.sqrt(2*Math.PI)*k) * Math.pow(Math.E,power);
            System.out.printf("%.2f %.6f",x,y);
            System.out.println();
        }*/
        //System.out.println(Math.log(Math.E));
    }
    public static void test(Double d){
         d = Double.sum(d,10);
    }
}

