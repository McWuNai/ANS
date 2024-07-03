package cn.wunai.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class UseAllpcm {
    public static String getPcmOutPath() {
        return PcmOutPath;
    }
    private static String PcmOutPath= "";
    public static void Allpcm(List<String> Pcm, String outputPath) throws IOException {
        // 创建一个新的PCM文件，作为最终的拼接结果。可以使用`FileOutputStream`类来创建一个新的文件。
        FileOutputStream fos = new FileOutputStream(outputPath);
        //打开每个PCM文件并读取其数据。你可以使用`FileInputStream`类来打开每个PCM文件。
        for (String inputFile : Pcm) {
            FileInputStream fis = new FileInputStream(inputFile);

            // 读取数据
            byte[] buffer = new byte[4096];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                // 将数据写入到输出文件
                fos.write(buffer, 0, len);
            }
            PcmOutPath = outputPath;
            // 关闭输入流
            fis.close();
        }
        //关闭输出流。
// 关闭输出流
        fos.close();
    }
}
