package com.csu.mcs;

import com.csu.kmeans.Cluster;
import com.csu.kmeans.Kmeans;
import com.csu.kmeans.KmeansModel;
import com.csu.kmeans.Point;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Main {

    //parameter setting
    // x size for map
    public static final int X_SIZE = 500;
    // y size for map
    public static final int Y_SIZE = 500;
    // the number of tasks.
    public static final int TASK_NUM = 50;
    // the number of workers.
    public static final int WORKER_NUM = 10;
    // cost coefficient - ai
    public static final double COST_COEFFICIENT = 1.0;
    // distance coefficient - bi
    public static final double DISTANCE_COEFFICIENT = 1.0;
    // budget
    public static final double BUDGET = 50000;
    // the upper limit of moving.
    public static final double MOVE_LIMIT = 1000;
    // the reward after completing task
    public static final double REWARD = 100;
    // cycle
    public static final double CYCLE = 200;
    // 控制边际增长递减效应的一个因素
    public static final double LAMBDA = 0.2;

    // kmeans - k
    public static final int KMEANS_K = 5;
    // kmeans - type, the calculation distance formula type.
    public static final int KMEANS_TYPE = 1;

    // GeneticAlgorithm parameter
    // Initial population number
    public static final int INIT_NUM = 1000;

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 20; i++) {
            // 任务初始化，聚类
            List<Point> points = kMeansForTasks();
            // 初始化参与者坐标
            List<Agent> agents = initAgents();
            //baselineAlgorithm(points, agents);
            System.out.println();
            geneticAlgorithm(points,agents);
        }
    }

    /**
     * 均匀生成任务，并且使用kmeans聚类
     *
     * @return
     * @throws IOException
     */
    public static List<Point> kMeansForTasks() throws IOException {
        //数据输出路径
        FileWriter writer = new FileWriter("src/main/resources/out.txt");
        List<Point> points = new ArrayList<>(); // 任务点集

        Random random = new Random();
        for (int i = 0; i < TASK_NUM; i++) {
            float curX = random.nextFloat() * X_SIZE;
            float curY = random.nextFloat() * Y_SIZE;
            //System.out.println(curX+" "+curY);
            points.add(new Point(curX, curY));
        }
        KmeansModel model = Kmeans.run(points, KMEANS_K, KMEANS_TYPE);
        // 将任务聚类
        writer.write("====================   K is " + model.getK() + " ,  Object Funcion Value is " + model.getOfv() + " ,  calc_distance_type is " + model.getCalc_distance_type() + "   ====================\n");
        int i = 0;
        for (Cluster cluster : model.getClusters()) {
            i++;
            writer.write("====================   classification " + i + "   ====================\n");
            for (Point point : cluster.getPoints()) {
                writer.write(point.toString() + "\n");
            }
            writer.write("\n");
            writer.write("centroid is " + cluster.getCentroid().toString());
            writer.write("\n\n");
        }
        writer.close();
        return points;
    }

    /**
     * Initialize participants' location
     *
     * @return
     */
    public static List<Agent> initAgents() {
        // 参与者位置初始化
        List<Agent> agents = new ArrayList<>(WORKER_NUM);
        Random random = new Random();
        for (int i = 0; i < WORKER_NUM; i++) {
            float curX = random.nextFloat() * X_SIZE;
            float curY = random.nextFloat() * Y_SIZE;
            Agent agent = new Agent(curX, curY);
            agent.setId(i);
            agents.add(agent);
        }

        return agents;
    }

    /**
     * Calculate the average profit
     *
     * @param agents
     * @return
     */
    public static double calAvgProfit(List<Agent> agents) {
        double totalIncome = agents.stream().mapToDouble(e -> e.getPay()).sum();
        double totalCost = agents.stream().mapToDouble(e -> e.getCost()).sum();
        double totalProfit = totalIncome - totalCost;
        return totalProfit / agents.size();
    }

    /**
     * Calculate the reward if the participant finish the task
     * @param agent
     * @param point
     * @param cycle
     * @param reward
     * @return
     */
    public static double expectedBenefits(Agent agent,Point point,double cycle,double reward){
        if (point.getDistance() == 0
                || point.getDistance()/cycle == (agent.getRunDistance()+agent.getDistance(point))/cycle){
            return point.getReward();
        }
        double newReward = reward * Math.pow(Math.E, - LAMBDA * point.getTimes());
        return newReward;
    }

    /**
     * Baseline algorithm
     *
     * @param tasks
     * @param agents
     */
    public static void baselineAlgorithm(List<Point> tasks, List<Agent> agents) {
        // copy data to run
        List<Point> taskList = new ArrayList<>(tasks);
        List<Agent> agentList = new ArrayList<>(agents);

        // parameter init
        double budget = BUDGET;
        double moveLimit = MOVE_LIMIT;
        double cycle = CYCLE;
        double reward = REWARD;

        // init taskList reward
        for (int i = 0; i < taskList.size(); i++) {
            Point point = taskList.get(i);
            point.setReward(reward);
        }

        // complete task
        int completeTask = -1;
        int totalCompleteTask = 0;
        for (double i = cycle; completeTask != 0; i += cycle) {
            //System.out.println(" total cycle : "+i);
            completeTask = 0;
            for (int j = 0; j < agentList.size(); j++) {
                Agent agent = agentList.get(j);
                Point targetTask = null;
                double maxVal = 0;
                // find the max profit task to execute.
                for (int k = 0; k < taskList.size(); k++) {
                    //Calculate the distance from the participant to the task
                    Point task = taskList.get(k);
                    // agent only execute the task once.
                    if (agent.getTaskSet().contains(task)) {
                        continue;
                    }
                    double d = agent.getDistance(task);
                    double cost = agent.getCost(task);
                    double expectedReward = expectedBenefits(agent,task,cycle,reward);
                    //System.out.println(expectedReward);
                    if (d <= cycle && d <= moveLimit - agent.getRunDistance()
                            && expectedReward - cost > maxVal
                            && budget >= expectedReward) {
                        maxVal = expectedReward - cost;
                        targetTask = task;
                    }
                }
                // participant execute the target task.
                if (targetTask != null) {
                    // current reward of task
                    double curReward = 0;
                    if (targetTask.getDistance() == 0.0){
                        curReward = targetTask.getReward();
                    }else {
                        double distance = targetTask.getDistance();
                        // judge
                        if (distance/cycle == (agent.getRunDistance()+agent.getDistance(targetTask))/cycle){
                            curReward = targetTask.getReward();
                        }else {
                            double newReward = reward * Math.pow(Math.E, -LAMBDA * targetTask.getTimes());
                            curReward = newReward;
                            // refresh reward of task
                            targetTask.setReward(newReward);
                        }
                    }
                    //System.out.println(curReward);
                    double totalCost = agent.getCost(targetTask) + agent.getCost();
                    double totalDistance = agent.getDistance(targetTask) + agent.getRunDistance();
                    double totalPay = curReward + agent.getPay();
                    agent.setCost(totalCost);
                    agent.setRunDistance(totalDistance);
                    agent.setPay(totalPay);
                    // refresh budget
                    budget-=curReward;
                    // refresh participant's location
                    agent.setX(targetTask.getX());
                    agent.setY(targetTask.getY());
                    agent.getTaskSet().add(targetTask);
                    // refresh task's reward and times
                    targetTask.setTimes(targetTask.getTimes() + 1);
                    /*double newReward = reward * Math.pow(Math.E, -LAMBDA * targetTask.getTimes());
                    targetTask.setReward(newReward);*/
                    // refresh task distance
                    targetTask.setDistance(agent.getRunDistance());
                    //System.out.println(agent.getId() + " finish the task. TotalCost:" + totalCost + " totalDistance:"+totalDistance +" totalPay:"+totalPay);

                    completeTask++;
                    totalCompleteTask++;
                }
            }
        }
        double avgProfit = calAvgProfit(agentList);
        System.out.println(avgProfit);
        System.out.println("the least budget :" +budget);
    }

    /**
     * Detective Algorithm
     * @param tasks
     * @param agents
     */
    public static void detectiveAlgorithm(List<Point> tasks, List<Agent> agents) {
        // copy data to run
        List<Point> taskList = new ArrayList<>(tasks);
        List<Agent> agentList = new ArrayList<>(agents);

        // parameter init
        double budget = BUDGET;
        double moveLimit = MOVE_LIMIT;
        double cycle = CYCLE;
        double reward = REWARD;

        // init taskList reward
        for (int i = 0; i < taskList.size(); i++) {
            Point point = taskList.get(i);
            point.setReward(reward);
        }

        // complete task
        int completeTask = -1;
        for (double k = cycle;completeTask !=0  ; k+=cycle) {
            completeTask = 0;
            for (int i = 0; i < agentList.size(); i++) {
                Agent agent = agentList.get(i);
                double maxVal = Double.MIN_VALUE;
                Point targetTask = null;
                for (int j = 0; j < taskList.size(); j++) {
                    Point task = taskList.get(j);
                    if (agent.getTaskSet().contains(task)){
                        continue;
                    }
                    double d = agent.getDistance(task);
                    double cost = agent.getCost(task);
                    double expectedReward = expectedBenefits(agent,task,cycle,reward);
                    //System.out.println(expectedReward);
                    if (d <= cycle && d <= moveLimit - agent.getRunDistance()
                            && expectedReward - cost > maxVal
                            && budget >= expectedReward) {
                        maxVal = expectedReward - cost;
                        targetTask = task;
                    }
                }
            }

        }
    }

    /**
     * GeneticAlgorithm
     * @param tasks
     * @param agents
     */
    public static void geneticAlgorithm(List<Point> tasks, List<Agent> agents){
        // copy data to run
        List<Point> taskList = new ArrayList<>(tasks);
        List<Agent> agentList = new ArrayList<>(agents);

        // parameter init
        double budget = BUDGET;
        double moveLimit = MOVE_LIMIT;
        double cycle = CYCLE;
        double reward = REWARD;

        // init taskList reward
        for (int i = 0; i < taskList.size(); i++) {
            Point point = taskList.get(i);
            point.setReward(reward);
        }

        // init 1000 route
        Random random = new Random();
        for (int i = 0; i < INIT_NUM; i++) {
            Agent agent = agentList.get(i % WORKER_NUM);
            // route plan
            List<Point> temp = new ArrayList<>();
            for (int j = 0; j < taskList.size(); j++) {
                if (random.nextBoolean()) {
                    continue;
                }
                temp.add(taskList.get(j));
            }
            // shuffle the list
            Collections.shuffle(temp);
            agent.getTaskSeq().add(temp);
        }
        handleRestrain(agentList,moveLimit,budget);
        partialMappedCrossover(agents,moveLimit);
    }

    /**
     * handleRestrain
     * @param agents
     * @param moveLimit
     */
    public static void handleRestrain(List<Agent> agents,double moveLimit,double budget){
        for (int i = 0; i < agents.size(); i++) {
            Agent agent = agents.get(i);
            List<List<Point>> taskSeq = agent.getTaskSeq();
            for (int j = 0; j < taskSeq.size(); j++) {
                List<Point> route = taskSeq.get(j);
                agent.getTaskSeq().set(j,routeTailHandle(agent,route,moveLimit,budget));
            }
        }
    }

    public static List<Point> routeTailHandle(Agent agent,List<Point> route,double moveLimit,double budget){
        double runDistance = 0;
        for (int i = 0; i < route.size(); i++) {
            Point task = route.get(i);
            double d = agent.getDistance(task);
            if (runDistance+d<moveLimit){
                runDistance+=d;
            }else {
                return route.subList(0,i);
            }
        }
        return route;
    }

    public static void partialMappedCrossover(List<Agent> agents,double moveLimit){
        for (int i = 0; i < agents.size(); i++) {
            Agent agent = agents.get(i);
            List<List<Point>> taskSeq = agent.getTaskSeq();
            for (int j = 0; j < taskSeq.size(); j++) {
                for (int k = j+1; k < taskSeq.size(); k++) {
                    // crossover
                    List<Point> child = crossover(taskSeq.get(j), taskSeq.get(k));
                    child = routeTailHandle(agent, child, moveLimit,BUDGET);
                    agent.getTaskSeq().add(child);
                }
            }
        }
    }

    public static List<Point> crossover(List<Point> taskPar, List<Point> taskPar2){
        Random random = new Random();
        List<Point> res = new ArrayList<>();
        int size = taskPar.size() > taskPar2.size() ? taskPar2.size() : taskPar.size();
        for (int i = 0; i < size; i++) {
            if (random.nextBoolean()) {
                res.add(taskPar.get(i));
            }else {
                res.add(taskPar2.get(i));
            }
        }
        return res;
    }

    public static void screenMaxVal(List<Agent> agents){

    }

}
