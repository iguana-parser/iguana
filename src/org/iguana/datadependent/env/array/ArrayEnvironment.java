package org.iguana.datadependent.env.array;

import org.iguana.datadependent.env.Environment;

public class ArrayEnvironment implements Environment {
	
	private final Object[] values;
	private final int length;
	private final int hashCode;
	
	private ArrayEnvironment(Object[] values, int length, int hashCode) {
		this.values = values;
		this.length = length;
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
		Object[] values = new Object[this.values.length];
		
		System.arraycopy(values, 0, this.values, 0, length);
		values[length] = value;
		
		return new ArrayEnvironment(values, length + 1, hashCode + 31 * value.hashCode());
	}

	@Override
	public Environment declare(Object[] values) {
		Object[] newvalues = new Object[this.values.length];
		
		System.arraycopy(newvalues, 0, this.values, 0, length);
		int hashCode = this.hashCode;
		
		int j = 0;
		for (int i = length; i < values.length; i++) {
			newvalues[i] = values[j];
			hashCode = hashCode + 31 * values[j].hashCode();
			j++;
		}
		
		return new ArrayEnvironment(newvalues, length + values.length, hashCode);
	}

	@Override
	public Environment store(int i, Object value) {
		Object[] values = new Object[length];
		
		int hashCode = 0;
		for (int j = 0; j < length; j++) {
			
			if (i != j)
				values[i] = this.values[i];
			else
				values[i] = value;
			
			if (values[i] != null)
				hashCode = hashCode + values[i].hashCode();
		}
		
		return new ArrayEnvironment(values, hashCode, length);
	}

	@Override
	public Object lookup(int i) {
		return values[i];
	}

}
