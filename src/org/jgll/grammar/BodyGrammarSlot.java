package org.jgll.grammar;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public abstract class BodyGrammarSlot extends GrammarSlot implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected BodyGrammarSlot previous;
	
	protected BodyGrammarSlot next;
	
	/**
	 * The position from the beginning of the alternate.
	 * Positions start from zero.
	 * TODO: see if can be removed
	 */
	protected final int position;
	
	private String label;

	protected HeadGrammarSlot head;
	
	public BodyGrammarSlot(int id, int position, BodyGrammarSlot previous, HeadGrammarSlot head) {
		super(id);
		this.position = position;
		this.head = head;
		if(previous != null) {
			previous.next = this;
		}
		this.previous = previous;
	}
	
	/**
	 * Checks whether the provide input belongs to the first set, and follow set
	 * in case the first set contains epsilon.  
	 */
	public abstract boolean checkAgainstTestSet(int i);
	
	public abstract void codeIfTestSetCheck(Writer writer) throws IOException;
	
	public abstract Iterable<Terminal> getTestSet();
	
	public void codeElseTestSetCheck(Writer writer) throws IOException {
		writer.append("} else { newParseError(grammar.getGrammarSlot(" + this.id +  "), ci); label = L0; return; } \n");
	}
	
	public BodyGrammarSlot next() {
		return next;
	}
	
	public BodyGrammarSlot previous() {
		return previous;
	}
	
	public int getPosition() {
		return position;
	}
	
	public HeadGrammarSlot getHead() {
		return head;
	}
	
	public abstract boolean isTerminalSlot();
	
	public abstract boolean isNonterminalSlot();
	
	public abstract boolean isLastSlot();
	
	public abstract boolean isNullable();
	
	@Override
	// TODO: Change it! too complicated and not necessary. 
	// Compute labels beforehand and then insert them.
	public String toString() {
		if(label == null) {
			StringBuilder sb = new StringBuilder();
			
			// Before
			BodyGrammarSlot current = previous;
			while(current != null) {
				sb.insert(0, current.getName() + " ");
				current = current.previous;
			}
			
			
			// This slot
			sb.append(". ");
			sb.append(getName());
			
			// Next
			current = next;
			while(current != null) {
				sb.append(current.getName()).append(" ");
				current = current.next;
			}

			sb.insert(0, " ::= ");
			
			sb.delete(sb.length() - 1, sb.length());
			label = sb.toString();
		}
		
		return label;
	}
	
}
