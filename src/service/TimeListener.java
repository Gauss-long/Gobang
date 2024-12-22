package service;

import ui.BoardUI;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TimeListener {
    private static final TimeListener instance = new TimeListener();
    private Thread currentThread;
    private final AtomicBoolean running = new AtomicBoolean(false);

    private TimeListener() {}

    public static TimeListener getInstance() {
        return instance;
    }

    @Override
    protected void finalize() throws Throwable {
        stopListening();
        super.finalize();
    }

    /**
     * 监听器线程的主要逻辑。
     */
    private final Runnable listenerTask = () -> {
        while (running.get()) {
            if (TimeCount.getOurCount().outOfTime()) {
                if (BoardUI.getInstance().otherPlayer.piece == 1) {
                    Game.getInstance().end(2);
                } else {
                    Game.getInstance().end(-2);
                }
            }
            if (TimeCount.getOtherCount().outOfTime()) {
                if (BoardUI.getInstance().otherPlayer.piece == 1) {
                    Game.getInstance().end(-2);
                } else {
                    Game.getInstance().end(2);
                }
            }
            if(TimeCount.getOurCount().currentStepTime<=5){
                TimeCount.getOurCount().setLabel2(Color.RED);
            }else{
                TimeCount.getOurCount().setLabel2(Color.BLACK);
            }
            // 防止CPU占用过高，适当休眠一段时间
            try {
                Thread.sleep(100); // 每次循环休眠100毫秒
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 恢复中断状态
                break; // 退出循环
            }
        }
        System.out.println("TimeListener thread stopped.");
    };

    /**
     * 启动监听器线程。
     */
    public synchronized void startListening() {
        if (currentThread == null || !currentThread.isAlive()) {
            running.set(true);
            currentThread = new Thread(listenerTask);
            currentThread.start();
        }
    }

    /**
     * 停止监听器线程。
     */
    public synchronized void stopListening() {
        if (currentThread != null && currentThread.isAlive()) {
            running.set(false);
            currentThread.interrupt(); // 中断线程以确保它尽快退出
            try {
                currentThread.join(); // 等待线程结束
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 恢复主线程的中断状态
            }
            currentThread = null; // 清除对已终止线程的引用
        }
    }
}

