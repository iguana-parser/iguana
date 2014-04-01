package org.jgll.util.trie;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class TrieTest {
		
	@Test
	public void test1() {
		Trie<String> trie = new Trie<>();
		
		trie.add("du");
		trie.add(list("du", "hast"));
		trie.add(list("du", "hast", "mich", "gefragt"));
		trie.add(list("und", "ich", "hab"));
		trie.add(list("und", "ich", "hab", "nichts", "gesagt"));
	}
	
	@Test
	public void test2() {
		Trie<String> trie = new Trie<>();
		trie.add(list("E", "*", "E"));
		trie.add(list("E", "+", "E"));
		trie.add(list("E", "-", "E"));
		trie.add(list("-", "E"));
		
		Node<String> node = trie.get(list("E"));
		assertEquals(3, node.size());
	}

}
