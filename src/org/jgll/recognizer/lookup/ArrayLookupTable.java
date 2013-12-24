package org.jgll.recognizer.lookup;

import java.util.ArrayDeque;
import java.util.Deque;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.recognizer.Descriptor;
import org.jgll.recognizer.GSSNode;
import org.jgll.util.Input;
import org.jgll.util.logging.LoggerWrapper;


public class ArrayLookupTable implements Lookup {

	private static final LoggerWrapper log = LoggerWrapper.getLogger(ArrayLookupTable.class);

	protected Deque<Descriptor> descriptorStack;
	
	/**
	 * Indexed on grammar slot and input positions of GSS nodes.
	 */
	protected GSSNode[][] gssNodes;

	public ArrayLookupTable(Grammar grammar, Input input) {
		descriptorStack = new ArrayDeque<>();
		gssNodes = new GSSNode[grammar.getGrammarSlots().size()][input.length()];
	}
	
	@Override
	public boolean hasDescriptor() {
		return false;
	}

	@Override
	public Descriptor nextDescriptor() {
		return null;
	}

	@Override
	public boolean addDescriptor(Descriptor descriptor) {
		return false;
	}

	@Override
	public boolean hasGSSEdge(GSSNode source, GSSNode destination) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GSSNode getGSSNode(GrammarSlot slot, int inputIndex) {
		
		GSSNode v = gssNodes[slot.getId()][inputIndex];

		if(v == null) {
			log.trace("GSSNode created: (%s, %d)",  slot, inputIndex);
			v = new GSSNode(slot, inputIndex);
			gssNodes[slot.getId()][inputIndex] = v;
		}
		
		return v;
	}

	@Override
	public int getGSSNodesCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getGSSEdgesCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDescriptorsCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Iterable<GSSNode> getGSSNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(Input input) {
		// TODO Auto-generated method stub
		
	}

}
