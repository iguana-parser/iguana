package org.jgll.parser;

import java.util.Collection;
import java.util.List;

import org.jgll.grammar.BodyGrammarSlot;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.LastGrammarSlot;
import org.jgll.grammar.Nonterminal;
import org.jgll.grammar.NonterminalGrammarSlot;
import org.jgll.grammar.Terminal;
import org.jgll.grammar.TerminalGrammarSlot;
import org.jgll.lookup.LookupTable;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.InputUtil;
import org.slf4j.Logger;

/**
 * GLLParser is the abstract base class for all the generated GLL parsers.
 * This class provides common methods for SPPF and GSS construction.
 * 
 * Based on the paper "GLL: parse tree generation."
 * 
 * @author Maarten Manders
 * @author Ali Afroozeh
 */
public abstract class GLLParser {
	
	protected InputUtil inputUtil;
	
	protected Logger log;
	
	protected LookupTable lookupTable;
	
	protected int[] I;

	/**
	 * The current SPPF node.
	 */
	protected SPPFNode cn;

	/**
	 * The current right SPPF node.
	 */
	protected SPPFNode cr;

	/**
	 * The current input index.
	 */
	protected int ci;

	/**
	 * The current GSS node.
	 */
	protected GSSNode cu;

	/**
	 * u0 is the bottom of the GSS.
	 */
	protected GSSNode u0;
	
	/**
	 * 
	 */
	protected Grammar grammar;

	/**
	 * The grammar slot at which a parse error has occured. 
	 */
	protected BodyGrammarSlot errorSlot;
	
	/**
	 * The last input index at which an error has occured. 
	 */
	protected int errorIndex = -1;
	
	
	public NonterminalSymbolNode parse(String input, Grammar grammar, String startSymbol) throws ParseError {
		return parse(InputUtil.fromString(input), grammar, startSymbol);
	}

	/**
	 * Parses the given input string. If the parsing of input was successful,
	 * the root of SPPF is returned.
	 * 
	 * @param input the input string to be parsed.
	 * @return 
	 * 
	 * @throws ParseError an instance of {@link ParseError} if the descriptor set is empty, but
	 * 								  no SPPF root has been found.
	 */
	public NonterminalSymbolNode parse(int[] input, Grammar grammar, String startSymbol) throws ParseError {
		return parse(input, grammar, grammar.getNonterminalByName(startSymbol));
	}
	
	public abstract NonterminalSymbolNode parse(int[] input, Grammar grammar, Nonterminal startSymbol) throws ParseError;
	
	/**
	 * Replaces the previously reported parse error with the new one if the
	 * inputIndex of the new parse error is greater than the previous one. In
	 * other words, we throw away an error if we find an error which happens at
	 * the next position of input.
	 * 
	 */
	public void newParseError(BodyGrammarSlot slot, int errorIndex) {
		if (errorIndex >= this.errorIndex) {
			this.errorIndex = errorIndex;
			this.errorSlot = slot;
		}
	}

	/**
	 * initialized the parser's state before a new parse.
	 * 
	 */
	protected abstract void init();
	

