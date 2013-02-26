package org.jgll.util;

import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalSymbolNode;

public class ToDotWithoutIntermeidateAndLists extends ToDotWithoutIntermediateNodes {

	public ToDotWithoutIntermeidateAndLists(StringBuilder sb) {
		super(sb);
	}
	
	@Override
	public void visit(ListSymbolNode node) {
		removeListSymbolNode(node);
		visit((NonterminalSymbolNode)node);
	}

}
