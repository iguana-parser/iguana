package org.jgll.datadependent.traversal;

import org.jgll.datadependent.ast.Expression.Assignment;
import org.jgll.datadependent.ast.Expression.Boolean;
import org.jgll.datadependent.ast.Expression.Call;
import org.jgll.datadependent.ast.Expression.Equal;
import org.jgll.datadependent.ast.Expression.Greater;
import org.jgll.datadependent.ast.Expression.GreaterThanEqual;
import org.jgll.datadependent.ast.Expression.Integer;
import org.jgll.datadependent.ast.Expression.LShiftANDEqZero;
import org.jgll.datadependent.ast.Expression.LeftExtent;
import org.jgll.datadependent.ast.Expression.Less;
import org.jgll.datadependent.ast.Expression.Name;
import org.jgll.datadependent.ast.Expression.NotEqual;
import org.jgll.datadependent.ast.Expression.Real;
import org.jgll.datadependent.ast.Expression.RightExtent;
import org.jgll.datadependent.ast.Expression.String;
import org.jgll.datadependent.ast.Statement;
import org.jgll.datadependent.ast.VariableDeclaration;

public interface IAbstractASTVisitor<T> {
	
	public T visit(Boolean expression);
	
	public T visit(Integer expression);
	
	public T visit(Real expression);
	
	public T visit(String expression);
	
	public T visit(Name expression);
	
	public T visit(Call expression);
	
	public T visit(Assignment expression);
	
	public T visit(LShiftANDEqZero expression);
	
	public T visit(Less expression);
	
	public T visit(Greater expression);
	
	public T visit(GreaterThanEqual expression);
	
	public T visit(Equal expression);
	
	public T visit(NotEqual expression);
	
	public T visit(LeftExtent expression);
	
	public T visit(RightExtent expression);
	
	public T visit(VariableDeclaration declaration);
	
	public T visit(Statement.Expression statement);
	
	public T visit(Statement.VariableDeclaration statement);

}
