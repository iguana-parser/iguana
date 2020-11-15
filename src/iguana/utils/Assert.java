package iguana.utils;

import java.util.Collection;

public class Assert {

    public static int requireNonNegative(int number) {
        if (number < 0)
            throw new IllegalArgumentException(number + " is negative");
        return number;
    }

    public static <T> Collection<T> requireNonEmpty(Collection<T> collection) {
        if (collection.isEmpty())
            throw new IllegalArgumentException("collection is empty");
        return collection;
    }
}
