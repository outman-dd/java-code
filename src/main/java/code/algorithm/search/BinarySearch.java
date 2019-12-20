package code.algorithm.search;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;

/**
 * 〈二分查找〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/11/27
 */
public class BinarySearch {

    public boolean search(int[] array, int toFind) {
        int[] ret = binSearch(array, toFind);
        return ret[0] == toFind;
    }

    private int[] binSearch(int[] array, int toFind) {
        if (array.length == 1) {
            return array;
        }
        int[] subArray;
        int middle = array.length / 2;
        if (array[middle] < toFind) {
            //取右侧部分数组
            subArray = Arrays.copyOfRange(array, middle + 1, array.length);
        } else if (array[middle] > toFind) {
            //取左侧部分数组
            subArray = Arrays.copyOfRange(array, 0, middle);
        } else {
            return Arrays.copyOfRange(array, middle, middle + 1);
        }
        return binSearch(subArray, toFind);
    }

    public boolean search2(int[] array, int toFind) {
        return binSearch(array, 0, array.length-1, toFind);
    }

    private boolean binSearch(int[] array, int low, int high, int toFind) {
        if(high == low){
            return array[low] == toFind;
        }
        int middle = (low+high)>>1;
        if (array[middle] < toFind) {
            //取右侧部分数组
            return binSearch(array,middle+1, high, toFind);
        } else if (array[middle] > toFind) {
            //取左侧部分数组
            return binSearch(array, low, middle-1, toFind);
        } else {
            return true;
        }
    }

    public static void main(String[] args) {
        int[] array = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        BinarySearch binarySearch = new BinarySearch();
        System.out.println("------search1:");
        System.out.println("search 10: " + binarySearch.search(array, 10));
        System.out.println("search 0: " + binarySearch.search(array, 0));
        System.out.println("search 9: " + binarySearch.search(array, 9));
        System.out.println("search 3: " + binarySearch.search(array, 3));

        System.out.println("------search2:");
        System.out.println("search 10: " + binarySearch.search2(array, 10));
        System.out.println("search 0: " + binarySearch.search2(array, 0));
        System.out.println("search 9: " + binarySearch.search2(array, 9));
        System.out.println("search 3: " + binarySearch.search2(array, 3));

        System.out.println("------search2:");
        int[] array2 = {0, 1, 2};
        System.out.println("search 0: " + binarySearch.search2(array, 0));

    }
}
