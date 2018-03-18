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

import org.iguana.grammar.symbol.*;
import org.iguana.traversal.ISymbolVisitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by Anastasia Izmaylova on 18/12/15.
 */
public class GenerateElements {

    /*
     * <Lang>ElementTypes.java
     * gen.psi.*
     * gen.psi.impl.*
     */

    enum NUM { ONE, MORE_THAN_ONE, ONE_AND_MORE }

    public static void generate(List<Rule> rules, String language, String path) {
        Map<String, Set<String>> elements = new LinkedHashMap<>();
        for (Rule rule : rules) {

            if (rule.getHead().getName().equals("$default$")) continue;

            Set<String> labels = elements.get(rule.getHead().getName());
            if (labels == null) {
                labels = new HashSet<>();
                elements.put(rule.getHead().getName(), labels);
            }
            labels.add(rule.getLabel() == null ? "Impl" : rule.getLabel());
        }
        generateElementTypes(elements, language, path);
        generatePhiElements(rules, language, path);
    }

    private static void generateElementTypes(Map<String, Set<String>> elements, String language, String path) {
        File file = new File(path + language.toLowerCase() + "/gen/psi/" + language + "ElementTypes.java");
        try {
            PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
            writer.println("package " + language.toLowerCase() + ".gen.psi;");
            writer.println();
            writer.println("/* This file has been generated. */");
            writer.println();
            writer.println("import com.intellij.lang.ASTNode;");
            writer.println("import com.intellij.psi.PsiElement;");
            writer.println("import com.intellij.psi.tree.IElementType;");
            writer.println("import " + language.toLowerCase() + ".gen.psi." + language + "ElementType;");
            writer.println("import " + language.toLowerCase() + ".gen.psi.impl.*;");
            writer.println();
            writer.println("public interface " + language + "ElementTypes {");
            writer.println();
            // ebnf related types, also data-dependent
            writer.println("    public IElementType LIST = new " + language + "ElementType(\"LIST\");"); // * and while
            writer.println("    public IElementType OPT = new " + language + "ElementType(\"OPT\");");   // ? and if-then
            writer.println("    public IElementType ALT = new " + language + "ElementType(\"ALT\");");   // | and if-then-else
            writer.println("    public IElementType SEQ = new " + language + "ElementType(\"SEQ\");");   // () and {}
            writer.println();
            for (String head : elements.keySet()) {
                for (String label : elements.get(head)) {
                    if (label.equals("Impl"))
                        writer.println("    public IElementType " + head.toUpperCase() + " = new " + language + "ElementType(\"" + head.toUpperCase() + "\");");
                    else
                        writer.println("    public IElementType " + head.toUpperCase() + "_" + label.toUpperCase() + " = new " + language + "ElementType(\"" + head.toUpperCase() + "_" + label.toUpperCase() + "\");");
                }
            }
            writer.println();
            writer.println("    public static IElementType get(String name) {");
            writer.println("        switch (name) {");
            writer.println("            case \"LIST\": return LIST;");
            writer.println("            case \"OPT\": return OPT;");
            writer.println("            case \"ALT\": return ALT;");
            writer.println("            case \"SEQ\": return SEQ;");
            for (String head : elements.keySet()) {
                for (String label : elements.get(head)) {
                    if (label.equals("Impl"))
                        writer.println("            case \"" + head.toUpperCase() + "\": return " + head.toUpperCase() + ";");
                    else
                        writer.println("            case \"" + head.toUpperCase() + "_" + label.toUpperCase() + "\": return " + head.toUpperCase() + "_" + label.toUpperCase() + ";");
                }
            }
            writer.println("        }");
            writer.println("        throw new RuntimeException(\"Should not have happened!\");");
            writer.println("    }");
            writer.println();
            writer.println("    class Factory {");
            writer.println("        public static PsiElement createElement(ASTNode node) {");
            writer.println("            IElementType type = node.getElementType();");
            writer.println("            if (type == LIST || type == OPT || type == ALT || type == SEQ) return new EbnfElementImpl(node);");
            for (String head : elements.keySet()) {
                for (String label : elements.get(head)) {
                    if (label.equals("Impl"))
                        writer.println("            if (type == " + head.toUpperCase() + ") return new " + head + "Impl(node);");
                    else
                        writer.println("            if (type == " + head.toUpperCase() + "_" + label.toUpperCase() + ") return new " + head + label + "Impl(node);");
                }
            }
            writer.println("            throw new RuntimeException(\"Should not have happened!\");");
            writer.println("        }");
            writer.println("    }");
            writer.println("}");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static void generatePhiElements(List<Rule> rules, String language, String path) {
        new File(path + language.toLowerCase() + "/gen/psi/impl").mkdir();
        // Symbol names with their occurrence counter; per nonterminal and per label of a rule
        Map<String, Map<String, Map<String, NUM>>> elements = new LinkedHashMap<>();

        for (Rule rule : rules) {

            if (rule.getHead().getName().equals("$default$")) continue;

            Map<String, Map<String, NUM>> m1 = elements.get(rule.getHead().getName());
            if (m1 == null) {
                m1 = new LinkedHashMap<>();
                elements.put(rule.getHead().getName(), m1);
            }

            String label = rule.getLabel();
            if (label == null || label.isEmpty()) label = "Impl";

            Map<String, NUM> m2 = m1.get(label);

            Map<String, NUM> m3 = new LinkedHashMap<>();
            new GetPhiElements(rule, m3).compute(language, path);

            if (m2 == null) {
                m1.put(label, m3);
                continue;
            }

            for (Map.Entry<String, NUM> entry : m3.entrySet()) {
                NUM num = m2.get(entry.getKey());
                if (num == null)
                    m2.put(entry.getKey(), entry.getValue());
                else
                    switch (num) {
                        case ONE:
                            if (entry.getValue() != NUM.ONE)
                                num = entry.getValue();
                            break;
                        case MORE_THAN_ONE:
                            break;
                        case ONE_AND_MORE: throw new RuntimeException("Should not happen!");
                    }
            }
        }

        GetPhiElements.generate(elements, language, path);
    }

    private static class GetPhiElements implements ISymbolVisitor<String> {

        private final Rule rule;
        private final Map<String, NUM> children;

        private static final InferPsiEbnfElementType typer = new InferPsiEbnfElementType();

        public GetPhiElements(Rule rule, Map<String, NUM> children) {
            this.rule = rule;
            this.children = children;
        }

        public void compute(String language, String path) {
            String prev_ebnf_element = null;
            for (Symbol symbol : rule.getBody()) {
                String child = symbol.accept(this);
                if (child != null) {

                    if (child.endsWith("$Ebnf")) {
                        if (prev_ebnf_element != null && !prev_ebnf_element.equals(child)) {
                            if (!prev_ebnf_element.equals("Element$Ebnf")) {
                                children.remove(prev_ebnf_element);
                                prev_ebnf_element = "Element$Ebnf";
                                children.put(prev_ebnf_element, NUM.MORE_THAN_ONE);
                            } else if (prev_ebnf_element.equals("Element$Ebnf")) {
                                NUM num = children.get(prev_ebnf_element);
                                if (num == NUM.ONE)
                                    children.put(prev_ebnf_element, NUM.MORE_THAN_ONE);
                            }
                            continue;
                        } else
                            prev_ebnf_element = child;
                    }

                    NUM num = children.get(child);
                    if (num == null)
                        num = NUM.ONE;
                    else
                        switch (num) {
                            case ONE: num = NUM.MORE_THAN_ONE; break;
                            case MORE_THAN_ONE: break;
                            default: throw new RuntimeException("Should not happen!");
                        }
                    children.put(child, num);
                }
            }
        }

        @Override
        public String visit(Align symbol) {
            return symbol.getSymbol().accept(this);
        }

        @Override
        public String visit(Block symbol) {
            String type = typer.visit(symbol);
            if (type == null) return null;
            if (type.equals("PsiElement")) return "Element$Ebnf";
            return type + "$Ebnf";
        }

        @Override
        public String visit(Code symbol) {
            return symbol.getSymbol().accept(this);
        }

        @Override
        public String visit(Conditional symbol) {
            return symbol.getSymbol().accept(this);
        }

        @Override
        public String visit(IfThen symbol) {
            String type = typer.visit(symbol);
            if (type == null) return null;
            if (type.equals("PsiElement")) return "Element$Ebnf";
            return type + "$Ebnf";
        }

        @Override
        public String visit(IfThenElse symbol) {
            String type = typer.visit(symbol);
            if (type == null) return null;
            if (type.equals("PsiElement")) return "Element$Ebnf";
            return type + "$Ebnf";
        }

        @Override
        public String visit(Ignore symbol) {
            return symbol.getSymbol().accept(this);
        }

        @Override
        public String visit(Nonterminal symbol) {
            return symbol.getName();
        }

        @Override
        public String visit(Offside symbol) {
            return symbol.getSymbol().getName();
        }

        @Override
        public String visit(Terminal symbol) {
            return null;
        }

        @Override
        public String visit(While symbol) {
            String type = typer.visit(symbol);
            if (type == null) return null;
            if (type.equals("PsiElement")) return "Element$Ebnf";
            return type + "$Ebnf";
        }

        @Override
        public String visit(Return symbol) {
            return null;
        }

        @Override
        public <E extends Symbol> String visit(Alt<E> symbol) {
            String type = typer.visit(symbol);
            if (type == null) return null;
            if (type.equals("PsiElement")) return "Element$Ebnf";
            return type + "$Ebnf";
        }

        @Override
        public String visit(Opt symbol) {
            String type = typer.visit(symbol);
            if (type == null) return null;
            if (type.equals("PsiElement")) return "Element$Ebnf";
            return type + "$Ebnf";
        }

        @Override
        public String visit(Plus symbol) {
            String type = typer.visit(symbol);
            if (type == null) return null;
            if (type.equals("PsiElement")) return "Element$Ebnf";
            return type + "$Ebnf";
        }

        @Override
        public <E extends Symbol> String visit(Sequence<E> symbol) {
            String type = typer.visit(symbol);
            if (type == null) return null;
            if (type.equals("PsiElement")) return "Element$Ebnf";
            return type + "$Ebnf";
        }

        @Override
        public String visit(Star symbol) {
            String type = typer.visit(symbol);
            if (type == null) return null;
            if (type.equals("PsiElement")) return "Element$Ebnf";
            return type + "$Ebnf";
        }

        @Override
        public String visit(Start start) {
            String type = typer.visit(start);
            if (type == null) return null;
            if (type.equals("PsiElement")) return "Element$Ebnf";
            return type + "$Ebnf";
        }

        public static void generate(Map<String, Map<String, Map<String, NUM>>> elements, String language, String path) {

            File file = new File(path + language.toLowerCase() + "/gen/psi/IEbnfElement.java");
            try {
                PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
                writer.println("package " + language.toLowerCase() + ".gen.psi;");
                writer.println();
                writer.println("/* This file has been generated. */");
                writer.println();
                writer.println("import com.intellij.psi.PsiElement;");
                writer.println("import java.util.List;");
                writer.println();
                writer.println("public interface IEbnfElement" + " extends PsiElement {");
                writer.println("    public List<PsiElement>" + " getElements" + "();");
                writer.println("}");
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            Map<String, Map<String, NUM>> elements_per_nt = new HashMap<>();

            // Interfaces
            for (String head : elements.keySet()) {

                Map<String, NUM> symbols = new LinkedHashMap<>();
                elements_per_nt.put(head, symbols);

                for (Map<String, NUM> m : elements.get(head).values()) {
                    for (Map.Entry<String, NUM> entry : m.entrySet()) {
                        NUM num = symbols.get(entry.getKey());
                        if (num == null)
                            num = entry.getValue();
                        else
                            switch (num) {
                                case ONE:
                                    if (entry.getValue() != NUM.ONE)
                                        num = NUM.ONE_AND_MORE;
                                    break;
                                case MORE_THAN_ONE:
                                    if (entry.getValue() == NUM.ONE || entry.getValue() == NUM.ONE_AND_MORE)
                                        num = NUM.ONE_AND_MORE;
                                    break;
                                case ONE_AND_MORE:
                                    break;
                                default:
                            }
                        symbols.put(entry.getKey(), num);
                    }
                }

                file = new File(path + language.toLowerCase() + "/gen/psi/I" + head + ".java");

                boolean declaration = head.endsWith("$Declaration");
                boolean reference = head.endsWith("$Reference");
                try {
                    PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
                    writer.println("package " + language.toLowerCase() + ".gen.psi;");
                    writer.println();
                    writer.println("/* This file has been generated. */");
                    writer.println();
                    writer.println("import com.intellij.psi.PsiElement;");
                    if (declaration) writer.println("import com.intellij.psi.PsiNamedElement;");
                    if (reference) writer.println("import com.intellij.psi.PsiReference;");
                    writer.println("import java.util.List;");
                    writer.println();
                    if (declaration)
                        writer.println("public interface I" + head + " extends PsiElement, PsiNamedElement {");
                    else if (reference)
                        writer.println("public interface I" + head + " extends PsiElement, PsiReference {");
                    else
                        writer.println("public interface I" + head + " extends PsiElement {");
                    for (String symbol : symbols.keySet()) {
                        NUM num = symbols.get(symbol);
                        switch (num) {
                            case ONE:
                                if (symbol.endsWith("$Ebnf"))
                                    writer.println("    List<PsiElement> get" + symbol.substring(0, symbol.lastIndexOf("$")) + "List();");
                                else
                                    writer.println("    I" + symbol + " get" + symbol + "();");
                                break;
                            case MORE_THAN_ONE:
                                if (symbol.endsWith("$Ebnf"))
                                    writer.println("    List<List<PsiElement>> getAll" + symbol.substring(0, symbol.lastIndexOf("$")) + "List();");
                                else
                                    writer.println("    List<I" + symbol + "> getAll" + symbol + "();");
                                break;
                            case ONE_AND_MORE:
                                if (symbol.endsWith("$Ebnf"))
                                    writer.println("    List<PsiElement> get" + symbol.substring(0, symbol.lastIndexOf("$")) + "List();");
                                else
                                    writer.println("    I" + symbol + " get" + symbol + "();");

                                if (symbol.endsWith("$Ebnf"))
                                    writer.println("    List<List<PsiElement>> getAll" + symbol.substring(0, symbol.lastIndexOf("$")) + "List();");
                                else
                                    writer.println("    List<I" + symbol + "> getAll" + symbol + "();");
                                break;
                            default:
                        }
                    }
                    writer.println("}");
                    writer.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            // Classes
            file = new File(path + language.toLowerCase() + "/gen/psi/impl/EbnfElementImpl.java");
            try {
                PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
                writer.println("package " + language.toLowerCase() + ".gen.psi.impl;");
                writer.println();
                writer.println("/* This file has been generated. */");
                writer.println();
                writer.println("import com.intellij.psi.PsiElement;");
                writer.println("import com.intellij.psi.PsiElementVisitor;");
                writer.println("import com.intellij.psi.util.PsiTreeUtil;");
                writer.println();
                writer.println("import com.intellij.extapi.psi.ASTWrapperPsiElement;");
                writer.println("import com.intellij.lang.ASTNode;");
                writer.println();
                writer.println("import java.util.List;");
                writer.println("import java.util.ArrayList;");
                writer.println("import " + language.toLowerCase() + ".gen.psi.IEbnfElement;");
                writer.println();
                writer.println("public class EbnfElementImpl" + " extends ASTWrapperPsiElement implements IEbnfElement {");
                writer.println();
                writer.println("    public EbnfElementImpl(ASTNode node) { super(node); }");
                writer.println();
                writer.println("    public void accept(PsiElementVisitor visitor) { super.accept(visitor); }");
                writer.println();
                writer.println("    public List<PsiElement>" + " getElements" + "() {");
                writer.println("        List<PsiElement> flattened = new ArrayList<>();");
                writer.println("        for (PsiElement e : PsiTreeUtil.getChildrenOfTypeAsList(this, PsiElement.class)) {");
                writer.println("            if (e instanceof IEbnfElement) ");
                writer.println("                flattened.addAll(((IEbnfElement) e).getElements());");
                writer.println("            else ");
                writer.println("                flattened.add(e);");
                writer.println("        }");
                writer.println("        return flattened;");
                writer.println("    }");
                writer.println("}");
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            for (String head : elements.keySet()) {

                Map<String, NUM> symbols = elements_per_nt.get(head);

                for (Map.Entry<String, Map<String, NUM>> entry : elements.get(head).entrySet()) {

                    String name = head + (entry.getKey().equals("Impl")? entry.getKey() : entry.getKey() + "Impl");
                    file = new File(path + language.toLowerCase() + "/gen/psi/impl/" + name + ".java");

                    boolean declaration = head.endsWith("$Declaration");
                    boolean reference = head.endsWith("$Reference");
                    try {
                        PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
                        writer.println("package " + language.toLowerCase() + ".gen.psi.impl;");
                        writer.println();
                        writer.println("/* This file has been generated. */");
                        writer.println();
                        writer.println("import com.intellij.psi.PsiElement;");
                        writer.println("import com.intellij.psi.PsiElementVisitor;");
                        if (reference) writer.println("import com.intellij.psi.PsiReference;");
                        writer.println("import com.intellij.psi.util.PsiTreeUtil;");
                        writer.println();
                        writer.println("import com.intellij.extapi.psi.ASTWrapperPsiElement;");
                        writer.println("import com.intellij.lang.ASTNode;");
                        if (reference) writer.println("import com.intellij.openapi.util.TextRange;");
                        writer.println();
                        if (declaration || reference)
                            writer.println("import com.intellij.util.IncorrectOperationException;");
                        writer.println();
                        if (declaration || reference)
                            writer.println("import " + language.toLowerCase() + ".gen.utils." + language + "ElementFactory;");
                        if (reference)
                            writer.println("import " + language.toLowerCase() + ".gen.utils." + language + "Util;");
                        writer.println();
                        writer.println("import java.util.List;");
                        writer.println("import java.util.ArrayList;");
                        writer.println();
                        writer.println("import " + language.toLowerCase() + ".gen.psi.*;");
                        writer.println();
                        writer.println("public class " + name + " extends ASTWrapperPsiElement implements I" + head + " {");
                        writer.println();
                        writer.println("    public " + name + "(ASTNode node) { super(node); }");
                        writer.println();
                        writer.println("    public void accept(PsiElementVisitor visitor) { super.accept(visitor); }");
                        writer.println();

                        for (String symbol : symbols.keySet()) {

                            NUM num1 = symbols.get(symbol);
                            NUM num2 = entry.getValue().get(symbol);

                            switch (num1) {
                                case ONE:
                                    if (num2 == null) {
                                        if (symbol.endsWith("$Ebnf"))
                                            writer.println("    public List<PsiElement> get" + symbol.substring(0, symbol.lastIndexOf("$")) + "List() { return null; }");
                                        else
                                            writer.println("    public I" + symbol + " get" + symbol + "() { return null; }");
                                    } else {
                                        if (symbol.endsWith("$Ebnf"))
                                            writer.println("    public List<PsiElement> get" + symbol.substring(0, symbol.lastIndexOf("$")) + "List() { return findNotNullChildByClass(IEbnfElement.class).getElements(); }");
                                        else
                                            writer.println("    public I" + symbol + " get" + symbol + "() { return findNotNullChildByClass(I" + symbol + ".class); }");
                                    }
                                    break;
                                case MORE_THAN_ONE:
                                    if (num2 == null) {
                                        if (symbol.endsWith("$Ebnf"))
                                            writer.println("    public List<List<PsiElement>> getAll" + symbol.substring(0, symbol.lastIndexOf("$")) + "List() { return null; }");
                                        else
                                            writer.println("    public List<I" + symbol + "> getAll" + symbol + "() { return null; }");
                                    } else {
                                        if (symbol.endsWith("$Ebnf")) {
                                            writer.println("    public List<List<PsiElement>> getAll" + symbol.substring(0, symbol.lastIndexOf("$")) + "List() {");
                                            writer.println("        List<List<PsiElement>> result = new ArrayList<>();");
                                            writer.println("        for (IEbnfElement e : PsiTreeUtil.getChildrenOfTypeAsList(this, IEbnfElement.class))");
                                            writer.println("            result.add(e.getElements());");
                                            writer.println("        return result;");
                                            writer.println("    }");
                                        } else
                                            writer.println("    public List<I" + symbol + "> getAll" + symbol + "() { return PsiTreeUtil.getChildrenOfTypeAsList(this, I" + symbol + ".class); }");
                                    }
                                    break;
                                case ONE_AND_MORE:
                                    if (num2 == null) {
                                        if (symbol.endsWith("$Ebnf")) {
                                            writer.println("    public List<PsiElement> get" + symbol.substring(0, symbol.lastIndexOf("$")) + "List() { return null; }");
                                            writer.println("    public List<List<PsiElement>> getAll" + symbol.substring(0, symbol.lastIndexOf("$")) + "List() { return null; }");
                                        } else {
                                            writer.println("    public I" + symbol + " get" + symbol + "() { return null; }");
                                            writer.println("    public List<I" + symbol + "> getAll" + symbol + "() { return null; }");
                                        }
                                    } else {
                                        switch (num2) {
                                            case ONE:
                                                if (symbol.endsWith("$Ebnf")) {
                                                    writer.println("    public List<PsiElement> get" + symbol.substring(0, symbol.lastIndexOf("$")) + "List() { return findNotNullChildByClass(IEbnfElement.class).getElements(); }");
                                                    writer.println("    public List<List<PsiElement>> getAll" + symbol.substring(0, symbol.lastIndexOf("$")) + "List() { return null; }");
                                                } else {
                                                    writer.println("    public I" + symbol + " get" + symbol + "() { return findNotNullChildByClass(I" + symbol + ".class); }");
                                                    writer.println("    public List<I" + symbol + "> getAll" + symbol + "() { return null; }");
                                                }
                                                break;
                                            case MORE_THAN_ONE:
                                            case ONE_AND_MORE:
                                                if (symbol.endsWith("$Ebnf")) {
                                                    writer.println("    public List<PsiElement> get" + symbol.substring(0, symbol.lastIndexOf("$")) + "List() { return null; }");
                                                    writer.println("    public List<List<PsiElement>> getAll" + symbol.substring(0, symbol.lastIndexOf("$")) + "List() {");
                                                    writer.println("        List<List<PsiElement>> result = new ArrayList<>();");
                                                    writer.println("        for (IEbnfElement e : PsiTreeUtil.getChildrenOfTypeAsList(this, IEbnfElement.class))");
                                                    writer.println("            result.add(e.getElements());");
                                                    writer.println("        return result;");
                                                    writer.println("    }");
                                                } else {
                                                    writer.println("    public I" + symbol + " get" + symbol + "() { return null; }");
                                                    writer.println("    public List<I" + symbol + "> getAll" + symbol + "() { return PsiTreeUtil.getChildrenOfTypeAsList(this, I" + symbol + ".class); }");
                                                }
                                                break;
                                        }
                                    }
                                    break;
                                default:
                            }
                        }

                        if (declaration) {
                            writer.println();
                            writer.println("    public String getName() { return getNode().getText(); }");
                            writer.println();
                            writer.println("    public PsiElement setName(String name) throws IncorrectOperationException {");
                            writer.println("        ASTNode node = " + language + "ElementFactory.create" + head.substring(0, head.length() - 12) + "(getProject(), name);");
                            writer.println("        ASTNode first = getNode().getFirstChildNode();");
                            writer.println("        getNode().replaceChild(first, node);");
                            writer.println("        return this;");
                            writer.println("    }");
                        }

                        if (reference) {
                            writer.println();
                            writer.println("    public PsiReference[] getReferences() { return new PsiReference[] {this}; }");
                            writer.println();
                            writer.println("    public PsiReference getReference() { return this; }");
                            writer.println();
                            writer.println("    public PsiElement getElement() { return this; }");
                            writer.println();
                            writer.println("    public TextRange getRangeInElement() { return new TextRange(0, getTextLength()); }");
                            writer.println();
                            writer.println("    public PsiElement resolve() {");
                            writer.println("        PsiElement element = " + language + "Util.find" + head.substring(0, head.length() - 10) + "(getProject(), this);");
                            writer.println("        return element;");
                            writer.println("    }");
                            writer.println();
                            writer.println("    public String getCanonicalText() { return this.getText(); }");
                            writer.println();
                            writer.println("    public PsiElement handleElementRename(String name) throws IncorrectOperationException {");
                            writer.println("        ASTNode node = " + language + "ElementFactory.create" + head.substring(0, head.length() - 10) + "(getProject(), name);");
                            writer.println("        ASTNode first = getNode().getFirstChildNode();");
                            writer.println("        getNode().replaceChild(first, node);");
                            writer.println("        return this;");
                            writer.println("    }");
                            writer.println();
                            writer.println("    public PsiElement bindToElement(PsiElement element) throws IncorrectOperationException { return null; }");
                            writer.println();
                            writer.println("    public boolean isReferenceTo(PsiElement element) {");
                            writer.println("        return element instanceof " + head.substring(0, head.length() - 10) + "$DeclarationImpl");
                            writer.println("                 && element.getTextLength() == this.getTextLength()");
                            writer.println("                 && element.getText().equals(this.getText());");
                            writer.println("    }");
                            writer.println();
                            writer.println("    public Object[] getVariants() { return new Object[0]; }");
                            writer.println();
                            writer.println("    public boolean isSoft() { return false; }");
                        }

                        writer.println("}");
                        writer.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static class InferPsiEbnfElementType implements ISymbolVisitor<String> {

        @Override
        public String visit(Align symbol) {
            return symbol.getSymbol().accept(this);
        }

        @Override
        public String visit(Block symbol) {
            String type = null;
            for (Symbol sym : symbol.getSymbols()) {
                String curr = sym.accept(this);
                if (type != null && curr != null && !type.equals(curr))
                    return "PsiElement";
                else if (type == null && curr != null)
                    type = curr;
            }
            return type;
        }

        @Override
        public String visit(Code symbol) {
            return symbol.getSymbol().accept(this);
        }

        @Override
        public String visit(Conditional symbol) {
            return symbol.getSymbol().accept(this);
        }

        @Override
        public String visit(IfThen symbol) {
            return symbol.getThenPart().accept(this);
        }

        @Override
        public String visit(IfThenElse symbol) {
            String thenPart = symbol.getThenPart().accept(this);
            String elsePart = symbol.getElsePart().accept(this);
            if (thenPart != null && elsePart != null && !thenPart.equals(elsePart))
                return "PsiElement";
            else if (thenPart == null && elsePart != null)
                return elsePart;
            else if (thenPart != null && (elsePart == null || thenPart.equals(elsePart)))
                return thenPart;
            return null;
        }

        @Override
        public String visit(Ignore symbol) {
            return symbol.getSymbol().accept(this);
        }

        @Override
        public String visit(Nonterminal symbol) {
            return symbol.getName();
        }

        @Override
        public String visit(Offside symbol) {
            return symbol.getSymbol().accept(this);
        }

        @Override
        public String visit(Terminal symbol) {
            return null;
        }

        @Override
        public String visit(While symbol) {
            return symbol.getBody().accept(this);
        }

        @Override
        public String visit(Return symbol) {
            return null;
        }

        @Override
        public <E extends Symbol> String visit(Alt<E> symbol) {
            String type = null;
            for (Symbol sym : symbol.getSymbols()) {
                String res = sym.accept(this);
                if (type != null && res != null && !type.equals(res))
                    return "PsiElement";
                else if (type == null && res != null)
                    type = res;
            }
            return type;
        }

        @Override
        public String visit(Opt symbol) {
            return symbol.getSymbol().accept(this);
        }

        @Override
        public String visit(Plus symbol) {
            for (Symbol sep : symbol.getSeparators())
                if (sep.accept(this) != null)
                    return "PsiElement";
            return symbol.getSymbol().accept(this);
        }

        @Override
        public <E extends Symbol> String visit(Sequence<E> symbol) {
            String type = null;
            for (Symbol sym : symbol.getSymbols()) {
                String res = sym.accept(this);
                if (type != null && res != null && !type.equals(res))
                    return "PsiElement";
                else if (type == null && res != null)
                    type = res;
            }
            return type;
        }

        @Override
        public String visit(Star symbol) {
            for (Symbol sep : symbol.getSeparators())
                if (sep.accept(this) != null)
                    return "PsiElement";
            return symbol.getSymbol().accept(this);
        }

        @Override
        public String visit(Start start) {
            return start.getNonterminal().accept(this);
        }
    }
}
