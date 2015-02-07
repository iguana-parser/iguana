package org.jgll.regex.automaton;

@FunctionalInterface
public interface VisitAction{
	public void visit(State state);
}
