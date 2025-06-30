package com.taskflow.util;

import java.io.File;
import java.io.IOException;

public class FileManager {

    public static boolean createFile(String path) throws IOException {
        File file = new File(path);
        return file.createNewFile();
    }

    public static boolean delete(String path) {
        File file = new File(path);
        return file.delete();
    }

    public static boolean isDirectory(String path) {
        return new File(path).isDirectory();
    }

    public static boolean isFile(String path) {
        return new File(path).isFile();
    }

    public static File[] listFiles(String dirPath) {
        File dir = new File(dirPath);
        if (dir.isDirectory()) {
            return dir.listFiles();
        }
        return null;
    }
}
