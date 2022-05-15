package org.iguana.parser.iggy;

import iguana.utils.input.Input;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.ParseTreeNode;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IGGYTest {
	
	@Test
	public void test() throws IOException {
		
		Input input = Input.fromFile(new File("test/org/iguana/parser/iggy/examples/Example1.iggy"));
		
		RuntimeGrammar grammar = Grammar.load(new File("test/org/iguana/parser/iggy/IGGY")).toRuntimeGrammar();
		
		DesugarPrecedenceAndAssociativity precedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
		precedenceAndAssociativity.setOP2();
		
		grammar = new EBNFToBNF().transform(grammar);
		grammar = precedenceAndAssociativity.transform(grammar);
		grammar = new LayoutWeaver().transform(grammar);

        IguanaParser parser = new IguanaParser(grammar);
        ParseTreeNode result = parser.getParseTree();

        assertNotNull(result);
        assertEquals(0, parser.getStatistics().getAmbiguousNodesCount());
	}

}
