package ui;

import model.Board;
import model.Player;
import model.PlayerUI;
import service.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class BoardUI extends JPanel {
    JPanel leftPanel;
    JPanel boardPanel;

    public Board board = Board.getInstance(); // 棋盘状态：0表示空，1表示黑子，-1表示白子
    Game game = Game.getInstance();
    JPanel buttonPanel = ButtonPanel.getInstance();

    public Player ourPlayer;
    public Player otherPlayer;
    public PlayerUI ourPlayerUI;
    public PlayerUI otherPlayerUI;
    public int piece = 1;

    private Point focusPoint = null; // 当前鼠标指向的交点
    private Point lastMove = null;  // 上一次落子的交点

    public void Switch() {
        if (piece == 1) piece = -1;
        else piece = 1;
        Game.getInstance().Timechanger();
    }

    private static final BoardUI instance = new BoardUI();

    public static BoardUI getInstance() {
        return instance;
    }

    private BoardUI() {
        ourPlayer = new Player(" ", " ", 1, false);
        otherPlayer = new Player(" ", " ", 1, false);
        ourPlayerUI = new PlayerUI(new ImageIcon(Objects.requireNonNull(getClass().getResource("/resource/背景.jpg"))), ourPlayer);
        otherPlayerUI = new PlayerUI(new ImageIcon(Objects.requireNonNull(getClass().getResource("/resource/背景.jpg"))), ourPlayer);

        setLayout(new BorderLayout());
        // 棋盘放在中间
        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                // 绘制整个棋盘背景
                g.setColor(new Color(222, 184, 135));
                g.fillRect(0, 0, getWidth(), getHeight());

                drawBoard(g2d); // 绘制棋盘网格
                drawPieces(g2d); // 绘制棋子
                drawFocus(g2d);  // 绘制鼠标焦点和最后落子标记
            }
        };

        // 添加鼠标事件监听器
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (piece == ourPlayer.piece) {
                    game.Move(ourPlayer, e);
                    int x = (e.getX() - board.getMARGIN() + board.getCELL_SIZE() / 2) / board.getCELL_SIZE();
                    int y = (e.getY() - board.getMARGIN() + board.getCELL_SIZE() / 2) / board.getCELL_SIZE();
                    if (x >= 0 && x < board.getBOARD_SIZE() && y >= 0 && y < board.getBOARD_SIZE()) {
                        lastMove = new Point(x, y); // 更新最后落子位置
                    }
                    if (otherPlayer.isAI && piece == -1) {
                        game.Move(otherPlayer, e);
                    }
                }
                repaint(); // 触发重绘
            }
        });

        boardPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // 计算鼠标指向的交点
                int x = (e.getX() - board.getMARGIN() + board.getCELL_SIZE() / 2) / board.getCELL_SIZE();
                int y = (e.getY() - board.getMARGIN() + board.getCELL_SIZE() / 2) / board.getCELL_SIZE();
                if (x >= 0 && x < board.getBOARD_SIZE() && y >= 0 && y < board.getBOARD_SIZE()) {
                    focusPoint = new Point(x, y); // 更新焦点位置
                } else {
                    focusPoint = null; // 鼠标移出棋盘
                }
                repaint(); // 触发重绘
            }
        });

        boardPanel.setPreferredSize(new Dimension(board.getCanvasSize(), board.getCanvasSize()));
        boardPanel.setBackground(new Color(210, 180, 140));

        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        otherPlayerUI.setPreferredSize(new Dimension(boardPanel.getWidth(), 100));
        leftPanel.add(otherPlayerUI);
        leftPanel.add(boardPanel);
        ourPlayerUI.setPreferredSize(new Dimension(boardPanel.getWidth(), 100));
        leftPanel.add(ourPlayerUI);
        add(leftPanel, BorderLayout.CENTER);

        // 右侧按钮区域
        add(buttonPanel, BorderLayout.EAST);
    }

    // 绘制焦点和最后落子标记
    private void drawFocus(Graphics2D g) {
        if (focusPoint != null&&BoardUI.getInstance().ourPlayer.piece==BoardUI.getInstance().piece) {
            // 绘制鼠标当前指向的虚线圆
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4, 4}, 0));
            int x = board.getMARGIN() + focusPoint.x * board.getCELL_SIZE();
            int y = board.getMARGIN() + focusPoint.y * board.getCELL_SIZE();
            int radius = board.getCELL_SIZE() / 3; // 调整虚线圆的大小
            g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
        }

        if (!Board.getInstance().Moves.isEmpty()) {
            // 绘制最后落子的实线圆
            lastMove = new Point(Board.getInstance().Moves.get(Board.getInstance().Moves.size() - 1).x, Board.getInstance().Moves.get(Board.getInstance().Moves.size() - 1).y);
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4, 4}, 0));
            int x = board.getMARGIN() + lastMove.x * board.getCELL_SIZE();
            int y = board.getMARGIN() + lastMove.y * board.getCELL_SIZE();
            int radius = board.getCELL_SIZE() / 3; // 调整实线圆的大小
            g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
        }
    }


    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 使用抗锯齿设置
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        // 绘制棋盘
        ourPlayerUI.repaint();
        otherPlayerUI.repaint();
        drawBoard(g2d);
        drawPieces(g2d);
    }


    private void drawBoard(Graphics2D g) {
        g.setColor(new Color(210, 180, 140));// 木纹背景颜色
        g.fillRect(board.getMARGIN(), board.getMARGIN(), (board.getBOARD_SIZE() - 1) * board.getCELL_SIZE(), (board.getBOARD_SIZE() - 1) * board.getCELL_SIZE());

        g.setColor(Color.BLACK);// 棋盘线颜色
        // 绘制网格线
        for (int i = 0; i < board.getBOARD_SIZE(); i++) {
            // 横线
            g.drawLine(board.getMARGIN(), board.getMARGIN() + i * board.getCELL_SIZE(), board.getMARGIN() + (board.getBOARD_SIZE() - 1) * board.getCELL_SIZE(), board.getMARGIN() + i * board.getCELL_SIZE());
            // 竖线
            g.drawLine(board.getMARGIN() + i * board.getCELL_SIZE(), board.getMARGIN(), board.getMARGIN() + i * board.getCELL_SIZE(), board.getMARGIN() + (board.getBOARD_SIZE() - 1) * board.getCELL_SIZE());
        }

        // 绘制天元和星位
        drawStarPoints(g);

        // 绘制棋盘边框
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.drawRect(board.getMARGIN(), board.getMARGIN(), (board.getBOARD_SIZE() - 1) * board.getCELL_SIZE(), (board.getBOARD_SIZE() - 1) * board.getCELL_SIZE());
    }

    // 绘制天元和星位
    private void drawStarPoints(Graphics2D g) {
        g.setColor(Color.BLACK);
        int[][] starPoints = {
                {3, 3}, {3, 11}, {11, 3}, {11, 11}, {7, 7} // 星位和天元位置
        };
        for (int[] point : starPoints) {
            int x = board.getMARGIN() + point[0] * board.getCELL_SIZE();
            int y = board.getMARGIN() + point[1] * board.getCELL_SIZE();
            g.fillOval(x - 5, y - 5, 10, 10); // 星位点大小
        }
    }

    // 绘制棋子
    private void drawPieces(Graphics2D g) {
        for (int i = 0; i < board.getBOARD_SIZE(); i++) {
            for (int j = 0; j < board.getBOARD_SIZE(); j++) {
                int x = board.getMARGIN() + i * board.getCELL_SIZE() - board.getCELL_SIZE() / 4;
                int y = board.getMARGIN() + j * board.getCELL_SIZE() - board.getCELL_SIZE() / 4;
                int size = board.getCELL_SIZE() / 2;

                if (board.board[i][j] == 1) { // 黑子
                    // 绘制棋子主体
                    GradientPaint blackGradient = new GradientPaint(
                            x, y, Color.DARK_GRAY,
                            x + size, y + size, Color.BLACK
                    );
                    g.setPaint(blackGradient);
                    g.fillOval(x, y, size, size);

                    // 绘制高光效果
                    GradientPaint blackHighlight = new GradientPaint(
                            x, y, Color.WHITE,
                            x + ((float) size / 2), y + (float) size / 2, new Color(0, 0, 0, 0)
                    );
                    g.setPaint(blackHighlight);
                    g.fillOval(x + size / 6, y + size / 6, size / 2, size / 2);

                } else if (board.board[i][j] == -1) { // 白子
                    // 绘制棋子主体
                    GradientPaint whiteGradient = new GradientPaint(
                            x, y, new Color(220, 220, 220), // 更柔和的浅灰色
                            x + size, y + size, Color.WHITE
                    );
                    g.setPaint(whiteGradient);
                    g.fillOval(x, y, size, size);

                    // 绘制高光效果
                    GradientPaint whiteHighlight = new GradientPaint(
                            x + (float) size / 4, y + (float) size / 4, Color.WHITE,
                            x + size, y + size, new Color(255, 255, 255, 0)
                    );
                    g.setPaint(whiteHighlight);
                    g.fillOval(x + size / 8, y + size / 8, size * 3 / 4, size * 3 / 4);

                    // 绘制边框
                    g.setColor(Color.BLACK);
                    g.drawOval(x, y, size, size);
                }
            }
        }
    }

}