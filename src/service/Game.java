package service;
import model.AIPlayer;
import model.Board;
import model.DropdownPanel;
import model.Player;
import ui.BoardUI;
import ui.ButtonPanel;
import ui.ChatDialog;
import ui.FrameUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

import static jdk.nashorn.internal.objects.NativeString.substr;


public class Game {
    private static  final Game instance =new Game();
    Board board = Board.getInstance();
    public static Game getInstance() {
        return instance;
    }
    private Game() {
    }
    private final TimeListener thread = TimeListener.getInstance();

    public void Start() {
        ChatDialog.getInstance().clear();
        if(NetWork.getInstance().isSS())NetWork.getInstance().sendGameTime();
        Music.getInstance().stop(0);
        Music.getInstance().play(2);
        Music.getInstance().play(1);
        changeTime();
        String s = substr(String.valueOf(LocalDateTime.now()),11,2);
        Random random = new Random(Integer.parseInt(s));
        double flag = random.nextDouble();
        if(NetWork.getInstance().isS()){
            if(NetWork.getInstance().isSS()){
                if(flag>0.5){
                    BoardUI.getInstance().ourPlayer = new Player(NetWork.getInstance().getLocalIP(), "黑方",1,false);
                    BoardUI.getInstance().otherPlayer = new Player(NetWork.getInstance().getInetIP(), "白方",-1,false);
                }else{
                    BoardUI.getInstance().ourPlayer = new Player(NetWork.getInstance().getLocalIP(), "白方",-1,false);
                    BoardUI.getInstance().otherPlayer = new Player(NetWork.getInstance().getInetIP(), "黑方",1,false);
                }
            }else{
                if(flag>0.5){
                    BoardUI.getInstance().ourPlayer = new Player(NetWork.getInstance().getLocalIP(), "白方",-1,false);
                    BoardUI.getInstance().otherPlayer = new Player(NetWork.getInstance().getInetIP(), "黑方",1,false);
                }else{
                    BoardUI.getInstance().ourPlayer = new Player(NetWork.getInstance().getLocalIP(), "黑方",1,false);
                    BoardUI.getInstance().otherPlayer = new Player(NetWork.getInstance().getInetIP(), "白方",-1,false);
                }
            }
        }else{
            BoardUI.getInstance().ourPlayer = new Player("人类","黑方",1,false);
            BoardUI.getInstance().otherPlayer = new AIPlayer("AI","白方",-1);
        }
        ButtonPanel.getInstance().chatButton.setVisible(!BoardUI.getInstance().otherPlayer.isAI);
        BoardUI.getInstance().ourPlayerUI.panel.add(TimeCount.getOurCount());
        BoardUI.getInstance().otherPlayerUI.panel.add(TimeCount.getOtherCount());
        Timechanger();
        thread.startListening();
        BoardUI.getInstance().ourPlayerUI.reset(new ImageIcon(Objects.requireNonNull(getClass().getResource("/resource/头像1.png"))),BoardUI.getInstance().ourPlayer);
        BoardUI.getInstance().otherPlayerUI.reset(new ImageIcon(Objects.requireNonNull(getClass().getResource("/resource/头像2.png"))),BoardUI.getInstance().otherPlayer);

        BoardUI.getInstance().revalidate();
        BoardUI.getInstance().repaint();
        FrameUI.getInstance().cardLayout.show(FrameUI.getInstance().mainPanel, "Board");


        ButtonPanel.getInstance().undoButton.setEnabled(true);
        ButtonPanel.getInstance().admitDefeatButton.setEnabled(true);
        ButtonPanel.getInstance().drawButton.setEnabled(true);
        ButtonPanel.getInstance().goButton.setEnabled(false);
        ButtonPanel.getInstance().backButton.setEnabled(false);
        ButtonPanel.getInstance().quitButton.setEnabled(false);

        BoardUI.getInstance().piece = 1;
        Board.getInstance().clearBoard();
        Board.getInstance().clearMoves();
    }

    public void Move(Player player, MouseEvent e) {
        // 检查是否在有效范围内，且是否为空格
        int x, y;
        int[] a;
        a = player.Move(e, board.getMARGIN(), board.getCELL_SIZE(), BoardUI.getInstance().board.board);
        x = a[0];
        y = a[1];
        if (x >= 0 && x < board.getBOARD_SIZE() && y >= 0 && y < board.getBOARD_SIZE() && BoardUI.getInstance().board.board[x][y] == 0) {
            Music.getInstance().play(3);
            if(NetWork.getInstance().isS())NetWork.getInstance().sendChess(x,y,BoardUI.getInstance().piece);
            BoardUI.getInstance().board.forward(x, y, BoardUI.getInstance().piece);
            BoardUI.getInstance().repaint();
            if (BoardUI.getInstance().board.checkWin(x, y, BoardUI.getInstance().piece)) {
                if (player.name.equals("黑方")) end(1);
                else end(-1);
                return;
            }
            BoardUI.getInstance().Switch();
        }
    }

