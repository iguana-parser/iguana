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

package org.iguana.grammar.symbol;

import iguana.utils.collections.hash.MurmurHash3;
import org.iguana.grammar.slot.NonterminalNodeType;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 
 * @author Ali Afroozeh
 * @author Anastasia Izmaylova
 *
 */
public class Rule implements Serializable {
	
	private static final long serialVersionUID = 1L;

    private final Nonterminal head;

    private final List<Symbol> body;

	private final Nonterminal layout;
	
	private final LayoutStrategy layoutStrategy;
	
	private final Recursion recursion;
	private final Recursion irecursion;
	
	private final String leftEnd;
	private final String rightEnd;
	
	private final Set<String> leftEnds;
	private final Set<String> rightEnds;
		
	private final Associativity associativity;
	private final AssociativityGroup associativityGroup;
	
	private final int precedence;
	private final PrecedenceLevel precedenceLevel;
	
	private final String label;
	
    private final Map<String, Object> attributes;

    private final Symbol definition; // Used for converted EBNF nodes

    public Rule(Builder builder) {
        this.body = builder.body;
        this.head = builder.head;
        this.layout = builder.layout;
        this.layoutStrategy = builder.layoutStrategy;
        this.recursion = builder.recursion;
        this.irecursion = builder.irecursion;
        this.leftEnd = builder.leftEnd;
        this.rightEnd = builder.rightEnd;
        this.leftEnds = builder.leftEnds;
        this.rightEnds = builder.rightEnds;
        this.associativity = builder.associativity;

        this.associativityGroup = builder.associativityGroup;
        this.precedence = builder.precedence;
        this.precedenceLevel = builder.precedenceLevel;

        this.label = builder.label;

        this.attributes = builder.attributes;

        this.definition = builder.definition;
    }

    public Nonterminal getHead() {
		return head;
	}
	
	public List<Symbol> getBody() {
		return body;
	}
	
	public int size() {
		return body == null ? 0 : body.size();
	}
	
	public Symbol symbolAt(int i) {
		if (i > body.size())
			throw new IllegalArgumentException(i + " cannot be greater than " + body.size());
		
		return body.get(i);
	}
	
	public Nonterminal getLayout() {
		return layout;
	}
	
	public LayoutStrategy getLayoutStrategy() {
		return layoutStrategy;
	}
	
	public boolean isUnary() {
		return recursion == Recursion.LEFT_REC || recursion == Recursion.RIGHT_REC;
	}
	
	public boolean isLeftRecursive() {
		return recursion == Recursion.LEFT_RIGHT_REC || recursion == Recursion.LEFT_REC;
	}
	
	public boolean isILeftRecursive() {
		return irecursion == Recursion.iLEFT_RIGHT_REC || irecursion == Recursion.iLEFT_REC;
	}
	
	public boolean isRightRecursive() {
		return recursion == Recursion.LEFT_RIGHT_REC || recursion == Recursion.RIGHT_REC;
	}
	
	public boolean isIRightRecursive() {
		return irecursion == Recursion.iLEFT_RIGHT_REC || irecursion == Recursion.iRIGHT_REC;
	}
	
	public boolean isLeftOrRightRecursive() {
		return recursion == Recursion.LEFT_RIGHT_REC || recursion == Recursion.LEFT_REC || recursion == Recursion.RIGHT_REC;
	}
	
	public boolean isILeftOrRightRecursive() {
		return irecursion == Recursion.iLEFT_RIGHT_REC || irecursion == Recursion.iLEFT_REC || irecursion == Recursion.iRIGHT_REC;
	}
	
	public Recursion getRecursion() {
		return recursion;
	}
	
	public Recursion getIRecursion() {
		return irecursion;
	}
	
	public String getLeftEnd() {
		return leftEnd;
	}
	
	public String getRightEnd() {
		return rightEnd;
	}
	
	public Set<String> getLeftEnds() {
		return leftEnds;
	}
	
	public Set<String> getRightEnds() {
		return rightEnds;
	}
	
	public Associativity getAssociativity() {
		return associativity;
	}
	
	public AssociativityGroup getAssociativityGroup() {
		return associativityGroup;
	}
	
	public int getPrecedence() {
		return precedence;
	}
	
	public PrecedenceLevel getPrecedenceLevel() {
		return precedenceLevel;
	}
	
	public String getLabel() {
		return label;
	}
	
	public boolean hasLayout() {
		return layout != null;
	}
	
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public Symbol getDefinition() {
        return definition;
    }

