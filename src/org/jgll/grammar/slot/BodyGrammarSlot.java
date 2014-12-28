package org.jgll.grammar.slot;

import java.util.Set;
import java.util.stream.Collectors;

import org.jgll.grammar.GrammarSlotRegistry;


public class BodyGrammarSlot extends AbstractGrammarSlot {

	public BodyGrammarSlot() {
		super();
	}
	
	public BodyGrammarSlot(Set<Transition> transitions) {
		super(transitions);
	}
	
	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		StringBuilder sb = new StringBuilder();
		sb.append("new BodyGrammarSlot(")
		  .append("Sets.newHashSet(")
		  .append(transitions.stream().map(t -> t.getConstructorCode(registry)).collect(Collectors.joining(", ")))
		  .append("))");
		return sb.toString();
	}

}
