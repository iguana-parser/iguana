package org.jgll.util.hashing;

import java.util.Random;

import org.jgll.util.hashing.hashfunction.DavyHash;
import org.jgll.util.hashing.hashfunction.Jenkins;
import org.jgll.util.hashing.hashfunction.JenkinsCWI;
import org.jgll.util.hashing.hashfunction.MurmurHash2;
import org.jgll.util.hashing.hashfunction.MurmurHash3;
import org.jgll.util.hashing.hashfunction.SuperFastHash;
import org.jgll.util.hashing.hashfunction.SuperFastHash16BitOnly;
import org.jgll.util.hashing.hashfunction.XXHash;

public class RunBenchmark {
	public static void main(String[] args) {
		Random r = new Random();
		BenchmarkHashFunctions bencher = new BenchmarkHashFunctions(
				new MurmurHash2(r.nextInt())
				, new MurmurHash3(r.nextInt())
				, new DavyHash(r.nextInt())
				, new XXHash(r.nextInt())
				, new SuperFastHash(r.nextInt())
				, new SuperFastHash16BitOnly(r.nextInt())
				, new Jenkins(r.nextInt())
				, new JenkinsCWI(r.nextInt())
		);
		
		bencher.bench(1*1000*1000, 100);
		bencher.bench(3*1000*1000, 100);
	}

}
