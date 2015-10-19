package iguana.utils.collections.key;

import iguana.utils.function.IntFunctionAny;

public class IntKey1PlusObject implements Key {

    private final int k;
	private final Object obj;
	private final int hash;
	
	public IntKey1PlusObject(int k, Object obj, int hash) {
		this.k = k;
		this.obj = obj;
		this.hash = hash;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		
		if (!(other instanceof IntKey1PlusObject)) return false;
		
		IntKey1PlusObject that = (IntKey1PlusObject) other;
		return hash == that.hash && k == that.k && obj.equals(that.obj);
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	@Override
	public String toString() {
		return String.format("(%d, %s)", k, obj);
	}

    @Override
    public int hashCode(IntFunctionAny f) {
        return f.apply(k, obj);
    }
}