package org.iguana.datadependent.env.intarray;

import org.iguana.datadependent.env.AbstractEvaluatorContext;
import org.iguana.datadependent.env.Environment;

public class IntArrayEvaluatorContext extends AbstractEvaluatorContext {

    public IntArrayEvaluatorContext() {
        setEnvironment(getEmptyEnvironment());
    }

    @Override
    public Environment getEmptyEnvironment() {
        return IntArrayEnvironment.EMPTY;
    }
}
