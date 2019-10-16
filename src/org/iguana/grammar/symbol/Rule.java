package org.iguana.grammar.symbol;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * A rule is comprised of a list of priority levels, which are separated by > in the textual grammar notation.
 * In the following example, there are two priority groups, one consisting of the alternative A B | C D,
 * and second of the alternative E F | G H.
 *
 * S : A B
 *   | C D
 *   > E F
 *   | G H
 *   ;
 */
public class Rule implements Serializable {

    private static final long serialVersionUID = 1L;

    private Nonterminal head;

    private List<PriorityLevel> priorityLevels;

    public Rule() { }

    public Rule(Nonterminal head, List<PriorityLevel> priorityLevels) {
        this.head = head;
        this.priorityLevels = priorityLevels;
    }

    public Nonterminal getHead() {
        return head;
    }

    public List<PriorityLevel> getPriorityLevels() {
        return priorityLevels;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Rule)) return false;
        Rule other = (Rule) obj;
        return this.head.equals(other.head) && this.priorityLevels.equals(other.priorityLevels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(head, priorityLevels);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(head).append("\n  : ");
        if (priorityLevels.isEmpty()) {
            sb.append("\n");
        }
        for (PriorityLevel alternatives : priorityLevels) {
            sb.append(alternatives).append("  > ");
        }
        sb.delete(sb.length() - 4, sb.length());
        sb.append("  ;");
        return sb.toString();
    }

}
