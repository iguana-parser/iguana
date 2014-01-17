package org.jgll.regex;

import java.io.Serializable;

public interface MatchAction extends Serializable {
	
	public void execute(int length, int state);

}
