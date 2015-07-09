package org.iguana.util;

import java.net.URI;

public interface RunResult {
	
	public boolean isSuccess();
	
	default SuccessResult asSuccess() { return (SuccessResult) this; }
	
	default boolean isFailure() { return !isSuccess(); }
	
	default FailureResult asFailure() { return (FailureResult) this; }

	public URI getInput(); 
}
