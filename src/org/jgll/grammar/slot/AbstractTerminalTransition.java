package org.jgll.grammar.slot;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.condition.Condition;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Input;
import org.jgll.util.logging.LoggerWrapper;


public abstract class AbstractTerminalTransition extends AbstractTransition {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(AbstractTerminalTransition.class);

	protected final TerminalGrammarSlot slot;
	
	private final Set<Condition> preConditions;
	
	private final Set<Condition> postConditions;
	
	public AbstractTerminalTransition(TerminalGrammarSlot slot, BodyGrammarSlot origin, BodyGrammarSlot dest) {
		this(slot, origin, dest, Collections.emptySet(), Collections.emptySet());
	}
	
	public AbstractTerminalTransition(TerminalGrammarSlot slot, BodyGrammarSlot origin, BodyGrammarSlot dest, 
							  Set<Condition> preConditions, Set<Condition> postConditions) {
		super(origin, dest);
		this.slot = slot;
		this.preConditions = preConditions;
		this.postConditions = postConditions;
	}

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		
		log.trace("Processing %s", this);
		
		Input input = parser.getInput();

		for (Condition c : preConditions) {
			if (c.getSlotAction().execute(input, u, i)) 
				return;
		}

		int length = slot.match(input, i);
		
		if (length < 0) {
			parser.recordParseError(origin);
			return;
		}

		for (Condition c : postConditions) {
			if (c.getSlotAction().execute(input, u, i)) 
				return;
		}
		
		TerminalNode cr = parser.getTerminalNode(slot, i, i + length);
		
		createNode(length, cr, parser, u, i, node);
	}
	
	protected abstract void createNode(int length, TerminalNode cr, GLLParser parser, GSSNode u, int i, NonPackedNode node);
	
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
	
}
