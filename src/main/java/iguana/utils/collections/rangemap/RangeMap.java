package iguana.utils.collections.rangemap;

import java.util.List;

@FunctionalInterface
public interface RangeMap<T> {
    List<T> get(int key);
}
