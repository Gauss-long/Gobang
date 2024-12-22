package ui;

import service.Music;

import javax.swing.*;
import java.awt.*;

public class FrameUI extends JFrame {
    private static final FrameUI instance = new FrameUI("五子棋");
    public  CardLayout cardLayout = new CardLayout();
    public   JPanel mainPanel = new JPanel(cardLayout);
    private FrameUI(String s){
        super(s);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 900);

        // 创建菜单和棋盘界面
        MenuUI menuUI = MenuUI.getInstance();

        BoardUI boardUI = BoardUI.getInstance();

        // 将界面添加到 CardLayout 中
        mainPanel.add(menuUI, "Menu");
        mainPanel.add(boardUI, "Board");
        //播放音乐
        Music.getInstance().play(0);

        // 设置主面板到窗口中
        add(mainPanel);
        setVisible(true);
    }

    public static FrameUI getInstance(){
        return instance;
    }
}
