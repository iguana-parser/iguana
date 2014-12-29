package org.jgll.grammar.slot;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.condition.Condition;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Input;


public abstract class AbstractTerminalTransition extends AbstractTransition {

	protected final TerminalGrammarSlot slot;
	
	private final Set<Condition> preConditions;
	
	private final Set<Condition> postConditions;
	
	public AbstractTerminalTransition(TerminalGrammarSlot slot, GrammarSlot origin, GrammarSlot dest) {
		this(slot, origin, dest, Collections.emptySet(), Collections.emptySet());
	}
	
	public AbstractTerminalTransition(TerminalGrammarSlot slot, GrammarSlot origin, GrammarSlot dest, 
							  Set<Condition> preConditions, Set<Condition> postConditions) {
		super(origin, dest);
		this.slot = slot;
		this.preConditions = preConditions;
		this.postConditions = postConditions;
	}

	@Override
	public void execute(GLLParser parser, int i, NonPackedNode node) {
		
		Input input = parser.getInput();

		if (preConditions.stream().anyMatch(c -> c.getSlotAction().execute(input, parser.getCurrentGSSNode(), i))) 
			return;

		int length = slot.getRegularExpression().getMatcher().match(input, i);
		
		if (length < 0) {
			parser.recordParseError(origin);
			return;
		}

		if (postConditions.stream().anyMatch(c -> c.getSlotAction().execute(parser.getInput(), parser.getCurrentGSSNode(), i + length))) 
			return;
		
		TerminalNode cr = parser.getTerminalNode(slot, i, i + length);
		
		createNode(length, cr, parser, i, node);
	}
	
	protected abstract void createNode(int length, TerminalNode cr, GLLParser parser, int i, NonPackedNode node);
	
	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return new StringBuilder()
			.append("new NonterminalTransition(")
			.append("slot" + registry.getId(slot)).append(", ")
			.append("slot" + registry.getId(origin)).append(", ")
			.append("slot" + registry.getId(dest)).append(", ")
			.append(getConstructorCode(preConditions, registry)).append(", ")
			.append(getConstructorCode(postConditions, registry))
			.toString();
	}
	
	@Override
	public String toString() {
		return origin + " --[" + slot + "]--> " + dest;
	}
	
}
