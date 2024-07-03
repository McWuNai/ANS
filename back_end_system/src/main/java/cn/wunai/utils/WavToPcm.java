package cn.wunai.utils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.FileOutputStream;

import static cn.wunai.WuNaiApplication.FilePath;
import static cn.wunai.utils.pcmToMp3.convertAudioFiles;

public class WavToPcm {
    public static String getWavtoPcmPath() {
        return WavtoPcmPath;
    }

    public static void setWavtoPcmPath(String wavtoPcmPath) {
        WavtoPcmPath = wavtoPcmPath;
    }

    private static String WavtoPcmPath;
    public static void WavtoPcm(String wavfile, String pcmfile) {
        setWavtoPcmPath(pcmfile);
        try {
// 读取wav文件
            File wavFile = new File(wavfile);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavFile);

// 获取音频格式
            AudioFormat audioFormat = audioInputStream.getFormat();

// 创建输出的pcm文件
            File pcmFile = new File(pcmfile);
            FileOutputStream outputStream = new FileOutputStream(pcmFile);

// 缓冲区大小
            int bufferSize = 4096;
            byte[] buffer = new byte[bufferSize];

// 转换为pcm格式并写入文件
            AudioInputStream pcmStream = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_UNSIGNED, audioInputStream);
            int bytesRead;
            while ((bytesRead = pcmStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

// 关闭流
            pcmStream.close();
            outputStream.close();
            audioInputStream.close();

            System.out.println("WavToPcm转换成功！");
            convertAudioFiles(getWavtoPcmPath(), FilePath + System.currentTimeMillis() + ".mp3");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
