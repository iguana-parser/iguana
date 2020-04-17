package org.iguana.result;

public interface Result {
    int getIndex();
    int getLeftExtent();
    boolean isDummy();
    Object getValue();
}
