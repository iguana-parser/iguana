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
		super(slot.id, slot.position, slot.previous, slot.getHead());
		this.slot = slot;
		this.beforeAction = before;
		this.afterAction = after;
	}

	@Override
	public void code(Writer writer) throws IOException {
		slot.code(writer);
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
	public void recognize(GLLRecognizer recognizer, Input input, org.jgll.recognizer.GSSNode cu, int ci) {
//		if(beforeAction != null) {
//			beforeAction.execute(slot);
//		}
//		slot.recognize(recognizer, input, cu, ci);
//		if(afterAction != null) {
//			afterAction.execute(slot);
//		}		
	}

	@Override
	public String getName() {
		return slot.getName();
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
	
}
