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

package org.iguana.parser.datadependent.excepts;

import iguana.regex.Char;
import iguana.regex.Seq;
import iguana.utils.input.Input;
import org.iguana.grammar.runtime.PrecedenceLevel;
import org.iguana.grammar.runtime.Recursion;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.ParseTreeNode;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static org.iguana.grammar.symbol.LayoutStrategy.NO_LAYOUT;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("unused")
public class Test2_5 {

    @Test
    public void test() {
         RuntimeGrammar grammar =

RuntimeGrammar.builder()

// $default$ ::=  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
// E ::= E ('^') E  {UNDEFINED,1,LEFT_RIGHT_REC} PREC(1,1) hat
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E").build()).addSymbol(new Nonterminal.Builder("E").addExcept("plus") .addExcept("minus").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(94).build()).build()).build()).addSymbol(new Nonterminal.Builder("E").addExcept("hat").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,true,false,false,false)).setLabel("hat").build())
// E ::= E ('*') E  {UNDEFINED,1,LEFT_RIGHT_REC} PREC(1,1) star
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E").build()).addSymbol(new Nonterminal.Builder("E").addExcept("plus").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(42).build()).build()).build()).addSymbol(new Nonterminal.Builder("E").addExcept("star") .addExcept("hat").build()).setRecursion(Recursion.LEFT_RIGHT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,true,false,false,false)).setLabel("star").build())
// E ::= ('a')  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(97).build()).build()).build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,true,false,false,false)).build())
// E ::= ('+') E  {UNDEFINED,1,RIGHT_REC} PREC(1,1) plus
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(43).build()).build()).build()).addSymbol(new Nonterminal.Builder("E").build()).setRecursion(Recursion.RIGHT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,true,false,false,false)).setLabel("plus").build())
// E ::= ('-') E  {UNDEFINED,1,RIGHT_REC} PREC(1,1) minus
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("E").build()).addSymbol(new Terminal.Builder(Seq.builder(Char.builder(45).build()).build()).build()).addSymbol(new Nonterminal.Builder("E").build()).setRecursion(Recursion.RIGHT_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(1).setPrecedenceLevel(PrecedenceLevel.from(1,1,1,true,false,false,false)).setLabel("minus").build())
// S ::= E  {UNDEFINED,-1,NON_REC} PREC(1,1) 
.addRule(RuntimeRule.withHead(new Nonterminal.Builder("S").build()).addSymbol(new Nonterminal.Builder("E").build()).setRecursion(Recursion.NON_REC).setAssociativity(Associativity.UNDEFINED).setPrecedence(-1).setPrecedenceLevel(PrecedenceLevel.from(1,1,-1,false,false,false,false)).build())
.build();
         grammar = new DesugarPrecedenceAndAssociativity().transform(grammar);
         System.out.println(grammar.toStringWithOrderByPrecedence());

         Input input = Input.fromString("a*a^a");

        IguanaParser parser = new IguanaParser(grammar);
        ParseTreeNode result = parser.getParseTree();

        assertNotNull(result);
        assertEquals(0, parser.getStatistics().getAmbiguousNodesCount());
    }
}
