/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */

package org.iguana.parser;

import java.util.Objects;

public class ParseStatistics extends RecognizerStatistics {
    private final int nonterminalNodesCount;
    private final int terminalNodesCount;
    private final int intermediateNodesCount;
    private final int packedNodesCount;
    private final int ambiguousNodesCount;

    public ParseStatistics(Builder builder) {
        super(builder);
        this.nonterminalNodesCount = builder.nonterminalNodesCount;
        this.terminalNodesCount = builder.terminalNodesCount;
        this.intermediateNodesCount = builder.intermediateNodesCount;
        this.packedNodesCount = builder.packedNodesCount;
        this.ambiguousNodesCount = builder.ambiguousNodesCount;
    }

    public int getNonterminalNodesCount() {
        return nonterminalNodesCount;
    }

    public int getTerminalNodesCount() {
        return terminalNodesCount;
    }

    public int getIntermediateNodesCount() {
        return intermediateNodesCount;
    }

    public int getPackedNodesCount() {
        return packedNodesCount;
    }

    public int getAmbiguousNodesCount() {
        return ambiguousNodesCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), nonterminalNodesCount,
                terminalNodesCount, intermediateNodesCount, packedNodesCount,
                ambiguousNodesCount);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof ParseStatistics))
            return false;

        ParseStatistics other = (ParseStatistics) obj;

        return super.equals(obj) &&
                nonterminalNodesCount == other.nonterminalNodesCount &&
                terminalNodesCount == other.terminalNodesCount &&
                intermediateNodesCount == other.intermediateNodesCount &&
                packedNodesCount == other.packedNodesCount &&
                ambiguousNodesCount == other.ambiguousNodesCount;
    }

    @Override
    public String toString() {
        return super.toString() +
               "Nonterminal nodes: " + nonterminalNodesCount + "\n" +
               "Terminal nodes: " + terminalNodesCount + "\n" +
               "Intermediate nodes: " + intermediateNodesCount + "\n" +
               "Packed nodes: " + packedNodesCount + "\n" +
               "Ambiguities: " + ambiguousNodesCount + "\n";
    }

    public static class Builder extends RecognizerStatistics.Builder<Builder> {
        int descriptorsCount;
        int gssNodesCount;
        int gssEdgesCount;
        int nonterminalNodesCount;
        int terminalNodesCount;
        int intermediateNodesCount;
        int packedNodesCount;
        int ambiguousNodesCount;

        public Builder setDescriptorsCount(int descriptorsCount) {
            this.descriptorsCount = descriptorsCount;
            return this;
        }

        public Builder setGSSNodesCount(int gssNodesCount) {
            this.gssNodesCount = gssNodesCount;
            return this;
        }

        public Builder setGSSEdgesCount(int gssEdgesCount) {
            this.gssEdgesCount = gssEdgesCount;
            return this;
        }

        public Builder setNonterminalNodesCount(int nonterminalNodesCount) {
            this.nonterminalNodesCount = nonterminalNodesCount;
            return this;
        }

        public Builder setTerminalNodesCount(int terminalNodesCount) {
            this.terminalNodesCount = terminalNodesCount;
            return this;
        }

        public Builder setIntermediateNodesCount(int intermediateNodesCount) {
            this.intermediateNodesCount = intermediateNodesCount;
            return this;
        }

        public Builder setPackedNodesCount(int packedNodesCount) {
            this.packedNodesCount = packedNodesCount;
            return this;
        }

        public Builder setAmbiguousNodesCount(int ambiguousNodesCount) {
            this.ambiguousNodesCount = ambiguousNodesCount;
            return this;
        }

        public ParseStatistics build() {
            return new ParseStatistics(this);
        }

    }
}
