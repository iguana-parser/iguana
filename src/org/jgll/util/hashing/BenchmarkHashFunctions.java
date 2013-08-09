package org.jgll.util.hashing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BenchmarkHashFunctions {
	private final HashFunction[] targets;
	public BenchmarkHashFunctions(HashFunction... functions) {
		targets = functions;
	}
	class FiveNumbers {
		public int a,b,c,d,e;
		
		public FiveNumbers(Random r) {
			fillA(r);
			fillB(r);
			fillC(r);
			fillD(r);
			fillE(r);
		}
		
		private void fillA(Random r) {
			a = r.nextBoolean() ? r.nextInt(0xFF) : r.nextInt(0xFFFF);
		}
		private void fillB(Random r) {
			b = r.nextInt(5) <= 3 ? r.nextInt(0xFFF) : r.nextInt(0xFFFF);
		}
		private void fillC(Random r) {
			c = r.nextBoolean() ? r.nextInt(0xFFFF) : r.nextInt(0x1FFFF);
		}
		private void fillD(Random r) {
			d = r.nextBoolean() ? r.nextInt(0xFFF) : r.nextInt(0x3FFFF);
		}
		private void fillE(Random r) {
			e = r.nextInt(Integer.MAX_VALUE);
		}
	}
	public void bench(int size, int iterations) {
		final List<FiveNumbers> data = generateData(size);
		// warmup
		int r = run5(data);
		r += run4(data);
		r += run3(data);
		System.out.println("Warmed up: " + r);
		
		for (final HashFunction f : this.targets) {
			System.out.println(f.getClass().getName());
			List<Long> measurements = new ArrayList<>();
			for (int i = 0; i < iterations; i++) {
				measurements.add(measure(new Runnable() {
					@Override
					public void run() {
						for (FiveNumbers fn: data) {
							f.hash(fn.a, fn.b, fn.c);
						}
					}
				}));
			}
			printAvgStdDev("\t 3 numbers", measurements);
			
			measurements = new ArrayList<>();
			for (int i = 0; i < iterations; i++) {
				measurements.add(measure(new Runnable() {
					@Override
					public void run() {
						for (FiveNumbers fn: data) {
							f.hash(fn.a, fn.b, fn.c, fn.d);
						}
					}
				}));
			}
			printAvgStdDev("\t 4 numbers", measurements);
			
			measurements = new ArrayList<>();
			for (int i = 0; i < iterations; i++) {
				measurements.add(measure(new Runnable() {
					@Override
					public void run() {
						for (FiveNumbers fn: data) {
							f.hash(fn.a, fn.b, fn.c, fn.d, fn.e);
						}
					}
				}));
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
	

	private int run3(List<FiveNumbers> data) {
		int result = 0;
		for (HashFunction f : this.targets) {
			for (FiveNumbers fn: data) {
				result += f.hash(fn.a, fn.b, fn.c);
			}
		}
		return result;
	}


	private int run4(List<FiveNumbers> data) {
		int result = 0;
		for (HashFunction f : this.targets) {
			for (FiveNumbers fn: data) {
				result += f.hash(fn.a, fn.b, fn.c, fn.d);
			}
		}
		return result;
	}


	private int run5(List<FiveNumbers> data) {
		int result = 0;
		for (HashFunction f : this.targets) {
			for (FiveNumbers fn: data) {
				result += f.hash(fn.a, fn.b, fn.c, fn.d, fn.e);
			}
		}
		return result;
	}


	private List<FiveNumbers> generateData(int size) {
		Random r = new Random(size);
		List<FiveNumbers> result = new ArrayList<>(size);
		for (int i=0; i < size; i++) {
			result.add(new FiveNumbers(r));
		}
		return result;
	}
	
	
	
}
