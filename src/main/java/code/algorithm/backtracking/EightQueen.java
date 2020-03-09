package code.algorithm.backtracking;

/**
 * 〈8皇后问题〉<p>
 * 有一个8x8的棋盘，希望往里放8个棋子(皇后)，每个棋子所在的行、列、对角线都不能有另一个棋子
 *
 * @author zixiao
 * @date 2019/12/19
 */
public class EightQueen {

    private static int EIGHT = 8;

    /**
     * 下标索引代表行row
     * 值代表列col
     */
    private int[] _8Queen = new int[EIGHT];

    public void build8Queen() {
        for (int i = 0; i < _8Queen.length; i++) {
            _8Queen[i] = -1;
        }
        calc8Queen(0);
    }

    private void calc8Queen(int row) {
        if (row == EIGHT) {
            print();
            return;
        }
        //8个列的位置可选
        for (int col = 0; col < EIGHT; col++) {
            if (isOk(row, col)) {
                _8Queen[row] = col;
                calc8Queen(row + 1);
            }
        }
    }

    /**
     * 与已放置的棋子，逐个比较
     * 1 不能同一列
     * 2 不能在对角线上
     *
     * @param row
     * @param col
     * @return
     */
    private boolean isOk(int row, int col) {
        for (int i = 0; i < row; i++) {
            //在同一列，不符合要求
            if (_8Queen[i] == col) {
                return false;
            }
            //在对角线上，不符合要求
            if (Math.abs(_8Queen[i] - col) == Math.abs(i - row)) {
                return false;
            }
        }
        return true;
    }

    private void print() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < _8Queen.length; i++) {
            //第一行
            for (int j = 0; j < EIGHT; j++) {
                if (_8Queen[i] == j) {
                    sb.append(1).append("\t");
                } else {
                    sb.append(0).append("\t");
                }
            }
            sb.append("\n\r");
        }
        System.out.println(sb);
    }

    public static void main(String[] args) {
        EightQueen eightQueen = new EightQueen();
        eightQueen.build8Queen();
    }
}
