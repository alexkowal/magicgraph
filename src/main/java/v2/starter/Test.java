package v2.starter;

import com.google.common.collect.Lists;
import org.paukov.combinatorics3.Generator;
import org.springframework.util.CollectionUtils;
import utils.GraphUtilsV3;

import java.util.List;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
//        generation();
    }

   /* private static void generation(List<Integer> possibleValues, Integer magicNumber, Integer permSize) {
        List<Integer> values = Lists.newArrayList(possibleValues);
        int size = permSize;
        List<List<Integer>> first = generatePerms2(values, size / 3)
                .stream()
                .filter(ar -> getSum(ar) < magicNumber)
                .collect(Collectors.toList());

        size = size - size / 3;
        List<List<Integer>> second = generatePerms2(values, size / 3 + 1)
                .stream()
                .filter(ar -> getSum(ar) < magicNumber)
                .collect(Collectors.toList());

        size = permSize - first.get(0).size() - second.get(0).size();
        List<List<Integer>> third = generatePerms2(values, size)
                .stream()
                .filter(list -> getSum(list) < magicNumber)
                .collect(Collectors.toList());

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


                }
                tempValues.removeAll(filteredSecond.get(j));
            }
        }
        System.out.println("FINISHED");
    }

    private static List<List<Integer>> generatePerms(List<Integer> values, int size, int sum) {
        List<List<Integer>> result = Lists.newArrayList();
        result = Generator.combination(values)
                .simple(size)
                .stream()
                .filter(GraphUtilsV3::checkValuesUnique)
                .collect(Collectors.toList());
//        System.out.println(res);
//                .collect(Collectors.toList());
//                .forEach(combination -> Generator.permutation(combination)
//                        .simple()
//                        .stream().peek(valuess-> System.out.println(valuess))
//                        .forEach(result::add));
        return result;
    }

    private static List<List<Integer>> generatePerms2(List<Integer> values, int size) {
        List<List<Integer>> result = Lists.newArrayList();
        Generator.combination(values)
                .simple(size)
                .stream()
//                .filter(GraphUtilsV3::checkValuesUnique)
                .forEach(combination -> Generator.permutation(combination)
                        .simple()
                        .forEach(result::add));
        return result;
    }

    private static boolean checkSum(List<Integer> v, Integer target) {
        int s = 0;
        for (Integer integer : v) {
            s += integer;
        }
        return s == target;
    }

    private static int getSum(List<Integer> v) {
        int s = 0;
        for (Integer integer : v) {
            s += integer;
        }
        return s;
    }*/
}
