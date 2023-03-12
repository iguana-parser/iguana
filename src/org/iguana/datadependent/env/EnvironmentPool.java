package org.iguana.datadependent.env;

import java.util.ArrayDeque;
import java.util.Deque;

public class EnvironmentPool {

    @SuppressWarnings({"rawtypes", "unchecked"})
    static Deque<Environment>[] environmentPools = new ArrayDeque[3];

    static {
        for (int i = 0; i < environmentPools.length; i++) {
            environmentPools[i] = new ArrayDeque<>(1000);
        }
    }

    public static void clean() {
        for (int i = 0; i < environmentPools.length; i++) {
            environmentPools[i].clear();
        }
    }

    public static Environment get(int size) {
        if (size >= 1 && size <= 3) {
            Deque<Environment> environmentPool = environmentPools[size - 1];
            if (!environmentPool.isEmpty()) {
                return environmentPool.pop();
            }
        }
        return null;
    }

    public static void returnToPool(Environment env) {
        int size = env.size();
        if (size >= 1 && size <= 3) {
            environmentPools[size - 1].push(env);
        }
    }
}
