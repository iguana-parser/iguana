package org.iguana.traversal.idea;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.symbol.Character;
import org.iguana.regex.*;
import org.iguana.traversal.ISymbolVisitor;
import org.iguana.traversal.RegularExpressionVisitor;
import org.iguana.util.unicode.UnicodeUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Anastasia Izmaylova on 28/09/15.
 */

public class IdeaIDEGenerator {

    public void generate(Grammar grammar, String language, String extension, String path) {

        path = path.endsWith("/")? path : path + "/";

        generateBasicFiles(language, extension, path);

        Set<String> tokenTypes = new HashSet<>();
        Map<String, RegularExpression> terminals = new LinkedHashMap<>();
        for (Rule rule : grammar.getRules()) {
            for (Symbol symbol : rule.getBody())
                symbol.accept(new CollectRegularExpressions(terminals));
        }

        if (grammar.getLayout().isTerminal()) // That is, defined as a token
            grammar.getLayout().accept(new CollectRegularExpressions(terminals));

        new ToJFlexGenerator(language, path, terminals, tokenTypes).generate();

        generateBasicHighlighter(language, path, tokenTypes);

        Set<String> elements = new LinkedHashSet<>();

        for (Rule rule : grammar.getRules())
            elements.add(rule.getHead().getName());

        generateElementTypes(elements, language, path);

        generatePhiElements(grammar.getRules(), language, path);
    }

