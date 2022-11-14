package org.iguana.parser.options;

public class ParseOptions extends RecognizerOptions {

    private final boolean errorRecoveryEnabled;

    private ParseOptions(Builder builder) {
        super(builder);
        this.errorRecoveryEnabled = builder.errorRecoveryEnabled;
    }

    public boolean isErrorRecoveryEnabled() {
        return errorRecoveryEnabled;
    }

    public static ParseOptions defaultOptions() {
        return new Builder().build();
    }

    public static class Builder extends RecognizerOptions.Builder {
        private boolean errorRecoveryEnabled = false;

        public Builder setErrorRecoveryEnabled(boolean errorRecoveryEnabled) {
            this.errorRecoveryEnabled = errorRecoveryEnabled;
            return this;
        }

        public ParseOptions build() {
            return new ParseOptions(this);
        }
    }
}
