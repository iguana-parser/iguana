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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.Set;

import static iguana.utils.collections.CollectionsUtil.immutableSet;

public class Epsilon extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	public static int VALUE = -2;
	
	private static final Set<org.iguana.regex.CharRange> firstSet = immutableSet(org.iguana.regex.CharRange.in(VALUE, VALUE));
	
	private static Epsilon instance;

	public static Epsilon getInstance() {
		if(instance == null)
			instance = new Epsilon();
		
		return instance;
	}
	
	private static org.iguana.regex.RegexBuilder<Epsilon> builder =
			new org.iguana.regex.RegexBuilder<Epsilon>() {
					@Override
					public Epsilon build() {
						return Epsilon.getInstance();
					}};

    public static org.iguana.regex.CharRange asCharRange() {
        return org.iguana.regex.CharRange.in(VALUE, VALUE);
    }
	
	private Epsilon() {
		super(builder);
	}

	private Object readResolve()  {
	    return instance;
	}
	
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
	    ois.defaultReadObject();
	    instance = this;
	}

    @Override
    public String toString() {
        return "epsilon";
    }

	@Override
	public boolean isNullable() {
		return true;
	}
	
	@Override
	public Set<org.iguana.regex.CharRange> getFirstSet() {
		return firstSet;
	}
	
	@Override
	public Set<CharRange> getNotFollowSet() {
		return Collections.emptySet();
	}

    @Override
    public int length() {
        return 0;
    }

    @Override
	public RegexBuilder<Epsilon> copy() {
		return builder;
	}

	@Override
	public <T> T accept(RegularExpressionVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
