package org.jgll.grammar;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author Ali Afroozeh <afroozeh@gmail.com>
 * 
 */
public class Nonterminal implements Serializable {

	private static final long serialVersionUID = 1890711559136311884L;

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

	public void code(Writer writer) throws IOException {
		writer.append("// " + name + "\n");
		writer.append("private void parse_" + id + "() {\n");
		for (GrammarSlot slot : alternates) {
			writer.append("   //" + slot.getName() + "\n");
			writer.append("   add(" + slot.id + ", cu, ci, DUMMY);\n");
		}
		writer.append("   label = L0;\n");
		writer.append("}\n");

		for (GrammarSlot slot : alternates) {
			writer.append("// " + slot.getName() + "\n");
			writer.append("private void parse_" + slot.id + "() {\n");
			slot.code(writer);
		}
	}

	@Override
	public String toString() {
		return name;
	}
}
