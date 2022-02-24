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

package iguana.regex;

import iguana.regex.visitor.RegularExpressionVisitor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Char extends AbstractRegularExpression {
	
	private static final long serialVersionUID = 1L;
	
	private final int val;

	private Char(Builder builder) {
		super(builder);
		this.val = builder.val;
	}	
	
	public static Char from(int val) {
		return new Builder(val).build();
	}
	
	public int getValue() {
		return val;
	}

	@Override
	public int hashCode() {
		return val;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;

		if (!(obj instanceof Char))
			return false;
		
		Char other = (Char) obj;
		
		return val == other.val;
	}

	public static String getName(int c) {
		if(CharacterRanges.isPrintableAscii(c)) {
			return (char) c + "";
		} else {
			String s = "\\u" + String.format("%04X", c);
			// Escape newline inside strings
			return s.equals("\\u000D") || s.equals("\\u000A") ? "\\" + s  : s;
		}
	}

	@Override
	public String toString() {
		return getName(val);
	}

	@Override
	public boolean isNullable() {
		return false;
	}
	
	@Override
	public Set<CharRange> getFirstSet() {
		Set<CharRange> firstSet = new HashSet<>();
		firstSet.add(CharRange.in(val, val));
		return firstSet;
	}
	
	@Override
	public Set<CharRange> getNotFollowSet() {
		return Collections.emptySet();
	}

    @Override
    public int length() {
        return 1;
    }

    public static Builder builder(int c) {
		return new Builder(c);
	}
	
	@Override
	public RegexBuilder<Char> copy() {
		return new Builder(this);
	}

	public static class Builder extends RegexBuilder<Char> {
		
		private int val;

		private Builder() {}
		
		public Builder(int val) {
			this.val = val;
		}

		public Builder(Char character) {
			super(character);
			this.val = character.val;
		}
		
		@Override
		public Char build() {
			return new Char(this);
		}
	}
	
	@Override
	public <T> T accept(RegularExpressionVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
