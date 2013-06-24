package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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

	private List<Alternate> alternates;
	
	private final Nonterminal nonterminal;
	
	private final Set<Terminal> firstSet;
	
	private final Set<Terminal> followSet;
	
	private final Set<Integer> alternatesSet;
	
	public HeadGrammarSlot(Nonterminal nonterminal) {
		super(nonterminal.getName());
		this.nonterminal = nonterminal;
		this.alternates = new ArrayList<>();
		this.firstSet = new HashSet<>();
		this.followSet = new HashSet<>();
		this.alternatesSet = new HashSet<>(alternates.size());
		
		for(int i = 0; i < alternates.size(); i++) {
			alternatesSet.add(i);
		}
	}
	
	public void addAlternate(Alternate alternate) {		
		alternates.add(alternate);
		if(alternate != null) {
			alternatesSet.add(alternates.size() - 1);
		}
	}
	
	public void setAlternates(List<Alternate> alternates) {
		this.alternates = alternates;
	}
	
	public void removeAlternate(Alternate alternate) {
		alternates.remove(alternate);
	}
	
	public Set<Integer> getAlternateIndices() {
		Set<Integer> set = new HashSet<>();
		for(Alternate alternate : alternates) {
			set.add(alternate.getIndex());
		}
		return set;
	}
	
	public Set<Alternate> without(List<Symbol> list) {
		Set<Alternate> set = new HashSet<>(alternates);
		for(Alternate alternate : alternates) {
			if(alternate.match(list)) {
				set.remove(alternate);
				return set;
			}
		}
		return set;
	}
	
	public void remove(List<Symbol> list) {
		Iterator<Alternate> it = alternates.iterator();
		while(it.hasNext()) {
			Alternate alternate = it.next();
			if(alternate.match(list)) {
				it.remove();
			}
		}
	}
	
	public boolean isNullable() {
		return firstSet.contains(Epsilon.getInstance());
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, Input input) {
		for(Alternate alternate : alternates) {
			GSSNode cu = parser.getCu();
			int ci = parser.getCi();
			parser.add(alternate.getFirstSlot(), cu, ci, DummyNode.getInstance());
		}
		return null;
	}
	
	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, Input input) {
		return null;
	}
	
	@Override
	public void codeParser(Writer writer) throws IOException {
		writer.append("// " + nonterminal.getName() + "\n");
		writer.append("private void parse_" + id + "() {\n");
		for (Alternate alternate : alternates) {
			writer.append("   //" + alternate.getFirstSlot() + "\n");
			alternate.getFirstSlot().codeIfTestSetCheck(writer);			
			writer.append("   add(grammar.getGrammarSlot(" + alternate.getFirstSlot().id + "), cu, ci, DummyNode.getInstance());\n");
			writer.append("}\n");
		}
		writer.append("   label = L0;\n");
		writer.append("}\n");

		for (Alternate alternate : alternates) {
			writer.append("// " + alternate + "\n");
			writer.append("private void parse_" + alternate.getFirstSlot().getId() + "() {\n");
			alternate.getFirstSlot().codeParser(writer);
		}
	}
	
	public Alternate getAlternateAt(int index) {
		return alternates.get(index);
	}
	
	public List<Alternate> getAlternatesIncludingNull() {
		return alternates;
	}

	public List<Alternate> getAlternates() {
		List<Alternate> newList = new ArrayList<>();
		for(Alternate alternate : alternates) {
			if(alternate != null) {
				newList.add(alternate);
			}else {
				System.out.println("WTF?");
			}
		}
		return newList;
	}
	
	public Nonterminal getNonterminal() {
		return nonterminal;
	}
		
	public Set<Terminal> getFirstSet() {
		return firstSet;
	}
	
	public Set<Terminal> getFollowSet() {
		return followSet;
	}
	
	public int getCountAlternates() {
		return alternates.size();
	}
	
	public boolean contains(List<Symbol> list) {
		for(Alternate alternate : alternates) {
			if(alternate.match(list)) {
				return true;
			}
		}
		return false;
	}

	public boolean contains(Set<Integer> set) {
		return alternatesSet.containsAll(set);
	}
	
	@Override
	public String toString() {
		return nonterminal.toString();
	}

}
