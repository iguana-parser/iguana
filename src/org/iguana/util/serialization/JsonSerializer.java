package org.iguana.util.serialization;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.iguana.datadependent.ast.AST;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.ast.Statement;
import org.iguana.datadependent.ast.VariableDeclaration;
import org.iguana.datadependent.attrs.AbstractAttrs;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.condition.*;
import org.iguana.grammar.runtime.AssociativityGroup;
import org.iguana.grammar.runtime.PrecedenceLevel;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.grammar.slot.NonterminalNodeType;
import org.iguana.grammar.symbol.Alt;
import org.iguana.grammar.symbol.Opt;
import org.iguana.grammar.symbol.Plus;
import org.iguana.grammar.symbol.Star;
import org.iguana.grammar.symbol.*;
import org.iguana.parser.ParseError;
import org.iguana.parsetree.*;
import org.iguana.regex.*;
import org.iguana.regex.automaton.Automaton;
import org.iguana.utils.input.Input;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonSerializer {

    private final static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
            .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
            .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE));

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        mapper.addMixIn(RuntimeGrammar.class, RuntimeGrammarMixIn.class);
        mapper.addMixIn(RuntimeRule.class, RuntimeRuleMixIn.class);

        mapper.addMixIn(Grammar.class, GrammarMixIn.class);
        mapper.addMixIn(Rule.class, RuleMixIn.class);
        mapper.addMixIn(PriorityLevel.class, PriorityLevelMixIn.class);
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
        mapper.addMixIn(Identifier.class, IdentifierMixIn.class);
        mapper.addMixIn(Start.class, StartMixIn.class);
        mapper.addMixIn(Code.class, CodeMixIn.class);
        mapper.addMixIn(CodeHolder.class, CodeHolderMixIn.class);
        mapper.addMixIn(Return.class, ReturnMixIn.class);
        mapper.addMixIn(IfThenElse.class, IfThenElseMixIn.class);
        mapper.addMixIn(Offside.class, OffsideMixIn.class);
        mapper.addMixIn(Align.class, AlignMixIn.class);
        mapper.addMixIn(Ignore.class, IgnoreMixIn.class);

        mapper.addMixIn(AbstractAttrs.class, AbstractAttrsMixIn.class);

        // Conditions
        mapper.addMixIn(Condition.class, ConditionMixIn.class);
        mapper.addMixIn(RegularExpressionCondition.class, RegularExpressionConditionMixIn.class);
        mapper.addMixIn(DataDependentCondition.class, DataDependentConditionMixIn.class);
        mapper.addMixIn(PositionalCondition.class, PositionalConditionMixIn.class);

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
        mapper.addMixIn(Expression.Not.class, ExpressionMixIn.NotMixIn.class);
        mapper.addMixIn(Expression.Or.class, ExpressionMixIn.OrMixIn.class);
        mapper.addMixIn(Expression.And.class, ExpressionMixIn.AndMixIn.class);
        mapper.addMixIn(Expression.Add.class, ExpressionMixIn.AddMixIn.class);
        mapper.addMixIn(Expression.Subtract.class, ExpressionMixIn.SubtractMixIn.class);
        mapper.addMixIn(Expression.Multiply.class, ExpressionMixIn.MultiplyMixIn.class);
        mapper.addMixIn(Expression.Divide.class, ExpressionMixIn.DivideMixIn.class);
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
        mapper.addMixIn(Expression.Call.class, ExpressionMixIn.CallMixIn.class);

        mapper.addMixIn(VariableDeclaration.class, VariableDeclarationMixIn.class);

        // Call Expressions
        mapper.addMixIn(AST.Println.class, ExpressionMixIn.CallMixIn.PrintlnMixIn.class);
        mapper.addMixIn(AST.Assert.class, ExpressionMixIn.CallMixIn.AssertMixIn.class);
        mapper.addMixIn(AST.Set.class, ExpressionMixIn.CallMixIn.SetMixIn.class);
        mapper.addMixIn(AST.StartsWith.class, ExpressionMixIn.CallMixIn.StartsWithMixIn.class);
        mapper.addMixIn(AST.Push.class, ExpressionMixIn.CallMixIn.PushMixIn.class);
        mapper.addMixIn(AST.Shift.class, ExpressionMixIn.CallMixIn.ShiftMixIn.class);
        mapper.addMixIn(AST.Println.class, ExpressionMixIn.CallMixIn.PrintlnMixIn.class);
        mapper.addMixIn(AST.Indent.class, ExpressionMixIn.CallMixIn.IndentMixIn.class);
        mapper.addMixIn(AST.Min.class, ExpressionMixIn.CallMixIn.MinMixIn.class);
        mapper.addMixIn(AST.Neg.class, ExpressionMixIn.CallMixIn.NegMixIn.class);
        mapper.addMixIn(AST.Pop.class, ExpressionMixIn.CallMixIn.PopMixIn.class);
        mapper.addMixIn(AST.Get.class, ExpressionMixIn.CallMixIn.GetMixIn.class);
        mapper.addMixIn(AST.Len.class, ExpressionMixIn.CallMixIn.LenMixIn.class);
        mapper.addMixIn(AST.Top.class, ExpressionMixIn.CallMixIn.TopMixIn.class);
        mapper.addMixIn(AST.Get2.class, ExpressionMixIn.CallMixIn.Get2MixIn.class);
        mapper.addMixIn(AST.Put.class, ExpressionMixIn.CallMixIn.PutMixIn.class);
        mapper.addMixIn(AST.Contains.class, ExpressionMixIn.CallMixIn.ContainsMixIn.class);
        mapper.addMixIn(AST.PPDeclare.class, ExpressionMixIn.CallMixIn.PPDeclareMixIn.class);
        mapper.addMixIn(AST.Put3.class, ExpressionMixIn.CallMixIn.Put3MixIn.class);
        mapper.addMixIn(AST.EndsWith.class, ExpressionMixIn.CallMixIn.EndsWithMixIn.class);
        mapper.addMixIn(AST.Map.class, ExpressionMixIn.CallMixIn.MapMixIn.class);
        mapper.addMixIn(AST.Pr2.class, ExpressionMixIn.CallMixIn.Pr2MixIn.class);
        mapper.addMixIn(AST.Pr3.class, ExpressionMixIn.CallMixIn.Pr3MixIn.class);
        mapper.addMixIn(AST.PPLookup.class, ExpressionMixIn.CallMixIn.PPLookupMixIn.class);
        mapper.addMixIn(AST.Undef.class, ExpressionMixIn.CallMixIn.UndefMixIn.class);
        mapper.addMixIn(AST.Find.class, ExpressionMixIn.CallMixIn.FindMixIn.class);
        mapper.addMixIn(AST.Pr1.class, ExpressionMixIn.CallMixIn.Pr1MixIn.class);


        // Statement
        mapper.addMixIn(Statement.class, StatementMixIn.class);
        mapper.addMixIn(Statement.Expression.class, StatementMixIn.ExpressionMixIn.class);
        mapper.addMixIn(Statement.VariableDeclaration.class, StatementMixIn.VariableDeclarationMixIn.class);

        // Regex
        mapper.addMixIn(RegularExpression.class, RegularExpressionMixIn.class);
        mapper.addMixIn(Seq.class, SeqMixIn.class);
        mapper.addMixIn(org.iguana.regex.Alt.class, RegexAltMixIn.class);
        mapper.addMixIn(org.iguana.regex.Star.class, RegexStarMixIn.class);
        mapper.addMixIn(org.iguana.regex.Plus.class, RegexPlusMixIn.class);
        mapper.addMixIn(org.iguana.regex.Opt.class, RegexOptMixIn.class);
        mapper.addMixIn(Char.class, CharMixIn.class);
        mapper.addMixIn(CharRange.class, CharRangeMixIn.class);
        mapper.addMixIn(Reference.class, ReferenceMixIn.class);
        mapper.addMixIn(Epsilon.class, EpsilonMixIn.class);

        // Parse tree
        mapper.addMixIn(ParseTreeNode.class, ParseTreeNodeMixIn.class);
        mapper.addMixIn(NonterminalNode.class, NonterminalNodeMixIn.class);
        mapper.addMixIn(AmbiguityNode.class, AmbiguityNodeMixIn.class);
        mapper.addMixIn(MetaSymbolNode.StarNode.class, StarNodeMixIn.class);
        mapper.addMixIn(MetaSymbolNode.PlusNode.class, PlusNodeMixIn.class);
        mapper.addMixIn(MetaSymbolNode.GroupNode.class, GroupNodeMixIn.class);
        mapper.addMixIn(MetaSymbolNode.AltNode.class, AltNodeMixIn.class);
        mapper.addMixIn(MetaSymbolNode.OptionNode.class, OptionNodeMixIn.class);
        mapper.addMixIn(MetaSymbolNode.StartNode.class, StartNodeMixIn.class);
        mapper.addMixIn(DefaultTerminalNode.class, DefaultTerminalNodeMixIn.class);
        mapper.addMixIn(KeywordTerminalNode.class, KeywordTerminalNodeMixIn.class);

        mapper.addMixIn(ParseError.class, ParseErrorMixIn.class);

        mapper.addMixIn(PrecedenceLevel.class, PrecedenceLevelMixIn.class);
        mapper.addMixIn(AssociativityGroup.class, AssociativityGroupMixIn.class);

        // Since @JsonIgnore doesn't work on a map property with non-string keys and Jackson
        // tries to deserialize the definitions field. This is a solution to let Jackson know
        // about the Nonterminal key type.
        class NonterminalKeyDeserializer extends KeyDeserializer {
            @Override
            public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
                return mapper.readValue(key, Nonterminal.class);
            }
        }

        SimpleModule module = new SimpleModule();
        module.addKeyDeserializer(Nonterminal.class, new NonterminalKeyDeserializer());
        mapper.registerModule(module);
    }

    public static String toJSON(Grammar grammar) {
        return serialize(grammar);
    }

    public static String toJSON(ParseTreeNode node) {
        return serialize(node);
    }

    public static void serialize(Object obj, String path) throws IOException {
        OutputStream out = new FileOutputStream(path);
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

    @JsonDeserialize(builder = RuntimeGrammar.Builder.class)
    abstract static class RuntimeGrammarMixIn {
        @JsonIgnore
        Map<Nonterminal, List<RuntimeRule>> definitions;
        @JsonIgnore
        Map<String, RegularExpression> terminals;
        @JsonIgnore
        Set<RegularExpression> literals;
    }

    @JsonDeserialize(builder = RuntimeRule.Builder.class)
    abstract static class RuntimeRuleMixIn {
        @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LayoutStrategyFilter.class)
        LayoutStrategy layoutStrategy;
        @JsonIgnore
        Map<String, Object> attributes;
    }

    @JsonDeserialize(builder = Grammar.Builder.class)
    abstract static class GrammarMixIn {
        @JsonIgnore
        RuntimeGrammar grammar;
    }

    @JsonDeserialize(builder = Rule.Builder.class)
    abstract static class RuleMixIn {
        @JsonIgnore
        Map<String, Object> attributes;
    }

    abstract static class AnnotationMixIn {
        @JsonCreator
        AnnotationMixIn(
            @JsonProperty("name") String name,
            @JsonProperty("value") String value,
            @JsonProperty("symbolName") String symbolName) { }
    }

    @JsonDeserialize(builder = PriorityLevel.Builder.class)
    abstract static class PriorityLevelMixIn { }

    @JsonDeserialize(builder = Alternative.Builder.class)
    abstract static class AlternativeMixIn { }

    @JsonDeserialize(builder = Sequence.Builder.class)
    abstract static class SequenceMixIn {
        @JsonIgnore
        Map<String, Object> attributes;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
    @JsonSubTypes({
        @JsonSubTypes.Type(value=Nonterminal.class, name="Nonterminal"),
        @JsonSubTypes.Type(value=Terminal.class, name="Terminal"),
        @JsonSubTypes.Type(value=Star.class, name="Star"),
        @JsonSubTypes.Type(value=Plus.class, name="Plus"),
        @JsonSubTypes.Type(value=Opt.class, name="Opt"),
        @JsonSubTypes.Type(value=Alt.class, name="Alt"),
        @JsonSubTypes.Type(value=Group.class, name="Sequence"),
        @JsonSubTypes.Type(value=Start.class, name="Start"),
        @JsonSubTypes.Type(value=Identifier.class, name="Identifier"),
        @JsonSubTypes.Type(value=CodeHolder.class, name="CodeHolder"),
        @JsonSubTypes.Type(value=Code.class, name="Code"),
        @JsonSubTypes.Type(value=Return.class, name="Return"),
        @JsonSubTypes.Type(value=IfThenElse.class, name="IfThenElse"),
        @JsonSubTypes.Type(value=Offside.class, name="Offside"),
        @JsonSubTypes.Type(value=Align.class, name="Align"),
        @JsonSubTypes.Type(value=Ignore.class, name="Ignore")
    })
    abstract static class SymbolMixIn { }

    abstract static class AbstractSymbolMixIn extends SymbolMixIn {
        @JsonIgnore
        Map<String, Object> attributes;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = org.iguana.regex.Seq.class, name="regex.Seq"),
        @JsonSubTypes.Type(value = org.iguana.regex.Alt.class, name="regex.Alt"),
        @JsonSubTypes.Type(value = org.iguana.regex.Star.class, name="regex.Star"),
        @JsonSubTypes.Type(value = org.iguana.regex.Plus.class, name="regex.Plus"),
        @JsonSubTypes.Type(value = org.iguana.regex.Opt.class, name="regex.Opt"),
        @JsonSubTypes.Type(value = org.iguana.regex.Char.class, name="Char"),
        @JsonSubTypes.Type(value = org.iguana.regex.CharRange.class, name="CharRange"),
        @JsonSubTypes.Type(value = org.iguana.regex.Reference.class, name="Reference"),
        @JsonSubTypes.Type(value = org.iguana.regex.Epsilon.class, name="Epsilon")
    })
    abstract static class RegularExpressionMixIn { }

    abstract static class AbstractRegularExpressionMixIn {
        @JsonIgnore Automaton automaton;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
    @JsonSubTypes({
        @JsonSubTypes.Type(value=DefaultTerminalNode.class, name="TerminalNode"),
        @JsonSubTypes.Type(value=KeywordTerminalNode.class, name="KeywordTerminalNode"),
        @JsonSubTypes.Type(value=NonterminalNode.class, name="NonterminalNode"),
        @JsonSubTypes.Type(value=AmbiguityNode.class, name="AmbiguityNode"),
        @JsonSubTypes.Type(value=MetaSymbolNode.StarNode.class, name="StarNode"),
        @JsonSubTypes.Type(value=MetaSymbolNode.PlusNode.class, name="PlusNode"),
        @JsonSubTypes.Type(value=MetaSymbolNode.GroupNode.class, name="GroupNode"),
        @JsonSubTypes.Type(value=MetaSymbolNode.OptionNode.class, name="OptionNode"),
        @JsonSubTypes.Type(value=MetaSymbolNode.AltNode.class, name="AltNode"),
        @JsonSubTypes.Type(value=MetaSymbolNode.StartNode.class, name="StartNode")
    })
    abstract static class ParseTreeNodeMixIn { }

    abstract static class DefaultTerminalNodeMixIn {
        DefaultTerminalNodeMixIn(
            @JsonProperty("terminal") Terminal terminal,
            @JsonProperty("start") int start,
            @JsonProperty("end") int end,
            @JsonProperty("ignore") Input input
        ) { }
        @JsonIgnore
        Input input;
    }

    abstract static class KeywordTerminalNodeMixIn {
        KeywordTerminalNodeMixIn(
            @JsonProperty("terminal") Terminal terminal,
            @JsonProperty("start") int start,
            @JsonProperty("end") int end
        ) { }
    }

    abstract static class NonterminalNodeMixIn {
        NonterminalNodeMixIn(
            @JsonProperty("rule") RuntimeRule rule,
            @JsonProperty("children") List<ParseTreeNode> children,
            @JsonProperty("start") int start,
            @JsonProperty("end") int end) { }
    }

    abstract static class StarNodeMixIn {
        StarNodeMixIn(
            @JsonProperty("symbol") Star symbol,
            @JsonProperty("children") List<ParseTreeNode> children,
            @JsonProperty("start") int start,
            @JsonProperty("end") int end) { }
    }

    abstract static class PlusNodeMixIn {
        PlusNodeMixIn(
            @JsonProperty("symbol") Plus symbol,
            @JsonProperty("children") List<ParseTreeNode> children,
            @JsonProperty("start") int start,
            @JsonProperty("end") int end) { }
    }

    abstract static class GroupNodeMixIn {
        GroupNodeMixIn(
            @JsonProperty("symbol") Group symbol,
            @JsonProperty("children") List<ParseTreeNode> children,
            @JsonProperty("start") int start,
            @JsonProperty("end") int end) { }
    }

    abstract static class OptionNodeMixIn {
        OptionNodeMixIn(
            @JsonProperty("symbol") Opt symbol,
            @JsonProperty("child") ParseTreeNode child,
            @JsonProperty("start") int start,
            @JsonProperty("end") int end) { }
    }

    abstract static class AltNodeMixIn {
        AltNodeMixIn(
            @JsonProperty("symbol") Alt symbol,
            @JsonProperty("child") ParseTreeNode child,
            @JsonProperty("start") int start,
            @JsonProperty("end") int end) { }
    }

    abstract static class StartNodeMixIn {
        StartNodeMixIn(
            @JsonProperty("symbol") Start symbol,
            @JsonProperty("children") List<ParseTreeNode> children,
            @JsonProperty("start") int start,
            @JsonProperty("end") int end) { }
    }

    abstract static class AmbiguityNodeMixIn {
        AmbiguityNodeMixIn(@JsonProperty("alternatives") Set<ParseTreeNode> alternatives) { }
    }

    @JsonDeserialize(builder = Nonterminal.Builder.class)
    abstract static class NonterminalMixIn extends AbstractSymbolMixIn {

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
    abstract static class StarMixIn extends AbstractSymbolMixIn { }

    @JsonDeserialize(builder = Start.Builder.class)
    abstract static class StartMixIn extends AbstractSymbolMixIn { }

    @JsonDeserialize(builder = Code.Builder.class)
    abstract static class CodeMixIn extends AbstractSymbolMixIn { }

    @JsonDeserialize(builder = CodeHolder.Builder.class)
    abstract static class CodeHolderMixIn extends AbstractSymbolMixIn { }

    @JsonDeserialize(builder = Plus.Builder.class)
    abstract static class PlusMixIn extends AbstractSymbolMixIn { }

    @JsonDeserialize(builder = Opt.Builder.class)
    abstract static class OptMixIn extends AbstractSymbolMixIn { }

    @JsonDeserialize(builder = Alt.Builder.class)
    abstract static class AltMixIn extends AbstractSymbolMixIn { }

    @JsonDeserialize(builder = Group.Builder.class)
    abstract static class GroupMixIn extends AbstractSymbolMixIn { }

    @JsonDeserialize(builder = Identifier.Builder.class)
    abstract static class IdentifierMixIn extends AbstractSymbolMixIn { }

    @JsonDeserialize(builder = Terminal.Builder.class)
    abstract static class TerminalMixIn extends AbstractSymbolMixIn { }

    @JsonDeserialize(builder = Return.Builder.class)
    abstract static class ReturnMixIn extends AbstractSymbolMixIn { }

    @JsonDeserialize(builder = IfThenElse.Builder.class)
    abstract static class IfThenElseMixIn extends AbstractSymbolMixIn { }

    @JsonDeserialize(builder = Offside.Builder.class)
    abstract static class OffsideMixIn extends AbstractSymbolMixIn { }

    @JsonDeserialize(builder = Align.Builder.class)
    abstract static class AlignMixIn extends AbstractSymbolMixIn { }

    @JsonDeserialize(builder = Ignore.Builder.class)
    abstract static class IgnoreMixIn extends AbstractSymbolMixIn { }

    @JsonDeserialize(builder = org.iguana.regex.Seq.Builder.class)
    abstract static class SeqMixIn extends AbstractRegularExpressionMixIn { }

    @JsonDeserialize(builder = org.iguana.regex.Alt.Builder.class)
    abstract static class RegexAltMixIn extends AbstractRegularExpressionMixIn { }

    @JsonDeserialize(builder = org.iguana.regex.Star.Builder.class)
    abstract static class RegexStarMixIn extends AbstractRegularExpressionMixIn { }

    @JsonDeserialize(builder = org.iguana.regex.Plus.Builder.class)
    abstract static class RegexPlusMixIn extends AbstractRegularExpressionMixIn { }

    @JsonDeserialize(builder = org.iguana.regex.Opt.Builder.class)
    abstract static class RegexOptMixIn extends AbstractRegularExpressionMixIn { }

    @JsonDeserialize(builder = org.iguana.regex.Char.Builder.class)
    abstract static class CharMixIn extends AbstractRegularExpressionMixIn { }

    @JsonDeserialize(builder = org.iguana.regex.CharRange.Builder.class)
    abstract static class CharRangeMixIn extends AbstractRegularExpressionMixIn { }

    @JsonDeserialize(builder = org.iguana.regex.Reference.Builder.class)
    abstract static class ReferenceMixIn extends AbstractRegularExpressionMixIn { }

    abstract static class EpsilonMixIn extends AbstractRegularExpressionMixIn {
        @JsonCreator
        public abstract Epsilon getInstance();
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
    @JsonSubTypes({
        @JsonSubTypes.Type(value=RegularExpressionCondition.class, name="RegularExpressionCondition"),
        @JsonSubTypes.Type(value=DataDependentCondition.class, name="DataDependentCondition"),
        @JsonSubTypes.Type(value=PositionalCondition.class, name="PositionalCondition")
    })
    abstract static class ConditionMixIn { }

    abstract static class VariableDeclarationMixIn {
        @JsonCreator
        VariableDeclarationMixIn(@JsonProperty("name") String name,
                                 @JsonProperty("i") int i,
                                 @JsonProperty("expression") Expression expression) { }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
    @JsonSubTypes({
        @JsonSubTypes.Type(value= Expression.Integer.class, name="Integer"),
        @JsonSubTypes.Type(value= Expression.Real.class, name="Real"),
        @JsonSubTypes.Type(value= Expression.String.class, name="String"),
        @JsonSubTypes.Type(value= Expression.Name.class, name="Name"),
        @JsonSubTypes.Type(value= Expression.Assignment.class, name="Assignment"),
        @JsonSubTypes.Type(value= Expression.Tuple.class, name="Tuple"),
        @JsonSubTypes.Type(value= Expression.GreaterThanEqual.class, name="GreaterThanEqual"),
        @JsonSubTypes.Type(value= Expression.LShiftANDEqZero.class, name="LShiftANDEqZero"),
        @JsonSubTypes.Type(value= Expression.Greater.class, name="Greater"),
        @JsonSubTypes.Type(value= Expression.Not.class, name="Not"),
        @JsonSubTypes.Type(value= Expression.Or.class, name="Or"),
        @JsonSubTypes.Type(value= Expression.LessThanEqual.class, name="LessThanEqual"),
        @JsonSubTypes.Type(value= Expression.IfThenElse.class, name="IfThenElse"),
        @JsonSubTypes.Type(value= Expression.Equal.class, name="Equal"),
        @JsonSubTypes.Type(value= Expression.NotEqual.class, name="NotEqual"),
        @JsonSubTypes.Type(value= Expression.OrIndent.class, name="OrIndent"),
        @JsonSubTypes.Type(value= Expression.AndIndent.class, name="AndIndent"),
        @JsonSubTypes.Type(value= Expression.And.class, name="And"),
        @JsonSubTypes.Type(value= Expression.Add.class, name="Add"),
        @JsonSubTypes.Type(value= Expression.LeftExtent.class, name="LeftExtent"),
        @JsonSubTypes.Type(value= Expression.RightExtent.class, name="RightExtent"),
        @JsonSubTypes.Type(value= Expression.Yield.class, name="Yield"),
        @JsonSubTypes.Type(value= Expression.Val.class, name="Val"),
        @JsonSubTypes.Type(value= Expression.EndOfFile.class, name="EndOfFile"),
        @JsonSubTypes.Type(value= Expression.Call.class, name="Call")
    })
    abstract static class ExpressionMixIn {

        abstract static class IntegerMixIn {
            @JsonCreator
            IntegerMixIn(@JsonProperty("value") Integer value) { }
        }

        abstract static class RealMixIn {
            @JsonCreator
            RealMixIn(@JsonProperty("value") Float value) { }
        }

        abstract static class StringMixIn {
            @JsonCreator
            StringMixIn(@JsonProperty("value") String value) { }
        }

        abstract static class NameMixIn {
            @JsonCreator
            NameMixIn(@JsonProperty("name") String name, @JsonProperty("i") int i) { }
        }

        abstract static class AssignmentMixIn {
            @JsonCreator
            AssignmentMixIn(@JsonProperty("id") String id, @JsonProperty("exp") Expression exp) { }
        }

        abstract static class TupleMixIn {
            @JsonCreator
            TupleMixIn(@JsonProperty("elements") Expression... elements) { }
        }

        abstract static class IfThenElseMixIn {
            @JsonCreator
            IfThenElseMixIn(@JsonProperty("condition") Expression condition,
                            @JsonProperty("thenPart") Expression thenPart,
                            @JsonProperty("elsePart") Expression elsePart) { }
        }

        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
        @JsonSubTypes({
            @JsonSubTypes.Type(value = AST.StartsWith.class, name = "StartsWith"),
            @JsonSubTypes.Type(value = AST.Push.class, name = "Push"),
            @JsonSubTypes.Type(value = AST.Pop.class, name = "Pop"),
            @JsonSubTypes.Type(value = AST.Top.class, name = "Top"),
            @JsonSubTypes.Type(value = AST.Find.class, name = "Find"),
            @JsonSubTypes.Type(value = AST.Shift.class, name = "Shift"),
            @JsonSubTypes.Type(value = AST.Println.class, name = "Println"),
            @JsonSubTypes.Type(value = AST.Assert.class, name = "Assert"),
            @JsonSubTypes.Type(value = AST.Set.class, name = "Set"),
            @JsonSubTypes.Type(value = AST.Indent.class, name = "Indent"),
            @JsonSubTypes.Type(value = AST.Min.class, name = "Min"),
            @JsonSubTypes.Type(value = AST.Neg.class, name = "Neg"),
            @JsonSubTypes.Type(value = AST.Get.class, name = "Get"),
            @JsonSubTypes.Type(value = AST.Len.class, name = "Len"),
            @JsonSubTypes.Type(value = AST.Get2.class, name = "Get2"),
            @JsonSubTypes.Type(value = AST.Put.class, name = "Put"),
            @JsonSubTypes.Type(value = AST.Contains.class, name = "Contains"),
            @JsonSubTypes.Type(value = AST.PPDeclare.class, name = "PPDeclare"),
            @JsonSubTypes.Type(value = AST.Put3.class, name = "Put3"),
            @JsonSubTypes.Type(value = AST.EndsWith.class, name = "EndsWith"),
            @JsonSubTypes.Type(value = AST.Map.class, name = "Map"),
            @JsonSubTypes.Type(value = AST.Pr2.class, name = "Pr2"),
            @JsonSubTypes.Type(value = AST.Pr3.class, name = "Pr3"),
            @JsonSubTypes.Type(value = AST.PPLookup.class, name = "PPLookup"),
            @JsonSubTypes.Type(value = AST.Undef.class, name = "Undef"),
            @JsonSubTypes.Type(value = AST.Pr1.class, name = "Pr1")
        })
        abstract static class CallMixIn {

            abstract static class PrintlnMixIn {
                @JsonCreator
                PrintlnMixIn(@JsonProperty("arguments") Expression[] arguments) { }
            }

            abstract static class AssertMixIn {
                @JsonCreator
                AssertMixIn(@JsonProperty("arguments") Expression[] arguments) { }
            }

            abstract static class SetMixIn {
                @JsonCreator
                SetMixIn(@JsonProperty("arguments") Expression[] arguments) { }
            }

            abstract static class StartsWithMixIn {
                @JsonCreator
                StartsWithMixIn(@JsonProperty("arguments") Expression[] arguments) { }
            }

            abstract static class PushMixIn {
                @JsonCreator
                PushMixIn(@JsonProperty("arguments") Expression[] arguments) { }
            }

            abstract static class PopMixIn {
                @JsonCreator
                PopMixIn(@JsonProperty("arguments") Expression[] arguments) { }
            }

            abstract static class TopMixIn {
                @JsonCreator
                TopMixIn(@JsonProperty("arguments") Expression[] arguments) { }
            }

            abstract static class FindMixIn {
                @JsonCreator
                FindMixIn(@JsonProperty("arguments") Expression[] arguments) { }
            }

            abstract static class ShiftMixIn {
                @JsonCreator
                ShiftMixIn(@JsonProperty("arguments") Expression[] arguments) { }
            }

            abstract static class IndentMixIn {
                @JsonCreator
                IndentMixIn(@JsonProperty("argument") Expression argument) { }
            }

            abstract static class MinMixIn {
                @JsonCreator
                MinMixIn(@JsonProperty("arguments") Expression[] arguments) { }
            }

            abstract static class NotMixIn {
                @JsonCreator
                NotMixIn(@JsonProperty("arguments") Expression[] arguments) { }
            }

            abstract static class NegMixIn {
                @JsonCreator
                NegMixIn(@JsonProperty("arguments") Expression[] arguments) { }
            }

            abstract static class GetMixIn {
                @JsonCreator
                GetMixIn(@JsonProperty("arg1") Expression arg1, @JsonProperty("arg2") int arg2) { }
            }

            abstract static class LenMixIn {
                @JsonCreator
                LenMixIn(@JsonProperty("arguments") Expression[] arguments) { }
            }

            abstract static class Get2MixIn {
                @JsonCreator
                Get2MixIn(@JsonProperty("arguments") Expression[] arguments) { }
            }

            abstract static class PutMixIn {
                @JsonCreator
                PutMixIn(@JsonProperty("arguments") Expression[] arguments) { }
            }

            abstract static class ContainsMixIn {
                @JsonCreator
                ContainsMixIn(@JsonProperty("arguments") Expression[] arguments) { }
            }

            abstract static class PPDeclareMixIn {
                @JsonCreator
                PPDeclareMixIn(@JsonProperty("variable") Expression variable, @JsonProperty("value") Expression value) { }
            }

            abstract static class Put3MixIn {
                @JsonCreator
                Put3MixIn(@JsonProperty("arguments") Expression[] arguments) { }
            }

            abstract static class EndsWithMixIn {
                @JsonCreator
                EndsWithMixIn(@JsonProperty("arguments") Expression[] arguments) { }
            }

            abstract static class MapMixIn {
                @JsonCreator
                MapMixIn(@JsonProperty("arguments") Expression[] arguments) { }
            }

            abstract static class Pr2MixIn {
                @JsonCreator
                Pr2MixIn(@JsonProperty("arg1") Expression arg1, @JsonProperty("arg2") Expression arg2, @JsonProperty("arg3") Expression[] arg3) { }
            }

            abstract static class Pr3MixIn {
                @JsonCreator
                Pr3MixIn(@JsonProperty("arguments") Expression[] arguments) { }
            }

            abstract static class PPLookupMixIn {
                @JsonCreator
                PPLookupMixIn(@JsonProperty("arguments") Expression[] arguments) { }
            }

            abstract static class UndefMixIn {
                @JsonCreator
                UndefMixIn(@JsonProperty("arguments") Expression[] arguments) { }
            }

            abstract static class Pr1MixIn {
                @JsonCreator
                Pr1MixIn(@JsonProperty("arguments") Expression[] arguments) { }
            }
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

        abstract static class BinaryExpressionMixIn {
            @JsonIgnore java.lang.String symbolName;
        }

        abstract static class AndMixIn extends BinaryExpressionMixIn {
            @JsonCreator
            AndMixIn(@JsonProperty("lhs") Expression lhs,
                     @JsonProperty("rhs") Expression rhs) { }
        }

        abstract static class LShiftANDEqZeroMixIn {
            @JsonCreator
            LShiftANDEqZeroMixIn(@JsonProperty("lhs") Expression lhs, @JsonProperty("rhs") Expression rhs) { }
        }

        abstract static class GreaterThanEqualMixIn extends BinaryExpressionMixIn {
            @JsonCreator
            GreaterThanEqualMixIn(@JsonProperty("lhs") Expression lhs, @JsonProperty("rhs") Expression rhs) { }
        }

        abstract static class EqualMixIn extends BinaryExpressionMixIn {
            @JsonCreator
            EqualMixIn(@JsonProperty("lhs") Expression lhs, @JsonProperty("rhs") Expression rhs) { }
        }

        abstract static class NotEqualMixIn extends BinaryExpressionMixIn {
            @JsonCreator
            NotEqualMixIn(@JsonProperty("lhs") Expression lhs, @JsonProperty("rhs") Expression rhs) { }
        }

        abstract static class GreaterMixIn extends BinaryExpressionMixIn {
            @JsonCreator
            GreaterMixIn(@JsonProperty("lhs") Expression lhs, @JsonProperty("rhs") Expression rhs) { }
        }

        abstract static class NotMixIn {
            @JsonCreator
            NotMixIn(@JsonProperty("exp") Expression exp) { }
        }

        abstract static class OrMixIn extends BinaryExpressionMixIn {
            @JsonCreator
            OrMixIn(@JsonProperty("lhs") Expression lhs, @JsonProperty("rhs") Expression rhs) { }
        }

        abstract static class LessThanEqualMixIn extends BinaryExpressionMixIn {
            @JsonCreator
            LessThanEqualMixIn(@JsonProperty("lhs") Expression lhs, @JsonProperty("rhs") Expression rhs) { }
        }

        abstract static class LessMixIn extends BinaryExpressionMixIn {
            @JsonCreator
            LessMixIn(@JsonProperty("lhs") Expression lhs, @JsonProperty("rhs") Expression rhs) { }
        }

        abstract static class AddMixIn extends BinaryExpressionMixIn {
            @JsonCreator
            AddMixIn(@JsonProperty("lhs") Expression lhs,
                     @JsonProperty("rhs") Expression rhs) { }
        }

        abstract static class SubtractMixIn extends BinaryExpressionMixIn {
            @JsonCreator
            SubtractMixIn(@JsonProperty("lhs") Expression lhs,
                          @JsonProperty("rhs") Expression rhs) { }
        }

        abstract static class MultiplyMixIn extends BinaryExpressionMixIn {
            @JsonCreator
            MultiplyMixIn(@JsonProperty("lhs") Expression lhs,
                          @JsonProperty("rhs") Expression rhs) { }
        }

        abstract static class DivideMixIn extends BinaryExpressionMixIn {
            @JsonCreator
            DivideMixIn(@JsonProperty("lhs") Expression lhs,
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

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
    @JsonSubTypes({
        @JsonSubTypes.Type(value=Statement.Expression.class, name="ExpressionStatement"),
        @JsonSubTypes.Type(value= Statement.VariableDeclaration.class, name="VariableDeclaration")
    })
    abstract static class StatementMixIn {

        abstract static class ExpressionMixIn {
            @JsonCreator
            ExpressionMixIn(@JsonProperty("exp") Expression value) { }
        }

        abstract static class VariableDeclarationMixIn {
            @JsonCreator
            VariableDeclarationMixIn(@JsonProperty("decl") VariableDeclaration value) { }
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

    abstract static class PositionalConditionMixIn {
        @JsonCreator
        PositionalConditionMixIn(@JsonProperty("type") ConditionType type) { }
    }

    abstract static class AbstractAttrsMixIn {
        @JsonIgnore
        io.usethesource.capsule.Set.Immutable<String> env;
    }

    abstract static class ParseErrorMixIn {
        @JsonIgnore GrammarSlot slot;
        ParseErrorMixIn(
            @JsonProperty("slot") GrammarSlot slot,
            @JsonProperty("inputIndex") int inputIndex,
            @JsonProperty("lineNumber") int lineNumber,
            @JsonProperty("columnNumber") int columnNumber,
            @JsonProperty("description") String description) { }
    }

    abstract static class PrecedenceLevelMixIn {
        PrecedenceLevelMixIn(
            @JsonProperty("lhs") int lhs,
            @JsonProperty("rhs") int rhs,
            @JsonProperty("undefined") int undefined,
            @JsonProperty("hasPrefixUnary") boolean hasPrefixUnary,
            @JsonProperty("hasPostfixUnary") boolean hasPostfixUnary,
            @JsonProperty("hasPrefixUnaryBelow") boolean hasPrefixUnaryBelow,
            @JsonProperty("prefixUnaryBelow") Integer[] prefixUnaryBelow,
            @JsonProperty("hasPostfixUnaryBelow") boolean hasPostfixUnaryBelow,
            @JsonProperty("postfixUnaryBelow") Integer[] postfixUnaryBelow
        ) { }
    }

    abstract static class AssociativityGroupMixIn {
        AssociativityGroupMixIn(
            @JsonProperty("associativity") Associativity associativity,
            @JsonProperty("precedenceLevel") PrecedenceLevel precedenceLevel,
            @JsonProperty("lhs") int lhs,
            @JsonProperty("rhs") int rhs,
            @JsonProperty("precedence") int precedence) { }
    }
}
