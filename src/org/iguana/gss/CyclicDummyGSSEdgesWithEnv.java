package org.iguana.gss;

import org.iguana.datadependent.env.Environment;
import org.iguana.result.Result;

public class CyclicDummyGSSEdgesWithEnv<T extends Result> extends CyclicDummyGSSEdges<T> {

    private final Environment env;

    public CyclicDummyGSSEdgesWithEnv(Environment env) {
        this.env = env;
    }

    @Override
    public Environment getEnv() {
        return env;
    }
}
