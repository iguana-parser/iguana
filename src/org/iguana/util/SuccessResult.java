package org.iguana.util;

import java.net.URI;

public class SuccessResult implements RunResult {
	
	int count;
	
	ParseStatistics statistics;
	
	URI input;

	public SuccessResult() {}
	
	public SuccessResult(int count, ParseStatistics statistics, URI input) {
		this.count = count;
		this.statistics = statistics;
		this.input = input;
	}
	
	public static SuccessResult from(ParseStatistics statistics) {
		SuccessResult successResult = new SuccessResult();
		successResult.statistics = statistics;
		return successResult;
	}
	
	public URI getInput() {
		return input;
	}
}
