package org.iguana.generator;

import org.iguana.utils.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GeneratorUtils {

    public static void writeToJavaFile(String content, String genDirectory, String className) {
        writeToFile(content, genDirectory, className, "java");
    }

    public static void writeToFile(String content, String genDirectory, String className, String extension) {
        try {
            Files.createDirectories(Paths.get(genDirectory));
            FileUtils.writeFile(content, new File(genDirectory, className + "." + extension).getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
