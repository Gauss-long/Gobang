package model;
import javax.swing.*;
import java.awt.*;

public class DropdownPanel extends JPanel {
    private static final DropdownPanel instance1 = new DropdownPanel();
    private static final DropdownPanel instance2 = new DropdownPanel();
    public static DropdownPanel getInstance1(){return instance1;}
    public static DropdownPanel getInstance2(){return instance2;}
    public JComboBox<String> comboBox;
    public JLabel label = new JLabel(" ");
    public DropdownPanel() {
        setLayout(new FlowLayout()); // 使用 BorderLayout

        // 初始化下拉菜单
        comboBox = new JComboBox<>();
        comboBox.setFont(new Font("楷体", Font.BOLD, 18));
        comboBox.setForeground(Color.BLACK);
        comboBox.setBackground(Color.WHITE);

        label.setFont(new Font("楷体", Font.BOLD, 18));
        // 添加下拉菜单到面板
        add(label);
        add(comboBox);
    }

    // 方法：添加条目
    public void addItem(String item) {
        comboBox.addItem(item);
    }

    // 方法：获取选中条目
    public String getSelectedItem() {
        return (String) comboBox.getSelectedItem();
    }

    // 主函数：测试自定义下拉菜单
    public static void main(String[] args) {
        JFrame frame = new JFrame("自定义下拉菜单");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        // 创建自定义下拉菜单面板
        DropdownPanel dropdownPanel = new DropdownPanel();

        // 添加自定义条目
        dropdownPanel.addItem("选项 A");
        dropdownPanel.addItem("选项 B");
        dropdownPanel.addItem("选项 C");


        // 将面板添加到窗口
        frame.add(dropdownPanel);

        frame.setVisible(true);
    }
}

