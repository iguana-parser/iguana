package org.jgll.regex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Range;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.AutomatonOperations;
import org.jgll.regex.automaton.AutomatonVisitor;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.Transition;
import org.jgll.regex.automaton.VisitAction;
import org.jgll.util.Visualization;

public class RegularExpressionsUtil {

	public static Iterable<RegularExpression> addFollowRestrictions(Iterable<RegularExpression> regularExpressions) {
		
		final Set<RegularExpression> newRegularExpressions = new HashSet<>();
		
		List<Automaton> automatons = new ArrayList<>();
		for (RegularExpression regex : regularExpressions) {
			automatons.add(regex.getAutomaton().setRegularExpression(regex));
		}
		
		Automaton automaton = AutomatonOperations.union(automatons).determinize();
		
		AutomatonVisitor.visit(automaton, new VisitAction() {
			
			@Override
			public void visit(State state) {
				if (state.isFinalState()) {
					System.out.println(state.getRegularExpressions());
					for (RegularExpression regex : state.getRegularExpressions()) {
						for (Transition t : state.getTransitions()) {
							for (Range range : regex.getNotFollowSet()) {
								if (!range.overlaps(t.getRange())) {
									newRegularExpressions.add(regex.withCondition(RegularExpressionCondition.notFollow(t.getRange())));
								}								
							}
						}
					}
				}
			}
		});
		
		return newRegularExpressions;
	}
}
