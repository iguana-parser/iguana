package org.jgll.parser;


import org.jgll.grammar.Grammar;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.L0;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TokenGrammarSlot;
import org.jgll.grammar.slotaction.SlotAction;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.lexer.GLLLexer;
import org.jgll.lexer.GLLLexerImpl;
import org.jgll.parser.lookup.LookupTable;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.hashing.HashTableFactory;
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
public class GLLParserImpl implements GLLParser {
		
	private static final LoggerWrapper log = LoggerWrapper.getLogger(GLLParserImpl.class);
	
	static {		
		HashTableFactory.init(HashTableFactory.OPEN_ADDRESSING);
	}
	
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
	
	protected GLLLexer lexer;
	
	/**
	 * 
	 */
	protected Grammar grammar;
	
	protected Descriptor currentDescriptor;
	
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
	
	protected int regularListLength = 10;

	private long start;

	private long end;
	
	private boolean llOptimization = false;

	public GLLParserImpl(LookupTable lookupTable) {
		this.lookupTable = lookupTable;
	}
	
	public GLLParserImpl(LookupTable lookupTable, int regularListLength) {
		this.lookupTable = lookupTable;
		this.regularListLength = regularListLength;
	}
	
	@Override
	public NonterminalSymbolNode parse(Input input, Grammar grammar, String startSymbolName) throws ParseError {
		return parse(new GLLLexerImpl(input, grammar), grammar, startSymbolName);
	}
	
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
	public final NonterminalSymbolNode parse(GLLLexer lexer, Grammar grammar, String startSymbolName) throws ParseError {
		
		HeadGrammarSlot startSymbol = grammar.getNonterminalByName(startSymbolName);
		
		if(startSymbol == null) {
			throw new RuntimeException("No nonterminal named " + startSymbolName + " found");
		}
		
		this.grammar = grammar;
		
		this.lexer = lexer;
		
		this.input = lexer.getInput();
		
		init();
		lookupTable.init(input);
	
		log.info("Iguana started...");

		start = System.nanoTime();
		
		NonterminalSymbolNode root;
		
		if(llOptimization && startSymbol.isLl1SubGrammar()) {
			root = (NonterminalSymbolNode) startSymbol.parseLL1(this, lexer);
		} else {
			L0.getInstance().parse(this, lexer, startSymbol);			
			root = lookupTable.getStartSymbol(startSymbol, input.length());
		}

		end = System.nanoTime();
		
		if (root == null) {
			throw new ParseError(errorSlot, this.input, errorIndex, errorGSSNode);
		}
		
		log.info("Parsing finished successfully.");
		logParseStatistics(end - start);
		return root;
	}
		
