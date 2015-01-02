package org.jgll.grammar.slot;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.regex.Matcher;
import org.jgll.regex.RegularExpression;
import org.jgll.sppf.TerminalNode;
import org.jgll.sppf.lookup.NodeAddedAction;
import org.jgll.util.Input;


public class TerminalGrammarSlot implements GrammarSlot {
	
	private RegularExpression regex;
	private Matcher matcher;
	private Map<TerminalNode, TerminalNode> terminalNodes;

	public TerminalGrammarSlot(RegularExpression regex) {
		this.regex = regex;
		// TODO: add type of regex to config!
		this.matcher = regex.getMatcher();
		this.terminalNodes = new HashMap<>();
	}

	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return null;
	}

	public RegularExpression getRegularExpression() {
		return regex;
	}
	
	public int match(Input input, int i) {
		return matcher.match(input, i);
	}
	
	@Override
	public Set<Transition> getTransitions() {
		return Collections.emptySet();
	}

	@Override
	public boolean addTransition(Transition transition) {
		return false;
	}

	@Override
	public String toString() {
		return regex.toString();
	}
	
	public TerminalNode getTerminalNode(TerminalNode node, NodeAddedAction<TerminalNode> action) {
		return terminalNodes.computeIfAbsent(node, k -> { action.execute(k); return k; });
	}
	
	public TerminalNode findTerminalNode(TerminalNode node) {
		return terminalNodes.get(node);
	}

	@Override
	public void reset() {
		terminalNodes = new HashMap<>();
	}

}
