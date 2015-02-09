package org.jgll.datadependent.attrs;

import org.eclipse.imp.pdb.facts.util.ImmutableSet;

public abstract class AbstractAttrs implements Attr {
	
	private ImmutableSet<String> env;

	@Override
	public ImmutableSet<String> getEnv() {
		return env;
	}

	@Override
	public void setEnv(ImmutableSet<String> env) {
		this.env = env;
	}

}
