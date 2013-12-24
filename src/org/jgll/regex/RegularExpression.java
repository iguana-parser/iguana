package org.jgll.regex;

import java.io.Serializable;

public interface RegularExpression extends Serializable {

	public NFA toNFA();
	
}
