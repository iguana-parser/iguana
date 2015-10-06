package org.iguana.util;

import java.net.URI;

public class FailureResult implements RunResult {

    String errorMessage;
    URI uri;

    public FailureResult(URI uri, String errorMessage) {
        this.uri = uri;
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public URI getInput() {
        return uri;
    }

    @Override
    public String toString() {
        return errorMessage;
    }
}
