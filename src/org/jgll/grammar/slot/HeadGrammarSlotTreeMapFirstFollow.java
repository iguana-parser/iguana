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
		
		Set<Integer> starts = new HashSet<>();
		Set<Integer> ends = new HashSet<>();
		
		for(Range r : map.keySet()) {
			starts.add(r.getStart());
			ends.add(r.getEnd());
		}
		
		Set<Integer> points = new HashSet<>(starts);
		points.addAll(ends);
		List<Integer> sortedPoints = new ArrayList<>(points);
		Collections.sort(sortedPoints);
		
		Set<Integer> set = new HashSet<>();
		for(int i : sortedPoints) {
			if(starts.contains(i)) {
				set.add(i);
			} 
			if(ends.contains(i)) {
				set.add(i + 1);
			}
		}
		List<Integer> list = new ArrayList<>(set);
		Collections.sort(list);
		
		for(Range range : map.keySet()) {
			for(int i : list) {
				if(i >= range.getStart() && i <= range.getEnd()) {
					Set<BodyGrammarSlot> s = predictionMap.get(i);
					if(s == null) {
						s = new HashSet<>();
						predictionMap.put(i, s);
					}
					s.addAll(map.get(range));
				}
			}
		}
		
		predictionMap.put(list.get(list.size() - 1), new HashSet<BodyGrammarSlot>());
	}

}
