/*
 * Copyright (c) 2015, CWI
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

package org.iguana.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectionsUtil {

	@SafeVarargs
	public static <T> Set<T> set(T...objects) {
		Set<T>  set = new HashSet<>();
		for(T t : objects) {
			set.add(t);
		}
		return set;
	}
	
	@SafeVarargs
	public static <T> List<T> list(T...objects){
		return Arrays.asList(objects);
	}
	
	public static <T> Set<T> union(Set<T> set1, Set<T> set2) {
		Set<T> set = new HashSet<>();
		set.addAll(set1);
		set.addAll(set2);
		return set;
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

}
