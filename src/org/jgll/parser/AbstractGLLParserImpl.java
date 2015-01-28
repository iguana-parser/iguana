package org.jgll.parser;


import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.jgll.datadependent.ast.Expression;
import org.jgll.datadependent.ast.Statement;
import org.jgll.datadependent.env.Environment;
import org.jgll.datadependent.env.IEvaluatorContext;
import org.jgll.datadependent.env.persistent.PersistentEvaluatorContext;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.GrammarRegistry;
import org.jgll.grammar.condition.DataDependentCondition;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.EndGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.grammar.symbol.CodeBlock;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.gss.GSSNodeData;
import org.jgll.parser.gss.lookup.GSSLookup;
import org.jgll.parser.gss.lookup.GlobalHashGSSLookupImpl;
import org.jgll.parser.lookup.DescriptorLookup;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.TerminalNode;
import org.jgll.sppf.lookup.SPPFLookup;
import org.jgll.util.Configuration;
import org.jgll.util.Configuration.LookupStrategy;
import org.jgll.util.Input;
import org.jgll.util.ParseStatistics;
import org.jgll.util.benchmark.BenchmarkUtil;
import org.jgll.util.logging.LoggerWrapper;

/**
 * 
 * 
 * @author Ali Afroozeh
 * 
 */
public abstract class AbstractGLLParserImpl implements GLLParser {
		
	protected static final LoggerWrapper log = LoggerWrapper.getLogger(AbstractGLLParserImpl.class);
	
	protected final GSSLookup gssLookup;
	
	protected final SPPFLookup sppfLookup;
	
	protected final DescriptorLookup descriptorLookup;

	protected GSSNode cu;
	
	protected NonPackedNode cn = DummyNode.getInstance();
	
	protected int ci = 0;
	
	protected Input input;
	
	/**
	 * 
	 */
	protected GrammarGraph grammarGraph;
	
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
	
	protected int descriptorsCount;

	private final Configuration config;

	public AbstractGLLParserImpl(Configuration config, GSSLookup gssLookup, SPPFLookup sppfLookup, DescriptorLookup descriptorLookup) {
		this.config = config;
		this.gssLookup = gssLookup;
		this.sppfLookup = sppfLookup;
		this.descriptorLookup = descriptorLookup;
	}
	
	@Override
	public final ParseResult parse(Input input, GrammarGraph grammarGraph, Nonterminal nonterminal) {
		this.grammarGraph = grammarGraph;
		this.input = input;
		
		NonterminalGrammarSlot startSymbol = getStartSymbol(nonterminal);
		
		if(startSymbol == null) {
			throw new RuntimeException("No nonterminal named " + nonterminal + " found");
		}
		
		initLookups();

		initParserState(startSymbol);
	
		log.info("Parsing %s:", input.getURI());

		long start = System.nanoTime();
		long startUserTime = BenchmarkUtil.getUserTime();
		long startSystemTime = BenchmarkUtil.getSystemTime();
		
		NonterminalNode root;
		
		parse(startSymbol);
		
		root = sppfLookup.getStartSymbol(startSymbol, input.length());

		ParseResult parseResult;
		
		long end = System.nanoTime();
		long endUserTime = BenchmarkUtil.getUserTime();
		long endSystemTime = BenchmarkUtil.getSystemTime();
		
		if (root == null) {
			parseResult = new ParseError(errorSlot, this.input, errorIndex, errorGSSNode);
			log.info("Parse error:\n %s", parseResult);
		} else {
			ParseStatistics parseStatistics = ParseStatistics.builder()
					.setNanoTime(end - start)
					.setUserTime(endUserTime - startUserTime)
					.setSystemTime(endSystemTime - startSystemTime) 
					.setMemoryUsed(BenchmarkUtil.getMemoryUsed())
					.setDescriptorsCount(descriptorsCount) 
					.setGSSNodesCount(gssLookup.getGSSNodesCount()) 
					.setGSSEdgesCount(gssLookup.getGSSEdgesCount()) 
					.setNonterminalNodesCount(sppfLookup.getNonterminalNodesCount())
					.setTerminalNodesCount(sppfLookup.getTerminalNodesCount())
					.setIntermediateNodesCount(sppfLookup.getIntermediateNodesCount()) 
					.setPackedNodesCount(sppfLookup.getPackedNodesCount()) 
					.setAmbiguousNodesCount(sppfLookup.getAmbiguousNodesCount()).build();

			parseResult = new ParseSuccess(root, parseStatistics);
			log.info("Parsing finished successfully.");			
			log.info(parseStatistics.toString());
		}
		
		return parseResult;
	}
	
