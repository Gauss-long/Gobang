package ui;


import service.Game;
import service.NetWork;
import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import static java.lang.Thread.*;


public class MenuUI extends JPanel {
    private  final Image backgroundImage= new ImageIcon(Objects.requireNonNull(getClass().getResource("/resource/背景.jpg"))).getImage();
    private static final MenuUI instance = new MenuUI();
    public static MenuUI getInstance(){return  instance;}
    public int AIHard = 0;
    private MenuUI() {
        // 使用 BoxLayout 布局
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false); // 设置透明以显示背景图片

        // 添加一些垂直间距使按钮整体居中
        add(Box.createVerticalGlue());

        // 创建按钮
        JButton button1 = new JButton("人人对战");
        JButton button2 = new JButton("人机对战");
        JButton button3 = new JButton("设置");
        JButton button4 = new JButton("帮助");

        Font font = new Font("楷体", Font.BOLD, 22);
        Color buttonColor = new Color(210, 180, 140);

        setupButton(button1, font, buttonColor);
        setupButton(button2, font, buttonColor);
        setupButton(button3, font, buttonColor);
        setupButton(button4, font, buttonColor);

        // 设置按钮功能
        button1.addActionListener(e -> {
            ConnectionDialog  dialog = ConnectionDialog.getInstance();
            dialog.setLocationRelativeTo(FrameUI.getInstance());
            dialog.setVisible(true);
            new Thread(() -> {
                try {
                    while (!NetWork.getInstance().isConnected()) {
                        sleep(100); // 每100ms检测连接
                    }
                    // 连接成功后关闭 dialog，并启动游戏
                    SwingUtilities.invokeLater(() -> {
                        if (dialog.isVisible()) { // 确保 dialog 尚未被手动关闭
                            dialog.dispose();
                            Game.getInstance().Start();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();

        });

        button2.addActionListener(e -> Game.getInstance().Start());
        button3.addActionListener(e -> new Thread(()->{
                SettingDialog dialog = SettingDialog.getInstance();
                dialog.setLocationRelativeTo(FrameUI.getInstance());
                dialog.setVisible(true);
        }).start());
        button4.addActionListener(e -> JOptionPane.showMessageDialog(this, "该项目源代码已上传github:www.github.com/86915。\n如有疑问欢迎联系作者，qq:869150368"));

        // 添加按钮到面板
        add(button1);
        add(Box.createVerticalStrut(30));
        add(button2);
        add(Box.createVerticalStrut(30));
        add(button3);
        add(Box.createVerticalStrut(30));
        add(button4);
        // 添加垂直间距
        add(Box.createVerticalGlue());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
    private void setupButton(JButton button, Font font, Color color) {
        button.setBackground(color);
        button.setFont(font);
        button.setPreferredSize(new Dimension(150, 100));
        button.setMaximumSize(new Dimension(150, 100));
        button.setMinimumSize(new Dimension(150, 100));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

}

