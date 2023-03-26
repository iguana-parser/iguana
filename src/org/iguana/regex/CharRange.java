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

package org.iguana.regex;

import org.iguana.regex.visitor.RegularExpressionVisitor;
import org.iguana.utils.collections.hash.MurmurHash3;
import org.iguana.utils.collections.rangemap.Range;

import java.util.Collections;
import java.util.Set;

import static org.iguana.utils.collections.CollectionsUtil.immutableSet;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class CharRange extends AbstractRegularExpression implements Range {

    private final int start;

    private final int end;

    private CharRange(Builder builder) {
        super(builder);

        if (builder.end < builder.start)
            throw new IllegalArgumentException("Start cannot be less than end.");

        this.start = builder.start;
        this.end = builder.end;
    }

    public static CharRange from(int c) {
        return in(c, c);
    }

    public static CharRange in(int start, int end) {
        return new Builder(start, end).build();
    }

    public static String getName(int start, int end) {
        if (start == end) {
            return Char.getName(start);
        } else {
            return Char.getName(start) + "-" + Char.getName(end);
        }
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getEnd() {
        return end;
    }

    @Override
    public int hashCode() {
        return MurmurHash3.f2().apply(start, end);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof CharRange))
            return false;

        CharRange other = (CharRange) obj;

        return start == other.start && end == other.end;
    }

    @Override
    public String toString() {
        return getName(start, end);
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public Set<CharRange> getFirstSet() {
        return immutableSet(this);
    }

    @Override
    public Set<CharRange> getNotFollowSet() {
        return Collections.emptySet();
    }

    @Override
    public int length() {
        return 1;
    }

    public static Builder builder(int start, int end) {
        return new Builder(start, end);
    }

    @Override
    public Builder copy() {
        return new Builder(this);
    }
    
    public static class Builder extends RegexBuilder<CharRange> {

        private int start;
        private int end;

        private Builder() {}

        public Builder(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public Builder(CharRange range) {
            super(range);
            this.start = range.start;
            this.end = range.end;
        }

        @Override
        public CharRange build() {
            return new CharRange(this);
        }
    }

    @Override
    public <T> T accept(RegularExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
