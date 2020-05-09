import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class TestPerms {
    public static List<List<Integer>> combinationSum2(List<Integer> nums, int target, int permutationSize) {
        List<List<Integer>> list = new ArrayList<>();
        backtrack(list, new ArrayList<>(), nums, permutationSize, target, 0);
        return list;

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
        List<Integer> nums = Lists.newArrayList();
        for (int i = 0; i < 55; i++) {
            nums.add(i + 1);
        }
        long s = System.currentTimeMillis();

        List<List<Integer>> res = combinationSum2(nums, 164, 7);
        System.out.println(System.currentTimeMillis() - s);
        System.out.println(res.size());
    }
}
