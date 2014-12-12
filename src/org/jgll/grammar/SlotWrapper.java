package org.jgll.grammar;

import java.io.PrintWriter;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.lexer.Lexer;
import org.jgll.parser.GLLParser;

public class SlotWrapper extends BodyGrammarSlot {

	private final int id;

	private final BodyGrammarSlot slot;
	
	public SlotWrapper(int id, BodyGrammarSlot slot) {
		super(slot.toString(), slot.previous(), slot.getPreConditions(), 
			  slot.getPostConditions(), slot.getPopConditions(),
			  slot.getNodeCreator(), slot.getNodeCreatorFromPop());
		this.id = id;
		this.slot = slot;
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, Lexer lexer) {
		return slot.parse(parser, lexer);
	}

	@Override
	public void code(PrintWriter writer, GrammarSlotRegistry registry) {
		slot.code(writer, registry);
	}

	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return slot.getConstructorCode(registry);
	}

	@Override
	public Symbol getSymbol() {
		return slot.getSymbol();
	}

	@Override
	public boolean isNullable() {
		return slot.isNullable();
	}

	@Override
	public int getId() {
		return id;
	}
}
