package org.jgll.regex.matcher;

import java.util.regex.Pattern;

import org.jgll.util.Input;
import org.jgll.util.IntArrayCharSequence;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class JavaRegexMatcher implements Matcher {
	
	private final Pattern pattern;
	private final java.util.regex.Matcher matcher;
	
	public JavaRegexMatcher(String str) {
		this.pattern = Pattern.compile(str);
		this.matcher = pattern.matcher(""); 
	}
	
	@Override
	public int match(Input input, int i) {
        IntArrayCharSequence charSeq = input.asCharSequence();
		matcher.reset(charSeq);
		if (matcher.find(i)) {
			int end = i + matcher.end();
			return charSeq.logicalIndexAt(end - 1);									
		}
		return -1;
	}

}
