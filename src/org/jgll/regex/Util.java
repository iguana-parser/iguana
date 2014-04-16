package org.jgll.regex;

import java.util.ArrayList;
import java.util.List;

public class Util {
	
	@SuppressWarnings("unchecked")
	public static <T extends RegularExpression> List<T> cloneList(List<T> regularExpressions) {
		List<T> list = new ArrayList<>();
		for(T regex : regularExpressions) {
			list.add((T) regex.clone());
		}
		
		return list;
	}

}
