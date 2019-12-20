package code.collection.graph;

/**
 * 〈邻接矩阵〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/12/16
 */
public class AdjacencyMatrix {

    private int[][] matrix;

    private int capacity;

    /**
     * 有向/无向
     */
    private boolean directed;

    public AdjacencyMatrix(int capacity, boolean directed) {
        this.capacity = capacity;
        this.directed = directed;
        matrix = new int[capacity][capacity];
    }

    public void put(int a, int b) {
        if (directed) {
            putDirected(a, b);
        } else {
            putUndirected(a, b);
        }
    }

    /**
     * 无向图
     * @param a
     * @param b
     */
    private void putUndirected(int a, int b) {
        matrix[a-1][b-1] = 1;
        matrix[b-1][a-1] = 1;
    }

    /**
     * 有向图
     * @param user
     * @param follower
     */
    private void putDirected(int user, int follower) {
        matrix[user-1][follower-1] = 1;
    }

    public void print() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < matrix.length; i++) {
            int[] a = matrix[i];
            for (int j = 0; j < a.length; j++) {
                sb.append(a[j]).append("\t");
            }
            sb.append("\n\r");
        }
        System.out.println(sb);
    }

    public static void main(String[] args) {
        /**
         * 无向图
         * 1----3
         * |   /|
         * |  / |
         * | /  |
         * 2----4
         */
        System.out.println("--------------无向图");
        AdjacencyMatrix undirected = new AdjacencyMatrix(4, false);
        undirected.put(1, 3);
        undirected.put(1, 2);
        undirected.put(2, 4);
        undirected.put(2, 3);
        undirected.put(3, 4);
        undirected.print();

        /**
         * 有向图
         * 1<----3
         * |    ^^
         * |   / |
         * |  /  |
         * | /   |
         * vv    |
         * 2<----4
         */
        System.out.println("--------------有向图");
        AdjacencyMatrix directed = new AdjacencyMatrix(4, true);
        directed.put(1, 2);
        directed.put(2, 3);
        directed.put(3, 1);
        directed.put(3, 2);
        directed.put(4, 2);
        directed.put(4, 3);
        directed.print();
    }

}
