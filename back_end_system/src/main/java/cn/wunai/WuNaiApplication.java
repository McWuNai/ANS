package cn.wunai;
import cn.wunai.utils.CreateSaveFile;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class WuNaiApplication {
    static String applicationPath = System.getProperty("user.dir");
    public static String FilePath = applicationPath + File.separator + "Out" + File.separator;
    public static String PCMFilePath = applicationPath + File.separator + "Out" + File.separator + "PCM" + File.separator;
    public static String WAVFilePath = applicationPath + File.separator + "Out" + File.separator + "WAV" + File.separator;

    public static void main(String[] args) {
        CreateSaveFile.CreateFile(FilePath);
        SpringApplication.run(WuNaiApplication.class, args);

    }

}
