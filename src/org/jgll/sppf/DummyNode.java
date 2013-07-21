package org.jgll.sppf;

import java.util.Collections;

import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.L0;
import org.jgll.traversal.SPPFVisitor;
import org.jgll.util.hashing.HashFunction;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class DummyNode extends SPPFNode {
	
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
	
	private DummyNode() {}

	@Override
	public String getLabel() {
		return "$";
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
	public Iterable<SPPFNode> getChildren() {
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
		return L0.getInstance();
	}

	@Override
	public int getLevel() {
		return 0;
	}

	@Override
	public int hash(HashFunction f) {
		return 0;
	}

}
