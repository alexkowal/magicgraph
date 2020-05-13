package utils;

import com.google.common.collect.Lists;
import domain.Graph;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

public class GraphComposer {

    Map<Integer, List<String>> edgesToGraphNameMapping = new HashMap<>();


    public void checkEdgesCount(Graph graph) {
        if (!edgesToGraphNameMapping.containsKey(graph.getEdges().size())) {
            edgesToGraphNameMapping.put(graph.getEdges().size(), Lists.newArrayList());
        }
        edgesToGraphNameMapping.get(graph.getEdges().size()).add(graph.getName());
    }


    public void writeGraphsToFiles() {
        edgesToGraphNameMapping.entrySet().stream()
                .forEach(entry -> {
                    BufferedWriter bw;
                    try {
                        bw = new BufferedWriter(new FileWriter(entry.getKey() + ".txt"));
                        entry.getValue().forEach(val -> {
                            try {
                                bw.write(val);
                                bw.newLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        bw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                });

    }

    public void v() throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("/Users/aleksandr/Desktop/graph/9.txt"))) {
            String graph6String;
            Long l = System.currentTimeMillis();

            while (nonNull(graph6String = bufferedReader.readLine())) {
                Graph g = Graph6Reader.readGraph6Format(graph6String);
                checkEdgesCount(g);
            }
            System.out.println(System.currentTimeMillis() - l);
            writeGraphsToFiles();
        }

    }

    public static void main(String[] args) throws IOException {
        GraphComposer composer = new GraphComposer();
        composer.v();
    }
}
