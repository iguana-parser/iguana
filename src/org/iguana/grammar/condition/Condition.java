package org.iguana.grammar.condition;

import java.io.Serializable;

import org.iguana.datadependent.attrs.AbstractAttrs;
import org.iguana.traversal.IConditionVisitor;
import org.iguana.util.generator.ConstructorCode;


public abstract class Condition extends AbstractAttrs implements Serializable, ConstructorCode {
	
	private static final long serialVersionUID = 1L;

	protected ConditionType type;

	public Condition (ConditionType type) {
		this.type = type;
	}
	
	public ConditionType getType() {
		return type;
	}

	public abstract SlotAction getSlotAction();
	
	public abstract <T> T accept(IConditionVisitor<T> visitor);
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	public boolean isDataDependent() {
		return false;
	}
	
}