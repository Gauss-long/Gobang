package ui;

import service.NetWork;

import javax.swing.*;
import java.awt.*;

public class ConnectionDialog extends JDialog {
    private static final ConnectionDialog instance = new ConnectionDialog();
    private ConnectionDialog(){
        super(FrameUI.getInstance(), "网络连接", false);
        Font font = new Font("楷体", Font.BOLD, 22);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // 垂直布局

        // IP 输入部分
        JPanel panel1 = new JPanel();
        JLabel label1 = new JLabel("IP:  ");
        label1.setFont(font);
        JTextField ipTf = new JTextField("localhost", 20);
        ipTf.setFont(font);
        panel1.add(label1);
        panel1.add(ipTf);

        // 端口输入部分
        JPanel panel2 = new JPanel();
        JLabel label2 = new JLabel("Port:");
        label2.setFont(font);
        JTextField portTf = new JTextField("2345", 20);
        portTf.setFont(font);
        panel2.add(label2);
        panel2.add(portTf);

        // 按钮部分
        JButton Button1 = new JButton("发起连接");
        Button1.setFont(font);
        Button1.setAlignmentX(Component.CENTER_ALIGNMENT); // 居中对齐
        Button1.addActionListener(e1 -> NetWork.getInstance().beginListen());

        JButton Button2 = new JButton("建立连接");
        Button2.setFont(font);
        Button2.setAlignmentX(Component.CENTER_ALIGNMENT); // 居中对齐
        Button2.addActionListener(e2-> NetWork.getInstance().connect(ipTf.getText(),Integer.parseInt(portTf.getText())));

        JButton Button3 = new JButton("返回菜单");
        Button3.setFont(font);
        Button3.setAlignmentX(Component.CENTER_ALIGNMENT);// 居中对齐
        Button3.addActionListener(e3 -> setVisible(false));

        // 添加组件到主面板
        panel.add(panel1);
        panel.add(panel2);
        panel.add(Box.createVerticalStrut(10)); // 添加固定间距
        panel.add(Button1);
        panel.add(Box.createVerticalStrut(10)); // 添加固定间距
        panel.add(Button2);
        panel.add(Box.createVerticalStrut(10)); // 添加固定间距
        panel.add(Button3);
        panel.add(Box.createVerticalGlue()); // 添加弹性空白

        // 配置对话框
        add(panel);
        pack();
    }
    public static ConnectionDialog getInstance(){
        return instance;
    }

}
