package org.jgll.util.hashing.hashfunction;

import java.util.Random;

import org.jgll.util.RandomUtil;
import org.jgll.util.hashing.IntegerHashKey4;
import org.junit.Test;


public class SimpleTabulationTest {
	
	@Test
	public void test() {
		
		SimpleTabulation f = new SimpleTabulation(6);
		
		int[] result = new int[64];
		
		Random rand = RandomUtil.random;
		for(int i = 0; i < 10000; i++) {
			IntegerHashKey4 key = new IntegerHashKey4(rand.nextInt(Integer.MAX_VALUE), 
													  rand.nextInt(Integer.MAX_VALUE), 
													  rand.nextInt(Integer.MAX_VALUE), 
													  rand.nextInt(Integer.MAX_VALUE));
			
			result[f.hash(key.getK1(), key.getK2(), key.getK3(), key.getK4())]++;
//			System.out.println(f.hash(key.getK1(), key.getK2(), key.getK3(), key.getK4()));
		}

		for(int i = 0; i < result.length; i++) {
			System.out.println(result[i]);
		}
		
	}

}
