package org.jgll.regex;

import java.io.Serializable;
import java.util.Set;

import org.jgll.grammar.symbol.Range;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.util.generator.ConstructorCode;

public interface RegularExpression extends Serializable, Symbol, ConstructorCode {

	public Matcher matcher();
	
	public boolean isNullable();
	
	public Set<Range> getFirstSet();
	
	/**
	 * The set of characters (ranges) that cannot follow this regular expressions. 
	 */
	public Set<Range> getNotFollowSet();
	
}
