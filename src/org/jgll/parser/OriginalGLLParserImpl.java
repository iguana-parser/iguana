package org.jgll.parser;


import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.L0;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.parser.gss.GSSEdge;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.gss.OriginalGSSEdgeImpl;
import org.jgll.parser.lookup.factory.DescriptorLookupFactory;
import org.jgll.parser.lookup.factory.GSSLookupFactory;
import org.jgll.parser.lookup.factory.SPPFLookupFactory;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonPackedNode;

/**
 *
 * @author Ali Afroozeh
 * 
 */
public class OriginalGLLParserImpl extends AbstractGLLParserImpl {
	
	protected static final GSSNode u0 = new GSSNode(L0.getInstance(), 0);
		
	public OriginalGLLParserImpl(GSSLookupFactory gssLookupFactory, 
						 SPPFLookupFactory sppfLookupFactory, 
						 DescriptorLookupFactory descriptorLookupFactory) {
		super(gssLookupFactory, sppfLookupFactory, descriptorLookupFactory);
	}
	
	@Override
	protected void initParserState(HeadGrammarSlot startSymbol) {
		u0.clearDescriptors();
		cu = u0;
		cn = DummyNode.getInstance();
		ci = 0;
		errorSlot = null;
		errorIndex = 0;
		errorGSSNode = null;
	}
	
	@Override
	public final GrammarSlot pop(GSSNode gssNode, int inputIndex, NonPackedNode node) {
		
		if (gssNode != u0) {

			log.debug("Pop %s, %d, %s", gssNode, inputIndex, node);
			
			if (!gssLookup.addToPoppedElements(gssNode, node))
				return null;

			BodyGrammarSlot returnSlot = (BodyGrammarSlot) gssNode.getGrammarSlot();

			if (returnSlot.getPopConditions().stream().anyMatch(c -> c.getSlotAction().execute(input, gssNode, inputIndex)))
				return null; 
			
			// Optimization for the case when only one GSS Edge is available.
			// No scheduling of descriptors, rather direct jump to the slot
			// to be processed.
			if (gssNode.countGSSEdges() == 1) {
				GSSEdge edge = gssNode.getGSSEdges().iterator().next();
		
				NonPackedNode sppfNode = returnSlot.getNodeCreatorFromPop().create(this, returnSlot, edge.getNode(), node);
				Descriptor descriptor = new Descriptor(returnSlot, edge.getDestination(), inputIndex, sppfNode);
				
				if (!hasDescriptor(descriptor)) {
					cn = sppfNode;
					cu = edge.getDestination();
					ci = inputIndex;
					log.trace("Processing %s", descriptor);
					descriptorsCount++;
					return returnSlot;
				}
				return null;
			}
			
			for (GSSEdge edge : gssNode.getGSSEdges()) {
				NonPackedNode y = returnSlot.getNodeCreatorFromPop().create(this, returnSlot, edge.getNode(), node);
				Descriptor descriptor = new Descriptor(returnSlot, edge.getDestination(), inputIndex, y);
				if (!hasDescriptor(descriptor)) {
					scheduleDescriptor(new Descriptor(returnSlot, edge.getDestination(), inputIndex, y));
				}
			}
		}
		
		return null;
	}
	
	@Override
	public final GSSNode createGSSNode(BodyGrammarSlot returnSlot, HeadGrammarSlot head) {
		if (! returnSlot.isInitialized()) returnSlot.init(input);
		return gssLookup.getGSSNode(returnSlot, ci);
	}
	
	@Override
	public final GSSNode hasGSSNode(BodyGrammarSlot slot, HeadGrammarSlot head) {
		return gssLookup.hasGSSNode(slot, ci);
	}
	
	@Override
	public GrammarSlot createGSSEdge(BodyGrammarSlot slot, GSSNode destination, NonPackedNode w, GSSNode source) {
		
		GSSEdge edge = new OriginalGSSEdgeImpl(w, destination);
		
		if(gssLookup.getGSSEdge(source, edge)) {
			
			BodyGrammarSlot returnSlot = (BodyGrammarSlot) source.getGrammarSlot();
			
			log.trace("GSS Edge created from %s to %s", source, destination);
			
			// Optimization for the case when only one element is in the popped elements.
			// No scheduling of descriptors, rather direct jump to the slot
			// to be processed.
			if (source.countPoppedElements() == 1) {
				NonPackedNode z = source.getPoppedElements().iterator().next();
				
				if (returnSlot.getPopConditions().stream().anyMatch(c -> c.getSlotAction().execute(input, destination, z.getRightExtent())))
					return null;
				
				NonPackedNode x = returnSlot.getNodeCreatorFromPop().create(this, returnSlot, w, z);
				Descriptor descriptor = new Descriptor(returnSlot, destination, z.getRightExtent(), x);
				
				if (!hasDescriptor(descriptor)) {
					cn = x;
					cu = destination;
					ci = z.getRightExtent();
					log.trace("Processing %s", descriptor);
					descriptorsCount++;
					return returnSlot;
				}
				return null;
			}
			
			label:
			for (NonPackedNode z : source.getPoppedElements()) {
				
				// Execute pop actions for continuations, when the GSS node already
				// exits. The input index will be the right extend of the node
				// stored in the popped elements.
				if (returnSlot.getPopConditions().stream().anyMatch(c -> c.getSlotAction().execute(input, destination, z.getRightExtent())))
					continue label;
				
				NonPackedNode x = returnSlot.getNodeCreatorFromPop().create(this, returnSlot, w, z); 
				Descriptor descriptor = new Descriptor(returnSlot, destination, z.getRightExtent(), x);
				if (!hasDescriptor(descriptor)) {
					scheduleDescriptor(descriptor);
				}
			}
		}
		
		return null;
	}

}