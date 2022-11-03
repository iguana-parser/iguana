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
 * Created by Anastasia Izmaylova on 18/12/15.
 */
public class GenerateParser {
    /*
     * <Lang>Parser.java
     * <Lang>ParserDefinition.java
     * <Lang>File.java
     */
    public static void generate(String language, String path) {
        new File(path + language.toLowerCase() + "/gen/parser").mkdir();
        File file = new File(path + language.toLowerCase() + "/gen/parser/" + language + "Parser.java");
        try {
            PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
            writer.println("package " + language.toLowerCase() + ".gen.parser;");
            writer.println();
            writer.println("/* This file has been generated. */");
            writer.println();
            writer.println("import com.intellij.lang.ASTNode;");
            writer.println("import com.intellij.lang.PsiBuilder;");
            writer.println("import com.intellij.lang.PsiParser;");
            writer.println("import com.intellij.psi.impl.source.tree.Factory;");
            writer.println("import com.intellij.psi.tree.IElementType;");
            writer.println("import iguana.parsetrees.sppf.NonterminalNode;");
            writer.println("import iguana.parsetrees.term.SPPFToTerms;");
            writer.println("import iguana.utils.input.Input;");
            writer.println("import org.iguana.grammar.Grammar;");
            writer.println("import org.iguana.grammar.GrammarGraph;");
            writer.println("import org.iguana.grammar.symbol.Nonterminal;");
            writer.println("import org.iguana.grammar.symbol.Start;");
            writer.println("import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;");
            writer.println("import org.iguana.grammar.transformation.EBNFToBNF;");
            writer.println("import org.iguana.grammar.transformation.LayoutWeaver;");
            writer.println("import org.iguana.parser.IguanaParser;");
            writer.println("import org.iguana.parser.ParseResult;");
            writer.println("import org.iguana.traversal.idea.Names;");
            writer.println("import org.iguana.util.Configuration;");
            writer.println();
            writer.println("public class " + language + "Parser implements PsiParser {");
            writer.println();
            writer.println("    private Grammar grammar;");
            writer.println("    private Start start;");
            writer.println("    private GrammarGraph graph;");
            writer.println();
            writer.println("    public ASTNode getParserTree(IElementType root, PsiBuilder builder) {");
            writer.println("        Input input = Input.fromString(builder.getOriginalText().toString());");
            writer.println("        if (graph == null) {");
            writer.println(
                "            grammar = Grammar.load(this.getClass().getClassLoader().getResourceAsStream(\"" +
                language.toLowerCase() + "/gen/parser/grammar/" + language + "\"));");
            writer.println(
                "            DesugarPrecedenceAndAssociativity precedenceAndAssociativity = " +
                "new DesugarPrecedenceAndAssociativity();");
            writer.println("            precedenceAndAssociativity.setOP2();");
            writer.println("            grammar = new EBNFToBNF().transform(grammar);");
            writer.println("            grammar = precedenceAndAssociativity.transform(grammar);");
            writer.println("            grammar = new Names().transform(grammar);");
            writer.println("            grammar = new LayoutWeaver().transform(grammar);");
            writer.println("            start = grammar.getStartSymbol(Nonterminal.withName(\"\"));");
            writer.println("            graph = GrammarGraph.from(grammar, input, Configuration.DEFAULT);");
            writer.println("        }");
            writer.println("        ParseResult result = IguanaParser.getParserTree(input, graph, start);");
            writer.println("        if (result.isParseSuccess()) {");
            writer.println("            System.out.println(\"Success...\");");
            writer.println("            NonterminalNode sppf = result.asParseSuccess().getResult();");
            writer.println(
                "            ASTNode ast = SPPFToTerms.convertNoSharing(sppf, new " + language + "TermBuilder());");
            writer.println("            return ast;");
            writer.println("        } else {");
            writer.println("            System.out.println(\"Parse error...\");");
            writer.println(
                "            return Factory.createErrorElement(\"Sorry, you have a getParserTree error.\");");
            writer.println("        }");
            writer.println("    }");
            writer.println("}");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        file = new File(path + language.toLowerCase() + "/gen/parser/" + language + "ParserDefinition.java");
        try {
            PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
            writer.println("package " + language.toLowerCase() + ".gen.parser;");
            writer.println();
            writer.println("/* This file has been generated. */");
            writer.println();
            writer.println("import com.intellij.lang.ASTNode;");
            writer.println("import com.intellij.lang.Language;");
            writer.println("import com.intellij.lang.ParserDefinition;");
            writer.println("import com.intellij.lang.PsiParser;");
            writer.println("import com.intellij.lexer.Lexer;");
            writer.println("import com.intellij.openapi.project.Project;");
            writer.println("import com.intellij.psi.FileViewProvider;");
            writer.println("import com.intellij.psi.PsiElement;");
            writer.println("import com.intellij.psi.PsiFile;");
            writer.println("import com.intellij.psi.text.BlockSupport;");
            writer.println("import com.intellij.psi.tree.IFileElementType;");
            writer.println("import com.intellij.psi.tree.TokenSet;");
            writer.println("import " + language.toLowerCase() + ".gen.lang." + language + "File;");
            writer.println("import " + language.toLowerCase() + ".gen.lang." + language + "Lang;");
            writer.println("import " + language.toLowerCase() + ".gen.lexer." + language + "Lexer;");
            writer.println("import " + language.toLowerCase() + ".gen.psi." + language + "ElementTypes;");
            writer.println("import " + language.toLowerCase() + ".gen.psi." + language + "TokenTypes;");
            writer.println();
            writer.println("public class " + language + "ParserDefinition implements ParserDefinition {");
            writer.println();
            writer.println(
                "    public static final IFileElementType FILE = new IFileElementType(Language.<" + language +
                "Lang>findInstance(" + language + "Lang.class));");
            writer.println();
            writer.println("    public Lexer createLexer(Project project) { return new " + language + "Lexer(); }");
            writer.println();
            writer.println(
                "    public PsiParser createParser(Project project) { return new " + language + "Parser(); }");
            writer.println();
            writer.println("    public IFileElementType getFileNodeType() { return FILE; }");
            writer.println();
            writer.println("    public TokenSet getWhitespaceTokens() { return TokenSet.EMPTY; }");
            writer.println();
            writer.println("    public TokenSet getCommentTokens() { return TokenSet.EMPTY; }");
            writer.println();
            writer.println("    public TokenSet getStringLiteralElements() { return TokenSet.EMPTY; }");
            writer.println();
            writer.println("    public PsiElement createElement(ASTNode node) { return " + language +
                           "ElementTypes.Factory.createElement(node); }");
            writer.println();
            writer.println("    public PsiFile createFile(FileViewProvider viewProvider) {");
            writer.println("        " + language + "File file = new " + language + "File(viewProvider);");
            writer.println("        file.putUserData(BlockSupport.DO_NOT_REPARSE_INCREMENTALLY, Boolean.TRUE);");
            writer.println("        return file;");
            writer.println("    }");
            writer.println();
            writer.println(
                "    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) " +
                "{ return SpaceRequirements.MAY; }");
            writer.println("}");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        file = new File(path + language.toLowerCase() + "/gen/lang/" + language + "File.java");
        try {
            PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
            writer.println("package " + language.toLowerCase() + ".gen.lang;");
            writer.println();
            writer.println("/* This file has been generated. */");
            writer.println();
            writer.println("import com.intellij.extapi.psi.PsiFileBase;");
            writer.println("import com.intellij.openapi.fileTypes.FileType;");
            writer.println("import com.intellij.psi.FileViewProvider;");
            writer.println("import javax.swing.Icon;");
            writer.println();
            writer.println("public class " + language + "File extends PsiFileBase {");
            writer.println(
                "    public " + language + "File(FileViewProvider viewProvider) { super(viewProvider, " + language +
                "Lang.instance); }");
            writer.println("    public FileType getFileType() { return " + language + "FileType.instance; }");
            writer.println("    public String toString() { return \"" + language + " file\"; }");
            writer.println("    public Icon getIcon(int flags) { return super.getIcon(flags); }");
            writer.println("}");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // generateTreeBuider(language, path);
    }
}
