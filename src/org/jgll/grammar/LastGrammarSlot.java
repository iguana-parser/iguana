package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import org.jgll.parser.ParserInterpreter;


/**
 * Corresponds to the last grammar slot in an alternate, e.g., X ::= alpha .
 * 
 * @author Ali Afroozeh
 *
 */
public class LastGrammarSlot extends BodyGrammarSlot {

	/**
	 * 
	 */
	private final Nonterminal head;
	
	public LastGrammarSlot(int id, String label, int position, Nonterminal head, BodyGrammarSlot previous) {
		super(id, label, position, previous);
		this.head = head;
	}
	
	public Nonterminal getHead() {
		return head;
	}
	
	@Override
	public Object execute(ParserInterpreter parser) {
		parser.pop();
		L0.getInstance().execute(parser);
		return null;
	}

	@Override
	public void code(Writer writer) throws IOException {
		writer.append("   pop(cu, ci, cn);\n");
		writer.append("   label = L0;\n}\n");
	}

	@Override
	public Set<Terminal> getTestSet() {
		return null;
	}

}
