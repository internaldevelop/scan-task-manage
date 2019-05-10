package com.toolkit.scantaskmng.global.utils;

import java.io.*;

public class MyFileUtils {
    /**
     * 在当前路径保存数据到指定文件中
     * @param data
     * @param fileName
     * @param bOverride
     * @return
     */
    public static boolean save(String data, String fileName, boolean bOverride) {
        OutputStream outputStream = null;

        // 获取当前路径
        String workingPath = getWorkingPath();
        File currentDir = new File(workingPath);
        // 路径不存在时，创建路径，保留此创建操作，是为了兼容其他路径
        if (!currentDir.exists()) {
            currentDir.mkdirs();
        }

        String fileFullPath = currentDir.getPath() + File.separator + fileName;
        try {
            outputStream = new FileOutputStream(fileFullPath, !bOverride);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean save(String data, String fileName) {
        return save(data, fileName, true);
    }

    public static String getWorkingPath() {
        return System.getProperty("user.dir");
    }
}
