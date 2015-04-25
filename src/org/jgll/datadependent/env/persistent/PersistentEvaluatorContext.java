package org.jgll.datadependent.env.persistent;

import org.jgll.datadependent.env.AbstractEvaluatorContext;
import org.jgll.datadependent.env.Environment;
import org.jgll.util.Input;

public class PersistentEvaluatorContext extends AbstractEvaluatorContext {
	
	public PersistentEvaluatorContext(Input input) {
		super(input);
		setEnvironment(PersistentEnvironment.EMPTY);
	}

	@Override
	public Environment getEmptyEnvironment() {
		return PersistentEnvironment.EMPTY;
	}

}
