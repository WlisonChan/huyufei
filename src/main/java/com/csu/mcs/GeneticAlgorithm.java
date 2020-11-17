package com.csu.mcs;

import com.csu.kmeans.Point;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class GeneticAlgorithm {
    // init group size
    public final static int INIT_NUM = 1000;
    // init group main number
    public final static int MAIN_PARTICIPANT = 0;

    public static List<List<List<Point>>> allRoute;

    /**
     * init GA and run
     *
     * @param tasks
     * @param agents
     */
    public static void init(List<Point> tasks, List<Agent> agents) {
        // copy data to run
        List<Point> taskList = new ArrayList<>(tasks);
        List<Agent> agentList = new ArrayList<>(agents);

        // parameter init
        double budget = Main.BUDGET;
        double moveLimit = Main.MOVE_LIMIT;
        double cycle = Main.CYCLE;
        double reward = Main.REWARD;

        // init taskList reward
        for (int i = 0; i < taskList.size(); i++) {
            Point point = taskList.get(i);
            point.setReward(reward);
        }

        // init 1000 route
        Random random = new Random();
        for (int i = 0; i < INIT_NUM; i++) {
            Agent agent = agentList.get(MAIN_PARTICIPANT);
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
/*            Collections.sort(temp, new Comparator<Point>() {
                @Override
                public int compare(Point o1, Point o2) {
                    return agent.getDistance(o1) > agent.getDistance(o2) ? 1 : -1;
                }
            });*/
            agent.getTaskSeq().add(temp);
        }
        // init other agents' route
        for (int i = 0; i < Main.WORKER_NUM; i++) {
            if (i == MAIN_PARTICIPANT) {
                continue;
            }
            Agent agent = agentList.get(i);
            List<Point> temp = new ArrayList<>();
            for (int j = 0; j < taskList.size(); j++) {
                if (random.nextBoolean()) {
                    continue;
                }
                temp.add(taskList.get(j));
            }
            // shuffle the list
            Collections.shuffle(temp);
/*            Collections.sort(temp, new Comparator<Point>() {
                @Override
                public int compare(Point o1, Point o2) {
                    return agent.getDistance(o1) > agent.getDistance(o2) ? 1 : -1;
                }
            });*/
            agent.getTaskSeq().add(temp);
        }

/*        int[] preSize = new int[Main.WORKER_NUM];
        for (int i = 0; i < preSize.length; i++) {
            preSize[i] = agentList.get(i).getTaskSeq().size();
        }
        System.out.println(Arrays.toString(preSize));
        for (int i = 0; i < agentList.size(); i++) {
            Agent agentA = agentList.get(i);
            for (int j = i+1; j < agentList.size(); j++) {
                Agent agentB = agentList.get(j);
                generateChild(agentA,agentB, preSize[i],preSize[j]);
            }
        }*/

        // init task information
        initTaskVal(taskList,Main.REWARD);
        prune(agentList, moveLimit);
        allRoute = new ArrayList<>();
        getRoute(agentList, 0, new ArrayList<>());
        log.info("all route size : " + allRoute.size());

        for (int i = 0; i < allRoute.size(); i++) {
            List<List<Point>> lists = allRoute.get(i);
            for (int j = 0; j < lists.size(); j++) {
                //System.out.println(lists.get(j).size());
            }
        }
        
        log.info(" crossover: ");
        for (int i = 0; i < allRoute.size(); i++) {
            initTaskVal(taskList,reward);
            List<List<Point>> cur = allRoute.get(i);
            boolean flag = budgetRestrain(agentList, cur, budget);
            if (!flag){
                allRoute.remove(i);
                i--;
            }
        }
        log.info("all route size : " + allRoute.size());
    }

    /**
     * The method will return true if the route can be finished under the budget
     * @param agents
     * @param task
     * @param budget
     * @return
     */
    public static boolean budgetRestrain(List<Agent> agents, List<List<Point>> task, double budget) {
        boolean flag = true;
        for (int j = 0; budget>=0 && flag; j++) {
            flag = false;
            for (int i = 0; i < Main.WORKER_NUM; i++) {
                Agent agent = agents.get(i);
                List<Point> points = task.get(i);
                if (j<points.size()) {
                    Point target = points.get(j);
                    double preReward = target.getReward();
                    boolean res = execute(agent, target);
                    if (res) {
                        budget -= preReward;
                        flag = flag || res;
                    }else{
                        return false;
                    }
                }
            }
            //System.out.println(budget);
        }
        return budget < 0 ? false : true;
    }

    /**
     * The agent execute the target task and refresh parameter
     * @param agent
     * @param task
     * @return
     */
    public static boolean execute(Agent agent, Point task) {
        double cost = agent.getCost(task);
        if (cost > task.getReward()) {
            return false;
        }
        //System.out.println(agent.getId()+" complete "+cost);

        // refresh reward
        double newReward = Main.REWARD * Math.pow(Math.E, -Main.LAMBDA * task.getTimes());
        task.setReward(newReward);
        task.setTimes(task.getTimes() + 1);

        // refresh agent location
        agent.setX(task.getX());
        agent.setY(task.getY());
        return true;
    }

    /**
     * Get all array
     * @param agents
     * @param index
     * @param task
     */
    public static void getRoute(List<Agent> agents, int index, List<List<Point>> task) {
        if (index == Main.WORKER_NUM) {
            List<List<Point>> temp = new ArrayList<>(task);
            allRoute.add(temp);
            return;
        }
        Agent agent = agents.get(index);
        List<List<Point>> taskSeq = agent.getTaskSeq();
        for (int i = 0; i < taskSeq.size(); i++) {
            task.add(taskSeq.get(i));
            getRoute(agents, index + 1, task);
            task.remove(task.size() - 1);
        }
    }

    /**
     * prune the tail of route
     *
     * @param agents
     * @param moveLimit
     */
    public static void prune(List<Agent> agents, double moveLimit) {
        for (int i = 0; i < agents.size(); i++) {
            Agent agent = agents.get(i);
            List<List<Point>> taskSeq = agent.getTaskSeq();
            for (int j = 0; j < taskSeq.size(); j++) {
                List<Point> route = taskSeq.get(j);
                Agent copy = new Agent(agent.getX(),agent.getY());
                copy.setId(agent.getId());
                agent.getTaskSeq().set(j, routeTailHandle(copy, route, moveLimit));
            }
        }
    }

    /**
     * handle the route under the restrain
     *
     * @param agent
     * @param route
     * @param moveLimit
     * @return
     */
    public static List<Point> routeTailHandle(Agent agent, List<Point> route, double moveLimit) {
        double runDistance = 0;
        for (int i = 0; i < route.size(); i++) {
            Point task = route.get(i);
            double d = agent.getDistance(task);
            double cost = agent.getCost(task);
            System.out.printf("%d   %.2f   %.2f   %.2f   %.2f",agent.getId(),runDistance,d,cost,task.getReward());
            System.out.println();
            if (runDistance + d <= moveLimit && cost <= task.getReward()) {
                runDistance += d;
                // refresh agent's location
                agent.setX(task.getX());
                agent.setY(task.getY());
            } else {
                return route.subList(0, i);
            }
        }
        System.out.println();
        return route;
    }

    /**
     * get child of route between A and B
     *
     * @param agentA
     * @param agentB
     */
    public static void generateChild(Agent agentA, Agent agentB,int sizeA,int sizeB) {
        List<List<Point>> taskSeqA = agentA.getTaskSeq();
        List<List<Point>> taskSeqB = agentB.getTaskSeq();
        //log.debug(" task crossover begin ");
        for (int i = 0; i < sizeA; i++) {
            List<Point> taskA = taskSeqA.get(i);
            for (int j = 0; j < sizeB; j++) {
                List<Point> taskB = taskSeqB.get(j);
                crossover(agentA, agentB, taskA, taskB);
            }
        }
        //log.debug(" task crossover end");
    }

    /**
     * crossover between A and B
     *
     * @param agentA
     * @param agentB
     * @param taskA
     * @param taskB
     */
    public static void crossover(Agent agentA, Agent agentB, List<Point> taskA, List<Point> taskB) {
        List<Point> taskSeqAChild = new ArrayList<>();
        List<Point> taskSeqBChild = new ArrayList<>();
        int size = taskA.size() > taskB.size() ? taskB.size() : taskA.size();
        Random random = new Random();
        int i = 0;
        Point tempA;
        Point tempB;
        //log.debug(" task crossover begin between "+agentA.getId()+" "+ agentB.getId());
        for (; i < size && (tempA = taskA.get(i)) != null && (tempB = taskB.get(i)) != null; i++) {
            if (random.nextBoolean()) {
                taskSeqAChild.add(tempB);
                taskSeqBChild.add(tempA);
            } else {
                taskSeqAChild.add(tempA);
                taskSeqBChild.add(tempB);
            }
        }
        //log.debug(" a begin");
        while (i < taskA.size() && (tempA = taskA.get(i)) != null) {
            taskSeqAChild.add(tempA);
            taskSeqBChild.add(tempA);
            i++;
        }
        //log.debug(" b begin ");
        while (i < taskB.size() && (tempB = taskB.get(i)) != null) {
            taskSeqAChild.add(tempB);
            taskSeqBChild.add(tempB);
            i++;
        }
        //log.debug(" end ");
        agentA.getTaskSeq().add(taskSeqAChild);
        agentB.getTaskSeq().add(taskSeqBChild);
    }

    /**
     * init task's reward
     *
     * @param task
     * @param reward
     */
    public static void initTaskVal(List<Point> task, double reward) {
        for (int i = 0; i < task.size(); i++) {
            Point point = task.get(i);
            point.setReward(reward);
            point.setTimes(0);
        }
    }
}
