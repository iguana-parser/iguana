package iguana.utils.collections;

import iguana.utils.collections.hash.MurmurHash3;
import iguana.utils.function.IntFunction2;
import iguana.utils.collections.key.*;
import iguana.utils.collections.key.Key;
import iguana.utils.function.IntFunction3;
import iguana.utils.function.IntFunction4;

public class Keys {

    public static Key from(int a, int b) {
        return from(MurmurHash3.f2(), a, b);
    }

    public static Key from(int a) { return new IntKey1(a); }

    public static Key from(int a, Object o) { return new IntObjectKey(a, o); }

    public static Key from(int a, Object[] objects) { return new IntArrayKey(a, objects); }

    public static Key from(int a, int b, Object object) { return new IntIntObjectKey(a, b, object); }

    public static Key from(IntFunction2 f, int a, int b) {
        return new IntKey2(a, b, f.apply(a, b));
    }

    public static Key from(Object a, Object b) {
        return new ObjectKey2(a, b, MurmurHash3.f2().apply(a, b));
    }

    public static Key from(int a, int b, int c) {
        return from(MurmurHash3.f3(), a, b, c);
    }

    public static Key from(IntFunction3 f, int a, int b, int c) {
        return new IntKey3(a, b, c, f.apply(a, b, c));
    }

    public static Key from(Object a, Object b, Object c) {
        return from(MurmurHash3.f3(), a, b, c);
    }

    public static Key from(IntFunction3 f, Object a, Object b, Object c) {
        return new ObjectKey3(a, b, c, f.apply(a, b, c));
    }

    public static Key from(int a, int b, int c, int d) {
        return from(MurmurHash3.f4(), a, b, c, d);
    }

    public static Key from(IntFunction4 f, int a, int b, int c, int d) {
        return new IntKey4(a, b, c, d, f.apply(a, b, c, d));
    }

    public static Key from(IntFunction4 f, Object a, Object b, Object c, Object d) {
        return new ObjectKey4(a, b, c, d, f.apply(a, b, c, d));
    }

    public static Key from(Object...elements) {
        return new ObjectKeyN(MurmurHash3.fn(), elements);
    }

}
