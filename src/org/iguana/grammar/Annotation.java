package org.iguana.grammar;

public class Annotation {

    private final String name;
    private final String value;
    private final String symbolName;

    public Annotation(String name, String value, String symbolName) {
        this.name = name;
        this.value = value;
        this.symbolName = symbolName;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getSymbolName() {
        return symbolName;
    }
}
