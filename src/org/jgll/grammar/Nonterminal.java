package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.jgll.parser.GrammarInterpreter;

/**
 * 
 * @author Ali Afroozeh
 * 
 */
public class Nonterminal extends GrammarSlot implements Symbol {
	
	private static final long serialVersionUID = 1L;

	private final ArrayList<BodyGrammarSlot> alternates;
	
	private final boolean nullable;
	
	private final String name;

	/**
	 * Whether this nonterminal is an converted ebnf node.
	 */
	private final boolean ebnfList;
	
	public Nonterminal(int id, String name) {
		this(id, name, false, false);
	}
	
	public Nonterminal(int id, String name, boolean nullable) {
		this(id, name, nullable, false);
	}
	
	public Nonterminal(int id, String name, boolean nullable, boolean ebnfList) {
		super(id);
		this.name = name;
		this.nullable = nullable;
		this.alternates = new ArrayList<>();
		this.ebnfList = ebnfList;
	}


	public void addAlternate(BodyGrammarSlot slot) {
		alternates.add(slot);
	}

	public boolean isNullable() {
		return nullable;
	}
	
	@Override
	public void execute(GrammarInterpreter parser) {
		for(BodyGrammarSlot slot : getReverseAlternates()) {
			if(slot.checkAgainstTestSet(parser.getCurrentInputValue())) {
				parser.add(slot);
			}
		}
	}

	@Override
	public void code(Writer writer) throws IOException {
		writer.append("// " + name + "\n");
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
			slot.code(writer);
		}
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public Iterable<BodyGrammarSlot> getAlternates() {
		return alternates;
	}
	
	public boolean isEbnfList() {
		return ebnfList;
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
	
	
	/**
	 * Returns a shallow copy of the alternates. This method is useful when
	 * one defines filters based on the existing alternates.
	 */
	@SuppressWarnings("unchecked")
	public List<BodyGrammarSlot> copyAlternates() {
		return (List<BodyGrammarSlot>) alternates.clone();
	}

}
