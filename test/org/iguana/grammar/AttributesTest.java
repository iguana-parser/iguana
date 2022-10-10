package org.iguana.grammar;

import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.transformation.GrammarTransformer;
import org.iguana.regex.Char;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AttributesTest {

    @Test
    public void testTransformationChainShouldKeepAttributes() {
        // A = "a"
        Rule.Builder ruleBuilder = new Rule.Builder(Nonterminal.withName("A"));
        PriorityLevel.Builder priorityLevelBuilder = new PriorityLevel.Builder();
        Alternative.Builder alternativeBuilder = new Alternative.Builder();
        Sequence.Builder sequenceBuilder = new Sequence.Builder();
        sequenceBuilder.addSymbol(Terminal.from(Char.from('a')));
        sequenceBuilder.addAttribute("test", 123);
        alternativeBuilder.addSequence(sequenceBuilder.build());
        priorityLevelBuilder.addAlternative(alternativeBuilder.build());
        ruleBuilder.addPriorityLevel(priorityLevelBuilder.build());
        Rule rule = ruleBuilder.build();

        Grammar.Builder grammarBuilder = new Grammar.Builder();
        grammarBuilder.addStartSymbol(Start.from("A"));
        grammarBuilder.addRule(rule);
        Grammar grammar = grammarBuilder.build();

        RuntimeGrammar result = GrammarTransformer.transform(grammar.toRuntimeGrammar());
        RuntimeRule runtimeRule = result.getRules().get(0);

        Assertions.assertTrue(runtimeRule.getAttributes().containsKey("test"));
        Assertions.assertEquals(runtimeRule.getAttributes().get("test"), 123);
    }
}
