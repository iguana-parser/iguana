package org.jgll.datadependent.env.persistent;

import org.jgll.datadependent.env.AbstractEvaluatorContext;
import org.jgll.datadependent.env.Environment;

public class PersistentEvaluatorContext extends AbstractEvaluatorContext {
	
	public PersistentEvaluatorContext() {
		setEnvironment(PersistentEnvironment.EMPTY);
	}

	@Override
	public Environment getEmptyEnvironment() {
		return PersistentEnvironment.EMPTY;
	}

}
