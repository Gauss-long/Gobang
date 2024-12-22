package service;


import model.Board;
import model.DropdownPanel;
import ui.BoardUI;
import ui.ChatDialog;
import ui.FrameUI;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;


public class NetWork {
    private ServerSocket ss ;
    private Socket s;
    private BufferedReader in;
    private PrintStream out;
    public static final int PORT = 2345;
    public void beginListen(){
        new Thread(() -> {
            try {
                ss = new ServerSocket(PORT);
                System.out.println("服务器已启动，监听端口：" + PORT);
                s = ss.accept();
                System.out.println("客户端已连接：" + s.getInetAddress());
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                out = new PrintStream(s.getOutputStream(),true);
                startReadThread();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }
    public void connect(String ip,int port){
        try {
            s = new Socket(ip,port);
            //System.out.println();
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintStream(s.getOutputStream(),true);
            startReadThread();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    protected void startReadThread() {
        new Thread(() -> {
            String line;
            try {
                while(s != null && in != null && !s.isClosed() &&in!=null&&(line=in.readLine())!=null){
                    parseMessage(line);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }).start();
    }
    protected void parseMessage(String line) {
        // chess:3,15
        // chat:hehehehehe
        if(line.startsWith("chess:")){
            parseChess(line.substring(6));
        }else if(line.startsWith("chat:")){
            ChatDialog.getInstance().receive(line.substring(5));
        }else if(line.startsWith("undo")){
            int response = JOptionPane.showConfirmDialog(
                    FrameUI.getInstance(),                 // 父组件
                    "对方请求悔棋，是否同意",      // 提示信息
                    "悔棋请求",                // 对话框标题
                    JOptionPane.YES_NO_OPTION // 按钮类型：是/否
            );

            // 根据用户的选择执行不同的操作
            if (response == JOptionPane.YES_OPTION) {
                sendYesUndo();
                Board.getInstance().back();
                BoardUI.getInstance().Switch();
                BoardUI.getInstance().repaint();
            } else {
                sendNOUndo();
            }
        }else if(line.startsWith("yesundo")){
            popRemind("对方同意了你的悔棋请求！");
            Board.getInstance().back();
            BoardUI.getInstance().Switch();
            BoardUI.getInstance().repaint();
        }else if(line.startsWith("noundo")){
            popRemind("对方拒绝了你的悔棋请求！");
        }else if(line.startsWith("admitdefeat")){
            popRemind("对方认输");
            Game.getInstance().end(-BoardUI.getInstance().otherPlayer.piece);
        }else if(line.startsWith("proposedraw")){
            int response = JOptionPane.showConfirmDialog(
                    FrameUI.getInstance(),                 // 父组件
                    "对方请求和棋，是否同意",      // 提示信息
                    "和棋请求",                // 对话框标题
                    JOptionPane.YES_NO_OPTION // 按钮类型：是/否
            );
            // 根据用户的选择执行不同的操作
            if (response == JOptionPane.YES_OPTION) {
                sendYesDraw();
                Game.getInstance().end(0);
            } else {
                sendNoDraw();
            }
        }else if(line.startsWith("yesdraw")){
            popRemind("对方同意和棋");
            Game.getInstance().end(0);
        }else if(line.startsWith("nodraw")){
            popRemind("对方拒绝和棋");
        }else if(line.startsWith("1")){
            TimeCount.getOurCount().set(300,20);
            TimeCount.getOtherCount().set(300,20);
        }else if(line.startsWith("2")){
            TimeCount.getOurCount().set(600,30);
            TimeCount.getOtherCount().set(600,30);
        }else if(line.startsWith("3")){
            TimeCount.getOurCount().set(900,45);
            TimeCount.getOtherCount().set(900,45);
        }
    }
    private void parseChess(String msg) {
        // 3,15
        String[] a = msg.split(",");
        int row = Integer.parseInt(a[0]);
        int col = Integer.parseInt(a[1]);
        int piece = Integer.parseInt(a[2]);
        Board.getInstance().forward(row,col,piece);
        System.out.println("接收成功"+row+","+col+","+piece);
        Music.getInstance().play(3);
        BoardUI.getInstance().repaint();
        BoardUI.getInstance().Switch();
    }
    //发送信息
    public void sendChess(int row, int col,int piece) {
        if(s!=null){
            out.println("chess:"+row+","+col+","+piece);
            System.out.println("发送成功:chess:"+row+","+col+","+piece);
        }
    }
    public void sendChat(String line){
        if(s!=null)out.println("chat:"+line);
    }
    public void sendUndo(){
        if(s!=null)out.println("undo");
    }
    public void sendYesUndo(){
        if(s!=null)out.println("yesundo");
    }
    public void sendNOUndo(){
        if(s!=null)out.println("noundo");
    }
    public void sendAdimitDefeat(){
        if(s!=null)out.println("admitdefeat");
    }
    public void sendproposeDraw(){
        if(s!=null)out.println("proposedraw");
    }
    public void sendYesDraw(){
        if(s!=null)out.println("yesdraw");
    }
    public void sendNoDraw(){
        if(s!=null)out.println("nodraw");
    }
    public void sendGameTime(){
        if(s!=null){
            if(Objects.equals(DropdownPanel.getInstance1().getSelectedItem(), "15分钟，单步45秒")){
                out.println("3");
            }else if(Objects.equals(DropdownPanel.getInstance1().getSelectedItem(), "10分钟，单步30秒")){
                out.println("2");
            }else{
                out.println("1");
            }
        }
    }


    public void close()  {
        try{if(isSS())
            ss.close();}
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        try{s.close();}
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("断开连接");
    }
    public boolean isConnected() {
        return s != null && s.isConnected() && !s.isClosed()&& !s.isOutputShutdown();
    }

    private static final NetWork instance = new NetWork();
    private NetWork(){}
    public static NetWork getInstance() {
        return instance;
    }
    public boolean isSS(){
        return ss != null;
    }
    public boolean isS(){
        return s != null;
    }
    public String getLocalIP(){
        return s.getLocalAddress().toString();
    }
    public String getInetIP(){
        return s.getInetAddress().toString();
    }
    public void popRemind(String s){
        JOptionPane optionPane = new JOptionPane(s, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = optionPane.createDialog("提醒");
        dialog.setLocationRelativeTo(FrameUI.getInstance());
        dialog.setVisible(true);
        int delay = 500;  //
        // 使用 Timer 设置自动关闭
        Timer timer = new Timer(delay, e -> {
            dialog.dispose();  // 关闭对话框
        });
        timer.setRepeats(false);  // 只执行一次
        timer.start();
    }
}