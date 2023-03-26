package org.iguana.parser.options;

import java.util.Map;

import static java.util.Collections.emptyMap;

public class RecognizerOptions {
    /**
     * The user-provided key-value map to set the value of the top-level variables in the environment
     */
    private final Map<String, Object> map;

    /**
     * If variables are global. TODO: figure it out if this is still useful.
     */
    private final boolean global;

    protected RecognizerOptions(Builder builder) {
        this.map = builder.map;
        this.global = builder.global;
    }

    public static RecognizerOptions defaultOptions() {
        return new Builder().build();
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public boolean isGlobal() {
        return global;
    }

    public static class Builder {
        private Map<String, Object> map = emptyMap();
        private boolean global = false;

        public Builder setMap(Map<String, Object> map) {
            this.map = map;
            return this;
        }

        public Builder setGlobal(boolean global) {
            this.global = global;
            return this;
        }

        public RecognizerOptions build() {
            return new RecognizerOptions(this);
        }
    }
}
