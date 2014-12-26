package org.jgll.grammar.slot;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.Input;


/**
 * 
 * @author Al Afroozeh
 *
 */
public class ChoiceGrammarSlot implements GrammarSlot {

	private final List<Transition> transitions;

	public ChoiceGrammarSlot(List<Transition> transitions) {
		this.transitions = new ArrayList<>(transitions);
	}
	
	@Override
	public void execute(GLLParser parser, Input input, NonPackedNode node) {
		transitions.forEach(t -> t.execute(parser, input, node));
	}

	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return null;
	}
	
	public List<Transition> getTransitions() {
		return transitions;
	}
	
	public Set<GrammarSlot> getReachableSlots() {
		return transitions.stream().map(t -> t.destination()).collect(Collectors.toSet());
	}

}
