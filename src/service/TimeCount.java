package service;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class TimeCount extends JPanel {
    private static final TimeCount ourCount = new TimeCount();
    private static final TimeCount otherCount = new TimeCount();

    public static TimeCount getOurCount() {
        return ourCount;
    }

    public static TimeCount getOtherCount() {
        return otherCount;
    }

    private int totalTime = 300; // 总时间（以秒为单位）
    private int stepTime = 20;  // 步时间（以秒为单位）
    public int currentStepTime = stepTime;
    private final JLabel label1 = new JLabel(); // 显示总时间

    public void setLabel2(Color color) {
        this.label2.setForeground(color);
    }
    private final JLabel label2 = new JLabel(); // 显示步时间

    private Timer timer;
    private boolean isPaused = true;
    public void set(int a,int b){
        totalTime=a;
        stepTime=b;
        currentStepTime=stepTime;
    }
    private TimeCount() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBackground(new Color(204,217,255));
        // 初始化标签
        Font font = new Font("楷体", Font.BOLD, 22);
        label1.setFont(font);
        label2.setFont(font);
        updateLabels();

        JLabel totalLabel = new JLabel("总时");
        totalLabel.setFont(font);
        JLabel stepLabel = new JLabel("步时");
        stepLabel.setFont(font);
        add(totalLabel);
        add(label1);
        add(stepLabel);
        add(label2);
    }

    // 更新显示的时间（格式为 mm:ss）
    private void updateLabels() {
        label1.setText(formatTime(totalTime));
        label2.setText(formatTime(currentStepTime));
    }

    // 格式化时间为 mm:ss 格式
    private String formatTime(int timeInSeconds) {
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // 开始计时
    public void startTimer() {
        if (timer != null) {
            timer.cancel();
        }
        isPaused = false;

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isPaused) {
                    return;
                }

                // 总时间递减
                if (totalTime > 0) {
                    totalTime--;
                }

                // 步时间递减
                if (currentStepTime > 0) {
                    currentStepTime--;
                }
                updateLabels();

                // 如果总时间耗尽，停止计时器
                if (totalTime <= 0) {
                    stopTimer();
                }
            }
        }, 0, 1000); // 每 1 秒更新一次
    }

    // 暂停计时
    public void pauseTimer() {
        isPaused = true;
        if (timer != null) {
            timer.cancel();
        }

        // 重置步时间
        currentStepTime = Math.min(stepTime, totalTime);
        updateLabels();
    }

    // 停止计时
    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        isPaused = true;
        updateLabels();
    }
    public boolean outOfTime(){
        return totalTime<=0||currentStepTime<=0;
    }

    // 主函数：演示计时功能
    public static void main(String[] args) {
        JFrame frame = new JFrame("倒计时演示");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        TimeCount timeCount = TimeCount.getOurCount();

        // 创建按钮
        JButton startButton = new JButton("开始");
        JButton pauseButton = new JButton("暂停");
        JButton resetButton = new JButton("重置");

        // 按钮事件
        startButton.addActionListener(e -> timeCount.startTimer());
        pauseButton.addActionListener(e -> timeCount.pauseTimer());
        resetButton.addActionListener(e -> {
            timeCount.totalTime = 600;
            timeCount.currentStepTime = 10;
            timeCount.updateLabels();
        });

        // 添加组件到面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(resetButton);

        frame.setLayout(new BorderLayout());
        frame.add(timeCount, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}



