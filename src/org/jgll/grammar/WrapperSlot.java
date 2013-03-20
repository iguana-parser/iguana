package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;

import org.jgll.parser.GLLParser;
import org.jgll.parser.GSSNode;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.Input;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class WrapperSlot extends GrammarSlot {
	
	private static final long serialVersionUID = 1L;
	
	private GrammarSlot slot;
	private SlotAction before;
	private SlotAction after;
	
	public static WrapperSlot before(GrammarSlot slot, SlotAction before) {
		return new WrapperSlot(slot, before, null);
	}

	public static WrapperSlot after(GrammarSlot slot, SlotAction after) {
		return new WrapperSlot(slot, null, after);
	}

	public WrapperSlot(GrammarSlot slot, SlotAction before, SlotAction after) {
		super(slot.getId());
		this.slot = slot;
		this.before = before;
		this.after = after;
	}

	@Override
	public void code(Writer writer) throws IOException {
		slot.code(writer);
	}

	@Override
	public void parse(GLLParser parser, Input input, GSSNode cu, SPPFNode cn, int ci) {
		if(before != null) {
			before.execute(slot);
		}
		slot.parse(parser, input, cu, cn, ci);
		if(after != null) {
			after.execute(slot);
		}
	}

	@Override
	public void recognize(GLLRecognizer recognizer, Input input, org.jgll.recognizer.GSSNode cu, int ci) {
		if(before != null) {
			before.execute(slot);
		}
		slot.recognize(recognizer, input, cu, ci);
		if(after != null) {
			after.execute(slot);
		}		
	}

	@Override
	public String getName() {
		return slot.getName();
	}

	
	
}
