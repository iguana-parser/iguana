package org.jgll.grammar.symbol;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;


public class Test {

	public static void main(String[] args) {
		
		Map<String, List<Set<Integer>>> map = Maps.newHashMap();
		map.put("a", Arrays.asList(Sets.newHashSet(1, 2, 3), Sets.newHashSet(4, 5)));

		ImmutableMap<String, List<Set<Integer>>> immutableMap = ImmutableMap.copyOf(map);
//		immutableMap.get("a").add(Sets.newHashSet(4));
		immutableMap.get("a").get(1).add(6);
		System.out.println(immutableMap);
		
	}
}
