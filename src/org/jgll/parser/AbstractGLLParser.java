package org.jgll.parser;


import org.jgll.grammar.Grammar;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.grammar.PopAction;
import org.jgll.grammar.TerminalGrammarSlot;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.lookup.LookupTable;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.logging.LoggerWrapper;

/**
 * GLLParser is the abstract base class for all the generated GLL parsers.
 * This class provides common methods for SPPF and GSS construction.
 * 
 * Based on the paper "GLL: parse tree generation."
 * 
 * @author Maarten Manders
 * @author Ali Afroozeh
 * 
 */
public abstract class AbstractGLLParser implements GLLParser {
		
	private static final LoggerWrapper log = LoggerWrapper.getLogger(AbstractGLLParser.class);
	
	/**
	 * u0 is the bottom of the GSS.
	 */
	protected static final GSSNode u0 = GSSNode.U0;

	protected LookupTable lookupTable;

	/**
	 * u0 is the bottom of the GSS.
	 */
	protected GSSNode cu = u0;
	
	protected SPPFNode cn = DummyNode.getInstance();
	
	protected int ci = 0;
	
	protected Input input;
	
	/**
	 * 
	 */
	protected Grammar grammar;
	
	/**
	 * The grammar slot at which a parse error is occurred. 
	 */
	protected GrammarSlot errorSlot;
	
	/**
	 * The last input index at which an error is occurred. 
	 */
	protected int errorIndex = -1;
	
	/**
	 * The current GSS node at which an error is occurred.
	 */
	protected GSSNode errorGSSNode;
	

	/**
	 * Parses the given input string. If the parsing of the input was successful,
	 * the root of SPPF is returned.
	 * 
	 * @param input the input string to be parsed.
	 * @return the SPPF root resulting from parsing the input
	 * 
	 * @throws ParseError a {@link ParseError} if the descriptor set is empty, but
	 * 								  no SPPF root has been found.
	 * @throws RuntimeException if no nonterminal with the given start symbol name is found.
	 */
	@Override
	public final NonterminalSymbolNode parse(Input input, Grammar grammar, String startSymbolName) throws ParseError {
		HeadGrammarSlot startSymbol = grammar.getNonterminalByName(startSymbolName);
		if(startSymbol == null) {
			throw new RuntimeException("No nonterminal named " + startSymbolName + " found");
		}

		this.input = input;
		this.grammar = grammar;
		
		init();
	
		long start = System.nanoTime();

		parse(startSymbol);
		
		long end = System.nanoTime();
		
		NonterminalSymbolNode root = lookupTable.getStartSymbol(startSymbol);
		if (root == null) {
			throw new ParseError(errorSlot, this.input, errorIndex, errorGSSNode);
		}
		
		logParseStatistics(end - start);
		return root;
	}
	
	/**
	 * Subclasses should provide implementations for this method. 
	 * 
	 */
	protected abstract void parse(HeadGrammarSlot startSymbol);
	
	private void logParseStatistics(long duration) {
		log.info("Parsing Time: " + duration/1000000 + " ms");
		
		int mb = 1024 * 1024;
		Runtime runtime = Runtime.getRuntime();
		log.info("Memory used: %d mb", (runtime.totalMemory() - runtime.freeMemory()) / mb);
		log.debug("Descriptors: %d", lookupTable.getDescriptorsCount());
		log.debug("Non-packed nodes: %d", lookupTable.getDescriptorsCount());
		log.debug("Packed nodes: %d", lookupTable.getPackedNodesCount());
		log.debug("GSS Nodes: %d", lookupTable.getGSSNodesCount());
		log.debug("GSS Edges: %d", lookupTable.getGSSEdgesCount());
	}

	
	/**
	 * Replaces the previously reported parse error with the new one if the
	 * inputIndex of the new parse error is greater than the previous one. In
	 * other words, we throw away an error if we find an error which happens at
	 * the next position of input.
	 * 
	 */
	@Override
	public void recordParseError(GrammarSlot slot) {
		if (errorIndex >= this.errorIndex) {
			this.errorIndex = ci;
			this.errorSlot = slot;
			this.errorGSSNode = cu;
		}
	}

