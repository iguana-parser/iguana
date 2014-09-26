package org.jgll.parser;

import org.jgll.grammar.*;
import org.jgll.parser.lookup.factory.*;
import org.jgll.util.*;

public class ParserFactory {
	
	public static GLLParser newParser() {
		GSSLookupFactory gssLookupFactory = new NewGSSLookupFactory();
		SPPFLookupFactory sppfLookupFactory = new NewSPPFLookupFactory();
		DescriptorLookupFactory descriptorLookupFactory = new DefaultDescriptorLookupFactory();
		return new NewGLLParserImpl(gssLookupFactory, sppfLookupFactory, descriptorLookupFactory);
	}

	public static GLLParser newParser(Grammar grammar, Input input) {
		GSSLookupFactory gssLookupFactory = new NewGSSLookupFactory();
		SPPFLookupFactory sppfLookupFactory = new NewSPPFLookupFactory();
		DescriptorLookupFactory descriptorLookupFactory = new DefaultDescriptorLookupFactory();
		return new NewGLLParserImpl(gssLookupFactory, sppfLookupFactory, descriptorLookupFactory);
	}
	
	public static GLLParser originalParser(Grammar grammar, Input input) {
		GSSLookupFactory gssLookupFactory = new OriginalGSSLookupFactory();
		SPPFLookupFactory sppfLookupFactory = new DefaultSPPFLookupFactory();
		DescriptorLookupFactory descriptorLookupFactory = new DefaultDescriptorLookupFactory();
		return new OriginalGLLParserImpl(gssLookupFactory, sppfLookupFactory, descriptorLookupFactory);
	}

}
