package org.jgll.parser.datadependent.preprocess;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Start;
import org.jgll.grammar.transformation.EBNFToBNF;
import org.jgll.grammar.transformation.LayoutWeaver;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalNode;
import org.jgll.traversal.NonterminalNodeVisitor;
import org.jgll.util.Configuration;
import org.jgll.util.Input;
import org.jgll.util.Tuple;
import org.junit.Test;

public class TestPreprocessor {

	private static Grammar grammar = new LayoutWeaver().transform(new EBNFToBNF().transform(Preprocessor.grammar));
	
	private static Nonterminal start = Start.from(Nonterminal.withName("A"));
	
	@Test
	public void test1() throws Exception {

		Input input = Input.fromPath(getClass().getResource("examples/Example1.pp").getPath());
		
		for (Tuple<Map<String, Object>, Set<String>> t : getTestResult()) {
			Map<String, Object> variables = t.getFirst();
			Set<String> expected = t.getSecond();
			
			System.out.println(variables);
			System.out.println(expected);
			
			GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
			ParseResult result = parser.parse(input, grammar, start, variables);

			Map<String, NonterminalNode> nodes = new HashMap<>();
			
			NonterminalNodeVisitor.create(n -> {
				if (n.getGrammarSlot().getNonterminal().getName().equals("Id")) {
					String yield = input.subString(n.getLeftExtent(), n.getRightExtent());
					nodes.put(yield, n);
				}
			}).visit(result.asParseSuccess().getRoot());
			
			assertEquals(expected, nodes.keySet());
		}		
	}

	private List<Tuple<Map<String, Object>, Set<String>>> getTestResult() {
		
		List<Tuple<Map<String, Object>, Set<String>>> result = new ArrayList<>();
	
		boolean v1, v2, v3, v4, v5;
		
		for (int i1 = 1; i1 <= 2; i1++) {
			v1 = i1 == 1;
			for (int i2 = 1; i2 <= 2; i2++) {
				v2 = i2 == 1;
				for (int i3 = 1; i3 <= 2; i3++) {
					v3 = i3 == 1;
					for (int i4 = 1; i4 <= 2; i4++) {
						v4 = i4 == 1;
						for (int i5 = 1; i5 <= 2; i5++) {
							v5 = i5 == 1;
							
							Map<String, Object> map = new HashMap<>();
							Set<String> set = new HashSet<>();

							map.put("v1", v1);
							map.put("v2", v2);
							map.put("v3", v3);
							map.put("v4", v4);
							map.put("v5", v5);
							
							
							if (v1) {
								set.add("v1");
								if (v2) {
									set.add("v2");
									if (v3) { 
										set.add("v3");
										if (v4) { 
											set.add("v4");
											if (v5) { 
												set.add("v5"); 
											} else { 
												set.add("nv5");
											}
										} else {
											set.add("nv4");
										}
									} else {
										set.add("nv3");
									}
								} else {
									set.add("nv2");
								}
							} else {
								set.add("nv1");
							}
							
							result.add(Tuple.of(map, set));
						}
					}									
				}
			}
		}		
		
		return result;
	}
	
}
