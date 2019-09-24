package org.iguana.grammar.symbol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighLevelRule implements Serializable {

    private static final long serialVersionUID = 1L;

    final List<Alternatives> alternativesList;
    final String head;
    final List<String> parameters;

    public HighLevelRule(Builder builder) {
        this.head = builder.head;
        this.parameters = builder.parameters;
        this.alternativesList = builder.alternativesList;
    }

    public static class Builder {
        List<Alternatives> alternativesList;
        String head;
        List<String> parameters;

        public Builder() {}

        public Builder(String head, List<String> parameters) {
            this.head = head;
            this.parameters = parameters;
        }

        public Builder(String head, List<String> parameters, List<Alternatives> alternativesList) {
            this.head = head;
            this.parameters = parameters;
            this.alternativesList = alternativesList;
        }

        public Builder addAlternative(Alternatives alternatives) {
            if (alternativesList == null) {
                alternativesList = new ArrayList<>();
            }
            this.alternativesList.add(alternatives);
            return this;
        }

        public HighLevelRule build() {
            if (alternativesList == null) alternativesList = Collections.emptyList();
            if (parameters == null) parameters = Collections.emptyList();
            return new HighLevelRule(this);
        }
    }
}
