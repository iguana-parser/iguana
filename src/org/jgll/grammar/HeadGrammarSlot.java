package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.jgll.parser.GLLParser;
import org.jgll.parser.GSSNode;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.sppf.DummyNode;
import org.jgll.util.Input;


/**
 * 
 * The grammar slot corresponding to the head of a rule.
 * 
 * @author Ali Afroozeh
 * 
 */
public class HeadGrammarSlot extends GrammarSlot {
	
	private static final long serialVersionUID = 1L;

	private ArrayList<BodyGrammarSlot> alternates;
	
	private final Nonterminal nonterminal;
	
	private final Set<Terminal> firstSet;
	
	private final Set<Terminal> followSet;
	
	public HeadGrammarSlot(int id, Nonterminal nonterminal) {
		super(id, nonterminal.getName());
		this.nonterminal = nonterminal;
		this.alternates = new ArrayList<>();
		this.firstSet = new HashSet<>();
		this.followSet = new HashSet<>();
	}
	
	// TODO: what's this method good for? 
	public HeadGrammarSlot(int id, Nonterminal nonterminal, HeadGrammarSlot head, List<BodyGrammarSlot> alternateHeads) {
		super(id, nonterminal.getName());
		this.nonterminal = nonterminal;
		this.alternates = new ArrayList<>(head.alternates);

		for(BodyGrammarSlot slot : alternateHeads) {
			alternates.remove(slot);
		}
		
		this.firstSet = head.firstSet;
		this.followSet = head.followSet;
	}
		
	public void addAlternate(BodyGrammarSlot slot) {
		alternates.add(slot);
	}

	public boolean isNullable() {
		return firstSet.contains(Epsilon.getInstance());
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, Input input) {
		for(BodyGrammarSlot slot : getReverseAlternates()) {
			GSSNode cu = parser.getCu();
			int ci = parser.getCi();
			if(slot.checkAgainstTestSet(input.get(ci))) {
				parser.add(slot, cu, ci, DummyNode.getInstance());
			}
		}
		return null;
	}
	
	@Override
	public void recognize(GLLRecognizer recognizer, Input input) {
		
	}
	
	@Override
	public void codeParser(Writer writer) throws IOException {
		writer.append("// " + nonterminal.getName() + "\n");
		writer.append("private void parse_" + id + "() {\n");
		for (BodyGrammarSlot slot : getReverseAlternates()) {
			writer.append("   //" + slot + "\n");
			slot.codeIfTestSetCheck(writer);			
			writer.append("   add(grammar.getGrammarSlot(" + slot.id + "), cu, ci, DummyNode.getInstance());\n");
			writer.append("}\n");
		}
		writer.append("   label = L0;\n");
		writer.append("}\n");

		for (BodyGrammarSlot slot : getReverseAlternates()) {
			writer.append("// " + slot + "\n");
			writer.append("private void parse_" + slot.id + "() {\n");
			slot.codeParser(writer);
		}
	}

	public Iterable<BodyGrammarSlot> getAlternates() {
		return alternates;
	}
	
	public void replaceAlternate(BodyGrammarSlot oldSlot, BodyGrammarSlot newSlot) {
		ArrayList<BodyGrammarSlot> newAlternates = new ArrayList<>();
		for(BodyGrammarSlot alternate : alternates) {
			if(alternate == oldSlot) {
				newAlternates.add(newSlot);
			} else {
				newAlternates.add(alternate);
			}
		}
		alternates = newAlternates;
	}
	
	public Nonterminal getNonterminal() {
		return nonterminal;
	}
		
	public Iterable<BodyGrammarSlot> getReverseAlternates() {
		return new Iterable<BodyGrammarSlot>() {
			
			@Override
			public Iterator<BodyGrammarSlot> iterator() {
				return new Iterator<BodyGrammarSlot>() {

					ListIterator<BodyGrammarSlot> listIterator = alternates.listIterator(alternates.size());
					
					@Override
					public boolean hasNext() {
						return listIterator.hasPrevious();
					}

					@Override
					public BodyGrammarSlot next() {
						return listIterator.previous();
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}
		
	public Set<Terminal> getFirstSet() {
		return firstSet;
	}
	
	public Set<Terminal> getFollowSet() {
		return followSet;
	}

	@Override
	public String getSymbolName() {
		return nonterminal.getName();
	}
	
}
