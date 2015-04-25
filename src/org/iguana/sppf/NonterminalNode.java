package org.iguana.sppf;

import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.traversal.SPPFVisitor;
import org.iguana.util.SPPFToJavaCode;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class NonterminalNode extends NonterminalOrIntermediateNode {
	
	public NonterminalNode(GrammarSlot slot, int leftExtent, int rightExtent, PackedNodeSet set) {
		super(slot, leftExtent, rightExtent, set);
	}

	@Override
	public void accept(SPPFVisitor visitAction) {
		visitAction.visit(this);
	}
	
	@Override
	public NonterminalGrammarSlot getGrammarSlot() {
		return (NonterminalGrammarSlot) slot;
	}

	public String toJavaCode() {
		return SPPFToJavaCode.toJavaCode(this);
	}
	
	public boolean isListNode() {
		return getGrammarSlot().getNonterminal().isEbnfList();
	}
	
}
