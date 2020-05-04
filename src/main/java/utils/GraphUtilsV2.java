package utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import domain.Edge;
import domain.Graph;
import domain.ResearchResult;
import domain.Vertex;
import org.apache.commons.collections4.iterators.PermutationIterator;
import org.paukov.combinatorics3.Generator;

import java.util.List;
import java.util.stream.Collectors;

public class GraphUtilsV2 {

    private Integer calculateSumForVertex(Graph graph, Integer vertexNum) throws Exception {
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

    private boolean checkGraphIsMagic(Graph graph) throws Exception {
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

    private Graph generateNextEdgePermutation(Graph graph, List<Integer> prefix, List<Integer> values) {
        List<Edge> edges = Lists.newArrayList();
        for (Vertex vertex : graph.getVertices()) {
            edges.addAll(vertex.getEdges());
        }
        for (int i = 0; i < prefix.size(); i++) {
            edges.get(i).setWeight(prefix.get(i));
        }
        for (int i = prefix.size(); i < prefix.size() + values.size(); i++) {
            edges.get(i).setWeight(values.get(i - prefix.size()));
        }
        return graph;
    }


    public ResearchResult checkMagicPermutationExist(final Graph graph) throws Exception {
        boolean isMagic = checkGraphIsMagic(graph);
        List<Integer> initialPermutation = Lists.newArrayList();
        for (int i = 0; i < graph.getEdges().size(); i++) {
            initialPermutation.add(i + 1);
        }
        List<Integer> result = Lists.newArrayList();
        int sum = graph.getMagicNumber();
        Vertex v = graph.getVertices().get(0);
        v.setPermutations(generatePerms(initialPermutation, v.getEdges().size(), sum));
        v.getPermutations().forEach(permutation -> {
            List<Integer> perm = Lists.newArrayList(initialPermutation);
            perm.removeAll(permutation);
            PermutationIterator<Integer> iterator = new PermutationIterator<>(perm);
            boolean magic = false;
            Graph g = graph;
            while (iterator.hasNext() && result.isEmpty()) {
                List<Integer> tempPerm = iterator.next();
                g = generateNextEdgePermutation(g, permutation, tempPerm);
                try {
                    magic = checkGraphIsMagic(g);
                    if (magic) {
                        result.addAll(permutation);
                        result.addAll(tempPerm);
                        System.out.println(g);
                        System.out.println(permutation + " " + tempPerm);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return new ResearchResult(checkGraphIsMagic(graph), graph.getEdges());
    }

    private List<List<Integer>> generatePerms(List<Integer> values, int size, int sum) {
        List<List<Integer>> result = Lists.newArrayList();
        Generator.combination(values)
                .simple(size)
                .stream()
                .filter(GraphUtilsV2::checkValuesUnique)
                .filter(integers -> checkSum(integers, sum))
                .forEach(combination -> Generator.permutation(combination)
                        .simple()
                        .forEach(result::add));
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

}
