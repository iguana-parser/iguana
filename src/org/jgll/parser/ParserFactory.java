package org.jgll.parser;

import org.jgll.grammar.GrammarGraph;
import org.jgll.parser.lookup.factory.DefaultDescriptorLookupFactory;
import org.jgll.parser.lookup.factory.DefaultSPPFLookupFactory;
import org.jgll.parser.lookup.factory.DescriptorLookupFactory;
import org.jgll.parser.lookup.factory.GSSLookupFactory;
import org.jgll.parser.lookup.factory.NewGSSLookupFactory;
import org.jgll.parser.lookup.factory.OriginalGSSLookupFactory;
import org.jgll.parser.lookup.factory.SPPFLookupFactory;
import org.jgll.util.Input;

public class ParserFactory {

	public static GLLParser newParser(GrammarGraph grammar, Input input) {
		GSSLookupFactory gssLookupFactory = new NewGSSLookupFactory();
		SPPFLookupFactory sppfLookupFactory = new DefaultSPPFLookupFactory();
		DescriptorLookupFactory descriptorLookupFactory = new DefaultDescriptorLookupFactory();
		return new NewGLLParserImpl(gssLookupFactory, sppfLookupFactory, descriptorLookupFactory);
	}
	
	public static GLLParser originalParser(GrammarGraph grammar, Input input) {
		GSSLookupFactory gssLookupFactory = new OriginalGSSLookupFactory();
		SPPFLookupFactory sppfLookupFactory = new DefaultSPPFLookupFactory();
		DescriptorLookupFactory descriptorLookupFactory = new DefaultDescriptorLookupFactory();
		return new OriginalGLLParserImpl(gssLookupFactory, sppfLookupFactory, descriptorLookupFactory);
	}

}
