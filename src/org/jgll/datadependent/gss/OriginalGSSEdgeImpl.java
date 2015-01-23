package org.jgll.datadependent.gss;

import org.jgll.datadependent.env.Environment;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;

public class OriginalGSSEdgeImpl extends org.jgll.parser.gss.OriginalGSSEdgeImpl {
	
	@SuppressWarnings("unused")
	private final Environment env;

	public OriginalGSSEdgeImpl(NonPackedNode node, GSSNode destination, Environment env) {
		super(node, destination);
		
		assert env != null;
		this.env = env;
	}

}
