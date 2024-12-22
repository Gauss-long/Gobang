package ui;

import model.Board;
import service.Game;
import service.Review;

import javax.swing.*;
import java.awt.*;

public class ButtonPanel extends JPanel {
    private static final ButtonPanel instance = new ButtonPanel();
    Review review = Review.getInstance();
    Game game = Game.getInstance();
    Font font = new Font("楷体", Font.BOLD, 20);
    Color color = new Color(210, 180, 140);
    Dimension dimension = new Dimension(120,50);
    public JButton undoButton = new JButton("悔棋");
    public JButton admitDefeatButton = new JButton("认输");
    public JButton drawButton = new JButton("提和");
    public JButton goButton = new JButton("->");
    public JButton backButton = new JButton("<-");
    public JButton quitButton = new JButton("返回菜单");
    public JButton chatButton = new JButton("聊天");

    private ButtonPanel(){
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(150, 77, 21));
        setPreferredSize(new Dimension(150, Board.getInstance().getCanvasSize()));


        add(Box.createVerticalStrut(150));
        setButton(undoButton,color,font,dimension);
        undoButton.addActionListener(e->game.undo());
        add(undoButton);
        add(Box.createVerticalStrut(50));

        setButton(admitDefeatButton,color,font,dimension);
        admitDefeatButton.addActionListener(e -> game.admitDefeat());
        add(admitDefeatButton);
        add(Box.createVerticalStrut(50));

        setButton(drawButton,color,font,dimension);
        drawButton.addActionListener(e -> game.proposeDraw());
        add(drawButton);
        add(Box.createVerticalStrut(50));

        setButton(chatButton,color,font,dimension);
        chatButton.addActionListener(e->game.chat());
        add(chatButton);
        add(Box.createVerticalStrut(50));

        JPanel panel = new JPanel();
        panel.setPreferredSize(dimension);
        panel.setOpaque(true);
        panel.setBackground(new Color(150, 77, 21));
        setButton(goButton,color,font,new Dimension(60,50));
        setButton(backButton,color,font,new Dimension(60,50));
        goButton.addActionListener(e -> review.goStep());
        backButton.addActionListener(e-> review.backStep());
        panel.add(backButton);
        panel.add(goButton);
        add(panel);
        add(Box.createVerticalStrut(50));

        setButton(quitButton,color,font,dimension);
        quitButton.addActionListener(e -> review.quit());
        add(quitButton);
        add(Box.createVerticalStrut(150));
    }
    void setButton(JButton button,Color color,Font font,Dimension dimension){
        button.setBackground(color);
        button.setFont(font);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(dimension);
        button.setMaximumSize(dimension);
        button.setMinimumSize(dimension);
    }
    public static ButtonPanel getInstance(){
        return instance;
    }
}
