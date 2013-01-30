package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;

public interface Codeable {
	
	public void code(Writer writer) throws IOException;
	
}
