package org.jgll.disambiguation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jgll.action.RemoveEBNFNodes;
import org.jgll.action.RemoveEpsilonTerminals;
import org.jgll.action.RemovePackedAndIntermediateNodes;
import org.jgll.exception.DisambiguationFailedException;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.visitor.BottomUpVisitor;
import org.jgll.visitor.OnceBottomUpVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 *
 * 
 * @author Ali Afroozeh
 * 
 */
public final class BottomUpDisambiguator implements Disambiguator {

	private static final Logger log = LoggerFactory.getLogger(BottomUpDisambiguator.class);

	/**
	 * A map from the label of ambiguity nodes to applicable disambiguation rules.
	 */
	private final Multimap<String, DisambiguationRule> rules = ArrayListMultimap.create();
	
	/**
	 * Default rules will be applied on ambiguity nodes whose label
	 * does not match root node of a disambiguation rule.
	 * 
	 */
	private final List<DisambiguationRule> otherRules = new ArrayList<DisambiguationRule>();
	 
	private final RemoveEBNFNodes removeEBNFNodes = new RemoveEBNFNodes();
	private final RemoveEpsilonTerminals removeEpsilonTerminals = new RemoveEpsilonTerminals();

	/**
	 * Holds the number of ambiguity nodes in an SPPF. 
	 */
	private int nAmbiguousNodes;

	@Override
	public void disambiguate(SPPFNode node) throws DisambiguationFailedException {
		long start = System.nanoTime();
		visit(node);
		long end = System.nanoTime();
		log.info("Disambiguation Time: {} ms for {} ambiguous nodes.", new Object[] { (end - start) / 1000000, nAmbiguousNodes });
		
		new BottomUpVisitor().visit(node, new RemovePackedAndIntermediateNodes());
		new OnceBottomUpVisitor().visit(node, removeEBNFNodes, removeEpsilonTerminals);
	}

	private void visit(SPPFNode node) throws DisambiguationFailedException {
		
		if(node.isVisited()) {
			return;
		}
		
		for(SPPFNode child : node.getChildren()) {
			visit(child);
		}
			
		// Only symbol or intermediate nodes can be ambiguous
		if(node instanceof PackedNode) {
			return;
		}

		NonPackedNode nonPackedNode = (NonPackedNode) node;
		if (! nonPackedNode.isAmbiguous()) {
			return;
		}
		
		nAmbiguousNodes++;

		// TODO: check if you're not checking a node twice.
		new BottomUpVisitor().visit(node, new RemovePackedAndIntermediateNodes());
	
		new OnceBottomUpVisitor().visit(node, removeEBNFNodes, removeEpsilonTerminals);
		
		String symbolName = nonPackedNode.getLabel();
					
		Collection<DisambiguationRule> list = rules.get(symbolName);
		
		for (DisambiguationRule pattern : list) {
			pattern.apply(node);
			
			// Disambiguation successful
			if(node.sizeChildren() == 1) {
				disambiguationSuccessful(node);
				return;
			}
		}

		// For intermediate nodes or symbol nodes which are not 
		// disambiguated, try other rules
		for (DisambiguationRule pattern : otherRules) {
			pattern.apply(node);
			
			// Disambiguation successful
			if(node.sizeChildren() == 1) {
				disambiguationSuccessful(node);
				return;
			}
		}
		
		log.error("Disambiguation failed");
		throw new DisambiguationFailedException((NonPackedNode) node);
	}
	
	private void disambiguationSuccessful(SPPFNode node) {
		node.replaceByChildren(node.getChildren().get(0));
	}

	@Override
	public int getNumberOfAmbiguosNodes() {
		return nAmbiguousNodes;
	}

	@Override
	public void addDisambiguationPattern(DisambiguationRule pattern) {
		if(pattern.getRootNode().size() == 0) {
			otherRules.add(pattern);
		} else {
			for(String rootNode : pattern.getRootNode()) {
				rules.put(rootNode, pattern);			
			}			
		}
	}
}