    @Override
	public String toString() {

		if(body == null) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(head).append(" ::= ");
		for(Symbol s : body) {
			sb.append(s).append(" ");
		}

        if (associativity != Associativity.UNDEFINED && precedence != 0) {
            sb.append(" {").append(associativity.name());
            if (precedence != 0) sb.append(", " + precedence);
            if (recursion != Recursion.NON_REC) sb.append(", " + recursion);
            sb.append(" }");
        }

        if (associativityGroup != null) sb.append(associativityGroup + " " );
        if (precedenceLevel != null) sb.append(precedenceLevel + " ");
        if (label != null) sb.append(label);

        return sb.toString();
	}
	
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Rule)) return false;
		
		Rule other = (Rule) obj;

		if (body == null && other.body != null) {
            System.out.println("Hi!!!");
        }
		if (body != null && !body.equals(other.body)) {
            System.out.println("fuck!");
        }
		return head.equals(other.head) && (body == null ? other.body == null : body.equals(other.body));
	}
	
	@Override
	public int hashCode() {
		return MurmurHash3.fn().apply(head, body);
	}
	
	public Position getPosition(int i) {
		if (i < 0)
			throw new IllegalArgumentException("i cannot be less than zero.");
		
		if (i > size())
			throw new IllegalArgumentException("i cannot be greater than the size.");
		
		return new Position(this, i);
	}
	
	public Position getPosition(int i, int j) {
		if (i < 0)
			throw new IllegalArgumentException("i cannot be less than zero.");
		
		if (i > size())
			throw new IllegalArgumentException("i cannot be greater than the size.");
		
		return new Position(this, i, j);
	}
	
	public Builder copyBuilder() {
		return new Builder(this);
	}
	
	public Builder copyBuilderButWithHead(Nonterminal nonterminal) {
		Builder builder = new Builder(this);
		builder.head = nonterminal;
		return builder;
	}
	
	public static Builder withHead(Nonterminal nonterminal) {
		return new Builder(nonterminal);
	}

    public String head() {
        return head.getName();
    }

    public String label() {
    	return label;
    }
        

    public int position() {
        return size();
    }

    public boolean isLayout() {
        return head.getNodeType() == NonterminalNodeType.Layout;
    }

    public static class Builder {

        private Nonterminal head;
        private List<Symbol> body;
        private Serializable object;
        private LayoutStrategy layoutStrategy = LayoutStrategy.INHERITED;
        private Nonterminal layout;

        private Recursion recursion = Recursion.NON_REC;
        private Recursion irecursion = Recursion.NON_REC;

        private String leftEnd = "";
        private String rightEnd = "";
        private Set<String> leftEnds = new HashSet<>();
        private Set<String> rightEnds = new HashSet<>();

        private Associativity associativity = Associativity.UNDEFINED;
        private AssociativityGroup associativityGroup;

        private int precedence;
        private PrecedenceLevel precedenceLevel;

        private String label;

        private Map<String, Object> attributes = new HashMap<>();
        private Symbol definition;

        public Builder(Nonterminal head) {
            this.head = head;
            this.body = new ArrayList<>();
        }

        public Builder() {}

        public Builder(Rule rule) {
            this.head = rule.head;
            this.body = rule.body;
            this.layoutStrategy = rule.layoutStrategy;
            this.layout = rule.layout;
            this.recursion = rule.recursion;
            this.irecursion = rule.irecursion;
            this.leftEnd = rule.leftEnd;
            this.rightEnd = rule.rightEnd;
            this.leftEnds = rule.leftEnds;
            this.rightEnds = rule.rightEnds;
            this.associativity = rule.associativity;

            this.associativityGroup = rule.associativityGroup;
            this.precedence = rule.precedence;
            this.precedenceLevel = rule.precedenceLevel;

            this.label = rule.label;

            this.attributes = rule.attributes;
            this.definition = rule.definition;
        }

        public Builder addSymbol(Symbol symbol) {
            body.add(symbol);
            return this;
        }

        public Builder addSymbols(Symbol... symbols) {
            body.addAll(Arrays.asList(symbols));
            return this;
        }

        public Builder addSymbols(List<Symbol> symbols) {
            body.addAll(symbols);
            return this;
        }

        public Builder setSymbols(List<Symbol> symbols) {
            body = symbols;
            return this;
        }

        public Builder setLayoutStrategy(LayoutStrategy layoutStrategy) {
            this.layoutStrategy = layoutStrategy;
            return this;
        }

        public Builder setObject(Serializable object) {
            this.object = object;
            return this;
        }

        public Builder setLayout(Nonterminal layout) {
            this.layoutStrategy = LayoutStrategy.FIXED;
            this.layout = layout;
            return this;
        }

        public Builder setRecursion(Recursion recursion) {
            this.recursion = recursion;
            return this;
        }

        public Builder setiRecursion(Recursion irecursion) {
            this.irecursion = irecursion;
            return this;
        }

        public Builder setLeftEnd(String end) {
            this.leftEnd = end;
            return this;
        }

        public Builder setRightEnd(String end) {
            this.rightEnd = end;
            return this;
        }

        public Builder setLeftEnds(Set<String> leftEnds) {
            if (leftEnds != null)
                this.leftEnds = leftEnds;
            return this;
        }

        public Builder setRightEnds(Set<String> rightEnds) {
            if (rightEnds != null)
                this.rightEnds = rightEnds;
            return this;
        }

        public Builder setAssociativity(Associativity associativity) {
            this.associativity = associativity;
            return this;
        }

        public Builder setAssociativityGroup(AssociativityGroup associativityGroup) {
            this.associativityGroup = associativityGroup;
            return this;
        }

        public Builder setPrecedence(int precedence) {
            this.precedence = precedence;
            return this;
        }

        public Builder setPrecedenceLevel(PrecedenceLevel precedenceLevel) {
            this.precedenceLevel = precedenceLevel;
            return this;
        }

        public Builder setLabel(String label) {
            this.label = label;
            return this;
        }

        public Builder setAttributes(Map<String, Object> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder addAttribute(String key, Object value) {
            this.attributes.put(key, value);
            return this;
        }

        public Builder addAttributes(Map<String, Object> attributes) {
            this.attributes.putAll(attributes);
            return this;
        }

        public Builder setDefinition(Symbol definition) {
            this.definition = definition;
            return this;
        }

        public Rule build() {
            if (body == null) body = Collections.emptyList();
            return new Rule(this);
        }
    }
	
}
