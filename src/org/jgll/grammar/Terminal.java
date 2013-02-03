package org.jgll.grammar;

import java.io.Serializable;

public abstract class Terminal implements Serializable, Symbol {
	
	private static final long serialVersionUID = 1L;
	
	protected int id;
	
	public abstract boolean match(int i);
	
	public int getId() {
		return id;
	}
}
