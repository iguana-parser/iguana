package org.iguana.grammar.slot;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.iguana.regex.RegularExpression;
import org.iguana.regex.matcher.Matcher;
import org.iguana.regex.matcher.MatcherFactory;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.Input;
import org.iguana.util.collections.Key;


public class TerminalGrammarSlot extends AbstractGrammarSlot {
	
	private RegularExpression regex;
	private Matcher matcher;
	private Map<Key, TerminalNode> terminalNodes;

	public TerminalGrammarSlot(int id, RegularExpression regex) {
		super(id, Collections.emptyList());
		this.regex = regex;
		this.matcher = MatcherFactory.getMatcher(regex);
		this.terminalNodes = new HashMap<>();
	}

	@Override
	public String getConstructorCode() {
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
		return terminalNodes.computeIfAbsent(key, k -> { TerminalNode val = s.get();
														 c.accept(val);
														 return val; 
													   });
	}
	
	public TerminalNode findTerminalNode(Key key) {
		return terminalNodes.get(key);
	}

	@Override
	public void reset(Input input) {
		terminalNodes = new HashMap<>();
	}

}
