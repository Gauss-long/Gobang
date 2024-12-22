package ui;

import service.NetWork;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChatDialog extends JDialog {
    private final JTextPane chatArea; // 聊天记录区域
    private final JTextField inputField; // 消息输入框
    private final List<String> chatHistory; // 聊天记录
    private static final ChatDialog instance = new ChatDialog();
    private static final Font fontMessage = new Font("楷体", Font.BOLD, 20); // 消息字体
    private static final Font fontTimestamp = new Font("Consolas", Font.PLAIN, 12); // 时间戳字体

    public static ChatDialog getInstance() {
        return instance;
    }

    public ChatDialog() {
        super(FrameUI.getInstance(), "聊天", false); // 设置为非模态对话框
        setSize(400, 300);
        setLocationRelativeTo(FrameUI.getInstance());
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // 初始化聊天记录
        chatHistory = new ArrayList<>();

        // 聊天记录区域
        chatArea = new JTextPane();
        chatArea.setEditable(false); // 禁止编辑聊天记录
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        // 输入区域
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        // 发送按钮
        JButton sendButton = new JButton("发送");

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // 发送按钮监听器
        sendButton.addActionListener(e -> sendMessage());

        // 回车键发送消息
        inputField.addActionListener(e -> sendMessage());
    }

    // 发送消息
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            String timestamp = getCurrentTimestamp();
            chatHistory.add("我: " + message); // 添加到聊天记录
            updateChatArea("我: " + message, timestamp);
            NetWork.getInstance().sendChat(message);
            inputField.setText(""); // 清空输入框
        }
    }

    // 接收消息
    public void receive(String message) {
        String timestamp = getCurrentTimestamp();
        chatHistory.add("对方: " + message); // 添加到聊天记录
        updateChatArea("对方: " + message, timestamp);
        showDialog();
    }

    // 获取当前时间戳
    private String getCurrentTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    // 更新聊天记录区域
    private void updateChatArea(String message, String timestamp) {
        StyledDocument doc = chatArea.getStyledDocument();

        // 添加消息文本
        try {
            Style messageStyle = chatArea.addStyle("MessageStyle", null);
            StyleConstants.setFontFamily(messageStyle, fontMessage.getFamily());
            StyleConstants.setFontSize(messageStyle, fontMessage.getSize());
            doc.insertString(doc.getLength(), message, messageStyle);

            // 添加时间戳
            Style timestampStyle = chatArea.addStyle("TimestampStyle", null);
            StyleConstants.setFontFamily(timestampStyle, fontTimestamp.getFamily());
            StyleConstants.setFontSize(timestampStyle, fontTimestamp.getSize());
            StyleConstants.setForeground(timestampStyle, Color.GRAY);
            doc.insertString(doc.getLength(), " (" + timestamp + ")\n", timestampStyle);

        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        // 滚动到最新消息
        chatArea.setCaretPosition(doc.getLength());
    }

    // 显示对话框
    public static void showDialog() {
        SwingUtilities.invokeLater(() -> instance.setVisible(true));
    }

    public static void hideDialog() {
        SwingUtilities.invokeLater(() -> instance.setVisible(false));
    }

    public void clear() {
        chatHistory.clear();
        chatArea.setText(""); // 清空聊天区域
    }
}
