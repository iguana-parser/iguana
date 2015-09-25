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

import static org.iguana.util.generator.GeneratorUtil.*;
import static iguana.utils.visualization.GraphVizUtil.*;

import org.iguana.util.collections.rangemap.AVLIntRangeTree;
import org.iguana.util.collections.rangemap.RangeTree;
import org.iguana.util.collections.rangemap.AVLIntRangeTree.IntNode;
import org.iguana.util.collections.rangemap.RangeTree.Node;

public class RangeTreeToDot {
		
	public static <T> String toDot(RangeTree<T> g) {
		StringBuilder sb = new StringBuilder();
		toDot(sb, g.getRoot());
		return sb.toString();
	}
	
	private static <T> void toDot(StringBuilder sb, Node<T> node) {
		sb.append("\"" + node.toString() + "\"" + String.format(NODE, escape(node.toString()) + "\n"));
		
		if (node.getLeft() != null) {
			sb.append(EDGE + "\"" + node.toString() + "\"" + "->" + "{\"" + node.getLeft().toString() + "\"}" + "\n");
			toDot(sb, node.getLeft());
		}
		
		if (node.getRight() != null) {
			sb.append(EDGE + "\"" + node.toString() + "\"" + "->" + "{\"" + node.getRight().toString() + "\"}" + "\n");
			toDot(sb, node.getRight());
		}
	}
	
	
	public static <T> String toDot(AVLIntRangeTree g) {
		StringBuilder sb = new StringBuilder();
		toDot(sb, g.getRoot());
		return sb.toString();
	}
	
	private static <T> void toDot(StringBuilder sb, IntNode node) {
		sb.append("\"" + escape(node.toString()) + "\"" + String.format(NODE, escape(node.toString())) + "\n");
		
		if (node.getLeft() != null) {
			sb.append(EDGE + "\"" + escape(node.toString()) + "\"" + "->" + "{\"" + escape(node.getLeft().toString()) + "\"}" + "\n");
			toDot(sb, node.getLeft());
		}
		
		if (node.getRight() != null) {
			sb.append(EDGE + "\"" + escape(node.toString()) + "\"" + "->" + "{\"" + escape(node.getRight().toString()) + "\"}" + "\n");
			toDot(sb, node.getRight());
		}
	}

}
