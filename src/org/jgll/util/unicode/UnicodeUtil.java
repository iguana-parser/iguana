package org.jgll.util.unicode;

import java.util.Map;
import java.util.Set;

public class UnicodeUtil {
	
	private static final int MAX_UTF32_VAL = 0x10FFFF;
	
	private Map<String, Set<Integer>> map;

	public static void main(String[] args) {
		
		for (int i = 0; i <= MAX_UTF32_VAL; i++) {
			System.out.println(Character.getName(i));
//			switch (Character.getType(i)) {
//			}
		}
		
		System.out.println(Character.UnicodeBlock.of('a'));
		
	}
	
}
