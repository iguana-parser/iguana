package iguana.utils.input;

import java.net.URI;
import java.util.Arrays;

public abstract class AbstractInput implements Input {

    private int[] lineStarts;
    private final int lineCount;
    private final URI uri;

    public AbstractInput(int lineCount, URI uri) {
        this.lineCount = lineCount;
        this.uri = uri;
    }

    @Override
    public int getLineNumber(int inputIndex) {
        checkBounds(inputIndex, length());
        checkLineStartsInitialized();

        int lineStart = Arrays.binarySearch(lineStarts, inputIndex);
        if (lineStart >= 0) return lineStart + 1;
        return -lineStart - 1;
    }

    @Override
    public int getColumnNumber(int inputIndex) {
        checkBounds(inputIndex, length());
        checkLineStartsInitialized();

        int lineStart = Arrays.binarySearch(lineStarts, inputIndex);
        if (lineStart >= 0) return 1;
        return inputIndex - lineStarts[-lineStart - 1 - 1] + 1;
    }

    @Override
    public boolean isStartOfLine(int inputIndex) {
        checkBounds(inputIndex, length());
        checkLineStartsInitialized();

        return Arrays.binarySearch(lineStarts, inputIndex) >= 0;
    }

    @Override
    public boolean isEndOfLine(int inputIndex) {
        checkBounds(inputIndex, length());

        return nextSymbols(inputIndex).get(0) == '\n' || nextSymbols(inputIndex).get(0) == EOF;
    }

    @Override
    public boolean isEndOfFile(int inputIndex) {
        checkBounds(inputIndex, length());

        return nextSymbols(inputIndex).get(0) == EOF;
    }

    @Override
    public int getLineCount() {
        return this.lineCount;
    }

    @Override
    public URI getURI() {
        return uri;
    }

    private void checkLineStartsInitialized() {
        if (lineStarts == null) {
            lineStarts = calculateLineLengths(getLineCount());
        }
    }

    private static void checkBounds(int index, int length) {
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("index must be greater than or equal to 0 and smaller than the input length (" + length + ")");
        }
    }
}
