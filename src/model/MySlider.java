package model;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;

public class MySlider extends JPanel {
    private final JSlider slider;
    public JLabel valueLabel;
    public JLabel leftLabel;
    private static final MySlider instance1 = new MySlider();
    private static final MySlider instance2 = new MySlider();
    public static MySlider getInstance1(){return instance1;}
    public static MySlider getInstance2(){return instance2;}
    public MySlider() {
        setLayout(new BorderLayout());

        // 创建左侧的 JLabel
        leftLabel = new JLabel("Left Label");
        leftLabel.setFont(new Font("楷体", Font.BOLD, 16));
        leftLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // 创建 Slider
        slider = new JSlider(0, 100, 50);
        slider.setOpaque(false); // 设置透明背景
        slider.setMajorTickSpacing(20);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setFont(new Font("Arial", Font.BOLD, 12));

        // 自定义 Slider 外观
        slider.setUI(new BasicSliderUI(slider) {
            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(100, 150, 255)); // 滑块颜色
                g2d.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);

                // 添加外边框
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
            }

            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 清理背景
                g2d.setColor(slider.getParent().getBackground());
                g2d.fillRect(trackRect.x - 10, trackRect.y - 10, trackRect.width + 20, trackRect.height + 20);

                // 背景轨道
                g2d.setColor(new Color(220, 220, 220));
                g2d.fillRoundRect(trackRect.x, trackRect.y + trackRect.height / 3, trackRect.width, trackRect.height / 3, 10, 10);

                // 填充已滑过的轨道
                int filledWidth = (int) (((double) slider.getValue() / slider.getMaximum()) * trackRect.width);
                g2d.setColor(new Color(100, 150, 255));
                g2d.fillRoundRect(trackRect.x, trackRect.y + trackRect.height / 3, filledWidth, trackRect.height / 3, 10, 10);
            }

            @Override
            public void paintFocus(Graphics g) {
                // 禁用焦点绘制
            }
        });

        // 添加标签来显示滑块的当前值
        valueLabel = new JLabel(slider.getValue() + "");
        valueLabel.setFont(new Font("楷体", Font.BOLD, 16));
        valueLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // 添加滑块值改变事件
        slider.addChangeListener(e -> valueLabel.setText(slider.getValue()+""));

        // 布局组件
        add(leftLabel, BorderLayout.WEST);
        add(slider, BorderLayout.CENTER);
        add(valueLabel, BorderLayout.EAST);
    }

}
