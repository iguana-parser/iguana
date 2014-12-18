package org.jgll.parser;

import org.jgll.parser.lookup.factory.DefaultDescriptorLookupFactory;
import org.jgll.parser.lookup.factory.DefaultSPPFLookupFactory;
import org.jgll.parser.lookup.factory.DescriptorLookupFactory;
import org.jgll.parser.lookup.factory.DistributedGSSLookupFactory;
import org.jgll.parser.lookup.factory.GSSLookupFactory;
import org.jgll.parser.lookup.factory.HashGSSLookupFactory;
import org.jgll.parser.lookup.factory.NewSPPFLookupFactory;
import org.jgll.parser.lookup.factory.SPPFLookupFactory;
import org.jgll.sppf.SPPFUtil;
import org.jgll.util.Configuration;
import org.jgll.util.Configuration.Builder;
import org.jgll.util.Configuration.GSSType;
import org.jgll.util.Configuration.LookupType;

public class ParserFactory {
	
	public static GLLParser getParser() {
		return newParser();
	}
	
	public static GLLParser newParser() {
		SPPFUtil.init(Configuration.DEFAULT);
		GSSLookupFactory gssLookupFactory = new DistributedGSSLookupFactory();
		SPPFLookupFactory sppfLookupFactory = new NewSPPFLookupFactory();
		DescriptorLookupFactory descriptorLookupFactory = new DefaultDescriptorLookupFactory();
		return new NewGLLParserImpl(gssLookupFactory, sppfLookupFactory, descriptorLookupFactory);
	}

	public static GLLParser originalParser() {
		
		Configuration config = new Builder().setGSSType(GSSType.ORIGINAL)
											.setHashFunction(HashFunctions.primeMultiplication)
											.setLookupType(LookupType.MAP_DISTRIBUTED).build();
		SPPFUtil.init(config);
		GSSLookupFactory gssLookupFactory = new HashGSSLookupFactory();
		SPPFLookupFactory sppfLookupFactory = new DefaultSPPFLookupFactory();
		DescriptorLookupFactory descriptorLookupFactory = new DefaultDescriptorLookupFactory();
		return new OriginalGLLParserImpl(gssLookupFactory, sppfLookupFactory, descriptorLookupFactory);
	}

}
