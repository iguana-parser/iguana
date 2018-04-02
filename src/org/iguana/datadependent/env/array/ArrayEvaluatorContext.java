package org.iguana.datadependent.env.array;

import org.iguana.datadependent.env.AbstractEvaluatorContext;
import org.iguana.datadependent.env.Environment;

public class ArrayEvaluatorContext extends AbstractEvaluatorContext {

	public ArrayEvaluatorContext() {
		setEnvironment(ArrayEnvironment.EMPTY);
	}

	@Override
	public Environment getEmptyEnvironment() {
		return ArrayEnvironment.EMPTY;
	}

}