	protected NonterminalGrammarSlot getStartSymbol(Nonterminal nonterminal) {
		return grammarGraph.getRegistry().getHead(nonterminal);
	}
	
	protected void parse(NonterminalGrammarSlot startSymbol) {
		
		if(!startSymbol.test(input.charAt(ci))) {
			recordParseError(startSymbol);
			return;
		}
		
		startSymbol.getFirstSlots().forEach(s -> scheduleDescriptor(new Descriptor(s, cu, ci, DummyNode.getInstance())));
		
		while(descriptorLookup.hasNextDescriptor()) {
			Descriptor descriptor = descriptorLookup.nextDescriptor();
			ci = descriptor.getInputIndex();
			cu = descriptor.getGSSNode();
			cn = descriptor.getSPPFNode();
			log.trace("Processing %s", descriptor);
			descriptor.execute(this);
		}
	}
	
	protected abstract void initParserState(NonterminalGrammarSlot startSymbol);
	
	@Override
	public GSSNode create(BodyGrammarSlot returnSlot, NonterminalGrammarSlot nonterminal, GSSNode u, int i, NonPackedNode node) {

		GSSNode gssNode = hasGSSNode(returnSlot, nonterminal, i);
		if (gssNode == null) {
			
			gssNode = createGSSNode(returnSlot, nonterminal, i);
			log.trace("GSSNode created: %s",  gssNode);
			createGSSEdge(returnSlot, u, node, gssNode);
			
			final GSSNode __gssNode = gssNode;
			nonterminal.getFirstSlots().forEach(s -> scheduleDescriptor(new Descriptor(s, __gssNode, i, DummyNode.getInstance())));
		} else {
			log.trace("GSSNode found: %s",  gssNode);
			createGSSEdge(returnSlot, u, node, gssNode);			
		}
		return gssNode;
	}
		
	public abstract void createGSSEdge(BodyGrammarSlot returnSlot, GSSNode destination, NonPackedNode w, GSSNode source);
	
	public abstract GSSNode createGSSNode(BodyGrammarSlot returnSlot, NonterminalGrammarSlot nonterminal, int i);
	
