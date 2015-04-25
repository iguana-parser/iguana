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
