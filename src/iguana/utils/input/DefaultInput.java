package iguana.utils.input;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class DefaultInput extends AbstractInput {

    private final String s;

    DefaultInput(String s, int lineCount, URI uri) {
        super(lineCount, uri);
        this.s = s;
    }

    @Override
    public Stream<Integer> nextSymbols(int index) {
        if (index == s.length()) return Stream.of(EOF);
        return Stream.of((int) s.charAt(index));
    }

    @Override
    public int length() {
        return s.length() + 1;
    }

    @Override
    public String subString(int start, int end) {
        return s.substring(start, end);
    }

    @Override
    public Stream<Integer> getStartVertices() {
        return Collections.singletonList(0).stream();
    }

    @Override
    public Stream<Integer> getFinalVertices() {
        return Collections.singletonList(length() - 1).stream();
    }

    @Override
    public boolean isFinal(int index) {
        return index == s.length();
    }
}
