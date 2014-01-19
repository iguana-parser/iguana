package org.jgll.regex;

import java.io.Serializable;

public interface StateAction extends Serializable {
	
	public void execute(int length, int state);

}
