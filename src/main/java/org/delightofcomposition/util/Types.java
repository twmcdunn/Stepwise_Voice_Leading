package org.delightofcomposition.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Types {
    public static ArrayList<Integer> arrToArrList(int[] arr) {
        return new ArrayList<Integer>(Arrays.stream(arr).boxed().collect(Collectors.toList()));
    }

    public static int[] arrListToArr(ArrayList<Integer> list) {
        int[] arr = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }
}
