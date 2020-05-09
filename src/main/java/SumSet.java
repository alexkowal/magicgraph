import com.google.common.collect.Lists;
import org.paukov.combinatorics3.Generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class SumSet {
    static void sum_up_recursive(List<Integer> numbers, int target, ArrayList<Integer> partial, List<List<Integer>> res) {
        int s = 0;
        if (partial.size() > 4)
            return;
        for (int x : partial)
            s += x;
        if (s == target) {
            if (partial.size() == 4) {
//                System.out.println("sum(" + Arrays.toString(partial.toArray()) + ")=" + target);
                res.add(partial);
            }
        }
        if (s > target)
            return;
        for (int i = 0; i < numbers.size(); i++) {
            List<Integer> remaining = new ArrayList<>();
            int n = numbers.get(i);
            for (int j = i + 1; j < numbers.size(); j++)
                remaining.add(numbers.get(j));
            ArrayList<Integer> partial_rec = Lists.newArrayList(partial);
            partial_rec.add(n);
            remaining = remaining.stream().filter(dig -> dig <= target - getCurrentSum(partial_rec))
                    .collect(Collectors.toList());
            sum_up_recursive(remaining, target, partial_rec, res);
        }
    }

    static int getCurrentSum(List<Integer> values) {
        int sum = 0;
        for (int i = 0; i < values.size(); i++) {
            sum += values.get(i);

        }
        return sum;
    }

    static void sum_up(ArrayList<Integer> numbers, int target, List<List<Integer>> res) {
        sum_up_recursive(numbers, target, new ArrayList<Integer>(), res);
    }

    public static void main(String args[]) {
        List<Integer> numbers = Lists.newArrayList();
        for (int i = 0; i < 55; i++)
            numbers.add(i + 1);
        int target = 207;


        long s = System.currentTimeMillis();
        List<List<Integer>> res = generatePermsRecursive(numbers, target, 9);
//        sum_up(numbers, target, res);
        System.out.println(System.currentTimeMillis() - s);
        System.out.println(res.size());
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

    private static List<List<Integer>> generatePermsRecursive(List<Integer> numbers, int target, int permSize) {
        List<List<Integer>> res = Lists.newArrayList();
        List<List<Integer>> finalRes = Lists.newArrayList();
        sumUpRecursive(numbers, target, new ArrayList<>(), res, permSize);
        res.forEach(perm -> finalRes.addAll(permuteCombination(perm, target)));
        if (finalRes.size() > 1000)
            System.out.println(finalRes.size());
        return finalRes;
    }

    private static List<List<Integer>> permuteCombination(List<Integer> numbers, int sum) {
        return generatePerms(numbers, numbers.size(), sum);
    }

}