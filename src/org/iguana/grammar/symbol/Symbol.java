package org.iguana.grammar.symbol;

import java.io.Serializable;
import java.util.Set;

import org.iguana.datadependent.attrs.Attr;
import org.iguana.grammar.condition.Condition;
import org.iguana.traversal.ISymbolVisitor;
import org.iguana.util.generator.ConstructorCode;

/**
 * 
 * 
 * @author Ali Afroozeh
 * @author Anastasia Izmaylova
 * 
 * Symbol ::= Label ':' Symbol
 *          | Nonterminal '(' {Expression ','}+ ')'
 *          
 *          | ...
 *          
 *          | '{' Symbol+ '}'
 *          > "align" Symbol
 *          | "offside" Symbol 
 *          > Symbol "do" Statement
 *          | Symbol "when" Expression
 *          > "if" '(' Expression ')' Symbol
 *          | "if" '(' Expression ')' Symbol "else" Symbol
 *          | "while" '(' Expression ')' Symbol
 *          
 *
 */
public interface Symbol extends ConstructorCode, Serializable, Attr {
	
	public String getName();
	
	public Set<Condition> getPreConditions();
	
	public Set<Condition> getPostConditions();
	
	public Object getObject();
	
	public String getLabel();
	
	default boolean isTerminal() {
		return false;
	}
	
	public SymbolBuilder<? extends Symbol> copyBuilder();
	
	public int size();
	
	public String toString(int j);
	
	public <T> T accept(ISymbolVisitor<T> visitor);
	
}	
