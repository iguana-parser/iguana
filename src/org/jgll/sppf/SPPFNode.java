package org.jgll.sppf;

import java.util.Iterator;

import org.jgll.traversal.Node;
import org.jgll.traversal.Result;
import org.jgll.traversal.SPPFVisitor;
import org.jgll.util.InputUtil;

/**
 * An SPPF node is a node in an Shared Packed Parse Forest. This data structure
 * is used to store the parse forest that is the result of parsing an input
 * string with a GLL parser. <br />
 * 
 * @author Maarten Manders
 * @author Ali Afroozeh
 * 
 */

public abstract class SPPFNode implements Node {

	private boolean visited;
	
	private Result<Object> object;

	public abstract String getId();

	public abstract String getLabel();

	public abstract SPPFNode get(int index);
	
	public abstract Iterable<SPPFNode> getChildren();
	
	public abstract boolean isAmbiguous();

	public abstract int size();

	public abstract int getLeftExtent();

	public abstract int getRightExtent();

	public abstract void accept(SPPFVisitor visitAction);
	
	@SuppressWarnings("unchecked")
	public <T> Result<T> getResult() {
		return (Result<T>) object;
	}

	@SuppressWarnings("unchecked")
	public <T> void setObject(Result<T> result) {
		this.object = (Result<Object>) result;
	}
	
	public Iterable<Object> childrenValues() {

		final Iterator<SPPFNode> iterator = getChildren().iterator();

		return new Iterable<Object>() {

			@Override
			public Iterator<Object> iterator() {
				return new Iterator<Object>() {

					private SPPFNode next;

					@Override
					public boolean hasNext() {
						while (iterator.hasNext()) {
							next = iterator.next();
							if(next.getResult() == Result.filter()) {
								object = Result.filter();
							} else if(next.getResult() == Result.skip()) {
								// Do nothing
							} else {
								return true;								
							}
						}
						return false;
					}

					@Override
					public Object next() {
						return next.getResult().getObject();
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
				
			}
		};
	}
	
	public int getStart() {
		return getLeftExtent();
	}
	
	public int getOffset() {
		return getRightExtent() - getLeftExtent();
	}

	public int getLineNumber() {
		return InputUtil.getInstance().getLineNumber(getLeftExtent()).getLineNumber();
	}
	
	public int getColumn() {
		return InputUtil.getInstance().getLineNumber(getLeftExtent()).getColumnNumber();
	}


	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
	/**
	 * Compares this SPPFNode with the given node and their 
	 * children. 
	 * 
	 * @return true if this node is equal to the given node, and
	 * 				this node has the same number of children as
	 * 				the other node, and children at corresponding
	 * 				indices are equals using the deepEquals method.
	 * 
	 * Note: The standard equals method on SPPF nodes only compares
	 * 		 nodes ignoring their children. While parsing, the standard
	 * 		 equals method is sufficient, but for comparing parse trees,
	 * 		 the client should use deepEquals.
	 * 
	 */
	public boolean deepEquals(SPPFNode node) {
		
		if(!this.equals(node)) {
			return false;
		}
		
		if(size() != node.size()) {
			return false;
		}
		
		Iterator<SPPFNode> thisIt = getChildren().iterator();
		Iterator<SPPFNode> otherIt = node.getChildren().iterator();
		
		while(thisIt.hasNext() && otherIt.hasNext()) {
			SPPFNode thisChild = thisIt.next();
			SPPFNode otherChild = otherIt.next();
			if(!thisChild.deepEquals(otherChild)) {
				return false;
			}
		}
		
		return true;
	}

}
