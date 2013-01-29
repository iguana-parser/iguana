package org.jgll.grammar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jgll.lexer.GLLLexer;

public class Nonterminal implements Serializable {

	private final List<GrammarSlot> alternates;
	private final int id;
	private final String name;
	private final boolean nullable;
	
	public Nonterminal(int id, String name, boolean nullable) {
		this.id = id;
		this.name = name;
		this.nullable = nullable;
		this.alternates = new ArrayList<>();
	}
	
	public void addAlternate(GrammarSlot slot) {
		alternates.add(slot);
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isNullable() {
		return nullable;
	}
	
	public void execute(int inputIndex, GLLLexer lexer) {
		for(GrammarSlot slot : alternates) {
		}
	}
	
	public String code() {
		String s = "";
		
		s += "// " + name + "\n"; 
		s += "private void parse_" + id + "() {\n";
		for(GrammarSlot slot : alternates) {
			s += "   //" + slot.getName() + "\n";
			s += "   add(" + slot.id + ", cu, ci, DUMMY);\n";
		}
		s += "   label = L0;\n";
		s += "}\n";
		
		for(GrammarSlot slot : alternates) {
			s += "// " + slot.getName() + "\n";
			s += "private void parse_" + slot.id + "() {\n";
			s += slot.code();
		}
		
		return s;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
