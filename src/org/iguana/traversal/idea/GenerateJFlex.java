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

import iguana.regex.Char;
import iguana.regex.*;
import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.condition.RegularExpressionCondition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

//import iguana.util.unicode.UnicodeUtil;

/**
 * Created by Anastasia Izmaylova on 17/12/15.
 */
class GenerateJFlex implements RegularExpressionVisitor<String> {
    /*
     * <Lang>TokenTypes.java
     * <Lang>.flex
     * <Lang>Lexer.java
     */
    private final String language;
    private final String path;

    private final Map<String, RegularExpression> regularExpressions;

    private final Set<String> seenTokenTypes;

    private final StringBuffer header;
    private final StringBuffer macros;
    private final StringBuffer rules;
    private final StringBuffer tokens;

    public GenerateJFlex(String language, String path, Map<String, RegularExpression> regularExpressions, Set<String> seenTokenTypes) {
        this.language = language;
        this.path = path;
        this.regularExpressions = regularExpressions;
        this.seenTokenTypes = seenTokenTypes;
        this.header = new StringBuffer();
        this.macros = new StringBuffer();
        this.rules = new StringBuffer();
        this.tokens = new StringBuffer();
    }

    public void generate() {

        header.append("package " + language.toLowerCase() + ".gen.lexer;\n\n");
        header.append("import com.intellij.lexer.FlexLexer;\n");
        header.append("import com.intellij.psi.tree.IElementType;\n");
        header.append("import " + language.toLowerCase() + ".gen.psi." + language + "TokenTypes;\n\n");
        header.append("%%\n\n");
        header.append("%public").append("\n");
        header.append("%class " + "_" + language + "Lexer").append("\n");
        header.append("%implements FlexLexer").append("\n");
        header.append("%function advance").append("\n");
        header.append("%type IElementType").append("\n");
        header.append("%unicode").append("\n");
        header.append("%eof{").append("\n");
        header.append("  return;").append("\n");
        header.append("%eof}").append("\n\n");

        rules.append("%%").append("\n").append("\n");
        rules.append("<YYINITIAL> {").append("\n");

        seenTokenTypes.add("Keyword");
        tokens.append("    IElementType Keyword = new " + language + "TokenType(\"Keyword\");")
                .append("\n");
        regularExpressions.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith("|keyword|:"))
            .forEach(entry -> {
                String regex = entry.getValue().accept(this);
                rules.append(regex + getLookaheads(entry.getValue().getLookaheads()))
                        .append("\t{ return " + language + "TokenTypes.Keyword; }").append("\n");
            });

