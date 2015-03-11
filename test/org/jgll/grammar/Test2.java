package org.jgll.grammar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jgll.grammar.patterns.ExceptPattern;
import org.jgll.grammar.patterns.PrecedencePattern;
import org.jgll.grammar.symbol.Associativity;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.PrecedenceLevel;
import org.jgll.grammar.symbol.Recursion;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Terminal;
import org.jgll.regex.Sequence;

import static org.jgll.grammar.symbol.LayoutStrategy.*;

public class Test2 {
	public static Grammar grammar =
			Grammar.builder()

			// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1) 
			.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
			// E ::= E (+) E  {LEFT,1,LEFT_RIGHT_REC} PREC(1,1) 
			.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(43).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
			// E ::= (a)  {UNDEFINED,-1,NON_REC} PREC(1,1) 
			.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(97).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
			// E ::= E (*) E  {LEFT,2,LEFT_RIGHT_REC} PREC(2,2) 
			.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(42).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.LEFT).setPrecedence(2).setPrecedenceLevel(PrecedenceLevel.from(2,2,-1,false,false,false,false)).build())
			// E ::= (-) E  {UNDEFINED,3,RIGHT_REC} PREC(3,3) 
			.addRule(Rule.withHead(Nonterminal.builder("E").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build()).build()).build()).addSymbol(Nonterminal.builder("E").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.RIGHT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(3).setPrecedenceLevel(PrecedenceLevel.from(3,3,3,true,false,false,false)).build())
			.build();

}
