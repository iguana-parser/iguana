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

package org.iguana.parser.datadependent;

import static org.iguana.datadependent.ast.AST.equal;
import static org.iguana.datadependent.ast.AST.indent;
import static org.iguana.datadependent.ast.AST.lExt;
import static org.iguana.datadependent.ast.AST.less;
import static org.iguana.datadependent.ast.AST.var;
import static org.iguana.grammar.condition.DataDependentCondition.predicate;
import static org.iguana.util.CollectionsUtil.set;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.condition.ConditionType;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.parser.GLLParser;
import org.iguana.parser.GLLParserImpl;
import org.iguana.parser.ParseResult;
import org.iguana.regex.Alt;
import org.iguana.regex.Plus;
import org.iguana.regex.Sequence;
import org.iguana.regex.Star;
import org.iguana.util.Configuration;
import org.junit.Test;

import iguana.utils.input.Input;

public class Python {
	
	public static Grammar grammar = 
			Grammar.builder()
			//ShortBytesChar ::= (\u0001-\u10FFFF) 
			.addRule(Rule.withHead(Nonterminal.builder("ShortBytesChar").build()).addSymbol(Alt.builder(CharacterRange.builder(1, 1114111).build()).addPostConditions(set(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Character.builder(10).build()).build()))).build()).build())
			//IntPart ::= Digit+ 
			.addRule(Rule.withHead(Nonterminal.builder("IntPart").build()).addSymbol(Plus.builder(Nonterminal.builder("Digit").build()).build()).build())
			//BinInteger ::= "0" ("b" | "B") BinDigit+ 
			.addRule(Rule.withHead(Nonterminal.builder("BinInteger").build()).addSymbol(Sequence.builder(Character.builder(48).build()).build()).addSymbol(Alt.builder(Sequence.builder(Character.builder(98).build()).build(), Sequence.builder(Character.builder(66).build()).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("BinDigit").build()).build()).build())
			//LongBytesChar ::= (\u0001-\u10FFFF) 
			.addRule(Rule.withHead(Nonterminal.builder("LongBytesChar").build()).addSymbol(Alt.builder(CharacterRange.builder(1, 1114111).build()).addPostConditions(set(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Character.builder(92).build()).build()))).build()).build())
			//Term ::= Factor (("/" | "*" | "%" | "//") Factor)* 
			.addRule(Rule.withHead(Nonterminal.builder("Term").build()).addSymbol(Nonterminal.builder("Factor").build()).addSymbol(Star.builder(Sequence.builder(Alt.builder(Sequence.builder(Character.builder(47).build()).build(), Sequence.builder(Character.builder(42).build()).build(), Sequence.builder(Character.builder(37).build()).build(), Sequence.builder(Character.builder(47).build(), Character.builder(47).build()).build()).build(), Nonterminal.builder("Factor").build()).build()).build()).build())
			//ImportStmt ::= ImportName 
			.addRule(Rule.withHead(Nonterminal.builder("ImportStmt").build()).addSymbol(Nonterminal.builder("ImportName").build()).build())
			//ImportStmt ::= ImportFrom 
			.addRule(Rule.withHead(Nonterminal.builder("ImportStmt").build()).addSymbol(Nonterminal.builder("ImportFrom").build()).build())
			//Parameters ::= "(" TypedArgsList? ")" 
			.addRule(Rule.withHead(Nonterminal.builder("Parameters").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Nonterminal.builder("TypedArgsList").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
			//DottedAsNames ::= DottedAsName ("," DottedAsName)* 
			.addRule(Rule.withHead(Nonterminal.builder("DottedAsNames").build()).addSymbol(Nonterminal.builder("DottedAsName").build()).addSymbol(Star.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Nonterminal.builder("DottedAsName").build()).build()).build()).build())
			//ShortString ::= """ ShortStringItem* """ 
			.addRule(Rule.withHead(Nonterminal.builder("ShortString").build()).addSymbol(Sequence.builder(Character.builder(34).build()).build()).addSymbol(Star.builder(Nonterminal.builder("ShortStringItem").build()).build()).addSymbol(Sequence.builder(Character.builder(34).build()).build()).build())
			//ShortString ::= "'" ShortStringItem* "'" 
			.addRule(Rule.withHead(Nonterminal.builder("ShortString").build()).addSymbol(Sequence.builder(Character.builder(39).build()).build()).addSymbol(Star.builder(Nonterminal.builder("ShortStringItem").build()).build()).addSymbol(Sequence.builder(Character.builder(39).build()).build()).build())
			//ShortStringItem ::= ShortStringChar 
			.addRule(Rule.withHead(Nonterminal.builder("ShortStringItem").build()).addSymbol(Nonterminal.builder("ShortStringChar").build()).build())
			//ShortStringItem ::= StringEscapeSeq 
			.addRule(Rule.withHead(Nonterminal.builder("ShortStringItem").build()).addSymbol(Nonterminal.builder("StringEscapeSeq").build()).build())
			//EncodingDecl ::= Name 
			.addRule(Rule.withHead(Nonterminal.builder("EncodingDecl").build()).addSymbol(Nonterminal.builder("Name").build()).build())
			//LongStringItem ::= LongStringChar 
			.addRule(Rule.withHead(Nonterminal.builder("LongStringItem").build()).addSymbol(Nonterminal.builder("LongStringChar").build()).build())
			//LongStringItem ::= StringEscapeSeq 
			.addRule(Rule.withHead(Nonterminal.builder("LongStringItem").build()).addSymbol(Nonterminal.builder("StringEscapeSeq").build()).build())
			//empty() ::= 
			.addRule(Rule.withHead(Nonterminal.builder("empty()").build()).build())
			//FloatNumber ::= ExponentFloat 
			.addRule(Rule.withHead(Nonterminal.builder("FloatNumber").build()).addSymbol(Nonterminal.builder("ExponentFloat").build()).build())
			//FloatNumber ::= PointFloat 
			.addRule(Rule.withHead(Nonterminal.builder("FloatNumber").build()).addSymbol(Nonterminal.builder("PointFloat").build()).build())
			//StringLiteral ::= StringPrefix? (LongString | ShortString) 
			.addRule(Rule.withHead(Nonterminal.builder("StringLiteral").build()).addSymbol(org.iguana.regex.Opt.builder(Nonterminal.builder("StringPrefix").build()).build()).addSymbol(Alt.builder(Nonterminal.builder("LongString").build(), Nonterminal.builder("ShortString").build()).build()).build())
			//WhileStmt ::= "while" Test ":" Suite ("else" ":" Suite)? 
			.addRule(Rule.withHead(Nonterminal.builder("WhileStmt").build()).addSymbol(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(105).build(), Character.builder(108).build(), Character.builder(101).build()).build()).addSymbol(Nonterminal.builder("Test").build()).addSymbol(Sequence.builder(Character.builder(58).build()).build()).addSymbol(Nonterminal.builder("Suite").build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(101).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build(), Sequence.builder(Character.builder(58).build()).build(), Nonterminal.builder("Suite").build()).build()).build()).build())
			//SmallStmt ::= PassStmt 
			.addRule(Rule.withHead(Nonterminal.builder("SmallStmt").build()).addSymbol(Nonterminal.builder("PassStmt").build()).build())
			//SmallStmt ::= ExprStmt 
			.addRule(Rule.withHead(Nonterminal.builder("SmallStmt").build()).addSymbol(Nonterminal.builder("ExprStmt").build()).build())
			//SmallStmt ::= DelStmt 
			.addRule(Rule.withHead(Nonterminal.builder("SmallStmt").build()).addSymbol(Nonterminal.builder("DelStmt").build()).build())
			//SmallStmt ::= NonlocalStmt 
			.addRule(Rule.withHead(Nonterminal.builder("SmallStmt").build()).addSymbol(Nonterminal.builder("NonlocalStmt").build()).build())
			//SmallStmt ::= ImportStmt 
			.addRule(Rule.withHead(Nonterminal.builder("SmallStmt").build()).addSymbol(Nonterminal.builder("ImportStmt").build()).build())
			//SmallStmt ::= AssertStmt 
			.addRule(Rule.withHead(Nonterminal.builder("SmallStmt").build()).addSymbol(Nonterminal.builder("AssertStmt").build()).build())
			//SmallStmt ::= FlowStmt 
			.addRule(Rule.withHead(Nonterminal.builder("SmallStmt").build()).addSymbol(Nonterminal.builder("FlowStmt").build()).build())
			//SmallStmt ::= GlobalStmt 
			.addRule(Rule.withHead(Nonterminal.builder("SmallStmt").build()).addSymbol(Nonterminal.builder("GlobalStmt").build()).build())
			//HexDigit ::= (A-F) 
			.addRule(Rule.withHead(Nonterminal.builder("HexDigit").build()).addSymbol(Alt.builder(CharacterRange.builder(65, 70).build()).build()).build())
			//HexDigit ::= Digit 
			.addRule(Rule.withHead(Nonterminal.builder("HexDigit").build()).addSymbol(Nonterminal.builder("Digit").build()).build())
			//HexDigit ::= (a-f) 
			.addRule(Rule.withHead(Nonterminal.builder("HexDigit").build()).addSymbol(Alt.builder(CharacterRange.builder(97, 102).build()).build()).build())
			//ImagNumber ::= (IntPart | FloatNumber) ("j" | "J") 
			.addRule(Rule.withHead(Nonterminal.builder("ImagNumber").build()).addSymbol(Alt.builder(Nonterminal.builder("IntPart").build(), Nonterminal.builder("FloatNumber").build()).build()).addSymbol(Alt.builder(Sequence.builder(Character.builder(106).build()).build(), Sequence.builder(Character.builder(74).build()).build()).build()).build())
			//Keyword ::= "yield" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(121).build(), Character.builder(105).build(), Character.builder(101).build(), Character.builder(108).build(), Character.builder(100).build()).build()).build())
			//Keyword ::= "lambda" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(108).build(), Character.builder(97).build(), Character.builder(109).build(), Character.builder(98).build(), Character.builder(100).build(), Character.builder(97).build()).build()).build())
			//Keyword ::= "raise" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(114).build(), Character.builder(97).build(), Character.builder(105).build(), Character.builder(115).build(), Character.builder(101).build()).build()).build())
			//Keyword ::= "while" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(105).build(), Character.builder(108).build(), Character.builder(101).build()).build()).build())
			//Keyword ::= "not" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(110).build(), Character.builder(111).build(), Character.builder(116).build()).build()).build())
			//Keyword ::= "finally" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(102).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(97).build(), Character.builder(108).build(), Character.builder(108).build(), Character.builder(121).build()).build()).build())
			//Keyword ::= "and" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(97).build(), Character.builder(110).build(), Character.builder(100).build()).build()).build())
			//Keyword ::= "or" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(111).build(), Character.builder(114).build()).build()).build())
			//Keyword ::= "class" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(99).build(), Character.builder(108).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build()).build()).build())
			//Keyword ::= "break" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(98).build(), Character.builder(114).build(), Character.builder(101).build(), Character.builder(97).build(), Character.builder(107).build()).build()).build())
			//Keyword ::= "as" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(97).build(), Character.builder(115).build()).build()).build())
			//Keyword ::= "import" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build()).build())
			//Keyword ::= "from" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(102).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(109).build()).build()).build())
			//Keyword ::= "global" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(103).build(), Character.builder(108).build(), Character.builder(111).build(), Character.builder(98).build(), Character.builder(97).build(), Character.builder(108).build()).build()).build())
			//Keyword ::= "assert" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(116).build()).build()).build())
			//Keyword ::= "None" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(78).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(101).build()).build()).build())
			//Keyword ::= "True" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(84).build(), Character.builder(114).build(), Character.builder(117).build(), Character.builder(101).build()).build()).build())
			//Keyword ::= "try" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(116).build(), Character.builder(114).build(), Character.builder(121).build()).build()).build())
			//Keyword ::= "False" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(70).build(), Character.builder(97).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build()).build())
			//Keyword ::= "except" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(101).build(), Character.builder(120).build(), Character.builder(99).build(), Character.builder(101).build(), Character.builder(112).build(), Character.builder(116).build()).build()).build())
			//Keyword ::= "if" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(102).build()).build()).build())
			//Keyword ::= "return" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(114).build(), Character.builder(101).build(), Character.builder(116).build(), Character.builder(117).build(), Character.builder(114).build(), Character.builder(110).build()).build()).build())
			//Keyword ::= "else" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(101).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build()).build())
			//Keyword ::= "in" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(110).build()).build()).build())
			//Keyword ::= "is" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(115).build()).build()).build())
			//Keyword ::= "nonlocal" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(110).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(108).build(), Character.builder(111).build(), Character.builder(99).build(), Character.builder(97).build(), Character.builder(108).build()).build()).build())
			//Keyword ::= "continue" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(99).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(117).build(), Character.builder(101).build()).build()).build())
			//Keyword ::= "with" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(119).build(), Character.builder(105).build(), Character.builder(116).build(), Character.builder(104).build()).build()).build())
			//Keyword ::= "for" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(102).build(), Character.builder(111).build(), Character.builder(114).build()).build()).build())
			//Keyword ::= "pass" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(112).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build()).build()).build())
			//Keyword ::= "elif" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(101).build(), Character.builder(108).build(), Character.builder(105).build(), Character.builder(102).build()).build()).build())
			//Keyword ::= "def" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(100).build(), Character.builder(101).build(), Character.builder(102).build()).build()).build())
			//Keyword ::= "del" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(100).build(), Character.builder(101).build(), Character.builder(108).build()).build()).build())
			//Stmt ::= CompoundStmt 
			.addRule(Rule.withHead(Nonterminal.builder("Stmt").build()).addSymbol(Nonterminal.builder("CompoundStmt").build()).build())
			//Stmt ::= SimpleStmt 
			.addRule(Rule.withHead(Nonterminal.builder("Stmt").build()).addSymbol(Nonterminal.builder("SimpleStmt").build()).build())
			//Classdef ::= "class" Name ("(" Arglist? ")")? ":" Suite 
			.addRule(Rule.withHead(Nonterminal.builder("Classdef").build()).addSymbol(Sequence.builder(Character.builder(99).build(), Character.builder(108).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build()).build()).addSymbol(Nonterminal.builder("Name").build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(40).build()).build(), org.iguana.regex.Opt.builder(Nonterminal.builder("Arglist").build()).build(), Sequence.builder(Character.builder(41).build()).build()).build()).build()).addSymbol(Sequence.builder(Character.builder(58).build()).build()).addSymbol(Nonterminal.builder("Suite").build()).build())
			//StringPrefix ::= "u" 
			.addRule(Rule.withHead(Nonterminal.builder("StringPrefix").build()).addSymbol(Sequence.builder(Character.builder(117).build()).build()).build())
			//StringPrefix ::= "U" 
			.addRule(Rule.withHead(Nonterminal.builder("StringPrefix").build()).addSymbol(Sequence.builder(Character.builder(85).build()).build()).build())
			//StringPrefix ::= "R" 
			.addRule(Rule.withHead(Nonterminal.builder("StringPrefix").build()).addSymbol(Sequence.builder(Character.builder(82).build()).build()).build())
			//ShortBytesitem ::= ShortBytesChar 
			.addRule(Rule.withHead(Nonterminal.builder("ShortBytesitem").build()).addSymbol(Nonterminal.builder("ShortBytesChar").build()).build())
			//ShortBytesitem ::= BytesEscapeSeq 
			.addRule(Rule.withHead(Nonterminal.builder("ShortBytesitem").build()).addSymbol(Nonterminal.builder("BytesEscapeSeq").build()).build())
			//CompIter ::= CompFor 
			.addRule(Rule.withHead(Nonterminal.builder("CompIter").build()).addSymbol(Nonterminal.builder("CompFor").build()).build())
			//CompIter ::= CompIf 
			.addRule(Rule.withHead(Nonterminal.builder("CompIter").build()).addSymbol(Nonterminal.builder("CompIf").build()).build())
			//StarExpr ::= "*" Expr 
			.addRule(Rule.withHead(Nonterminal.builder("StarExpr").build()).addSymbol(Sequence.builder(Character.builder(42).build()).build()).addSymbol(Nonterminal.builder("Expr").build()).build())
			//NonzeroDigit ::= (1-9) 
			.addRule(Rule.withHead(Nonterminal.builder("NonzeroDigit").build()).addSymbol(Alt.builder(CharacterRange.builder(49, 57).build()).build()).build())
			//YieldExpr ::= "yield" YieldArg? 
			.addRule(Rule.withHead(Nonterminal.builder("YieldExpr").build()).addSymbol(Sequence.builder(Character.builder(121).build(), Character.builder(105).build(), Character.builder(101).build(), Character.builder(108).build(), Character.builder(100).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Nonterminal.builder("YieldArg").build()).build()).build())
			//ImportFrom ::= "from" (("..." | ".")+ | (("..." | ".")* DottedName)) "import" ("*" | ImportAsNames | ("(" ImportAsNames ")")) 
			.addRule(Rule.withHead(Nonterminal.builder("ImportFrom").build()).addSymbol(Sequence.builder(Character.builder(102).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(109).build()).build()).addSymbol(Alt.builder(Plus.builder(Alt.builder(Sequence.builder(Character.builder(46).build(), Character.builder(46).build(), Character.builder(46).build()).build(), Sequence.builder(Character.builder(46).build()).build()).build()).build(), Sequence.builder(Star.builder(Alt.builder(Sequence.builder(Character.builder(46).build(), Character.builder(46).build(), Character.builder(46).build()).build(), Sequence.builder(Character.builder(46).build()).build()).build()).build(), Nonterminal.builder("DottedName").build()).build()).build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build()).addSymbol(Alt.builder(Sequence.builder(Character.builder(42).build()).build(), Nonterminal.builder("ImportAsNames").build(), Sequence.builder(Sequence.builder(Character.builder(40).build()).build(), Nonterminal.builder("ImportAsNames").build(), Sequence.builder(Character.builder(41).build()).build()).build()).build()).build())
			//DottedAsName ::= DottedName ("as" Name)? 
			.addRule(Rule.withHead(Nonterminal.builder("DottedAsName").build()).addSymbol(Nonterminal.builder("DottedName").build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(97).build(), Character.builder(115).build()).build(), Nonterminal.builder("Name").build()).build()).build()).build())
			//BytesPrefix ::= "bR" 
			.addRule(Rule.withHead(Nonterminal.builder("BytesPrefix").build()).addSymbol(Sequence.builder(Character.builder(98).build(), Character.builder(82).build()).build()).build())
			//BytesPrefix ::= "rB" 
			.addRule(Rule.withHead(Nonterminal.builder("BytesPrefix").build()).addSymbol(Sequence.builder(Character.builder(114).build(), Character.builder(66).build()).build()).build())
			//BytesPrefix ::= "Rb" 
			.addRule(Rule.withHead(Nonterminal.builder("BytesPrefix").build()).addSymbol(Sequence.builder(Character.builder(82).build(), Character.builder(98).build()).build()).build())
			//BytesPrefix ::= "B" 
			.addRule(Rule.withHead(Nonterminal.builder("BytesPrefix").build()).addSymbol(Sequence.builder(Character.builder(66).build()).build()).build())
			//BytesPrefix ::= "BR" 
			.addRule(Rule.withHead(Nonterminal.builder("BytesPrefix").build()).addSymbol(Sequence.builder(Character.builder(66).build(), Character.builder(82).build()).build()).build())
			//BytesPrefix ::= "br" 
			.addRule(Rule.withHead(Nonterminal.builder("BytesPrefix").build()).addSymbol(Sequence.builder(Character.builder(98).build(), Character.builder(114).build()).build()).build())
			//BytesPrefix ::= "b" 
			.addRule(Rule.withHead(Nonterminal.builder("BytesPrefix").build()).addSymbol(Sequence.builder(Character.builder(98).build()).build()).build())
			//BytesPrefix ::= "Br" 
			.addRule(Rule.withHead(Nonterminal.builder("BytesPrefix").build()).addSymbol(Sequence.builder(Character.builder(66).build(), Character.builder(114).build()).build()).build())
			//BytesPrefix ::= "RB" 
			.addRule(Rule.withHead(Nonterminal.builder("BytesPrefix").build()).addSymbol(Sequence.builder(Character.builder(82).build(), Character.builder(66).build()).build()).build())
			//BytesPrefix ::= "rb" 
			.addRule(Rule.withHead(Nonterminal.builder("BytesPrefix").build()).addSymbol(Sequence.builder(Character.builder(114).build(), Character.builder(98).build()).build()).build())
			//FlowStmt ::= YieldStmt 
			.addRule(Rule.withHead(Nonterminal.builder("FlowStmt").build()).addSymbol(Nonterminal.builder("YieldStmt").build()).build())
			//FlowStmt ::= RaiseStmt 
			.addRule(Rule.withHead(Nonterminal.builder("FlowStmt").build()).addSymbol(Nonterminal.builder("RaiseStmt").build()).build())
			//FlowStmt ::= BreakStmt 
			.addRule(Rule.withHead(Nonterminal.builder("FlowStmt").build()).addSymbol(Nonterminal.builder("BreakStmt").build()).build())
			//FlowStmt ::= ContinueStmt 
			.addRule(Rule.withHead(Nonterminal.builder("FlowStmt").build()).addSymbol(Nonterminal.builder("ContinueStmt").build()).build())
			//FlowStmt ::= ReturnStmt 
			.addRule(Rule.withHead(Nonterminal.builder("FlowStmt").build()).addSymbol(Nonterminal.builder("ReturnStmt").build()).build())
			//CompoundStmt ::= WithStmt 
			.addRule(Rule.withHead(Nonterminal.builder("CompoundStmt").build()).addSymbol(Nonterminal.builder("WithStmt").build()).build())
			//CompoundStmt ::= WhileStmt 
			.addRule(Rule.withHead(Nonterminal.builder("CompoundStmt").build()).addSymbol(Nonterminal.builder("WhileStmt").build()).build())
			//CompoundStmt ::= Decorated 
			.addRule(Rule.withHead(Nonterminal.builder("CompoundStmt").build()).addSymbol(Nonterminal.builder("Decorated").build()).build())
			//CompoundStmt ::= Classdef 
			.addRule(Rule.withHead(Nonterminal.builder("CompoundStmt").build()).addSymbol(Nonterminal.builder("Classdef").build()).build())
			//CompoundStmt ::= ForStmt 
			.addRule(Rule.withHead(Nonterminal.builder("CompoundStmt").build()).addSymbol(Nonterminal.builder("ForStmt").build()).build())
			//CompoundStmt ::= TryStmt 
			.addRule(Rule.withHead(Nonterminal.builder("CompoundStmt").build()).addSymbol(Nonterminal.builder("TryStmt").build()).build())
			//CompoundStmt ::= IfStmt 
			.addRule(Rule.withHead(Nonterminal.builder("CompoundStmt").build()).addSymbol(Nonterminal.builder("IfStmt").build()).build())
			//CompoundStmt ::= Funcdef 
			.addRule(Rule.withHead(Nonterminal.builder("CompoundStmt").build()).addSymbol(Nonterminal.builder("Funcdef").build()).build())
			//EscapeSeq ::= \ ' 
			.addRule(Rule.withHead(Nonterminal.builder("EscapeSeq").build()).addSymbol(Character.builder(92).build()).addSymbol(Character.builder(39).build()).build())
			//EscapeSeq ::= \ f 
			.addRule(Rule.withHead(Nonterminal.builder("EscapeSeq").build()).addSymbol(Character.builder(92).build()).addSymbol(Character.builder(102).build()).build())
			//EscapeSeq ::= \ \ 
			.addRule(Rule.withHead(Nonterminal.builder("EscapeSeq").build()).addSymbol(Character.builder(92).build()).addSymbol(Character.builder(92).build()).build())
			//EscapeSeq ::= \ n 
			.addRule(Rule.withHead(Nonterminal.builder("EscapeSeq").build()).addSymbol(Character.builder(92).build()).addSymbol(Character.builder(110).build()).build())
			//EscapeSeq ::= \ r 
			.addRule(Rule.withHead(Nonterminal.builder("EscapeSeq").build()).addSymbol(Character.builder(92).build()).addSymbol(Character.builder(114).build()).build())
			//EscapeSeq ::= \ " 
			.addRule(Rule.withHead(Nonterminal.builder("EscapeSeq").build()).addSymbol(Character.builder(92).build()).addSymbol(Character.builder(34).build()).build())
			//EscapeSeq ::= \ b 
			.addRule(Rule.withHead(Nonterminal.builder("EscapeSeq").build()).addSymbol(Character.builder(92).build()).addSymbol(Character.builder(98).build()).build())
			//EscapeSeq ::= \ OctInteger OctInteger OctInteger 
			.addRule(Rule.withHead(Nonterminal.builder("EscapeSeq").build()).addSymbol(Character.builder(92).build()).addSymbol(Nonterminal.builder("OctInteger").build()).addSymbol(Nonterminal.builder("OctInteger").build()).addSymbol(Nonterminal.builder("OctInteger").build()).build())
			//EscapeSeq ::= \ x HexInteger HexInteger 
			.addRule(Rule.withHead(Nonterminal.builder("EscapeSeq").build()).addSymbol(Character.builder(92).build()).addSymbol(Character.builder(120).build()).addSymbol(Nonterminal.builder("HexInteger").build()).addSymbol(Nonterminal.builder("HexInteger").build()).build())
			//EscapeSeq ::= \ a 
			.addRule(Rule.withHead(Nonterminal.builder("EscapeSeq").build()).addSymbol(Character.builder(92).build()).addSymbol(Character.builder(97).build()).build())
			//EscapeSeq ::= \ v 
			.addRule(Rule.withHead(Nonterminal.builder("EscapeSeq").build()).addSymbol(Character.builder(92).build()).addSymbol(Character.builder(118).build()).build())
			//EscapeSeq ::= \ t 
			.addRule(Rule.withHead(Nonterminal.builder("EscapeSeq").build()).addSymbol(Character.builder(92).build()).addSymbol(Character.builder(116).build()).build())
			//LambdefNocond ::= "lambda" Varargslist? ":" TestNocond 
			.addRule(Rule.withHead(Nonterminal.builder("LambdefNocond").build()).addSymbol(Sequence.builder(Character.builder(108).build(), Character.builder(97).build(), Character.builder(109).build(), Character.builder(98).build(), Character.builder(100).build(), Character.builder(97).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Nonterminal.builder("Varargslist").build()).build()).addSymbol(Sequence.builder(Character.builder(58).build()).build()).addSymbol(Nonterminal.builder("TestNocond").build()).build())
			//Atom ::= Name 
			.addRule(Rule.withHead(Nonterminal.builder("Atom").build()).addSymbol(Nonterminal.builder("Name").build()).build())
			//Atom ::= String+ 
			.addRule(Rule.withHead(Nonterminal.builder("Atom").build()).addSymbol(Plus.builder(Nonterminal.builder("String").build()).build()).build())
			//Atom ::= "True" 
			.addRule(Rule.withHead(Nonterminal.builder("Atom").build()).addSymbol(Sequence.builder(Character.builder(84).build(), Character.builder(114).build(), Character.builder(117).build(), Character.builder(101).build()).build()).build())
			//Atom ::= "(" (TestlistComp | YieldExpr)? ")" 
			.addRule(Rule.withHead(Nonterminal.builder("Atom").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Alt.builder(Nonterminal.builder("TestlistComp").build(), Nonterminal.builder("YieldExpr").build()).build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
			//Atom ::= "False" 
			.addRule(Rule.withHead(Nonterminal.builder("Atom").build()).addSymbol(Sequence.builder(Character.builder(70).build(), Character.builder(97).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build()).build())
			//Atom ::= Number 
			.addRule(Rule.withHead(Nonterminal.builder("Atom").build()).addSymbol(Nonterminal.builder("Number").build()).build())
			//Atom ::= "{" Dictorsetmaker? "}" 
			.addRule(Rule.withHead(Nonterminal.builder("Atom").build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Nonterminal.builder("Dictorsetmaker").build()).build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).build())
			//Atom ::= "..." 
			.addRule(Rule.withHead(Nonterminal.builder("Atom").build()).addSymbol(Sequence.builder(Character.builder(46).build(), Character.builder(46).build(), Character.builder(46).build()).build()).build())
			//Atom ::= "[" TestlistComp? "]" 
			.addRule(Rule.withHead(Nonterminal.builder("Atom").build()).addSymbol(Sequence.builder(Character.builder(91).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Nonterminal.builder("TestlistComp").build()).build()).addSymbol(Sequence.builder(Character.builder(93).build()).build()).build())
			//Atom ::= "None" 
			.addRule(Rule.withHead(Nonterminal.builder("Atom").build()).addSymbol(Sequence.builder(Character.builder(78).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(101).build()).build()).build())
			//Augassign ::= "**=" 
			.addRule(Rule.withHead(Nonterminal.builder("Augassign").build()).addSymbol(Sequence.builder(Character.builder(42).build(), Character.builder(42).build(), Character.builder(61).build()).build()).build())
			//Augassign ::= "//=" 
			.addRule(Rule.withHead(Nonterminal.builder("Augassign").build()).addSymbol(Sequence.builder(Character.builder(47).build(), Character.builder(47).build(), Character.builder(61).build()).build()).build())
			//Augassign ::= "^=" 
			.addRule(Rule.withHead(Nonterminal.builder("Augassign").build()).addSymbol(Sequence.builder(Character.builder(94).build(), Character.builder(61).build()).build()).build())
			//Augassign ::= "|=" 
			.addRule(Rule.withHead(Nonterminal.builder("Augassign").build()).addSymbol(Sequence.builder(Character.builder(124).build(), Character.builder(61).build()).build()).build())
			//Augassign ::= "&=" 
			.addRule(Rule.withHead(Nonterminal.builder("Augassign").build()).addSymbol(Sequence.builder(Character.builder(38).build(), Character.builder(61).build()).build()).build())
			//Augassign ::= ">>=" 
			.addRule(Rule.withHead(Nonterminal.builder("Augassign").build()).addSymbol(Sequence.builder(Character.builder(62).build(), Character.builder(62).build(), Character.builder(61).build()).build()).build())
			//Augassign ::= "/=" 
			.addRule(Rule.withHead(Nonterminal.builder("Augassign").build()).addSymbol(Sequence.builder(Character.builder(47).build(), Character.builder(61).build()).build()).build())
			//Augassign ::= "-=" 
			.addRule(Rule.withHead(Nonterminal.builder("Augassign").build()).addSymbol(Sequence.builder(Character.builder(45).build(), Character.builder(61).build()).build()).build())
			//Augassign ::= "+=" 
			.addRule(Rule.withHead(Nonterminal.builder("Augassign").build()).addSymbol(Sequence.builder(Character.builder(43).build(), Character.builder(61).build()).build()).build())
			//Augassign ::= "*=" 
			.addRule(Rule.withHead(Nonterminal.builder("Augassign").build()).addSymbol(Sequence.builder(Character.builder(42).build(), Character.builder(61).build()).build()).build())
			//Augassign ::= "<<=" 
			.addRule(Rule.withHead(Nonterminal.builder("Augassign").build()).addSymbol(Sequence.builder(Character.builder(60).build(), Character.builder(60).build(), Character.builder(61).build()).build()).build())
			//Augassign ::= "%=" 
			.addRule(Rule.withHead(Nonterminal.builder("Augassign").build()).addSymbol(Sequence.builder(Character.builder(37).build(), Character.builder(61).build()).build()).build())
			//Varargslist ::= "**" Vfpdef 
			.addRule(Rule.withHead(Nonterminal.builder("Varargslist").build()).addSymbol(Sequence.builder(Character.builder(42).build(), Character.builder(42).build()).build()).addSymbol(Nonterminal.builder("Vfpdef").build()).build())
			//Varargslist ::= Vfpdef ("=" Test)? ("," Vfpdef ("=" Test)?)* ("," (("**" Vfpdef) | ("*" Vfpdef? ("," Vfpdef ("=" Test)?)* ("," "**" Vfpdef)?))?)? 
			.addRule(Rule.withHead(Nonterminal.builder("Varargslist").build()).addSymbol(Nonterminal.builder("Vfpdef").build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(61).build()).build(), Nonterminal.builder("Test").build()).build()).build()).addSymbol(Star.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Nonterminal.builder("Vfpdef").build(), org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(61).build()).build(), Nonterminal.builder("Test").build()).build()).build()).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), org.iguana.regex.Opt.builder(Alt.builder(Sequence.builder(Sequence.builder(Character.builder(42).build(), Character.builder(42).build()).build(), Nonterminal.builder("Vfpdef").build()).build(), Sequence.builder(Sequence.builder(Character.builder(42).build()).build(), org.iguana.regex.Opt.builder(Nonterminal.builder("Vfpdef").build()).build(), Star.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Nonterminal.builder("Vfpdef").build(), org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(61).build()).build(), Nonterminal.builder("Test").build()).build()).build()).build()).build(), org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Sequence.builder(Character.builder(42).build(), Character.builder(42).build()).build(), Nonterminal.builder("Vfpdef").build()).build()).build()).build()).build()).build()).build()).build()).build())
			//Varargslist ::= "*" Vfpdef? ("," Vfpdef ("=" Test)?)* ("," "**" Vfpdef)? 
			.addRule(Rule.withHead(Nonterminal.builder("Varargslist").build()).addSymbol(Sequence.builder(Character.builder(42).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Nonterminal.builder("Vfpdef").build()).build()).addSymbol(Star.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Nonterminal.builder("Vfpdef").build(), org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(61).build()).build(), Nonterminal.builder("Test").build()).build()).build()).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Sequence.builder(Character.builder(42).build(), Character.builder(42).build()).build(), Nonterminal.builder("Vfpdef").build()).build()).build()).build())
			//Fraction ::= "." Digit+ 
			.addRule(Rule.withHead(Nonterminal.builder("Fraction").build()).addSymbol(Sequence.builder(Character.builder(46).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("Digit").build()).build()).build())
			//ForStmt ::= "for" Exprlist "in" TestList ":" Suite ("else" ":" Suite)? 
			.addRule(Rule.withHead(Nonterminal.builder("ForStmt").build()).addSymbol(Sequence.builder(Character.builder(102).build(), Character.builder(111).build(), Character.builder(114).build()).build()).addSymbol(Nonterminal.builder("Exprlist").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(110).build()).build()).addSymbol(Nonterminal.builder("TestList").build()).addSymbol(Sequence.builder(Character.builder(58).build()).build()).addSymbol(Nonterminal.builder("Suite").build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(101).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build(), Sequence.builder(Character.builder(58).build()).build(), Nonterminal.builder("Suite").build()).build()).build()).build())
			//BytesEscapeSeq ::= EscapeSeq 
			.addRule(Rule.withHead(Nonterminal.builder("BytesEscapeSeq").build()).addSymbol(Nonterminal.builder("EscapeSeq").build()).build())
			//Expr ::= XorExpr ("|" XorExpr)* 
			.addRule(Rule.withHead(Nonterminal.builder("Expr").build()).addSymbol(Nonterminal.builder("XorExpr").build()).addSymbol(Star.builder(Sequence.builder(Sequence.builder(Character.builder(124).build()).build(), Nonterminal.builder("XorExpr").build()).build()).build()).build())
			//OrTest ::= AndTest ("or" AndTest)* 
			.addRule(Rule.withHead(Nonterminal.builder("OrTest").build()).addSymbol(Nonterminal.builder("AndTest").build()).addSymbol(Star.builder(Sequence.builder(Sequence.builder(Character.builder(111).build(), Character.builder(114).build()).build(), Nonterminal.builder("AndTest").build()).build()).build()).build())
			//TestlistStarExpr ::= (Test | StarExpr) ("," (Test | StarExpr))* (,)? 
			.addRule(Rule.withHead(Nonterminal.builder("TestlistStarExpr").build()).addSymbol(Alt.builder(Nonterminal.builder("Test").build(), Nonterminal.builder("StarExpr").build()).build()).addSymbol(Star.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Alt.builder(Nonterminal.builder("Test").build(), Nonterminal.builder("StarExpr").build()).build()).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Character.builder(44).build()).build()).build()).build())
			//Subscript ::= Test? ":" Test? Sliceop? 
			.addRule(Rule.withHead(Nonterminal.builder("Subscript").build()).addSymbol(org.iguana.regex.Opt.builder(Nonterminal.builder("Test").build()).build()).addSymbol(Sequence.builder(Character.builder(58).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Nonterminal.builder("Test").build()).build()).addSymbol(org.iguana.regex.Opt.builder(Nonterminal.builder("Sliceop").build()).build()).build())
			//Subscript ::= Test 
			.addRule(Rule.withHead(Nonterminal.builder("Subscript").build()).addSymbol(Nonterminal.builder("Test").build()).build())
			//HexInteger ::= "0" ("x" | "X") HexDigit+ 
			.addRule(Rule.withHead(Nonterminal.builder("HexInteger").build()).addSymbol(Sequence.builder(Character.builder(48).build()).build()).addSymbol(Alt.builder(Sequence.builder(Character.builder(120).build()).build(), Sequence.builder(Character.builder(88).build()).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("HexDigit").build()).build()).build())
			//Power ::= Atom Trailer* ("**" Factor)? 
			.addRule(Rule.withHead(Nonterminal.builder("Power").build()).addSymbol(Nonterminal.builder("Atom").build()).addSymbol(Star.builder(Nonterminal.builder("Trailer").build()).build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(42).build(), Character.builder(42).build()).build(), Nonterminal.builder("Factor").build()).build()).build()).build())
			//NewLine ::= \u000A 
			.addRule(Rule.withHead(Nonterminal.builder("NewLine").build()).addSymbol(Character.builder(10).build()).build())
			//YieldStmt ::= YieldExpr 
			.addRule(Rule.withHead(Nonterminal.builder("YieldStmt").build()).addSymbol(Nonterminal.builder("YieldExpr").build()).build())
			//Digit ::= (0-9) 
			.addRule(Rule.withHead(Nonterminal.builder("Digit").build()).addSymbol(Alt.builder(CharacterRange.builder(48, 57).build()).build()).build())
			//DelStmt ::= "del" Exprlist 
			.addRule(Rule.withHead(Nonterminal.builder("DelStmt").build()).addSymbol(Sequence.builder(Character.builder(100).build(), Character.builder(101).build(), Character.builder(108).build()).build()).addSymbol(Nonterminal.builder("Exprlist").build()).build())
			//Factor ::= Power 
			.addRule(Rule.withHead(Nonterminal.builder("Factor").build()).addSymbol(Nonterminal.builder("Power").build()).build())
			//Factor ::= ("-" | "+" | "~") Factor 
			.addRule(Rule.withHead(Nonterminal.builder("Factor").build()).addSymbol(Alt.builder(Sequence.builder(Character.builder(45).build()).build(), Sequence.builder(Character.builder(43).build()).build(), Sequence.builder(Character.builder(126).build()).build()).build()).addSymbol(Nonterminal.builder("Factor").build()).build())
			//Decorator ::= "@" DottedName ("(" Arglist? ")")? NewLine 
			.addRule(Rule.withHead(Nonterminal.builder("Decorator").build()).addSymbol(Sequence.builder(Character.builder(64).build()).build()).addSymbol(Nonterminal.builder("DottedName").build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(40).build()).build(), org.iguana.regex.Opt.builder(Nonterminal.builder("Arglist").build()).build(), Sequence.builder(Character.builder(41).build()).build()).build()).build()).addSymbol(Nonterminal.builder("NewLine").build()).build())
			//LongString ::= """"" LongStringItem* """"" 
			.addRule(Rule.withHead(Nonterminal.builder("LongString").build()).addSymbol(Sequence.builder(Character.builder(34).build(), Character.builder(34).build(), Character.builder(34).build()).build()).addSymbol(Star.builder(Nonterminal.builder("LongStringItem").build()).build()).addSymbol(Sequence.builder(Character.builder(34).build(), Character.builder(34).build(), Character.builder(34).build()).build()).build())
			//LongString ::= "'''" LongStringItem* "'''" 
			.addRule(Rule.withHead(Nonterminal.builder("LongString").build()).addSymbol(Sequence.builder(Character.builder(39).build(), Character.builder(39).build(), Character.builder(39).build()).build()).addSymbol(Star.builder(Nonterminal.builder("LongStringItem").build()).build()).addSymbol(Sequence.builder(Character.builder(39).build(), Character.builder(39).build(), Character.builder(39).build()).build()).build())
			//TestList ::= Test ("," Test)* (,)? 
			.addRule(Rule.withHead(Nonterminal.builder("TestList").build()).addSymbol(Nonterminal.builder("Test").build()).addSymbol(Star.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Nonterminal.builder("Test").build()).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Character.builder(44).build()).build()).build()).build())
			//Decorators ::= Decorator+ 
			.addRule(Rule.withHead(Nonterminal.builder("Decorators").build()).addSymbol(Plus.builder(Nonterminal.builder("Decorator").build()).build()).build())
			//Dictorsetmaker ::= Test ":" Test (CompFor | (("," Test ":" Test)* (,)?)) 
			.addRule(Rule.withHead(Nonterminal.builder("Dictorsetmaker").build()).addSymbol(Nonterminal.builder("Test").build()).addSymbol(Sequence.builder(Character.builder(58).build()).build()).addSymbol(Nonterminal.builder("Test").build()).addSymbol(Alt.builder(Nonterminal.builder("CompFor").build(), Sequence.builder(Star.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Nonterminal.builder("Test").build(), Sequence.builder(Character.builder(58).build()).build(), Nonterminal.builder("Test").build()).build()).build(), org.iguana.regex.Opt.builder(Sequence.builder(Character.builder(44).build()).build()).build()).build()).build()).build())
			//Dictorsetmaker ::= Test (CompFor | (("," Test)* (,)?)) 
			.addRule(Rule.withHead(Nonterminal.builder("Dictorsetmaker").build()).addSymbol(Nonterminal.builder("Test").build()).addSymbol(Alt.builder(Nonterminal.builder("CompFor").build(), Sequence.builder(Star.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Nonterminal.builder("Test").build()).build()).build(), org.iguana.regex.Opt.builder(Sequence.builder(Character.builder(44).build()).build()).build()).build()).build()).build())
			//Funcdef ::= "def" Name Parameters ("->" Test)? ":" Suite 
			.addRule(Rule.withHead(Nonterminal.builder("Funcdef").build()).addSymbol(Sequence.builder(Character.builder(100).build(), Character.builder(101).build(), Character.builder(102).build()).build()).addSymbol(Nonterminal.builder("Name").build()).addSymbol(Nonterminal.builder("Parameters").build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(45).build(), Character.builder(62).build()).build(), Nonterminal.builder("Test").build()).build()).build()).addSymbol(Sequence.builder(Character.builder(58).build()).build()).addSymbol(Nonterminal.builder("Suite").build()).build())
			//Number ::= FloatNumber 
			.addRule(Rule.withHead(Nonterminal.builder("Number").build()).addSymbol(Nonterminal.builder("FloatNumber").build()).build())
			//Number ::= ImagNumber 
			.addRule(Rule.withHead(Nonterminal.builder("Number").build()).addSymbol(Nonterminal.builder("ImagNumber").build()).build())
			//Number ::= Integer 
			.addRule(Rule.withHead(Nonterminal.builder("Number").build()).addSymbol(Nonterminal.builder("Integer").build()).build())
			//BytesLiteral ::= BytesPrefix (ShortBytes | LongBytes) 
			.addRule(Rule.withHead(Nonterminal.builder("BytesLiteral").build()).addSymbol(Nonterminal.builder("BytesPrefix").build()).addSymbol(Alt.builder(Nonterminal.builder("ShortBytes").build(), Nonterminal.builder("LongBytes").build()).build()).build())
			
			/**
			 * Data-dependent
			 * 
			 * @layout(NoNL)
			 * SimpleStmt ::= SmallStmt (";" SmallStmt)* (;)? NL
			 */
			.addRule(Rule.withHead(Nonterminal.builder("SimpleStmt").build()).addSymbol(Nonterminal.builder("SmallStmt").build()).addSymbol(Star.builder(Sequence.builder(Sequence.builder(Character.builder(59).build()).build(), Nonterminal.builder("SmallStmt").build()).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Character.builder(59).build()).build()).build()).addSymbol(Nonterminal.builder("NL").build())
							.setLayout(Nonterminal.builder("NoNL").build()).build())
			
			//Tfpdef ::= Name (":" Test)? 
			.addRule(Rule.withHead(Nonterminal.builder("Tfpdef").build()).addSymbol(Nonterminal.builder("Name").build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(58).build()).build(), Nonterminal.builder("Test").build()).build()).build()).build())
			//CompIf ::= "if" TestNocond CompIter? 
			.addRule(Rule.withHead(Nonterminal.builder("CompIf").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(102).build()).build()).addSymbol(Nonterminal.builder("TestNocond").build()).addSymbol(org.iguana.regex.Opt.builder(Nonterminal.builder("CompIter").build()).build()).build())
			//ArithExpr ::= Term (("-" | "+") Term)* 
			.addRule(Rule.withHead(Nonterminal.builder("ArithExpr").build()).addSymbol(Nonterminal.builder("Term").build()).addSymbol(Star.builder(Sequence.builder(Alt.builder(Sequence.builder(Character.builder(45).build()).build(), Sequence.builder(Character.builder(43).build()).build()).build(), Nonterminal.builder("Term").build()).build()).build()).build())
			//WithItem ::= Test ("as" Expr)? 
			.addRule(Rule.withHead(Nonterminal.builder("WithItem").build()).addSymbol(Nonterminal.builder("Test").build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(97).build(), Character.builder(115).build()).build(), Nonterminal.builder("Expr").build()).build()).build()).build())
			//Subscriptlist ::= Subscript ("," Subscript)* (,)? 
			.addRule(Rule.withHead(Nonterminal.builder("Subscriptlist").build()).addSymbol(Nonterminal.builder("Subscript").build()).addSymbol(Star.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Nonterminal.builder("Subscript").build()).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Character.builder(44).build()).build()).build()).build())
			//PassStmt ::= "pass" 
			.addRule(Rule.withHead(Nonterminal.builder("PassStmt").build()).addSymbol(Sequence.builder(Character.builder(112).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build()).build()).build())
			//Exponent ::= ("e" | "E") ("-" | "+")? Digit+ 
			.addRule(Rule.withHead(Nonterminal.builder("Exponent").build()).addSymbol(Alt.builder(Sequence.builder(Character.builder(101).build()).build(), Sequence.builder(Character.builder(69).build()).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Alt.builder(Sequence.builder(Character.builder(45).build()).build(), Sequence.builder(Character.builder(43).build()).build()).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("Digit").build()).build()).build())
			//ShortBytes ::= "'" ShortBytesitem* "'" 
			.addRule(Rule.withHead(Nonterminal.builder("ShortBytes").build()).addSymbol(Sequence.builder(Character.builder(39).build()).build()).addSymbol(Star.builder(Nonterminal.builder("ShortBytesitem").build()).build()).addSymbol(Sequence.builder(Character.builder(39).build()).build()).build())
			//ShortBytes ::= """ ShortBytesitem* """ 
			.addRule(Rule.withHead(Nonterminal.builder("ShortBytes").build()).addSymbol(Sequence.builder(Character.builder(34).build()).build()).addSymbol(Star.builder(Nonterminal.builder("ShortBytesitem").build()).build()).addSymbol(Sequence.builder(Character.builder(34).build()).build()).build())
			//TryStmt ::= "try" ":" Suite (((ExceptClause ":" Suite)+ ("else" ":" Suite)? ("finally" ":" Suite)?) | ("finally" ":" Suite)) 
			.addRule(Rule.withHead(Nonterminal.builder("TryStmt").build()).addSymbol(Sequence.builder(Character.builder(116).build(), Character.builder(114).build(), Character.builder(121).build()).build()).addSymbol(Sequence.builder(Character.builder(58).build()).build()).addSymbol(Nonterminal.builder("Suite").build()).addSymbol(Alt.builder(Sequence.builder(Plus.builder(Sequence.builder(Nonterminal.builder("ExceptClause").build(), Sequence.builder(Character.builder(58).build()).build(), Nonterminal.builder("Suite").build()).build()).build(), org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(101).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build(), Sequence.builder(Character.builder(58).build()).build(), Nonterminal.builder("Suite").build()).build()).build(), org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(102).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(97).build(), Character.builder(108).build(), Character.builder(108).build(), Character.builder(121).build()).build(), Sequence.builder(Character.builder(58).build()).build(), Nonterminal.builder("Suite").build()).build()).build()).build(), Sequence.builder(Sequence.builder(Character.builder(102).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(97).build(), Character.builder(108).build(), Character.builder(108).build(), Character.builder(121).build()).build(), Sequence.builder(Character.builder(58).build()).build(), Nonterminal.builder("Suite").build()).build()).build()).build())
			//AndTest ::= NotTest ("and" NotTest)* 
			.addRule(Rule.withHead(Nonterminal.builder("AndTest").build()).addSymbol(Nonterminal.builder("NotTest").build()).addSymbol(Star.builder(Sequence.builder(Sequence.builder(Character.builder(97).build(), Character.builder(110).build(), Character.builder(100).build()).build(), Nonterminal.builder("NotTest").build()).build()).build()).build())
			//LongStringChar ::= (\u0001-\u10FFFF) 
			.addRule(Rule.withHead(Nonterminal.builder("LongStringChar").build()).addSymbol(Alt.builder(CharacterRange.builder(1, 1114111).build()).addPostConditions(set(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Character.builder(92).build()).build()))).build()).build())
			//NonlocalStmt ::= "nonlocal" Name ("," Name)* 
			.addRule(Rule.withHead(Nonterminal.builder("NonlocalStmt").build()).addSymbol(Sequence.builder(Character.builder(110).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(108).build(), Character.builder(111).build(), Character.builder(99).build(), Character.builder(97).build(), Character.builder(108).build()).build()).addSymbol(Nonterminal.builder("Name").build()).addSymbol(Star.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Nonterminal.builder("Name").build()).build()).build()).build())
			//ContinueStmt ::= "continue" 
			.addRule(Rule.withHead(Nonterminal.builder("ContinueStmt").build()).addSymbol(Sequence.builder(Character.builder(99).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(117).build(), Character.builder(101).build()).build()).build())
			//Argument ::= Test "=" Test 
			.addRule(Rule.withHead(Nonterminal.builder("Argument").build()).addSymbol(Nonterminal.builder("Test").build()).addSymbol(Sequence.builder(Character.builder(61).build()).build()).addSymbol(Nonterminal.builder("Test").build()).build())
			//Argument ::= Test CompFor? 
			.addRule(Rule.withHead(Nonterminal.builder("Argument").build()).addSymbol(Nonterminal.builder("Test").build()).addSymbol(org.iguana.regex.Opt.builder(Nonterminal.builder("CompFor").build()).build()).build())
			//PointFloat ::= IntPart "." 
			.addRule(Rule.withHead(Nonterminal.builder("PointFloat").build()).addSymbol(Nonterminal.builder("IntPart").build()).addSymbol(Sequence.builder(Character.builder(46).build()).build()).build())
			//PointFloat ::= IntPart? Fraction 
			.addRule(Rule.withHead(Nonterminal.builder("PointFloat").build()).addSymbol(org.iguana.regex.Opt.builder(Nonterminal.builder("IntPart").build()).build()).addSymbol(Nonterminal.builder("Fraction").build()).build())
			//Test ::= OrTest ("if" OrTest "else" Test)? 
			.addRule(Rule.withHead(Nonterminal.builder("Test").build()).addSymbol(Nonterminal.builder("OrTest").build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(105).build(), Character.builder(102).build()).build(), Nonterminal.builder("OrTest").build(), Sequence.builder(Character.builder(101).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build(), Nonterminal.builder("Test").build()).build()).build()).build())
			//Test ::= Lambdef 
			.addRule(Rule.withHead(Nonterminal.builder("Test").build()).addSymbol(Nonterminal.builder("Lambdef").build()).build())
			//ShiftExpr ::= ArithExpr (("<<" | ">>") ArithExpr)* 
			.addRule(Rule.withHead(Nonterminal.builder("ShiftExpr").build()).addSymbol(Nonterminal.builder("ArithExpr").build()).addSymbol(Star.builder(Sequence.builder(Alt.builder(Sequence.builder(Character.builder(60).build(), Character.builder(60).build()).build(), Sequence.builder(Character.builder(62).build(), Character.builder(62).build()).build()).build(), Nonterminal.builder("ArithExpr").build()).build()).build()).build())
			//Exprlist ::= (Expr | StarExpr) ("," (Expr | StarExpr))* (,)? 
			.addRule(Rule.withHead(Nonterminal.builder("Exprlist").build()).addSymbol(Alt.builder(Nonterminal.builder("Expr").build(), Nonterminal.builder("StarExpr").build()).build()).addSymbol(Star.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Alt.builder(Nonterminal.builder("Expr").build(), Nonterminal.builder("StarExpr").build()).build()).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Character.builder(44).build()).build()).build()).build())
			//ImportAsName ::= Name ("as" Name)? 
			.addRule(Rule.withHead(Nonterminal.builder("ImportAsName").build()).addSymbol(Nonterminal.builder("Name").build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(97).build(), Character.builder(115).build()).build(), Nonterminal.builder("Name").build()).build()).build()).build())
			//DecimalInteger ::= (0)+ 
			.addRule(Rule.withHead(Nonterminal.builder("DecimalInteger").build()).addSymbol(Plus.builder(Sequence.builder(Character.builder(48).build()).build()).build()).build())
			//DecimalInteger ::= NonzeroDigit Digit* 
			.addRule(Rule.withHead(Nonterminal.builder("DecimalInteger").build()).addSymbol(Nonterminal.builder("NonzeroDigit").build()).addSymbol(Star.builder(Nonterminal.builder("Digit").build()).build()).build())
			//ImportAsNames ::= ImportAsName ("," ImportAsName)* (,)? 
			.addRule(Rule.withHead(Nonterminal.builder("ImportAsNames").build()).addSymbol(Nonterminal.builder("ImportAsName").build()).addSymbol(Star.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Nonterminal.builder("ImportAsName").build()).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Character.builder(44).build()).build()).build()).build())
			//GlobalStmt ::= "global" Name ("," Name)* 
			.addRule(Rule.withHead(Nonterminal.builder("GlobalStmt").build()).addSymbol(Sequence.builder(Character.builder(103).build(), Character.builder(108).build(), Character.builder(111).build(), Character.builder(98).build(), Character.builder(97).build(), Character.builder(108).build()).build()).addSymbol(Nonterminal.builder("Name").build()).addSymbol(Star.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Nonterminal.builder("Name").build()).build()).build()).build())
			//OctInteger ::= "0" ("o" | "O") OctDigit+ 
			.addRule(Rule.withHead(Nonterminal.builder("OctInteger").build()).addSymbol(Sequence.builder(Character.builder(48).build()).build()).addSymbol(Alt.builder(Sequence.builder(Character.builder(111).build()).build(), Sequence.builder(Character.builder(79).build()).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("OctDigit").build()).build()).build())
			//Decorated ::= Decorators (Classdef | Funcdef) 
			.addRule(Rule.withHead(Nonterminal.builder("Decorated").build()).addSymbol(Nonterminal.builder("Decorators").build()).addSymbol(Alt.builder(Nonterminal.builder("Classdef").build(), Nonterminal.builder("Funcdef").build()).build()).build())
			//Integer ::= BinInteger 
			.addRule(Rule.withHead(Nonterminal.builder("Integer").build()).addSymbol(Nonterminal.builder("BinInteger").build()).build())
			//Integer ::= OctInteger 
			.addRule(Rule.withHead(Nonterminal.builder("Integer").build()).addSymbol(Nonterminal.builder("OctInteger").build()).build())
			//Integer ::= DecimalInteger 
			.addRule(Rule.withHead(Nonterminal.builder("Integer").build()).addSymbol(Nonterminal.builder("DecimalInteger").build()).build())
			//Integer ::= HexInteger 
			.addRule(Rule.withHead(Nonterminal.builder("Integer").build()).addSymbol(Nonterminal.builder("HexInteger").build()).build())
			//TestlistComp ::= (Test | StarExpr) (CompFor | (("," (Test | StarExpr))* (,)?)) 
			.addRule(Rule.withHead(Nonterminal.builder("TestlistComp").build()).addSymbol(Alt.builder(Nonterminal.builder("Test").build(), Nonterminal.builder("StarExpr").build()).build()).addSymbol(Alt.builder(Nonterminal.builder("CompFor").build(), Sequence.builder(Star.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Alt.builder(Nonterminal.builder("Test").build(), Nonterminal.builder("StarExpr").build()).build()).build()).build(), org.iguana.regex.Opt.builder(Sequence.builder(Character.builder(44).build()).build()).build()).build()).build()).build())
			//AssertStmt ::= "assert" Test ("," Test)? 
			.addRule(Rule.withHead(Nonterminal.builder("AssertStmt").build()).addSymbol(Sequence.builder(Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(116).build()).build()).addSymbol(Nonterminal.builder("Test").build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Nonterminal.builder("Test").build()).build()).build()).build())
			//Sliceop ::= ":" Test? 
			.addRule(Rule.withHead(Nonterminal.builder("Sliceop").build()).addSymbol(Sequence.builder(Character.builder(58).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Nonterminal.builder("Test").build()).build()).build())
			//YieldArg ::= "from" Test 
			.addRule(Rule.withHead(Nonterminal.builder("YieldArg").build()).addSymbol(Sequence.builder(Character.builder(102).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(109).build()).build()).addSymbol(Nonterminal.builder("Test").build()).build())
			//YieldArg ::= TestList 
			.addRule(Rule.withHead(Nonterminal.builder("YieldArg").build()).addSymbol(Nonterminal.builder("TestList").build()).build())
			//BreakStmt ::= "break" 
			.addRule(Rule.withHead(Nonterminal.builder("BreakStmt").build()).addSymbol(Sequence.builder(Character.builder(98).build(), Character.builder(114).build(), Character.builder(101).build(), Character.builder(97).build(), Character.builder(107).build()).build()).build())
			//LongBytesItem ::= BytesEscapeSeq 
			.addRule(Rule.withHead(Nonterminal.builder("LongBytesItem").build()).addSymbol(Nonterminal.builder("BytesEscapeSeq").build()).build())
			//LongBytesItem ::= LongBytesChar 
			.addRule(Rule.withHead(Nonterminal.builder("LongBytesItem").build()).addSymbol(Nonterminal.builder("LongBytesChar").build()).build())
			
			/**
			 * Data-dependent
			 * 
			 * @layout(NoNL)
			 * IfStmt ::= a:"if" Test ":" Suite(a.lExt) (b:"elif" Test ":" Suite(b.lExt))* (c:"else" ":" Suite(c.lExt))?
			 */ 
			.addRule(Rule.withHead(Nonterminal.builder("IfStmt").build())
									.addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(102).build()).setLabel("a").build())
									.addSymbol(Nonterminal.builder("Test").build())
									.addSymbol(Sequence.builder(Character.builder(58).build()).build())
									.addSymbol(Nonterminal.builder("Suite").apply(lExt("a")).build())
									.addSymbol(Star.builder(Sequence.builder(Sequence.builder(Character.builder(101).build(), Character.builder(108).build(), Character.builder(105).build(), Character.builder(102).build()).setLabel("b").build(), 
																							  Nonterminal.builder("Test").build(), 
																							  Sequence.builder(Character.builder(58).build()).build(), 
																							  Nonterminal.builder("Suite").apply(lExt("b")).build()).build()).build())
									.addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(101).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).setLabel("c").build(), 
																						   Sequence.builder(Character.builder(58).build()).build(), 
																						   Nonterminal.builder("Suite").apply(lExt("c")).build()).build()).build())
									
							.setLayout(Nonterminal.builder("NoNL").build()).build())
			
			//ReturnStmt ::= "return" TestList? 
			.addRule(Rule.withHead(Nonterminal.builder("ReturnStmt").build()).addSymbol(Sequence.builder(Character.builder(114).build(), Character.builder(101).build(), Character.builder(116).build(), Character.builder(117).build(), Character.builder(114).build(), Character.builder(110).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Nonterminal.builder("TestList").build()).build()).build())
			//Vfpdef ::= Name 
			.addRule(Rule.withHead(Nonterminal.builder("Vfpdef").build()).addSymbol(Nonterminal.builder("Name").build()).build())
			
			/**
			 * Data-dependent
			 * 
			 * @layout(NoNL)
			 * Suite(lExt) ::= SimpleStmt
			 *               | NL a:(b:Stmt [indent(a.lExt) == indent(b.lExt)])+ [indent(lExt) < indent(a.lExt)]
			 */
			.addRule(Rule.withHead(Nonterminal.builder("Suite").addParameters("lExt").build()).addSymbol(Nonterminal.builder("SimpleStmt").build()).build())
			
			.addRule(Rule.withHead(Nonterminal.builder("Suite").addParameters("lExt").build())
									.addSymbol(Nonterminal.builder("NL").build())
									.addSymbol(Plus.builder(Nonterminal.builder("Stmt").setLabel("b")
																.addPreCondition(predicate(equal(indent(lExt("a")), indent(lExt("b"))))).build()).setLabel("a")
													.addPreCondition(predicate(less(indent(var("lExt")), indent(lExt("a"))))).build())
						.setLayout(Nonterminal.builder("NoNL").build()).build())
			
			
			//TestNocond ::= OrTest 
			.addRule(Rule.withHead(Nonterminal.builder("TestNocond").build()).addSymbol(Nonterminal.builder("OrTest").build()).build())
			//TestNocond ::= LambdefNocond 
			.addRule(Rule.withHead(Nonterminal.builder("TestNocond").build()).addSymbol(Nonterminal.builder("LambdefNocond").build()).build())
			//EvalInput ::= TestList NewLine* 
			.addRule(Rule.withHead(Nonterminal.builder("EvalInput").build()).addSymbol(Nonterminal.builder("TestList").build()).addSymbol(Star.builder(Nonterminal.builder("NewLine").build()).build()).build())
			//NotTest ::= Comparison 
			.addRule(Rule.withHead(Nonterminal.builder("NotTest").build()).addSymbol(Nonterminal.builder("Comparison").build()).build())
			//NotTest ::= "not" NotTest 
			.addRule(Rule.withHead(Nonterminal.builder("NotTest").build()).addSymbol(Sequence.builder(Character.builder(110).build(), Character.builder(111).build(), Character.builder(116).build()).build()).addSymbol(Nonterminal.builder("NotTest").build()).build())
			//CompOp ::= "!=" 
			.addRule(Rule.withHead(Nonterminal.builder("CompOp").build()).addSymbol(Sequence.builder(Character.builder(33).build(), Character.builder(61).build()).build()).build())
			//CompOp ::= "<" 
			.addRule(Rule.withHead(Nonterminal.builder("CompOp").build()).addSymbol(Sequence.builder(Character.builder(60).build()).build()).build())
			//CompOp ::= ">" 
			.addRule(Rule.withHead(Nonterminal.builder("CompOp").build()).addSymbol(Sequence.builder(Character.builder(62).build()).build()).build())
			//CompOp ::= "not" "in" 
			.addRule(Rule.withHead(Nonterminal.builder("CompOp").build()).addSymbol(Sequence.builder(Character.builder(110).build(), Character.builder(111).build(), Character.builder(116).build()).build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(110).build()).build()).build())
			//CompOp ::= "is" "not" 
			.addRule(Rule.withHead(Nonterminal.builder("CompOp").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(115).build()).build()).addSymbol(Sequence.builder(Character.builder(110).build(), Character.builder(111).build(), Character.builder(116).build()).build()).build())
			//CompOp ::= "in" 
			.addRule(Rule.withHead(Nonterminal.builder("CompOp").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(110).build()).build()).build())
			//CompOp ::= "is" 
			.addRule(Rule.withHead(Nonterminal.builder("CompOp").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(115).build()).build()).build())
			//CompOp ::= ">=" 
			.addRule(Rule.withHead(Nonterminal.builder("CompOp").build()).addSymbol(Sequence.builder(Character.builder(62).build(), Character.builder(61).build()).build()).build())
			//CompOp ::= "==" 
			.addRule(Rule.withHead(Nonterminal.builder("CompOp").build()).addSymbol(Sequence.builder(Character.builder(61).build(), Character.builder(61).build()).build()).build())
			//CompOp ::= "<=" 
			.addRule(Rule.withHead(Nonterminal.builder("CompOp").build()).addSymbol(Sequence.builder(Character.builder(60).build(), Character.builder(61).build()).build()).build())
			//CompOp ::= "<>" 
			.addRule(Rule.withHead(Nonterminal.builder("CompOp").build()).addSymbol(Sequence.builder(Character.builder(60).build(), Character.builder(62).build()).build()).build())
			//RaiseStmt ::= "raise" (Test ("from" Test)?)? 
			.addRule(Rule.withHead(Nonterminal.builder("RaiseStmt").build()).addSymbol(Sequence.builder(Character.builder(114).build(), Character.builder(97).build(), Character.builder(105).build(), Character.builder(115).build(), Character.builder(101).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Nonterminal.builder("Test").build(), org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(102).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(109).build()).build(), Nonterminal.builder("Test").build()).build()).build()).build()).build()).build())
			//StringEscapeSeq ::= \ u HexInteger HexInteger HexInteger HexInteger HexInteger HexInteger HexInteger HexInteger 
			.addRule(Rule.withHead(Nonterminal.builder("StringEscapeSeq").build()).addSymbol(Character.builder(92).build()).addSymbol(Character.builder(117).build()).addSymbol(Nonterminal.builder("HexInteger").build()).addSymbol(Nonterminal.builder("HexInteger").build()).addSymbol(Nonterminal.builder("HexInteger").build()).addSymbol(Nonterminal.builder("HexInteger").build()).addSymbol(Nonterminal.builder("HexInteger").build()).addSymbol(Nonterminal.builder("HexInteger").build()).addSymbol(Nonterminal.builder("HexInteger").build()).addSymbol(Nonterminal.builder("HexInteger").build()).build())
			//StringEscapeSeq ::= \ u HexInteger HexInteger HexInteger HexInteger 
			.addRule(Rule.withHead(Nonterminal.builder("StringEscapeSeq").build()).addSymbol(Character.builder(92).build()).addSymbol(Character.builder(117).build()).addSymbol(Nonterminal.builder("HexInteger").build()).addSymbol(Nonterminal.builder("HexInteger").build()).addSymbol(Nonterminal.builder("HexInteger").build()).addSymbol(Nonterminal.builder("HexInteger").build()).build())
			//StringEscapeSeq ::= EscapeSeq 
			.addRule(Rule.withHead(Nonterminal.builder("StringEscapeSeq").build()).addSymbol(Nonterminal.builder("EscapeSeq").build()).build())
			//AndExpr ::= ShiftExpr ("&" ShiftExpr)* 
			.addRule(Rule.withHead(Nonterminal.builder("AndExpr").build()).addSymbol(Nonterminal.builder("ShiftExpr").build()).addSymbol(Star.builder(Sequence.builder(Sequence.builder(Character.builder(38).build()).build(), Nonterminal.builder("ShiftExpr").build()).build()).build()).build())
			//XorExpr ::= AndExpr ("^" AndExpr)* 
			.addRule(Rule.withHead(Nonterminal.builder("XorExpr").build()).addSymbol(Nonterminal.builder("AndExpr").build()).addSymbol(Star.builder(Sequence.builder(Sequence.builder(Character.builder(94).build()).build(), Nonterminal.builder("AndExpr").build()).build()).build()).build())
			//FileInput ::= (NewLine | Stmt)* 
			.addRule(Rule.withHead(Nonterminal.builder("FileInput").build()).addSymbol(Star.builder(Alt.builder(Nonterminal.builder("NewLine").build(), Nonterminal.builder("Stmt").build()).build()).build()).build())
			//String ::= StringLiteral 
			.addRule(Rule.withHead(Nonterminal.builder("String").build()).addSymbol(Nonterminal.builder("StringLiteral").build()).build())
			//String ::= BytesLiteral 
			.addRule(Rule.withHead(Nonterminal.builder("String").build()).addSymbol(Nonterminal.builder("BytesLiteral").build()).build())
			// ::= 
			.addRule(Rule.withHead(Nonterminal.builder("").build()).build())
			//TypedArgsList ::= "*" Tfpdef? ("," Tfpdef ("=" Test)?)* ("," "**" Tfpdef)? 
			.addRule(Rule.withHead(Nonterminal.builder("TypedArgsList").build()).addSymbol(Sequence.builder(Character.builder(42).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Nonterminal.builder("Tfpdef").build()).build()).addSymbol(Star.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Nonterminal.builder("Tfpdef").build(), org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(61).build()).build(), Nonterminal.builder("Test").build()).build()).build()).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Sequence.builder(Character.builder(42).build(), Character.builder(42).build()).build(), Nonterminal.builder("Tfpdef").build()).build()).build()).build())
			//TypedArgsList ::= Tfpdef ("=" Test)? ("," Tfpdef ("=" Test)?)* ("," (("**" Tfpdef) | ("*" Tfpdef? ("," Tfpdef ("=" Test)?)* ("," "**" Tfpdef)?))?)? 
			.addRule(Rule.withHead(Nonterminal.builder("TypedArgsList").build()).addSymbol(Nonterminal.builder("Tfpdef").build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(61).build()).build(), Nonterminal.builder("Test").build()).build()).build()).addSymbol(Star.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Nonterminal.builder("Tfpdef").build(), org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(61).build()).build(), Nonterminal.builder("Test").build()).build()).build()).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), org.iguana.regex.Opt.builder(Alt.builder(Sequence.builder(Sequence.builder(Character.builder(42).build(), Character.builder(42).build()).build(), Nonterminal.builder("Tfpdef").build()).build(), Sequence.builder(Sequence.builder(Character.builder(42).build()).build(), org.iguana.regex.Opt.builder(Nonterminal.builder("Tfpdef").build()).build(), Star.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Nonterminal.builder("Tfpdef").build(), org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(61).build()).build(), Nonterminal.builder("Test").build()).build()).build()).build()).build(), org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Sequence.builder(Character.builder(42).build(), Character.builder(42).build()).build(), Nonterminal.builder("Tfpdef").build()).build()).build()).build()).build()).build()).build()).build()).build())
			//TypedArgsList ::= "**" Tfpdef 
			.addRule(Rule.withHead(Nonterminal.builder("TypedArgsList").build()).addSymbol(Sequence.builder(Character.builder(42).build(), Character.builder(42).build()).build()).addSymbol(Nonterminal.builder("Tfpdef").build()).build())
			//Comparison ::= Expr (CompOp Expr)* 
			.addRule(Rule.withHead(Nonterminal.builder("Comparison").build()).addSymbol(Nonterminal.builder("Expr").build()).addSymbol(Star.builder(Sequence.builder(Nonterminal.builder("CompOp").build(), Nonterminal.builder("Expr").build()).build()).build()).build())
			//ExceptClause ::= "except" (Test ("as" Name)?)? 
			.addRule(Rule.withHead(Nonterminal.builder("ExceptClause").build()).addSymbol(Sequence.builder(Character.builder(101).build(), Character.builder(120).build(), Character.builder(99).build(), Character.builder(101).build(), Character.builder(112).build(), Character.builder(116).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Sequence.builder(Nonterminal.builder("Test").build(), org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(97).build(), Character.builder(115).build()).build(), Nonterminal.builder("Name").build()).build()).build()).build()).build()).build())
			//LongBytes ::= """"" LongBytesItem* """"" 
			.addRule(Rule.withHead(Nonterminal.builder("LongBytes").build()).addSymbol(Sequence.builder(Character.builder(34).build(), Character.builder(34).build(), Character.builder(34).build()).build()).addSymbol(Star.builder(Nonterminal.builder("LongBytesItem").build()).build()).addSymbol(Sequence.builder(Character.builder(34).build(), Character.builder(34).build(), Character.builder(34).build()).build()).build())
			//LongBytes ::= "'''" LongBytesItem* "'''" 
			.addRule(Rule.withHead(Nonterminal.builder("LongBytes").build()).addSymbol(Sequence.builder(Character.builder(39).build(), Character.builder(39).build(), Character.builder(39).build()).build()).addSymbol(Star.builder(Nonterminal.builder("LongBytesItem").build()).build()).addSymbol(Sequence.builder(Character.builder(39).build(), Character.builder(39).build(), Character.builder(39).build()).build()).build())
			//ImportName ::= "import" DottedAsNames 
			.addRule(Rule.withHead(Nonterminal.builder("ImportName").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build()).addSymbol(Nonterminal.builder("DottedAsNames").build()).build())
			//OctDigit ::= (0-7) 
			.addRule(Rule.withHead(Nonterminal.builder("OctDigit").build()).addSymbol(Alt.builder(CharacterRange.builder(48, 55).build()).build()).build())
			//Arglist ::= (Argument ",")* ((Argument (,)?) | ("*" Test ("," Argument)* ("," "**" Test)?) | ("**" Test)) 
			.addRule(Rule.withHead(Nonterminal.builder("Arglist").build()).addSymbol(Star.builder(Sequence.builder(Nonterminal.builder("Argument").build(), Sequence.builder(Character.builder(44).build()).build()).build()).build()).addSymbol(Alt.builder(Sequence.builder(Nonterminal.builder("Argument").build(), org.iguana.regex.Opt.builder(Sequence.builder(Character.builder(44).build()).build()).build()).build(), Sequence.builder(Sequence.builder(Character.builder(42).build()).build(), Nonterminal.builder("Test").build(), Star.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Nonterminal.builder("Argument").build()).build()).build(), org.iguana.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Sequence.builder(Character.builder(42).build(), Character.builder(42).build()).build(), Nonterminal.builder("Test").build()).build()).build()).build(), Sequence.builder(Sequence.builder(Character.builder(42).build(), Character.builder(42).build()).build(), Nonterminal.builder("Test").build()).build()).build()).build())
			//BinDigit ::= "0" 
			.addRule(Rule.withHead(Nonterminal.builder("BinDigit").build()).addSymbol(Sequence.builder(Character.builder(48).build()).build()).build())
			//BinDigit ::= "1" 
			.addRule(Rule.withHead(Nonterminal.builder("BinDigit").build()).addSymbol(Sequence.builder(Character.builder(49).build()).build()).build())
			//DottedName ::= Name ("." Name)* 
			.addRule(Rule.withHead(Nonterminal.builder("DottedName").build()).addSymbol(Nonterminal.builder("Name").build()).addSymbol(Star.builder(Sequence.builder(Sequence.builder(Character.builder(46).build()).build(), Nonterminal.builder("Name").build()).build()).build()).build())
			//SingleInput ::= NewLine 
			.addRule(Rule.withHead(Nonterminal.builder("SingleInput").build()).addSymbol(Nonterminal.builder("NewLine").build()).build())
			//SingleInput ::= CompoundStmt NewLine 
			.addRule(Rule.withHead(Nonterminal.builder("SingleInput").build()).addSymbol(Nonterminal.builder("CompoundStmt").build()).addSymbol(Nonterminal.builder("NewLine").build()).build())
			//SingleInput ::= SimpleStmt 
			.addRule(Rule.withHead(Nonterminal.builder("SingleInput").build()).addSymbol(Nonterminal.builder("SimpleStmt").build()).build())
			//ShortStringChar ::= (\u0001-\u10FFFF) 
			.addRule(Rule.withHead(Nonterminal.builder("ShortStringChar").build()).addSymbol(Alt.builder(CharacterRange.builder(1, 1114111).build()).addPostConditions(set(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Character.builder(10).build()).build()))).build()).build())
			//WithStmt ::= "with" WithItem ("," WithItem)* ":" Suite 
			.addRule(Rule.withHead(Nonterminal.builder("WithStmt").build()).addSymbol(Sequence.builder(Character.builder(119).build(), Character.builder(105).build(), Character.builder(116).build(), Character.builder(104).build()).build()).addSymbol(Nonterminal.builder("WithItem").build()).addSymbol(Star.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Nonterminal.builder("WithItem").build()).build()).build()).addSymbol(Sequence.builder(Character.builder(58).build()).build()).addSymbol(Nonterminal.builder("Suite").build()).build())
			//ExponentFloat ::= (IntPart | PointFloat) Exponent 
			.addRule(Rule.withHead(Nonterminal.builder("ExponentFloat").build()).addSymbol(Alt.builder(Nonterminal.builder("IntPart").build(), Nonterminal.builder("PointFloat").build()).build()).addSymbol(Nonterminal.builder("Exponent").build()).build())
			//Trailer ::= "(" Arglist? ")" 
			.addRule(Rule.withHead(Nonterminal.builder("Trailer").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Nonterminal.builder("Arglist").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
			//Trailer ::= "[" Subscriptlist "]" 
			.addRule(Rule.withHead(Nonterminal.builder("Trailer").build()).addSymbol(Sequence.builder(Character.builder(91).build()).build()).addSymbol(Nonterminal.builder("Subscriptlist").build()).addSymbol(Sequence.builder(Character.builder(93).build()).build()).build())
			//Trailer ::= "." Name 
			.addRule(Rule.withHead(Nonterminal.builder("Trailer").build()).addSymbol(Sequence.builder(Character.builder(46).build()).build()).addSymbol(Nonterminal.builder("Name").build()).build())
			//CompFor ::= "for" Exprlist "in" OrTest CompIter? 
			.addRule(Rule.withHead(Nonterminal.builder("CompFor").build()).addSymbol(Sequence.builder(Character.builder(102).build(), Character.builder(111).build(), Character.builder(114).build()).build()).addSymbol(Nonterminal.builder("Exprlist").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(110).build()).build()).addSymbol(Nonterminal.builder("OrTest").build()).addSymbol(org.iguana.regex.Opt.builder(Nonterminal.builder("CompIter").build()).build()).build())
			//Lambdef ::= "lambda" Varargslist? ":" Test 
			.addRule(Rule.withHead(Nonterminal.builder("Lambdef").build()).addSymbol(Sequence.builder(Character.builder(108).build(), Character.builder(97).build(), Character.builder(109).build(), Character.builder(98).build(), Character.builder(100).build(), Character.builder(97).build()).build()).addSymbol(org.iguana.regex.Opt.builder(Nonterminal.builder("Varargslist").build()).build()).addSymbol(Sequence.builder(Character.builder(58).build()).build()).addSymbol(Nonterminal.builder("Test").build()).build())
			//ExprStmt ::= TestlistStarExpr ((Augassign (TestList | YieldExpr)) | ("=" (TestlistStarExpr | YieldExpr))*) 
			.addRule(Rule.withHead(Nonterminal.builder("ExprStmt").build()).addSymbol(Nonterminal.builder("TestlistStarExpr").build()).addSymbol(Alt.builder(Sequence.builder(Nonterminal.builder("Augassign").build(), Alt.builder(Nonterminal.builder("TestList").build(), Nonterminal.builder("YieldExpr").build()).build()).build(), Star.builder(Sequence.builder(Sequence.builder(Character.builder(61).build()).build(), Alt.builder(Nonterminal.builder("TestlistStarExpr").build(), Nonterminal.builder("YieldExpr").build()).build()).build()).build()).build()).build())
			//Name ::= (A-Z | _ | a-z) (0-9 | A-Z | _ | a-z)* 
			.addRule(Rule.withHead(Nonterminal.builder("Name").build()).addSymbol(Alt.builder(CharacterRange.builder(65, 90).build(), CharacterRange.builder(95, 95).build(), CharacterRange.builder(97, 122).build()).build()).addSymbol(Star.builder(Alt.builder(CharacterRange.builder(48, 57).build(), CharacterRange.builder(65, 90).build(), CharacterRange.builder(95, 95).build(), CharacterRange.builder(97, 122).build()).build()).build()).build())
			
			
			/**
			 * Data-dependent
			 * 
			 *  NoNL ::= (' ' | '\t')* !>> (' ' | '\t')
			 */
			.addRule(Rule.withHead(Nonterminal.builder("NoNL").build())
						.addSymbol(Star.builder(Alt.from(Character.from(' '), Character.from('\t')))
								.addPostCondition(RegularExpressionCondition.notFollow(Character.from(' ')))
								.addPostCondition(RegularExpressionCondition.notFollow(Character.from('\t'))).build()).build())
			
			/**
			 *  L ::= (' ' | '\t' | '\r' | '\n')* !>> (' ' | '\t' | '\r' | '\n')
			 */
			.addRule(Rule.withHead(Nonterminal.builder("L").build())
						.addSymbol(Star.builder(Alt.from(Character.from(' '), Character.from('\t'), Character.from('\r'), Character.from('\n')))
								.addPostCondition(RegularExpressionCondition.notFollow(Character.from(' ')))
								.addPostCondition(RegularExpressionCondition.notFollow(Character.from('\t')))
								.addPostCondition(RegularExpressionCondition.notFollow(Character.from('\r')))
								.addPostCondition(RegularExpressionCondition.notFollow(Character.from('\n'))).build()).build())
			
			/**
			 ********** LOGICAL NEW LINE:
			 *
			 * NL ::= ('\n' | '\r') !>> ('\n' | '\r') 
			 *      | ('\n' | '\r') (' ' | '\t' | '\n' | '\r')* ('\n' | '\r') !>> ('\n' | '\r')
			 */
			.addRule(Rule.withHead(Nonterminal.builder("NL").build())
						.addSymbol(Alt.builder(Character.from('\n'), Character.from('\r'))
										.addPostCondition(RegularExpressionCondition.notFollow(Character.from('\n')))
										.addPostCondition(RegularExpressionCondition.notFollow(Character.from('\r'))).build()).build())
			.addRule(Rule.withHead(Nonterminal.builder("NL").build())
						.addSymbol(Alt.from(Character.from('\n'), Character.from('\r')))
						.addSymbol(Star.from(Alt.from(Character.from(' '), Character.from('\t'), Character.from('\n'), Character.from('\r'))))
						.addSymbol(Alt.builder(Character.from('\n'), Character.from('\r'))
										.addPostCondition(RegularExpressionCondition.notFollow(Character.from('\n')))
										.addPostCondition(RegularExpressionCondition.notFollow(Character.from('\r'))).build()).build())
										
			.build();

	
	@Test
	public void test() {
		
		Input input = Input.fromString("if a  :  "   + "\n"
				                     + "    "        + "\n"
				                     + "  "          + "\n"
				                     + "   x=0"      + "\n"
				                     + "        "    + "\n"
				                     + "  "          + "\n"
				                     + "   y=0  "    + "\n"
				                     + "   z=0   "   + "\n"
				                     + "   if b: "   + "\n"
				                     + "    "        + "\n"
				                     + "         "   + "\n"
				                     + "      l=0"   + "\n"
				                     + "        "    + "\n"
				                     + "  "          + "\n"
				                     + "      k=0  " + "\n"
				                     + "   w=0   "   + "\n");
		
		GrammarGraph graph = GrammarGraph.from(grammar, input, Configuration.DEFAULT);
		GLLParser parser = new GLLParserImpl();
		
		ParseResult result = parser.parse(input, graph, Nonterminal.withName("IfStmt"));
	}
	
}
