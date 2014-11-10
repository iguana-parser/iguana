package org.jgll.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import static org.jgll.util.generator.GeneratorUtil.*;

public class GeneratorUtilTest {

	@Test
	public void testListToString() {
		List<Integer> elements = new ArrayList<>();
		elements.add(1);
		elements.add(2);
		elements.add(3);
		elements.add(4);
		elements.add(5);
		String s = listToString(elements);
		Assert.assertEquals("1 2 3 4 5", s);
	}
	
	@Test
	public void testListToStringSep() {
		List<Integer> elements = new ArrayList<>();
		elements.add(1);
		elements.add(2);
		elements.add(3);
		elements.add(4);
		elements.add(5);
		String s = listToString(elements, ",");
		Assert.assertEquals("1,2,3,4,5", s);
	}

	
}
