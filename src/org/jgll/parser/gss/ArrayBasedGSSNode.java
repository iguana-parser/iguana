package org.jgll.parser.gss;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.parser.HashFunctions;
import org.jgll.sppf.NonPackedNode;


public class ArrayBasedGSSNode implements GSSNode {

	private final HeadGrammarSlot head;

	private final int inputIndex;
	
	private List<GSSNode> children;
	
	private int countPoppedElements;
	
	private NonPackedNode[] poppedElements;

	private final int hash;
	
	/**
	 * Creates a new {@code GSSNode} with the given {@code label},
	 * {@code position} and {@code index}
	 * 
	 * @param slot
	 * @param inputIndex
	 */
	public ArrayBasedGSSNode(HeadGrammarSlot head, int inputIndex, int inputSize) {
		this.head = head;
		this.inputIndex = inputIndex;
		
		children = new ArrayList<>();
		
		// Each GSS nodes can be popped from the GSS node's input index to the
		// the length of input index.
		poppedElements = new NonPackedNode[inputSize - inputIndex];
		
		this.hash = GSSNode.externalHasher.hash(this, HashFunctions.defaulFunction());
	}
	
	@Override
	public void addToPoppedElements(NonPackedNode node) {
		if(poppedElements[node.getRightExtent()] == null) {
			countPoppedElements++;
		}
		poppedElements[node.getRightExtent() - inputIndex] = node;
	}
	
	@Override
	public Iterable<NonPackedNode> getPoppedElements() {
		return new Iterable<NonPackedNode>() {
			
			@Override
			public Iterator<NonPackedNode> iterator() {
				
				return new Iterator<NonPackedNode>() {
					
					int i = 0;

					@Override
					public boolean hasNext() {
						return i < countPoppedElements;
					}

					@Override
					public NonPackedNode next() {
						while(poppedElements[i++] == null) {}
						return poppedElements[i++];
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}
		
	@Override
	public Iterable<GSSNode> getChildren() {
		return children;
	}

	@Override
	public void addChild(GSSNode node) {
		children.add(node);
	}
	
	@Override
	public int sizeChildren() {
		return children.size();
	}
		
	@Override
	public GrammarSlot getGrammarSlot() {
		return head;
	}

	@Override
	public int getInputIndex() {
		return inputIndex;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}

		if (!(obj instanceof GSSNode)) {
			return false;
		}
		
		GSSNode other = (GSSNode) obj;

		return  head == other.getGrammarSlot() &&
				inputIndex == other.getInputIndex();
	}

	@Override
	public int hashCode() {
		return hash;
	}
	
	@Override
	public String toString() {
		return "(" + head + "," + inputIndex + ")";
	}
	
}
