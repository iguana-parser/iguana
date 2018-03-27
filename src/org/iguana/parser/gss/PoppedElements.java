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

package org.iguana.parser.gss;

import iguana.utils.collections.IntHashMap;
import iguana.utils.collections.Keys;
import iguana.utils.collections.OpenAddressingIntHashMap;
import iguana.utils.collections.key.Key;
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.parser.descriptor.ResultOps;
import org.iguana.util.Holder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class PoppedElements<T> {

    private ResultOps<T> ops;

    private T firstResult;

	private IntHashMap<T> poppedElements;

    PoppedElements(ResultOps<T> ops) {
        this.ops = ops;
    }

	public T add(EndGrammarSlot slot, T result) {

		// No node added yet
		if (firstResult == null) {
			firstResult = ops.convert(null, result, slot, null);
			return firstResult;
		// Only one node is added and there is an ambiguity
		} else if (poppedElements == null && ops.getRightIndex(firstResult) == ops.getRightIndex(result)) {
		    ops.convert(firstResult, result, slot, null);
			return null;
        // Only one node is added and the node is not in the map
		} else {
			// Initialize the map and put the firstResult element there
			if (poppedElements == null) {
				poppedElements = new OpenAddressingIntHashMap<>();
				poppedElements.put(ops.getRightIndex(firstResult), firstResult);
			}

			return poppedElements.compute(ops.getRightIndex(result), (k, v) -> {
                if (v == null) {
                    return ops.convert(null, result, slot, null);
                } else {
                    ops.convert(v, result, slot, null);
                    return v;
                }
            });
		}
	}

	public T add(EndGrammarSlot slot, T result, Object value) {
    	throw new UnsupportedOperationException();
//		// No node added yet
//		if (firstResult == null) {
//            firstResult = ops.convert(null, result, slot, value);
//            return firstResult;
//        } else {
//            Key key = Keys.from(ops.getRightIndex(result), value);
//            // Only one node is added and there is an ambiguity
//            if (poppedElements == null && Keys.from(ops.getRightIndex(firstResult), ops.getValue(firstResult)).equals(key)) {
//                ops.convert(firstResult, result, slot, value);
//                return null;
//            } else {
//                // Initialize the map and put the firstResult element there
//                if (poppedElements == null) {
//                    poppedElements = new HashMap<>();
//                    poppedElements.put(Keys.from(ops.getRightIndex(firstResult), ops.getValue(firstResult)), firstResult);
//                }
//
//                Holder<T> holder = new Holder<>();
//                poppedElements.compute(key, (k, v) -> {
//                    if (v == null) {
//                        T node = ops.convert(null, result, slot, value);
//                        holder.set(node);
//                        return node;
//                    } else {
//                        ops.convert(v, result, slot, value);
//                        return v;
//                    }
//                });
//                return holder.get();
//            }
//        }
	}

	public void forEach(Consumer<T> c) {
		if (poppedElements == null) {
			if (firstResult != null) c.accept(firstResult);
		} else {
			poppedElements.values().forEach(c);
		}
	}

	public T getResult(int j) {
		if (poppedElements == null) {
			if (firstResult != null && ops.getRightIndex(firstResult) == j)
				return firstResult;
			else
				return null;
		}
		return poppedElements.get(j);
	}

	public int size() {
		if (poppedElements == null) {
			return firstResult != null ? 1 : 0;
		} else {
			return poppedElements.size();
		}
	}
}