    private void generateBasicFiles(String language, String extension, String path) {
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
            writer.println("    public static final Icon file = IconLoader.getIcon(\"/" + language.toLowerCase() + "/gen/icons/icon.png\");");
            writer.println("    private " + language + "Lang() { super(\""+ language + "\"); }");
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
            writer.println("    public static final " + language + "FileType instance = new " + language + "FileType();");
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
            writer.println("    public void createFileTypes(FileTypeConsumer fileTypeConsumer) { fileTypeConsumer.consume(" + language + "FileType.instance, \"" + extension + "\"); }");
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
            writer.println("    public " + language + "ElementType(String debugName) { super(debugName, " + language + "Lang.instance); }");
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
            writer.println("    public " + language + "TokenType(String debugName) { super(debugName, " + language + "Lang.instance); }");
            writer.println("    public String toString() { return \"" + language + "TokenType.\" + super.toString(); }");
            writer.println("}");
            writer.println();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static class CollectRegularExpressions implements ISymbolVisitor<Void> {

        private final Map<String, RegularExpression> terminals;

        private int tokens = 0;

        public CollectRegularExpressions(Map<String, RegularExpression> terminals) {
            this.terminals = terminals;
        }

        @Override
        public Void visit(Align symbol) {
            return symbol.getSymbol().accept(this);
        }

        @Override
        public Void visit(Block symbol) {
            for (Symbol sym : symbol.getSymbols())
                sym.accept(this);
            return null;
        }

        @Override
        public Void visit(org.iguana.grammar.symbol.Character symbol) {
            if (tokens != 0) return null;
            terminals.put(symbol.getName(), symbol);
            return null;
        }

        @Override
        public Void visit(CharacterRange symbol) {
            if (tokens != 0) return null;
            terminals.put(symbol.getName(), symbol);
            return null;
        }

        @Override
        public Void visit(Code symbol) {
            return symbol.getSymbol().accept(this);
        }

        @Override
        public Void visit(Conditional symbol) {
            return symbol.getSymbol().accept(this);
        }

        @Override
        public Void visit(EOF symbol) {
            return null;
        }

        @Override
        public Void visit(Epsilon symbol) {
            return null;
        }

        @Override
        public Void visit(IfThen symbol) {
            return symbol.getThenPart().accept(this);
        }

        @Override
        public Void visit(IfThenElse symbol) {
            symbol.getThenPart().accept(this);
            return symbol.getElsePart().accept(this);
        }

        @Override
        public Void visit(Ignore symbol) {
            return symbol.getSymbol().accept(this);
        }

        @Override
        public Void visit(Nonterminal symbol) {
            return null;
        }

        @Override
        public Void visit(Offside symbol) {
            return symbol.getSymbol().accept(this);
        }

        @Override
        public Void visit(Terminal symbol) {
            RegularExpression regex = symbol.getRegularExpression();
            terminals.put((symbol.token() == 0? "" : "|token|:") + symbol.getName(), regex);
            if (symbol.token() != 0) {
                tokens += 1;
                regex.accept(this);
                tokens -= 1;
            }
            return null;
        }

        @Override
        public Void visit(While symbol) {
            return symbol.getBody().accept(this);
        }

        @Override
        public Void visit(Return symbol) {
            return null;
        }

        @Override
        public <E extends Symbol> Void visit(Alt<E> symbol) {
            for (Symbol sym : symbol.getSymbols())
                sym.accept(this);
            return null;
        }

        @Override
        public Void visit(Opt symbol) {
            return symbol.getSymbol().accept(this);
        }

        @Override
        public Void visit(Plus symbol) {
            for (Symbol sep : symbol.getSeparators())
                sep.accept(this);
            return symbol.getSymbol().accept(this);
        }

        @Override
        public <E extends Symbol> Void visit(Sequence<E> symbol) {
            for (Symbol sym : symbol.getSymbols())
                sym.accept(this);
            return null;
        }

        @Override
        public Void visit(Star symbol) {
            for (Symbol sep : symbol.getSeparators())
                sep.accept(this);
            return symbol.getSymbol().accept(this);
        }

    }

    private static class ToJFlexGenerator implements RegularExpressionVisitor<String> {

        private final String language;
        private final String path;

        private final Map<String, RegularExpression> regularExpressions;

        private final Set<String> seenTokenTypes;

        private final StringBuffer tokenTypes;

        private final StringBuffer header;
        private final StringBuffer macros;
        private final StringBuffer rules;

        public ToJFlexGenerator(String language, String path, Map<String, RegularExpression> regularExpressions, Set<String> seenTokenTypes) {
            this.language = language;
            this.path = path;
            this.regularExpressions = regularExpressions;
            this.seenTokenTypes = seenTokenTypes;

            header = new StringBuffer();

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

            macros = new StringBuffer();
            rules = new StringBuffer();
            tokenTypes = new StringBuffer();
        }

        public void generate() {

            rules.append("%%").append("\n").append("\n");
            rules.append("<YYINITIAL> {").append("\n");

            for (Map.Entry<String, RegularExpression> entry : regularExpressions.entrySet()) {

                if (entry.getKey().startsWith("|token|:")) {

                    String tokenType = entry.getKey().replaceFirst("\\|token\\|:", "").toUpperCase();

                    if (!seenTokenTypes.contains(tokenType)) {
                        tokenTypes.append("    public IElementType " + tokenType + " = " +
                                               "new " + language + "TokenType(\"" + tokenType + "\");").append("\n");
                        seenTokenTypes.add(tokenType);
                    }

                    macros.append(tokenType + "=" + entry.getValue().accept(this)).append("\n");

                    rules.append("{" + tokenType + "} " + getConditions(entry.getValue().getPostConditions()))
                         .append("\t{ return " + language + "TokenTypes." + tokenType + "; }").append("\n");
                }
            }

            for (Map.Entry<String, RegularExpression> entry : regularExpressions.entrySet()) {

                if (!entry.getKey().startsWith("|token|:")) {

                    String regex = entry.getValue().accept(this);
                    String tokenType = getTokenType(regex);

                    if (!seenTokenTypes.contains(tokenType)) {
                        tokenTypes.append("    public IElementType " + tokenType + " = " +
                                               "new " + language + "TokenType(\"" + tokenType + "\");").append("\n");
                        seenTokenTypes.add(tokenType);
                    }

                    rules.append(regex + getConditions(entry.getValue().getPostConditions()))
                         .append("\t{ return " + language + "TokenTypes." + tokenType + "; }").append("\n");
                }
            }

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
                writer.print(tokenTypes.toString());
                writer.println("    public IElementType BAD_CHARACTER = new " + language + "TokenType(\"BAD_CHARACTER\");");
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
        public String visit(Character c) {
            return getChar(c.getValue());
        }

        @Override
        public String visit(CharacterRange r) {
            return "[" +  getRange(r) + "]";
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
        public String visit(Terminal t) {
            return t.getRegularExpression().accept(this);
        }

        @Override
        public String visit(Star s) {
            return s.getSymbol().accept(this) + "*";
        }

        @Override
        public String visit(Plus p) {
            return p.getSymbol().accept(this) + "+";
        }

        @Override
        public String visit(Opt o) {
            return o.getSymbol().accept(this) + "?";
        }

        @Override
        public <E extends Symbol> String visit(Alt<E> symbol) {
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
        public <E extends Symbol> String visit(Sequence<E> symbol) {

            List<E> symbols = symbol.getSymbols();

            if (symbols.size() == 1)
                return "[" + symbols.get(0).accept(this) + "]";

            return "(" + symbols.stream().map(s -> s.accept(this)).collect(Collectors.joining()) + ")";
        }

        private boolean isCharClass(Symbol s) {
            if (!s.getPostConditions().isEmpty()) return false;
            return s instanceof Character || s instanceof CharacterRange;
        }

        private String asCharClass(Symbol s) {
            if (s instanceof Character) {
                Character c = (Character) s;
                return getChar(c.getValue());
            }
            else if (s instanceof CharacterRange) {
                CharacterRange r = (CharacterRange) s;
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

                default: return "CHARACTER";
            }
        }

        private String getConditions(Set<Condition> conditions) {
            StringBuffer code = new StringBuffer();
            for (Condition condition : conditions)
                code.append(getCondition(condition));

            return code.toString();
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
            if(UnicodeUtil.isPrintableAscii(c))
                return escape((char) c + "");
            else
                return escape(String.format("\\u%04X", c));
        }

        private String getRange(CharacterRange r) {
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

    private void generateBasicHighlighter(String language, String path, Set<String> tokenTypes) {
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
                        color = "HighlighterColors." + color;
                    else
                        color = "DefaultLanguageHighlighterColors." + color;

                    writer.println((i ==0? "        if " : "        else if ") + "(tokenType.equals(" + language + "TokenTypes." + tokenType + "))");
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
                if (tokenType.toUpperCase().contains("IDENTIFIER"))
                    return "IDENTIFIER";
                else if (tokenType.toUpperCase().contains("COMMENT"))
                    return "LINE_COMMENT";
                else
                    return null;
        }
    }

    private void generateElementTypes(Set<String> elements, String language, String path) {
        File file = new File(path + language.toLowerCase() + "/gen/psi/" + language + "ElementTypes.java");

        try {
            PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
            writer.println("package " + language.toLowerCase() + ".gen.psi;");
            writer.println();
            writer.println("/* This file has been generated. */");
            writer.println();
            writer.println("import com.intellij.psi.tree.IElementType;");
            writer.println("import " + language.toLowerCase() + ".gen.psi." + language + "ElementType;");
            writer.println();
            writer.println("interface " + language + "ElementTypes {");
            writer.println();
            // ebnf related types, also data-dependent
            writer.println("    public IElementType LIST = new " + language + "ElementType(\"LIST\");"); // * and while
            writer.println("    public IElementType OPT = new " + language + "ElementType(\"OPT\");");     // ? and if-then
            writer.println("    public IElementType ALT = new " + language + "ElementType(\"ALT\");");     // | and if-then-else
            writer.println("    public IElementType SEQ = new " + language + "ElementType(\"SEQ\");");     // () and {}
            writer.println();
            for (String element : elements)
                writer.println("    public IElementType " + element.toUpperCase() + " = new " + language + "ElementType(\"" + element.toUpperCase() + "\");");
            writer.println("}");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void generatePhiElements(List<Rule> rules, String language, String path) {

        new File(path + language.toLowerCase() + "/gen/psi/impl").mkdir();

        // Symbol names with their occurrence counter, per nonterminal and per label of a rule
        Map<String, Map<String, Map<String, Set<Integer>>>> elements = new LinkedHashMap<>();

        for (Rule rule : rules) {
            Map<String, Map<String, Set<Integer>>> m1 = elements.get(rule.getHead().getName());
            if (m1 == null) {
                m1 = new LinkedHashMap<>();
                elements.put(rule.getHead().getName(), m1);
            }

            String label = rule.getLabel();
            if (label == null || label.isEmpty()) label = "Impl";

            Map<String, Set<Integer>> m2 = m1.get(label);

            Map<String, Integer> m3 = new LinkedHashMap<>();
            new GetPhiElements(rule, m3).compute(language, path);

            if (m2 == null) {
                Map<String, Set<Integer>> m = new LinkedHashMap<>();
                for (Map.Entry<String, Integer> entry : m3.entrySet()) {
                    Set<Integer> nums = new HashSet<>();
                    nums.add(entry.getValue());
                    m.put(entry.getKey(), nums);
                }
                m1.put(label, m);
                continue;
            }

            for (Map.Entry<String, Integer> entry : m3.entrySet()) {
                Set<Integer> nums = m2.get(entry.getKey());
                if (nums == null)
                    m2.put(entry.getKey(), new HashSet<>(Arrays.asList(entry.getValue())));
                else
                   nums.add(entry.getValue());
            }
        }

        GetPhiElements.generate(elements, language, path);
    }

    private static class GetPhiElements implements ISymbolVisitor<String> {

        private final Rule rule;
        private final Map<String, Integer> children;

        public GetPhiElements(Rule rule, Map<String, Integer> children) {
            this.rule = rule;
            this.children = children;
        }

        public void compute(String language, String path) {
            for (Symbol symbol : rule.getBody()) {
                String child = symbol.accept(this);
                if (child != null) {
                    Integer num = children.get(child);
                    children.put(child, num == null? 1 : num + 1);
                }
            }
        }

        @Override
        public String visit(Align symbol) {
            return symbol.getSymbol().accept(this);
        }

        @Override
        public String visit(Block symbol) {
            return "EbnfElement";
        }

        @Override
        public String visit(Character symbol) {
            return null;
        }

        @Override
        public String visit(CharacterRange symbol) {
            return null;
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
        public String visit(EOF symbol) {
            return null;
        }

        @Override
        public String visit(Epsilon symbol) {
            return null;
        }

        @Override
        public String visit(IfThen symbol) {
            return "EbnfElement";
        }

        @Override
        public String visit(IfThenElse symbol) {
            return "EbnfElement";
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
            return "EbnfElement";
        }

        @Override
        public String visit(Return symbol) {
            return null;
        }

        @Override
        public <E extends Symbol> String visit(Alt<E> symbol) {
            return "EbnfElement";
        }

        @Override
        public String visit(Opt symbol) {
            return "EbnfElement";
        }

        @Override
        public String visit(Plus symbol) {
            return "EbnfElement";
        }

        @Override
        public <E extends Symbol> String visit(Sequence<E> symbol) {
            return "EbnfElement";
        }

        @Override
        public String visit(Star symbol) {
            return "EbnfElement";
        }

        public static void generate(Map<String, Map<String, Map<String, Set<Integer>>>> elements, String language, String path) {

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

            for (String head : elements.keySet()) {

                file = new File(path + language.toLowerCase() + "/gen/psi/I" + head + ".java");

                Map<String, Set<Integer>> symbols = new HashMap<>();

                for (Map<String, Set<Integer>> m : elements.get(head).values()) {
                    for (Map.Entry<String, Set<Integer>> entry : m.entrySet()) {
                        Set<Integer> nums = symbols.get(entry.getKey());
                        if (nums == null) {
                            nums = new HashSet<>();
                            symbols.put(entry.getKey(), nums);
                        }
                        nums.addAll(entry.getValue());
                    }
                }

                try {
                    PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
                    writer.println("package " + language.toLowerCase() + ".gen.psi;");
                    writer.println();
                    writer.println("/* This file has been generated. */");
                    writer.println();
                    writer.println("import com.intellij.psi.PsiElement;");
                    writer.println();
                    writer.println("public interface I" + head + " extends PsiElement {");
                    for (String symbol : symbols.keySet()) {
                        Set<Integer> nums = symbols.get(symbol);
                        Integer[] arr = new Integer[nums.size()];
                        nums.toArray(arr);
                        Arrays.sort(arr);
                        boolean hasSingle = nums.contains(1);
                        int max = arr[arr.length - 1];
                        if (hasSingle)
                            writer.println("    public I" + symbol + " get" + symbol + "();");
                        if (max > 1) {
                            for (int i = 1; i < max + 1; i++)
                                writer.println("    public I" + symbol + " get" + symbol + i + "();");
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
        }
    }
}
