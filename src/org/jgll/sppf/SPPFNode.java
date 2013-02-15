package org.jgll.sppf;

import java.util.Iterator;

import org.jgll.traversal.Node;
import org.jgll.traversal.Result;
import org.jgll.traversal.SPPFVisitor;

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
		return 0;
	}
	
	public int getColumn() {
		return 0;
	}


	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

}