	private void logParseStatistics(long duration) {
		log.info("Parsing Time: " + duration/1000000 + " ms");
		
		int mb = 1024 * 1024;
		Runtime runtime = Runtime.getRuntime();
		log.info("Input size: %d, loc: %d", input.length(), input.getLineCount());
		log.info("Memory used: %d mb", (runtime.totalMemory() - runtime.freeMemory()) / mb);
		log.info("Descriptors: %d", lookupTable.getDescriptorsCount());
		log.debug("Non-packed nodes: %d", lookupTable.getNonPackedNodesCount());
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
			log.trace("Error recorded at %s %d", this, ci);
			this.errorIndex = ci;
			this.errorSlot = slot;
			this.errorGSSNode = cu;
		}
	}

	private void init() {
		cu = u0;
		cn = DummyNode.getInstance();
		ci = 0;
		errorSlot = null;
		errorIndex = -1;
		errorGSSNode = null;
	}
	

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
	public final void addDescriptor(GrammarSlot slot, GSSNode u, int inputIndex, SPPFNode w) {
		Descriptor descriptor = new Descriptor(slot, u, inputIndex, w);
		boolean added = lookupTable.addDescriptor(descriptor);
		if(added) {
			log.trace("Descriptor created: %s : %b", descriptor, added);
		}
	}
	
	@Override
	public void addDescriptor(GrammarSlot slot, GSSNode currentGSSNode, int inputIndex, SPPFNode currentNode, Object object) {
		Descriptor descriptor = new Descriptor(slot, currentGSSNode, inputIndex, currentNode);
		descriptor.setObject(object);
		boolean added = lookupTable.addDescriptor(descriptor);
		if(added) {
			log.trace("Descriptor created: %s : %b", descriptor, added);
		}
	}
	
	@Override
	public void addDescriptor(GrammarSlot label) {
		addDescriptor(label, cu, ci, DummyNode.getInstance());
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
			lookupTable.addToPoppedElements(cu, (NonPackedNode) cn);
			
			label:
			for(GSSEdge edge : lookupTable.getEdges(cu)) {
				GrammarSlot slot = edge.getReturnSlot();
				
				for(SlotAction<Boolean> popAction : ((BodyGrammarSlot) edge.getReturnSlot()).getPopActions()) {
					if(popAction.execute(this, lexer)) {
						continue label;
					}
				}
				
				SPPFNode y;
				if(slot instanceof LastGrammarSlot) {
					y = getNonterminalNode((LastGrammarSlot) slot, edge.getNode(), cn);
				} else {
					y = getIntermediateNode((BodyGrammarSlot) slot, edge.getNode(), cn);
				}
				addDescriptor(edge.getReturnSlot(), edge.getDestination(), ci, y);
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
	public final void createGSSNode(BodyGrammarSlot slot, HeadGrammarSlot head) {
		cu = create(slot, head, cu, ci, cn);
	}
	
	private final GSSNode create(BodyGrammarSlot returnSlot, HeadGrammarSlot head, GSSNode u, int i, SPPFNode w) {
		log.trace("GSSNode created: (%s, %d)",  returnSlot, i);

		GSSNode v = lookupTable.getGSSNode(head, i);

		if(lookupTable.getGSSEdge(v, u, w, returnSlot)) {
			for (SPPFNode z : lookupTable.getPoppedElementsOf(v)) {
				SPPFNode x;
				if(returnSlot instanceof LastGrammarSlot) {
					x = getNonterminalNode((LastGrammarSlot) returnSlot, w, z);
				} else {
					x = getIntermediateNode((BodyGrammarSlot) returnSlot, w, z);
				}
				addDescriptor(returnSlot, u, z.getRightExtent(), x);
			}
		}

		return v;
	}
	
	@Override
	public void getNonterminalNode(LastGrammarSlot slot, SPPFNode rightChild) {
		cn = getNonterminalNode(slot, cn, rightChild);
	}
	
	@Override
	public void getIntermediateNode(BodyGrammarSlot slot, SPPFNode rightChild) {
		cn = getIntermediateNode(slot, cn, rightChild);
	}
	
	private final SPPFNode getNonterminalNode(LastGrammarSlot slot, SPPFNode leftChild, SPPFNode rightChild) {
		
		GrammarSlot t = slot.getHead();

		int leftExtent;
		int rightExtent = rightChild.getRightExtent();
		
		if (leftChild != DummyNode.getInstance()) {
			leftExtent = leftChild.getLeftExtent();
		} else {
			leftExtent = rightChild.getLeftExtent();
		}
		
		NonPackedNode newNode = (NonPackedNode) lookupTable.getNonPackedNode(t, leftExtent, rightExtent);
		
		lookupTable.addPackedNode(newNode, slot, rightChild.getLeftExtent(), leftChild, rightChild);
		
		return newNode;
	}
	
	private final SPPFNode getIntermediateNode(BodyGrammarSlot slot, SPPFNode leftChild, SPPFNode rightChild) {
		
		BodyGrammarSlot previous = slot.previous();
		
		// if (alpha is a terminal or a not nullable nonterminal and beta != empty)
		if(previous.isFirst()) {
			if ((previous instanceof NonterminalGrammarSlot || previous instanceof TokenGrammarSlot) && !previous.isNullable()) {
				return rightChild;
			} 		
		}
		
		int leftExtent;
		int rightExtent = rightChild.getRightExtent();
		
		if (leftChild != DummyNode.getInstance()) {
			leftExtent = leftChild.getLeftExtent();
		} else {
			leftExtent = rightChild.getLeftExtent();
		}
		
		NonPackedNode newNode = (NonPackedNode) lookupTable.getNonPackedNode(slot, leftExtent, rightExtent);
		
		lookupTable.addPackedNode(newNode, slot, rightChild.getLeftExtent(), leftChild, rightChild);
		
		return newNode;
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
		currentDescriptor = descriptor;
		log.trace("Processing (%s, %s, %s, %s)", descriptor.getGrammarSlot(), ci, cu, cn);
		return descriptor;
	}
	
	@Override
	public int getCurrentInputIndex() {
		return ci;
	}
	
	@Override
	public GSSNode getCurrentGSSNode() {
		return cu;
	}
	
	@Override
	public NonPackedNode getKeywordStub(Keyword keyword, HeadGrammarSlot slot, int inputIndex) {
		int nextIndex = inputIndex + keyword.size();
		NonPackedNode node = lookupTable.getNonPackedNode(slot, inputIndex, nextIndex);
		node.addFirstPackedNode(new PackedNode(slot.getAlternateAt(0).getLastSlot().next(), nextIndex, node));
		((NonterminalSymbolNode) node).setKeywordNode(true);
		ci = nextIndex;
		return node;
	}

	@Override
	public LookupTable getLookupTable() {
		return lookupTable;
	}

	@Override
	public Grammar getGrammar() {
		return grammar;
	}

	@Override
	public int getRegularListLength() {
		return regularListLength;
	}

	@Override
	public SPPFNode getCurrentSPPFNode() {
		return cn;
	}

	@Override
	public void setCurrentSPPFNode(SPPFNode node) {
		this.cn = node;
	}

	@Override
	public Descriptor getCurrentDescriptor() {
		return currentDescriptor;
	}
	
	@Override
	public long getParsingTime() {
		return end - start;
	}

	public void setLlOptimization(boolean llOptimization) {
		this.llOptimization = llOptimization;
	}
	
	@Override
	public boolean isLLOptimizationEnabled() {
		return llOptimization;
	}

	@Override
	public TokenSymbolNode getTokenNode(int tokenID, int inputIndex, int length) {
		ci += length;
		return lookupTable.getTokenSymbolNode(tokenID, inputIndex, length);
	}
	
}