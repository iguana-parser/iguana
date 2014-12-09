package org.jgll.sppf;

import java.util.Collections;
import java.util.List;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * @author Ali Afroozeh
 * 
 * TODO: try to replace it with null, doesn't much make sense
 *
 */
public class DummyNode implements SPPFNode {
	
	private static DummyNode instance;
	
	public static DummyNode getInstance() {
		if(instance == null) {
			instance = new DummyNode();
		}
		return instance;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof DummyNode;
	}
	
	@Override
	public int hashCode() {
		return 16769023;
	}
	
	@Override
	public int getLeftExtent() {
		return -1;
	}

	@Override
	public int getRightExtent() {
		return -1;
	}
	
	@Override
	public SPPFNode getChildAt(int index) {
		return null;
	}

	@Override
	public int childrenCount() {
		return 0;
	}

	@Override
	public List<SPPFNode> getChildren() {
		return Collections.emptyList();
	}
	
	@Override
	public String toString() {
		return "$";
	}

	@Override
	public boolean isAmbiguous() {
		return false;
	}

	@Override
	public void accept(SPPFVisitor visitAction) {
		// do nothing
	}

	@Override
	public GrammarSlot getGrammarSlot() {
		// TODO Auto-generated method stub
		return null;
	}

}
