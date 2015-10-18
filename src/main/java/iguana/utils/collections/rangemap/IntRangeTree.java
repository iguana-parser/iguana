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

package iguana.utils.collections.rangemap;

import iguana.utils.collections.rangemap.AVLIntRangeTree.IntNode;

import java.util.function.Consumer;
import java.util.function.Function;

public interface IntRangeTree {
	
	int ABSENT_VALUE = -2;
	
    IntNode getRoot();
	
	int size();
	
	default boolean contains(int key) {
		return get(key) != ABSENT_VALUE;
	}

	boolean contains(Range range);
	
	int get(int key);
	
	void insert(Range range, int val);
	
	boolean isBalanced();
	
	<T> void inOrder(Function<IntNode, ? extends T> action, Consumer<? super T> acc);
	
	<T> void preOrder(Function<IntNode, ? extends T> action, Consumer<? super T> acc);
	
    <T> void levelOrder(Function<IntNode, ? extends T> action, Consumer<? super T> acc);
	
	default void inOrder(Consumer<IntNode> action) {
		inOrder(n -> { action.accept(n); return null; }, n -> {});
	}
	
	default void preOrder(Consumer<IntNode> action) {
		preOrder(n -> { action.accept(n); return null; }, n -> {});
	}
	
	default void levelOrder(Consumer<IntNode> action) {
		levelOrder(n -> { action.accept(n); return null; }, n -> {});
	}

	default int height() {
		return getRoot() == null ? 0 : getRoot().height;
	}

}
