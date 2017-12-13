/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
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

package org.iguana.util.visualization;

import org.iguana.util.trie.Edge;
import org.iguana.util.trie.Node;
import org.iguana.util.trie.Trie;

import java.util.HashMap;
import java.util.Map;


public class TrieToDot<T> {
	
	private static final String NODE = "[shape=circle, height=0.1, width=0.1, color=black, fontcolor=black, label=\"\", fontsize=10];";
	public static final String EDGE = "edge [color=black, style=solid, penwidth=0.5, arrowsize=0.7, label=\"%s\"];";

	private String string;
	
	private Map<Node<T>, Integer> ids;

	public TrieToDot(Trie<T> trie) {
		ids = new HashMap<>();
		StringBuilder sb = new StringBuilder();
		toDot(trie.getRoot(), sb);
		string = sb.toString();
	}
	
	private void toDot(Node<T> node, StringBuilder sb) {
		sb.append("\"").append(getId(node)).append("\"").append(NODE).append("\n");
		
		for (Edge<T> edge : node.getEdges()) {
			sb.append(String.format(EDGE, edge.getLabel())).append("\"").append(getId(node)).append("\"").append(" -> ").append("{");
			sb.append(getId(edge.getDestination()));
			sb.append("}").append("\n");
			toDot(edge.getDestination(), sb);
		}
	}
	
	@Override
	public String toString() {
		return string;
	}
	
	
	public String getId(Node<T> node) {
		Integer id = ids.get(node);
		
		if (id == null) {
			id = ids.size() + 1;
			ids.put(node, id);
		}
		
		return id + "";
	}
}
