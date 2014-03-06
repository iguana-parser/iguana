package org.jgll.grammar.slot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Collections;
import java.util.HashMap;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import org.jgll.grammar.symbol.Alternate;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Range;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.regex.RegularExpression;

public class HeadGrammarSlotTreeMapFirstFollow extends HeadGrammarSlot {

	private static final long serialVersionUID = 1L;
	
	private NavigableMap<Integer, Set<BodyGrammarSlot>> predictionMap;

	public HeadGrammarSlotTreeMapFirstFollow(Nonterminal nonterminal) {
		super(nonterminal);
		predictionMap = new TreeMap<>();
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		int ci = parser.getCurrentInputIndex();
		
		Entry<Integer, Set<BodyGrammarSlot>> e = predictionMap.floorEntry(lexer.getInput().charAt(ci));
		if(e != null) {
			Set<BodyGrammarSlot> set = e.getValue();

			if(set != null) {
				for(BodyGrammarSlot slot : set) {
					parser.addDescriptor(slot);
				}
			}			
		}
		
		return null;
	}
	
	@Override
	public boolean test(int v) {
		Entry<Integer, Set<BodyGrammarSlot>> e = predictionMap.floorEntry(v);
		return e != null && e.getValue() != null;
	}
	
	@Override
	public void setPredictionSet() {
		
		predictionMap = new TreeMap<>();
		
		Map<Range, Set<BodyGrammarSlot>> map = new HashMap<>();
		
		for(Alternate alt : alternates) {
			for(RegularExpression regex : alt.getPredictionSet()) {
				for(Range r : regex.getFirstSet()) {
					Set<BodyGrammarSlot> set = map.get(r);
					if(set == null) {
						set = new HashSet<>();
						map.put(r, set);
					}
					set.add(alt.getFirstSlot());
				}
			}
		}
		
		Map<Integer, Range> starts = new HashMap<>();
		Map<Integer, Range> ends = new HashMap<>();
		
		for(Range r : map.keySet()) {
			starts.put(r.getStart(), r);
			ends.put(r.getEnd(), r);
		}
		
		Set<Integer> points = new HashSet<>(starts.keySet());
		points.addAll(ends.keySet());
		List<Integer> sortedPoints = new ArrayList<>(points);
		Collections.sort(sortedPoints);
		
		
		for(int i = 0; i < sortedPoints.size(); i++) {
			int val = sortedPoints.get(i);
			if(starts.containsKey(val)) {
				predictionMap.put(val, map.get(starts.get(val)));				
			} else {
				if(i < sortedPoints.size() - 1) {
					predictionMap.put(i + 1, map.get(ends.get(val)));
				} else {
					predictionMap.put(i + 1, new HashSet<BodyGrammarSlot>());
				}
			}
		}
	}

}