    public void end(int p) {
        ChatDialog.hideDialog();
        thread.stopListening();
        TimeCount.getOurCount().pauseTimer();
        TimeCount.getOtherCount().pauseTimer();
        if(NetWork.getInstance().isS()){NetWork.getInstance().close();}
        JDialog dialog;
        dialog = new JDialog(FrameUI.getInstance(), "GameOver", true);

        JPanel panel = new JPanel();
        Font font = new Font("楷体", Font.BOLD, 20);
        Color color = new Color(210, 180, 140);
        JLabel label;
        if (p == 1) {
            label = new JLabel("黑方胜");
        }else if(p==2){
            label = new JLabel("白方超时,黑方胜");
        }else if(p==-1){
            label = new JLabel("白方胜");
        }else if(p==-2){
            label = new JLabel("黑方超时,白方胜");
        }else{
            label = new JLabel("和棋!");
        }
        label.setFont(font);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton b1 = new JButton("返回菜单");
        b1.setFont(font);
        b1.setBackground(color);
        b1.setAlignmentX(Component.CENTER_ALIGNMENT); // 让按钮居中
        b1.addActionListener(e -> {
            FrameUI.getInstance().cardLayout.show(FrameUI.getInstance().mainPanel, "Menu");
            Music.getInstance().stop(1);
            Music.getInstance().play(0);
            dialog.dispose();
        });
        JButton b2 = new JButton("我要复盘");
        b2.setFont(font);
        b2.setBackground(color);
        b2.setAlignmentX(Component.CENTER_ALIGNMENT); // 让按钮居中
        b2.addActionListener(e->{
            Review.getInstance().startReview();
            dialog.dispose();
        });

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createVerticalGlue());

        panel.add(label);
        panel.add(Box.createVerticalStrut(40));
        panel.add(b1);
        panel.add(Box.createVerticalStrut(40));
        panel.add(b2);
        panel.add(Box.createVerticalGlue());


        dialog.add(panel);
        dialog.setPreferredSize(new Dimension(250, 300));
        dialog.setMinimumSize(new Dimension(250, 300));
        dialog.setLocationRelativeTo(FrameUI.getInstance());
        dialog.setVisible(true);
    }
    public void undo(){
        if(BoardUI.getInstance().otherPlayer.isAI){
            BoardUI.getInstance().board.back();
            BoardUI.getInstance().board.back();
            BoardUI.getInstance().repaint();
        }else{
            if(BoardUI.getInstance().otherPlayer.piece==BoardUI.getInstance().piece) {
                NetWork.getInstance().sendUndo();
            }
        }
    }
    public void admitDefeat() {
        if(BoardUI.getInstance().otherPlayer.isAI){
            Game.getInstance().end(-BoardUI.getInstance().ourPlayer.piece);
        }else{
            NetWork.getInstance().sendAdimitDefeat();
            Game.getInstance().end(-BoardUI.getInstance().ourPlayer.piece);
        }
    }

    public void proposeDraw() {
        if(BoardUI.getInstance().otherPlayer.isAI){
            if(BoardUI.getInstance().otherPlayer.AIacceptDraw()){
               NetWork.getInstance().popRemind("AI同意了你的提和请求");
               Game.getInstance().end(0);
            }else{
                NetWork.getInstance().popRemind("AI拒绝了你的提和请求");
            }
        }else{
            NetWork.getInstance().sendproposeDraw();
        }
    }
    public void Timechanger(){
        if(BoardUI.getInstance().otherPlayer.piece==BoardUI.getInstance().piece) {
            TimeCount.getOurCount().pauseTimer();
            TimeCount.getOtherCount().startTimer();
        }else{
            TimeCount.getOtherCount().pauseTimer();
            TimeCount.getOurCount().startTimer();
        }
    }
    void changeTime(){
        if(Objects.equals(DropdownPanel.getInstance1().getSelectedItem(), " 5分钟，单步20秒")){
            TimeCount.getOurCount().set(300,20);
            TimeCount.getOtherCount().set(300,20);
        }else if(Objects.equals(DropdownPanel.getInstance1().getSelectedItem(), "10分钟，单步30秒")){
            TimeCount.getOurCount().set(600,30);
            TimeCount.getOtherCount().set(600,30);
        }else{
            TimeCount.getOurCount().set(900,45);
            TimeCount.getOtherCount().set(900,45);
        }
    }
    public void chat(){
        if(ChatDialog.getInstance().isShowing()){
            ChatDialog.hideDialog();
        }else{
            ChatDialog.showDialog();
        }
    }

}

