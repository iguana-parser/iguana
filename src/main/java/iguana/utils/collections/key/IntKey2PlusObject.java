package iguana.utils.collections.key;

import iguana.utils.function.IntFunctionAny;

public class IntKey2PlusObject implements Key {
	
	private final int k1;
	private final int k2;
	private final Object obj;
	private final int hash;

	public IntKey2PlusObject(int k1, int k2, Object obj, int hash) {
		this.k1 = k1;
		this.k2 = k2;
		this.obj = obj;
		this.hash = hash;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		
		if (!(other instanceof IntKey2PlusObject)) return false;
		
		IntKey2PlusObject that = (IntKey2PlusObject) other;
		return hash == that.hash &&
               k1 == that.k1 &&
               k2 == that.k2 &&
               obj.equals(that.obj);
	}
	
	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public String toString() {
		return String.format("(%d, %d, %s)", k1, k2, obj);
	}

    @Override
    public int hashCode(IntFunctionAny f) {
        return f.apply(k1, k2, obj);
    }
}