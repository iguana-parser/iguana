package org.iguana.regex;

import org.iguana.regex.visitor.GatherReferencesVisitor;
import org.iguana.regex.visitor.InlineReferencesVisitor;
import org.iguana.regex.visitor.RegularExpressionVisitor;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class InlineReferences {

    public static Map<String, RegularExpression> inline(Map<String, RegularExpression> definitions) {
        Map<String, List<String>> referencesMap = new HashMap<>();
        for (Map.Entry<String, RegularExpression> entry : definitions.entrySet()) {
            org.iguana.regex.visitor.GatherReferencesVisitor gatherReferencesVisitor = new GatherReferencesVisitor();
            entry.getValue().accept(gatherReferencesVisitor);
            referencesMap.put(entry.getKey(), gatherReferencesVisitor.getReferences());
        }

        for (List<String> references : referencesMap.values()) {
            for (String reference : references) {
                if (!referencesMap.containsKey(reference)) {
                    throw new RuntimeException(reference + " refers to an name which is not a defined regular expression");
                }
            }
        }

        isCyclic(referencesMap);

        Map<String, RegularExpression> current = definitions;

        while (true) {
            RegularExpressionVisitor<RegularExpression> visitor = new InlineReferencesVisitor(current);

            Map<String, RegularExpression> newMap = new HashMap<>();
            for (Map.Entry<String, RegularExpression> entry : current.entrySet()) {
                newMap.put(entry.getKey(), entry.getValue().accept(visitor));
            }
            if (newMap.equals(current)) {
                break;
            }
            current = newMap;
        }

        return current;
    }

    private static void isCyclic(Map<String, List<String>> map) {
        for (String node : map.keySet()) {
            visit(node, map, new LinkedHashSet<>());
        }
    }

    private static void visit(String node, Map<String, List<String>> map, LinkedHashSet<String> visited) {
        if (visited.contains(node)) {
            throw new RuntimeException("Regular expression references cannot be cyclic: " +
                String.join("->", visited) + "->" + node);
        }

        visited.add(node);
        for (String neighbor : map.get(node)) {
            visit(neighbor, map, visited);
        }
        visited.remove(node);
    }
}
