package org.jgll.grammar.slot;

import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.grammar.Terminal;
import org.jgll.grammar.TerminalGrammarSlot;

public class FirstTerminalGrammarSlot extends TerminalGrammarSlot {

	private static final long serialVersionUID = 1L;

	public FirstTerminalGrammarSlot(String label, Terminal terminal, HeadGrammarSlot head) {
		super(label, 0, null, terminal, head);
	}
	
}
