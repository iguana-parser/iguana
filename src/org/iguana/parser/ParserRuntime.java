package org.iguana.parser;

import org.iguana.result.ParserResultOps;
import org.iguana.sppf.NonPackedNode;
import org.iguana.util.Configuration;

public class ParserRuntime extends AbstractRuntime<NonPackedNode> {

    public ParserRuntime(Configuration config) {
        super(config, new ParserResultOps());
    }
}
