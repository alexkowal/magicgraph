package utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import domain.Edge;
import domain.Graph;
import domain.ResearchResult;
import domain.Vertex;
import org.paukov.combinatorics3.Generator;

import java.util.*;
import java.util.stream.Collectors;


public class GraphUtilsV5 {

    public static boolean generateAndCheck(Graph graph) throws Exception {
        Map<Integer, Long> vertexToUnmarkedEdgesMap = new HashMap<>();

        for (Vertex vertex : graph.getVertices()) {
            long unmarkedEdgesCount = vertex.getEdges()
                    .stream()
                    .filter(edge -> !edge.isMarked())
                    .count();
            vertexToUnmarkedEdgesMap.put(vertex.getNum(), unmarkedEdgesCount);
//            vertex.getEdges().forEach(edge -> edge.setMarked(true));
        }

        List<Integer> possibleValues = Lists.newArrayList();
        for (int i = 0; i < graph.getEdges().size(); i++) {
            possibleValues.add(i + 1);
        }
        vertexToUnmarkedEdgesMap.entrySet().forEach(value -> {
            System.out.println(value.getKey());
            Vertex v = graph.getVertices().stream().
                    filter(vertex -> vertex.getNum().equals(value.getKey())).findFirst().get();
            if (v.getPermutations().isEmpty()) {
                if(value.getValue()>0)
                    System.out.println("");
                List<List<Integer>> perm = combinationSum2(possibleValues, graph.getMagicNumber(),
                        Math.toIntExact(value.getValue()));
                vertexToUnmarkedEdgesMap.entrySet()
                        .stream()
                        .filter(entry -> entry.getValue().equals(value.getValue()))
                        .forEach(entry -> graph.getVertices()
                                .stream()
                                .filter(vertex -> vertex.getNum().equals(entry.getKey()))
                                .collect(Collectors.toList()).get(0)
                                .setPermutations(perm));
            }
        });

        iterateOverPermutationsForVertex(graph, 0, Lists.newArrayList());
        // рекурсивно пройтись по перестановкам фильтровать и если массив пустой то стоп
        // если не пустой в конце то промаркировать граф по перестановке и вывести результат


        return false;
    }

    private static void iterateOverPermutationsForVertex(Graph graph, int vertexNum, List<Integer> currentPermutation) {
        Vertex v = graph.getVertices().get(vertexNum);
        List<List<Integer>> perms = v.getPermutations();
        perms.stream()
                .filter(perm -> {
                    boolean allowed = true;
                    for (int i = 0; i < v.getEdges().size(); i++) {
                        if (v.getEdges().get(i).isMarked()) {
                            if (!perm.get(i).equals(v.getEdges().get(i).getWeight()))
                                allowed = false;
                        }
                    }
                    for (int i = 0; i < perm.size(); i++) {
                        if (currentPermutation.contains(perm.get(i)))
                            return false;
                    }
                    return allowed;
                })
                .forEach(perm -> {
                    List<Integer> tempPerm = Lists.newArrayList(currentPermutation);
                    tempPerm.addAll(perm);
                    System.out.println(tempPerm);
                    List<Edge> edges = v.getEdges();
                    for (int i = 0; i < perm.size(); i++) {
                        edges.get(i).setWeight(perm.get(i));
                        edges.get(i).setMarked(true);
                    }
                    if (vertexNum == graph.getVertices().size()) {
                        try {
                            if (checkGraphIsMagic(graph))
                                System.out.println(graph);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (vertexNum < graph.getVertices().size())
                        iterateOverPermutationsForVertex(graph, vertexNum + 1, tempPerm);
                });
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

    private static List<List<Integer>> generatePerms(List<Integer> values, int size, int sum) {
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

    private static List<List<Integer>> permuteCombination(List<Integer> numbers, int sum) {
        return generatePerms(numbers, numbers.size(), sum);
    }

    public static boolean checkValuesUnique(List<Integer> v) {
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


    public static List<List<Integer>> combinationSum2(List<Integer> nums, int target, int permutationSize) {
        System.out.println("Start generation");
        List<List<Integer>> list = new ArrayList<>();
        List<List<Integer>> finalRes = Lists.newArrayList();
        backtrack(list, new ArrayList<>(), nums, permutationSize, target, 0);
        list.forEach(perm -> finalRes.addAll(permuteCombination(perm, permutationSize)));
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

    public static void main(String[] args) {

    }

}
