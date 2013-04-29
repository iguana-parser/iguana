package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;

import org.jgll.parser.GLLParser;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.util.Input;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class WrapperSlot extends BodyGrammarSlot {
	
	private static final long serialVersionUID = 1L;
	
	private BodyGrammarSlot slot;
	private SlotAction beforeAction;
	private SlotAction afterAction;
	
	public static WrapperSlot before(BodyGrammarSlot slot, SlotAction before) {
		return new WrapperSlot(slot, before, null);
	}

	public static WrapperSlot after(BodyGrammarSlot slot, SlotAction after) {
		return new WrapperSlot(slot, null, after);
	}

	public WrapperSlot(BodyGrammarSlot slot, SlotAction before, SlotAction after) {
		super(slot.id, slot.getLabel(), slot.position, slot.previous, slot.getHead());
		this.slot = slot;
		this.beforeAction = before;
		this.afterAction = after;
	}

	@Override
	public void codeParser(Writer writer) throws IOException {
		slot.codeParser(writer);
	}

	@Override
	public GrammarSlot parse(GLLParser parser, Input input) {
		if(beforeAction != null) {
			beforeAction.execute(slot, parser, input);
		}
		GrammarSlot s = slot.parse(parser, input);
		if(afterAction != null) {
			afterAction.execute(slot, parser, input);
		}
		return s;
	}

	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, Input input) {
//		if(beforeAction != null) {
//			beforeAction.execute(slot);
//		}
		return slot.recognize(recognizer, input);
//		if(afterAction != null) {
//			afterAction.execute(slot);
//		}		
	}

	@Override
	public boolean checkAgainstTestSet(int i) {
		return slot.checkAgainstTestSet(i);
	}

	@Override
	public void codeIfTestSetCheck(Writer writer) throws IOException {
		slot.codeIfTestSetCheck(writer);
	}

	@Override
	public Iterable<Terminal> getTestSet() {
		return slot.getTestSet();
	}

	@Override
	public boolean isTerminalSlot() {
		return slot.isTerminalSlot();
	}

	@Override
	public boolean isNonterminalSlot() {
		return slot.isNonterminalSlot();
	}

	@Override
	public boolean isLastSlot() {
		return slot.isLastSlot();
	}

	@Override
	public boolean isNullable() {
		return slot.isNullable();
	}

	@Override
	public String getSymbolName() {
		return slot.getSymbolName();
	}

	@Override
	public BodyGrammarSlot copy(BodyGrammarSlot previous) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
