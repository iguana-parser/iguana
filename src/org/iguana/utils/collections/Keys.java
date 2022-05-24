package org.iguana.utils.collections;

import org.iguana.utils.collections.hash.MurmurHash3;
import org.iguana.utils.collections.key.ObjectKeyN;
import org.iguana.utils.function.IntFunction3;
import org.iguana.utils.function.IntFunction4;

public class Keys {

    public static org.iguana.utils.collections.key.Key from(int a, int b) {
        return from(a, b);
    }

    public static org.iguana.utils.collections.key.Key from(int a) { return new org.iguana.utils.collections.key.IntKey1(a); }

    public static org.iguana.utils.collections.key.Key from(int a, Object o) { return new org.iguana.utils.collections.key.IntObjectKey(a, o); }

    public static org.iguana.utils.collections.key.Key from(int a, Object[] objects) { return new org.iguana.utils.collections.key.IntArrayKey(a, objects); }

    public static org.iguana.utils.collections.key.Key from(int a, int b, Object object) { return new org.iguana.utils.collections.key.IntIntObjectKey(a, b, object); }

    public static org.iguana.utils.collections.key.Key from(int a, int b, int c, Object object) { return new org.iguana.utils.collections.key.IntIntIntObjectKey(a, b, c, object); }

    public static org.iguana.utils.collections.key.Key from(Object a, Object b) {
        return new org.iguana.utils.collections.key.ObjectKey2(a, b, org.iguana.utils.collections.hash.MurmurHash3.f2().apply(a, b));
    }

    public static org.iguana.utils.collections.key.Key from(int a, int b, int c) {
        return from(org.iguana.utils.collections.hash.MurmurHash3.f3(), a, b, c);
    }

    public static org.iguana.utils.collections.key.Key from(org.iguana.utils.function.IntFunction3 f, int a, int b, int c) {
        return new org.iguana.utils.collections.key.IntKey3(a, b, c, f.apply(a, b, c));
    }

    public static org.iguana.utils.collections.key.Key from(Object a, Object b, Object c) {
        return from(org.iguana.utils.collections.hash.MurmurHash3.f3(), a, b, c);
    }

    public static org.iguana.utils.collections.key.Key from(IntFunction3 f, Object a, Object b, Object c) {
        return new org.iguana.utils.collections.key.ObjectKey3(a, b, c, f.apply(a, b, c));
    }

    public static org.iguana.utils.collections.key.Key from(int a, int b, int c, int d) {
        return from(org.iguana.utils.collections.hash.MurmurHash3.f4(), a, b, c, d);
    }

    public static org.iguana.utils.collections.key.Key from(org.iguana.utils.function.IntFunction4 f, int a, int b, int c, int d) {
        return new org.iguana.utils.collections.key.IntKey4(a, b, c, d, f.apply(a, b, c, d));
    }

    public static org.iguana.utils.collections.key.Key from(IntFunction4 f, Object a, Object b, Object c, Object d) {
        return new org.iguana.utils.collections.key.ObjectKey4(a, b, c, d, f.apply(a, b, c, d));
    }

    public static org.iguana.utils.collections.key.Key from(Object...elements) {
        return new ObjectKeyN(MurmurHash3.fn(), elements);
    }

}
