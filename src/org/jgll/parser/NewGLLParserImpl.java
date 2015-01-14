package org.jgll.parser;


import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.DummySlot;
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
		
		log.debug("Pop %s, %d, %s", gssNode, inputIndex, node);
		
		if (!gssLookup.addToPoppedElements(gssNode, node))
			return;
		
		for(GSSEdge edge : gssNode.getGSSEdges()) {
			BodyGrammarSlot returnSlot = edge.getReturnSlot();
			
			if (returnSlot.getConditions().execute(input, gssNode, inputIndex))
				continue;

			NonPackedNode y = sppfLookup.getNode(returnSlot, edge.getNode(), node);
			addDescriptor(returnSlot, edge.getDestination(), inputIndex, y);
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
				
				if (returnSlot.getConditions().execute(input, destination, z.getRightExtent()))
					continue;
				
				NonPackedNode x = sppfLookup.getNode(returnSlot, w, z); 
				addDescriptor(returnSlot, destination, z.getRightExtent(), x);
			}
		}
	}
}