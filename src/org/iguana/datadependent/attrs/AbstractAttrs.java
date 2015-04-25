package org.jgll.datadependent.attrs;

import java.io.Serializable;

import org.eclipse.imp.pdb.facts.util.ImmutableSet;
import org.eclipse.imp.pdb.facts.util.TrieSet;

public abstract class AbstractAttrs implements Attr, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private transient ImmutableSet<String> env;

	@Override
	public ImmutableSet<String> getEnv() {
		return env == null? TrieSet.of() : env;
	}

	@Override
	public void setEnv(ImmutableSet<String> env) {
		this.env = env;
	}
	
	@Override
	public void setEmpty() {
		this.env = TrieSet.of();
	}

}
