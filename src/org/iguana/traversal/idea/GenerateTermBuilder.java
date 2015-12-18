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
public class GenerateTermBuilder {
    /*
     * <Lang>TreeBuilder.java
     */
    public static void generate(String language, String path) {
        File file = new File(path + language.toLowerCase() + "/gen/parser/" + language + "TreeBuilder.java");
        try {
            PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
            writer.println("package " + language.toLowerCase() + ".gen.parser;");
            writer.println();
            writer.println("/* This file has been generated. */");
            writer.println();
            writer.println("import com.intellij.lang.ASTFactory;");
            writer.println("import com.intellij.psi.impl.source.tree.CompositeElement;");
            writer.println("import com.intellij.psi.impl.source.tree.TreeElement;");
            writer.println("import com.intellij.psi.tree.IElementType;");
            writer.println("import " + language.toLowerCase() + ".gen.psi." + language + "ElementTypes;");
            writer.println("import " + language.toLowerCase() + ".gen.psi." + language + "TokenTypes;");
            writer.println("import iguana.parsetrees.tree.Branch;");
            writer.println("import iguana.parsetrees.tree.RuleType;");
            writer.println("import iguana.parsetrees.tree.TreeBuilder;");
            writer.println("import iguana.utils.input.Input;");
            writer.println("import org.iguana.grammar.symbol.Rule;");
            writer.println("import scala.collection.*;");
            writer.println();
            writer.println("public class " + language + "TreeBuilder implements TreeBuilder<TreeElement> {");
            writer.println();
            writer.println("    public TreeElement terminalNode(int l, int r, Input input) { return ASTFactory.leaf(" + language + "TokenTypes.TERMINAL, input.subString(l, r)); }");
            writer.println();
            writer.println("    public TreeElement terminalNode(String name, int l, int r, Input input) {");
            writer.println("        IElementType tokenType = " + language + "TokenTypes.get(name);");
            writer.println("        return ASTFactory.leaf(tokenType, input.subString(l, r));");
            writer.println("    }");
            writer.println();
            writer.println("    public TreeElement nonterminalNode(RuleType type, Seq<TreeElement> children, int l, int r, Input input) {");
            writer.println("        Rule rule = (Rule) type;");
            writer.println("        String name = rule.getHead().getName().toUpperCase() + (rule.getLabel() == null? \"\" : \"_\" + rule.getLabel().toUpperCase());");
            writer.println("        CompositeElement node = ASTFactory.composite(" + language + "ElementTypes.get(name));");
            writer.println("        Iterator<TreeElement> iterator = children.iterator();");
            writer.println("        while (iterator.hasNext()) node.rawAddChildrenWithoutNotifications(iterator.next());");
            writer.println("        return node;");
            writer.println("    }");
            writer.println();
            writer.println("    public TreeElement ambiguityNode(scala.collection.Iterable<Branch<TreeElement>> children, int l, int r) { throw new RuntimeException(\"Not yet supported in the idea tree builder: ambiguity.\"); }");
            writer.println();
            writer.println("    public TreeElement cycle(String label) { throw new RuntimeException(\"Not yet supported in the idea tree builder: cycles.\"); }");
            writer.println();
            writer.println("    public Branch<TreeElement> branch(RuleType type, Seq<TreeElement> children) { throw new RuntimeException(\"Not yet supported in the idea tree builder: ambiguity.\"); } ");
            writer.println();
            writer.println("    public TreeElement star(Seq<TreeElement> children) {");
            writer.println("        CompositeElement node = ASTFactory.composite(" + language + "ElementTypes.LIST);");
            writer.println("        Iterator<TreeElement> iterator = children.iterator();");
            writer.println("        while (iterator.hasNext()) node.rawAddChildrenWithoutNotifications(iterator.next());");
            writer.println("        return node;");
            writer.println("    }");
            writer.println();
            writer.println("    public TreeElement plus(Seq<TreeElement> children) { return star(children); }");
            writer.println();
            writer.println("    public TreeElement opt(TreeElement child) {");
            writer.println("        CompositeElement node = ASTFactory.composite(" + language + "ElementTypes.OPT);");
            writer.println("        node.rawAddChildrenWithoutNotifications(child);");
            writer.println("        return node;");
            writer.println("    }");
            writer.println();
            writer.println("    public TreeElement group(Seq<TreeElement> children) {");
            writer.println("        CompositeElement node = ASTFactory.composite(" + language + "ElementTypes.SEQ);");
            writer.println("        Iterator<TreeElement> iterator = children.iterator();");
            writer.println("        while (iterator.hasNext()) node.rawAddChildrenWithoutNotifications(iterator.next());");
            writer.println("        return node;");
            writer.println("    }");
            writer.println();
            writer.println("    public TreeElement alt(Seq<TreeElement> children) {");
            writer.println("        CompositeElement node = ASTFactory.composite(" + language + "ElementTypes.ALT);");
            writer.println("        Iterator<TreeElement> iterator = children.iterator();");
            writer.println("        while (iterator.hasNext()) node.rawAddChildrenWithoutNotifications(iterator.next());");
            writer.println("        return node;");
            writer.println("    }");
            writer.println();
            writer.println("    public TreeElement epsilon(int i) { return ASTFactory.leaf(" + language + "TokenTypes.TERMINAL, \"\"); }");
            writer.println("}");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
