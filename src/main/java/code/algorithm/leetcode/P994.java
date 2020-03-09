package code.algorithm.leetcode;

import java.util.*;


/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/3/4
 */
public class P994 {

    class Orange {
        int x;
        int y;

        public Orange(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Orange orange = (Orange) o;
            return x == orange.x &&
                    y == orange.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    Queue<Orange> badQueue = new ArrayDeque<>();
    int goodNum = 0;

    public int orangesRotting(int[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 2) {
                    badQueue.add(new Orange(i, j));
                } else if (grid[i][j] == 1) {
                    goodNum++;
                }
            }
        }
        int minutes = 0;
        int maxX = grid.length - 1;
        int maxY = grid[0].length - 1;
        while (goodNum > 0 && !badQueue.isEmpty()) {
            int badNum = badQueue.size();
            for (int i = 0; i < badNum; i++) {
                Orange bad = badQueue.poll();
                if (bad.x > 0) {
                    goodToBad(grid, bad.x - 1, bad.y);
                }
                if (bad.y > 0) {
                    goodToBad(grid, bad.x, bad.y - 1);
                }
                if (bad.x < maxX) {
                    goodToBad(grid, bad.x + 1, bad.y);
                }
                if (bad.y < maxY) {
                    goodToBad(grid, bad.x, bad.y + 1);
                }
            }
            minutes++;
        }
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1) {
                    return -1;
                }
            }
        }
        return minutes;
    }

    private void goodToBad(int[][] grid, int x, int y) {
        if (grid[x][y] == 1) {
            goodNum--;
            grid[x][y] = 2;
            badQueue.add(new Orange(x, y));
        }
    }

    public static void main(String[] args) {
        P994 solution = new P994();
        int[][] grid = {{2, 1, 1}, {1, 1, 0}, {0, 1, 1}};
        System.out.println(solution.orangesRotting(grid));

        int level = 1;
        if(level%2 == 1){
            Collections.emptyList();
        }
    }

}
