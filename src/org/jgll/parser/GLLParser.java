package org.jgll.parser;

import java.util.List;

import org.jgll.exception.ParsingFailedException;
import org.jgll.grammar.BodyGrammarSlot;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.LastGrammarSlot;
import org.jgll.grammar.NonterminalGrammarSlot;
import org.jgll.grammar.TerminalGrammarSlot;
import org.jgll.lookup.Lookup;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonPackedNodeWithChildren;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.InputUtil;

/**
 * GLLParser is the abstract base class for all the generated GLL parsers.
 * This class provides common methods for SPPF and GSS construction.
 * 
 * Based on the paper "GLL: parse tree generation."
 * 
 * @author Maarten Manders
 * 
 * @author Ali Afroozeh 	<afroozeh@gmail.com>
 */
public abstract class GLLParser {
	
	public final static NonPackedNode DUMMY = new TerminalSymbolNode(-1, -1, -1, "$"); 
	
	protected InputUtil inputUtil;
	
	protected Lookup lookup;
	
	protected int[] I;
	
	/**
	 * 
	 */
	protected DescriptorSet descriptorSet;
	
	/**
	 * The current SPPF node.
	 */
	protected NonPackedNode cn;

	/**
	 * The current right SPPF node.
	 */
	protected NonPackedNode cr;

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
	 * The nonterminal head at which a parser error has occured. 
	 */
	protected String errorNonterminal;
	
	/**
	 * The last input index at which an error has occured. 
	 */
	protected int errorIndex = -1;
	
	protected int column;
	
	public NonterminalSymbolNode parse(String input, Grammar grammar) throws ParsingFailedException {
		I = new int[input.length() + 1];
		for (int i = 0; i < input.length(); i++) {
			I[i] = input.charAt(i);
		}
		I[input.length()] = -1;
		return parse(I, grammar);
	}

	/**
	 * Parses the given input string. If the parsing of input was successful,
	 * the root of SPPF is returned.
	 * 
	 * @param input the input string to be parsed.
	 * @return 
	 * 
	 * @throws ParsingFailedException an instance of {@link ParsingFailedException} if the descriptor set is empty, but
	 * 								  no SPPF root has been found.
	 */
	public abstract NonterminalSymbolNode parse(int[] input, Grammar grammar) throws ParsingFailedException;
	
	/**
	 * Replaces the previously reported parse error with the new one if the
	 * inputIndex of the new parse error is greater than the previous one. In
	 * other words, we throw away an error if we find an error which happens at
	 * the next position of input.
	 * 
	 */
	protected void newError(String errorNonterminal, int errorIndex) {
		if (errorIndex >= this.errorIndex) {
			this.errorIndex = errorIndex;
			this.errorNonterminal = errorNonterminal;
		}
	}

	/**
	 * initialized the parser's state before a new parse.
	 * 
	 */
	protected abstract void init();
	

	public final void add(GrammarSlot label) {
		add(label, cu, ci, DUMMY);
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
	public final void add(GrammarSlot label, GSSNode u, int inputIndex, NonPackedNode w) {
		Descriptor d = new Descriptor(label, u, inputIndex, w);
//		d.setColumn(inputUtil.getLineNumber(inputIndex).getColumnNumber());
		descriptorSet.add(d);
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
	public final void pop(GSSNode u, int i, NonPackedNode z) {
		
		if (!u.equals(u0)) {
			
			// Add (cu, cn) to P
			lookup.addToPoppedElements(u, z);
			
			for(GSSEdge edge : u.getEdges()) {
				assert u.getLabel() instanceof BodyGrammarSlot;
				NonPackedNode x = getNodeP((BodyGrammarSlot) u.getLabel(), edge.getSppfNode(), z);
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
	 * @return 
	 * 
     *
	 */
	public final GSSNode create(GrammarSlot L, GSSNode u, int i, NonPackedNode w) {
		assert L instanceof BodyGrammarSlot;
		
		GSSNode v = lookup.getGSSNode(L, i);
		
		if(!lookup.getGSSEdge(v, w, u)) {
			List<NonPackedNode> edgeLabels = lookup.getEdgeLabels(v);
			if(edgeLabels != null) {
				for (NonPackedNode z : edgeLabels) {
					NonPackedNode x = getNodeP((BodyGrammarSlot) L, w, z);
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
	public final NonPackedNode getNodeT(int x, int i) {
		int h;
		if(x == -2) {
			h = i;
		} else {
			h = i + 1;
		}
		return lookup.getTerminalNode(x, i, h);
	}
	
	public final NonPackedNode getNodeP(BodyGrammarSlot slot) {
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
	public final NonPackedNode getNodeP(BodyGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild) {
		
		// if (alpha is a terminal or a not nullable nonterminal and beta != empty)
		if (slot.getPosition() == 1 && 
			!(slot instanceof LastGrammarSlot) &&
			(slot.previous() instanceof TerminalGrammarSlot ||
			 slot.previous() instanceof NonterminalGrammarSlot && !((NonterminalGrammarSlot) slot.previous()).getNonterminal().isNullable())) {
				cn = rightChild;
				return rightChild;
		} else {
			
			GrammarSlot t;
			// if (beta = empty)
			if (slot instanceof LastGrammarSlot) {
				t = ((LastGrammarSlot) slot).getHead();
			} else {
				t = slot;
			}

			// if (z != $)
			int leftExtent;
			int rightExtent = rightChild.getRightExtent();
			
			if (!leftChild.equals(DUMMY)) {
				leftExtent = leftChild.getLeftExtent();
			} else {
				leftExtent = rightChild.getLeftExtent();
			}
			
			NonPackedNode newNode = lookup.getNonPackedNode(t, leftExtent, rightExtent);

			lookup.createPackedNode(slot, rightChild.getLeftExtent(), (NonPackedNodeWithChildren) newNode, leftChild, rightChild);
			
			return newNode;
		}
	}	
}
