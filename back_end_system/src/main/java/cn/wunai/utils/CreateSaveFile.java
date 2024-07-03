package cn.wunai.utils;

import java.io.File;

public class CreateSaveFile {
    public static void CreateFile(String folderPath) {
        // 创建File对象，表示要操作的文件夹
        File folder = new File(folderPath);

        // 判断文件夹是否存在
        if (!folder.exists()) {
            // 文件夹不存在，创建文件夹
            boolean success = folder.mkdirs();
            if (success) {
                System.out.println("文件夹创建成功！");
            } else {
                System.out.println("文件夹创建失败！");
            }
        } else {
            System.out.println("文件夹已经存在！");
        }
    }
}
