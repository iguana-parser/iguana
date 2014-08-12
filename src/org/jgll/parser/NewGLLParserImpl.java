package org.jgll.parser;


import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.L0;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.parser.gss.GSSEdge;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.gss.NewGSSEdgeImpl;
import org.jgll.parser.lookup.factory.DescriptorLookupFactory;
import org.jgll.parser.lookup.factory.GSSLookupFactory;
import org.jgll.parser.lookup.factory.SPPFLookupFactory;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.BenchmarkUtil;
import org.jgll.util.ParseStatistics;

/**

 * @author Ali Afroozeh
 * 
 */
public class NewGLLParserImpl extends AbstractGLLParserImpl {
		
	public NewGLLParserImpl(GSSLookupFactory gssLookupFactory, 
						 SPPFLookupFactory sppfLookupFactory, 
						 DescriptorLookupFactory descriptorLookupFactory) {
		super(gssLookupFactory, sppfLookupFactory, descriptorLookupFactory);
	}
		
	@Override
	public ParseResult parse(GLLLexer lexer, GrammarGraph grammar, String startSymbolName) {
		HeadGrammarSlot startSymbol = grammar.getHeadGrammarSlot(startSymbolName);
		
		if(startSymbol == null) {
			throw new RuntimeException("No nonterminal named " + startSymbolName + " found");
		}
		
		this.grammar = grammar;
		this.lexer = lexer;
		this.input = lexer.getInput();
		
		initParserState();
		initLookups(grammar, input);
	
		log.info("Parsing %s:", input.getURI());

		long start = System.nanoTime();
		long startUserTime = BenchmarkUtil.getUserTime();
		long startSystemTime = BenchmarkUtil.getSystemTime();
		
		cu = createGSSNode(null, startSymbol);
		
		NonterminalNode root;
		
		L0.getInstance().parse(this, lexer, startSymbol);			
		root = sppfLookup.getStartSymbol(startSymbol, input.length());

		ParseResult parseResult;
		
		long end = System.nanoTime();
		long endUserTime = BenchmarkUtil.getUserTime();
		long endSystemTime = BenchmarkUtil.getSystemTime();
		
		if (root == null) {
			parseResult = new ParseError(errorSlot, this.input, errorIndex, errorGSSNode);
			log.info("Parse error:\n %s", parseResult);
		} else {
			ParseStatistics parseStatistics = new ParseStatistics(input, end - start,
					  endUserTime - startUserTime,
					  endSystemTime - startSystemTime, 
					  BenchmarkUtil.getMemoryUsed(),
					  descriptorLookup.getDescriptorsCount(), 
					  gssLookup.getGSSNodesCount(), 
					  gssLookup.getGSSEdgesCount(), 
					  sppfLookup.getNonterminalNodesCount(), 
					  sppfLookup.getIntermediateNodesCount(), 
					  sppfLookup.getPackedNodesCount(), 
					  sppfLookup.getAmbiguousNodesCount());

			parseResult = new ParseSuccess(root, parseStatistics);
			log.info("Parsing finished successfully.");			
			log.info(parseStatistics.toString());
		}
		
		return parseResult;
	}
	
	private void initParserState() {
		cu = null;
		cn = DummyNode.getInstance();
		ci = 0;
		errorSlot = null;
		errorIndex = 0;
		errorGSSNode = null;
	}
	
	@Override
	public final GrammarSlot pop(GSSNode gssNode, int inputIndex, NonPackedNode node) {
		
		log.debug("Pop %s, %d, %s", gssNode, inputIndex, node);
		
		if (!gssLookup.addToPoppedElements(gssNode, node)) {
			return null;
		}
		
		// Optimization for the case when only one GSS Edge is available.
		// No scheduling of descriptors, rather direct jump to the slot
		// to be processed.
		if (gssNode.countGSSEdges() == 1) {
			GSSEdge edge = gssNode.getGSSEdges().iterator().next();
			BodyGrammarSlot returnSlot = edge.getReturnSlot();
			
			if(returnSlot.getPopConditions().execute(this, lexer, gssNode, inputIndex)) {
				return null;
			}

			SPPFNode sppfNode = returnSlot.getNodeCreatorFromPop().create(this, returnSlot, edge.getNode(), node);
			Descriptor descriptor = new Descriptor(returnSlot, edge.getDestination(), inputIndex, sppfNode);
			
			if (!hasDescriptor(descriptor)) {
				cn = sppfNode;
				cu = edge.getDestination();
				ci = inputIndex;
				log.trace("Processing %s", descriptor);
				return returnSlot;
			}
			return null;
		}
		
		label:
		for(GSSEdge edge : gssNode.getGSSEdges()) {
			BodyGrammarSlot returnSlot = edge.getReturnSlot();
			
			if(returnSlot.getPopConditions().execute(this, lexer, gssNode, inputIndex)) {
				continue label;
			}
			
			SPPFNode y = returnSlot.getNodeCreatorFromPop().create(this, returnSlot, edge.getNode(), node);
			
			
			Descriptor descriptor = new Descriptor(returnSlot, edge.getDestination(), inputIndex, y);
			if (!hasDescriptor(descriptor)) {
				scheduleDescriptor(descriptor);
			}
		}
		
		return null;
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
	 * @param returnSlot the grammar label
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
	public final GSSNode createGSSNode(BodyGrammarSlot returnSlot, HeadGrammarSlot head) {
		cu = gssLookup.getGSSNode(head, ci);
		log.trace("GSSNode created: %s",  cu);
		return cu;
	}
	
	@Override
	public final GSSNode hasGSSNode(BodyGrammarSlot slot, HeadGrammarSlot head) {
		return gssLookup.hasGSSNode(head, ci);
	}
	
	@Override
	public GrammarSlot createGSSEdge(BodyGrammarSlot returnSlot, GSSNode destination, SPPFNode w, GSSNode source) {
		NewGSSEdgeImpl edge = new NewGSSEdgeImpl(returnSlot, w, destination);
		
		if(source.getGSSEdge(edge)) {
			log.trace("GSS Edge created: %s from %s to %s", returnSlot, source, destination);

			// Optimization for the case when only one element is in the popped elements.
			// No scheduling of descriptors, rather direct jump to the slot
			// to be processed.
			if (source.countPoppedElements() == 1) {
				SPPFNode z = source.getPoppedElements().iterator().next();
				if(returnSlot.getPopConditions().execute(this, lexer, destination, z.getRightExtent())) {
					return null;
				}
				
				SPPFNode x = returnSlot.getNodeCreatorFromPop().create(this, returnSlot, w, z);
				Descriptor descriptor = new Descriptor(returnSlot, destination, z.getRightExtent(), x);
				
				if (!hasDescriptor(descriptor)) {
					cn = x;
					cu = destination;
					ci = z.getRightExtent();
					log.trace("Processing %s", descriptor);
					return returnSlot;
				}
				return null;
			}
			
			label:
			for (SPPFNode z : source.getPoppedElements()) {
				
				// Execute pop actions for continuations, when the GSS node already
				// exits. The input index will be the right extend of the node
				// stored in the popped elements.
				if(returnSlot.getPopConditions().execute(this, lexer, destination, z.getRightExtent())) {
					continue label;
				}
				
				SPPFNode x = returnSlot.getNodeCreatorFromPop().create(this, returnSlot, w, z); 
				
				Descriptor descriptor = new Descriptor(returnSlot, destination, z.getRightExtent(), x);
				if (!hasDescriptor(descriptor)) {
					scheduleDescriptor(descriptor);
				}
			}
		}
		
		return null;
	}
}