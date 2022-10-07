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

package org.iguana.grammar.transformation;

import org.iguana.datadependent.ast.Expression;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.operations.ReachabilityGraph;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.*;
import org.iguana.traversal.ISymbolVisitor;

import java.util.*;
import java.util.Map;
import java.util.Set;

import static org.iguana.datadependent.ast.AST.*;
import static org.iguana.grammar.condition.DataDependentCondition.predicate;

public class DesugarAlignAndOffside implements GrammarTransformation {
	
	private Map<Nonterminal, Set<Nonterminal>> reachabilityGraph;
	
	private Set<String> offsided;
	
	private boolean doAlign = true;
	
	public void doAlign() {
		this.doAlign = true;
	}
	
	public void doOffside() {
		this.doAlign = false;
	}
	
	@Override
	public RuntimeGrammar transform(RuntimeGrammar grammar) {
		
		if (doAlign) {
			DesugarAlignAndOffsideVisitor desugarAligns = new DesugarAlignAndOffsideVisitor(new HashSet<>());
			desugarAligns.doAlign(doAlign);
			
			Set<RuntimeRule> rules = new LinkedHashSet<>();
			
			for (RuntimeRule rule : grammar.getRules())
				rules.add(desugarAligns.transform(rule, grammar.getLayout()));
			
			return RuntimeGrammar.builder().addRules(rules)
				.setLayout(grammar.getLayout())
				.setStartSymbols(grammar.getStartSymbols())
				.setEbnfLefts(grammar.getEBNFLefts())
				.setEbnfRights(grammar.getEBNFRights())
				.setGlobals(grammar.getGlobals())
				.setRegularExpressionDefinitions(grammar.getRegularExpressionDefinitions())
				.build();
		}
		
		// After EBNF translation
		reachabilityGraph = new ReachabilityGraph(grammar).getReachabilityGraph();
		
		FindOffsidesVisitor findOffsides = new FindOffsidesVisitor();
		findOffsides.find(grammar);
		offsided = findOffsides.getOffsides();
		
		reachabilityGraph.entrySet().forEach(e -> { if (offsided.contains(e.getKey().getName())) {
			e.getValue().forEach(n -> offsided.add(n.getName()));
		}});
				
		DesugarAlignAndOffsideVisitor desugarOffsides = new DesugarAlignAndOffsideVisitor(offsided);
		desugarOffsides.doAlign(doAlign);
		
		Set<RuntimeRule> rules = new LinkedHashSet<>();
		
		for (RuntimeRule rule : grammar.getRules())
			rules.add(desugarOffsides.transform(rule, grammar.getLayout()));
		
		return RuntimeGrammar.builder().addRules(rules)
			.setLayout(grammar.getLayout())
			.setStartSymbols(grammar.getStartSymbols())
			.setGlobals(grammar.getGlobals())
			.setEbnfLefts(grammar.getEBNFLefts())
			.setEbnfRights(grammar.getEBNFRights())
			.setRegularExpressionDefinitions(grammar.getRegularExpressionDefinitions())
			.build();
	}
	
	private static class DesugarAlignAndOffsideVisitor implements ISymbolVisitor<Symbol> {
		
		private static final String ind = "ind";
		private static final String first = "fst";
		private static final String index = "i";
		
		private static final Expression ind_exp = var(ind);
		private static final Expression first_exp = var(first);
		private static final Expression index_exp = var(index);
		
		private static final String l_align = "a";
		private static final String l_offside = "o";
		
		private boolean doAlign;
		
		private final Set<String> offsided;
		
		private RuntimeRule rule;
		private Symbol layout;
		
		private boolean isOffsided;
		
		private int i;
		
		public DesugarAlignAndOffsideVisitor(Set<String> offsided) {
			this.offsided = offsided;
		}
		
		public void doAlign(boolean doAlign) {
			this.doAlign = doAlign;
		}
		
