package iguana.utils.collections;

import iguana.utils.collections.hash.MurmurHash3;
import iguana.utils.function.IntFunction2;
import iguana.utils.collections.key.*;
import iguana.utils.collections.key.Key;
import iguana.utils.function.IntFunction3;
import iguana.utils.function.IntFunction4;

public class Keys {

    public static Key from(int a, int b, IntFunction2 f) {
        return new IntKey2(a, b, f.apply(a, b));
    }

    public static Key from(int a, Object b) {
        return new IntKey1PlusObject(a, b, MurmurHash3.f2().apply(a, b.hashCode()));
    }

    public static Key from(int a, int b, int c, IntFunction3 f) {
        return new IntKey3(a, b, c, f);
    }

    public static Key from(int a, int b, Object o) {
        return new IntKey2PlusObject(a, b, o, MurmurHash3.f3().apply(a, b, o.hashCode()));
    }

    public static Key from(int a, int b, int c, int d, IntFunction4 f) {
        return new IntKey4(a, b, c, d, f);
    }

    public static Key from(Object...elements) {
        return new GenericKey(MurmurHash3.fn(), elements);
    }

}
