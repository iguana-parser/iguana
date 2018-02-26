package org.iguana.iggy;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Start;
import org.iguana.util.JsonSerializer;

import java.io.File;
import java.io.IOException;

import static iguana.utils.io.FileUtils.readFile;

public class IggyParser {

    private static Start start = Start.from("Definition");

    private static Grammar iggyGrammar() {
        try {
            String content = readFile(IggyParser.class.getResourceAsStream("/iggy.json"));
            return JsonSerializer.deserialize(content, Grammar.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        iggyGrammar();
    }

}
