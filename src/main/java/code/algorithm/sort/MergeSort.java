package code.algorithm.sort;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;

/**
 * 〈归并排序〉<p>
 * 〈递归分治〉
 *
 * @author zixiao
 * @date 2019/11/27
 */
public class MergeSort implements ISort {

    @Override
    public int[] sort(int[] array) {
        if (array.length <= 1) {
            return array;
        }
        return mergeSort(array, array.length);
    }

    private int[] mergeSort(int[] array, int length) {
        Pair<int[], int[]> pair = splitArray(array);
        int[] leftArray = pair.getLeft();
        int[] rightArray = pair.getRight();

        //拆分成left和right两个数组，分别排序
        if (leftArray.length > 2) {
            leftArray = mergeSort(leftArray, leftArray.length);
        } else {
            Arrays.sort(leftArray);
        }
        if (rightArray.length > 2) {
            rightArray = mergeSort(rightArray, rightArray.length);
        } else {
            Arrays.sort(rightArray);
        }

        //合并, 数组left与right
        int[] mergedArray = new int[length];
        int l = 0;
        int r = 0;
        for (int i = 0; i < length; i++) {
            if (l == leftArray.length) {
                mergedArray[i] = rightArray[r];
                r++;
            } else if (r == rightArray.length) {
                mergedArray[i] = leftArray[l];
                l++;
            } else if (leftArray[l] <= rightArray[r]) {
                //稳定算法：如果两个值相等，把原顺序在前面的数据排在前面
                mergedArray[i] = leftArray[l];
                l++;
            } else {
                mergedArray[i] = rightArray[r];
                r++;
            }
        }
        return mergedArray;
    }

    private Pair<int[], int[]> splitArray(int[] array) {
        int middle = array.length / 2;
        return Pair.of(Arrays.copyOfRange(array, 0, middle + 1),
                Arrays.copyOfRange(array, middle + 1, array.length));
    }

    public static void main(String[] args) {
        int[] array = {2, 7, 3, 1, 8, 6, 9, 4, 0, 5};
        MergeSort mergeSort = new MergeSort();
        int[] sorted = mergeSort.sort(array);
        for (int i : sorted) {
            System.out.print(i + " ");
        }
    }

}
