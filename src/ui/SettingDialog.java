package ui;

import model.DropdownPanel;
import model.MySlider;
import service.Music;
import service.TimeCount;

import javax.swing.*;
import java.util.Objects;

public class SettingDialog extends javax.swing.JDialog{
    private static final SettingDialog instance = new SettingDialog();
    SettingDialog(){
        super(FrameUI.getInstance(), "设置", true);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        MySlider slider1 = MySlider.getInstance1();
        slider1.leftLabel.setText("音乐:");
        slider1.valueLabel.addPropertyChangeListener(evt -> {
            int k = Integer.parseInt(slider1.valueLabel.getText());
            Music.getInstance().setVolume(0, cal(k));
            Music.getInstance().setVolume(1, cal(k));
        });
        MySlider slider2 = MySlider.getInstance2();
        slider2.leftLabel.setText("音效:");
        slider2.valueLabel.addPropertyChangeListener(evt -> {
            int k = Integer.parseInt(slider2.valueLabel.getText());
            Music.getInstance().setVolume(2, cal(k));
            Music.getInstance().setVolume(3, cal(k));
        });
        DropdownPanel dropdownPanel1 = DropdownPanel.getInstance1();
        dropdownPanel1.label.setText("局时: ");
        dropdownPanel1.comboBox.addItem(" 5分钟，单步20秒");
        dropdownPanel1.comboBox.addItem("10分钟，单步30秒");
        dropdownPanel1.comboBox.addItem("15分钟，单步45秒");
        dropdownPanel1.comboBox.addActionListener(e1 -> changeTime());
        DropdownPanel dropdownPanel2 = DropdownPanel.getInstance2();
        dropdownPanel2.label.setText("AI难度");
        dropdownPanel2.comboBox.addItem("简单");
        dropdownPanel2.comboBox.addItem("困难");
        dropdownPanel2.comboBox.addActionListener(e2->changeHard());
        add(slider1);
        add(slider2);
        add(Box.createVerticalStrut(10));
        add(dropdownPanel1);
        add(Box.createVerticalStrut(10));
        add(dropdownPanel2);
        pack();
        setLocationRelativeTo(FrameUI.getInstance());
        setVisible(true);
    }
    public static SettingDialog getInstance(){return instance;}
    private void changeTime(){
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
    private void changeHard(){
        if(Objects.equals(DropdownPanel.getInstance2().getSelectedItem(), "简单")){
            MenuUI.getInstance().AIHard=0;
        }else{
            MenuUI.getInstance().AIHard=1;
        }
    }
    private float cal(int k){
        return (float) (-80 + Math.pow(k / 100.0, 0.1033) * 86.0206);
    }
}
