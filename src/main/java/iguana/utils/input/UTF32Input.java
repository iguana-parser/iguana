package iguana.utils.input;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class UTF32Input extends AbstractInput {

    private static final int EOF = -1;

    private final int[] characters;

    private final int length;

    UTF32Input(int[] characters, int length, int lineCount, URI uri) {
        super(lineCount, uri);
        this.characters = characters;
        this.length = length;
    }

    @Override
    public int charAt(int index) {
        return characters[index];
    }

    /**
     * The length is one more than the actual characters in the input as the last input character is considered EOF.
     */
    @Override
    public int length() {
        return length;
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

    @Override
    int[] calculateLineLengths(int lineCount) {
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

}
