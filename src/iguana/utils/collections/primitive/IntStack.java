package iguana.utils.collections.primitive;

import java.util.function.IntConsumer;

public interface IntStack extends IntIterable {
    void push(int val);
    int pop();
    int peek();
    int size();
    void popOrder(IntConsumer c);
}