	/**
	 * initialized the parser's state before a new parse.
	 */
	protected abstract void init();
	

	/**
	 * Corresponds to the add method from the paper:
	 * 
	 * add(L, u, i, w) { 
	 * 	if(L, u, w) in ui { 
	 * 		add (L, u, w) to ui
	 * 		 add (L, u, i, w) to R 
	 *   } 
	 * }
	 */
	private final void add(GrammarSlot label, GSSNode u, int inputIndex, SPPFNode w) {
		Descriptor descriptor = new Descriptor(label, u, inputIndex, w);
		boolean result = lookupTable.addDescriptor(descriptor);
		log.trace("Descriptor created: %s : %b", descriptor, result);
	}
	
	@Override
	public void addDescriptor(GrammarSlot label) {
		add(label, cu, ci, DummyNode.getInstance());
	}
	
	
	/**
	 * Pops the current element from GSS. When the top element, cu, is popped, there
	 * may be possibly n children. For each child u, reachable via an edge labelled
	 * by an SPPF node w, a new descriptor is added to the set of descriptors
	 * to be processed.
	 * 
	 * pop(u, i, z) { 
	 * 	if (u != u0) { 
	 * 		let (L, k) be the label of u 
	 * 		add (u, z) to P 
	 * 		for each edge (u, w, v) { 
	 * 			let y be the node returned by getNodeP(L, w, u)
	 * 			add(L, v, i , y)) 
	 *      } 
	 * }
	 */
	@Override
	public final void pop() {
		
		if (cu != u0) {

			log.trace("Pop %s, %d, %s", cu.getGrammarSlot(), ci, cn);
			
			// Add (u, z) to P
			lookupTable.addToPoppedElements(cu, cn);
			
			label:
			for(GSSEdge edge : cu.getEdges()) {
				
				// Don't pop if a pop action associated with the slot returns false.
				if(cu.getGrammarSlot() instanceof LastGrammarSlot) {
					for(PopAction popAction : ((LastGrammarSlot) cu.getGrammarSlot()).getPopActions()) {
						if(!popAction.execute(edge, ci, input)) {
							continue label;
						}
					}					
				}
				
				assert cu.getGrammarSlot() instanceof BodyGrammarSlot;
				
				BodyGrammarSlot slot = (BodyGrammarSlot) cu.getGrammarSlot();
				SPPFNode y = getNodeP(slot, edge.getSppfNode(), cn);
				add(cu.getGrammarSlot(), edge.getDestination(), ci, y);
			}			
		}
	}
	
	
	
	/**
	 * 
	 * create(L, u, w) {
     *	 let w be the value of cn
	 *	 if there is not already a GSS node labelled (L,A ::= alpha . beta, ci) create one
	 * 	 let v be the GSS node labelled (L,A ::= alpha . beta, ci)
	 *   if there is not an edge from v to cu labelled w {
	 * 		create an edge from v to cu labelled w
	 * 		for all ((v, z) in P) {
	 * 			let x be the node returned by getNodeP(A ::= alpha . beta, w, z)
	 * 			add(L, cu, h, x)) where h is the right extent of z
	 * 		}
	 * 	 }
	 * 	 return v
	 * }
	 * 
	 * @param slot the grammar label
	 * 
	 * @param nonterminalIndex the index of the nonterminal appearing as the head of the rule
	 *                         where this position refers to. 
	 * 
	 * @param alternateIndex the index of the alternate of the rule where this position refers to.
	 * 
	 * @param position the position in the body of the rule where this position refers to
	 *
	 * @return 
     *
	 */
	@Override
	public final void createGSSNode(GrammarSlot slot) {
		cu = create(slot, cu, ci, cn);
	}
	
