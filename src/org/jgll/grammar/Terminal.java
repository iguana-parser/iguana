package org.jgll.grammar;

import java.io.Serializable;

public abstract class Terminal implements Serializable {
	
	protected int id;
	
	public abstract boolean match(int i);
	
	public int getId() {
		return id;
	}
}
