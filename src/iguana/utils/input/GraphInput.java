package iguana.utils.input;

import java.net.URI;
import java.util.List;

public abstract class GraphInput implements Input {

    public abstract List<Integer> getDestVertex(int v, String t);

    public abstract boolean isFinal(int v);

    @Override
    public int length() {
        return 0;
    }

    @Override
    public int getLineNumber(int inputIndex) {
        return 0;
    }

    @Override
    public int getColumnNumber(int inputIndex) {
        return 0;
    }

    @Override
    public String subString(int start, int end) {
        return null;
    }

    @Override
    public int getLineCount() {
        return 0;
    }

    @Override
    public URI getURI() {
        return null;
    }

    @Override
    public boolean isStartOfLine(int inputIndex) {
        return false;
    }

    @Override
    public boolean isEndOfLine(int inputIndex) {
        return false;
    }

    @Override
    public boolean isEndOfFile(int inputIndex) {
        return false;
    }
}
