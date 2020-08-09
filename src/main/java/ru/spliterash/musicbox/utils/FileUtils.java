package ru.spliterash.musicbox.utils;

public class FileUtils {
    public static String getFilename(String fileName) {
        int index = fileName.lastIndexOf('.');
        return fileName.substring(0, index);
    }
}
