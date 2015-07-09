package org.iguana.util;

public class FailureResult implements RunResult {
	String errorMessage;
	
	public static FailureResult from(String s) {
		FailureResult failureResult = new FailureResult();
		failureResult.errorMessage = s;
		return failureResult;
	}
}
