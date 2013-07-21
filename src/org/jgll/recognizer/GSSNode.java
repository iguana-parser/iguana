package org.jgll.recognizer;

import java.util.Iterator;

import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.L0;
import org.jgll.parser.HashFunctions;
import org.jgll.util.hashing.CuckooHashSet;
import org.jgll.util.hashing.HashFunction;
import org.jgll.util.hashing.HashKey;
import org.jgll.util.hashing.IntegerKey;

public class GSSNode  implements HashKey {

	public static final GSSNode U0 = new GSSNode(L0.getInstance(), 0);

	private final GrammarSlot slot;

	private final int inputIndex;

	private final CuckooHashSet<GSSNode> children;
	
	private final CuckooHashSet<IntegerKey> poppedIndices;
	
	/**
	 * Creates a new {@code GSSNode} with the given {@code label},
	 * {@code position} and {@code index}
	 * 
	 * @param slot
	 * @param position
	 * @param inputIndex
	 */
	public GSSNode(GrammarSlot slot, int inputIndex) {
		this.slot = slot;
		this.inputIndex = inputIndex;
		this.children = new CuckooHashSet<>();
		this.poppedIndices = new CuckooHashSet<>();
	}
	
	public boolean hasChild(GSSNode child) {
		return children.contains(child);
	}
	
	public void addChild(GSSNode edge) {
		children.add(edge);
	}
	
	public Iterable<GSSNode> getChildren() {
		return children;
	}
	
	public void addPoppedIndex(int i) {
		poppedIndices.add(IntegerKey.from(i));
	}
	
	public Iterable<Integer> getPoppedIndices() {
		return new Iterable<Integer>() {
			
			@Override
			public Iterator<Integer> iterator() {
				
				final Iterator<IntegerKey> it = poppedIndices.iterator();
				
				return new Iterator<Integer>() {

					@Override
					public boolean hasNext() {
						return it.hasNext();
					}

					@Override
					public Integer next() {
						return it.next().getInt();
					}

					@Override
					public void remove() {
						// TODO Auto-generated method stub
						
					}
				};
			}
		};
	}

	public final GrammarSlot getLabel() {
		return slot;
	}

	public final int getIndex() {
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

		return  other.slot.equals(slot) &&
				other.inputIndex == inputIndex;
	}

	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction().hash(inputIndex, slot.getId());
	}
	
	@Override
	public int hash(HashFunction f) {
		return f.hash(inputIndex, slot.getId());
	}


	@Override
	public String toString() {
		return slot + "," + inputIndex;
	}

}
