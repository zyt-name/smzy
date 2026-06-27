package com.clou.admindemo.uitl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Imgutil {

    /**
     * 保存图片到指定路径并返回绝对路径
     * @param inputStream 图片输入流
     * @param fileName 文件名（包含后缀）
     * @param savePath 图片保存路径
     * @return 保存成功返回文件绝对路径，失败返回null
     */
    public static String saveImage(InputStream inputStream, String fileName, String savePath) {
        if (!savePath.endsWith(File.separator)) {
            savePath = savePath + File.separator;
        }
        
        File dir = new File(savePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String filePath = savePath + fileName;
        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(filePath);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }

            return new File(filePath).getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
