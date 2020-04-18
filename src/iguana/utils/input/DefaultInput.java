package iguana.utils.input;

import java.net.URI;

public class DefaultInput extends AbstractInput {

    private final String s;

    DefaultInput(String s, int lineCount, URI uri) {
        super(lineCount, uri);
        this.s = s;
    }

    @Override
    public int charAt(int index) {
        if (index == s.length()) return EOF;
        return s.charAt(index);
    }

    @Override
    public int length() {
        return s.length() + 1;
    }

    @Override
    public String subString(int start, int end) {
        return s.substring(start, end);
    }

}
