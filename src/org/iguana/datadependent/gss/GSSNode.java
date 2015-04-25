package org.iguana.datadependent.gss;

import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.parser.HashFunctions;
import org.iguana.parser.gss.GSSNodeData;

public class GSSNode<T> extends org.iguana.parser.gss.GSSNode {
	
	private final GSSNodeData<T> data;

	public GSSNode(GrammarSlot slot, int inputIndex, GSSNodeData<T> data) {
		super(slot, inputIndex);
		this.data = data;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		
		if (!(other instanceof GSSNode<?>)) return false;
		
		GSSNode<?> that = (GSSNode<?>) other;
		
		return getGrammarSlot() == that.getGrammarSlot() 
				&& getInputIndex() == that.getInputIndex()
				&& data.equals(that.data);
	}
	
	public GSSNodeData<T> getData() {
		return data;
	}
	
	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction.hash(getGrammarSlot().getId(), getInputIndex(), data.hashCode());
	}
	
	@Override
	public String toString() {
		return super.toString() + String.format("(%s)", data);
	}

}
