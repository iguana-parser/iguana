package iguana.utils.collections;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class TestIntHashMap {
	
	int max = 1000_000;
	
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

			keys.forEach(k -> Assert.assertTrue(map.containsKey(k) && imap.containsKey(k) && map.get(k).equals(imap.get(k))));

		}

		
	}

}
