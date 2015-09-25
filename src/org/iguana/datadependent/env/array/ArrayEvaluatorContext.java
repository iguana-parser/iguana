package org.iguana.datadependent.env.array;

import iguana.utils.input.Input;
import org.iguana.datadependent.env.AbstractEvaluatorContext;
import org.iguana.datadependent.env.Environment;

public class ArrayEvaluatorContext extends AbstractEvaluatorContext {

	public ArrayEvaluatorContext(Input input) {
		super(input);
		setEnvironment(ArrayEnvironment.EMPTY);
	}

	@Override
	public Environment getEmptyEnvironment() {
		return ArrayEnvironment.EMPTY;
	}

}
