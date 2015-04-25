package org.iguana.regex.automaton;

@FunctionalInterface
public interface VisitAction{
	public void visit(State state);
}
