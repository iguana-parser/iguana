package org.iguana.generator.ide;

import java.io.File;

import static org.iguana.generator.GeneratorUtils.writeToJavaFile;
import static org.iguana.utils.string.StringUtil.toFirstUpperCase;

public class GenerateLangFiles {

    private final String grammarName;
    private final String langGenDirectory;

    public GenerateLangFiles(String grammarName, String genDirectory) {
        this.grammarName = grammarName;
        this.langGenDirectory = new File(genDirectory, "lang").getAbsolutePath();
    }

    public void generate() {
        generateFileType();
        generateIcon();
        generateLang();
    }

    private void generateFileType() {
        String className = toFirstUpperCase(grammarName);
        String content =
            "// This file has been generated, do not directly edit this file!\n" +
            "package " + grammarName.toLowerCase() + ".ide.lang;\n" +
            "\n" +
            "import com.intellij.openapi.fileTypes.LanguageFileType;\n" +
            "import com.intellij.openapi.util.NlsContexts;\n" +
            "import com.intellij.openapi.util.NlsSafe;\n" +
            "import org.jetbrains.annotations.NonNls;\n" +
            "import org.jetbrains.annotations.NotNull;\n" +
            "\n" +
            "import javax.swing.*;\n" +
            "\n" +
            "public class " + className + "FileType extends LanguageFileType {\n" +
            "\n" +
            "public static final " + className + "FileType instance = new " + className + "FileType();\n" +
            "\n" +
            "  protected " + className + "FileType() {\n" +
            "    super(" + className + "Lang.instance);\n" +
            "  }\n" +
            "\n" +
            "  @Override\n" +
            "  public @NonNls @NotNull String getName() {\n" +
            "    return \"" + className + "\";\n" +
            "  }\n" +
            "\n" +
            "  public @NlsContexts.Label @NotNull String getDescription() {\n" +
            "    return \"" + className + " grammar definition file\"\n;" +
            "  }\n" +
            "\n" +
            "  @Override\n" +
            "  public @NlsSafe @NotNull String getDefaultExtension() {\n" +
            "    return \"" + className + "\";\n" +
            "  }\n" +
            "\n" +
            "  @Override\n" +
            "  public Icon getIcon() {\n" +
            "    return " + className + "Icon.icon;\n" +
            "  }\n" +
            "}\n";

        writeToJavaFile(content, langGenDirectory, className + "FileType");
    }

    private void generateIcon() {
        String className = toFirstUpperCase(grammarName);
        String content =
            "// This file has been generated, do not directly edit this file!\n" +
            "package " + grammarName.toLowerCase() + ".ide.lang;\n" +
            "\n" +
            "import com.intellij.openapi.util.IconLoader;\n" +
            "\n" +
            "import javax.swing.*;\n" +
            "\n" +
            "public class " + className + "Icon {\n" +
            "  public static final Icon icon = IconLoader.getIcon(\"/META-INF/pluginIcon.svg\", " + className + "Icon.class);\n" +
            "}\n";
        writeToJavaFile(content, langGenDirectory, className + "Icon");
    }

    private void generateLang() {
        String className = toFirstUpperCase(grammarName);
        String content =
            "// This file has been generated, do not directly edit this file!\n" +
            "package " + grammarName.toLowerCase() + ".ide.lang;\n" +
            "\n" +
            "import com.intellij.lang.Language;\n" +
            "\n" +
            "public class " + className + "Lang extends Language {\n" +
            "\n" +
            "  public static final " + className + "Lang instance = new " + className + "Lang();\n" +
            "\n" +
            "  private " + className + "Lang() {\n" +
            "    super(\"" + className + "\");\n" +
            "  }\n" +
            "}\n" +
            "\n";
        writeToJavaFile(content, langGenDirectory, className + "Lang");
    }
}