		public RuntimeRule transform(RuntimeRule rule, Symbol layout) {
			this.rule = rule;
			this.layout = layout;
			this.isOffsided = false;
			i = 0;
			
			if (doAlign) {
				List<Symbol> symbols = new ArrayList<>();
				RuntimeRule.Builder builder = rule.copy();
				
				if (this.rule.getBody() != null) {
					
					builder = builder.setSymbols(symbols);
					
					for (Symbol symbol : this.rule.getBody()) {
//						if (symbol instanceof Align) {
//							Symbol sym = ((Align) symbol).getSymbol();
//							if (sym instanceof Plus || sym instanceof Star || sym instanceof Sequence) {
//								Symbol s = symbol.accept(this);
//								String l3 = l_align + i++;
//								Nonterminal longest = getLayout().copyBuilder().setLabel(l3)
//														.addPostCondition(predicate(or(endOfFile(rExt(l3)), lessEq(indent(rExt(l3)), indent(lExt(s.getLabel()))))))
//														.build();
//								symbols.add(s);
//								symbols.add(longest);
//								continue;
//							}
//						}
						symbols.add(symbol.accept(this));
					}
				}
				
				return builder.build();
			}
			
			isOffsided = offsided.contains(this.rule.getHead().getName());
			
			RuntimeRule.Builder builder;
			
			if (isOffsided)
				builder = rule.copyBuilderButWithHead(rule.getHead().copy().addParameters(index, ind, first).build());
			else
				builder = rule.copy();
			
			List<Symbol> symbols = new ArrayList<>();
			
			if (this.rule.getBody() != null) {
				
				builder = builder.setSymbols(symbols);
				
				for (Symbol symbol : this.rule.getBody()) {
					symbols.add(symbol.accept(this));
				}
			}
			
			return builder.build();
		}

