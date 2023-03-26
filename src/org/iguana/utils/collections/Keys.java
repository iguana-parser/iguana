package org.iguana.utils.collections;

import org.iguana.utils.collections.hash.MurmurHash3;
import org.iguana.utils.collections.key.IntArrayKey;
import org.iguana.utils.collections.key.IntIntIntObjectKey;
import org.iguana.utils.collections.key.IntIntObjectKey;
import org.iguana.utils.collections.key.IntKey1;
import org.iguana.utils.collections.key.IntKey3;
import org.iguana.utils.collections.key.IntKey4;
import org.iguana.utils.collections.key.IntObjectKey;
import org.iguana.utils.collections.key.Key;
import org.iguana.utils.collections.key.ObjectKey2;
import org.iguana.utils.collections.key.ObjectKey3;
import org.iguana.utils.collections.key.ObjectKey4;
import org.iguana.utils.collections.key.ObjectKeyN;
import org.iguana.utils.function.IntFunction3;
import org.iguana.utils.function.IntFunction4;

public class Keys {

    public static Key from(int a, int b) {
        return from(a, b);
    }

    public static Key from(int a) {
        return new IntKey1(a);
    }

    public static Key from(int a, Object o) {
        return new IntObjectKey(a, o);
    }

    public static Key from(int a, Object[] objects) {
        return new IntArrayKey(a, objects);
    }

    public static Key from(int a, int b, Object object) {
        return new IntIntObjectKey(a, b, object);
    }

    public static Key from(int a, int b, int c, Object object) {
        return new IntIntIntObjectKey(a, b, c, object);
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

    public static Key from(org.iguana.utils.function.IntFunction4 f, int a, int b, int c, int d) {
        return new IntKey4(a, b, c, d, f.apply(a, b, c, d));
    }

    public static Key from(IntFunction4 f, Object a, Object b, Object c, Object d) {
        return new ObjectKey4(a, b, c, d, f.apply(a, b, c, d));
    }

    public static Key from(Object... elements) {
        return new ObjectKeyN(MurmurHash3.fn(), elements);
    }

}
