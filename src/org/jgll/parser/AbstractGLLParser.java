package org.jgll.parser;


import org.jgll.grammar.BodyGrammarSlot;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.grammar.SlotAction;
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
		log.info("Descriptors: %d", lookupTable.getDescriptorsCount());
		log.info("Non-packed nodes: %d", lookupTable.getDescriptorsCount());
		log.info("Packed nodes: %d", lookupTable.getPackedNodesCount());
		log.info("GSS Nodes: %d", lookupTable.getGSSNodesCount());
		log.info("GSS Edges: %d", lookupTable.getGSSEdgesCount());
	}

	
	/**
	 * Replaces the previously reported parse error with the new one if the
	 * inputIndex of the new parse error is greater than the previous one. In
	 * other words, we throw away an error if we find an error which happens at
	 * the next position of input.
	 * 
	 */
	@Override
	public void newParseError(GrammarSlot slot, int errorIndex, GSSNode node) {
		if (errorIndex >= this.errorIndex) {
			this.errorIndex = errorIndex;
			this.errorSlot = slot;
			this.errorGSSNode = node;
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
	@Override
	public final void add(GrammarSlot label, GSSNode u, int inputIndex, SPPFNode w) {
		Descriptor descriptor = new Descriptor(label, u, inputIndex, w);
		boolean result = lookupTable.addDescriptor(descriptor);
		log.trace("Descriptor created: %s : %b", descriptor, result);
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
	public final void pop(GSSNode u, int i, SPPFNode z) {
		
		if (u != u0) {
			
			// Don't pop if a pop action associated with the slot returns false.
			for(SlotAction<Boolean> popAction : ((BodyGrammarSlot) u.getGrammarSlot()).getPopActions()) {
				if(!popAction.execute(this, input)) {
					return;
				}
			}
			
			log.trace("Pop %s, %d, %s", u.getGrammarSlot(), i, z);
			
			// Add (u, z) to P
			lookupTable.addToPoppedElements(u, z);
			
			for(GSSEdge edge : u.getEdges()) {
				assert u.getGrammarSlot() instanceof BodyGrammarSlot;
				BodyGrammarSlot slot = (BodyGrammarSlot) u.getGrammarSlot();
				SPPFNode y = getNodeP(slot, edge.getSppfNode(), z);
				add(u.getGrammarSlot(), edge.getDestination(), i, y);
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
	 * @param L the grammar label
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
	public final GSSNode create(GrammarSlot L, GSSNode u, int i, SPPFNode w) {
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
	public final TerminalSymbolNode getNodeT(int x, int i) {
		return lookupTable.getTerminalNode(x, i);
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
	@Override
	public final SPPFNode getNodeP(BodyGrammarSlot slot, SPPFNode leftChild, SPPFNode rightChild) {
		
		// if (alpha is a terminal or a not nullable nonterminal and beta != empty)
		if (slot.getPosition() == 1 && 
			!(slot.isLastSlot()) &&
			(slot.previous().isTerminalSlot() || !slot.previous().isNullable())) {
				return rightChild;
		} else {
			
			GrammarSlot t = slot;
			// if (beta = empty)
			if (slot.isLastSlot()) {
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
		return lookupTable.nextDescriptor();
	}
	
	@Override
	public void update(GSSNode cu, SPPFNode cn, int ci) {
		this.cu = cu;
		this.cn = cn;
		this.ci = ci;
	}

	@Override
	public int getCi() {
		return ci;
	}
	
	@Override
	public SPPFNode getCn() {
		return cn;
	}
	
	@Override
	public GSSNode getCu() {
		return cu;
	}
			
}
