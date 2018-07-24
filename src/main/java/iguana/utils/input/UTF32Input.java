package iguana.utils.input;

import iguana.utils.collections.tuple.IntTuple;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class UTF32Input implements Input {

    private static final int EOF = -1;

    private final int[] characters;

    private int[] lineStarts;

    private final int lineCount;

    private final int length;

    private final URI uri;

    UTF32Input(int[] characters, int length, int lineCount, URI uri) {
        this.uri = uri;
        this.characters = characters;
        this.length = length;
        this.lineCount = lineCount;
    }

    private static int[] calculateLineLengths(int lineCount, int[] characters) {
        int[] lineStarts = new int[lineCount];
        lineStarts[0] = 0;
        int j = 0;

        for (int i = 0; i < characters.length; i++) {
            if (characters[i] == '\n') {
                if (i + 1 < characters.length)
                    lineStarts[++j] = i + 1;
            }
        }
        return lineStarts;
    }

    @Override
    public int charAt(int index) {
        return characters[index];
    }

    @Override
    public int charAtIgnoreLayout(int index) {
        while (true) {
            int c = characters[index++];
            if (c == EOF) return EOF;
            if (!Character.isWhitespace(c))
                return c;
        }
    }

    /**
     * The length is one more than the actual characters in the input as the last input character is considered EOF.
     */
    @Override
    public int length() {
        return length;
    }

    @Override
    public boolean match(int start, int end, String target) {
        return match(start, end, toIntArray(target));
    }

    public boolean match(int start, int end, int[] target) {
        if(target.length != end - start) {
            return false;
        }

        int i = 0;
        while(i < target.length) {
            if(target[i] != characters[start + i]) {
                return false;
            }
            i++;
        }

        return true;
    }

    @Override
    public boolean match(int from, String target) {
        return match(from, toIntArray(target));
    }

    @Override
    public boolean matchBackward(int start, String target) {
        return matchBackward(start, toIntArray(target));
    }

    public boolean matchBackward(int start, int[] target) {
        if(start - target.length < 0) {
            return false;
        }

        int i = target.length - 1;
        int j = start - 1;
        while(i >= 0) {
            if(target[i] != characters[j]) {
                return false;
            }
            i--;
            j--;
        }

        return true;
    }

    public boolean match(int from, int[] target) {
        if (target.length > length() - from) {
            return false;
        }

        int i = 0;
        while (i < target.length) {
            if(target[i] != characters[from + i]) {
                return false;
            }
            i++;
        }

        return true;
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

    private void checkLineStartsInitialized() {
        if (lineStarts == null) {
            lineStarts = calculateLineLengths(getLineCount(), characters);
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(characters);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (!(obj instanceof Input)) return false;

        UTF32Input other = (UTF32Input) obj;

        return this.length() == other.length() &&
                Arrays.equals(characters, other.characters);
    }

    /**
     * Returns a string representation of this input instance from the
     * given start (including) and end (excluding) indices.
     *
     */
    public String subString(int start, int end) {
        List<Character> charList = new ArrayList<>();

        for(int i = start; i < end; i++) {
            if (characters[i] == -1) continue;
            char[] chars = Character.toChars(characters[i]);
            for(char c : chars) {
                charList.add(c);
            }
        }

        StringBuilder sb = new StringBuilder();
        for(char c : charList) {
            sb.append(c);
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return subString(0, characters.length - 1);
    }

    public int getLineCount() {
        return this.lineCount;
    }

    public boolean isEmpty() {
        return length() == 1;
    }

    public URI getURI() {
        return uri;
    }

    public boolean isStartOfLine(int inputIndex) {
        checkBounds(inputIndex, length());
        checkLineStartsInitialized();

        return Arrays.binarySearch(lineStarts, inputIndex) >= 0;
    }

    public boolean isEndOfLine(int inputIndex) {
        checkBounds(inputIndex, length());

        return charAt(inputIndex) == '\n' || charAt(inputIndex) == EOF;
    }

    public boolean isEndOfFile(int inputIndex) {
        checkBounds(inputIndex, length());

        return characters[inputIndex] == -1;
    }

    private static void checkBounds(int index, int length) {
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("index must be greater than or equal to 0 and smaller than the input length (" + length + ")");
        }
    }

    private static int[] toIntArray(String s) {
        int[] array = new int[s.codePointCount(0, s.length())];
        for(int i = 0; i < array.length; i++) {
            array[i] = s.codePointAt(i);
        }
        return array;
    }

}
