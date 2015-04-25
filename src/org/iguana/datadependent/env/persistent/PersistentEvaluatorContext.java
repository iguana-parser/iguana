package org.iguana.datadependent.env.persistent;

import org.iguana.datadependent.env.AbstractEvaluatorContext;
import org.iguana.datadependent.env.Environment;
import org.iguana.util.Input;

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
