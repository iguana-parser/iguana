package org.jgll.util.hashing.hashfunction;

import java.util.Random;

import org.jgll.util.RandomUtil;
import org.jgll.util.hashing.IntegerHashKey4;
import org.junit.Test;


public class SimpleTabulationTest {
	
	@Test
	public void test() {
		
		SimpleTabulation8 f = SimpleTabulation8.getInstance();
		
		int[] result = new int[262144];
		
		Random rand = RandomUtil.random;
		
		int mask = (int) Math.pow(2, 18) - 1;;
		
		for(int i = 0; i < 100000; i++) {
			IntegerHashKey4 key = new IntegerHashKey4(rand.nextInt(Integer.MAX_VALUE), 
													  rand.nextInt(Integer.MAX_VALUE), 
													  rand.nextInt(Integer.MAX_VALUE), 
													  rand.nextInt(Integer.MAX_VALUE));
			
			int hash = f.hash(key.getK1(), key.getK2(), key.getK3(), key.getK4()) & mask;
//			System.out.println(hash);
			result[hash] = result[hash] + 1;
		}

		int max = 0;
		for(int i = 0; i < result.length; i++) {
			if(result[i] > max) {
				max = result[i];
			}
		}
		System.out.println(max);
		
	}

}
