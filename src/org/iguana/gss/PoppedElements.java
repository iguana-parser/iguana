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
import iguana.utils.collections.key.Key;
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.result.ResultOps;
import org.iguana.util.Holder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class PoppedElements<T> implements Iterable<T> {

    private ResultOps<T> ops;

    private T firstResult;

	private Map<Key, T> poppedElements;

    PoppedElements(ResultOps<T> ops) {
        this.ops = ops;
    }

	public T add(EndGrammarSlot<T> slot, T result, Object value) {
		// No node added yet
		if (firstResult == null) {
            firstResult = ops.convert(null, result, slot, value);
            return firstResult;
        } else {
            int rightIndex = ops.getRightIndex(result);
            Key key = value == null ? Keys.from(rightIndex) : Keys.from(rightIndex, value);
            // Only one node is added and there is an ambiguity

            if (rightIndex == ops.getRightIndex(firstResult) && Objects.equals(value, ops.getValue(firstResult))) {
                ops.convert(firstResult, result, slot, value);
                return null;
            } else {
                // Initialize the map and put the firstResult element there
                if (poppedElements == null) {
                    poppedElements = new HashMap<>();
                }

                Holder<T> holder = new Holder<>();
                poppedElements.compute(key, (k, v) -> {
                    if (v == null) {
                        T node = ops.convert(null, result, slot, value);
                        holder.set(node);
                        return node;
                    } else {
                        ops.convert(v, result, slot, value);
                        return v;
                    }
                });
                return holder.get();
            }
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

	private PoppedElementIterator it = new PoppedElementIterator();

    @Override
    public Iterator<T> iterator() {
        return it.reset();
    }

    class PoppedElementIterator implements Iterator<T> {

        private boolean iteratedFirst;
        private Iterator<T> poppedElementsIterator;

        PoppedElementIterator() {
            reset();
        }

        @Override
        public boolean hasNext() {
            if (!iteratedFirst && firstResult != null)
                return true;
            return poppedElements != null && poppedElementsIterator.hasNext();
        }

        @Override
        public T next() {
            if (!iteratedFirst) {
                iteratedFirst = true;
                return firstResult;
            }
            return poppedElementsIterator.next();
        }

        public PoppedElementIterator reset() {
            iteratedFirst = false;
            if (poppedElements != null)
                poppedElementsIterator = poppedElements.values().iterator();
            return this;
        }
    }
}
