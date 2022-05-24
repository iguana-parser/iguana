package org.iguana.regex.utils;

import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.automaton.AutomatonVisitor;
import org.iguana.regex.automaton.State;
import org.iguana.utils.visualization.DotGraph;
import org.iguana.regex.automaton.Transition;

import java.util.IdentityHashMap;
import java.util.Map;

import static org.iguana.utils.visualization.DotGraph.newEdge;
import static org.iguana.utils.visualization.DotGraph.newNode;

public class AutomatonToDot {

    private static Map<State, Integer> idsMap = new IdentityHashMap<>();

    public static DotGraph toDot(Automaton automaton) {
        idsMap.clear();
        DotGraph dotGraph = new DotGraph(DotGraph.Direction.LEFT_TO_RIGHT);
        AutomatonVisitor.visit(automaton, state -> {
            DotGraph.Node node = newNode(getId(state));
            if (state.isFinalState()) {
                node.setShape(DotGraph.Shape.DOUBLE_CIRCLE);
            } else {
                node.setShape(DotGraph.Shape.CIRCLE);
            }

            node.setLabel(state.getRegularExpressions().isEmpty() ? "" : state.getRegularExpressions().toString());

            dotGraph.addNode(node);

            for (Transition transition : state.getTransitions()) {
                String label;
                if (transition.isEpsilonTransition())
                    label = "&#949;";
                else
                    label = transition.getRange().toString();
                dotGraph.addEdge(newEdge(getId(state), getId(transition.getDestination()), label));
            }
        });
        return dotGraph;
    }

    private static Integer getId(State state) {
        return idsMap.computeIfAbsent(state, k -> idsMap.size() + 1);
    }
}
