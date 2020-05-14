package utils;

import com.google.common.collect.Lists;
import domain.Edge;
import domain.Graph;
import domain.ResearchResult;
import domain.Vertex;
import lombok.extern.slf4j.Slf4j;
import org.paukov.combinatorics3.Generator;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class GraphUtilsV3 {
    private static int totalAttempts = 30000;

    public static boolean generateAndCheck(Graph graph, int currentVertexNum, List<Integer> possibleValues,
                                           ResearchResult found, AtomicBoolean finished, AtomicInteger attempts) throws Exception {
        if (found.getResult())
            return true;

        attempts.incrementAndGet();
        if (attempts.get() > totalAttempts) {
            return false;
        }
        if (!finished.get()) {
            for (int i = currentVertexNum; i < graph.getVertices().size(); i++) {
                graph.getVertices().get(i).getEdges()
                        .forEach(edge -> edge.setMarked(false));
            }

            for (int i = 0; i < currentVertexNum; i++) {
                graph.getVertices().get(i).getEdges()
                        .forEach(edge -> edge.setMarked(true));
            }
            if (currentVertexNum < graph.getVertices().size()) {

                Vertex vertex = graph.getVertices().get(currentVertexNum);
                List<Edge> unmarkedEdges = vertex.getEdges()
                        .stream()
                        .filter(edge -> !edge.isMarked())
                        .collect(Collectors.toList());

                Integer recalculatedMagicNumber = graph.getMagicNumber() - calculateCurrentSumForVertex(vertex);

                Integer permutationSize = unmarkedEdges.size();

                if (unmarkedEdges.size() > 3) {
                    boolean res = generation(possibleValues, recalculatedMagicNumber, permutationSize, found,
                            currentVertexNum, unmarkedEdges, graph, finished, attempts);
                    if (res) {
                        found.setResult(true);
                        finished.set(true);
                        return true;
                    }
                } else {
                    List<List<Integer>> permutations = combinationSum2(possibleValues, recalculatedMagicNumber, permutationSize);
                    Collections.shuffle(permutations);
                    vertex.setPermutations(permutations);

                    if (finished.get())
                        return true;

                    for (int i = 0; i < permutations.size(); i++) {
                        if (found.getResult())
                            break;
                        if (!found.getResult()) {
                            List<Integer> permutation = permutations.get(i);
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
                                finished.set(true);
                                System.out.println(graph);
                                return true;
                            }
                            if (found.getResult())
                                break;
                            boolean res = generateAndCheck(graph, currentVertexNum + 1, copyOfPossibleValues, found, finished, attempts);
                            if (res) {
                                found.setResult(true);
                                finished.set(true);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return found.getResult();
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

    private static List<List<Integer>> generatePerms(List<Integer> values, int size) {
        List<List<Integer>> result = Lists.newArrayList();
        Generator.combination(values)
                .simple(size)
                .stream()
                .forEach(combination -> Generator.permutation(combination)
                        .simple()
                        .forEach(result::add));
        return result;
    }

    private static List<List<Integer>> permuteCombination(List<Integer> numbers) {
        return generatePerms(numbers, numbers.size());
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

    private static boolean generation(List<Integer> possibleValues, Integer magicNumber, Integer permSize, ResearchResult found,
                                      Integer currentVertexNum, List<Edge> unmarkedEdges, Graph graph, AtomicBoolean finished, AtomicInteger attempts) throws Exception {
        if (attempts.get() > totalAttempts) {
            found.setResult(false);
            return false;
        }
        if (finished.get()) {
            return true;
        }

        List<Integer> values = Lists.newArrayList(possibleValues);

        if (found.getResult()) {
            finished.set(true);
            return true;
        }

        int size = permSize;
        List<List<Integer>> first = new ArrayList<>(generatePermsForNonRecursiveSearch(values, size / 3, magicNumber));

        if (first.isEmpty()) {
            return false;
        }

        size = size - size / 3;
        List<List<Integer>> second = new ArrayList<>(generatePermsForNonRecursiveSearch(values, size / 3 + 1, magicNumber));

        if (second.isEmpty()) {
            return false;
        }

        size = permSize - first.get(0).size() - second.get(0).size();
        List<List<Integer>> third = new ArrayList<>(generatePermsForNonRecursiveSearch(values, size, magicNumber));

        if (third.isEmpty())
            return false;

        Collections.shuffle(first);
        Collections.shuffle(second);
        Collections.shuffle(third);

        for (int i = 0; i < first.size(); i++) {
            final int pos = i;
            List<Integer> tempValues = Lists.newArrayList(first.get(i));
            List<List<Integer>> filteredSecond = second.stream()
                    .filter(array -> !CollectionUtils.containsAny(array, first.get(pos)))
                    .collect(Collectors.toList());

            for (int j = 0; j < filteredSecond.size(); j++) {
                tempValues.addAll(filteredSecond.get(j));

                int tempSum = getSum(tempValues);
                List<List<Integer>> last = third.stream()
                        .filter(ar -> !CollectionUtils.containsAny(ar, tempValues))
                        .filter(ar -> checkSum(ar, magicNumber - tempSum))
                        .collect(Collectors.toList());

                for (int cc = 0; cc < last.size(); cc++) {
                    List<Integer> permutation = Lists.newArrayList();
                    permutation.addAll(first.get(i));
                    permutation.addAll(filteredSecond.get(j));
                    permutation.addAll(last.get(cc));

                    List<Integer> copyOfPossibleValues = Lists.newArrayList(possibleValues);
                    copyOfPossibleValues = copyOfPossibleValues.stream()
                            .filter(value -> !permutation.contains(value))
                            .collect(Collectors.toList());

                    for (int m = 0; m < permutation.size(); m++) {
                        Edge edge = unmarkedEdges.get(m);
                        edge.setMarked(true);
                        edge.setWeight(permutation.get(m));
                    }

                    if (checkGraphIsMagic(graph)) {
                        found.setResult(true);
                        found.setEdgeList(graph.getEdges());
                        finished.set(true);
                        System.out.println(graph);
                        return true;
                    }
                    if (found.getResult()) {
                        break;
                    }
                    boolean f = generateAndCheck(graph, currentVertexNum + 1, copyOfPossibleValues, found, finished, attempts);
                if(f)
                    return true;
                }
                tempValues.removeAll(filteredSecond.get(j));
            }
        }
        return false;
    }

    private static List<List<Integer>> generatePermsForNonRecursiveSearch(List<Integer> values, int size, int magicNumber) {
        List<List<Integer>> result = Lists.newArrayList();
        Generator.combination(values)
                .simple(size)
                .stream()
                .filter(ar -> getSum(ar) < magicNumber)
                .forEach(combination -> Generator.permutation(combination)
                        .simple()
                        .forEach(result::add));
        return result;
    }

    private static int getSum(List<Integer> v) {
        int s = 0;
        for (Integer integer : v) {
            s += integer;
        }
        return s;
    }

    private static List<List<Integer>> combinationSum2(List<Integer> nums, int target, int permutationSize) {
        List<List<Integer>> list = new ArrayList<>();
        List<List<Integer>> finalRes = Lists.newArrayList();
        backtrack(list, new ArrayList<>(), nums, permutationSize, target, 0);
        list.forEach(perm -> finalRes.addAll(permuteCombination(perm)));
        return finalRes;

    }

    private static void backtrack(List<List<Integer>> list, List<Integer> tempList,
                                  List<Integer> nums,
                                  int permutationSize,
                                  int remain,
                                  int start) {
        if (remain < 0) return;
        if (tempList.size() > permutationSize)
            return;
        else if (remain == 0 && tempList.size() == permutationSize)
            list.add(new ArrayList<>(tempList));
        else {
            for (int i = start; i < nums.size(); i++) {
                if (i > start && nums.get(i) == nums.get(i - 1))
                    continue; // skip duplicates
                tempList.add(nums.get(i));
                backtrack(list, tempList, nums, permutationSize, remain - nums.get(i), i + 1);
                tempList.remove(tempList.size() - 1);
            }
        }
    }

}

