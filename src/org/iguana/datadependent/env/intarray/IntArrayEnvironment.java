package org.iguana.datadependent.env.intarray;

import org.iguana.datadependent.env.Environment;
import org.iguana.datadependent.env.EnvironmentPool;
import org.iguana.datadependent.env.array.ArrayEnvironment;

import static java.lang.Integer.toUnsignedLong;

public class IntArrayEnvironment implements Environment {

    /**
     * size o6 o5 o4 o3 o2 o1
     */
    private long value;

    static final IntArrayEnvironment EMPTY = new IntArrayEnvironment(0);

    private IntArrayEnvironment(long value) {
        this.value = value;
    }

    public void init(long value) {
        this.value = value;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
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
    public IntArrayEnvironment declare(String[] names, Object[] values) {
        throw new RuntimeException("Unsupported with this type of environment!");
    }

    @Override
    public IntArrayEnvironment store(String name, Object value) {
        throw new RuntimeException("Unsupported with this type of environment!");
    }

    @Override
    public Object lookup(String name) {
        throw new RuntimeException("Unsupported with this type of environment!");
    }

    @Override
    public IntArrayEnvironment _declare(Object value) {
        int size = size();

        long result;

        if (value instanceof Long) {
            long longValue = (long) value;
            int first = (int) (longValue >>> 32);
            int second = (int) (longValue & 0x00000000_FFFFFFFFL);
            if (size == 2) {
                result = set(2, second, this.value);
                result = set(3, first, result);
            } else if (size == 3) {
                result = set(4, second, this.value);
                result = set(5, first, result);
            } else {
                throw new RuntimeException("Tuple values cannot be added before single integers");
            }
        } else {
            if (size != 0 && size != 1)
                throw new RuntimeException("Integer values can only be added at position 0 or 1");
            result = set(size, (Integer) value, this.value);
        }

        result = setSize(size + 1, result);

        IntArrayEnvironment environment = (IntArrayEnvironment) EnvironmentPool.get(size + 1);
        if (environment != null) {
            environment.init(result);
            return environment;
        }

        return new IntArrayEnvironment(result);
    }

    private static long set(int i, int value, long currentValue) {
        if (value > 511) throw new RuntimeException("The value has to be smaller than 512");

        long bitmask = ~(0x00000000000003FFL << (i * 10));

        return (currentValue & bitmask) | (toUnsignedLong(value) << (i * 10));
    }

    private long setSize(int size, long currentValue) {
        return ((long) size << 60) | (currentValue & 0x0FFFFFFFFFFFFFFFL);
    }

    @Override
    public IntArrayEnvironment declare(Object[] values) {
        if (values.length > 6) throw new RuntimeException("Cannot add more than 6 values in this type of environment");

        long value = 0L;

        for (int i = 0; i < values.length; i++) {
            int currentValue = (Integer) values[i];
            value |= (toUnsignedLong(currentValue) << (i * 10));
        }

        long size = values.length;
        value = (size << 60) | (value & 0x0FFFFFFFFFFFFFFFL);
        return new IntArrayEnvironment(value);
    }

    @Override
    public IntArrayEnvironment store(int i, Object value) {
        return new IntArrayEnvironment(set(i, (int) value, this.value));
    }

    static MutableLong mutableLong = new MutableLong();

    @Override
    public Object lookup(int i) {
        if (i == 0 || i == 1) {
            return get(i);
        }
        mutableLong.setValue(getTuple(i));
        return mutableLong;
    }

    private int get(int i) {
        long bitmask = 0x00000000000003FFL << (i * 10);
        return (int) ((this.value & bitmask) >>> (i * 10));
    }

    private long getTuple(int i) {
        int first;
        int second;
        if (i == 2) {
            first = get(2);
            second = get(3);
        } else if (i == 3) {
            first = get(4);
            second = get(5);
        } else {
            throw new RuntimeException();
        }

        return toUnsignedLong(second) << 32 | toUnsignedLong(first);
    }

    @Override
    public int size() {
        return (int) ((this.value & 0xF000000000000000L) >>> 60);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntArrayEnvironment)) return false;
        IntArrayEnvironment other = (IntArrayEnvironment) o;
        return value == other.value;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        int first = get(0);
        int second = get(1);
        long firstTuple = getTuple(2);
        int third = (int) (firstTuple >> 32);
        int fourth = (int) (firstTuple & 0x0000_0000_FFFF_FFF);
        long secondTuple = getTuple(3);
        int fifth = (int) (secondTuple >> 32);
        int sixth = (int) (secondTuple & 0x0000_0000_FFFF_FFF);

        sb.append("{")
            .append(first).append(", ")
            .append(second).append(", ")
            .append("[").append(third).append(", ").append(fourth).append("], ")
            .append("[").append(fifth).append(", ").append(sixth).append("]")
        .append("}");
        return sb.toString();
    }
}
