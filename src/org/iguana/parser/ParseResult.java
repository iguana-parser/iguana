package org.iguana.parser;

public interface ParseResult {

	public boolean isParseError();
	
	public boolean isParseSuccess();
	
	public ParseError asParseError();
	
	public ParseSuccess asParseSuccess();
	
}
