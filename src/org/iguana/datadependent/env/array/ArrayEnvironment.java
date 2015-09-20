package org.iguana.datadependent.env.array;

import org.iguana.datadependent.env.Environment;

public class ArrayEnvironment implements Environment {
	
	private final Object[] values;
	private final int hashCode;
	
	private ArrayEnvironment(Object[] values, int hashCode) {
		this.values = values;
		this.hashCode = hashCode;
	}

	@Override
	public boolean isEmpty() {
		return hashCode == 0;
	}

	@Override
	public Environment pop() {
		throw new RuntimeException("Unsupported with this type of environment!");
	}

	@Override
	public Environment push() {
		throw new RuntimeException("Unsupported with this type of environment!");
	}

	@Override
	public Environment _declare(String name, Object value) {
		throw new RuntimeException("Unsupported with this type of environment!");
	}

	@Override
	public Environment declare(String[] names, Object[] values) {
		throw new RuntimeException("Unsupported with this type of environment!");
	}

	@Override
	public Environment store(String name, Object value) {
		throw new RuntimeException("Unsupported with this type of environment!");
	}

	@Override
	public Object lookup(String name) {
		throw new RuntimeException("Unsupported with this type of environment!");
	}

	@Override
	public Environment _declare(Object value) {
		int length = this.values.length + 1;
		Object[] values = new Object[length];
		
		System.arraycopy(this.values, 0, values, 0, this.values.length);
		values[this.values.length] = value;
		
		return new ArrayEnvironment(values, hashCode + 31 * value.hashCode());
	}

	@Override
	public Environment declare(Object[] values) {
		int length = this.values.length + values.length;
		Object[] vals = new Object[length];
		
		System.arraycopy(this.values, 0, vals, 0, this.values.length);
		int hashCode = this.hashCode;
		
		int j = 0;
		for (int i = this.values.length; i < values.length; i++) {
			vals[i] = values[j];
			hashCode = hashCode + 31 * values[j].hashCode();
			j++;
		}
		
		return new ArrayEnvironment(vals, hashCode);
	}

	@Override
	public Environment store(int i, Object value) {
		Object[] values = new Object[this.values.length];
		
		int hashCode = 0;
		for (int j = 0; j < values.length; j++) {
			
			if (i != j)
				values[i] = this.values[i];
			else
				values[i] = value;
			
			if (values[i] != null)
				hashCode = hashCode + values[i].hashCode();
		}
		
		return new ArrayEnvironment(values, hashCode);
	}

	@Override
	public Object lookup(int i) {
		return values[i];
	}

}
