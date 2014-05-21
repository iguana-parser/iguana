package org.jgll.parser;

import org.jgll.grammar.GrammarGraph;
import org.jgll.parser.descriptor.DescriptorFactory;
import org.jgll.parser.descriptor.factory.DescriptorFactory1;
import org.jgll.parser.gss.ArrayBasedGSSNodeFactory;
import org.jgll.parser.lookup.factory.DefaultDescriptorLookupFactory;
import org.jgll.parser.lookup.factory.DefaultGSSLookupFactory;
import org.jgll.parser.lookup.factory.DefaultSPPFLookupFactory;
import org.jgll.parser.lookup.factory.DescriptorLookupFactory;
import org.jgll.parser.lookup.factory.GSSLookupFactory;
import org.jgll.parser.lookup.factory.SPPFLookupFactory;
import org.jgll.util.Input;

public class ParserFactory {
	
	public static GLLParser newParser(GrammarGraph grammar, Input input) {
		GSSLookupFactory gssLookupFactory = new DefaultGSSLookupFactory(new ArrayBasedGSSNodeFactory(input));
		SPPFLookupFactory sppfLookupFactory = new DefaultSPPFLookupFactory();
		DescriptorLookupFactory descriptorLookupFactory = new DefaultDescriptorLookupFactory();
		DescriptorFactory descriptorFactory = new DescriptorFactory1();
		return new GLLParserImpl(gssLookupFactory, sppfLookupFactory, descriptorFactory, descriptorLookupFactory);
	}
}
