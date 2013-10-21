package org.jgll.parser;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.util.Input;


/**
 * 
 * @author Ali Afroozeh
 *
 */
@SuppressWarnings("serial")
public class ParseError extends Exception {

	private final GSSNode currentNode;
	private final GrammarSlot slot;
	private final int inputIndex;
	
	public ParseError(GrammarSlot slot, Input input, int inputIndex, GSSNode curerntNode) {
		super(getMessage(input, inputIndex));
		this.slot = slot;
		this.inputIndex = inputIndex;
		this.currentNode = curerntNode;
	}
	
	public int getInputIndex() {
		return inputIndex;
	}

	public GrammarSlot getSlot() {
		return slot;
	}
	
	public static String getMessage(Input input, int inputIndex) {		
		int lineNumber = input.getLineNumber(inputIndex);
		int columnNumber = input.getColumnNumber(inputIndex);
		
		return "Parse error at line:" + lineNumber + " column:" + columnNumber;
	}
	
	public void printGrammarTrace() {
		printGrammarTrace(System.out);
	}
	
	public void printGrammarTrace(PrintStream out) {
		out.println(toString());
		
		indent(out, 1, GSSNode.recursiveDescentGSSNode(((BodyGrammarSlot) slot).next(), inputIndex));
		
		GSSNode gssNode = currentNode;
		
		while(gssNode != GSSNode.U0) {
			indent(out, 1, gssNode);			
			gssNode = findMergePoint(gssNode, out, 1);
		}
	}

	private void indent(PrintStream out, int i, GSSNode node) {
		if(node == GSSNode.U0) {
			return;
		}
		out.println(String.format("%" + i * 2 + "s, %d", ((BodyGrammarSlot) node.getGrammarSlot()).previous().toString(), node.getInputIndex()));
	}
	
	private GSSNode findMergePoint(GSSNode node, PrintStream out, int i) {
		
		if(node.getCountEdges() == 1) {
			return node.getEdges().iterator().next().getDestination();
		}
		
		return reachableFrom(node, out, i);
	}
	
	/**
	 * Adds all the GSS nodes reachable from the given node to the
	 * provided set and removes the node from the set.
	 * 
	 * @throws IllegalArgumentException if the given set is null.
	 *  
	 */
	private GSSNode reachableFrom(GSSNode node, PrintStream out, int i) {
		
		Set<GSSNode> set = new HashSet<>();
		Deque<GSSNode> frontier = new ArrayDeque<>();
		
		for(GSSEdge edge : node.getEdges()) {
			GSSNode destination = edge.getDestination();
			set.add(destination);
			frontier.add(destination);
			indent(out, i+1, destination);
		}
		
		i++;
		while(frontier.size() > 1) {
			GSSNode f = (GSSNode) frontier.poll();
			for(GSSEdge edge : f.getEdges()) {
				GSSNode destination = edge.getDestination();
				if(!set.contains(destination)) {
					set.add(destination);
					frontier.add(destination);
					indent(out, i+1, destination);
				}
			}
		}
		
		return frontier.poll();
	}
 	
}
