package org.iguana.result;

public interface RecognizerResult extends Result {
    static RecognizerResult of(int index) {
        return new SimpleRecognizerResult(index);
    }

    static RecognizerResult of(int index, Object value) {
        return new DataDependentRecognizerResult(index, value);
    }
}

class SimpleRecognizerResult implements RecognizerResult {

    private final int index;

    SimpleRecognizerResult(int index) {
        this.index = index;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public boolean isDummy() {
        return false;
    }

    @Override
    public Object getValue() {
        return null;
    }
}

class DataDependentRecognizerResult implements RecognizerResult {

    private final int index;
    private final Object value;

    DataDependentRecognizerResult(int index, Object value) {
        this.index = index;
        this.value = value;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public boolean isDummy() {
        return false;
    }

    @Override
    public Object getValue() {
        return value;
    }
}