		@Override
		public Symbol visit(Align symbol) {
			
			if (doAlign) {
				Symbol sym = symbol.getSymbol().accept(this);
				
				if (sym instanceof Plus) {
					String l1 = getLabel(symbol, sym);
					
					Plus plus = (Plus) sym;
					
					Symbol s = plus.getSymbol();
					String l2 = s.getLabel() != null? s.getLabel() : l_align + i++; // TODO: conflicting labels
					
					s = getSymbol(s, predicate(equal(indent(lExt(l2)), indent(lExt(l1)))), l2);
					
					return new Plus.Builder(s).addSeparators(plus.getSeparators()).setLabel(l1).addConditions(plus).addConditions(symbol).build();
					
				} else if (sym instanceof Star) {
					String l1 = getLabel(symbol, sym);
					
					Star star = (Star) sym;
					
					Symbol s = star.getSymbol();
					String l2 = s.getLabel() != null? s.getLabel() : l_align + i++; // TODO: conflicting labels
					
					s = getSymbol(s, predicate(equal(indent(lExt(l2)), indent(lExt(l1)))), l2);
					
					return new Star.Builder(s).addSeparators(star.getSeparators()).setLabel(l1).addConditions(star).addConditions(symbol).build();
				} else if (sym instanceof Group) {
					String l1 = getLabel(symbol, sym);
					
					@SuppressWarnings("unchecked")
                    Group seq = (Group) sym;
					
					List<Symbol> symbols = seq.getSymbols();
					List<Symbol> syms = new ArrayList<>();
					
					for (Symbol s : symbols) {
						String l2 = s.getLabel() != null? s.getLabel() : l_align + i++; // TODO: conflicting labels
						syms.add(getSymbol(s, predicate(equal(indent(lExt(l2)), indent(lExt(l1)))), l2));
					}
					
					return new Group.Builder(syms).setLabel(l1)
							.addConditions(seq).addConditions(symbol).build();
				} else 
					return sym;
			}
			
			Symbol sym = symbol.getSymbol().accept(this);
			
			return sym == symbol.getSymbol()? symbol 
					: new Align.Builder(sym).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(Block symbol) {
			List<Symbol> symbols = symbol.getSymbols();
			Symbol[] syms = new Symbol[symbols.size()];
			
			int j = 0;
			boolean modified = false;
			for (Symbol sym : symbols) {
				
				syms[j] = sym.accept(this);
				if (sym != syms[j])
					modified |= true;
				j++;
			}
			
			return modified? new Block.Builder(syms).setLabel(symbol.getLabel()).addConditions(symbol).build()
					: symbol;
		}

		@Override
		public Symbol visit(Code symbol) {
			Symbol sym = symbol.getSymbol().accept(this);
			if (sym == symbol.getSymbol())
				return symbol;
			
			return new Code.Builder(sym, symbol.getStatements()).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(Conditional symbol) {
			Symbol sym = symbol.getSymbol().accept(this);
			if (sym == symbol.getSymbol())
				return symbol;
			
			return new Conditional.Builder(sym, symbol.getExpression()).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(IfThen symbol) {
			Symbol sym = symbol.getThenPart().accept(this);
			if (sym == symbol.getThenPart())
				return symbol;
			
			return new IfThen.Builder(symbol.getExpression(), sym).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(IfThenElse symbol) {
			Symbol thenPart = symbol.getThenPart().accept(this);
			Symbol elsePart = symbol.getElsePart().accept(this);
			if (thenPart == symbol.getThenPart() 
					&& elsePart == symbol.getElsePart())
				return symbol;
			
			return new IfThenElse.Builder(symbol.getExpression(), thenPart, elsePart).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(Nonterminal symbol) {
			if (isOffsided && offsided.contains(symbol.getName())) { // The rule has a parameter for indentation, and therefore, also all reachable nonterminals
				String l = symbol.getLabel() != null? symbol.getLabel() : l_offside + i++;
				return symbol.copy().apply(// (fst & (lExt - index == 0)) == 1? index : 0 or fst == 1? index : 0
						                          //     after non-nullable (0 as only indentation will be needed)
						                          andIndent(index_exp, first_exp, lExt(l), true), 
												  ind_exp,
												  // fst & (lExt - index == 0) or 0 after non-nullable
												  andIndent(index_exp, first_exp, lExt(l)))
												  .setLabel(l).build();
			} else if (offsided.contains(symbol.getName())) // A ::= offside B; B ::= D; C ::= B or C ::= D
				return symbol.copy().apply(integer(0), integer(0), integer(0)).build();
			else
				return symbol;
		}
		
		@Override
		public Symbol visit(Ignore symbol) {
			if (doAlign) {
				Symbol sym = symbol.getSymbol().accept(this);
				
				return sym == symbol.getSymbol()? symbol 
						: new Ignore.Builder(sym).setLabel(symbol.getLabel()).addConditions(symbol).build();
			}
			
			Symbol sym = symbol.getSymbol();
			
			if (sym instanceof Nonterminal) {
				Nonterminal s = (Nonterminal) sym;
				
				if (offsided.contains(s.getName())) { // TODO: too general
					return s.copy()
							.apply(integer(0), integer(0), integer(0))
							.addConditions(symbol)
							.addConditions(sym)
							.build();
				}
			}
				
			// Otherwise, ignore 'ignore'
			sym = sym.accept(this);
			return sym.copy().addConditions(symbol).build();
		}

		@Override
		public Symbol visit(Offside symbol) {
			
			if (doAlign) {
				Symbol sym = symbol.getSymbol().accept(this);
				
				return sym == symbol.getSymbol()? symbol 
						: new Offside.Builder(sym).setLabel(symbol.getLabel()).addConditions(symbol).build();
			}
			
			Symbol sym = symbol.getSymbol();
			
			if (sym instanceof Nonterminal) {
				Nonterminal s = (Nonterminal) sym;
				
				String l = symbol.getLabel();
				if (l != null && s.getLabel() != null) {
					if (!l.equals(s.getLabel()))
						throw new RuntimeException("Conflicting labels: " + symbol);
				} else if (s.getLabel() != null)
					l = s.getLabel();
				else if (l == null && s.getLabel() == null)
				    l = l_offside + i++;
				
				if (isOffsided) { // Offside inside a rule that has a parameter for indentation
					return s.copy()
							.apply(lExt(l), indent(lExt(l)), integer(1))
							.setLabel(l)
							.addConditions(symbol)
							.addConditions(sym)
							// [ ind == 0 || (first && l.lExt - index == 0) || indent(l.lExt) > ind]
							.addPreCondition(predicate(orIndent(index_exp, ind_exp, first_exp, lExt(l))))
							.build();
				} else {
					return s.copy()
							.apply(lExt(l), indent(lExt(l)), integer(1))
							.setLabel(l)
							.addConditions(symbol)
							.addConditions(sym)
							.build();
				}
			} 
			
			// Otherwise, ignore offside
			sym = sym.accept(this);
			return sym.copy().addConditions(symbol).build();
		}

		@Override
		public Symbol visit(Terminal symbol) {
			if (isOffsided) {
				String l = symbol.getLabel() != null? symbol.getLabel() : l_offside + i++;
				return symbol.copy()
								.setLabel(l)
								.addConditions(symbol)
								.addPreCondition(predicate(orIndent(index_exp, ind_exp, first_exp, lExt(l))))
								.build();
			}
			return symbol;
		}

		@Override
		public Symbol visit(While symbol) {
			Symbol body = symbol.getBody().accept(this);
			if (body == symbol.getBody()) 
				return symbol;
			
			return new While.Builder(symbol.getExpression(), body).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}
		
		@Override
		public Symbol visit(Return symbol) {
			return symbol;
		}

		@Override
		public Symbol visit(Alt symbol) {
			
			List<? extends Symbol> symbols = symbol.getSymbols();
			List<Symbol> syms = new ArrayList<>();
			
			boolean modified = false;
			for (Symbol sym : symbols) {
				Symbol s = sym.accept(this);
				syms.add(s);
				if (sym != s)
					modified |= true;
			}
			
			return modified? new Alt.Builder(syms).setLabel(symbol.getLabel()).addConditions(symbol).build()
					       : symbol;
		}

		@Override
		public Symbol visit(Opt symbol) {
			Symbol sym = symbol.getSymbol().accept(this);
			if (sym == symbol.getSymbol())
				return symbol;
			return new Opt.Builder(sym).setLabel(symbol.getLabel()).addConditions(symbol).build();
		}

		@Override
		public Symbol visit(Plus symbol) {
			
			Symbol sym = symbol.getSymbol().accept(this);
			List<Symbol> separators = symbol.getSeparators();
			
			boolean modified = sym != symbol.getSymbol();
			
			List<Symbol> seps = new ArrayList<>();
			for (Symbol sep : separators) {
				Symbol s = sep.accept(this);
				seps.add(s);
				if (s != sep)
					modified |= true;
			}
			
			return modified? new Plus.Builder(sym).addSeparators(seps).setLabel(symbol.getLabel()).addConditions(symbol).build()
					       : symbol;
		}

		@Override
		public Symbol visit(Group symbol) {
			
			List<? extends Symbol> symbols = symbol.getSymbols();
			List<Symbol> syms = new ArrayList<>();
			
			boolean modified = false;
			for (Symbol sym : symbols) {
				Symbol s = sym.accept(this);
				syms.add(s);
				if (sym != s)
					modified |= true;
			}
			
			return modified? new Group.Builder(syms).setLabel(symbol.getLabel()).addConditions(symbol).build()
					       : symbol;
		}

		@Override
		public Symbol visit(Star symbol) {
			Symbol sym = symbol.getSymbol().accept(this);
			List<Symbol> separators = symbol.getSeparators();
			
			boolean modified = sym != symbol.getSymbol();
			
			List<Symbol> seps = new ArrayList<>();
			for (Symbol sep : separators) {
				Symbol s = sep.accept(this);
				seps.add(s);
				if (s != sep)
					modified |= true;
			}
			
			return modified? new Star.Builder(sym).addSeparators(seps).setLabel(symbol.getLabel()).addConditions(symbol).build()
					       : symbol;
		}

        @Override
        public Symbol visit(Start start) {
            return null;
        }

        private String getLabel(Align align, Symbol symbol) {
			String label = null;
			
			if (align.getLabel() != null && symbol.getLabel() != null) {
				if (!align.getLabel().equals(symbol.getLabel()))
					throw new RuntimeException("Two conflicting labels: " + align);
				else 
					label = align.getLabel();
			} else if (align.getLabel() != null)
				label = align.getLabel();
			else if (symbol.getLabel() != null)
				label = symbol.getLabel();
			
			return label != null? label : l_align + i++;
		}
		
		private Symbol getSymbol(Symbol symbol, Condition precondition, String label) {
			
			if (symbol instanceof Offside) {
				Offside sym = (Offside) symbol;
				return new Offside.Builder(sym.getSymbol().copy().addPreCondition(precondition).setLabel(label).build())
									.addConditions(symbol).setLabel(symbol.getLabel()).build();
			}
			
			return symbol.copy().addPreCondition(precondition).setLabel(label).build();
		}
		
		@SuppressWarnings("unused")
		private Symbol getLayout() {
			switch(rule.getLayoutStrategy()) {
				case NO_LAYOUT:
					throw new RuntimeException("Align should not be part of lexicals.");
				case INHERITED:
					return layout;
				case FIXED:
					return rule.getLayout();
			}
			return layout;
		}
		
	}
	
	private static class FindOffsidesVisitor implements ISymbolVisitor<Void> {
		
		private final Set<String> offsided;
		
		public FindOffsidesVisitor() {
			this.offsided = new HashSet<>();
		}
		
		public Set<String> getOffsides() {
			return offsided;
		}
		
		public void find(RuntimeGrammar grammar) {
			
			for (RuntimeRule rule : grammar.getRules()) {
				if (rule.getBody() == null)
					continue;
				
				for (Symbol s : rule.getBody())
					s.accept(this);
			}
		}
		
		@Override
		public Void visit(Offside symbol) {
			Symbol sym = symbol.getSymbol();
			
			// Offside will only be applied to nonterminals 
			if (sym instanceof Nonterminal)
				offsided.add(((Nonterminal) sym).getName());
			
			return sym.accept(this);
		}

		@Override
		public Void visit(Align symbol) {
			return symbol.getSymbol().accept(this);
		}

		@Override
		public Void visit(Block symbol) {
			for (Symbol sym : symbol.getSymbols())
				sym.accept(this);
			return null;
		}

		@Override
		public Void visit(Code symbol) {
			return symbol.getSymbol().accept(this);
		}

		@Override
		public Void visit(Conditional symbol) {
			return symbol.getSymbol().accept(this);
		}

		@Override
		public Void visit(IfThen symbol) {
			return symbol.getThenPart().accept(this);
		}

		@Override
		public Void visit(IfThenElse symbol) {
			symbol.getThenPart().accept(this);
			symbol.getElsePart().accept(this);
			return null;
		}
		
		@Override
		public Void visit(Ignore symbol) {
			return symbol.getSymbol().accept(this);
		}

		@Override
		public Void visit(Nonterminal symbol) {
			return null;
		}

		@Override
		public Void visit(Terminal symbol) {
			return null;
		}

		@Override
		public Void visit(While symbol) {
			return symbol.getBody().accept(this);
		}
		
		@Override
		public Void visit(Return symbol) {
			// TODO: support for return
			return null;
		}

		@Override
		public Void visit(Alt symbol) {
			for (Symbol sym : symbol.getSymbols())
				sym.accept(this);
			return null;
		}

		@Override
		public Void visit(Opt symbol) {
			return symbol.getSymbol().accept(this);
		}

		@Override
		public Void visit(Plus symbol) {
			for (Symbol sym : symbol.getSeparators())
				sym.accept(this);
			return symbol.getSymbol().accept(this);
		}

		@Override
		public Void visit(Group symbol) {
			for (Symbol sym : symbol.getSymbols())
				sym.accept(this);
			return null;
		}

		@Override
		public Void visit(Star symbol) {
			for (Symbol sym : symbol.getSeparators())
				sym.accept(this);
			return symbol.getSymbol().accept(this);
		}

        @Override
        public Void visit(Start start) {
//		    start.getNonterminal().accept(this);
            return null;
        }

    }

}
