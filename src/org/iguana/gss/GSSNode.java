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

package org.iguana.gss;

import iguana.utils.collections.Keys;
import iguana.utils.collections.hash.MurmurHash3;
import iguana.utils.collections.key.Key;
import iguana.utils.input.Input;
import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.parser.Runtime;
import org.iguana.result.Result;
import org.iguana.result.ResultOps;
import org.iguana.util.ParserLogger;

import java.util.*;

public class GSSNode<T extends Result> {

	private final NonterminalGrammarSlot slot;

	private final int inputIndex;

	private GSSEdge<T> firstGSSEdge;

	private List<GSSEdge<T>> restGSSEdges;

	private T firstPoppedElement;

	private Map<Key, T> restPoppedElements;

	private final Object[] data;

	public GSSNode(NonterminalGrammarSlot slot, int inputIndex) {
		this(slot, inputIndex, null);
	}

	public GSSNode(NonterminalGrammarSlot slot, int inputIndex, Object[] data) {
		this.slot = slot;
		this.inputIndex = inputIndex;
		this.data = data;
	}

	public void createGSSEdge(Input input, BodyGrammarSlot returnSlot, GSSNode<T> destination, T w, Environment env, Runtime<T> runtime) {
		GSSEdge<T> edge = new GSSEdge<>(returnSlot, w, destination, env);
		ParserLogger.getInstance().gssEdgeAdded(edge);

		if (firstGSSEdge == null) {
			firstGSSEdge = edge;
		} else {
			if (restGSSEdges == null) restGSSEdges = new ArrayList<>(4);
			restGSSEdges.add(edge);
		}

		iterateOverPoppedElements(edge, destination, input, env, runtime);
	}

	public boolean pop(Input input, EndGrammarSlot slot, T child, Runtime<T> runtime) {
		return pop(input, slot, child, null, runtime);
	}

	public boolean pop(Input input, EndGrammarSlot slot, T child, Object value, Runtime<T> runtime) {
		ParserLogger.getInstance().pop(this, inputIndex, child, value);
		T node = addPoppedElements(slot, child, value, runtime.getResultOps());
		if (node != null)
			iterateOverEdges(input, node, runtime);
		return node != null;
	}

	/**
	 * Returns the newly created popped element, or null if the node already exists
	 */
	private T addPoppedElements(EndGrammarSlot slot, T child, Object value, ResultOps<T> ops) {
		// No node added yet
		if (firstPoppedElement == null) {
			firstPoppedElement = ops.convert(null, child, slot, value);
			return firstPoppedElement;
		} else {
			int rightIndex = child.getIndex();

			// Only one node is added and there is an ambiguity
			if (rightIndex == firstPoppedElement.getIndex() && Objects.equals(value, firstPoppedElement.getValue())) {
				ops.convert(firstPoppedElement, child, slot, value);
				return null;
			} else {
				Key key = value == null ? Keys.from(rightIndex) : Keys.from(rightIndex, value);

				if (restPoppedElements == null) {
					restPoppedElements = new HashMap<>(8);
					T poppedElement = ops.convert(null, child, slot, value);
					restPoppedElements.put(key, poppedElement);
					return poppedElement;
				}

				T poppedElement = restPoppedElements.get(key);
				if (poppedElement == null) {
					poppedElement = ops.convert(null, child, slot, value);
					restPoppedElements.put(key, poppedElement);
					return poppedElement;
				}

				ops.convert(poppedElement, child, slot, value);
				return null;
			}
		}
	}

	private void iterateOverPoppedElements(GSSEdge<T> edge, GSSNode<T> destination, Input input, Environment env, Runtime<T> runtime) {
		if (firstPoppedElement != null)
			processPoppedElement(firstPoppedElement, edge, destination, input, env, runtime);

		if (restPoppedElements != null) {
			for (T poppedElement: restPoppedElements.values()) {
				processPoppedElement(poppedElement, edge, destination, input, env, runtime);
			}
		}
	}

	private void processPoppedElement(T poppedElement, GSSEdge<T> edge, GSSNode<T> destination, Input input, Environment env, Runtime<T> runtime) {
		BodyGrammarSlot returnSlot = edge.getReturnSlot();
		if (returnSlot.testFollow(input.charAt(poppedElement.getIndex()))) {
			T result = edge.addDescriptor(input, this, poppedElement, runtime);
			if (result != null) {
				runtime.scheduleDescriptor(returnSlot, destination, result, env);
			}
		}
	}

	private void iterateOverEdges(Input input, T result, Runtime<T> runtime) {
		if (firstGSSEdge != null)
			processEdge(input, result, firstGSSEdge, runtime);

		if (restGSSEdges != null)
			for (int i = 0; i < restGSSEdges.size(); i++) {
				GSSEdge<T> edge = restGSSEdges.get(i);
				processEdge(input, result, edge, runtime);
			}
	}

	private void processEdge(Input input, T node, GSSEdge<T> edge, Runtime<T> runtime) {
		if (!edge.getReturnSlot().testFollow(input.charAt(node.getIndex()))) return;

		T result = edge.addDescriptor(input, this, node, runtime);
		if (result != null) {
			Environment env = runtime.getEnvironment();
			runtime.scheduleDescriptor(edge.getReturnSlot(), edge.getDestination(), result, env);
		}
	}

	public Result getResult(int j) {
		if (firstPoppedElement != null && firstPoppedElement.getIndex() == j)
			return firstPoppedElement;

		if (restPoppedElements != null) {
			return restPoppedElements.get(Keys.from(j));
		}
		return null;
	}

	public NonterminalGrammarSlot getGrammarSlot() {
		return slot;
	}

	public int getInputIndex() {
		return inputIndex;
	}

	public Object[] getData() {
		return data;
	}

	public int countGSSEdges() {
		int count = 0;
		count += firstGSSEdge == null ? 0 : 1;
		count += restGSSEdges == null ? 0 : restGSSEdges.size();
		return count;
	}

	public int countPoppedElements() {
		int count = 0;
		if (firstPoppedElement != null) count++;
		if (restPoppedElements != null) count += restPoppedElements.size();
		return count;
	}

	public Iterable<GSSEdge<T>> getGSSEdges() {
		return restGSSEdges;
	}

	public boolean equals(Object obj) {
		if(this == obj) return true;

		if (!(obj instanceof GSSNode)) return false;

		GSSNode<?> other = (GSSNode<?>) obj;

		return  slot == other.getGrammarSlot() &&
				inputIndex == other.getInputIndex() &&
				Arrays.equals(data, other.data);
	}

	public int hashCode() {
		return MurmurHash3.fn().apply(slot.hashCode(), getInputIndex(), data);
	}

	public List<Result> getPoppedElements() {
		List<Result> poppedElements = new ArrayList<>(countPoppedElements());
		if (firstPoppedElement != null) poppedElements.add(firstPoppedElement);
		if (restPoppedElements != null)
			poppedElements.addAll(restPoppedElements.values());

		return poppedElements;
	}

	public int getCountGSSEdges() {
		int count = 0;
		if (firstGSSEdge != null) count++;
		if (restGSSEdges != null) count += restGSSEdges.size();
		return count;
	}

	public String toString() {
		String s = String.format("(%s, %d)", slot, inputIndex);
		if (data != null) {
			s += String.format("(%s)", data);
		}
		return s;
	}

}