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

package org.iguana.regex.automaton;

import org.iguana.regex.CharRange;
import org.iguana.regex.EOF;
import org.iguana.utils.collections.hash.MurmurHash3;

import java.io.Serializable;

public class Transition implements Comparable<Transition>, Serializable {
	
	private static final long serialVersionUID = 1L;

	private CharRange range;
	
	private org.iguana.regex.automaton.State destination;
	
	private int id;
	
	public Transition(int start, int end, org.iguana.regex.automaton.State destination) {
		this(CharRange.in(start, end), destination);
	}
	
	public Transition(int c, org.iguana.regex.automaton.State destination) {
		this(c, c, destination);
	}
	
	public Transition(CharRange range, org.iguana.regex.automaton.State destination) {
		if (range.getEnd() < range.getStart())
			throw new IllegalArgumentException("start cannot be less than end.");
		
		if (destination == null) 
			throw new IllegalArgumentException("Destination cannot be null.");

		this.range = range;
		this.destination = destination;
	}
	
	public static Transition EOFTransition(org.iguana.regex.automaton.State destination) {
		return new Transition(EOF.VALUE, destination);
	}
	
	public int getStart() {
		return range.getStart();
	}
	
	public int getEnd() {
		return range.getEnd();
	}
	
	public CharRange getRange() {
		return range;
	}
	
	public org.iguana.regex.automaton.State getDestination() {
		return destination;
	}
	
	public boolean isEpsilonTransition() {
		return range.getStart() == -1;
	}
	
	public boolean isLoop(State source) {
		return source == destination;
	}
	
	public boolean canMove(int c) {
		return range.contains(c);
	}
	
	public boolean overlaps(Transition t) {
		return range.overlaps(t.range);
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj)
			return true;
		
		if(!(obj instanceof Transition))
			return false;
		
		Transition other = (Transition) obj;
		
		return isEpsilonTransition() ? other.isEpsilonTransition() && this == other : range.equals(other.range);
	}
	
	@Override
	public int hashCode() {
		
		if (isEpsilonTransition())
			return super.hashCode();
		
		return MurmurHash3.f2().apply(range.getStart(), range.getEnd());
	}
	
	@Override
	public String toString() {
		if (isEpsilonTransition()) return "-1";

		return range.toString();
	}

	@Override
	public int compareTo(Transition t) {
		return range.compareTo(t.range);
	}
		
}
