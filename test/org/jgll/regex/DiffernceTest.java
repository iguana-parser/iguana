package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Range;
import org.jgll.regex.RegularExpression;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Test;


public class DiffernceTest {
	
	private RegularExpression id = RegularExpressionExamples.getId();
	private Keyword k1 = new Keyword("if");
	private Keyword k2 = new Keyword("when");
	private Keyword k3 = new Keyword("new");

	@Test
	public void test1() {
		RegexStar star = new RegexStar(new CharacterClass(new Range('a', 'z')));
		
		Automaton c1 = AutomatonOperations.makeComplete(star.toAutomaton(), AutomatonOperations.merge(AutomatonOperations.getIntervals(star.toAutomaton()), AutomatonOperations.getIntervals(k1.toAutomaton())));
		Automaton c2 = AutomatonOperations.makeComplete(k1.toAutomaton(), AutomatonOperations.merge(AutomatonOperations.getIntervals(star.toAutomaton()), AutomatonOperations.getIntervals(k1.toAutomaton())));
		
		Automaton a = AutomatonOperations.difference(star.toAutomaton(), k1.toAutomaton());
		Visualization.generateAutomatonGraph("/Users/aliafroozeh/output", a.getStartState());
		assertTrue(a.getMatcher().match(Input.fromString("first")));
	}

}
