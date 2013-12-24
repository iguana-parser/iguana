package org.jgll.regex;

import java.io.Serializable;
import java.util.BitSet;

public interface RegularExpression extends Serializable {

	public NFA toNFA();
	
	public boolean isNullable();
	
	/** 
	 * Returns the first characters that can be matched by this regular expression.
	 */
	public BitSet asBitSet();
	
}
