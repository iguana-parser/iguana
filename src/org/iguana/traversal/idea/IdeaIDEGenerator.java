package org.iguana.traversal.idea;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.Terminal.Category;
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

        if (grammar.getLayout() instanceof Terminal) // That is, defined as a token
            grammar.getLayout().accept(new CollectRegularExpressions(terminals));

        new ToJFlexGenerator(language, path, terminals, tokenTypes).generate();

        generateBasicHighlighter(language, path, tokenTypes);

        Map<String, Set<String>> elements = new LinkedHashMap<>();

        for (Rule rule : grammar.getRules()) {
            if (!rule.getHead().getName().equals("$default$")) {
                Set<String> labels = elements.get(rule.getHead().getName());
                if (labels == null) {
                    labels = new HashSet<>();
                    elements.put(rule.getHead().getName(), labels);
                }
                labels.add(rule.getLabel() == null ? "Impl" : rule.getLabel());
            }
        }

        generateElementTypes(elements, language, path);

        generatePhiElements(grammar.getRules(), language, path);

        generateParserDefinition(language, path);
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

        private int regexs = 0;

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
            if (regexs != 0) return null;
            terminals.put(symbol.getName(), symbol);
            return null;
        }

        @Override
        public Void visit(CharacterRange symbol) {
            if (regexs != 0) return null;
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

            if (symbol.category() == Category.REGEX) {
                terminals.put("|regex|:" + symbol.getName(), regex);
                regexs += 1;
                regex.accept(this);
                regexs -= 1;
            } else if (symbol.category() == Category.KEYWORD)
                terminals.put("|keyword|:" + symbol.getName(), regex);
            else
                terminals.put(symbol.getName(), regex);

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

            regularExpressions.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith("|keyword|:"))
                    .forEach(entry -> {
                        String regex = entry.getValue().accept(this);
                        String tokenType = getTokenType(regex);

                        if (!seenTokenTypes.contains(tokenType)) {
                            tokenTypes.append("    public IElementType KEYWORD = " +
                                    "new " + language + "TokenType(\"KEYWORD\");").append("\n");
                            seenTokenTypes.add(tokenType);
                        }

                        rules.append(regex + getConditions(entry.getValue().getPostConditions()))
                              .append("\t{ return " + language + "TokenTypes." + tokenType + "; }").append("\n");
                    });

            regularExpressions.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith("|regex|:"))
                    .forEach(entry -> {
                        String tokenType = entry.getKey().replaceFirst("\\|regex\\|:", "").toUpperCase();

                        if (!seenTokenTypes.contains(tokenType)) {
                            seenTokenTypes.add(tokenType);
                            tokenTypes.append("    public IElementType " + tokenType + " = " +
                                                       "new " + language + "TokenType(\"" + tokenType + "\");").append("\n");
                        }

                        macros.append(tokenType + "=" + entry.getValue().accept(this)).append("\n");

                        rules.append("{" + tokenType + "} " + getConditions(entry.getValue().getPostConditions()))
                             .append("\t{ return " + language + "TokenTypes." + tokenType + "; }").append("\n");
                    });

            regularExpressions.entrySet().stream()
                    .filter(entry -> !(entry.getKey().startsWith("|regex|:") || entry.getKey().startsWith("|keyword|:")))
                    .forEach(entry -> {
                        String regex = entry.getValue().accept(this);
                        String tokenType = getTokenType(regex);

                        if (!seenTokenTypes.contains(tokenType)) {
                            tokenTypes.append("    public IElementType " + tokenType + " = " +
                                    "new " + language + "TokenType(\"" + tokenType + "\");").append("\n");
                            seenTokenTypes.add(tokenType);
                        }

                        rules.append(regex + getConditions(entry.getValue().getPostConditions()))
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
                writer.print(tokenTypes.toString());
                writer.println("    public IElementType BAD_CHARACTER = new " + language + "TokenType(\"BAD_CHARACTER\");");
                writer.println();
                writer.println("    public static IElementType get(String name) {");
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

                default: return "TERMINAL";
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
            writer.println("    public IElementType OPT = new " + language + "ElementType(\"OPT\");");     // ? and if-then
            writer.println("    public IElementType ALT = new " + language + "ElementType(\"ALT\");");     // | and if-then-else
            writer.println("    public IElementType SEQ = new " + language + "ElementType(\"SEQ\");");     // () and {}
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

    enum NUM {
        ONE, MORE_THAN_ONE, ONE_AND_MORE
    }

    private static void generatePhiElements(List<Rule> rules, String language, String path) {

        new File(path + language.toLowerCase() + "/gen/psi/impl").mkdir();

        // Symbol names with their occurrence counter, per nonterminal and per label of a rule
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

                try {
                    PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
                    writer.println("package " + language.toLowerCase() + ".gen.psi;");
                    writer.println();
                    writer.println("/* This file has been generated. */");
                    writer.println();
                    writer.println("import com.intellij.psi.PsiElement;");
                    writer.println("import java.util.List;");
                    writer.println();
                    writer.println("public interface I" + head + " extends PsiElement {");
                    for (String symbol : symbols.keySet()) {
                        NUM num = symbols.get(symbol);
                        switch (num) {
                            case ONE:
                                if (symbol.endsWith("$Ebnf"))
                                    writer.println("    public List<PsiElement> get" + symbol.substring(0, symbol.lastIndexOf("$")) + "List();");
                                else
                                    writer.println("    public I" + symbol + " get" + symbol + "();");
                                break;
                            case MORE_THAN_ONE:
                                if (symbol.endsWith("$Ebnf"))
                                    writer.println("    public List<List<PsiElement>> get" + symbol.substring(0, symbol.lastIndexOf("$")) + "Lists();");
                                else
                                    writer.println("    public List<I" + symbol + "> get" + symbol + "s();");
                                break;
                            case ONE_AND_MORE:
                                if (symbol.endsWith("$Ebnf"))
                                    writer.println("    public List<PsiElement> get" + symbol.substring(0, symbol.lastIndexOf("$")) + "List();");
                                else
                                    writer.println("    public I" + symbol + " get" + symbol + "();");

                                if (symbol.endsWith("$Ebnf"))
                                    writer.println("    public List<List<PsiElement>> get" + symbol.substring(0, symbol.lastIndexOf("$")) + "Lists();");
                                else
                                    writer.println("    public List<I" + symbol + "> get" + symbol + "s();");
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
                                            writer.println("    public List<List<PsiElement>> get" + symbol.substring(0, symbol.lastIndexOf("$")) + "Lists() { return null; }");
                                        else
                                            writer.println("    public List<I" + symbol + "> get" + symbol + "s() { return null; }");
                                    } else {
                                        if (symbol.endsWith("$Ebnf")) {
                                            writer.println("    public List<List<PsiElement>> get" + symbol.substring(0, symbol.lastIndexOf("$")) + "Lists() {");
                                            writer.println("        List<List<PsiElement>> result = new ArrayList<>();");
                                            writer.println("        for (IEbnfElement e : PsiTreeUtil.getChildrenOfTypeAsList(this, IEbnfElement.class))");
                                            writer.println("            result.add(e.getElements());");
                                            writer.println("        return result;");
                                            writer.println("    }");
                                        } else
                                            writer.println("    public List<I" + symbol + "> get" + symbol + "s() { return PsiTreeUtil.getChildrenOfTypeAsList(this, I" + symbol + ".class); }");
                                    }
                                    break;
                                case ONE_AND_MORE:
                                    if (num2 == null) {
                                        if (symbol.endsWith("$Ebnf")) {
                                            writer.println("    public List<PsiElement> get" + symbol.substring(0, symbol.lastIndexOf("$")) + "List() { return null; }");
                                            writer.println("    public List<List<PsiElement>> get" + symbol.substring(0, symbol.lastIndexOf("$")) + "Lists() { return null; }");
                                        } else {
                                            writer.println("    public I" + symbol + " get" + symbol + "() { return null; }");
                                            writer.println("    public List<I" + symbol + "> get" + symbol + "s() { return null; }");
                                        }
                                    } else {
                                        switch (num2) {
                                            case ONE:
                                                if (symbol.endsWith("$Ebnf")) {
                                                    writer.println("    public List<PsiElement> get" + symbol.substring(0, symbol.lastIndexOf("$")) + "List() { return findNotNullChildByClass(EbnfElement.class).getElements(); }");
                                                    writer.println("    public List<List<PsiElement>> get" + symbol.substring(0, symbol.lastIndexOf("$")) + "Lists() { return null; }");
                                                } else {
                                                    writer.println("    public I" + symbol + " get" + symbol + "() { return findNotNullChildByClass(I" + symbol + ".class); }");
                                                    writer.println("    public List<I" + symbol + "> get" + symbol + "s() { return null; }");
                                                }
                                                break;
                                            case MORE_THAN_ONE:
                                            case ONE_AND_MORE:
                                                if (symbol.endsWith("$Ebnf")) {
                                                    writer.println("    public List<PsiElement> get" + symbol.substring(0, symbol.lastIndexOf("$")) + "List() { return null; }");
                                                    writer.println("    public List<List<PsiElement>> get" + symbol.substring(0, symbol.lastIndexOf("$")) + "Lists() {");
                                                    writer.println("        List<List<PsiElement>> result = new ArrayList<>();");
                                                    writer.println("        for (IEbnfElement e : PsiTreeUtil.getChildrenOfTypeAsList(this, IEbnfElement.class))");
                                                    writer.println("            result.add(e.getElements());");
                                                    writer.println("        return result;");
                                                    writer.println("    }");
                                                } else {
                                                    writer.println("    public I" + symbol + " get" + symbol + "() { return null; }");
                                                    writer.println("    public List<I" + symbol + "> get" + symbol + "s() { return PsiTreeUtil.getChildrenOfTypeAsList(this, I" + symbol + ".class); }");
                                                }
                                                break;
                                        }
                                    }
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
    }

    private static void generateParserDefinition(String language, String path) {
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
            writer.println("import iguana.parsetrees.tree.TermBuilder;");
            writer.println("import iguana.utils.input.Input;");
            writer.println("import org.iguana.grammar.Grammar;");
            writer.println("import org.iguana.grammar.GrammarGraph;");
            writer.println("import org.iguana.grammar.symbol.Nonterminal;");
            writer.println("import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;");
            writer.println("import org.iguana.grammar.transformation.EBNFToBNF;");
            writer.println("import org.iguana.grammar.transformation.LayoutWeaver;");
            writer.println("import org.iguana.parser.GLLParser;");
            writer.println("import org.iguana.parser.ParseResult;");
            writer.println("import org.iguana.parser.ParserFactory;");
            writer.println("import org.iguana.util.Configuration;");
            writer.println();
            writer.println("public class " + language + "Parser implements PsiParser {");
            writer.println();
            writer.println("    private Grammar grammar;");
            writer.println("    private GrammarGraph graph;");
            writer.println("    private GLLParser parser;");
            writer.println();
            writer.println("    public ASTNode parse(IElementType root, PsiBuilder builder) {");
            writer.println("        Input input = Input.fromString(builder.getOriginalText().toString());");
            writer.println("        if (parser == null) {");
            writer.println("            grammar = Grammar.load(this.getClass().getClassLoader().getResourceAsStream(\"" + language.toLowerCase() + "/gen/parser/grammar/" + language + "\"));");
            writer.println("            DesugarPrecedenceAndAssociativity precedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();");
            writer.println("            precedenceAndAssociativity.setOP2();");
            writer.println("            grammar = new EBNFToBNF().transform(grammar);");
            writer.println("            grammar = precedenceAndAssociativity.transform(grammar);");
            writer.println("            grammar = new LayoutWeaver().transform(grammar);");
            writer.println("            graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);");
            writer.println("            parser = ParserFactory.getParser();");
            writer.println("        }");
            writer.println("        ParseResult result = parser.parse(input, graph, Nonterminal.withName(\"\"));");
            writer.println("        if (result.isParseSuccess()) {");
            writer.println("            System.out.println(\"Success...\");");
            writer.println("            NonterminalNode sppf = result.asParseSuccess().getSPPFNode();");
            writer.println("            ASTNode ast = TermBuilder.build_no_memo(sppf, new " + language + "TreeBuilder(input));");
            writer.println("            return ast;");
            writer.println("        } else {");
            writer.println("            System.out.println(\"Parse error...\");");
            writer.println("            return Factory.createErrorElement(\"Sorry, you have a parse error.\");");
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
            writer.println("    public static final IFileElementType FILE = new IFileElementType(Language.<" + language + "Lang>findInstance(" + language + "Lang.class));");
            writer.println();
            writer.println("    public Lexer createLexer(Project project) { return new " + language + "Lexer(); }");
            writer.println();
            writer.println("    public PsiParser createParser(Project project) { return new " + language + "Parser(); }");
            writer.println();
            writer.println("    public IFileElementType getFileNodeType() { return FILE; }");
            writer.println();
            writer.println("    public TokenSet getWhitespaceTokens() { return TokenSet.EMPTY; }");
            writer.println();
            writer.println("    public TokenSet getCommentTokens() { return TokenSet.EMPTY; }");
            writer.println();
            writer.println("    public TokenSet getStringLiteralElements() { return TokenSet.EMPTY; }");
            writer.println();
            writer.println("    public PsiElement createElement(ASTNode node) { return " + language + "ElementTypes.Factory.createElement(node); }");
            writer.println();
            writer.println("    public PsiFile createFile(FileViewProvider viewProvider) { return new " + language + "File(viewProvider); } ");
            writer.println();
            writer.println("    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) { return SpaceRequirements.MAY; }");
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
            writer.println("    public " + language + "File(FileViewProvider viewProvider) { super(viewProvider, " + language + "Lang.instance); }");
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

        generateTreeBuider(language, path);
    }

    private static void generateTreeBuider(String language, String path) {
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
            writer.println("    private final Input input;");
            writer.println();
            writer.println("    public " + language + "TreeBuilder(Input input) { this.input = input; }");
            writer.println();
            writer.println("    public TreeElement terminalNode(int l, int r) { return ASTFactory.leaf(" + language + "TokenTypes.CHARACTER, input.subString(l, r)); }");
            writer.println();
            writer.println("    public TreeElement terminalNode(String name, int l, int r) {");
            writer.println("        IElementType tokenType = " + language + "TokenTypes.get(name);");
            writer.println("        return ASTFactory.leaf(tokenType, input.subString(l, r));");
            writer.println("    }");
            writer.println();
            writer.println("    public TreeElement nonterminalNode(RuleType type, Seq<TreeElement> children, int l, int r) {");
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
            writer.println("    public TreeElement cycle() { throw new RuntimeException(\"Not yet supported in the idea tree builder: cycles.\"); }");
            writer.println();
            writer.println("    public Branch<TreeElement> branch(Seq<TreeElement> children) { throw new RuntimeException(\"Not yet supported in the idea tree builder: ambiguity.\"); } ");
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
            writer.println("    public TreeElement epsilon(int i) { return ASTFactory.leaf(" + language + "TokenTypes.CHARACTER, \"\"); }");
            writer.println("}");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
