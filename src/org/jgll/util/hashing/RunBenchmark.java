package org.jgll.util.hashing;

import java.util.Random;

public class RunBenchmark {
	public static void main(String[] args) {
		Random r = new Random();
		BenchmarkHashFunctions bencher = new BenchmarkHashFunctions(
				new MurmurHash2(r.nextInt())
				, new MurmurHash3(r.nextInt())
				, new SuperFastHash(r.nextInt())
		);
		
		bencher.bench(1*1000*1000, 100);
		bencher.bench(2*1000*1000, 100);
		bencher.bench(3*1000*1000, 100);
		bencher.bench(4*1000*1000, 100);
	}

}
