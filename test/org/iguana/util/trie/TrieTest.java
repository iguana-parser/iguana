/*
 * Copyright (c) 2015, CWI
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

package org.iguana.util.trie;

import static org.iguana.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.iguana.util.trie.Node;
import org.iguana.util.trie.Trie;
import org.junit.Test;

public class TrieTest {
		
	@Test
	public void test1() {
		Trie<String> trie = new Trie<>();
		
		trie.addToRoot(list("du"));
		trie.addToRoot(list("du", "hast"));
		trie.addToRoot(list("du", "hast", "mich", "gefragt"));
		trie.addToRoot(list("und", "ich", "hab"));
		trie.addToRoot(list("und", "ich", "hab", "nichts", "gesagt"));
	}
	
	@Test
	public void test2() {
		Trie<String> trie = new Trie<>();
		trie.addToRoot(list("E", "*", "E"));
		trie.addToRoot(list("E", "+", "E"));
		trie.addToRoot(list("E", "-", "E"));
		trie.addToRoot(list("-", "E"));
		
		Node<String> node = trie.get(list("E"));
		assertEquals(3, node.size());
	}

}
