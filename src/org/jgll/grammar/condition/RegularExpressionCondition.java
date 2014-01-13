package org.jgll.grammar.condition;

import java.util.List;

import org.jgll.grammar.symbol.Keyword;
import org.jgll.regex.RegexAlt;
import org.jgll.regex.RegularExpression;

/**
 * Conditions relating to the keyword exclusions or follow restrictions. 
 * For example, Id !>> "[]" or Id \ "if"
 * 
 * @author Ali Afroozeh
 *
 */
public class RegularExpressionCondition extends Condition {
	
	private static final long serialVersionUID = 1L;
	
	private RegularExpression regularExpression;
	
	public RegularExpressionCondition(ConditionType type, RegularExpression regularExpression) {
		super(type);
		this.regularExpression = regularExpression;
	}

	public RegularExpression getRegularExpression() {
		return regularExpression;
	}
	
	@Override
	public String toString() {
		return type.toString() + " " +  regularExpression;
	}
	
	public static RegularExpressionCondition notMatch(RegularExpression regularExpression) {
		return new RegularExpressionCondition(ConditionType.NOT_MATCH, regularExpression);
	}
	
	public static RegularExpressionCondition notMatch(List<Keyword> keywords) {
		return notMatch(new RegexAlt<>(keywords));
	}
	
	public static RegularExpressionCondition notMatch(Keyword...keywords) {
		return notMatch(new RegexAlt<>(keywords));
	}
	
	public static RegularExpressionCondition match(RegularExpression regularExpression) {
		return new RegularExpressionCondition(ConditionType.MATCH, regularExpression);
	}
	
	public static RegularExpressionCondition match(List<Keyword> keywords) {
		return match(new RegexAlt<>(keywords));
	}
	
	public static RegularExpressionCondition match(Keyword...keywords) {
		return match(new RegexAlt<>(keywords));
	}

	public static RegularExpressionCondition notFollow(RegularExpression regularExpression) {
		return new RegularExpressionCondition(ConditionType.NOT_FOLLOW, regularExpression);
	}
	
	public static RegularExpressionCondition notFollow(List<Keyword> keywords) {
		return notFollow(new RegexAlt<>(keywords));
	}
	
	public static RegularExpressionCondition notFollow(Keyword...keywords) {
		return notFollow(new RegexAlt<>(keywords));
	}
	
	public static RegularExpressionCondition follow(RegularExpression regularExpression) {
		return new RegularExpressionCondition(ConditionType.FOLLOW, regularExpression);
	}
	
	public static RegularExpressionCondition follow(List<Keyword> keywords) {
		return follow(new RegexAlt<>(keywords));
	}
	
	public static RegularExpressionCondition follow(Keyword...keywords) {
		return follow(new RegexAlt<>(keywords));
	}
	
	public static RegularExpressionCondition precede(RegularExpression regularExpression) {
		return new RegularExpressionCondition(ConditionType.PRECEDE, regularExpression);
	}
	
	public static RegularExpressionCondition precede(List<Keyword> keywords) {
		return precede(new RegexAlt<>(keywords));
	}
	
	public static RegularExpressionCondition precede(Keyword...keywords) {
		return precede(new RegexAlt<>(keywords));
	}
	
	public static RegularExpressionCondition notPrecede(RegularExpression regularExpression) {
		return new RegularExpressionCondition(ConditionType.NOT_PRECEDE, regularExpression);
	}
	
	public static RegularExpressionCondition notPrecede(List<Keyword> keywords) {
		return notPrecede(new RegexAlt<>(keywords));
	}
	
	public static RegularExpressionCondition notPrecede(Keyword...keywords) {
		return notPrecede(new RegexAlt<>(keywords));
	}
		
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof RegularExpressionCondition)) {
			return false;
		}
		
		RegularExpressionCondition other = (RegularExpressionCondition) obj;
		
		return type == other.type && regularExpression.equals(other.regularExpression);
	}
}
