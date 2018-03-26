/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this 
 *    list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 *
 */

package iguana.utils.collections;

import iguana.utils.collections.primitive.IntIterable;
import iguana.utils.collections.primitive.IntIterator;
import iguana.utils.collections.tuple.Tuple;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;
import java.util.stream.StreamSupport;

public class CollectionsUtil {

	@SafeVarargs
	public static <T> Set<T> set(T...objects) {
		if (objects.length == 0) return Collections.emptySet();
		
		Set<T>  set = new HashSet<>();
		for (T t : objects) {
			set.add(t);
		}
		return set;
	}
	
	@SafeVarargs
	public static <T> Set<T> immutableSet(T...objects) {
		return Collections.unmodifiableSet(set(objects));
	}
	
	@SafeVarargs
	public static <T> List<T> list(T...objects){
		if (objects.length == 0) return Collections.emptyList();
		
		return Arrays.asList(objects);
	}
	
	@SafeVarargs
	public static <T> List<T> immutableList(T...objects){
		return Collections.unmodifiableList(list(objects));
	}
	
	public static <K, V> Tuple<K, V> tuple(K k, V v) {
		return Tuple.of(k, v);
	}
	
	public static <K, V> Map<K, V> map(List<Tuple<K, V>> mappings) {
		return mappings.stream().collect(Collectors.toMap(t -> t.getFirst(), t -> t.getSecond()));
	}
	
	public static <K, V> Map<K, V> map(List<K> keys, List<V> values) {	
		return zip(keys.stream(), values.stream(), (k, v) -> tuple(k, v)).collect(Collectors.toMap(t -> t.getFirst(), t -> t.getSecond()));		
	}
	
	public static <A, B, C> Stream<C> zip(Stream<A> as, Stream<B> bs, BiFunction<A, B, C> f) {
		Iterator<A> asIterator = as.iterator();
		Iterator<B> bsIterator = bs.iterator();
		
		Builder<C> builder = Stream.builder();
		
		while(asIterator.hasNext() && bsIterator.hasNext()) {
			builder.add(f.apply(asIterator.next(), bsIterator.next()));
		}
		
		return builder.build();
	}

	public static <T> Set<T> union(Set<T> set1, Set<T> set2) {
		Set<T> set = new HashSet<>();
		set.addAll(set1);
		set.addAll(set2);
		return set;
	}

	public static <T> T first(List<T> list) {
		if (list == null || list.isEmpty())
			throw new IllegalArgumentException("List is empty");
		return list.get(0);
	}

	public static <T> T last(List<T> list) {
		if (list == null || list.isEmpty())
			throw new IllegalArgumentException("List is empty");
		return list.get(list.size() - 1);
	}

    public static boolean isEqual(IntIterable iterables1, IntIterable iterables2) {
        IntIterator it1 = iterables1.iterator();
        IntIterator it2 = iterables2.iterator();
        while (it1.hasNext()) {
            if (!it2.hasNext()) return false;
            int t1 = it1.next();
            int t2 = it2.next();
            if (t1 != t2) return false;
        }
        return true;
    }

	public static <T> boolean isEqual(Iterable<T> iterables1, Iterable<T> iterables2) {
		Iterator<T> it1 = iterables1.iterator();
		Iterator<T> it2 = iterables2.iterator();
		while (it1.hasNext()) {
			if (!it2.hasNext()) return false;
			T t1 = it1.next();
			T t2 = it2.next();
			if (!t1.equals(t2)) return false;
		}
		return true;
	}
	
	public static <T> Iterable<T> concat(Iterable<T> first, Iterable<T> second) {
		
		return new Iterable<T>() {
			
			final Iterator<T> it1 = first.iterator();
			final Iterator<T> it2 = second.iterator();

			@Override
			public Iterator<T> iterator() {
				
				return new Iterator<T>() {

					Iterator<T> curr;

					@Override
					public boolean hasNext() {
						if (it1.hasNext()) {
							curr = it1;
							return true;
						}
						else if (it2.hasNext()) {
							curr = it2;
							return true;
						}
						return false;
					}

					@Override
					public T next() {
						return curr.next();
					}
				};
			}
		};
	}
 	
	public static <T> List<List<T>> split(List<T> list, int size) {
		List<List<T>> result = new ArrayList<>();
		
		int n = list.size() / size;
		int r = list.size() % size;
		
		for (int i = 0; i < n; i++) {
			List<T> split = new ArrayList<>();
			for (int j = i * size; j < i * size + size; j++) {
				split.add(list.get(j));
			}
			result.add(split);
		}

		if (r > 0) {
			List<T> rest = new ArrayList<>();
			for (int i = n * size; i < list.size(); i++) {
				rest.add(list.get(i));
			}
			result.add(rest);			
		}
		
		return result;
	}
	
	public static <T> List<T> toList(Iterable<T> it) {
		return StreamSupport.stream(it.spliterator(), false).collect(Collectors.toList());
	}

	public static int max(int[] a) {
	    int max = Integer.MIN_VALUE;
	    for (int i = 0; i < a.length; i++)
	        if (a[i] > max)
	            max = a[i];
	    return max;
    }

    public static <T> List<T> concat(List<T> list1, List<T> list2) {
		List<T> list = new ArrayList<>(list1.size() + list2.size());
		list.addAll(list1);
		list.addAll(list2);
		return list;
	}

}
