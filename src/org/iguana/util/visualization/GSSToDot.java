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

import static org.iguana.util.visualization.GraphVizUtil.*;

import org.iguana.parser.gss.GSSEdge;
import org.iguana.parser.gss.GSSNode;

public class GSSToDot extends ToDot {
	
	private StringBuilder sb = new StringBuilder();
	
	public void execute(Iterable<GSSNode> set) {
		
		for(GSSNode gssNode : set) {
			
			sb.append("\"" + getId(gssNode) + "\"" + String.format(GSS_NODE, gssNode.toString()) + "\n");
			
			for(GSSEdge edge : gssNode.getGSSEdges()) {
				String label = edge.getReturnSlot() == null ? "" : String.format(GSS_EDGE, edge.getReturnSlot()); 
				sb.append(label + "\"" + getId(gssNode) + "\"" + "->" + "{\"" + getId(edge.getDestination()) + "\"}" + "\n");				
			}
		}
	}

	private String getId(GSSNode node) {
		if (node instanceof org.iguana.datadependent.gss.GSSNode<?>) {
			org.iguana.datadependent.gss.GSSNode<?> gssNode = (org.iguana.datadependent.gss.GSSNode<?>) node;
			return node.getGrammarSlot() + "" + node.getInputIndex() + gssNode.getData();
		}
		return node.getGrammarSlot() + "" + node.getInputIndex();
	}
	
	public String getString() {
		return sb.toString();
	}
	
}
