package org.iguana.generator;

import org.iguana.utils.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class GeneratorUtils {

    public static void writeToFile(String content, String genDirectory, String className) {
        try {
            FileUtils.writeFile(content, new File(genDirectory, className + ".java").getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
