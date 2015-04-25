package org.iguana.util.collections;

import static org.junit.Assert.*;

import org.iguana.util.collections.IntRangeTree;
import org.junit.Test;

public class IntRangeTest {
	
	@Test
	public void test() {
		IntRangeTree tree = new IntRangeTree();
		tree.insert('.', 1);
		tree.insert('$', 2);
		tree.insert('&', 3);
		tree.insert('*', 4);
		tree.insert(':', 5);
		tree.insert('<', 6);
		tree.insert('>', 7);
		tree.insert('@', 8);
		tree.insert('\\', 9);
		tree.insert('^', 10);
		tree.insert('|', 11);
		tree.insert('~', 12);
		tree.insert('!', 13);
		tree.insert('#', 14);
		tree.insert('%', 15);
		tree.insert('+', 16);
		tree.insert('-', 17);
		tree.insert('/', 18);
		tree.insert('=', 19);
		tree.insert('?', 20);
		assertEquals(20, tree.size());
		assertEquals(4, tree.getRoot().getHeight());
	}
	
	@Test
	public void test5() {
		IntRangeTree tree = new IntRangeTree();
		tree.insert(44, 1);
		tree.insert(17, 2);
		tree.insert(32, 3);
		tree.insert(78, 4);
		tree.insert(50, 5);
		tree.insert(48, 6);
		tree.insert(62, 7);
		tree.insert(54, 8);
		tree.insert(88, 9);
		assertEquals(9, tree.size());
		assertEquals(3, tree.getRoot().getHeight());
	}
	
	@Test
	public void test6() {
		IntRangeTree tree = new IntRangeTree();
		tree.insert(14, 1);
		tree.insert(17, 2);
		tree.insert(11, 3);
		tree.insert(7,  4);
		tree.insert(53, 5);
		tree.insert(4,  6);
		tree.insert(13, 7);
		assertEquals(7, tree.size());
		assertEquals(3, tree.getRoot().getHeight());
	}
	
	@Test
	public void test7() {
		IntRangeTree tree = new IntRangeTree();
		tree.insert(3, 1);
		tree.insert(2, 2);
		tree.insert(1, 3);
		tree.insert(4, 4);
		tree.insert(5, 5);
		tree.insert(6, 6);
		tree.insert(7, 7);
		tree.insert(16, 8);
		tree.insert(15, 9);
		tree.insert(14, 10);
		assertEquals(10, tree.size());
		assertEquals(3, tree.getRoot().getHeight());
	}

}
