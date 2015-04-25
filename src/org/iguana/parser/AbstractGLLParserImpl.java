/*
 * Copyright (c) 2015, CWI
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this 
 *    list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 *
 */

package org.iguana.parser;


import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.ast.Statement;
import org.iguana.datadependent.env.Environment;
import org.iguana.datadependent.env.IEvaluatorContext;
import org.iguana.datadependent.env.persistent.PersistentEvaluatorContext;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.condition.DataDependentCondition;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.DummySlot;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.grammar.slot.LastSymbolGrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.grammar.slot.TerminalGrammarSlot;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.parser.descriptor.Descriptor;
import org.iguana.parser.gss.GSSNode;
import org.iguana.parser.gss.GSSNodeData;
import org.iguana.parser.gss.lookup.GSSLookup;
import org.iguana.parser.gss.lookup.GlobalHashGSSLookupImpl;
import org.iguana.parser.lookup.DescriptorLookup;
import org.iguana.sppf.DummyNode;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.TerminalNode;
import org.iguana.sppf.lookup.SPPFLookup;
import org.iguana.util.BenchmarkUtil;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
import org.iguana.util.ParseStatistics;
import org.iguana.util.Configuration.LookupStrategy;
import org.iguana.util.logging.LoggerWrapper;

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
	public final ParseResult parse(Input input, GrammarGraph grammarGraph, Nonterminal nonterminal, Map<String, Object> map) {
		this.grammarGraph = grammarGraph;
		this.input = input;
		
		/**
		 * Data-dependent GLL parsing
		 */
		this.ctx = new PersistentEvaluatorContext(input);
		map.forEach((k,v) -> ctx.declareGlobalVariable(k, v));
		
		NonterminalGrammarSlot startSymbol = getStartSymbol(nonterminal);
		
		if(startSymbol == null) {
			throw new RuntimeException("No nonterminal named " + nonterminal + " found");
		}
		
		initLookups();

		reset();
		grammarGraph.reset(input);
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
		return grammarGraph.getHead(nonterminal);
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
			
			for (BodyGrammarSlot s : nonterminal.getFirstSlots()) {
				if (!s.getConditions().execute(getInput(), __gssNode, i))
					scheduleDescriptor(new Descriptor(s, __gssNode, i, DummyNode.getInstance()));
			}
			
			// nonterminal.getFirstSlots().forEach(s -> scheduleDescriptor(new Descriptor(s, __gssNode, i, DummyNode.getInstance())));
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
		descriptorsCount = 0;
		gssLookup.reset();
		sppfLookup.reset();
	}
	
	@Override
	public TerminalNode getEpsilonNode(TerminalGrammarSlot slot, int inputIndex) {
		return sppfLookup.getEpsilonNode(slot, inputIndex);
	}
	
	@Override
	public NonterminalNode getNonterminalNode(LastSymbolGrammarSlot slot, NonPackedNode child) {
		return sppfLookup.getNonterminalNode(slot, child);
	}
	
	@Override
	public NonterminalNode getNonterminalNode(LastSymbolGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild) {
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
	private IEvaluatorContext ctx;
	private BodyGrammarSlot currentEndGrammarSlot = DummySlot.getInstance();
	
	@Override
	public IEvaluatorContext getEvaluatorContext() {
		return ctx;
	}
	
	@Override
	public BodyGrammarSlot getCurrentEndGrammarSlot() {
		return currentEndGrammarSlot;
	}
	
	@Override
	public void setCurrentEndGrammarSlot(BodyGrammarSlot slot) {
		currentEndGrammarSlot = slot;
	}
	
	@Override
	public Environment getEnvironment() {
		return ctx.getEnvironment();
	}
	
	@Override
	public void setEnvironment(Environment env) {
		ctx.setEnvironment(env);
	}
	
	@Override
	public Environment getEmptyEnvironment() {
		return ctx.getEmptyEnvironment();
	}
	
	@Override
	public Object evaluate(Statement[] statements, Environment env) {
		assert statements.length > 1;
		
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
	public Object evaluate(Expression expression, Environment env) {
		ctx.setEnvironment(env);
		return expression.interpret(ctx);
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
				
				for (BodyGrammarSlot s : nonterminal.getFirstSlots()) {
					if (!s.getConditions().execute(getInput(), __gssNode, i))
						scheduleDescriptor(new Descriptor(s, __gssNode, i, DummyNode.getInstance()));
				}
				
				// nonterminal.getFirstSlots().forEach(s -> scheduleDescriptor(new Descriptor(s, __gssNode, i, DummyNode.getInstance())));
				
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
			log.trace("GSSNode created: %s(%s)",  gssNode, data);
			
			if (env.isEmpty()) createGSSEdge(returnSlot, u, node, gssNode);
			else createGSSEdge(returnSlot, u, node, gssNode, env);
			
			Environment newEnv = getEmptyEnvironment().declare(nonterminal.getParameters(), data.getValues());
			
			final GSSNode __gssNode = gssNode;
			
			setEnvironment(newEnv);
			for (BodyGrammarSlot s : nonterminal.getFirstSlots()) {
				if (s.getLabel() != null)
					this.getEvaluatorContext().declareVariable(String.format(Expression.LeftExtent.format, s.getLabel()), i);
				
				if (!s.getConditions().execute(getInput(), __gssNode, i, getEvaluatorContext()))
					scheduleDescriptor(new org.iguana.datadependent.descriptor.Descriptor(s, __gssNode, i, DummyNode.getInstance(), getEnvironment()));
			}
			
			// nonterminal.getFirstSlots().forEach(s -> scheduleDescriptor(new org.jgll.datadependent.descriptor.Descriptor(s, __gssNode, i, DummyNode.getInstance(), newEnv)));
			
		} else {
			log.trace("GSSNode found: %s",  gssNode);
			if (env.isEmpty()) createGSSEdge(returnSlot, u, node, gssNode);
			else createGSSEdge(returnSlot, u, node, gssNode, env);		
		}
		return gssNode;
	}
	
	@Override
	public boolean hasDescriptor(GrammarSlot slot, GSSNode gssNode, int inputIndex, NonPackedNode sppfNode, Environment env) {
		return descriptorLookup.addDescriptor(slot, gssNode, inputIndex, sppfNode, env);
    }
	
	public abstract void createGSSEdge(BodyGrammarSlot returnSlot, GSSNode destination, NonPackedNode w, GSSNode source, Environment env);
	
	public abstract <T> GSSNode createGSSNode(GrammarSlot returnSlot, NonterminalGrammarSlot nonterminal, int i, GSSNodeData<T> data);
	
	public abstract <T> GSSNode hasGSSNode(GrammarSlot returnSlot, NonterminalGrammarSlot nonterminal, int i, GSSNodeData<T> data);
	
	@Override
	public <T> NonterminalNode getNonterminalNode(LastSymbolGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild, GSSNodeData<T> data) {
		return sppfLookup.getNonterminalNode(slot, leftChild, rightChild, data);
	}
	
	@Override
	public <T> NonterminalNode getNonterminalNode(LastSymbolGrammarSlot slot, NonPackedNode child, GSSNodeData<T> data) {
		return sppfLookup.getNonterminalNode(slot, child, data);
	}
	
	public IntermediateNode getIntermediateNode(BodyGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild, Environment env) {
		return sppfLookup.getIntermediateNode(slot, leftChild, rightChild, env);
	}
	
	public <T> NonPackedNode getNode(GrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild, Environment env, GSSNodeData<T> data) {
		return sppfLookup.getNode(slot, leftChild, rightChild, env, data);
	}

	@Override
	public GrammarGraph getGrammarGraph() {
		return grammarGraph;
	}
}
