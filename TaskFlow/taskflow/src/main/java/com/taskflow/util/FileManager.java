package com.taskflow.util;

import java.io.File;
import java.io.IOException;

// Utilitar pentru operații de bază pe fișiere și directoare.
public class FileManager {

    // Creează un fișier nou la calea specificată; returnează true dacă a fost creat
    public static boolean createFile(String path) throws IOException {
        File file = new File(path);
        return file.createNewFile();
    }

    // Șterge fișierul sau directorul la calea specificată; returnează true dacă reușește
    public static boolean delete(String path) {
        File file = new File(path);
        return file.delete();
    }

    // Verifică dacă calea specificată este un director
    public static boolean isDirectory(String path) {
        return new File(path).isDirectory();
    }

    // Verifică dacă calea specificată este un fișier obișnuit
    public static boolean isFile(String path) {
        return new File(path).isFile();
    }

    // Listează fișierele dintr-un director; returnează null dacă nu e director
    public static File[] listFiles(String dirPath) {
        File dir = new File(dirPath);
        if (dir.isDirectory()) {
            return dir.listFiles();
        }
        return null;
    }
}
