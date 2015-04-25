package org.jgll.parser;

import org.jgll.sppf.NonterminalNode;
import org.jgll.util.ParseStatistics;

public class ParseSuccess implements ParseResult {

	private NonterminalNode sppfNode;
	private ParseStatistics parseStatistics;

	public ParseSuccess(NonterminalNode sppfNode, ParseStatistics parseStatistics) {
		this.sppfNode = sppfNode;
		this.parseStatistics = parseStatistics;
	}
	
	@Override
	public boolean isParseError() {
		return false;
	}

	@Override
	public boolean isParseSuccess() {
		return true;
	}

	@Override
	public ParseError asParseError() {
		throw new RuntimeException("Cannot call getParseError on Success.");
	}

	@Override
	public ParseSuccess asParseSuccess() {
		return this;
	}
	
	public NonterminalNode getRoot() {
		return sppfNode;
	}
	
	public ParseStatistics getStatistics() {
		return parseStatistics;
	}
	
	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction.hash(parseStatistics.hashCode(), sppfNode.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (!(obj instanceof ParseSuccess))
			return false;
		
		ParseSuccess other = (ParseSuccess) obj;
		return parseStatistics.equals(other.parseStatistics) && sppfNode.deepEquals(other.sppfNode);
	}
	
	@Override
	public String toString() {
		return sppfNode + "\n" + parseStatistics;
	}
	
}
