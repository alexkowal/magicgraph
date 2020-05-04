package v2.starter;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import domain.Graph;
import domain.ResearchResult;
import starter.Task;
import utils.Graph6Reader;
import utils.GraphTask;
import utils.GraphUtilsV2;
import utils.GraphUtilsV3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class GraphStarter {


    public static void main(String[] args) throws Exception {

        ThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(8);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("/Users/aleksandr/magicgraph/src/main/java/files/graph6.txt"))) {
            String graph6String;
            Long l = System.currentTimeMillis();

            while (nonNull(graph6String = bufferedReader.readLine())) {
                GraphTask task = new GraphTask(graph6String,null);
                executor.submit(task);
            }
            executor.shutdown();
            System.out.println(System.currentTimeMillis() - l);

        }

    }
}
