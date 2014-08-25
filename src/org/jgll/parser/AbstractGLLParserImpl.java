package org.jgll.parser;


import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.lexer.GLLLexer;
import org.jgll.lexer.GLLLexerImpl;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.lookup.DescriptorLookup;
import org.jgll.parser.lookup.GSSLookup;
import org.jgll.parser.lookup.SPPFLookup;
import org.jgll.parser.lookup.factory.DescriptorLookupFactory;
import org.jgll.parser.lookup.factory.GSSLookupFactory;
import org.jgll.parser.lookup.factory.SPPFLookupFactory;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonPackedNode;
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
public abstract class AbstractGLLParserImpl implements GLLParser {
		
	protected static final LoggerWrapper log = LoggerWrapper.getLogger(AbstractGLLParserImpl.class);
	
	static {		
		HashTableFactory.init(HashTableFactory.OPEN_ADDRESSING);
	}
	
	protected GSSLookup gssLookup;
	
	protected SPPFLookup sppfLookup;
	
	protected DescriptorLookup descriptorLookup;

	/**
	 * u0 is the bottom of the GSS.
	 */
	protected GSSNode cu;
	
	protected SPPFNode cn = DummyNode.getInstance();
	
	protected int ci = 0;
	
	protected Input input;
	
	protected GLLLexer lexer;
	
	/**
	 * 
	 */
	protected GrammarGraph grammar;
	
	protected Descriptor currentDescriptor;
	
	/**
	 * The grammar slot at which a parse error is occurred. 
	 */
	protected GrammarSlot errorSlot;
	
	/**
	 * The last input index at which an error is occurred. 
	 */
	protected int errorIndex;
	
	/**
	 * The current GSS node at which an error is occurred.
	 */
	protected GSSNode errorGSSNode;
	
	protected GSSLookupFactory gssLookupFactory;

	protected SPPFLookupFactory sppfLookupFactory;

	protected DescriptorLookupFactory descriptorLookupFactory;
	
	protected int descriptorsCount;

	public AbstractGLLParserImpl(GSSLookupFactory gssLookupFactory, 
								 SPPFLookupFactory sppfLookupFactory, 
								 DescriptorLookupFactory descriptorLookupFactory) {
		this.gssLookupFactory = gssLookupFactory;
		this.sppfLookupFactory = sppfLookupFactory;
		this.descriptorLookupFactory = descriptorLookupFactory;
	}
	
	@Override
	public ParseResult parse(Input input, GrammarGraph grammar, String startSymbolName) {
		return parse(new GLLLexerImpl(input, grammar), grammar, startSymbolName);
	}
	
	@Override
	public abstract ParseResult parse(GLLLexer lexer, GrammarGraph grammar, String startSymbolName);
	
	/**
	 * Replaces the previously reported parse error with the new one if the
	 * inputIndex of the new parse error is greater than the previous one. In
	 * other words, we throw away an error if we find an error which happens at
	 * the next position of input.
	 * 
	 */
	@Override
	public void recordParseError(GrammarSlot slot) {
		if (ci >= this.errorIndex) {
			log.debug("Error recorded at %s %d", this, ci);
			this.errorIndex = ci;
			this.errorSlot = slot;
			this.errorGSSNode = cu;
		}
	}
	
	protected void initLookups(GrammarGraph grammar, Input input) {
		gssLookup = gssLookupFactory.createGSSLookupFactory(grammar, input);
		sppfLookup = sppfLookupFactory.createSPPFLookup(grammar, input);
		descriptorLookup = descriptorLookupFactory.createDescriptorLookup(grammar, input);
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
	public final void scheduleDescriptor(Descriptor descriptor) {
		descriptorLookup.scheduleDescriptor(descriptor);
		log.trace("Descriptor created: %s", descriptor);
		descriptorsCount++;
	}
	
	@Override
	public boolean hasDescriptor(Descriptor descriptor) {
		return descriptorLookup.addDescriptor(descriptor);
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
	public final GrammarSlot pop() {
		return pop(cu, ci, (NonPackedNode) cn);
	}
	
	protected abstract GrammarSlot pop(GSSNode gssNode, int inputIndex, NonPackedNode node);

	@Override
	public boolean hasNextDescriptor() {
		return descriptorLookup.hasNextDescriptor();
	}
	
	@Override
	public Descriptor nextDescriptor() {
		Descriptor descriptor = descriptorLookup.nextDescriptor();
		ci = descriptor.getInputIndex();
		cu = descriptor.getGSSNode();
		cn = descriptor.getSPPFNode();
		currentDescriptor = descriptor;
		log.trace("Processing %s", descriptor);
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
	public GSSLookup getGSSLookup() {
		return gssLookup;
	}
	
	public SPPFLookup getSPPFLookup() {
		return sppfLookup;
	}
	
	public DescriptorLookup getDescriptorLookup() {
		return descriptorLookup;
	}

	@Override
	public GrammarGraph getGrammar() {
		return grammar;
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
	public TokenSymbolNode getTokenNode(int tokenID, int inputIndex, int length) {
		ci += length;
		return sppfLookup.getTokenSymbolNode(tokenID, inputIndex, length);
	}
	
}