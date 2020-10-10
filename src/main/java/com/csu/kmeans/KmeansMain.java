package com.csu.kmeans;




import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KmeansMain {

    public static void main(String[] args) throws IOException {
        // 读取数据源文件
        //CSVReader reader = new CSVReader(new FileReader("src/main/resources/data.csv")); // 数据源
        FileWriter writer = new FileWriter("src/main/resources/out.txt");
        //List<String[]> myEntries = reader.readAll(); // 6.8, 12.6

        // 转换数据点集
        List<Point> points = new ArrayList<Point>(); // 数据点集
        /*for (String[] entry : myEntries) {
            points.add(new Point(Float.parseFloat(entry[0]), Float.parseFloat(entry[1])));
        }*/
        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            float curX = random.nextFloat()*500;
            float curY = random.nextFloat()*500;
            System.out.println(curX+" "+curY);
            points.add(new Point(curX,curY));
        }

        int k = 6; // K值
        int type = 1;
        KmeansModel model = Kmeans.run(points, k, type);

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

    }

}
