package org.iguana.regex.utils;

import org.iguana.regex.Char;
import org.iguana.regex.RegularExpression;
import org.iguana.regex.Seq;

public class RegexUtils {

    public static boolean isLiteral(RegularExpression regularExpression) {
        if (regularExpression instanceof Char) {
            return true;
        }
        if (!(regularExpression instanceof Seq)) {
            return false;
        }
        Seq<?> seq = (Seq<?>) regularExpression;
        for (RegularExpression elem : seq.getSymbols()) {
            if (!(elem instanceof Char)) {
                return false;
            }
        }
        return true;
    }
}
