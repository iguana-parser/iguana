package org.iguana.result;

public interface Result {

    int getRightExtent();

    int getLeftExtent();

    boolean isDummy();

    Object getValue();
}
