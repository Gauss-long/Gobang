package service;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.util.Objects;

public class Music {
    private static final Music instance = new Music();
    private final Clip[] clips = new Clip[4];   // 用于存储音频文件的 Clip 对象
    private final boolean[] isPlaying = new boolean[4]; // 用于标记每个音频是否正在播放
    private final boolean[] hasEnded = new boolean[4];  // 用于标记每个音频是否播放结束
    private final FloatControl[] volumeControls = new FloatControl[4]; // 用于存储每个音频的音量控制对象

    // 私有构造函数确保单例模式
    private Music() {
        initializeClips();
    }

    // 获取 Music 类的单例对象
    public static synchronized Music getInstance() {
        return instance;
    }

    // 初始化所有剪辑
    private void initializeClips() {
        String[] audioFilePaths = {
                "/resource/music/music1.wav",
                "/resource/music/music2.wav",
                "/resource/music/music3.wav",
                "/resource/music/music4.wav"
        };

        for (int i = 0; i < clips.length; i++) {
            try {
                // 将资源流包装成支持 mark/reset 的流
                BufferedInputStream bufferedIn = new BufferedInputStream(
                        Objects.requireNonNull(getClass().getResourceAsStream(audioFilePaths[i]))
                );
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);

                clips[i] = AudioSystem.getClip();
                clips[i].open(audioIn);

                // 获取每个音频的音量控制对象
                volumeControls[i] = (FloatControl) clips[i].getControl(FloatControl.Type.MASTER_GAIN);

                // 添加音频结束的监听器
                final int index = i;
                clips[i].addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        hasEnded[index] = true;
                        isPlaying[index] = false; // 播放结束，更新状态
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 播放指定索引的音频
    public void play(int index) {
        if (index >= 0 && index < clips.length && clips[index] != null) {
            if (!isPlaying[index] || hasEnded[index]) { // 如果当前音频没有播放，或者已经结束
                new Thread(() -> {
                    clips[index].setFramePosition(0); // 重置到起始位置
                    clips[index].start();
                    isPlaying[index] = true;
                    hasEnded[index] = false; // 重置音频结束状态
                }).start(); // 在后台线程中播放音乐
            }
        }
    }

    // 停止指定索引的音频并重置到开头
    public void stop(int index) {
        if (index >= 0 && index < clips.length && clips[index] != null) {
            clips[index].stop();
            clips[index].setFramePosition(0); // 重置到起始位置
            isPlaying[index] = false;
            hasEnded[index] = false; // 重置音频结束状态
        }
    }

    // 关闭所有资源
    public void close() {
        for (Clip clip : clips) {
            if (clip != null && clip.isRunning()) {
                clip.stop();
                clip.close();
            }
        }
    }

    // 设置指定音频的音量 (范围: -80.0 到 6.0206)
    public void setVolume(int index, float volume) {
        if (index >= 0 && index < volumeControls.length && volumeControls[index] != null) {
            new Thread(() -> {
                // 设置音量，确保音量在允许的范围内
                float newVolume = Math.max(Math.min(volume, volumeControls[index].getMaximum()), volumeControls[index].getMinimum());
                volumeControls[index].setValue(newVolume);
            }).start();
        }
    }

}

