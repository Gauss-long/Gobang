package model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public List<Step> Moves = new ArrayList<>();
    public int[][] board = new int[15][15];

    private final int BOARD_SIZE = 15;  // 棋盘大小
    private final int CELL_SIZE = 40;  // 每个格子的大小
    private final int MARGIN = 50;// 棋盘四周的空白

    public int getBOARD_SIZE() {return BOARD_SIZE;}
    public int getCELL_SIZE() {return CELL_SIZE;}
    public int getMARGIN() {return MARGIN;}
    public int getCanvasSize() {
        return BOARD_SIZE * CELL_SIZE + MARGIN * 2;}

    private static final Board instance = new Board();

    public static Board getInstance() {
        return instance;
    }

    private Board() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++)
                board[i][j] = 0;
        }
    }

    public void clearBoard() {
        for (int i = 0; i < 15; i++)
            for (int j = 0; j < 15; j++)
                board[i][j] = 0;
    }

    public void clearMoves() {
        Moves.clear();
    }

    public void forward(int i, int j, int piece) {//
        board[i][j] = piece;
        Moves.add(new Step(i, j, piece, Moves.size() + 1));
    }

    public void back() {
        if (!Moves.isEmpty()) {
            Step s = Moves.get(Moves.size() - 1);
            System.out.println(Moves.size());
            board[s.x][s.y] = 0;
            Moves.remove(Moves.size() - 1);
        } else {
            System.out.println("棋盘已空，无法悔棋");
        }
    }

    public boolean checkWin(int x, int y, int piece) {
        return checkDirection(x, y, piece, 1, 0) // 检查横向
                || checkDirection(x, y, piece, 0, 1) // 检查纵向
                || checkDirection(x, y, piece, 1, 1) // 检查右下斜线
                || checkDirection(x, y, piece, 1, -1); // 检查左下斜线
    }

    // 检查某一方向是否有五个连续的棋子
    boolean checkDirection(int x, int y, int piece, int dx, int dy) {
        int count = 0;
        for (int i = -4; i <= 4; i++) {
            int nx = x + i * dx;
            int ny = y + i * dy;
            if (nx >= 0 && nx < 15 && ny >= 0 && ny < 15 && board[nx][ny] == piece) {
                count++;
                if (count == 5) {
                    return true;
                }
            } else {
                count = 0;
            }
        }
        return false;
    }

    boolean isFull() {
        for (int i = 0; i < 15; i++)
            for (int j = 0; j < 15; j++)
                if (board[i][j] == 0) return false;
        return true;
    }

}
