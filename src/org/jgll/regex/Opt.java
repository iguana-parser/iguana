package org.jgll.regex;


public class Opt implements RegularExpression {

	private static final long serialVersionUID = 1L;

	private RegularExpression regexp;
	
	public Opt(RegularExpression regexp) {
		
	}
	
	@Override
	public NFA toNFA() {
		return null;
	}

}
