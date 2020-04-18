package iguana.utils.collections;

@FunctionalInterface
public interface IntKeyMapper<T> {
	T apply(int k, T v);
}
