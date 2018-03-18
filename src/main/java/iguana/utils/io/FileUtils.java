package iguana.utils.io;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {

    public static String readFile(String path) throws IOException {
        byte[] content = Files.readAllBytes(Paths.get(path));
        return new String(content, StandardCharsets.UTF_8);
    }

    public static String readFile(InputStream in) throws IOException {
        byte[] content = new byte[in.available()];
        in.read(content);
        return new String(content, StandardCharsets.UTF_8);
    }

    public static void writeFile(String content, String path) throws IOException {
        try (Writer out = new FileWriter(path)) {
            out.write(content);
        }
    }

}
