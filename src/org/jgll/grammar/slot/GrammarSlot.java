package org.jgll.grammar.slot;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgll.grammar.condition.Condition;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.gss.GSSNodeData;
import org.jgll.util.Input;
import org.jgll.util.generator.ConstructorCode;

/**
 * A GrammarSlot is a position immediately before or after
 * a symbol in the body of a production rule. 
 * Grammar slots, similar to LR items, are represented by  
 * For example, in the rule X ::= alpha . beta, the grammar 
 * slot, denoted by ., is after
 * alpha and before beta, where alpha are a list of grammar symbols.
 * 
 *
 * @author Ali Afroozeh
 *
 */
public interface GrammarSlot extends ConstructorCode {
	
	default Set<GrammarSlot> getReachableSlots() {
		return getTransitions().stream().map(t -> t.destination()).collect(Collectors.toSet());
	}
	
	/**
	 * Corresponds to a grammar position A ::=  . \alpha 
	 */
	default boolean isFirst() { return false; }
	
	/**
	 * Corresponds to a grammar position A ::= \alpha . 
	 */
	default boolean isLast() { return false; }

	default Set<Condition> getConditions() {
		return Collections.emptySet();
	}
	
	default GSSNode getGSSNode(int inputIndex) { return null; }
	
	default GSSNode hasGSSNode(int inputIndex) { return null; }
	
	public void reset(Input input);
	
	public boolean addTransition(Transition transition);
	
	public Set<Transition> getTransitions();
	
	default GrammarSlot withId(int id) { return this; }
	
	default int getId() { return 0; }
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	default <T> GSSNode getGSSNode(int inputIndex, GSSNodeData<T> data) { return null; }
	
	default <T> GSSNode hasGSSNode(int inputIndex, GSSNodeData<T> data) { return null; }
	
}
