package org.jgll.grammar.condition;

import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.Keyword;
import static org.jgll.util.collections.CollectionsUtil.*;


/**
 * 
 * Conditions relating to the keyword exclusions or follow restrictions. 
 * For example, Id !>> "[]" or Id \ "if"
 * 
 * @author Ali Afroozeh
 *
 */
public class KeywordCondition extends Condition {
	
	private static final long serialVersionUID = 1L;
	
	private List<Keyword> keywords;
	
	public KeywordCondition(ConditionType type, Keyword keyword) {
		super(type);
		keywords = new ArrayList<>();
		keywords.add(keyword);
	}
	
	public KeywordCondition(ConditionType type, List<Keyword> keywords) {
		super(type);
		this.keywords = new ArrayList<>(keywords);
	}
	
	public List<Keyword> getKeywords() {
		return keywords;
	}
	
	@Override
	public String toString() {
		return type.toString() + " " +  listToString(keywords);
	}

}
