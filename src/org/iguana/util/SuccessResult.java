package org.iguana.util;

import java.net.URI;

public class SuccessResult implements RunResult {
	
	int runCount;
	
	int inputSize = -1;
	
	ParseStatistics statistics = new ParseStatistics();
	
	URI inputURI;

	public SuccessResult() {}
	
	public SuccessResult(int inputSize, URI input, ParseStatistics statistics) {
		this(0, inputSize, input, statistics);
	}
	
	public SuccessResult(int count, int inputSize, URI input, ParseStatistics statistics) {
		this.runCount = count;
		this.statistics = statistics;
		this.inputURI = input;
		this.inputSize = inputSize;
	}
	
	@Override
	public URI getInput() {
		return inputURI;
	}

	@Override
	public boolean isSuccess() {
		return true;
	}

	public ParseStatistics getStatistics() {
		return statistics;
	}
}
