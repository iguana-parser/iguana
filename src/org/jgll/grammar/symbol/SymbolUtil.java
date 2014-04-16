package org.jgll.grammar.symbol;

import java.util.ArrayList;
import java.util.List;

public class SymbolUtil {
	
	@SuppressWarnings("unchecked")
	public static <T extends Symbol> List<T> cloneList(List<T> symbols) {
		List<T> list = new ArrayList<>();
		for(T regex : symbols) {
			list.add((T) regex.clone());
		}
		
		return list;
	}

}
