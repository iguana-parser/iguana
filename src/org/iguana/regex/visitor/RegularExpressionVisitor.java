package org.iguana.regex.visitor;

import org.iguana.regex.Alt;
import org.iguana.regex.Char;
import org.iguana.regex.CharRange;
import org.iguana.regex.EOF;
import org.iguana.regex.Epsilon;
import org.iguana.regex.NewLine;
import org.iguana.regex.Opt;
import org.iguana.regex.Plus;
import org.iguana.regex.Reference;
import org.iguana.regex.RegularExpression;
import org.iguana.regex.Seq;
import org.iguana.regex.Star;

public interface RegularExpressionVisitor<T> {

    T visit(Char c);

    T visit(CharRange r);

    T visit(EOF eof);

    T visit(NewLine newLine);

    T visit(Epsilon e);

    T visit(Star s);

    T visit(Plus p);

    T visit(Opt o);

    <E extends RegularExpression> T visit(Alt<E> alt);

    <E extends RegularExpression> T visit(Seq<E> seq);

    T visit(Reference ref);

}
