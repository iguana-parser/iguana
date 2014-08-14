package org.jgll.parser;

import org.jgll.sppf.NonterminalNode;
import org.jgll.traversal.ModelBuilderVisitor;
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
	
	public ParseStatistics getParseStatistics() {
		return parseStatistics;
	}
	
	@SuppressWarnings("unchecked")
	public <T, U> U build(ModelBuilderVisitor<T, U> visitor) {
		sppfNode.accept(visitor);
		return (U) sppfNode.getObject();
	}

}
