package org.iguana.grammar.slot;

import java.util.Set;

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.condition.Condition;
import org.iguana.parser.GLLParser;
import org.iguana.parser.gss.GSSNode;
import org.iguana.sppf.DummyNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.TerminalNode;


/**
 * 
 * Corresponds to grammar slots of the form A ::= . b
 * 
 * @author Ali Afroozeh
 *
 */
public class FirstAndLastTerminalTransition extends AbstractTerminalTransition {

	public FirstAndLastTerminalTransition(TerminalGrammarSlot slot, BodyGrammarSlot origin, BodyGrammarSlot dest, Set<Condition> preConditions, Set<Condition> postConditions) {
		super(slot, origin, dest, preConditions, postConditions);
	}

	@Override
	protected void createNode(int length, TerminalNode cr, GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		if (dest.isEnd())
			dest.execute(parser, u, i + length, parser.getNonterminalNode((LastSymbolGrammarSlot) dest, cr));
		else {
			parser.setCurrentEndGrammarSlot(DummySlot.getInstance());
			dest.execute(parser, u, i + length, DummyNode.getInstance(cr.getLeftExtent(), i + length));
			
			if (parser.getCurrentEndGrammarSlot().isEnd())
				parser.getCurrentEndGrammarSlot().execute(parser, u, i + length, parser.getNonterminalNode((LastSymbolGrammarSlot) dest, cr));
		}
	}
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	@Override
	protected void createNode(int length, TerminalNode cr, GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env) {
		if (dest.isEnd()) {
			if (u instanceof org.iguana.datadependent.gss.GSSNode<?>) {
				org.iguana.datadependent.gss.GSSNode<?> gssNode = (org.iguana.datadependent.gss.GSSNode<?>) u;
				dest.execute(parser, u, i + length, parser.getNonterminalNode((LastSymbolGrammarSlot) dest, cr, gssNode.getData()), env);
			} else
				dest.execute(parser, u, i + length, parser.getNonterminalNode((LastSymbolGrammarSlot) dest, cr), env);
		} else {
			parser.setCurrentEndGrammarSlot(DummySlot.getInstance());
			dest.execute(parser, u, i + length, DummyNode.getInstance(cr.getLeftExtent(), i + length), env);
			
			if (parser.getCurrentEndGrammarSlot().isEnd()) {
				if (u instanceof org.iguana.datadependent.gss.GSSNode<?>) {
					org.iguana.datadependent.gss.GSSNode<?> gssNode = (org.iguana.datadependent.gss.GSSNode<?>) u;
					parser.getCurrentEndGrammarSlot().execute(parser, u, i + length, parser.getNonterminalNode((LastSymbolGrammarSlot) dest, cr, gssNode.getData()), parser.getEnvironment());
				} else
					parser.getCurrentEndGrammarSlot().execute(parser, u, i + length, parser.getNonterminalNode((LastSymbolGrammarSlot) dest, cr), parser.getEnvironment());
			}
		}
	}
}
