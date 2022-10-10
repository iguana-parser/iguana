// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen;

import org.iguana.grammar.Grammar;
import org.iguana.util.serialization.JsonSerializer;

import java.io.IOException;
import java.io.InputStream;

public class IggyGrammar {

    private static final String grammarName = "iggy";

    private static Grammar grammar;

    public static Grammar getGrammar() {
        if (grammar == null) {
            grammar = loadGrammar();
        }
        return grammar;
     }

    private static Grammar loadGrammar() {
        String grammarJsonFile = grammarName + ".json";
        try (InputStream in = IggyParser.class.getResourceAsStream("/" + grammarJsonFile)) {
            if (in == null) throw new RuntimeException("Grammar json file " + grammarJsonFile + " is not found.");
            return JsonSerializer.deserialize(in, Grammar.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