	public final void add(GrammarSlot label) {
		add(label, cu, ci, DummyNode.getInstance());
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
	public final void add(GrammarSlot label, GSSNode u, int inputIndex, SPPFNode w) {
		Descriptor descriptor = new Descriptor(label, u, inputIndex, w);
		boolean result = lookupTable.addDescriptor(descriptor);
		log.debug("Descriptor added: {} : {}", descriptor, result);
	}
	
	
	public final void pop() {
		pop(cu, ci, cn);
	}

	/**
	 * Pops the current element from GSS. When the top element, cu, is poped, there
	 * may be possibly n children. For each child u, reachable via an edge labelled
	 * by an SPPF node w, a new descriptor is added to the set of descriptors
	 * to be processed.
	 * 
	 * pop() { 
	 * 	if (cu != u0) { 
	 * 		let (L, t, k) be the label of cu 
	 * 		add (cu, cn) to P 
	 * 		for each edge (cu, w, u) { 
	 * 			let x be the node returned by getNodeP(t, w, cn)
	 * 			add(L, u, ci , x)) 
	 *      } 
	 * }
	 */
	public final void pop(GSSNode u, int i, SPPFNode z) {
		
		log.debug("Pop {}, {}, {}", u.getLabel(), i, z);
		
		if (u != u0) {
			
			// Add (cu, cn) to P
			lookupTable.addToPoppedElements(u, z);
			
			for(GSSEdge edge : u.getEdges()) {
				assert u.getLabel() instanceof BodyGrammarSlot;
				SPPFNode x = getNodeP((BodyGrammarSlot) u.getLabel(), edge.getSppfNode(), z);
				add(u.getLabel(), edge.getDestination(), i, x);
			}			
		}
	}
	
	public final GSSNode create(GrammarSlot L) {
		return create(L, cu, ci, cn);
	}
	
	/**
	 * 
	 * create(L,A ::= alpha . beta) {
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
	 * @param L the grammar label
	 * 
	 * @param nonterminalIndex the index of the nonterminal appearing as the head of the rule
	 *                         where this position refers to. 
	 * 
	 * @param alternateIndex the index of the alternate of the rule where this position refers to.
	 * 
	 * @param position the position in the body of the rule where this position referes to
	 *
	 * @return 
	 * 
     *
	 */
	public final GSSNode create(GrammarSlot L, GSSNode u, int i, SPPFNode w) {
		log.debug("GSSNode created: " +  L + ", " + i);
		GSSNode v = lookupTable.getGSSNode(L, i);
		
		if(!lookupTable.getGSSEdge(v, w, u)) {
			List<SPPFNode> edgeLabels = lookupTable.getEdgeLabels(v);
			if(edgeLabels != null) {
				for (SPPFNode z : edgeLabels) {
					SPPFNode x = getNodeP((BodyGrammarSlot) L, w, z);
					add(L, u, z.getRightExtent(), x);
				}			
			}
		}
		
		return v;
	}

	/** 
	 *  getNodeT(a, i) {
	 * 		if there is no SPPF node labelled (a, i, i + 1) create one
	 * 		return the SPPF node labelled (a, i, i + 1) 
	 *  }
	 * @return 
	 */
	public final TerminalSymbolNode getNodeT(int x, int i) {
		return lookupTable.getTerminalNode(x, i);
	}
	
	public final SPPFNode getNodeP(BodyGrammarSlot slot) {
		return getNodeP(slot, cn, cr);
	}

	 /**
	  * getNodeP(X ::= alpha . beta, z, w) {
	  * 	if (|alpha| = 1 and beta != empty) {
	  * 		return w
	  *		} else {
	  *			if (beta = empty) { 
	  *				t := X 
	  * 		} else {
	  * 			else t := (X ::= alpha . beta)
	  * 		}
	  * 		suppose that w has label (x, k, i)
	  * 		if (z != $) {
	  *				suppose that z has label (s, j, k)
	  *				if there does not exist an SPPF node y labelled (t, j, i) create one
	  * 			if y does not have a child labelled (X ::= alpha . beta, k)
	  *				create one with left child z and right child w
	  *			} else {
	  *		 		if there does not exist an SPPF node y labelled (t, k, i) create one
	  *				if y does not have a child labelled (X ::= alpha . beta, k)
	  *				create one with child w
	  * 		}
	  * 	return y
	  * 	}
	  * }
	  */
	public final SPPFNode getNodeP(BodyGrammarSlot slot, SPPFNode leftChild, SPPFNode rightChild) {
		
		// if (alpha is a terminal or a not nullable nonterminal and beta != empty)
		if (slot.getPosition() == 1 && 
			!(slot instanceof LastGrammarSlot) &&
			(slot.previous() instanceof TerminalGrammarSlot ||
			 slot.previous() instanceof NonterminalGrammarSlot && !((NonterminalGrammarSlot) slot.previous()).getNonterminal().isNullable())) {
				cn = rightChild;
				return rightChild;
		} else {
			
			GrammarSlot t = slot;
			// if (beta = empty)
			if (slot instanceof LastGrammarSlot) {
				t = slot.getHead();
			}

			// if (z != $)
			int leftExtent;
			int rightExtent = rightChild.getRightExtent();
			
			if (!leftChild.equals(DummyNode.getInstance())) {
				leftExtent = leftChild.getLeftExtent();
			} else {
				leftExtent = rightChild.getLeftExtent();
			}
			
			NonPackedNode newNode = (NonPackedNode) lookupTable.getNonPackedNode(t, leftExtent, rightExtent);
			
			newNode.addPackedNode(slot, rightChild.getLeftExtent(), leftChild, rightChild, grammar);
			
			return newNode;
		}
	}
	
	/**
	 * Retuns the list of created GSSNodes. This list may be useful for analysis
	 * or visulaization of GSS nodes after the parsing. 
	 */
	public Collection<GSSNode> getGSSNodes() {
		return lookupTable.getGSSNodes();
	}
	
	/**
	 * Checks whether the provided list of terminals match a part of the input
	 * from the given input index.
	 * 
	 * @param l      the given list of terminals
	 * @param inputIndex the current input index
	 * @return false when the list of terminals is longer than the part of the input
	 * 				 from the given input index, or at least one of the provided terminals
	 *               cannot match. Otherwise, this method returns true.
	 */
	public boolean test(List<Terminal> l) {
		if(l.size() > I.length - 1) {
			return false;
		}
		int i = ci;
		for(Terminal t : l) {
			if(!t.match(I[i++])) {
				return false;
			}
		}
		return true;
	}
}




