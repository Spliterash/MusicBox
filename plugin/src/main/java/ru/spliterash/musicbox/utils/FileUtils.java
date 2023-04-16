package ru.spliterash.musicbox.utils;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@UtilityClass
public class FileUtils {
    public String getFilename(String fileName) {
        int index = fileName.lastIndexOf('.');
        return fileName.substring(0, index);
    }

    public List<String> readFileToList(File file) throws IOException {
        return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
    }
}
