package utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import domain.Edge;
import domain.Graph;
import domain.ResearchResult;
import domain.Vertex;
import org.apache.commons.collections4.iterators.PermutationIterator;
import org.paukov.combinatorics3.Generator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GraphUtilsV4 {
    public static void generateAndCheck(Graph graph, int currentVertexNum, List<Integer> possibleValues,
                                        ResearchResult found) throws Exception {
        if (found.getResult())
            return;
        for (int i = currentVertexNum; i < graph.getVertices().size(); i++) {
            graph.getVertices().get(i).getEdges().stream()
                    .forEach(edge -> edge.setMarked(false));
        }

        for (int i = 0; i < currentVertexNum; i++) {
            graph.getVertices().get(i).getEdges().stream()
                    .forEach(edge -> edge.setMarked(true));
        }

        Vertex vertex = graph.getVertices().get(currentVertexNum);
        List<Edge> unmarkedEdges = vertex.getEdges()
                .stream()
                .filter(edge -> !edge.isMarked())
                .collect(Collectors.toList());

        Integer recalculatedMagicNumber = graph.getMagicNumber() - calculateCurrentSumForVertex(vertex);

        Integer permutationSize = unmarkedEdges.size();

        List<List<Integer>> permutations = generatePerms(possibleValues, permutationSize, recalculatedMagicNumber);
        Collections.sort(possibleValues);
        PermutationIterator<Integer> iterator = new PermutationIterator<>(possibleValues);

        vertex.setPermutations(permutations);

        while (iterator.hasNext() && !found.getResult()) {
            List<Integer> permutation = iterator.next();
            if (checkSum(permutation, recalculatedMagicNumber)) {

                List<Integer> copyOfPossibleValues = Lists.newArrayList(possibleValues);
                copyOfPossibleValues = copyOfPossibleValues.stream()
                        .filter(value -> !permutation.contains(value))
                        .collect(Collectors.toList());

                for (int j = 0; j < permutation.size(); j++) {
                    Edge edge = unmarkedEdges.get(j);
                    edge.setMarked(true);
                    edge.setWeight(permutation.get(j));
                }

                if (checkGraphIsMagic(graph)) {
                    found.setResult(true);
                    found.setEdgeList(graph.getEdges());
                    BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/aleksandr/magicgraph/src/main/java/files/" + graph.getName()));
                    bw.write(graph.getName());
                    bw.newLine();
                    bw.write(String.valueOf(graph.getEdges()));
                    bw.close();
                    System.out.println(graph);
                }
                if (found.getResult())
                    break;
                generateAndCheck(graph, currentVertexNum + 1, copyOfPossibleValues, found);
            }
        }
    }

    private static boolean checkAllEdgesMarked(Graph graph) {
        List<Edge> unmarkedEdges = graph.getEdges()
                .stream()
                .filter(edge -> edge.isMarked())
                .collect(Collectors.toList());

        return unmarkedEdges.isEmpty();

    }

    private static Integer calculateCurrentSumForVertex(Vertex v) {
        int sum = 0;
        for (Edge edge : v.getEdges()) {
            if (edge.isMarked()) {
                sum += edge.getWeight();
            }
        }
        return sum;
    }

    private static Integer calculateSeriesSum(Integer to) {
        Integer sum = 0;
        for (int i = 1; i <= to; i++) {
            sum += i;

        }
        return sum;
    }

    private static List<List<Integer>> generatePerms(List<Integer> values, int size, int sum) {
        List<List<Integer>> result = Lists.newArrayList();
        Generator.combination(values)
                .simple(size)
                .stream()
                .filter(GraphUtilsV4::checkValuesUnique)
                .filter(integers -> checkSum(integers, sum))
                .forEach(combination -> Generator.permutation(combination)
                        .simple()
                        .forEach(result::add));
        return result;
    }

    public static List<List<Integer>> generatePermsv(List<Integer> values, int size, int sum) {
        List<List<Integer>> result = Lists.newArrayList();
        Generator.subset(values)
                .simple()
                .stream()
                .filter(GraphUtilsV4::checkValuesUnique)
                .filter(integers -> checkSum(integers, sum))
                .collect(Collectors.toList());
        return result;
    }


    private static boolean checkValuesUnique(List<Integer> v) {
        return v.size() == Sets.newHashSet(v).size();
    }

    private static boolean checkSum(List<Integer> v, Integer target) {
        int s = 0;
        for (Integer integer : v) {
            s += integer;
        }
        return s == target;
    }

    private static boolean checkGraphIsMagic(Graph graph) throws Exception {
        if (!allEdgesDifferent(graph))
            return false;

        Integer commonSum = calculateSumForVertex(graph, 0);
        if (!commonSum.equals(graph.getMagicNumber())) {
            return false;
        }
        for (Vertex v : graph.getVertices()) {
            if (!calculateSumForVertex(graph, v.getNum()).equals(commonSum))
                return false;
        }
        return true;
    }

    private static boolean allEdgesDifferent(Graph g) {
        int count = g.getEdges().size();
        List<Integer> digits = Lists.newArrayList();
        for (int i = 0; i < count; i++) {
            digits.add(i + 1);
        }

        List<Integer> weights = Lists.newArrayList();
        for (Edge edge : g.getEdges()) {
            weights.add(edge.getWeight());
        }
        Collections.sort(weights);
        return weights.equals(digits);
    }

    private static Integer calculateSumForVertex(Graph graph, Integer vertexNum) throws Exception {
        List<Vertex> vertexFoundByNum = graph.getVertices()
                .stream()
                .filter(vertex -> vertex.getNum().equals(vertexNum))
                .collect(Collectors.toList());
        Vertex vertex;
        if (!vertexFoundByNum.isEmpty()) {
            vertex = vertexFoundByNum.get(0);
        } else {
            throw new Exception("Verex not found");
        }
        Integer sum = 0;
        for (Edge e : vertex.getEdges()) {
            sum += e.getWeight();
        }
        return sum;
    }
}
