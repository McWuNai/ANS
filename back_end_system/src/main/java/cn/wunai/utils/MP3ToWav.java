package cn.wunai.utils;

import javax.sound.sampled.*;
import java.io.File;

public class MP3ToWav {
    public static String convert(String mp3FilePath, String wavFilePath) {
        try {
// 创建输入和输出音频文件流
            AudioInputStream mp3Stream = AudioSystem.getAudioInputStream(new File(mp3FilePath));
            AudioInputStream pcmStream = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, mp3Stream);
            AudioInputStream wavStream = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, pcmStream);

// 将PCM流写入到WAV文件
            AudioSystem.write(wavStream, AudioFileFormat.Type.WAVE, new File(wavFilePath));

// 关闭打开的流
            mp3Stream.close();
            pcmStream.close();
            wavStream.close();

            System.out.println("转换成功");
        } catch (Exception e) {
            System.out.println("转换失败： " + e.getMessage());
        }
        return wavFilePath;
    }
}
