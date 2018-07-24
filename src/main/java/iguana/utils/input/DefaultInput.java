package iguana.utils.input;

import java.net.URI;

public class DefaultInput extends AbstractInput {

    private final String s;

    DefaultInput(String s, int lineCount, URI uri) {
        super(lineCount, uri);
        this.s = s + EOF;
    }

    @Override
    public int charAt(int index) {
        return s.charAt(index);
    }

    @Override
    public int length() {
        return s.length();
    }
    
    @Override
    public String subString(int start, int end) {
        return null;
    }

    @Override
    int[] calculateLineLengths(int lineCount) {
        return new int[0];
    }

}
