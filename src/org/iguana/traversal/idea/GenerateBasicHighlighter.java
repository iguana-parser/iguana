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
import java.util.Set;

/**
 * Created by Anastasia Izmaylova on 18/12/15.
 */
public class GenerateBasicHighlighter {
    /*
     * <Lang>SyntaxHighlighter.java
     * <Lang>SyntaxHighlighterFactory.java
     */
    public static void generate(String language, String path, Set<String> tokenTypes) {
        new File(path + language.toLowerCase() + "/gen/editor").mkdir();
        File file = new File(path + language.toLowerCase() + "/gen/editor/" + language + "SyntaxHighlighter.java");
        try {
            PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
            writer.println("package " + language.toLowerCase() + ".gen.editor;");
            writer.println();
            writer.println("/** This file has been generated. */");
            writer.println();
            writer.println("import com.intellij.lexer.Lexer;");
            writer.println("import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;");
            writer.println("import com.intellij.openapi.editor.HighlighterColors;");
            writer.println("import com.intellij.openapi.editor.colors.TextAttributesKey;");
            writer.println("import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;");
            writer.println("import com.intellij.psi.tree.IElementType;");
            writer.println("import " + language.toLowerCase() + ".gen.lexer." + language + "Lexer;");
            writer.println("import " + language.toLowerCase() + ".gen.psi." + language + "TokenTypes;");
            writer.println();
            writer.println("public class " + language + "SyntaxHighlighter extends SyntaxHighlighterBase {");
            writer.println();
            writer.println("    public Lexer getHighlightingLexer() { return new " + language + "Lexer(); }");
            writer.println();
            writer.println("    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {");
            int i = 0;
            for (String tokenType : tokenTypes) {
                String color = getColor(tokenType);
                if (color != null) {
                    if (color.equals("BAD_CHARACTER"))
                        color = "HighlighterColors.TEXT";
                    else
                        color = "DefaultLanguageHighlighterColors." + color;
                    writer.println((i == 0? "        if " : "        else if ") + "(tokenType.equals(" + language + "TokenTypes." + tokenType + "))");
                    writer.println("            return new TextAttributesKey[] {TextAttributesKey.createTextAttributesKey(\"" + tokenType + "\", " + color + ")};");
                    i++;
                }
            }
            writer.println("        return new TextAttributesKey[0];");
            writer.println("    }");
            writer.println("}");
            writer.println();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        file = new File(path + language.toLowerCase() + "/gen/editor/" + language + "SyntaxHighlighterFactory.java");
        try {
            PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
            writer.println("package " + language.toLowerCase() + ".gen.editor;");
            writer.println();
            writer.println("/** This file has been generated. */");
            writer.println();
            writer.println("import com.intellij.openapi.fileTypes.SyntaxHighlighter;");
            writer.println("import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;");
            writer.println("import com.intellij.openapi.project.Project;");
            writer.println("import com.intellij.openapi.vfs.VirtualFile;");
            writer.println();
            writer.println("public class " + language + "SyntaxHighlighterFactory extends SyntaxHighlighterFactory {");
            writer.println("    public SyntaxHighlighter getSyntaxHighlighter(Project project, VirtualFile virtualFile) { return new " + language + "SyntaxHighlighter(); }");
            writer.println("}");
            writer.println();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static String getColor(String tokenType) {
        switch (tokenType) {
            case "OPERATOR": return "OPERATION_SIGN";
            case "OPEN_BRACE": return "BRACES";
            case "CLOSE_BRACE": return "BRACES";
            case "OPEN_PARENTHESIS": return "PARENTHESES";
            case "CLOSE_PARENTHESIS": return "PARENTHESES";
            case "OPEN_BRACKET": return "BRACKETS";
            case "CLOSE_BRACKET": return "BRACKETS";
            case "BAD_CHARACTER": return "BAD_CHARACTER";
            default:
                if (tokenType.toUpperCase().contains("COMMENT"))
                    return "LINE_COMMENT";
                else
                    return "IDENTIFIER";
        }
    }
}
