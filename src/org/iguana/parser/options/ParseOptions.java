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
        public boolean errorRecoveryEnabled = false;

        public void setErrorRecoveryEnabled(boolean errorRecoveryEnabled) {
            this.errorRecoveryEnabled = errorRecoveryEnabled;
        }

        public ParseOptions build() {
            return new ParseOptions(this);
        }
    }
}
