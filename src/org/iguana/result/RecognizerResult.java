package org.iguana.result;

public interface RecognizerResult extends Result {
    static RecognizerResult of(int start, int end) {
        return new SimpleRecognizerResult(start, end);
    }

    static RecognizerResult of(int index, Object value) {
        return new DataDependentRecognizerResult(index, value);
    }
}

class SimpleRecognizerResult implements RecognizerResult {

    private final int start;
    private final int index;

    SimpleRecognizerResult(int start, int end) {
        this.start = start;
        this.index = end;
    }

    @Override
    public int getRightExtent() {
        return index;
    }

    @Override
    public int getLeftExtent() {
        return start;
    }

        @Override
    public boolean isDummy() {
        return false;
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public String toString() {
        return index + "";
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
    public int getRightExtent() {
        return index;
    }

    @Override
    public int getLeftExtent() {
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

    @Override
    public String toString() {
        return "(" + "index=" + index + ", value=" + value + ")";
    }
}
