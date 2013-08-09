package org.jgll.util.hashing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BenchmarkHashFunctions {
	private final HashFunction[] targets;
	public BenchmarkHashFunctions(HashFunction... functions) {
		targets = functions;
	}

	public void bench(int size, int iterations) {
		final int[][] data = generateData(size);
		// warmup
		int r = run5(data);
		r += run4(data);
		r += run3(data);
		System.out.println("Warmed up: " + r);
		
		for (HashFunction f : this.targets) {
			System.out.println(f.getClass().getName());
			List<Long> measurements = new ArrayList<>();
			for (int it = 0; it < iterations; it++) {
				long start = System.nanoTime();
				r = 0;
				for (int i = 0; i < data.length; i++) {
					int[] d = data[i];
					r += f.hash(d[0], d[1], d[2]);
				}
				long stop = System.nanoTime();
				measurements.add((stop - start)/(1000*1000));
			}
			printAvgStdDev("\t 3 numbers", measurements);
			
			measurements = new ArrayList<>();
			for (int it = 0; it < iterations; it++) {
				long start = System.nanoTime();
				r = 0;
				for (int i = 0; i < data.length; i++) {
					int[] d = data[i];
					r += f.hash(d[0], d[1], d[2], d[3]);
				}
				long stop = System.nanoTime();
				measurements.add((stop - start)/(1000*1000));
			}
			printAvgStdDev("\t 4 numbers", measurements);
			
			measurements = new ArrayList<>();
			for (int it = 0; it < iterations; it++) {
				long start = System.nanoTime();
				r = 0;
				for (int i = 0; i < data.length; i++) {
					int[] d = data[i];
					r += f.hash(d[0], d[1], d[2], d[3],d[4]);
				}
				long stop = System.nanoTime();
				measurements.add((stop - start)/(1000*1000));
			}
			printAvgStdDev("\t 5 numbers", measurements);
		}
		
	}
	
	private void printAvgStdDev(String name, List<Long> measurements) {
		long sum = 0;
		for (Long l : measurements) {
			sum += l;
		}
		double avg = sum / (double)measurements.size();
		double sd = 0;
		for (Long l : measurements) {
		    sd = sd + (l - avg)*(l - avg);
		}
		sd = sd / (measurements.size() - 1);
		System.out.println(String.format("%s\t %.2f (%.2f)", name, avg, sd));
		
	}

	private long measure(Runnable x) {
		long start = System.nanoTime();
		x.run();
		long stop = System.nanoTime();
		return (stop - start)/(1000*1000);
	}
	

	private int run3(int[][] data) {
		int result = 0;
		for (HashFunction f : this.targets) {
			for (int i = 0; i < data.length; i++) {
				int[] d = data[i];
				result += f.hash(d[0], d[1], d[2]);
			}
		}
		return result;
	}


	private int run4(int[][] data) {
		int result = 0;
		for (HashFunction f : this.targets) {
			for (int i = 0; i < data.length; i++) {
				int[] d = data[i];
				result += f.hash(d[0], d[1], d[2]);
			}
		}
		return result;
	}


	private int run5(int[][] data) {
		int result = 0;
		for (HashFunction f : this.targets) {
			for (int i = 0; i < data.length; i++) {
				int[] d = data[i];
				result += f.hash(d[0], d[1], d[2], d[3], d[4]);
			}
		}
		return result;
	}


	private int[][] generateData(int size) {
		Random r = new Random(size);
		int[][] result = new int[size][5];
		for (int i=0; i < size; i++) {
			result[i][0] = r.nextBoolean() ? r.nextInt(0xFF) : r.nextInt(0xFFFF);
			result[i][1] = r.nextInt(5) <= 3 ? r.nextInt(0xFFF) : r.nextInt(0xFFFF);
			result[i][2] = r.nextBoolean() ? r.nextInt(0xFFFF) : r.nextInt(0x1FFFF);
			result[i][3] = r.nextBoolean() ? r.nextInt(0xFFF) : r.nextInt(0x3FFFF);
			result[i][4] = r.nextInt(Integer.MAX_VALUE);
		}
		return result;
	}

	
}
