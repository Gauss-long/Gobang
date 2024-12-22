package model;

import ui.MenuUI;

import java.awt.event.MouseEvent;
import java.util.Random;

public class AIPlayer extends Player{

    public static final int BOARD_SIZE = 15;
    private static final Random RANDOM = new Random();

    public AIPlayer(String id, String name, int piece) {
        super(id, name, piece,true);
    }
    @Override
    public int[] Move(MouseEvent e, int MARGIN, int CELL_SIZE, int[][] board){
        if(MenuUI.getInstance().AIHard==0)return Move1(e,MARGIN,CELL_SIZE,board);
        else return Move2(e,MARGIN,CELL_SIZE,board,Board.getInstance().Moves.get(Board.getInstance().Moves.size()-1).x,Board.getInstance().Moves.get(Board.getInstance().Moves.size()-1).y);
    }
    private int[] Move1(MouseEvent e, int MARGIN, int CELL_SIZE, int[][] board){
        // 检查是否是第一步（整个棋盘只有空格）
        if (isFirstMove(board)) {
            // 随机选择一个位置
            int randomX = RANDOM.nextInt(BOARD_SIZE);
            int randomY = RANDOM.nextInt(BOARD_SIZE);
            while (board[randomX][randomY] != 0) { // 确保随机位置为空
                randomX = RANDOM.nextInt(BOARD_SIZE);
                randomY = RANDOM.nextInt(BOARD_SIZE);
            }
            return new int[]{randomX,randomY};
        }

        int bestScore = Integer.MIN_VALUE;
        int bestX = -1, bestY = -1;

        // 遍历棋盘的每个位置
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 0) { // 空位
                    // 模拟 AI 落子
                    board[i][j] = 2;

                    // 计算评分
                    int score = evaluateBoard(board);

                    // 还原空位
                    board[i][j] = 0;

                    // 更新最佳分数和位置
                    if (score > bestScore) {
                        bestScore = score;
                        bestX = i;
                        bestY = j;
                    }
                }
            }
        }
        int[] res =new int[2];
        // AI 落子
        if (bestX != -1 && bestY != -1) {
            res[0]=bestX;
            res[1]=bestY;
        }

        return res;
    }
    // 判断是否是第一步
    private static boolean isFirstMove(int[][] board) {
        int sum=0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] != 0) {
                    sum++;
                }
            }
        }
        return sum == 1;
    }

    // 简单评分函数：根据棋子连线数量进行评分
    private static int evaluateBoard(int[][] board) {
        int score = 0;

        // 遍历每个位置，根据棋子的连线数量评分
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == -1) { // AI 棋子
                    score += evaluatePosition(board, i, j, -1);
                } else if (board[i][j] == 1) { // 人类棋子
                    score -= evaluatePosition(board, i, j, 1);
                }
            }
        }

        return score;
    }

    // 计算某个位置的评分
    private static int evaluatePosition(int[][] board, int x, int y, int player) {
        int score = 0;

        // 方向数组：四个方向（水平、垂直、两条对角线）
        int[][] directions = {{1, 0}, {0, 1}, {1, 1}, {1, -1}};

        for (int[] dir : directions) {
            int count = 1; // 连线棋子数量
            int block = 0; // 是否被阻挡

            // 正方向
            int dx = dir[0], dy = dir[1];
            for (int step = 1; step < 5; step++) {
                int nx = x + step * dx, ny = y + step * dy;
                if (nx < 0 || ny < 0 || nx >= BOARD_SIZE || ny >= BOARD_SIZE || board[nx][ny] != player) {
                    if (nx < 0 || ny < 0 || nx >= BOARD_SIZE || ny >= BOARD_SIZE || board[nx][ny] != 0) {
                        block++;
                    }
                    break;
                }
                count++;
            }

            // 反方向
            for (int step = 1; step < 5; step++) {
                int nx = x - step * dx, ny = y - step * dy;
                if (nx < 0 || ny < 0 || nx >= BOARD_SIZE || ny >= BOARD_SIZE || board[nx][ny] != player) {
                    if (nx < 0 || ny < 0 || nx >= BOARD_SIZE || ny >= BOARD_SIZE || board[nx][ny] != 0) {
                        block++;
                    }
                    break;
                }
                count++;
            }

            // 评分规则：根据连线数量和阻挡情况评分
            if (count >= 5) {
                score += 100000; // 五连胜利
            } else if (count == 4 && block == 0) {
                score += 10000; // 活四
            } else if (count == 4 && block == 1) {
                score += 500; // 冲四
            } else if (count == 3 && block == 0) {
                score += 200; // 活三
            } else if (count == 3 && block == 1) {
                score += 50; // 冲三
            } else if (count == 2 && block == 0) {
                score += 10; // 活二
            }
        }

        return score;
    }
    public boolean acceptDraw(){
        int Score=evaluateBoard(Board.getInstance().board);
        return Score < 800;
    }



    static final int[] dx = {1, 1, 0, -1, -1, -1, 0, 1};
    static final int[] dy = {0, 1, 1, 1, 0, -1, -1, -1};

    private int[] Move2(MouseEvent e, int MARGIN, int CELL_SIZE, int[][] board, int lastMoveX, int lastMoveY) {
        int[] bestMove = new int[2];
        int maxScore = Integer.MIN_VALUE;

        // 首先检查是否有必须防守的位置（对手快要连五）
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 0) {
                    // 检查对手在此位置是否能连五
                    board[i][j] = 1;  // 模拟对手下子
                    if (isWinningMove(board, i, j)) {
                        bestMove[0] = i;
                        bestMove[1] = j;
                        board[i][j] = 0;  // 恢复
                        return bestMove;  // 立即返回防守位置
                    }
                    board[i][j] = 0;  // 恢复
                }
            }
        }

        // 如果没有紧急防守点，寻找最优进攻位置
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 0 && isNearLastMove(i, j, lastMoveX, lastMoveY)) {
                    // 计算这个位置的综合得分
                    int score = calculateScore(board, i, j);
                    if (score > maxScore) {
                        maxScore = score;
                        bestMove[0] = i;
                        bestMove[1] = j;
                    }
                }
            }
        }
        return bestMove;
    }

    // 判断某个位置是否能连五
    private boolean isWinningMove(int[][] board, int x, int y) {
        int player = board[x][y];
        // 检查四个方向
        for (int dir = 0; dir < 4; dir++) {
            int count = 1;
            // 正向检查
            for (int step = 1; step <= 4; step++) {
                int nx = x + dx[dir] * step;
                int ny = y + dy[dir] * step;
                if (nx < 0 || nx >= BOARD_SIZE || ny < 0 || ny >= BOARD_SIZE || board[nx][ny] != player) break;
                count++;
            }
            // 反向检查
            for (int step = 1; step <= 4; step++) {
                int nx = x - dx[dir] * step;
                int ny = y - dy[dir] * step;
                if (nx < 0 || nx >= BOARD_SIZE || ny < 0 || ny >= BOARD_SIZE || board[nx][ny] != player) break;
                count++;
            }
            if (count >= 5) return true;
        }
        return false;
    }

    // 计算位置的综合得分
    private int calculateScore(int[][] board, int x, int y) {
        board[x][y] = -1;  // 模拟白子落子
        int attackScore = evaluatePosition1(board, x, y, -1);  // 进攻得分
        board[x][y] = 1;   // 模拟黑子落子
        int defenseScore = evaluatePosition1(board, x, y, 1);  // 防守得分
        board[x][y] = 0;   // 恢复空位

        // 位置价值评估（越靠近中心价值越高）
        int positionScore = calculatePositionValue(x, y);

        // 根据不同情况权衡进攻和防守
        if (attackScore >= 10000) return attackScore * 2;  // 能赢直接走
        if (defenseScore >= 10000) return defenseScore;    // 防守紧急位置

        // 综合评分：进攻 + 防守 + 位置价值
        return attackScore + defenseScore + positionScore;
    }

    // 评估某个位置对某个玩家的价值
    private int evaluatePosition1(int[][] board, int x, int y, int player) {
        int totalScore = 0;
        // 检查四个方向
        for (int dir = 0; dir < 4; dir++) {
            // 获取该方向的连子信息
            int[] info = getLineInfo(board, x, y, dir, player);
            int count = info[0];    // 连子数
            int blocks = info[1];   // 被堵住的数量
            totalScore += getTypeScore(count, blocks);
        }
        return totalScore;
    }

    // 获取某个方向的连子信息
    private int[] getLineInfo(int[][] board, int x, int y, int dir, int player) {
        int count = 1;  // 连子数
        int blocks = 0; // 被封堵数

        // 向两个方向查找
        for (int sign = 1; sign >= -1; sign -= 2) {
            for (int step = 1; step <= 4; step++) {
                int nx = x + dx[dir] * step * sign;
                int ny = y + dy[dir] * step * sign;

                if (nx < 0 || nx >= BOARD_SIZE || ny < 0 || ny >= BOARD_SIZE) {
                    blocks++;
                    break;
                }

                if (board[nx][ny] == player) count++;
                else if (board[nx][ny] == 0) break;
                else {
                    blocks++;
                    break;
                }
            }
        }
        return new int[]{count, blocks};
    }

    // 计算位置价值
    private int calculatePositionValue(int x, int y) {
        int centerX = BOARD_SIZE / 2;
        int centerY = BOARD_SIZE / 2;
        int distance = Math.abs(x - centerX) + Math.abs(y - centerY);
        return (BOARD_SIZE - distance) * 5;  // 减小位置分数的权重
    }

    // 根据连子类型返回分数
    private int getTypeScore(int count, int blocks) {
        if (blocks == 0) {  // 活棋
            switch (count) {
                case 5: return 100000;  // 连五
                case 4: return 10000;   // 活四
                case 3: return 5000;    // 活三
                case 2: return 500;     // 活二
                default: return 10;
            }
        } else if (blocks == 1) {  // 半活棋
            switch (count) {
                case 5: return 100000;  // 连五
                case 4: return 5000;    // 冲四
                case 3: return 500;     // 眠三
                case 2: return 50;      // 眠二
                default: return 1;
            }
        }
        return 0;  // 死棋
    }

    // 判断是否在最近落子的范围内
    private boolean isNearLastMove(int x, int y, int lastMoveX, int lastMoveY) {
        return Math.abs(x - lastMoveX) <= 2 && Math.abs(y - lastMoveY) <= 2;
    }


}
