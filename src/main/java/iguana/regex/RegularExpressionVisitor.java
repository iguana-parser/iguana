package iguana.regex;

public interface RegularExpressionVisitor<T> {
	
	T visit(Character c);
	
    T visit(CharacterRange r);
	
	T visit(EOF eof);
	
	T visit(Epsilon e);
	
	T visit(Star s);
	
	T visit(Plus p);
	
	T visit(Opt o);
	
	<E extends RegularExpression> T visit(Alt<E> alt);
	
    <E extends RegularExpression> T visit(Sequence<E> seq);

}
