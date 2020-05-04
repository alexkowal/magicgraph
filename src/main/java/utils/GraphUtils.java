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

public class GraphUtils {

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

    private Graph generateNextEdgePermutation(Graph graph, PermutationIterator<Integer> iterator) {
        List<Integer> initialPermutation = iterator.next();
        for (int i = 0; i < initialPermutation.size(); i++) {
            graph.getEdges().get(i).setWeight(initialPermutation.get(i));
        }
        return graph;
    }


    public ResearchResult checkMagicPermutationExist(Graph graph) throws Exception {
        boolean isMagic = checkGraphIsMagic(graph);

        List<Integer> initialPermutation = Lists.newArrayList();
        for (int i = 0; i < graph.getEdges().size(); i++) {
            initialPermutation.add(i + 1);
        }
        PermutationIterator<Integer> iterator = new PermutationIterator<>(initialPermutation);
        while (iterator.hasNext() && !isMagic) {
            graph = generateNextEdgePermutation(graph, iterator);
            isMagic = checkGraphIsMagic(graph);
        }
        return new ResearchResult(isMagic, graph.getEdges());
    }

    private void v() {
        Generator.partition(55)
                .stream()
                .filter(integers -> integers.size() == 4)
                .filter(integers -> checkValuesUnique(integers))
                .collect(Collectors.toList());
    }

    private static boolean checkValuesUnique(List<Integer> v) {
        return v.size() == Sets.newHashSet(v).size();
    }

}
