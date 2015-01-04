package org.jgll.grammar.slot;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jgll.grammar.GrammarRegistry;
import org.jgll.regex.Matcher;
import org.jgll.regex.RegularExpression;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Input;
import org.jgll.util.hashing.Key;


public class TerminalGrammarSlot implements GrammarSlot {
	
	private RegularExpression regex;
	private Matcher matcher;
	private Map<Key, TerminalNode> terminalNodes;

	public TerminalGrammarSlot(RegularExpression regex) {
		this.regex = regex;
		// TODO: add type of regex to config!
		this.matcher = regex.getMatcher();
		this.terminalNodes = new HashMap<>(1000);
	}

	@Override
	public String getConstructorCode(GrammarRegistry registry) {
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
	
	public TerminalNode getTerminalNode(Key key, Supplier<TerminalNode> s, Consumer<TerminalNode> c) {
		TerminalNode val;
		if ((val = terminalNodes.get(key)) == null) {
			val = s.get();
			c.accept(val);
			terminalNodes.put(key, val);
		}
		return val;
	}
	
	public TerminalNode findTerminalNode(Key key) {
		return terminalNodes.get(key);
	}

	@Override
	public void reset(Input input) {
		terminalNodes = new HashMap<>();
	}

}
