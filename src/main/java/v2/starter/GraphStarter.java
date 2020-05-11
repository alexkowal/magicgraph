package v2.starter;

import utils.GraphTask;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.nonNull;

public class GraphStarter {
    static volatile AtomicInteger totalCount = new AtomicInteger(0);

    public static void main(String[] args) throws Exception {

        ThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(7);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("/Users/aleksandr/magicgraph/src/main/java/files/graph6.txt"))) {
            String graph6String;
            Long l = System.currentTimeMillis();
            while (nonNull(graph6String = bufferedReader.readLine())) {
                GraphTask task = new GraphTask(graph6String, null, GraphStarter.totalCount);
                executor.submit(task);
            }
            executor.shutdown();
            System.out.println(System.currentTimeMillis() - l);

        }

    }
}
