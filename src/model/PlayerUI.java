package model;

import javax.swing.*;
import java.awt.*;

public class PlayerUI extends JPanel {
    Icon icon;
    Player player;
    JLabel imgLabel;
    public JPanel panel;
    JLabel label1;
    JLabel label2;
    public PlayerUI(ImageIcon imgIcon, Player player){
        Font font = new Font("楷体",Font.BOLD,22);
        Color color = new Color(204,217,255);
        setBackground(color);

        Image img = imgIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        this.icon=new ImageIcon(img);
        this.player = player;
        setLayout(new GridLayout(1,2));
        imgLabel = new JLabel(this.icon);
        add(imgLabel);
        panel = new JPanel();
        panel.setLayout(new GridLayout(3,1));

        label1 = new JLabel("执棋:"+player.name);
        label2 = new JLabel("用户:"+player.id);
        label1.setFont(font);
        label1.setBackground(color);
        label1.setOpaque(true);
        label2.setFont(font);
        label2.setBackground(color);
        label2.setOpaque(true);

        panel.add(label1);
        panel.add(label2);
        add(panel);
        setVisible(true);
    }

    public void reset(ImageIcon imgIcon,Player player) {
        this.player = player;
        Image img = imgIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        this.icon = new ImageIcon(img);
        this.imgLabel.setIcon(icon);
        this.label1.setText("执棋:"+player.name);
        this.label2.setText("用户:"+player.id);
        this.revalidate();
        this.repaint();
    }

}
