package org.iguana.parser;

import java.util.Objects;

public class RecognizerStatistics {

    private final int descriptorsCount;
    private final int gssNodesCount;
    private final int gssEdgesCount;

    public RecognizerStatistics(Builder<? extends RecognizerStatistics> builder) {
        this.descriptorsCount = builder.descriptorsCount;
        this.gssNodesCount = builder.gssNodesCount;
        this.gssEdgesCount = builder.gssEdgesCount;
    }

    public int getDescriptorsCount() {
        return descriptorsCount;
    }

    public int getGssNodesCount() {
        return gssNodesCount;
    }

    public int getGssEdgesCount() {
        return gssEdgesCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptorsCount, gssEdgesCount, gssEdgesCount);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RecognizerStatistics)) return false;
        RecognizerStatistics other = (RecognizerStatistics) obj;

        return descriptorsCount == other.descriptorsCount
               && gssNodesCount == other.gssNodesCount
               && gssEdgesCount == other.gssEdgesCount;
    }

    @Override
    public String toString() {
        return "Descriptors: " + descriptorsCount + "\n"
               + "GSS Nodes: " + gssNodesCount + "\n"
               + "GSS Edges: " + gssEdgesCount + "\n";
    }

    public static Builder<? extends RecognizerStatistics> builder() {
        return new Builder<>();
    }

    public static class Builder<T extends RecognizerStatistics> {
        int descriptorsCount;
        int gssNodesCount;
        int gssEdgesCount;

        public Builder<T> setDescriptorsCount(int descriptorsCount) {
            this.descriptorsCount = descriptorsCount;
            return this;
        }

        public Builder<T> setGSSNodesCount(int gssNodesCount) {
            this.gssNodesCount = gssNodesCount;
            return this;
        }

        public Builder<T> setGSSEdgesCount(int gssEdgesCount) {
            this.gssEdgesCount = gssEdgesCount;
            return this;
        }

        public RecognizerStatistics build() {
            return new RecognizerStatistics(this);
        }

    }
}
