import com.csu.kmeans.Point;
import com.csu.mcs.Agent;
import com.csu.mcs.GeneticAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

import static com.csu.mcs.Main.initAgents;
import static com.csu.mcs.Main.kMeansForTasks;

@Slf4j
public class Test {
    @org.junit.jupiter.api.Test
    public void test() throws IOException {
        List<Point> points = kMeansForTasks();
        // 初始化参与者坐标
        List<Agent> agents = initAgents();
        //baselineAlgorithm(points, agents);
        System.out.println();
        GeneticAlgorithm.init(points,agents);
        log.info("insakls");
    }
}
