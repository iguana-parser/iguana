package iguana.regex.matcher;

import iguana.utils.input.GraphInput;
import iguana.utils.input.Input;

import java.util.ArrayList;
import java.util.List;

public class GraphMatcher implements Matcher {
    private String label;

    public GraphMatcher(String label) {
        this.label = label;
    }

    @Override
    public List<Integer> match(Input input, int vertexIndex) {
        if (input instanceof GraphInput) {
            GraphInput graphInput = (GraphInput) input;
            return graphInput.getDestVertex(vertexIndex, label);
        }
        return new ArrayList<>();
    }

    @Override
    public boolean match(Input input, int startIndex, int endIndex) {
        if (input instanceof GraphInput) {
            GraphInput graphInput = (GraphInput) input;
            return graphInput.getDestVertex(startIndex, label).contains(endIndex);
        }
        return false;
    }
}
