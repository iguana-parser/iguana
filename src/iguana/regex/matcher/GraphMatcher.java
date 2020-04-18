package iguana.regex.matcher;

import iguana.utils.input.GraphInput;
import iguana.utils.input.Input;

public class GraphMatcher implements Matcher {
    private String label;

    public GraphMatcher(String label) {
        this.label = label;
    }

    @Override
    public int match(Input input, int vertexIndex) {
        if (input instanceof GraphInput) {
            GraphInput graphInput = (GraphInput) input;
            return graphInput.getDestVertex(vertexIndex, label);
        } else {
            return -1;
        }
    }

    @Override
    public boolean match(Input input, int startIndex, int endIndex) {
        if (input instanceof GraphInput) {
            GraphInput graphInput = (GraphInput) input;
            return graphInput.getDestVertex(startIndex, label) == endIndex;
        } else {
            return false;
        }
    }
}
