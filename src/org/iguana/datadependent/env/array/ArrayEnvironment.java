package org.iguana.datadependent.env.array;

import org.iguana.datadependent.env.Environment;
import org.iguana.datadependent.env.EnvironmentPool;

import java.util.Arrays;

public class ArrayEnvironment implements Environment {

    static final ArrayEnvironment EMPTY = new ArrayEnvironment(new Object[0], 0);

    private final Object[] values;
    private int hashCode;

    private ArrayEnvironment(Object[] values, int hashCode) {
        this.values = values;
        this.hashCode = hashCode;
    }

    private void init(Object[] oldValues, Object value, int oldHashCode) {
        int length = oldValues.length;
        System.arraycopy(this.values, 0, values, 0, length);

        values[length] = value;

        int valueHashCode = oldHashCode;
        if (value instanceof Object[]) {
            for (int i = 0; i < ((Object[]) value).length; i++) {
                Object element = ((Object[]) value)[i];
                valueHashCode = 31 * valueHashCode + element.hashCode();
            }
        } else {
            valueHashCode = 31 * valueHashCode + value.hashCode();
        }

        this.hashCode = valueHashCode;
    }

    @Override
    public boolean isEmpty() {
        return values.length == 0;
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
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object other) {

        if (this == other)
            return true;

        if (!(other instanceof ArrayEnvironment))
            return false;

        if (this.hashCode() != other.hashCode())
            return false;

        ArrayEnvironment that = (ArrayEnvironment) other;

        if (values.length != that.values.length)
            return false;

        for (int i = 0; i < values.length; i++)
            if (!values[i].equals(that.values[i]))
                return false;

        return true;
    }

    @Override
    public String toString() {
        return Arrays.toString(values);
    }

    @Override
    public Environment _declare(Object value) {
        int length = this.values.length;

        ArrayEnvironment environment = (ArrayEnvironment) EnvironmentPool.get(length + 1);
        if (environment != null) {
            environment.init(this.values, value, this.hashCode);
            return environment;
        }

        Object[] values = new Object[length + 1];

        if (length != 0)
            System.arraycopy(this.values, 0, values, 0, length);

        values[length] = value;

        int valueHashCode = hashCode;
        if (value instanceof Object[]) {
            for (int i = 0; i < ((Object[]) value).length; i++) {
                Object element = ((Object[]) value)[i];
                valueHashCode = 31 * valueHashCode + element.hashCode();
            }
        } else {
            valueHashCode = 31 * valueHashCode + value.hashCode();
        }

        return new ArrayEnvironment(values, valueHashCode);
    }

    @Override
    public Environment declare(Object[] values) { // Assuming values.length != 0
        int length = this.values.length;
        Object[] vals = new Object[length + values.length];

        if (length != 0)
            System.arraycopy(this.values, 0, vals, 0, length);

        int hashCode = this.hashCode;

        int j = 0;
        for (int i = length; i < length + values.length; i++) {
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

    @Override
    public int size() {
        return values.length;
    }

}
