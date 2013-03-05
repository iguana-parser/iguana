package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;

import org.jgll.parser.GrammarInterpreter;


/**
 * Corresponds to the last grammar slot in an alternate, e.g., X ::= alpha .
 * 
 * @author Ali Afroozeh
 *
 */
public class LastGrammarSlot extends BodyGrammarSlot {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * An arbitrary data object that can be put in this grammar slot and
	 * retrieved later when traversing the parse tree.
	 * This object will appear as a property of nonterminal symbol nodes in a parse tree.
	 */
	private Object object;
	
	private Set<List<Terminal>> followRestrictions;

	private HeadGrammarSlot head;
	
	public LastGrammarSlot(int id, int position, BodyGrammarSlot previous, HeadGrammarSlot head, Object object) {
		super(id, position, previous);
		this.head = head;
		this.object = object;
	}
		
	@Override
	public void execute(GrammarInterpreter parser) {
		for(List<Terminal> l : followRestrictions) {
			if(parser.test(l)) {
				return;			
			}
		}
		parser.pop();
	}

	@Override
	public void code(Writer writer) throws IOException {
		writer.append("   pop(cu, ci, cn);\n");
		writer.append("   label = L0;\n}\n");
	}

	@Override
	public boolean checkAgainstTestSet(int i) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void codeIfTestSetCheck(Writer writer) throws IOException {
		throw new UnsupportedOperationException();
	}
	
	public Object getObject() {
		return object;
	}
	
	public void setObject(Object object) {
		this.object = object;
	}

	@Override
	public Iterable<Terminal> getTestSet() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String getName() {
		return "";
	}
	
	public HeadGrammarSlot getHead() {
		return head;
	}

}
