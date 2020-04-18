package iguana.utils.collections;

class Entry<T> {

	int key;
	T val;
	Entry<T> next;
	
	public Entry(int key, T val) {
		this.key = key;
		this.val = val;
	}
	
	@Override
	public String toString() {
		return "(" + key + ", " + val + ")";
	}
}