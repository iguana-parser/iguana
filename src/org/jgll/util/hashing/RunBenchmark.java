package org.jgll.util.hashing;

import java.util.Random;

public class RunBenchmark {
	public static void main(String[] args) {
		Random r = new Random();
		BenchmarkHashFunctions bencher = new BenchmarkHashFunctions(
				new MurmurHash2(r.nextInt())
				, new MurmurHash3(r.nextInt())
		);
		
		bencher.bench(5*1000*1000, 100);
	}

}
