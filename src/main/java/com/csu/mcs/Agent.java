package com.csu.mcs;

import com.csu.kmeans.Point;

import java.util.HashSet;
import java.util.Set;

public class Agent {

    // agent id
    private int id;
    // Accumulated travel distance
    private double runDistance;
    // Cumulative costs
    private double cost;
    // Accumulated income
    private double pay;
    // complete task set
    private Set<Point> taskSet;

    // current location of participant
    private float x;
    private float y;

    public Agent(float x, float y){
        this.x = x;
        this.y = y;
        this.taskSet = new HashSet<>();
    }

    // The cost of performing the target task
    public double getCost(Point point){
        double d = getDistance(point);
        return Main.COST_COEFFICIENT + Main.DISTANCE_COEFFICIENT * d;
    }

    /**
     * Calculate the distance from the participant to the task
     * @param point
     * @return
     */
    public double getDistance(Point point){
        double d = Math.pow(this.x - point.getX(), 2) + Math.pow(this.y - point.getY(), 2);
        d = Math.sqrt(d);
        return d;
    }

    @Override
    public String toString() {
        return "Agent{" +
                "runDistance=" + runDistance +
                ", cost=" + cost +
                ", pay=" + pay +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    public Set<Point> getTaskSet() {
        return taskSet;
    }

    public void setTaskSet(Set<Point> taskSet) {
        this.taskSet = taskSet;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getRunDistance() {
        return runDistance;
    }

    public void setRunDistance(double runDistance) {
        this.runDistance = runDistance;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getPay() {
        return pay;
    }

    public void setPay(double pay) {
        this.pay = pay;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
