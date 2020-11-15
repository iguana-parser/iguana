package iguana.utils.collections;

public class IntUtils {

    public static long merge(int high, int low) {
        return ((long) high << 32) | low;
    }

    public static int high(long value) {
        return (int) (value >>> 32);
    }

    public static int low(long value) {
        return (int) value;
    }
}