	private final GSSNode create(GrammarSlot L, GSSNode u, int i, SPPFNode w) {
		log.trace("GSSNode created: (%s, %d)",  L, i);

		GSSNode v = lookupTable.getGSSNode(L, i);

		if(!lookupTable.hasGSSEdge(v, w, u)) {
			for (SPPFNode z : lookupTable.getSPPFNodesOfPoppedElements(v)) {
				SPPFNode x = getNodeP((BodyGrammarSlot) L, w, z);
				add(L, u, z.getRightExtent(), x);
			}
		}

		return v;
	}


	/** 
	 *  getNodeT(a, i) {
	 * 		if there is no SPPF node labelled (a, i, i + 1) create one
	 * 		return the SPPF node labelled (a, i, i + 1) 
	 *  }
	 */
	@Override
	public final TerminalSymbolNode getTerminalNode(int c) {
		return lookupTable.getTerminalNode(c, ci++);
	}
	
	@Override
	public TerminalSymbolNode getEpsilonNode() {
		return lookupTable.getTerminalNode(TerminalSymbolNode.EPSILON, ci);
	}
	
	@Override
	public SPPFNode getNodeP(BodyGrammarSlot slot, SPPFNode rightChild) {
		cn = getNodeP(slot, cn, rightChild);
		return cn;
	}
	
	 /**
	  * getNodeP(X ::= alpha . beta, z, w) {
	  * 	if (|alpha| = 1 and beta != empty) {
	  * 		return w
	  *		} else {
	  *			if (beta = empty) { 
	  *				t := X 
	  * 		} else {
	  * 			else t := (X ::= alpha . beta)
	  * 		}
	  * 		suppose that w has label (x, k, i)
	  * 		if (z != $) {
	  *				suppose that z has label (s, j, k)
	  *				if there does not exist an SPPF node y labelled (t, j, i) create one
	  * 			if y does not have a child labelled (X ::= alpha . beta, k)
	  *				create one with left child z and right child w
	  *			} else {
	  *		 		if there does not exist an SPPF node y labelled (t, k, i) create one
	  *				if y does not have a child labelled (X ::= alpha . beta, k)
	  *				create one with child w
	  * 		}
	  * 	return y
	  * 	}
	  * }
	  */
	public final SPPFNode getNodeP(BodyGrammarSlot slot, SPPFNode leftChild, SPPFNode rightChild) {
		
		// if (alpha is a terminal or a not nullable nonterminal and beta != empty)
		if (slot.getPosition() == 1 && 
			!(slot instanceof LastGrammarSlot) &&
			(slot.previous() instanceof TerminalGrammarSlot || !slot.previous().isNullable())) {
				return rightChild;
		} else {
			
			GrammarSlot t = slot;
			// if (beta = empty)
			if (slot instanceof LastGrammarSlot) {
				t = slot.getHead();
			}

			// if (z != $)
			int leftExtent;
			int rightExtent = rightChild.getRightExtent();
			
			if (!leftChild.equals(DummyNode.getInstance())) {
				leftExtent = leftChild.getLeftExtent();
			} else {
				leftExtent = rightChild.getLeftExtent();
			}
			
			NonPackedNode newNode = (NonPackedNode) lookupTable.getNonPackedNode(t, leftExtent, rightExtent);
			
			lookupTable.addPackedNode(newNode, slot, rightChild.getLeftExtent(), leftChild, rightChild);
			
			return newNode;
		}
	}
	
	@Override
	public boolean hasNextDescriptor() {
		return lookupTable.hasNextDescriptor();
	}
	
	@Override
	public Descriptor nextDescriptor() {
		Descriptor descriptor = lookupTable.nextDescriptor();
		ci = descriptor.getInputIndex();
		cu = descriptor.getGSSNode();
		cn = descriptor.getSPPFNode();
		log.trace("Processing (%s, %s, %s, %s)", descriptor.getGrammarSlot(), ci, cu, cn);
		return descriptor;
	}
	
	@Override
	public int getCurrentInputIndex() {
		return ci;
	}
	
	@Override
	public NonterminalSymbolNode getNonterminalNode(LastGrammarSlot slot, SPPFNode child) {
		SPPFNode key = lookupTable.getNonPackedNode(slot, child.getLeftExtent(), child.getRightExtent());
		return null;
	}
	
	
}
