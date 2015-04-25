package org.iguana.datadependent.traversal;

import org.iguana.datadependent.ast.Statement;
import org.iguana.datadependent.ast.VariableDeclaration;
import org.iguana.datadependent.ast.Expression.AndIndent;
import org.iguana.datadependent.ast.Expression.Assignment;
import org.iguana.datadependent.ast.Expression.Boolean;
import org.iguana.datadependent.ast.Expression.Call;
import org.iguana.datadependent.ast.Expression.EndOfFile;
import org.iguana.datadependent.ast.Expression.Equal;
import org.iguana.datadependent.ast.Expression.Greater;
import org.iguana.datadependent.ast.Expression.GreaterThanEqual;
import org.iguana.datadependent.ast.Expression.Integer;
import org.iguana.datadependent.ast.Expression.LShiftANDEqZero;
import org.iguana.datadependent.ast.Expression.LeftExtent;
import org.iguana.datadependent.ast.Expression.Less;
import org.iguana.datadependent.ast.Expression.LessThanEqual;
import org.iguana.datadependent.ast.Expression.Name;
import org.iguana.datadependent.ast.Expression.NotEqual;
import org.iguana.datadependent.ast.Expression.Or;
import org.iguana.datadependent.ast.Expression.OrIndent;
import org.iguana.datadependent.ast.Expression.Real;
import org.iguana.datadependent.ast.Expression.RightExtent;
import org.iguana.datadependent.ast.Expression.String;

public interface IAbstractASTVisitor<T> {
	
	public T visit(Boolean expression);
	
	public T visit(Integer expression);
	
	public T visit(Real expression);
	
	public T visit(String expression);
	
	public T visit(Name expression);
	
	public T visit(Call expression);
	
	public T visit(Assignment expression);
	
	public T visit(LShiftANDEqZero expression);
	
	public T visit(OrIndent expression);
	
	public T visit(AndIndent expression);
	
	public T visit(Or expression);
	
	public T visit(Less expression);
	
	public T visit(LessThanEqual expression);
	
	public T visit(Greater expression);
	
	public T visit(GreaterThanEqual expression);
	
	public T visit(Equal expression);
	
	public T visit(NotEqual expression);
	
	public T visit(LeftExtent expression);
	
	public T visit(RightExtent expression);
	
	public T visit(EndOfFile expression);
	
	public T visit(VariableDeclaration declaration);
	
	public T visit(Statement.Expression statement);
	
	public T visit(Statement.VariableDeclaration statement);

}
