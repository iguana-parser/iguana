package org.jgll.parser;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.BodyGrammarSlot;
import org.jgll.grammar.GrammarSlot;
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
	private final Input input;
	
	public ParseError(GrammarSlot slot, Input input, int inputIndex, GSSNode curerntNode) {
		this.slot = slot;
		this.input = input;
		this.inputIndex = inputIndex;
		this.currentNode = curerntNode;
	}
	
	public int getInputIndex() {
		return inputIndex;
	}

	public GrammarSlot getSlot() {
		return slot;
	}
	
	@Override
	public String toString() {		
		int lineNumber = input.getLineNumber(inputIndex);
		int columnNumber = input.getColumnNumber(inputIndex);
		
		return "Parse error at at line:" + lineNumber + " column:" + columnNumber;
	}
	
	public void printGrammarTrace() {
		printGrammarTrace(System.err);
	}
	
	public void printGrammarTrace(PrintStream out) {
		out.println(toString());
		out.println(slot);
		
		GSSNode gssNode = currentNode;
		
		int i = 0;
		
		Set<GSSNode> visitedNodes = new HashSet<>();
		
		visitedNodes.add(gssNode);
		
		indent(out, i, gssNode);
		
		while(gssNode != GSSNode.U0 && !visitedNodes.contains(gssNode)) {
			visitedNodes.add(gssNode);
			
			gssNode = findMergePoint(gssNode, out, i);
			
			indent(out, i, gssNode);
		}
	}

	private void indent(PrintStream out, int i, GSSNode node) {
		out.println(String.format("%" + i * 2 + "s", ((BodyGrammarSlot) node.getGrammarSlot()).previous()));
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
