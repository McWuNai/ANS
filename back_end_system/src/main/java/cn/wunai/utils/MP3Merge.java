package cn.wunai.utils;

import cn.wunai.WuNaiApplication;
import cn.wunai.service.WebTtsWs;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;

import static cn.wunai.utils.pcmToMp3.convertAudioFiles;

public class MP3Merge {
    private static String Url;

    public static void setUrl(String url) {
        Url = url;
    }

    public static String getUrl() {
        return Url;
    }

    public static String OUTPUT_FILE_PATH_WAV = WuNaiApplication.WAVFilePath + System.currentTimeMillis() + ".wav";

    public static void Merge(String Target, String BGM) {
        CreateSaveFile.CreateFile(WuNaiApplication.WAVFilePath);
//创建输出路径
        try {
// 加载两个MP3文件
            AudioInputStream clip1 = AudioSystem.getAudioInputStream(new File(MP3ToWav.convert(Target, OUTPUT_FILE_PATH_WAV)));
            AudioInputStream clip2 = AudioSystem.getAudioInputStream(new File(MP3ToWav.convert(BGM, OUTPUT_FILE_PATH_WAV)));

// 获取两个clip的音频格式
            AudioFormat format1 = clip1.getFormat();
            AudioFormat format2 = clip2.getFormat();

// 确保两个clip的音频格式一致
            if (!format1.matches(format2)) {
                setUrl("音频格式不一致");
                throw new IOException("音频格式不一致");
            }

// 创建一个混合的音频流
            AudioInputStream mixedAudio = new AudioInputStream(
                    new SequenceInputStream(clip1, clip2), format1,
                    clip1.getFrameLength() + clip2.getFrameLength());

// 保存混合后的音频流
            AudioSystem.write(mixedAudio, AudioFileFormat.Type.WAVE, new File(OUTPUT_FILE_PATH_WAV));
            WavToPcm.WavtoPcm(OUTPUT_FILE_PATH_WAV, WuNaiApplication.PCMFilePath + System.currentTimeMillis() + ".pcm");
            convertAudioFiles(WavToPcm.getWavtoPcmPath(), WebTtsWs.OUTPUT_FILE_PATH_MP3);
            setUrl(WebTtsWs.OUTPUT_FILE_PATH_MP3);
            System.out.println("MP3文件融合成功！,保存位置为: " + Url);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

}
