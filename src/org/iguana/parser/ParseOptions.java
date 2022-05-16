package org.iguana.parser;

import java.util.Map;

import static java.util.Collections.emptyMap;

public class ParseOptions {

    private final boolean ignoreLayout;
    private final Map<String, Object> map;
    private final boolean global;

    private ParseOptions(Builder builder) {
        this.ignoreLayout = builder.ignoreLayout;
        this.map = builder.map;
        this.global= builder.global;
    }

    public boolean ignoreLayout() {
        return ignoreLayout;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public boolean isGlobal() {
        return global;
    }

    public static class Builder {
        boolean ambiguous = false;
        boolean ignoreLayout = true;
        Map<String, Object> map = emptyMap();
        boolean global = true;

        public Builder setAmbiguous(boolean ambiguous) {
            this.ambiguous = ambiguous;
            return this;
        }

        public Builder setIgnoreLayout(boolean ignoreLayout) {
            this.ignoreLayout = ignoreLayout;
            return this;
        }

        public Builder setMap(Map<String, Object> map) {
            this.map = map;
            return this;
        }

        public Builder setGlobal(boolean global) {
            this.global = global;
            return this;
        }

        public ParseOptions build() {
            return new ParseOptions(this);
        }
    }
}
