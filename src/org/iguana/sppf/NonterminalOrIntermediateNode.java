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

package org.iguana.sppf;

import java.util.ArrayList;
import java.util.List;


public abstract class NonterminalOrIntermediateNode extends NonPackedNode {

	protected PackedNode first;
	private List<PackedNode> rest;

	/*
	 * returns true if the second packed node of this nonterminal node is added.
	 *         This is useful for counting the number of ambiguous nodes.
	 */
	public boolean addPackedNode(PackedNode node) {
		if (first == null) {
			first = node;
			return false;
		}
		else if (rest == null) {
			rest = new ArrayList<>();
			rest.add(node);
			return true;
		} else {
			rest.add(node);
			return false;
		}
	}

	@Override
	public int getLeftExtent() {
		return first.getLeftExtent();
	}

	@Override
	public int getRightExtent() {
		return first.getRightExtent();
	}

	@Override
	public PackedNode getChildAt(int index) {
		if (index ==  0)
			return first;
		return index < rest.size() ? rest.get(index - 1) : null;
	}

	@Override
	public List<PackedNode> getChildren() {
		List<PackedNode> children = new ArrayList<>(childrenCount());
		if (first != null) children.add(first);
		if (rest != null) children.addAll(rest);
		return children;
	}

	@Override
	public int childrenCount() {
		return (first == null ? 0 : 1) + (rest == null ? 0 : rest.size());
	}

	public boolean isAmbiguous() {
		return rest != null;
	}

}