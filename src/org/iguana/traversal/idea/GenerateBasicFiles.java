/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */

package org.iguana.traversal.idea;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by Anastasia Izmaylova on 17/12/15.
 */
public class GenerateBasicFiles {
    /*
     * <Lang>Lang.java
     * <Lang>FileType.java
     * <Lang>FileTypeFactory.java
     * <Lang>ElementType.java
     * <Lang>TokenType.java
     */
    public static void generate(String language, String extension, String path) {
        // Create the respective directories
        String name = path + language.toLowerCase();
        new File(name).mkdir();
        name = path + language.toLowerCase() + "/gen";
        new File(name).mkdir();
        name = path + language.toLowerCase() + "/gen/lang";
        new File(name).mkdir();
        name = path + language.toLowerCase() + "/gen/icons";
        new File(name).mkdir();
        name = path + language.toLowerCase() + "/gen/psi";
        new File(name).mkdir();

        File file = new File(path + language.toLowerCase() + "/gen/lang/" + language + "Lang.java");
        try {
            PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
            writer.println("package " + language.toLowerCase() + ".gen.lang;");
            writer.println();
            writer.println("/** This file has been generated. */");
            writer.println();
            writer.println("import com.intellij.lang.Language;");
            writer.println("import com.intellij.openapi.util.IconLoader;");
            writer.println("import javax.swing.*;");
            writer.println();
            writer.println("public class " + language + "Lang extends Language {");
            writer.println("    public static final " + language + "Lang instance = new " + language + "Lang();");
            writer.println("    public static final Icon file = IconLoader.getIcon(\"/" + language.toLowerCase() +
                           "/gen/icons/icon.png\");");
            writer.println("    private " + language + "Lang() { super(\"" + language + "\"); }");
            writer.println("}");
            writer.println();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        file = new File(path + language.toLowerCase() + "/gen/lang/" + language + "FileType.java");
        try {
            PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
            writer.println("package " + language.toLowerCase() + ".gen.lang;");
            writer.println();
            writer.println("/** This file has been generated. */");
            writer.println();
            writer.println("import com.intellij.openapi.fileTypes.LanguageFileType;");
            writer.println("import javax.swing.*;");
            writer.println();
            writer.println("public class " + language + "FileType extends LanguageFileType {");
            writer.println(
                "    public static final " + language + "FileType instance = new " + language + "FileType();");
            writer.println("    private " + language + "FileType() { super(" + language + "Lang.instance); }");
            writer.println();
            writer.println("    public String getName() { return \"" + language + "\"; }");
            writer.println("    public String getDescription() { return \"" + language + "\"; }");
            writer.println("    public String getDefaultExtension() { return \"" + extension + "\"; }");
            writer.println("    public Icon getIcon() { return " + language + "Lang.file; }");
            writer.println("}");
            writer.println();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        file = new File(path + language.toLowerCase() + "/gen/lang/" + language + "FileTypeFactory.java");
        try {
            PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
            writer.println("package " + language.toLowerCase() + ".gen.lang;");
            writer.println();
            writer.println("/** This file has been generated. */");
            writer.println();
            writer.println("import com.intellij.openapi.fileTypes.FileTypeConsumer;");
            writer.println("import com.intellij.openapi.fileTypes.FileTypeFactory;");
            writer.println();
            writer.println("public class " + language + "FileTypeFactory extends FileTypeFactory {");
            writer.println(
                "    public void createFileTypes(FileTypeConsumer fileTypeConsumer) { fileTypeConsumer.consume(" +
                language + "FileType.instance, \"" + extension + "\"); }");
            writer.println("}");
            writer.println();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        file = new File(path + language.toLowerCase() + "/gen/psi/" + language + "ElementType.java");
        try {
            PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
            writer.println("package " + language.toLowerCase() + ".gen.psi;");
            writer.println();
            writer.println("/** This file has been generated. */");
            writer.println();
            writer.println("import com.intellij.psi.tree.IElementType;");
            writer.println("import " + language.toLowerCase() + ".gen.lang." + language + "Lang;");
            writer.println();
            writer.println("public class " + language + "ElementType extends IElementType {");
            writer.println("    public " + language + "ElementType(String debugName) { super(debugName, " + language +
                           "Lang.instance); }");
            writer.println("}");
            writer.println();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        file = new File(path + language.toLowerCase() + "/gen/psi/" + language + "TokenType.java");
        try {
            PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
            writer.println("package " + language.toLowerCase() + ".gen.psi;");
            writer.println();
            writer.println("/** This file has been generated. */");
            writer.println();
            writer.println("import com.intellij.psi.tree.IElementType;");
            writer.println("import " + language.toLowerCase() + ".gen.lang." + language + "Lang;");
            writer.println();
            writer.println("public class " + language + "TokenType extends IElementType {");
            writer.println("    public " + language + "TokenType(String debugName) { super(debugName, " + language +
                           "Lang.instance); }");
            writer.println(
                "    public String toString() { return \"" + language + "TokenType.\" + super.toString(); }");
            writer.println("}");
            writer.println();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
