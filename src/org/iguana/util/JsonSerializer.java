package org.iguana.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import iguana.utils.input.Input;
import org.iguana.grammar.symbol.*;
import org.iguana.parsetree.*;

import java.io.IOException;

public class JsonSerializer {

    public static String serialize(ParseTreeNode<?> node) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE));

        TypeResolverBuilder<?> typeResolverBuilder = new CustomTypeResolverBuilder();
        typeResolverBuilder.init(JsonTypeInfo.Id.NAME, null);
        typeResolverBuilder.inclusion(JsonTypeInfo.As.PROPERTY);
        typeResolverBuilder.typeProperty("kind");
        mapper.setDefaultTyping(typeResolverBuilder);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        mapper.addMixIn(Nonterminal.class, NonterminalMixIn.class);
        mapper.addMixIn(Rule.class, RuleMixIn.class);
        mapper.addMixIn(TerminalNode.class, TerminalNodeMixIn.class);
        mapper.addMixIn(NonterminalNode.class, NonterminalNodeMixIn.class);
        mapper.addMixIn(AmbiguityNode.class, AmbiguityNodeMixIn.class);
        mapper.addMixIn(ListNode.class, ListNode.class);

        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);

        DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
        pp.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

        try {
            return mapper.writer(pp).writeValueAsString(node);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static NonterminalNode deserialize(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, NonterminalNode.class);
    }


    static class CustomTypeResolverBuilder extends ObjectMapper.DefaultTypeResolverBuilder {

        private static final long serialVersionUID = 1L;

        public CustomTypeResolverBuilder() {
            super(ObjectMapper.DefaultTyping.NON_FINAL);
        }

        @Override
        public boolean useForType(JavaType t) {
            if (!t.isEnumType() && t.getRawClass().getName().startsWith("org.iguana")) {
                return true;
            }

            return false;
        }
    }

    abstract static class NonterminalMixIn {

        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        int index;

        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        boolean ebnfList;
    }

    abstract static class RuleMixIn {
        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        int precedence;

        @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = LayoutStrategyFilter.class)
        LayoutStrategy layoutStrategy;

        @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = RecursionFilter.class)
        Recursion recursion;

        @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = RecursionFilter.class)
        Recursion irecursion;

        @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = AssociativityFilter.class)
        Associativity associativity;
    }

    abstract class TerminalNodeMixIn {
        @JsonIgnore
        Input input;
    }

    abstract class NonterminalNodeMixIn {
        @JsonIgnore
        Input input;
    }

    abstract class AmbiguityNodeMixIn {
        @JsonIgnore
        Input input;
    }

    abstract class ListNodeMixIn {
        @JsonIgnore
        Input input;
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

    static class RecursionFilter {
        @Override
        public boolean equals(Object obj) {
            return obj == Recursion.UNDEFINED;
        }

        @Override
        public int hashCode() {
            return Recursion.UNDEFINED.hashCode();
        }

    }

    static class AssociativityFilter {
        @Override
        public boolean equals(Object obj) {
            return obj == Associativity.UNDEFINED;
        }

        @Override
        public int hashCode() {
            return Associativity.UNDEFINED.hashCode();
        }

    }
}


