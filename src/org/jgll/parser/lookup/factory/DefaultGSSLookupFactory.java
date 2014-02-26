package org.jgll.parser.lookup.factory;

import org.jgll.grammar.Grammar;
import org.jgll.parser.gss.GSSNodeFactory;
import org.jgll.parser.lookup.GSSLookup;
import org.jgll.parser.lookup.GSSLookupImpl;
import org.jgll.util.Input;


public class DefaultGSSLookupFactory implements GSSLookupFactory {

	private GSSNodeFactory gssNodeFactory;

	public DefaultGSSLookupFactory(GSSNodeFactory gssNodeFactory) {
		this.gssNodeFactory = gssNodeFactory;
	}
	
	@Override
	public GSSLookup createGSSLookupFactory(Grammar grammar, Input input) {
		return new GSSLookupImpl(grammar, input, gssNodeFactory);
	}

}
