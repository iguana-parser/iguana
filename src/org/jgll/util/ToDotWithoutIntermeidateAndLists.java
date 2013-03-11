package org.jgll.util;

import org.jgll.sppf.ListSymbolNode;
import org.jgll.sppf.NonterminalSymbolNode;

public class ToDotWithoutIntermeidateAndLists extends ToDotWithoutIntermediateNodes {
	
	@Override
	public void visit(ListSymbolNode node, StringBuilder sb) {
		removeListSymbolNode(node);
		visit((NonterminalSymbolNode)node, sb);
	}

}
