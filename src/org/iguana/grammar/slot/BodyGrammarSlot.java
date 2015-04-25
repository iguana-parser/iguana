package org.iguana.grammar.slot;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.condition.Conditions;
import org.iguana.grammar.condition.ConditionsFactory;
import org.iguana.grammar.symbol.Position;
import org.iguana.parser.GLLParser;
import org.iguana.parser.gss.GSSNode;
import org.iguana.parser.gss.GSSNodeData;
import org.iguana.parser.gss.lookup.GSSNodeLookup;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.util.Input;
import org.iguana.util.collections.Key;


public class BodyGrammarSlot extends AbstractGrammarSlot {
	
	protected final Position position;
	
	private HashMap<Key, IntermediateNode> intermediateNodes;
	
	private final GSSNodeLookup nodeLookup;
	
	private final Conditions conditions;
	
	private final String label;
	
	private final String variable;
	
	public BodyGrammarSlot(int id, Position position, GSSNodeLookup nodeLookup, 
			String label, String variable, Set<Condition> conditions) {
		super(id);
		this.position = position;
		this.nodeLookup = nodeLookup;
		this.conditions = ConditionsFactory.getConditions(conditions);
		this.label = label;
		this.variable = variable;
		this.intermediateNodes = new HashMap<>();
	}
	
	@Override
	public String getConstructorCode() {
		return new StringBuilder()
    	  .append("new BodyGrammarSlot(")
    	  .append(")").toString();
	}
	
	@Override
	public String toString() {
		return position.toString();
	}
	
	@Override
	public boolean isFirst() {
		return position.isFirst();
	}
	
	public IntermediateNode getIntermediateNode(Key key, Supplier<IntermediateNode> s, Consumer<IntermediateNode> c) {
		return intermediateNodes.computeIfAbsent(key, k -> { IntermediateNode val = s.get();
															 c.accept(val);
															 return val; 
														   });
	}
	
	public IntermediateNode findIntermediateNode(Key key) {
		return intermediateNodes.get(key);
	}
	
	@Override
	public GSSNode getGSSNode(int inputIndex) {
		return nodeLookup.getOrElseCreate(this, inputIndex);
	}
	
	@Override
	public GSSNode hasGSSNode(int inputIndex) { 
		if (nodeLookup.isInitialized()) {
			return nodeLookup.get(inputIndex);
		} else {
			nodeLookup.init();			
			return null;
		}
	}
	
	public Conditions getConditions() {
		return conditions;
	}

	@Override
	public void reset(Input input) {
		intermediateNodes = new HashMap<>();
		nodeLookup.reset(input);
	}
	
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		getTransitions().forEach(t -> t.execute(parser, u, i, node));
	}
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	public String getLabel() {
		return label;
	}
	
	public String getVariable() {
		return variable;
	}
	
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env) {
		getTransitions().forEach(t -> t.execute(parser, u, i, node, env));
	}
	
	@Override
	public <T> GSSNode getGSSNode(int inputIndex, GSSNodeData<T> data) {
		return nodeLookup.getOrElseCreate(this, inputIndex, data);
	}
	
	@Override
	public <T> GSSNode hasGSSNode(int inputIndex, GSSNodeData<T> data) {
		return nodeLookup.get(inputIndex, data);
	}
	
	public boolean requiresBinding() {
		return !(label == null && variable == null); 
	}
	
	public Environment doBinding(NonPackedNode sppfNode, Environment env) {
		if (label != null) {
			env = env.declare(label, sppfNode);
		}
		
		// TODO: Support for return values
		
		return env;
	}
	
}
