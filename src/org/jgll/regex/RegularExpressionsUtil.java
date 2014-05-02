package org.jgll.regex;

import java.util.ArrayList;
import java.util.List;

import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.AutomatonOperations;
import org.jgll.regex.automaton.AutomatonVisitor;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.VisitAction;
import org.jgll.util.Visualization;

public class RegularExpressionsUtil {

	public static List<RegularExpression> addFollowRestrictions(List<RegularExpression> regularExpressions) {
		
		List<Automaton> automatons = new ArrayList<>();
		for (RegularExpression regex : regularExpressions) {
			automatons.add(regex.getAutomaton().setRegularExpression(regex));
		}
		
		Automaton automaton = AutomatonOperations.union(automatons).determinize();
		
		Visualization.generateAutomatonGraph("/Users/aliafroozeh/output", automaton);
		
		AutomatonVisitor.visit(automaton, new VisitAction() {
			
			@Override
			public void visit(State state) {
				if (state.isFinalState()) {
					System.out.println(state.getRegularExpressions());
					for (RegularExpression regex : state.getRegularExpressions()) {
//						regex.get
					}
				}
			}
		});
		
		return null;
	}
}
