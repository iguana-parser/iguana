package org.jgll.grammar.condition;

import java.util.List;

import org.jgll.grammar.Keyword;

/**
 * 
 * Conditions relating to the keyword exclusions or follow restrictions. 
 * For example, Id !>> "[]" or Id \ "if"
 * 
 * @author Ali Afroozeh
 *
 */
public class KeywordCondition extends Condition {
	
	private List<Keyword> keywords;
	
	public KeywordCondition(ConditionType type, List<Keyword> keywords) {
		super(type);
		this.keywords = keywords;
	}
	
	public List<Keyword> getKeywords() {
		return keywords;
	}

}
