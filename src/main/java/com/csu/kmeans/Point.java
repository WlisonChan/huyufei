package com.csu.kmeans;

public class Point {

    // the reward after completing this task.
    private double reward;

    // The number of times the task was performed
    private int times;

    // the longest distance of current cycle
    private double distance;

    // current location of task
    private Float x;     // x 轴
    private Float y;    // y 轴

    public Point(Float x, Float y) {
        this.x = x;
        this.y = y;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return  x + " " + y;
    }

    /**
     * 计算距离
     *
     * @param centroid 质心点
     * @param type
     * @return
     */
    public Double calculateDistance(Point centroid, int type) {
        // TODO
        Double result = null;
        switch (type) {
            case 1:
                result = calcL1Distance(centroid);
                break;
            case 2:
                result = calcCanberraDistance(centroid);
                break;
            case 3:
                result = calcEuclidianDistance(centroid);
                break;
        }
        return result;
    }



    /*
            计算距离公式
     */

    private Double calcL1Distance(Point centroid) {
        double res = 0;
        res = Math.abs(getX() - centroid.getX()) + Math.abs(getY() - centroid.getY());
        return res / (double) 2;
    }

    private double calcEuclidianDistance(Point centroid) {
        return Math.sqrt(Math.pow((centroid.getX() - getX()), 2) + Math.pow((centroid.getY() - getY()), 2));
    }

    private double calcCanberraDistance(Point centroid) {
        double res = 0;
        res = Math.abs(getX() - centroid.getX()) / (Math.abs(getX()) + Math.abs(centroid.getX()))
                + Math.abs(getY() - centroid.getY()) / (Math.abs(getY()) + Math.abs(centroid.getY()));
        return res / (double) 2;
    }

    @Override
    public boolean equals(Object obj) {
        Point other = (Point) obj;
        if (getX().equals(other.getX()) && getY().equals(other.getY())) {
            return true;
        }
        return false;
    }
}