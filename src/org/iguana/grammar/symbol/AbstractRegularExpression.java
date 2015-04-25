package org.jgll.grammar.symbol;

import org.jgll.regex.RegularExpression;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.matcher.Matcher;
import org.jgll.regex.matcher.MatcherFactory;


public abstract class AbstractRegularExpression extends AbstractSymbol implements RegularExpression {

	private static final long serialVersionUID = 1L;
	
	protected transient Automaton automaton;
	
	protected transient Matcher matcher;
	
	protected transient Matcher backwardsMatcher;
	
	public AbstractRegularExpression(SymbolBuilder<? extends RegularExpression> builder) {
		super(builder);
	}
	
	@Override
	public final Automaton getAutomaton() {
		if (automaton == null) {
			automaton = createAutomaton();
		}
		return automaton;
	}
	
	@Override
	public Matcher getMatcher() {
		if (matcher == null) {
			matcher = MatcherFactory.getMatcher(this);
		}
		return matcher;
	}
	
	@Override
	public Matcher getBackwardsMatcher() {
		if (backwardsMatcher == null) {
			backwardsMatcher = MatcherFactory.getBackwardsMatcher(this);
		}
		return backwardsMatcher;
	}
	
	protected abstract Automaton createAutomaton();
	
	@Override
	public void initMatcher() {
		backwardsMatcher = MatcherFactory.getBackwardsMatcher(this);
		matcher = MatcherFactory.getMatcher(this);
	}
}
