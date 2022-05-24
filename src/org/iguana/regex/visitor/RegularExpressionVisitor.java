package org.iguana.regex.visitor;

import org.iguana.regex.EOF;

public interface RegularExpressionVisitor<T> {
	
	T visit(org.iguana.regex.Char c);
	
    T visit(org.iguana.regex.CharRange r);
	
	T visit(EOF eof);
	
	T visit(org.iguana.regex.Epsilon e);
	
	T visit(org.iguana.regex.Star s);
	
	T visit(org.iguana.regex.Plus p);
	
	T visit(org.iguana.regex.Opt o);

	<E extends org.iguana.regex.RegularExpression> T visit(org.iguana.regex.Alt<E> alt);
	
    <E extends org.iguana.regex.RegularExpression> T visit(org.iguana.regex.Seq<E> seq);

    T visit(org.iguana.regex.Reference ref);

}
