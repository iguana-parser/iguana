package org.jgll.util.trie;

import org.junit.Before;
import org.junit.Test;

import static org.jgll.util.CollectionsUtil.*;
import static org.junit.Assert.*;

public class TrieTest {
	
	private ExternalEqual<String> stringizer;

	@Before
	public void init() {
		stringizer = new ExternalEqual<String>() {

			@Override
			public boolean isEqual(String s1, String s2) {
				return s1.equals(s2);
			}
		};
	}
	
	@Test
	public void test1() {
		Trie<String> trie = new Trie<>(stringizer);
		
		trie.add("du");
		trie.add(list("du", "hast"));
		trie.add(list("du", "hast", "mich", "gefragt"));
		trie.add(list("und", "ich", "hab"));
		trie.add(list("und", "ich", "hab", "nichts", "gesagt"));
	}
	
	@Test
	public void test2() {
		Trie<String> trie = new Trie<>(stringizer);
		trie.add(list("E", "*", "E"));
		trie.add(list("E", "+", "E"));
		trie.add(list("E", "-", "E"));
		trie.add(list("-", "E"));
		
		Node<String> node = trie.get(list("E"));
		assertEquals(3, node.size());
	}

}