        regularExpressions.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith("|regex|:"))
            .forEach(entry -> {
                String tokenType = entry.getKey().replaceFirst("\\|regex\\|:", "").toUpperCase();

                if (!seenTokenTypes.contains(tokenType)) {
                    seenTokenTypes.add(tokenType);
                    tokens.append("    IElementType " + tokenType + " = new " + language + "TokenType(\"" + tokenType + "\");")
                            .append("\n");
                }

                macros.append(tokenType + "=" + entry.getValue().accept(this)).append("\n");
                rules.append("{" + tokenType + "} " + getLookaheads(entry.getValue().getLookaheads()))
                        .append("\t{ return " + language + "TokenTypes." + tokenType + "; }").append("\n");
            });

        regularExpressions.entrySet().stream()
            .filter(entry -> !(entry.getKey().startsWith("|regex|:") || entry.getKey().startsWith("|keyword|:")))
            .forEach(entry -> {
                String regex = entry.getValue().accept(this);
                String tokenType = getTokenType(regex);

                if (!seenTokenTypes.contains(tokenType)) {
                    seenTokenTypes.add(tokenType);
                    tokens.append("    IElementType " + tokenType + " = new " + language + "TokenType(\"" + tokenType + "\");")
                            .append("\n");
                }

                rules.append(regex + getLookaheads(entry.getValue().getLookaheads()))
                        .append("\t{ return " + language + "TokenTypes." + tokenType + "; }").append("\n");
            });

        rules.append("[^]").append("\t { return " + language + "TokenTypes.BAD_CHARACTER; }\n");
        rules.append("}").append("\n");

        seenTokenTypes.add("BAD_CHARACTER");

        File file = new File(path + language.toLowerCase() + "/gen/psi/" + language + "TokenTypes.java");
        try {
            PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
            writer.println("package " + language.toLowerCase() + ".gen.psi;");
            writer.println();
            writer.println("/** This file has been generated. */");
            writer.println();
            writer.println("import com.intellij.psi.tree.IElementType;");
            writer.println("import " + language.toLowerCase() + ".gen.psi." + language + "TokenType;");
            writer.println();
            writer.println("public interface " + language + "TokenTypes {");
            writer.println();
            writer.print(tokens.toString());
            if (!seenTokenTypes.contains("TERMINAL"))
                writer.println("    IElementType TERMINAL = new " + language + "TokenType(\"TERMINAL\");");
            writer.println("    IElementType BAD_CHARACTER = new " + language + "TokenType(\"BAD_CHARACTER\");");
            writer.println();
            writer.println("    static IElementType get(String name) {");
            writer.println("        switch (name) {");
            for (String tokenType : seenTokenTypes)
                writer.println("            case \"" + tokenType + "\": return " + tokenType + ";");
            writer.println("            default: return TERMINAL;");
            writer.println("        }");
            writer.println("    }");
            writer.println("}");
            writer.println();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        new File(path + language.toLowerCase() + "/gen/lexer").mkdir();
        file = new File(path + language.toLowerCase() + "/gen/lexer/" + language + ".flex");
        try {
            PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
            writer.println(header.toString());
            writer.println(macros.toString());
            writer.println(rules.toString());
            writer.println();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        file = new File(path + language.toLowerCase() + "/gen/lexer/" + language + "Lexer.java");
        try {
            PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
            writer.println("package " + language.toLowerCase() + ".gen.lexer;");
            writer.println();
            writer.println("/** This file has been generated. */");
            writer.println();
            writer.println("import com.intellij.lexer.FlexAdapter;");
            writer.println("import java.io.Reader;");
            writer.println();
            writer.println("public class " + language + "Lexer extends FlexAdapter {");
            writer.println("    public " + language + "Lexer() { super(new _" + language + "Lexer((Reader) null)); }");
            writer.println("}");
            writer.println();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String visit(EOF eof) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String visit(Epsilon e) {
        return "";
    }

    @Override
    public String visit(Char c) {
        return getChar(c.getValue());
    }

    @Override
    public String visit(CharRange r) {
        return "[" +  getRange(r) + "]";
    }

    @Override
    public String visit(iguana.regex.Star s) {
        return s.getSymbol().accept(this) + "*";
    }

    @Override
    public String visit(iguana.regex.Plus p) {
        return p.getSymbol().accept(this) + "+";
    }

    @Override
    public String visit(iguana.regex.Opt o) {
        return o.getSymbol().accept(this) + "?";
    }

    @Override
    public <E extends RegularExpression> String visit(iguana.regex.Alt<E> symbol) {
        Map<Boolean, List<E>> parition = symbol.getSymbols().stream().collect(Collectors.partitioningBy(s -> isCharClass(s)));
        List<E> charClasses = parition.get(true);
        List<E> other = parition.get(false);

        StringBuilder sb = new StringBuilder();

        if (!charClasses.isEmpty() && !other.isEmpty()) {
            int left  = charClasses.size();
            int right = other.stream().map(s -> (RegularExpression) s).mapToInt(r -> r.length()).max().getAsInt();

            sb.append("(");
            if (left > right) {
                sb.append("[" + charClasses.stream().map(s -> asCharClass(s)).collect(Collectors.joining()) + "]");
                sb.append("|");
                sb.append(other.stream().map(s -> s.accept(this)).collect(Collectors.joining("|")));
            } else {
                sb.append(other.stream().sorted(RegularExpression.lengthComparator()).map(s -> s.accept(this)).collect(Collectors.joining("|")));
                sb.append("|");
                sb.append("[" + charClasses.stream().map(s -> asCharClass(s)).collect(Collectors.joining()) + "]");
            }
            sb.append(")");
        }
        else if (!charClasses.isEmpty()) {
            sb.append("[" + charClasses.stream().map(s -> asCharClass(s)).collect(Collectors.joining()) + "]");
        }
        else {
            sb.append("(" + other.stream().sorted(RegularExpression.lengthComparator()).map(s -> s.accept(this)).collect(Collectors.joining("|")) + ")");
        }

        return sb.toString();
    }

    @Override
    public <E extends RegularExpression> String visit(iguana.regex.Seq<E> symbol) {

        List<E> symbols = symbol.getSymbols();

        if (symbols.size() == 1)
            return "[" + symbols.get(0).accept(this) + "]";

        return "(" + symbols.stream().map(s -> s.accept(this)).collect(Collectors.joining()) + ")";
    }

    @Override
    public String visit(iguana.regex.Reference reference) {
        throw new RuntimeException();
    }

    private boolean isCharClass(RegularExpression s) {
        if (!s.getLookaheads().isEmpty()) return false;
        return s instanceof Char || s instanceof CharRange;
    }

    private String asCharClass(RegularExpression s) {
        if (s instanceof Char) {
            Char c = (Char) s;
            return getChar(c.getValue());
        }
        else if (s instanceof CharRange) {
            CharRange r = (CharRange) s;
            return getRange(r);
        }

        throw new RuntimeException(s + " is not a character or character class.");
    }

    private String getTokenType(String terminal) {
        switch (terminal) {
            case "[\\(]": return "OPEN_PARENTHESIS";
            case "[\\)]": return "CLOSE_PARENTHESIS";
            case "[\\[]": return "OPEN_BRACKET";
            case "[\\]]": return "CLOSE_BRACKET";
            case "[\\{]": return "OPEN_BRACE";
            case "[\\}]": return "CLOSE_BRACE";
            case "\\*]": return "OPERATOR";
            case "[/]": return "OPERATOR";
            case "[\\+]": return "OPERATOR";
            case "[\\-]": return "OPERATOR";
            case "[&]": return "OPERATOR";
            case "[\\|]": return "OPERATOR";
            case "[=]": return "OPERATOR";
            default: return "TERMINAL";
        }
    }

    private String getConditions(Set<Condition> conditions) {
        StringBuffer code = new StringBuffer();
        for (Condition condition : conditions)
            code.append(getCondition(condition));

        return code.toString();
    }

    private String getLookaheads(Set<CharRange> lookaheads) {
        return "";
    }

    private String getLookbehinds(Set<CharRange> lookbehinds) {
        return "";
    }

    private String getCondition(Condition condition) {
        switch (condition.getType()) {
            case NOT_FOLLOW:
                return "/!(" + getRegularExpression(condition).accept(this) + ")";
            case FOLLOW:
                return "/(" + getRegularExpression(condition).accept(this) + ")";
            default: return "";
        }
    }

    private static RegularExpression getRegularExpression(Condition condition) {
        if (condition instanceof RegularExpressionCondition) {
            return ((RegularExpressionCondition) condition).getRegularExpression();
        }
        throw new UnsupportedOperationException();
    }

    private String getChar(int c) {
        if(CharacterRanges.isPrintableAscii(c))
            return escape((char) c + "");
        else
            return escape(String.format("\\u%04X", c));
    }

    private String getRange(CharRange r) {
        return getChar(r.getStart()) + "-" + getChar(r.getEnd());
    }

    private String escape(String s) {
        String backslash = "\\";
        switch (s) {
            case "(":
            case ")":
            case "[":
            case "]":
            case "{":
            case "}":
            case "\\":
            case "\"":
            case "^":
            case "-":
            case "=":
            case "$":
            case "!":
            case "|":
            case "?":
            case "*":
            case "+":
            case ".":
                return backslash + s;
            default:
                return s;
        }
    }

}
