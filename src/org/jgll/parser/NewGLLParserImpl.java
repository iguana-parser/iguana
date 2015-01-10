package org.jgll.parser;


import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.parser.gss.GSSEdge;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.gss.NewGSSEdgeImpl;
import org.jgll.parser.gss.lookup.GSSLookup;
import org.jgll.parser.lookup.DescriptorLookup;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.lookup.SPPFLookup;
import org.jgll.util.Configuration;

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
		cu = createGSSNode(null, startSymbol, ci);
		cn = DummyNode.getInstance();
		ci = 0;
		errorSlot = null;
		errorIndex = 0;
		errorGSSNode = null;
	}
	
	@Override
	public final void pop(GSSNode gssNode, int inputIndex, NonPackedNode node) {
		
		log.debug("Pop %s, %d, %s", gssNode, inputIndex, node);
		
		if (!gssLookup.addToPoppedElements(gssNode, node))
			return;
		
		for(GSSEdge edge : gssNode.getGSSEdges()) {
			GrammarSlot returnSlot = edge.getReturnSlot();
			
			for(Condition c : returnSlot.getConditions()) {
				if (c.getSlotAction().execute(input, gssNode, inputIndex)) 
					break;
			}

			NonPackedNode y = sppfLookup.getNode(returnSlot, edge.getNode(), node);
			addDescriptor(returnSlot, edge.getDestination(), inputIndex, y);
		}
	}
	
	@Override
	public final GSSNode createGSSNode(GrammarSlot returnSlot, NonterminalGrammarSlot nonterminal, int i) {
		return gssLookup.getGSSNode(nonterminal, i);
	}
	
	@Override
	public final GSSNode hasGSSNode(GrammarSlot returnSlot, NonterminalGrammarSlot nonterminal, int i) {
		return gssLookup.hasGSSNode(nonterminal, i);
	}
	
	@Override
	public void createGSSEdge(GrammarSlot returnSlot, GSSNode destination, NonPackedNode w, GSSNode source) {
		NewGSSEdgeImpl edge = new NewGSSEdgeImpl(returnSlot, w, destination);
		
		if(gssLookup.getGSSEdge(source, edge)) {
			log.trace("GSS Edge created: %s from %s to %s", returnSlot, source, destination);

			for (NonPackedNode z : source.getPoppedElements()) {
				
				// Execute pop actions for continuations, when the GSS node already
				// exits. The input index will be the right extend of the node
				// stored in the popped elements.
				for (Condition c : returnSlot.getConditions()) {
					if (c.getSlotAction().execute(input, destination, z.getRightExtent())) 
						break;
				}
				
				NonPackedNode x = sppfLookup.getNode(returnSlot, w, z); 
				addDescriptor(returnSlot, destination, z.getRightExtent(), x);
			}
		}
	}
}