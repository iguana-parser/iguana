package org.iguana.parser.options;

public class ParseTreeOptions {
    /**
     * Ignore the layout nodes when converting the SPPF into the parse tree.
     */
    private final boolean ignoreLayout;

    /**
     * If set to false (default value) allow converting ambiguous SPPF to parse trees. Otherwise, throws
     * an exception while traversing the SPPF.
     */
    private final boolean allowAmbiguities;

    private ParseTreeOptions(Builder builder) {
        this.ignoreLayout = builder.ignoreLayout;
        this.allowAmbiguities = builder.allowAmbiguities;
    }

    public static ParseTreeOptions defaultOptions() {
        return new Builder().build();
    }

    public boolean ignoreLayout() {
        return ignoreLayout;
    }

    public boolean allowAmbiguities() {
        return allowAmbiguities;
    }

    public static class Builder {
        private boolean allowAmbiguities = false;
        private boolean ignoreLayout = true;

        public Builder setAllowAmbiguities(boolean allowAmbiguities) {
            this.allowAmbiguities = allowAmbiguities;
            return this;
        }

        public Builder setIgnoreLayout(boolean ignoreLayout) {
            this.ignoreLayout = ignoreLayout;
            return this;
        }

        public ParseTreeOptions build() {
            return new ParseTreeOptions(this);
        }
    }

}
