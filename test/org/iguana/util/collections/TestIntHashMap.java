package org.iguana.util.collections;

import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

public class TestIntHashMap {
	
	int max = 1000_000;
	
	@SuppressWarnings("deprecation")
	@Test
	public void test() {
		Random rand = new Random();

		List<Integer> keys = new ArrayList<>();
		
		for (int i = 0; i < 100_000; i++) {
			keys.add(rand.nextInt(max));
		}

		for (int i = 0; i < 15; i++) {
			Map<Integer, String> map = new HashMap<>();
			keys.forEach(k -> map.put(k, k + ""));

			IntHashMap<String> imap = new ChainingIntHashMap<>();
			keys.forEach(k -> imap.put(k, k + ""));

			keys.forEach(k -> assertTrue(map.containsKey(k) && imap.containsKey(k) && map.get(k).equals(imap.get(k))));

		}

		
	}

}