	public abstract GSSNode hasGSSNode(BodyGrammarSlot returnSlot, NonterminalGrammarSlot nonterminal, int i);

	
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
			log.debug("Error recorded at %s %d", slot, ci);
			this.errorIndex = ci;
			this.errorSlot = slot;
			this.errorGSSNode = cu;
		}
	}
	
	protected void initLookups() {
		sppfLookup.reset();
		gssLookup.reset();
	}

	public final void scheduleDescriptor(Descriptor descriptor) {
		descriptorLookup.scheduleDescriptor(descriptor);
		log.trace("Descriptor created: %s", descriptor);
		descriptorsCount++;
	}
	
	@Override
	public boolean hasDescriptor(GrammarSlot slot, GSSNode gssNode, int inputIndex, NonPackedNode sppfNode) {
		return descriptorLookup.addDescriptor(slot, gssNode, inputIndex, sppfNode);
    }
	
	public SPPFLookup getSPPFLookup() {
		return sppfLookup;
	}
	
	public DescriptorLookup getDescriptorLookup() {
		return descriptorLookup;
	}

	@Override
	public Input getInput() {
		return input;
	}
	
	@Override
	public void reset() {
		grammarGraph.reset(input);
		gssLookup.reset();
		sppfLookup.reset();
	}
	
	@Override
	public GrammarRegistry getRegistry() {
		return grammarGraph.getRegistry();
	}
	
	@Override
	public TerminalNode getEpsilonNode(TerminalGrammarSlot slot, int inputIndex) {
		return sppfLookup.getEpsilonNode(slot, inputIndex);
	}
	
	@Override
	public NonterminalNode getNonterminalNode(EndGrammarSlot slot, NonPackedNode child) {
		return sppfLookup.getNonterminalNode(slot, child);
	}
	
	@Override
	public NonterminalNode getNonterminalNode(EndGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild) {
		return sppfLookup.getNonterminalNode(slot, leftChild, rightChild);
	}
	
	@Override
	public IntermediateNode getIntermediateNode(BodyGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild) {
		return sppfLookup.getIntermediateNode(slot, leftChild, rightChild);
	}
	
	@Override
	public TerminalNode getTerminalNode(TerminalGrammarSlot slot, int leftExtent, int rightExtent) {
		return sppfLookup.getTerminalNode(slot, leftExtent, rightExtent);
	}
	
	@Override 
	public NonPackedNode getNode(GrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild) {
		return sppfLookup.getNode(slot, leftChild, rightChild);
	}

	@Override
	public Configuration getConfiguration() {
		return config;
	}
	
	@Override
	public Iterable<GSSNode> getGSSNodes() {
		if (config.getGSSLookupStrategy() == LookupStrategy.GLOBAL) {
			return ((GlobalHashGSSLookupImpl) gssLookup).getGSSNodes();
		} else {
			return grammarGraph.getNonterminals().stream().flatMap(s -> StreamSupport.stream(s.getGSSNodes().spliterator(), false)).collect(Collectors.toList());
		}
	}
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	public final IEvaluatorContext ctx = new PersistentEvaluatorContext();
	
	@Override
	public IEvaluatorContext getEvaluatorContext() {
		return ctx;
	}
	
	@Override
	public Environment getEmptyEnvironment() {
		return ctx.getEmptyEnvironment();
	}
	
	@Override
	public Object evaluate(CodeBlock code, Environment env) {
		Statement[] statements = code.getStatements();
		
		if (statements.length == 0) return null;
		
		ctx.setEnvironment(env);
		
		int i = 0;
		while (i < statements.length) {
			statements[i].interpret(ctx);
			i++;
		}
		
		return null;
	}
	
	@Override
	public Object evaluate(DataDependentCondition condition, Environment env) {
		ctx.setEnvironment(env);
		
		return condition.getExpression().interpret(ctx);
	}
	
	@Override
	public Object[] evaluate(Expression[] arguments, Environment env) {
		if (arguments == null) return null;
		
		ctx.setEnvironment(env);
		
		Object[] values = new Object[arguments.length];
		
		int i = 0;
		while (i < arguments.length) {
			values[i] = arguments[i].interpret(ctx);
			i++;
		}
		
		return values;
	}
	
	@Override
	public GSSNode create(BodyGrammarSlot returnSlot, NonterminalGrammarSlot nonterminal, GSSNode u, int i, NonPackedNode node, Expression[] arguments, Environment env) {
		assert !(env.isEmpty() && arguments == null);
		
		if (arguments == null) {
			
			GSSNode gssNode = hasGSSNode(returnSlot, nonterminal, i);
			if (gssNode == null) {
				
				gssNode = createGSSNode(returnSlot, nonterminal, i);
				log.trace("GSSNode created: %s", gssNode);
				
				createGSSEdge(returnSlot, u, node, gssNode, env); // Record environment on the edge
				
				final GSSNode __gssNode = gssNode;
				nonterminal.getFirstSlots().forEach(s -> scheduleDescriptor(new Descriptor(s, __gssNode, i, DummyNode.getInstance())));
				
			} else {
				log.trace("GSSNode found: %s",  gssNode);
				createGSSEdge(returnSlot, u, node, gssNode, env); // Record environment on the edge
			}
			return gssNode;
		}
		
		GSSNodeData<Object> data = new GSSNodeData<>(evaluate(arguments, env));
		
		GSSNode gssNode = hasGSSNode(returnSlot, nonterminal, i, data);
		if (gssNode == null) {
			
			gssNode = createGSSNode(returnSlot, nonterminal, i, data);
			log.trace("GSSNode created: %s",  gssNode);
			
			if (env.isEmpty()) createGSSEdge(returnSlot, u, node, gssNode);
			else createGSSEdge(returnSlot, u, node, gssNode, env);
			
			Environment newEnv = getEmptyEnvironment().store(nonterminal.getParameters(), data.getValues());
			
			final GSSNode __gssNode = gssNode;
			nonterminal.getFirstSlots().forEach(s -> scheduleDescriptor(new org.jgll.datadependent.descriptor.Descriptor(s, __gssNode, i, DummyNode.getInstance(), newEnv)));
			
		} else {
			log.trace("GSSNode found: %s",  gssNode);
			if (env.isEmpty()) createGSSEdge(returnSlot, u, node, gssNode);
			else createGSSEdge(returnSlot, u, node, gssNode, env);		
		}
		return gssNode;
	}
	
	@Override
	public boolean hasDescriptor(GrammarSlot slot, GSSNode gssNode, int inputIndex, NonPackedNode sppfNode, Environment env) {
		// FIXME:
		return descriptorLookup.addDescriptor(slot, gssNode, inputIndex, sppfNode);
    }
	
	public abstract void createGSSEdge(BodyGrammarSlot returnSlot, GSSNode destination, NonPackedNode w, GSSNode source, Environment env);
	
	public abstract <T> GSSNode createGSSNode(GrammarSlot returnSlot, NonterminalGrammarSlot nonterminal, int i, GSSNodeData<T> data);
	
	public abstract <T> GSSNode hasGSSNode(GrammarSlot returnSlot, NonterminalGrammarSlot nonterminal, int i, GSSNodeData<T> data);

}
