package org.iguana.regex.visitor;

import org.iguana.regex.*;

public interface RegularExpressionVisitor<T> {

    T visit(Char c);

    T visit(CharRange r);

    T visit(EOF eof);

    T visit(Epsilon e);

    T visit(Star s);

    T visit(Plus p);

    T visit(Opt o);

    <E extends RegularExpression> T visit(Alt<E> alt);

    <E extends RegularExpression> T visit(Seq<E> seq);

    T visit(Reference ref);

}
