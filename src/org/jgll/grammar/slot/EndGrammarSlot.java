package org.jgll.grammar.slot;

import java.util.Collections;
import java.util.Set;

import org.jgll.datadependent.env.Environment;
import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.symbol.Position;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.gss.lookup.GSSNodeLookup;
import org.jgll.sppf.NonPackedNode;

public class EndGrammarSlot extends BodyGrammarSlot {

	public EndGrammarSlot(int id, Position position, GSSNodeLookup nodeLookup, 
			String label, String variable, Set<Condition> conditions) {
		super(id, position, nodeLookup, label, variable, conditions);
	}

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		if (node.isDummy()) {
			parser.setCurrentEndGrammarSlot(this);
			return;
		}
		parser.pop(u, i, node);
	}
	
	@Override
	public boolean isEnd() {
		return true;
	}
	
	@Override
	public String getConstructorCode() {
		return null;
	}
	
	public Object getObject() {
		return null;
	}
	
	@Override
	public Set<Transition> getTransitions() {
		return Collections.emptySet();
	}

	@Override
	public boolean addTransition(Transition transition) {
		return false;
	}
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env) {
		if (node.isDummy()) {
			parser.setEnvironment(env);
			parser.setCurrentEndGrammarSlot(this);
			return;
		}
		parser.pop(u, i, node);
	}

}
