package org.iguana.util.serialization;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.module.SimpleModule;
import iguana.regex.RegularExpression;
import org.eclipse.imp.pdb.facts.util.ImmutableSet;
import org.iguana.datadependent.ast.AST;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.attrs.AbstractAttrs;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.condition.ConditionType;
import org.iguana.grammar.condition.DataDependentCondition;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.slot.NonterminalNodeType;
import org.iguana.grammar.symbol.*;
import org.iguana.parsetree.*;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class JsonSerializer {

    private final static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE));

//        TypeResolverBuilder<?> typeResolverBuilder = new CustomTypeResolverBuilder();
//        typeResolverBuilder.init(JsonTypeInfo.Id.CUSTOM, new MyTypeIdResolver());
//        typeResolverBuilder.inclusion(JsonTypeInfo.As.PROPERTY);
//        mapper.setDefaultTyping(typeResolverBuilder);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        mapper.addMixIn(Grammar.class, GrammarMixIn.class);
        mapper.addMixIn(Rule.class, RuleMixIn.class);
        mapper.addMixIn(Alternative.class, AlternativeMixIn.class);
        mapper.addMixIn(Sequence.class, SequenceMixIn.class);

        mapper.addMixIn(Symbol.class, SymbolMixIn.class);
        mapper.addMixIn(Nonterminal.class, NonterminalMixIn.class);
        mapper.addMixIn(Terminal.class, TerminalMixIn.class);
        mapper.addMixIn(Star.class, StarMixIn.class);
        mapper.addMixIn(Plus.class, PlusMixIn.class);
        mapper.addMixIn(Opt.class, OptMixIn.class);
        mapper.addMixIn(Alt.class, AltMixIn.class);
        mapper.addMixIn(Group.class, GroupMixIn.class);
        mapper.addMixIn(Start.class, StartMixIn.class);

        mapper.addMixIn(AbstractAttrs.class, AbstractAttrsMixIn.class);
        mapper.addMixIn(Return.class, ReturnMixIn.class);

        // Conditions
        mapper.addMixIn(Condition.class, ConditionMixIn.class);
        mapper.addMixIn(RegularExpressionCondition.class, RegularExpressionConditionMixIn.class);
        mapper.addMixIn(DataDependentCondition.class, DataDependentConditionMixIn.class);

        // Expression
        mapper.addMixIn(Expression.class, ExpressionMixIn.class);
        mapper.addMixIn(Expression.Integer.class, ExpressionMixIn.IntegerMixIn.class);
        mapper.addMixIn(Expression.Real.class, ExpressionMixIn.RealMixIn.class);
        mapper.addMixIn(Expression.String.class, ExpressionMixIn.StringMixIn.class);
        mapper.addMixIn(Expression.Tuple.class, ExpressionMixIn.TupleMixIn.class);
        mapper.addMixIn(Expression.Name.class, ExpressionMixIn.NameMixIn.class);
        mapper.addMixIn(Expression.Assignment.class, ExpressionMixIn.AssignmentMixIn.class);
        mapper.addMixIn(Expression.LShiftANDEqZero.class, ExpressionMixIn.LShiftANDEqZeroMixIn.class);
        mapper.addMixIn(Expression.OrIndent.class, ExpressionMixIn.OrIndentMixIn.class);
        mapper.addMixIn(Expression.AndIndent.class, ExpressionMixIn.AndIndentMixIn.class);
        mapper.addMixIn(Expression.Or.class, ExpressionMixIn.OrMixIn.class);
        mapper.addMixIn(Expression.And.class, ExpressionMixIn.AndMixIn.class);
        mapper.addMixIn(Expression.Less.class, ExpressionMixIn.LessMixIn.class);
        mapper.addMixIn(Expression.LessThanEqual.class, ExpressionMixIn.LessThanEqualMixIn.class);
        mapper.addMixIn(Expression.Greater.class, ExpressionMixIn.GreaterMixIn.class);
        mapper.addMixIn(Expression.GreaterThanEqual.class, ExpressionMixIn.GreaterThanEqualMixIn.class);
        mapper.addMixIn(Expression.Equal.class, ExpressionMixIn.EqualMixIn.class);
        mapper.addMixIn(Expression.NotEqual.class, ExpressionMixIn.NotEqualMixIn.class);
        mapper.addMixIn(Expression.LeftExtent.class, ExpressionMixIn.LeftExtentMixIn.class);
        mapper.addMixIn(Expression.RightExtent.class, ExpressionMixIn.RightExtentMixIn.class);
        mapper.addMixIn(Expression.Yield.class, ExpressionMixIn.YieldMixIn.class);
        mapper.addMixIn(Expression.Val.class, ExpressionMixIn.ValMixIn.class);
        mapper.addMixIn(Expression.EndOfFile.class, ExpressionMixIn.EndOfFileMixIn.class);
        mapper.addMixIn(Expression.IfThenElse.class, ExpressionMixIn.IfThenElseMixIn.class);

        // Regex
        mapper.addMixIn(iguana.regex.RegularExpression.class, RegularExpressionMixIn.class);
        mapper.addMixIn(iguana.regex.Seq.class, SeqMixIn.class);
        mapper.addMixIn(iguana.regex.Alt.class, RegexAltMixIn.class);
        mapper.addMixIn(iguana.regex.Star.class, RegexStarMixIn.class);
        mapper.addMixIn(iguana.regex.Plus.class, RegexPlusMixIn.class);
        mapper.addMixIn(iguana.regex.Opt.class, RegexOptMixIn.class);
        mapper.addMixIn(iguana.regex.Char.class, CharMixIn.class);
        mapper.addMixIn(iguana.regex.CharRange.class, CharRangeMixIn.class);
        mapper.addMixIn(iguana.regex.Reference.class, ReferenceMixIn.class);

        // Parse tree
        mapper.addMixIn(ParseTreeNode.class, ParseTreeNodeMixIn.class);
        mapper.addMixIn(TerminalNode.class, TerminalNodeMixIn.class);
        mapper.addMixIn(NonterminalNode.class, NonterminalNodeMixIn.class);
        mapper.addMixIn(MetaSymbolNode.class, MetaSymbolNodeMixIn.class);
        mapper.addMixIn(AmbiguityNode.class, AmbiguityNodeMixIn.class);

        SimpleModule module = new SimpleModule();
        module.addDeserializer(Grammar.class, new GrammarDeserializer());
        module.addDeserializer(Expression.Call.class, new CallDeserializer());
        mapper.registerModule(module);
    }

    public static String toJSON(Grammar grammar) {
        return serialize(grammar);
    }

    public static String toJSON(ParseTreeNode node) {
        return serialize(node);
    }

    public static void serialize(Object obj, String path) throws IOException {
        serialize(obj, path, false);
    }

    public static void serialize(Object obj, String path, boolean gzip) throws IOException {
        OutputStream out = new FileOutputStream(path);
        if (gzip) {
            out = new GZIPOutputStream(out);
        }
        try (Writer writer = new OutputStreamWriter(out)) {
            DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
            pp.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
            mapper.writer(pp).writeValue(writer, obj);
        }
    }

    public static String serialize(Object obj) {
        DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
        pp.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
        try {
            return mapper.writer(pp).writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T deserialize(String jsonContent, Class<T> clazz) throws IOException {
        return mapper.readValue(jsonContent, clazz);
    }

    public static <T> T deserialize(InputStream in, Class<T> clazz) throws IOException {
        return mapper.readValue(in, clazz);
    }

    public static InputStream getInputStream(InputStream in) throws IOException {
        PushbackInputStream pb = new PushbackInputStream(in, 2);
        byte[] signature = new byte[2];
        int length = pb.read(signature);
        pb.unread(signature, 0, length);

        if (signature[0] == 0x1f && signature[1] == (byte) 0x8b)
            return new GZIPInputStream(pb);

        return pb;
    }

    static class CustomTypeResolverBuilder extends ObjectMapper.DefaultTypeResolverBuilder {

        public CustomTypeResolverBuilder() {
            super(ObjectMapper.DefaultTyping.NON_FINAL);
        }

        @Override
        public boolean useForType(JavaType t) {
            return !t.isEnumType() &&
                    (t.getRawClass().getName().startsWith("org.iguana") || t.getRawClass().getName().startsWith("iguana"));
        }
    }

    public static class MyTypeIdResolver extends TypeIdResolverBase {

        @Override
        public String idFromValue(Object value) {
            return getId(value);
        }

        @Override
        public String idFromValueAndType(Object value, Class<?> suggestedType) {
            return getId(value);
        }

        @Override
        public JsonTypeInfo.Id getMechanism() {
            return JsonTypeInfo.Id.CUSTOM;
        }

        private String getId(Object value) {
            if (value.getClass() == iguana.regex.Star.class) return "regex.Star";
            if (value.getClass() == iguana.regex.Plus.class) return "regex.Plus";
            if (value.getClass() == iguana.regex.Alt.class) return "regex.Alt";
            if (value.getClass() == iguana.regex.Opt.class) return "regex.Opt";
            if (value.getClass() == iguana.regex.Seq.class) return "regex.Seq";
            if (value.getClass() == Expression.IfThenElse.class) return "IfThenElseExpr";

            String id = value.getClass().getSimpleName();
            if (id.equals("")) { // For anonymous inner classes, use their super class name
                id = value.getClass().getSuperclass().getSimpleName();
            }
            return id;
        }

        @Override
        public JavaType typeFromId(DatabindContext context, String id) {
            switch (id) {
                case "regex.Star":
                    return context.constructType(iguana.regex.Star.class);
                case "regex.Plus":
                    return context.constructType(iguana.regex.Plus.class);
                case "regex.Alt":
                    return context.constructType(iguana.regex.Alt.class);
                case "regex.Opt":
                    return context.constructType(iguana.regex.Opt.class);
                case "regex.Seq":
                    return context.constructType(iguana.regex.Seq.class);
                case "IfThenElseExpr":
                    return context.constructType(Expression.IfThenElse.class);
            }

            String[] packages = {
                    "org.iguana.grammar.",
                    "org.iguana.parsetree.",
                    "org.iguana.grammar.symbol.",
                    "org.iguana.grammar.condition.",
                    "iguana.regex.",
                    "org.iguana.datadependent.ast.Expression$"
            };

            JavaType javaType = null;
            for (String packageName : packages) {
                try {
                    Class<?> clazz = Class.forName(packageName + id);
                    javaType = context.constructType(clazz);
                    break;
                } catch (ClassNotFoundException e) {
                    // skip
                }
            }

            if (javaType == null)
                throw new RuntimeException("No JavaType for the given id (" + id + ") found.");

            return javaType;
        }
    }

    static class GrammarDeserializer extends JsonDeserializer<Grammar> {

        @Override
        public Grammar deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
            ObjectCodec codec = parser.getCodec();
            JsonNode node = codec.readTree(parser);

            Grammar.Builder builder = new Grammar.Builder();

            builder.setLayout(getLayout(node));
            builder.setStartSymbol(getStartSymbol(node));

            JsonNode rulesNode = node.get("rules");

            if (rulesNode.isArray()) {
                for (JsonNode ruleNode : rulesNode) {
                    Rule rule = mapper.readValue(ruleNode.toString(), Rule.class);
                    builder.addHighLevelRule(rule);
                }
            }

            return builder.build();
        }
    }

    static Symbol getLayout(JsonNode node) throws IOException {
        JsonNode layoutNode = node.get("layout");
        if (layoutNode == null)
            return null;

        String layoutKind = layoutNode.get("kind").asText();
        if (layoutKind.equals("Nonterminal"))
            return mapper.readValue(layoutNode.toString(), Nonterminal.class);
        else if (layoutKind.equals("Terminal"))
            return mapper.readValue(layoutNode.toString(), Terminal.class);
        else
            throw new RuntimeException("Unknown layout kind '" + layoutKind + "'");
    }

    static Start getStartSymbol(JsonNode node) throws IOException {
        JsonNode layoutNode = node.get("startSymbol");
        if (layoutNode == null)
            return null;

        return mapper.readValue(layoutNode.toString(), Start.class);
    }

    static class CallDeserializer extends JsonDeserializer<Expression.Call> {

        @Override
        public Expression.Call deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
            ObjectCodec codec = parser.getCodec();
            JsonNode node = codec.readTree(parser);

            String fun = node.get("fun").asText();
            JsonNode arguments = node.get("arguments");

            Expression[] expressions = new Expression[0];
            if (arguments != null && arguments.isArray()) {
                expressions = new Expression[arguments.size()];
                int i = 0;
                for (JsonNode child : arguments) {
                    Expression expression = mapper.readValue(child.toString(), Expression.class);
                    expressions[i++] = expression;
                }
            }

            switch (fun) {
                case "println":
                    return AST.println(expressions);

                case "indent":
                    assertSize(1, expressions.length);
                    return AST.indent(expressions[0]);

                case "ppDeclare":
                    assertSize(2, expressions.length);
                    return AST.ppDeclare(expressions[0], expressions[1]);

                case "ppLookup":
                    assertSize(2, expressions.length);
                    return AST.ppDeclare(expressions[0], expressions[1]);

                case "endsWith":
                    assertSize(1, expressions.length);
                    return AST.endsWith(expressions[0], expressions[1]);

                case "startsWith":
                    assertSize(2, expressions.length);
                    return AST.startsWith(expressions[0], expressions[1]);

                case "not":
                    assertSize(1, expressions.length);
                    return AST.not(expressions[0]);

                case "neg":
                    assertSize(1, expressions.length);
                    return AST.neg(expressions[0]);

                case "len":
                    assertSize(1, expressions.length);
                    return AST.len(expressions[0]);

                case "pr1":
                    assertSize(2, expressions.length);
                    return AST.pr1(expressions[0], expressions[1], expressions[2]);

                case "pr2":
                    assertMinSize(2, expressions.length);
                    return AST.pr2(expressions[0], expressions[1], Arrays.copyOfRange(expressions, 2, expressions.length));

                case "pr3":
                    assertSize(2, expressions.length);
                    return AST.pr3(expressions[0], expressions[1]);

                case "min":
                    assertSize(2, expressions.length);
                    return AST.min(expressions[0], expressions[1]);

                case "map":
                    assertSize(0, expressions.length);
                    return AST.map();

                case "put":
                    assertMinSize(2, expressions.length);
                    if (expressions.length == 2)
                        return AST.put(expressions[0], expressions[1]);
                    else
                        return AST.put(expressions[0], expressions[1], expressions[2]);

                case "contains":
                    assertMinSize(2, expressions.length);
                    return AST.contains(expressions[0], expressions[1]);

                case "push":
                    assertMinSize(2, expressions.length);
                    return AST.push(expressions[0], expressions[1]);

                case "pop":
                    assertMinSize(1, expressions.length);
                    return AST.pop(expressions[0]);

                case "top":
                    assertMinSize(1, expressions.length);
                    return AST.top(expressions[0]);

                case "find":
                    assertMinSize(2, expressions.length);
                    return AST.find(expressions[0], expressions[1]);

                case "get":
                    assertMinSize(2, expressions.length);
                    return AST.get(expressions[0], expressions[1]);

                case "shift":
                    assertMinSize(2, expressions.length);
                    return AST.shift(expressions[0], expressions[1]);

                case "undef":
                    assertMinSize(0, expressions.length);
                    return AST.undef();

                default:
                    throw new RuntimeException("Unsupported call type: " + fun);
            }
        }
    }

    private static void assertMinSize(int min, int actual) {
        if (min > actual)
            throw new RuntimeException("Expected min: " + min + ", actual: " + actual);
    }

    private static void assertSize(int expected, int actual) {
        if (expected != actual)
            throw new RuntimeException("Expected: " + expected + ", Actual: " + actual);
    }

    static class LayoutStrategyFilter {
        @Override
        public boolean equals(Object obj) {
            return obj == LayoutStrategy.INHERITED;
        }

        @Override
        public int hashCode() {
            return LayoutStrategy.INHERITED.hashCode();
        }
    }

    @JsonDeserialize(builder = Grammar.Builder.class)
    abstract static class GrammarMixIn {
        @JsonIgnore
        RuntimeGrammar grammar;
    }

    @JsonDeserialize(builder = Rule.Builder.class)
    abstract static class RuleMixIn { }

    abstract static class AlternativeMixIn {
        @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = AssociativityFilter.class)
        Associativity associativity;
    }

    abstract static class SequenceMixIn {
        @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = AssociativityFilter.class)
        Associativity associativity;
    }

    private static class AssociativityFilter {
        @Override
        public boolean equals(Object obj) {
            return obj == null || obj == Associativity.UNDEFINED;
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
    @JsonSubTypes({
        @JsonSubTypes.Type(value=Nonterminal.class, name="Nonterminal"),
        @JsonSubTypes.Type(value=Terminal.class, name="Terminal"),
        @JsonSubTypes.Type(value=Star.class, name="Star"),
        @JsonSubTypes.Type(value=Plus.class, name="Plus"),
        @JsonSubTypes.Type(value=Opt.class, name="Opt"),
        @JsonSubTypes.Type(value=Alt.class, name="Alt"),
        @JsonSubTypes.Type(value= Group.class, name="Sequence"),
        @JsonSubTypes.Type(value=Start.class, name="Start"),
    })
    abstract static class SymbolMixIn { }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM,  property = "kind")
    @JsonTypeIdResolver(MyTypeIdResolver.class)
    abstract static class RegularExpressionMixIn { }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
    abstract static class ParseTreeNodeMixIn { }

    @JsonDeserialize(builder = Nonterminal.Builder.class)
    abstract static class NonterminalMixIn {

        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        int index;

        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        boolean ebnfList;

        @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = NonterminalNodeTypeFilter.class)
        NonterminalNodeType nodeType;

        @JsonIgnore
        Map<String, Object> attributes;

        private static class NonterminalNodeTypeFilter {
            @Override
            public boolean equals(Object obj) {
                return obj == NonterminalNodeType.Basic;
            }
        }
    }

    @JsonDeserialize(builder = Star.Builder.class)
    abstract static class StarMixIn { }

    @JsonDeserialize(builder = Start.Builder.class)
    abstract static class StartMixIn { }

    @JsonDeserialize(builder = Plus.Builder.class)
    abstract static class PlusMixIn { }

    @JsonDeserialize(builder = Opt.Builder.class)
    abstract static class OptMixIn { }

    @JsonDeserialize(builder = Alt.Builder.class)
    abstract static class AltMixIn { }

    @JsonDeserialize(builder = Group.Builder.class)
    abstract static class GroupMixIn { }

    @JsonDeserialize(builder = Terminal.Builder.class)
    abstract static class TerminalMixIn { }

    @JsonDeserialize(builder = Return.Builder.class)
    abstract static class ReturnMixIn { }

    @JsonDeserialize(builder = iguana.regex.Seq.Builder.class)
    abstract static class SeqMixIn { }

    @JsonDeserialize(builder = iguana.regex.Alt.Builder.class)
    abstract static class RegexAltMixIn { }

    @JsonDeserialize(builder = iguana.regex.Star.Builder.class)
    abstract static class RegexStarMixIn { }

    @JsonDeserialize(builder = iguana.regex.Plus.Builder.class)
    abstract static class RegexPlusMixIn { }

    @JsonDeserialize(builder = iguana.regex.Opt.Builder.class)
    abstract static class RegexOptMixIn { }

    @JsonDeserialize(builder = iguana.regex.Char.Builder.class)
    abstract static class CharMixIn { }

    @JsonDeserialize(builder = iguana.regex.CharRange.Builder.class)
    abstract static class CharRangeMixIn { }

    @JsonDeserialize(builder = iguana.regex.Reference.Builder.class)
    abstract static class ReferenceMixIn { }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
    @JsonSubTypes({
        @JsonSubTypes.Type(value=RegularExpressionCondition.class, name="RegularExpressionCondition"),
        @JsonSubTypes.Type(value=DataDependentCondition.class, name="DataDependentCondition")
    })
    abstract static class ConditionMixIn { }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
    abstract static class ExpressionMixIn {

        abstract static class IntegerMixIn {
            @JsonCreator
            IntegerMixIn(@JsonProperty("value") Integer value) { }
        }

        public class RealMixIn {
            @JsonCreator
            RealMixIn(@JsonProperty("value") Float value) { }
        }

        public class StringMixIn {
            @JsonCreator
            StringMixIn(@JsonProperty("value") String value) { }
        }

        abstract static class NameMixIn {
            @JsonCreator
            NameMixIn(@JsonProperty("name") String name) { }
        }

        public class AssignmentMixIn {
            @JsonCreator
            AssignmentMixIn(@JsonProperty("id") String id, @JsonProperty("exp") Expression exp) { }
        }

        abstract static class TupleMixIn {
            @JsonCreator
            TupleMixIn(@JsonProperty("elements") Expression... elements) { }
        }

        abstract static class GreaterThanEqualMixIn {
            @JsonCreator
            GreaterThanEqualMixIn(@JsonProperty("lhs") Expression lhs, @JsonProperty("rhs") Expression rhs) { }
        }

        abstract static class LShiftANDEqZeroMixIn {
            @JsonCreator
            LShiftANDEqZeroMixIn(@JsonProperty("lhs") Expression lhs, @JsonProperty("rhs") Expression rhs) { }
        }

        abstract static class GreaterMixIn {
            @JsonCreator
            GreaterMixIn(@JsonProperty("lhs") Expression lhs, @JsonProperty("rhs") Expression rhs) { }
        }

        abstract static class OrMixIn {
            @JsonCreator
            OrMixIn(@JsonProperty("lhs") Expression lhs, @JsonProperty("rhs") Expression rhs) { }
        }

        abstract static class LessThanEqualMixIn {
            @JsonCreator
            LessThanEqualMixIn(@JsonProperty("lhs") Expression lhs, @JsonProperty("rhs") Expression rhs) { }
        }

        abstract static class LessMixIn {
            @JsonCreator
            LessMixIn(@JsonProperty("lhs") Expression lhs, @JsonProperty("rhs") Expression rhs) { }
        }

        abstract static class IfThenElseMixIn {
            @JsonCreator
            IfThenElseMixIn(@JsonProperty("condition") Expression condition,
                            @JsonProperty("thenPart") Expression thenPart,
                            @JsonProperty("elsePart") Expression elsePart) { }
        }

        abstract static class EqualMixIn {
            @JsonCreator
            EqualMixIn(@JsonProperty("lhs") Expression lhs, @JsonProperty("rhs") Expression rhs) { }
        }

        abstract static class NotEqualMixIn {
            @JsonCreator
            NotEqualMixIn(@JsonProperty("lhs") Expression lhs, @JsonProperty("rhs") Expression rhs) { }
        }

        abstract static class OrIndentMixIn {
            @JsonCreator
            OrIndentMixIn(@JsonProperty("index") Expression index,
                          @JsonProperty("ind") Expression ind,
                          @JsonProperty("first") Expression first,
                          @JsonProperty("lExt") Expression lExt) { }
        }

        abstract static class AndIndentMixIn {
            @JsonCreator
            AndIndentMixIn(@JsonProperty("index") Expression index,
                           @JsonProperty("first") Expression first,
                           @JsonProperty("lExt") Expression lExt) { }
        }

        abstract static class AndMixIn {
            @JsonCreator
            AndMixIn(@JsonProperty("lhs") Expression lhs,
                     @JsonProperty("rhs") Expression rhs) { }
        }

        abstract static class LeftExtentMixIn {
            @JsonCreator
            LeftExtentMixIn(@JsonProperty("label") String label) { }
        }

        abstract static class RightExtentMixIn {
            @JsonCreator
            RightExtentMixIn(@JsonProperty("label") String label) { }
        }

        abstract static class YieldMixIn {
            @JsonCreator
            YieldMixIn(@JsonProperty("label") String label) { }
        }

        abstract static class ValMixIn {
            @JsonCreator
            ValMixIn(@JsonProperty("label") String label) { }
        }

        abstract static class EndOfFileMixIn {
            @JsonCreator
            EndOfFileMixIn(@JsonProperty("index") Expression index) { }
        }
    }

    abstract static class RegularExpressionConditionMixIn {
        @JsonCreator
        RegularExpressionConditionMixIn(
                @JsonProperty("type") ConditionType type,
                @JsonProperty("regularExpression") RegularExpression regularExpression) { }
    }

    abstract static class DataDependentConditionMixIn {
        @JsonCreator
        DataDependentConditionMixIn(@JsonProperty("type") ConditionType type,
                                    @JsonProperty("expression") Expression expression) { }
    }

    @JsonDeserialize(builder = TerminalNodeBuilder.class)
    abstract static class TerminalNodeMixIn {
    }

    abstract static class NonterminalNodeMixIn {
        NonterminalNodeMixIn(
                @JsonProperty("rule") RuntimeRule rule,
                @JsonProperty("children") List<ParseTreeNode> children,
                @JsonProperty("start") int start,
                @JsonProperty("end") int end) { }
    }

    abstract static class MetaSymbolNodeMixIn {
        MetaSymbolNodeMixIn(
                @JsonProperty("symbol") Symbol symbol,
                @JsonProperty("symbols") List<ParseTreeNode> symbols,
                @JsonProperty("start") int start,
                @JsonProperty("end") int end) { }
    }

    abstract static class AmbiguityNodeMixIn {
        AmbiguityNodeMixIn(@JsonProperty("alternatives") Set<ParseTreeNode> alternatives) {
        }
    }

    abstract static class AbstractAttrsMixIn {
        @JsonIgnore
        ImmutableSet<String> env;
    }

    static class TerminalNodeBuilder {
        private int start;
        private int end;
        private String text;
        private Terminal terminal;

        public TerminalNodeBuilder setTerminal(Terminal terminal) {
            this.terminal = terminal;
            return this;
        }

        public TerminalNodeBuilder setStart(int start) {
            this.start = start;
            return this;
        }

        public TerminalNodeBuilder setEnd(int end) {
            this.end = end;
            return this;
        }

        public TerminalNodeBuilder setStart(String text) {
            this.text = text;
            return this;
        }

        public TerminalNode build() {
            return new TerminalNode(terminal, start, end) {

                @Override
                public String getText() {
                    return text;
                }
            };
        }
    }

}


