package org.iguana.parser;


import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.DummySlot;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.parser.descriptor.Descriptor;
import org.iguana.parser.gss.GSSEdge;
import org.iguana.parser.gss.GSSNode;
import org.iguana.parser.gss.GSSNodeData;
import org.iguana.parser.gss.NewGSSEdgeImpl;
import org.iguana.parser.gss.lookup.GSSLookup;
import org.iguana.parser.lookup.DescriptorLookup;
import org.iguana.sppf.DummyNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.lookup.SPPFLookup;
import org.iguana.util.Configuration;

/**
 *
 * @author Ali Afroozeh
 * 
 */
public class NewGLLParserImpl extends AbstractGLLParserImpl {
		
	public NewGLLParserImpl(Configuration config, GSSLookup gssLookup, SPPFLookup sppfLookup, DescriptorLookup descriptor) {
		super(config, gssLookup, sppfLookup, descriptor);
	}
	
	@Override
	protected void initParserState(NonterminalGrammarSlot startSymbol) {
		startSymbol.initGSSLookup();
		cu = createGSSNode(DummySlot.getInstance(), startSymbol, ci);
		cn = DummyNode.getInstance();
		ci = 0;
		errorSlot = null;
		errorIndex = 0;
		errorGSSNode = null;
	}
	
	@Override
	public final void pop(GSSNode gssNode, int inputIndex, NonPackedNode node) {
		
		if (!gssLookup.addToPoppedElements(gssNode, node))
			return;
		
		log.debug("Pop %s, %d, %s", gssNode, inputIndex, node);
		
		for(GSSEdge edge : gssNode.getGSSEdges()) {			
			Descriptor descriptor = edge.addDescriptor(this, gssNode, inputIndex, node);
			if (descriptor != null) {
				scheduleDescriptor(descriptor);
			}
		}
	}
	
	@Override
	public final GSSNode createGSSNode(BodyGrammarSlot returnSlot, NonterminalGrammarSlot nonterminal, int i) {
		return gssLookup.getGSSNode(nonterminal, i);
	}
	
	@Override
	public final GSSNode hasGSSNode(BodyGrammarSlot returnSlot, NonterminalGrammarSlot nonterminal, int i) {
		return gssLookup.hasGSSNode(nonterminal, i);
	}
	
	@Override
	public void createGSSEdge(BodyGrammarSlot returnSlot, GSSNode destination, NonPackedNode w, GSSNode source) {
		NewGSSEdgeImpl edge = new NewGSSEdgeImpl(returnSlot, w, destination);
		
		if(gssLookup.getGSSEdge(source, edge)) {
			log.trace("GSS Edge created: %s from %s to %s", returnSlot, source, destination);

			for (NonPackedNode z : source.getPoppedElements()) {			
				Descriptor descriptor = edge.addDescriptor(this, source, z.getRightExtent(), z);
				if (descriptor != null) {
					scheduleDescriptor(descriptor);
				}
			}
		}
	}
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	@Override
	public void createGSSEdge(BodyGrammarSlot returnSlot, GSSNode destination, NonPackedNode w, GSSNode source, Environment env) {
		NewGSSEdgeImpl edge = new org.iguana.datadependent.gss.NewGSSEdgeImpl(returnSlot, w, destination, env);
		
		if(gssLookup.getGSSEdge(source, edge)) {
			log.trace("GSS Edge created: %s from %s to %s with %s", returnSlot, source, destination, env);

			for (NonPackedNode z : source.getPoppedElements()) {
				Descriptor descriptor = edge.addDescriptor(this, source, z.getRightExtent(), z);
				if (descriptor != null) {
					scheduleDescriptor(descriptor);
				}				
			}
		}
	}

	@Override
	public <T> GSSNode createGSSNode(GrammarSlot returnSlot, NonterminalGrammarSlot nonterminal, int i, GSSNodeData<T> data) {
		return gssLookup.getGSSNode(nonterminal, i, data);
	}

	@Override
	public <T> GSSNode hasGSSNode(GrammarSlot returnSlot, NonterminalGrammarSlot nonterminal, int i, GSSNodeData<T> data) {
		return gssLookup.hasGSSNode(nonterminal, i, data);
	}
}