package org.iguana.datadependent.attrs;

import org.eclipse.imp.pdb.facts.util.ImmutableSet;

public interface Attr {
	
	public ImmutableSet<String> getEnv();
	public void setEnv(ImmutableSet<String> env);
	public void setEmpty();

}
