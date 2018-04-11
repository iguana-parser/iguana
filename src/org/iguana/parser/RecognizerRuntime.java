package org.iguana.parser;

import org.iguana.result.RecognizerResult;
import org.iguana.result.RecognizerResultOps;
import org.iguana.util.Configuration;

public class RecognizerRuntime extends AbstractRuntime<RecognizerResult> {

    public RecognizerRuntime(Configuration config) {
        super(config, new RecognizerResultOps());
    }
}
