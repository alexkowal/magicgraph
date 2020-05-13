package utils;

import com.google.common.collect.Lists;
import domain.Edge;
import domain.Graph;
import domain.ResearchResult;
import domain.Vertex;
import org.paukov.combinatorics3.Generator;
import org.springframework.util.CollectionUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class GraphUtilsV3 {

    public static boolean generateAndCheck(Graph graph, int currentVertexNum, List<Integer> possibleValues,
                                           ResearchResult found, long iteration, AtomicInteger attempts) throws Exception {
        if (found.getResult())
            return true;
        iteration++;
        attempts.incrementAndGet();
        if (attempts.get() > 100000)
            return false;
        for (int i = currentVertexNum; i < graph.getVertices().size(); i++) {
            graph.getVertices().get(i).getEdges().stream()
                    .forEach(edge -> edge.setMarked(false));
        }

        for (int i = 0; i < currentVertexNum; i++) {
            graph.getVertices().get(i).getEdges().stream()
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

            if (unmarkedEdges.size() > 8) {
                generation(possibleValues, recalculatedMagicNumber, permutationSize, found, currentVertexNum, unmarkedEdges, graph, iteration, attempts);
            } else {
                List<List<Integer>> permutations = combinationSum2(possibleValues, recalculatedMagicNumber, permutationSize);
                Collections.shuffle(permutations);
                vertex.setPermutations(permutations);

                for (int i = 0; i < permutations.size(); i++) {
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
//                            BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/aleksandr/magicgraph/src/main/java/files/" + graph.getName()));
//                            bw.write(graph.getName());
//                            bw.newLine();
//                            bw.write(String.valueOf(graph.getEdges()));
//                            bw.close();
                            System.out.println(graph);
                            return true;
                        }
                        if (found.getResult())
                            break;
                        generateAndCheck(graph, currentVertexNum + 1, copyOfPossibleValues, found, iteration, attempts);
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

    static void sumUpRecursive(List<Integer> numbers, int target,
                               ArrayList<Integer> partial,
                               List<List<Integer>> res,
                               int permSize) {
        int s = 0;
        if (partial.size() > permSize)
            return;
        for (int x : partial) s += x;
        if (s == target) {
            if (partial.size() == permSize)
                res.add(partial);
        }
        if (s >= target)
            return;
        for (int i = 0; i < numbers.size(); i++) {
            List<Integer> remaining = new ArrayList<Integer>();
            int n = numbers.get(i);
            for (int j = i + 1; j < numbers.size(); j++) remaining.add(numbers.get(j));
            ArrayList<Integer> partialRec = new ArrayList<>(partial);
            partialRec.add(n);
            remaining = remaining.stream()
                    .filter(dig -> dig <= target - getCurrentSum(partialRec))
                    .collect(Collectors.toList());
            sumUpRecursive(remaining, target, partialRec, res, permSize);
        }
    }

    static int getCurrentSum(List<Integer> values) {
        int sum = 0;
        for (int i = 0; i < values.size(); i++) {
            sum += values.get(i);

        }
        return sum;
    }

    private static List<List<Integer>> generatePermsRecursive(List<Integer> numbers, int target, int permSize) {
        List<List<Integer>> res = Lists.newArrayList();
        List<List<Integer>> finalRes = Lists.newArrayList();
        sumUpRecursive(numbers, target, new ArrayList<>(), res, permSize);
        res.forEach(perm -> finalRes.addAll(permuteCombination(perm)));
        if (finalRes.size() > 1000)
            System.out.println(finalRes.size());
        return finalRes;
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
                                      Integer currentVertexNum, List<Edge> unmarkedEdges, Graph graph, long iteration, AtomicInteger attempts) throws Exception {
        List<Integer> values = Lists.newArrayList(possibleValues);
        if (iteration > 15)
            return false;
        if (attempts.get() > 100000)
            return false;
        iteration++;
        int size = permSize;
        List<List<Integer>> first = generatePermsForNonRecursiveSearch(values, size / 3)
                .stream()
                .filter(ar -> getSum(ar) < magicNumber)
                .collect(Collectors.toList());

        size = size - size / 3;
        List<List<Integer>> second = generatePermsForNonRecursiveSearch(values, size / 3 + 1)
                .stream()
                .filter(ar -> getSum(ar) < magicNumber)
                .collect(Collectors.toList());

        size = permSize - first.get(0).size() - second.get(0).size();
        List<List<Integer>> third = generatePermsForNonRecursiveSearch(values, size)
                .stream()
                .filter(list -> getSum(list) < magicNumber)
                .collect(Collectors.toList());

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

                List<List<Integer>> last = third.stream()
                        .filter(ar -> !CollectionUtils.containsAny(ar, tempValues))
                        .filter(ar -> checkSum(ar, magicNumber - getSum(tempValues)))
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
//                        BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/aleksandr/magicgraph/src/main/java/files/" + graph.getName()));
//                        bw.write(graph.getName());
//                        bw.newLine();
//                        bw.write(String.valueOf(graph.getEdges()));
//                        bw.close();
                        System.out.println(graph);
                        return true;
                    }
                    if (found.getResult())
                        break;
                    generateAndCheck(graph, currentVertexNum + 1, copyOfPossibleValues, found, iteration, attempts);
                }
                tempValues.removeAll(filteredSecond.get(j));

            }
        }
        return false;
    }

    private static List<List<Integer>> generatePermsForNonRecursiveSearch(List<Integer> values, int size) {
        List<List<Integer>> result = Lists.newArrayList();
        Generator.combination(values)
                .simple(size)
                .stream()
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
