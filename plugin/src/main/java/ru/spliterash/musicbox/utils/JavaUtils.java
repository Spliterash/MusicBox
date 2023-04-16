package ru.spliterash.musicbox.utils;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class JavaUtils {
    public List<String> stackTraceToList(StackTraceElement[] stackTrace) {
        return Arrays.stream(stackTrace)
                .map(trace -> trace.getClassName() + " " + trace.getMethodName() + " on line " + trace.getLineNumber())
                .collect(Collectors.toList());
    }
}
