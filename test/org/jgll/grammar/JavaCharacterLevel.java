package org.jgll.grammar;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.condition.ConditionType;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Terminal;
import org.jgll.regex.Alt;
import org.jgll.regex.Plus;
import org.jgll.regex.Sequence;
import org.jgll.regex.Star;

import static org.jgll.grammar.symbol.LayoutStrategy.*;

import com.google.common.collect.Sets;

public class JavaCharacterLevel {

	public static Grammar grammar = 
		Grammar.builder()
		.setLayout(Nonterminal.builder("Layout").build())
		// InputCharacter ::= UnicodeInputCharacter 
		.addRule(Rule.withHead(Nonterminal.builder("InputCharacter").build()).addSymbol(Nonterminal.builder("UnicodeInputCharacter").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Character.builder(10).build()).build()))).build()).setLayoutStrategy(NO_LAYOUT).build())
		// InputCharacter ::= \u0000 
		.addRule(Rule.withHead(Nonterminal.builder("InputCharacter").build()).addSymbol(Character.builder(0).build()).setLayoutStrategy(NO_LAYOUT).build())
		// TypeName ::= QualifiedIdentifier 
		.addRule(Rule.withHead(Nonterminal.builder("TypeName").build()).addSymbol(Nonterminal.builder("QualifiedIdentifier").build()).build())
		// Primary ::= PrimaryNoNewArray 
		.addRule(Rule.withHead(Nonterminal.builder("Primary").build()).addSymbol(Nonterminal.builder("PrimaryNoNewArray").build()).build())
		// Primary ::= ArrayCreationExpression 
		.addRule(Rule.withHead(Nonterminal.builder("Primary").build()).addSymbol(Nonterminal.builder("ArrayCreationExpression").build()).build())
		// CommentTail ::= (*) CommentTailStar 
		.addRule(Rule.withHead(Nonterminal.builder("CommentTail").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(42).build()).build()).build()).addSymbol(Nonterminal.builder("CommentTailStar").build()).setLayoutStrategy(NO_LAYOUT).build())
		// CommentTail ::= NotStar CommentTail 
		.addRule(Rule.withHead(Nonterminal.builder("CommentTail").build()).addSymbol(Nonterminal.builder("NotStar").build()).addSymbol(Nonterminal.builder("CommentTail").build()).setLayoutStrategy(NO_LAYOUT).build())
		// StatementWithoutTrailingSubstatement ::= (t r y) ResourceSpecification Block CatchClause* Finally? 
		.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(114).build(), Character.builder(121).build()).build()).build()).addSymbol(Nonterminal.builder("ResourceSpecification").build()).addSymbol(Nonterminal.builder("Block").build()).addSymbol(Star.builder(Nonterminal.builder("CatchClause").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Finally").build()).build()).build())
		// StatementWithoutTrailingSubstatement ::= (t h r o w) Expression (;) 
		.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(119).build()).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// StatementWithoutTrailingSubstatement ::= (t r y) Block (CatchClause+ | (CatchClause* Finally)) 
		.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(114).build(), Character.builder(121).build()).build()).build()).addSymbol(Nonterminal.builder("Block").build()).addSymbol(Alt.builder(Plus.builder(Nonterminal.builder("CatchClause").build()).build(), Sequence.builder(Star.builder(Nonterminal.builder("CatchClause").build()).build(), Nonterminal.builder("Finally").build()).build()).build()).build())
		// StatementWithoutTrailingSubstatement ::= (s w i t c h) (() Expression ()) ({) SwitchBlockStatementGroup* SwitchLabel* (}) 
		.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(119).build(), Character.builder(105).build(), Character.builder(116).build(), Character.builder(99).build(), Character.builder(104).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build()).addSymbol(Star.builder(Nonterminal.builder("SwitchBlockStatementGroup").build()).build()).addSymbol(Star.builder(Nonterminal.builder("SwitchLabel").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).build())
		// StatementWithoutTrailingSubstatement ::= (r e t u r n) Expression? (;) 
		.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(114).build(), Character.builder(101).build(), Character.builder(116).build(), Character.builder(117).build(), Character.builder(114).build(), Character.builder(110).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Expression").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// StatementWithoutTrailingSubstatement ::= (;) 
		.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// StatementWithoutTrailingSubstatement ::= (d o) Statement (w h i l e) (() Expression ()) (;) 
		.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(100).build(), Character.builder(111).build()).build()).build()).addSymbol(Nonterminal.builder("Statement").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(105).build(), Character.builder(108).build(), Character.builder(101).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// StatementWithoutTrailingSubstatement ::= (b r e a k) Identifier? (;) 
		.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(98).build(), Character.builder(114).build(), Character.builder(101).build(), Character.builder(97).build(), Character.builder(107).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Identifier").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// StatementWithoutTrailingSubstatement ::= (s y n c h r o n i z e d) (() Expression ()) Block 
		.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(121).build(), Character.builder(110).build(), Character.builder(99).build(), Character.builder(104).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(105).build(), Character.builder(122).build(), Character.builder(101).build(), Character.builder(100).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).addSymbol(Nonterminal.builder("Block").build()).build())
		// StatementWithoutTrailingSubstatement ::= (a s s e r t) Expression ((:) Expression)? (;) 
		.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(116).build()).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(58).build()).build()).build(), Nonterminal.builder("Expression").build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// StatementWithoutTrailingSubstatement ::= StatementExpression (;) 
		.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Nonterminal.builder("StatementExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// StatementWithoutTrailingSubstatement ::= (c o n t i n u e) Identifier? (;) 
		.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(117).build(), Character.builder(101).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Identifier").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// StatementWithoutTrailingSubstatement ::= Block 
		.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Nonterminal.builder("Block").build()).build())
		// ExplicitGenericInvocationSuffix ::= (s u p e r) SuperSuffix 
		.addRule(Rule.withHead(Nonterminal.builder("ExplicitGenericInvocationSuffix").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(117).build(), Character.builder(112).build(), Character.builder(101).build(), Character.builder(114).build()).build()).build()).addSymbol(Nonterminal.builder("SuperSuffix").build()).build())
		// ExplicitGenericInvocationSuffix ::= Identifier Arguments 
		.addRule(Rule.withHead(Nonterminal.builder("ExplicitGenericInvocationSuffix").build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Nonterminal.builder("Arguments").build()).build())
		// PreDecrementExpression ::= (- -) UnaryExpression 
		.addRule(Rule.withHead(Nonterminal.builder("PreDecrementExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build(), Character.builder(45).build()).build()).build()).addSymbol(Nonterminal.builder("UnaryExpression").build()).build())
		// HexDigit ::= (0-9 | A-F | a-f) 
		.addRule(Rule.withHead(Nonterminal.builder("HexDigit").build()).addSymbol(Alt.builder(CharacterRange.builder(48, 57).build(), CharacterRange.builder(65, 70).build(), CharacterRange.builder(97, 102).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// InterfaceBody ::= ({) InterfaceMemberDeclaration* (}) 
		.addRule(Rule.withHead(Nonterminal.builder("InterfaceBody").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build()).addSymbol(Star.builder(Nonterminal.builder("InterfaceMemberDeclaration").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).build())
		// ClassBody ::= ({) ClassBodyDeclaration* (}) 
		.addRule(Rule.withHead(Nonterminal.builder("ClassBody").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build()).addSymbol(Star.builder(Nonterminal.builder("ClassBodyDeclaration").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).build())
		// MethodDeclarator ::= MethodDeclarator ([) (]) 
		.addRule(Rule.withHead(Nonterminal.builder("MethodDeclarator").build()).addSymbol(Nonterminal.builder("MethodDeclarator").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(91).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(93).build()).build()).build()).build())
		// MethodDeclarator ::= Identifier (() FormalParameterList? ()) 
		.addRule(Rule.withHead(Nonterminal.builder("MethodDeclarator").build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("FormalParameterList").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// ConditionalAndExpression ::= ConditionalAndExpression (& &) InclusiveOrExpression 
		.addRule(Rule.withHead(Nonterminal.builder("ConditionalAndExpression").build()).addSymbol(Nonterminal.builder("ConditionalAndExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(38).build(), Character.builder(38).build()).build()).build()).addSymbol(Nonterminal.builder("InclusiveOrExpression").build()).build())
		// ConditionalAndExpression ::= InclusiveOrExpression 
		.addRule(Rule.withHead(Nonterminal.builder("ConditionalAndExpression").build()).addSymbol(Nonterminal.builder("InclusiveOrExpression").build()).build())
		// Keyword ::= (d o u b l e) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(100).build(), Character.builder(111).build(), Character.builder(117).build(), Character.builder(98).build(), Character.builder(108).build(), Character.builder(101).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (i n t) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(116).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (c a t c h) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(99).build(), Character.builder(104).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (t h r o w) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(119).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (s t r i c t f p) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(99).build(), Character.builder(116).build(), Character.builder(102).build(), Character.builder(112).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (c o n t i n u e) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(117).build(), Character.builder(101).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (f o r) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(102).build(), Character.builder(111).build(), Character.builder(114).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (b r e a k) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(98).build(), Character.builder(114).build(), Character.builder(101).build(), Character.builder(97).build(), Character.builder(107).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (n a t i v e) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(110).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(101).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (p a c k a g e) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(107).build(), Character.builder(97).build(), Character.builder(103).build(), Character.builder(101).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (s h o r t) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(104).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (i m p o r t) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (p r o t e c t e d) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(99).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(100).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (i m p l e m e n t s) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(108).build(), Character.builder(101).build(), Character.builder(109).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(116).build(), Character.builder(115).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (c a s e) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(101).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (w h i l e) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(105).build(), Character.builder(108).build(), Character.builder(101).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (s w i t c h) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(119).build(), Character.builder(105).build(), Character.builder(116).build(), Character.builder(99).build(), Character.builder(104).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (a s s e r t) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(116).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (c h a r) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(104).build(), Character.builder(97).build(), Character.builder(114).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (s u p e r) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(117).build(), Character.builder(112).build(), Character.builder(101).build(), Character.builder(114).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (c o n s t) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(115).build(), Character.builder(116).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (t h i s) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(105).build(), Character.builder(115).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (t r a n s i e n t) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(114).build(), Character.builder(97).build(), Character.builder(110).build(), Character.builder(115).build(), Character.builder(105).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(116).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (d e f a u l t) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(100).build(), Character.builder(101).build(), Character.builder(102).build(), Character.builder(97).build(), Character.builder(117).build(), Character.builder(108).build(), Character.builder(116).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (t h r o w s) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(119).build(), Character.builder(115).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (f l o a t) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(102).build(), Character.builder(108).build(), Character.builder(111).build(), Character.builder(97).build(), Character.builder(116).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (l o n g) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(108).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(103).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (s t a t i c) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(99).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (p u b l i c) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(117).build(), Character.builder(98).build(), Character.builder(108).build(), Character.builder(105).build(), Character.builder(99).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (v o l a t i l e) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(118).build(), Character.builder(111).build(), Character.builder(108).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(108).build(), Character.builder(101).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (v o i d) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(118).build(), Character.builder(111).build(), Character.builder(105).build(), Character.builder(100).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (f i n a l l y) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(102).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(97).build(), Character.builder(108).build(), Character.builder(108).build(), Character.builder(121).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (p r i v a t e) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(101).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (t r y) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(114).build(), Character.builder(121).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (n e w) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(110).build(), Character.builder(101).build(), Character.builder(119).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (b y t e) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(98).build(), Character.builder(121).build(), Character.builder(116).build(), Character.builder(101).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (e n u m) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(101).build(), Character.builder(110).build(), Character.builder(117).build(), Character.builder(109).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (s y n c h r o n i z e d) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(121).build(), Character.builder(110).build(), Character.builder(99).build(), Character.builder(104).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(105).build(), Character.builder(122).build(), Character.builder(101).build(), Character.builder(100).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (i f) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(102).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (i n t e r f a c e) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(102).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(101).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (i n s t a n c e o f) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(110).build(), Character.builder(99).build(), Character.builder(101).build(), Character.builder(111).build(), Character.builder(102).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (r e t u r n) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(114).build(), Character.builder(101).build(), Character.builder(116).build(), Character.builder(117).build(), Character.builder(114).build(), Character.builder(110).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (e l s e) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(101).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (f i n a l) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(102).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(97).build(), Character.builder(108).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (c l a s s) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(108).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (g o t o) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(103).build(), Character.builder(111).build(), Character.builder(116).build(), Character.builder(111).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (e x t e n d s) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(101).build(), Character.builder(120).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(100).build(), Character.builder(115).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (d o) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(100).build(), Character.builder(111).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (a b s t r a c t) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(97).build(), Character.builder(98).build(), Character.builder(115).build(), Character.builder(116).build(), Character.builder(114).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(116).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Keyword ::= (b o o l e a n) 
		.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(98).build(), Character.builder(111).build(), Character.builder(111).build(), Character.builder(108).build(), Character.builder(101).build(), Character.builder(97).build(), Character.builder(110).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// OctalIntegerLiteral ::= OctalNumeral IntegerTypeSuffix? 
		.addRule(Rule.withHead(Nonterminal.builder("OctalIntegerLiteral").build()).addSymbol(Nonterminal.builder("OctalNumeral").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("IntegerTypeSuffix").build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// TypeParameters ::= (<) TypeParameter+ (>) 
		.addRule(Rule.withHead(Nonterminal.builder("TypeParameters").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(60).build()).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("TypeParameter").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(62).build()).build()).build()).build())
		// TypeArgumentsOrDiamond ::= (<) (>) 
		.addRule(Rule.withHead(Nonterminal.builder("TypeArgumentsOrDiamond").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(60).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(62).build()).build()).build()).build())
		// TypeArgumentsOrDiamond ::= TypeArguments 
		.addRule(Rule.withHead(Nonterminal.builder("TypeArgumentsOrDiamond").build()).addSymbol(Nonterminal.builder("TypeArguments").build()).build())
		// Backslash ::= \ 
		.addRule(Rule.withHead(Nonterminal.builder("Backslash").build()).addSymbol(Character.builder(92).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Backslash ::= \ u+ (0 0 5) C 
		.addRule(Rule.withHead(Nonterminal.builder("Backslash").build()).addSymbol(Character.builder(92).build()).addSymbol(Plus.builder(Character.builder(117).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(48).build(), Character.builder(48).build(), Character.builder(53).build()).build()).build()).addSymbol(Character.builder(67).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ArgumentList ::= Expression+ 
		.addRule(Rule.withHead(Nonterminal.builder("ArgumentList").build()).addSymbol(Plus.builder(Nonterminal.builder("Expression").build()).build()).build())
		// BinaryNumeral ::= 0 b BinaryDigits 
		.addRule(Rule.withHead(Nonterminal.builder("BinaryNumeral").build()).addSymbol(Character.builder(48).build()).addSymbol(Character.builder(98).build()).addSymbol(Nonterminal.builder("BinaryDigits").build()).setLayoutStrategy(NO_LAYOUT).build())
		// BinaryNumeral ::= 0 B BinaryDigits 
		.addRule(Rule.withHead(Nonterminal.builder("BinaryNumeral").build()).addSymbol(Character.builder(48).build()).addSymbol(Character.builder(66).build()).addSymbol(Nonterminal.builder("BinaryDigits").build()).setLayoutStrategy(NO_LAYOUT).build())
		// OctalDigit ::= (0-7) 
		.addRule(Rule.withHead(Nonterminal.builder("OctalDigit").build()).addSymbol(Alt.builder(CharacterRange.builder(48, 55).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// $default$ ::= 
		.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).build())
		// UnaryExpressionNotPlusMinus ::= CastExpression 
		.addRule(Rule.withHead(Nonterminal.builder("UnaryExpressionNotPlusMinus").build()).addSymbol(Nonterminal.builder("CastExpression").build()).build())
		// UnaryExpressionNotPlusMinus ::= PostfixExpression 
		.addRule(Rule.withHead(Nonterminal.builder("UnaryExpressionNotPlusMinus").build()).addSymbol(Nonterminal.builder("PostfixExpression").build()).build())
		// UnaryExpressionNotPlusMinus ::= (~) UnaryExpression 
		.addRule(Rule.withHead(Nonterminal.builder("UnaryExpressionNotPlusMinus").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(126).build()).build()).build()).addSymbol(Nonterminal.builder("UnaryExpression").build()).build())
		// UnaryExpressionNotPlusMinus ::= (!) UnaryExpression 
		.addRule(Rule.withHead(Nonterminal.builder("UnaryExpressionNotPlusMinus").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(33).build()).build()).build()).addSymbol(Nonterminal.builder("UnaryExpression").build()).build())
		// PostDecrementExpression ::= PostfixExpression (- -) 
		.addRule(Rule.withHead(Nonterminal.builder("PostDecrementExpression").build()).addSymbol(Nonterminal.builder("PostfixExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build(), Character.builder(45).build()).build()).build()).build())
		// ClassDeclaration ::= NormalClassDeclaration 
		.addRule(Rule.withHead(Nonterminal.builder("ClassDeclaration").build()).addSymbol(Nonterminal.builder("NormalClassDeclaration").build()).build())
		// ClassDeclaration ::= EnumDeclaration 
		.addRule(Rule.withHead(Nonterminal.builder("ClassDeclaration").build()).addSymbol(Nonterminal.builder("EnumDeclaration").build()).build())
		// ResourceSpecification ::= (() Resources (;)? ()) 
		.addRule(Rule.withHead(Nonterminal.builder("ResourceSpecification").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("Resources").build()).addSymbol(org.jgll.regex.Opt.builder(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// StaticInitializer ::= (s t a t i c) Block 
		.addRule(Rule.withHead(Nonterminal.builder("StaticInitializer").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(99).build()).build()).build()).addSymbol(Nonterminal.builder("Block").build()).build())
		// ImportDeclaration ::= (i m p o r t) (s t a t i c)? Identifier+ ((.) (*))? (;) 
		.addRule(Rule.withHead(Nonterminal.builder("ImportDeclaration").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(99).build()).build()).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("Identifier").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(42).build()).build()).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// SuperSuffix ::= (.) Identifier Arguments? 
		.addRule(Rule.withHead(Nonterminal.builder("SuperSuffix").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Arguments").build()).build()).build())
		// SuperSuffix ::= Arguments 
		.addRule(Rule.withHead(Nonterminal.builder("SuperSuffix").build()).addSymbol(Nonterminal.builder("Arguments").build()).build())
		// UnaryExpression ::= PreIncrementExpression 
		.addRule(Rule.withHead(Nonterminal.builder("UnaryExpression").build()).addSymbol(Nonterminal.builder("PreIncrementExpression").build()).build())
		// UnaryExpression ::= (-) UnaryExpression 
		.addRule(Rule.withHead(Nonterminal.builder("UnaryExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build()).build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Terminal.builder(Sequence.builder(Character.builder(45).build()).build()).build()))).build()).addSymbol(Nonterminal.builder("UnaryExpression").build()).build())
		// UnaryExpression ::= (+) UnaryExpression 
		.addRule(Rule.withHead(Nonterminal.builder("UnaryExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(43).build()).build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Terminal.builder(Sequence.builder(Character.builder(43).build()).build()).build()))).build()).addSymbol(Nonterminal.builder("UnaryExpression").build()).build())
		// UnaryExpression ::= PreDecrementExpression 
		.addRule(Rule.withHead(Nonterminal.builder("UnaryExpression").build()).addSymbol(Nonterminal.builder("PreDecrementExpression").build()).build())
		// UnaryExpression ::= UnaryExpressionNotPlusMinus 
		.addRule(Rule.withHead(Nonterminal.builder("UnaryExpression").build()).addSymbol(Nonterminal.builder("UnaryExpressionNotPlusMinus").build()).build())
		// ElementValue ::= ElementValueArrayInitializer 
		.addRule(Rule.withHead(Nonterminal.builder("ElementValue").build()).addSymbol(Nonterminal.builder("ElementValueArrayInitializer").build()).build())
		// ElementValue ::= ConditionalExpression 
		.addRule(Rule.withHead(Nonterminal.builder("ElementValue").build()).addSymbol(Nonterminal.builder("ConditionalExpression").build()).build())
		// ElementValue ::= Annotation 
		.addRule(Rule.withHead(Nonterminal.builder("ElementValue").build()).addSymbol(Nonterminal.builder("Annotation").build()).build())
		// Resource ::= VariableModifier* ReferenceType VariableDeclaratorId (=) Expression 
		.addRule(Rule.withHead(Nonterminal.builder("Resource").build()).addSymbol(Star.builder(Nonterminal.builder("VariableModifier").build()).build()).addSymbol(Nonterminal.builder("ReferenceType").build()).addSymbol(Nonterminal.builder("VariableDeclaratorId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(61).build()).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).build())
		// CatchClause ::= (c a t c h) (() VariableModifier* CatchType Identifier ()) Block 
		.addRule(Rule.withHead(Nonterminal.builder("CatchClause").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(99).build(), Character.builder(104).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Star.builder(Nonterminal.builder("VariableModifier").build()).build()).addSymbol(Nonterminal.builder("CatchType").build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).addSymbol(Nonterminal.builder("Block").build()).build())
		// QualifiedIdentifierList ::= QualifiedIdentifier+ 
		.addRule(Rule.withHead(Nonterminal.builder("QualifiedIdentifierList").build()).addSymbol(Plus.builder(Nonterminal.builder("QualifiedIdentifier").build()).build()).build())
		// Statement ::= StatementWithoutTrailingSubstatement 
		.addRule(Rule.withHead(Nonterminal.builder("Statement").build()).addSymbol(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).build())
		// Statement ::= ForStatement 
		.addRule(Rule.withHead(Nonterminal.builder("Statement").build()).addSymbol(Nonterminal.builder("ForStatement").build()).build())
		// Statement ::= Identifier (:) Statement 
		.addRule(Rule.withHead(Nonterminal.builder("Statement").build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(58).build()).build()).build()).addSymbol(Nonterminal.builder("Statement").build()).build())
		// Statement ::= (w h i l e) (() Expression ()) Statement 
		.addRule(Rule.withHead(Nonterminal.builder("Statement").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(105).build(), Character.builder(108).build(), Character.builder(101).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).addSymbol(Nonterminal.builder("Statement").build()).build())
		// Statement ::= (i f) (() Expression ()) Statement 
		.addRule(Rule.withHead(Nonterminal.builder("Statement").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(102).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).addSymbol(Nonterminal.builder("Statement").build()).build())
		// Statement ::= (i f) (() Expression ()) StatementNoShortIf (e l s e) Statement 
		.addRule(Rule.withHead(Nonterminal.builder("Statement").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(102).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).addSymbol(Nonterminal.builder("StatementNoShortIf").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(101).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build()).build()).addSymbol(Nonterminal.builder("Statement").build()).build())
		// QualifiedIdentifier ::= Identifier+ 
		.addRule(Rule.withHead(Nonterminal.builder("QualifiedIdentifier").build()).addSymbol(Plus.builder(Nonterminal.builder("Identifier").build()).build()).build())
		// Digit ::= 0 
		.addRule(Rule.withHead(Nonterminal.builder("Digit").build()).addSymbol(Character.builder(48).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Digit ::= NonZeroDigit 
		.addRule(Rule.withHead(Nonterminal.builder("Digit").build()).addSymbol(Nonterminal.builder("NonZeroDigit").build()).setLayoutStrategy(NO_LAYOUT).build())
		// UnicodeEscape ::= \ u+ HexDigit HexDigit HexDigit HexDigit 
		.addRule(Rule.withHead(Nonterminal.builder("UnicodeEscape").build()).addSymbol(Character.builder(92).build()).addSymbol(Plus.builder(Character.builder(117).build()).build()).addSymbol(Nonterminal.builder("HexDigit").build()).addSymbol(Nonterminal.builder("HexDigit").build()).addSymbol(Nonterminal.builder("HexDigit").build()).addSymbol(Nonterminal.builder("HexDigit").build()).setLayoutStrategy(NO_LAYOUT).build())
		// ExclusiveOrExpression ::= ExclusiveOrExpression (^) AndExpression 
		.addRule(Rule.withHead(Nonterminal.builder("ExclusiveOrExpression").build()).addSymbol(Nonterminal.builder("ExclusiveOrExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(94).build()).build()).build()).addSymbol(Nonterminal.builder("AndExpression").build()).build())
		// ExclusiveOrExpression ::= AndExpression 
		.addRule(Rule.withHead(Nonterminal.builder("ExclusiveOrExpression").build()).addSymbol(Nonterminal.builder("AndExpression").build()).build())
		// AnnotationTypeDeclaration ::= InterfaceModifier* (@) (i n t e r f a c e) Identifier AnnotationTypeBody 
		.addRule(Rule.withHead(Nonterminal.builder("AnnotationTypeDeclaration").build()).addSymbol(Star.builder(Nonterminal.builder("InterfaceModifier").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(64).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(102).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(101).build()).build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Nonterminal.builder("AnnotationTypeBody").build()).build())
		// TypeArguments ::= (<) TypeArgument+ (>) 
		.addRule(Rule.withHead(Nonterminal.builder("TypeArguments").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(60).build()).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("TypeArgument").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(62).build()).build()).build()).build())
		// Annotation ::= (@) TypeName ((() ElementValue ()))? 
		.addRule(Rule.withHead(Nonterminal.builder("Annotation").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(64).build()).build()).build()).addSymbol(Nonterminal.builder("TypeName").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build(), Nonterminal.builder("ElementValue").build(), Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build()).build()).build())
		// Annotation ::= (@) TypeName (() ElementValuePair* ()) 
		.addRule(Rule.withHead(Nonterminal.builder("Annotation").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(64).build()).build()).build()).addSymbol(Nonterminal.builder("TypeName").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Star.builder(Nonterminal.builder("ElementValuePair").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// ClassBodyDeclaration ::= InstanceInitializer 
		.addRule(Rule.withHead(Nonterminal.builder("ClassBodyDeclaration").build()).addSymbol(Nonterminal.builder("InstanceInitializer").build()).build())
		// ClassBodyDeclaration ::= StaticInitializer 
		.addRule(Rule.withHead(Nonterminal.builder("ClassBodyDeclaration").build()).addSymbol(Nonterminal.builder("StaticInitializer").build()).build())
		// ClassBodyDeclaration ::= ClassMemberDeclaration 
		.addRule(Rule.withHead(Nonterminal.builder("ClassBodyDeclaration").build()).addSymbol(Nonterminal.builder("ClassMemberDeclaration").build()).build())
		// ClassBodyDeclaration ::= ConstructorDeclaration 
		.addRule(Rule.withHead(Nonterminal.builder("ClassBodyDeclaration").build()).addSymbol(Nonterminal.builder("ConstructorDeclaration").build()).build())
		// ForUpdate ::= StatementExpression+ 
		.addRule(Rule.withHead(Nonterminal.builder("ForUpdate").build()).addSymbol(Plus.builder(Nonterminal.builder("StatementExpression").build()).build()).build())
		// NotStarNotSlash ::= InputCharacter 
		.addRule(Rule.withHead(Nonterminal.builder("NotStarNotSlash").build()).addSymbol(Nonterminal.builder("InputCharacter").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Character.builder(42).build()).build()))).build()).setLayoutStrategy(NO_LAYOUT).build())
		// NotStarNotSlash ::= LineTerminator 
		.addRule(Rule.withHead(Nonterminal.builder("NotStarNotSlash").build()).addSymbol(Nonterminal.builder("LineTerminator").build()).setLayoutStrategy(NO_LAYOUT).build())
		// ForStatement ::= (f o r) (() FormalParameter (:) Expression ()) Statement 
		.addRule(Rule.withHead(Nonterminal.builder("ForStatement").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(102).build(), Character.builder(111).build(), Character.builder(114).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("FormalParameter").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(58).build()).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).addSymbol(Nonterminal.builder("Statement").build()).build())
		// ForStatement ::= (f o r) (() ForInit? (;) Expression? (;) ForUpdate? ()) Statement 
		.addRule(Rule.withHead(Nonterminal.builder("ForStatement").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(102).build(), Character.builder(111).build(), Character.builder(114).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ForInit").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Expression").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ForUpdate").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).addSymbol(Nonterminal.builder("Statement").build()).build())
		// Throws ::= (t h r o w s) ExceptionType+ 
		.addRule(Rule.withHead(Nonterminal.builder("Throws").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(119).build(), Character.builder(115).build()).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("ExceptionType").build()).build()).build())
		// StatementExpression ::= PostIncrementExpression 
		.addRule(Rule.withHead(Nonterminal.builder("StatementExpression").build()).addSymbol(Nonterminal.builder("PostIncrementExpression").build()).build())
		// StatementExpression ::= PostDecrementExpression 
		.addRule(Rule.withHead(Nonterminal.builder("StatementExpression").build()).addSymbol(Nonterminal.builder("PostDecrementExpression").build()).build())
		// StatementExpression ::= Assignment 
		.addRule(Rule.withHead(Nonterminal.builder("StatementExpression").build()).addSymbol(Nonterminal.builder("Assignment").build()).build())
		// StatementExpression ::= ClassInstanceCreationExpression 
		.addRule(Rule.withHead(Nonterminal.builder("StatementExpression").build()).addSymbol(Nonterminal.builder("ClassInstanceCreationExpression").build()).build())
		// StatementExpression ::= PreIncrementExpression 
		.addRule(Rule.withHead(Nonterminal.builder("StatementExpression").build()).addSymbol(Nonterminal.builder("PreIncrementExpression").build()).build())
		// StatementExpression ::= PreDecrementExpression 
		.addRule(Rule.withHead(Nonterminal.builder("StatementExpression").build()).addSymbol(Nonterminal.builder("PreDecrementExpression").build()).build())
		// StatementExpression ::= MethodInvocation 
		.addRule(Rule.withHead(Nonterminal.builder("StatementExpression").build()).addSymbol(Nonterminal.builder("MethodInvocation").build()).build())
		// Comment ::= TraditionalComment 
		.addRule(Rule.withHead(Nonterminal.builder("Comment").build()).addSymbol(Nonterminal.builder("TraditionalComment").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Comment ::= EndOfLineComment 
		.addRule(Rule.withHead(Nonterminal.builder("Comment").build()).addSymbol(Nonterminal.builder("EndOfLineComment").build()).setLayoutStrategy(NO_LAYOUT).build())
		// ConstantModifier ::= (f i n a l) 
		.addRule(Rule.withHead(Nonterminal.builder("ConstantModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(102).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(97).build(), Character.builder(108).build()).build()).build()).build())
		// ConstantModifier ::= Annotation 
		.addRule(Rule.withHead(Nonterminal.builder("ConstantModifier").build()).addSymbol(Nonterminal.builder("Annotation").build()).build())
		// ConstantModifier ::= (p u b l i c) 
		.addRule(Rule.withHead(Nonterminal.builder("ConstantModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(117).build(), Character.builder(98).build(), Character.builder(108).build(), Character.builder(105).build(), Character.builder(99).build()).build()).build()).build())
		// ConstantModifier ::= (s t a t i c) 
		.addRule(Rule.withHead(Nonterminal.builder("ConstantModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(99).build()).build()).build()).build())
		// WhiteSpace ::=  
		.addRule(Rule.withHead(Nonterminal.builder("WhiteSpace").build()).addSymbol(Alt.builder(CharacterRange.builder(9, 10).build(), CharacterRange.builder(12, 13).build(), CharacterRange.builder(26, 26).build(), CharacterRange.builder(32, 32).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// TypeParameter ::= TypeVariable TypeBound? 
		.addRule(Rule.withHead(Nonterminal.builder("TypeParameter").build()).addSymbol(Nonterminal.builder("TypeVariable").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeBound").build()).build()).build())
		// MethodHeader ::= MethodModifier* TypeParameters? Result MethodDeclarator Throws? 
		.addRule(Rule.withHead(Nonterminal.builder("MethodHeader").build()).addSymbol(Star.builder(Nonterminal.builder("MethodModifier").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeParameters").build()).build()).addSymbol(Nonterminal.builder("Result").build()).addSymbol(Nonterminal.builder("MethodDeclarator").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Throws").build()).build()).build())
		// AbstractMethodModifier ::= (a b s t r a c t) 
		.addRule(Rule.withHead(Nonterminal.builder("AbstractMethodModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(97).build(), Character.builder(98).build(), Character.builder(115).build(), Character.builder(116).build(), Character.builder(114).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(116).build()).build()).build()).build())
		// AbstractMethodModifier ::= Annotation 
		.addRule(Rule.withHead(Nonterminal.builder("AbstractMethodModifier").build()).addSymbol(Nonterminal.builder("Annotation").build()).build())
		// AbstractMethodModifier ::= (p u b l i c) 
		.addRule(Rule.withHead(Nonterminal.builder("AbstractMethodModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(117).build(), Character.builder(98).build(), Character.builder(108).build(), Character.builder(105).build(), Character.builder(99).build()).build()).build()).build())
		// ClassName ::= QualifiedIdentifier 
		.addRule(Rule.withHead(Nonterminal.builder("ClassName").build()).addSymbol(Nonterminal.builder("QualifiedIdentifier").build()).build())
		// HexadecimalFloatingPointLiteral ::= HexSignificand BinaryExponent FloatTypeSuffix? 
		.addRule(Rule.withHead(Nonterminal.builder("HexadecimalFloatingPointLiteral").build()).addSymbol(Nonterminal.builder("HexSignificand").build()).addSymbol(Nonterminal.builder("BinaryExponent").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("FloatTypeSuffix").build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// CommentTailStar ::= (*) CommentTailStar 
		.addRule(Rule.withHead(Nonterminal.builder("CommentTailStar").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(42).build()).build()).build()).addSymbol(Nonterminal.builder("CommentTailStar").build()).setLayoutStrategy(NO_LAYOUT).build())
		// CommentTailStar ::= (/) 
		.addRule(Rule.withHead(Nonterminal.builder("CommentTailStar").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(47).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// CommentTailStar ::= NotStarNotSlash CommentTail 
		.addRule(Rule.withHead(Nonterminal.builder("CommentTailStar").build()).addSymbol(Nonterminal.builder("NotStarNotSlash").build()).addSymbol(Nonterminal.builder("CommentTail").build()).setLayoutStrategy(NO_LAYOUT).build())
		// ConstructorBody ::= ({) ExplicitConstructorInvocation? BlockStatement* (}) 
		.addRule(Rule.withHead(Nonterminal.builder("ConstructorBody").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ExplicitConstructorInvocation").build()).build()).addSymbol(Star.builder(Nonterminal.builder("BlockStatement").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).build())
		// EndOfLineComment ::= (/ /) InputCharacter* 
		.addRule(Rule.withHead(Nonterminal.builder("EndOfLineComment").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(47).build(), Character.builder(47).build()).build()).build()).addSymbol(Star.builder(Nonterminal.builder("InputCharacter").build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Alt.builder(CharacterRange.builder(1, 9).build(), CharacterRange.builder(11, 12).build(), CharacterRange.builder(14, 1114111).build()).build()))).build()).setLayoutStrategy(NO_LAYOUT).build())
		// OctalDigits ::= OctalDigit OctalDigitOrUnderscore* OctalDigit 
		.addRule(Rule.withHead(Nonterminal.builder("OctalDigits").build()).addSymbol(Nonterminal.builder("OctalDigit").build()).addSymbol(Star.builder(Nonterminal.builder("OctalDigitOrUnderscore").build()).build()).addSymbol(Nonterminal.builder("OctalDigit").build()).setLayoutStrategy(NO_LAYOUT).build())
		// OctalDigits ::= OctalDigit 
		.addRule(Rule.withHead(Nonterminal.builder("OctalDigits").build()).addSymbol(Nonterminal.builder("OctalDigit").build()).setLayoutStrategy(NO_LAYOUT).build())
		// ConstantExpression ::= Expression 
		.addRule(Rule.withHead(Nonterminal.builder("ConstantExpression").build()).addSymbol(Nonterminal.builder("Expression").build()).build())
		// FieldDeclaration ::= FieldModifier* Type VariableDeclarators (;) 
		.addRule(Rule.withHead(Nonterminal.builder("FieldDeclaration").build()).addSymbol(Star.builder(Nonterminal.builder("FieldModifier").build()).build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Nonterminal.builder("VariableDeclarators").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// TypeBound ::= (e x t e n d s) ReferenceType+ 
		.addRule(Rule.withHead(Nonterminal.builder("TypeBound").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(101).build(), Character.builder(120).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(100).build(), Character.builder(115).build()).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("ReferenceType").build()).build()).build())
		// BlockStatement ::= LocalVariableDeclarationStatement 
		.addRule(Rule.withHead(Nonterminal.builder("BlockStatement").build()).addSymbol(Nonterminal.builder("LocalVariableDeclarationStatement").build()).build())
		// BlockStatement ::= ClassDeclaration 
		.addRule(Rule.withHead(Nonterminal.builder("BlockStatement").build()).addSymbol(Nonterminal.builder("ClassDeclaration").build()).build())
		// BlockStatement ::= Statement 
		.addRule(Rule.withHead(Nonterminal.builder("BlockStatement").build()).addSymbol(Nonterminal.builder("Statement").build()).build())
		// NullLiteral ::= (n u l l) 
		.addRule(Rule.withHead(Nonterminal.builder("NullLiteral").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(110).build(), Character.builder(117).build(), Character.builder(108).build(), Character.builder(108).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// DimExpr ::= ([) Expression (]) 
		.addRule(Rule.withHead(Nonterminal.builder("DimExpr").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(91).build()).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(93).build()).build()).build()).build())
		// ConstantDeclaration ::= ConstantModifier* Type VariableDeclarators (;) 
		.addRule(Rule.withHead(Nonterminal.builder("ConstantDeclaration").build()).addSymbol(Star.builder(Nonterminal.builder("ConstantModifier").build()).build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Nonterminal.builder("VariableDeclarators").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// InterfaceDeclaration ::= NormalInterfaceDeclaration 
		.addRule(Rule.withHead(Nonterminal.builder("InterfaceDeclaration").build()).addSymbol(Nonterminal.builder("NormalInterfaceDeclaration").build()).build())
		// InterfaceDeclaration ::= AnnotationTypeDeclaration 
		.addRule(Rule.withHead(Nonterminal.builder("InterfaceDeclaration").build()).addSymbol(Nonterminal.builder("AnnotationTypeDeclaration").build()).build())
		// AbstractMethodDeclaration ::= AbstractMethodModifier* TypeParameters? Result MethodDeclarator Throws? (;) 
		.addRule(Rule.withHead(Nonterminal.builder("AbstractMethodDeclaration").build()).addSymbol(Star.builder(Nonterminal.builder("AbstractMethodModifier").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeParameters").build()).build()).addSymbol(Nonterminal.builder("Result").build()).addSymbol(Nonterminal.builder("MethodDeclarator").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Throws").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// FormalParameterList ::= (FormalParameter (,))* LastFormalParameter 
		.addRule(Rule.withHead(Nonterminal.builder("FormalParameterList").build()).addSymbol(Star.builder(Sequence.builder(Nonterminal.builder("FormalParameter").build(), Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build()).build()).build()).addSymbol(Nonterminal.builder("LastFormalParameter").build()).build())
		// NonZeroDigit ::= (1-9) 
		.addRule(Rule.withHead(Nonterminal.builder("NonZeroDigit").build()).addSymbol(Alt.builder(CharacterRange.builder(49, 57).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ArrayInitializer ::= ({) VariableInitializer* (,)? (}) 
		.addRule(Rule.withHead(Nonterminal.builder("ArrayInitializer").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build()).addSymbol(Star.builder(Nonterminal.builder("VariableInitializer").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).build())
		// ConstructorDeclaration ::= ConstructorModifier* ConstructorDeclarator Throws? ConstructorBody 
		.addRule(Rule.withHead(Nonterminal.builder("ConstructorDeclaration").build()).addSymbol(Star.builder(Nonterminal.builder("ConstructorModifier").build()).build()).addSymbol(Nonterminal.builder("ConstructorDeclarator").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Throws").build()).build()).addSymbol(Nonterminal.builder("ConstructorBody").build()).build())
		// LocalVariableDeclarationStatement ::= VariableModifier* Type VariableDeclarators (;) 
		.addRule(Rule.withHead(Nonterminal.builder("LocalVariableDeclarationStatement").build()).addSymbol(Star.builder(Nonterminal.builder("VariableModifier").build()).build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Nonterminal.builder("VariableDeclarators").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// ExponentIndicator ::= E 
		.addRule(Rule.withHead(Nonterminal.builder("ExponentIndicator").build()).addSymbol(Character.builder(69).build()).setLayoutStrategy(NO_LAYOUT).build())
		// RelationalExpression ::= ShiftExpression 
		.addRule(Rule.withHead(Nonterminal.builder("RelationalExpression").build()).addSymbol(Nonterminal.builder("ShiftExpression").build()).build())
		// RelationalExpression ::= RelationalExpression (< =) ShiftExpression 
		.addRule(Rule.withHead(Nonterminal.builder("RelationalExpression").build()).addSymbol(Nonterminal.builder("RelationalExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(60).build(), Character.builder(61).build()).build()).build()).addSymbol(Nonterminal.builder("ShiftExpression").build()).build())
		// RelationalExpression ::= RelationalExpression (> =) ShiftExpression 
		.addRule(Rule.withHead(Nonterminal.builder("RelationalExpression").build()).addSymbol(Nonterminal.builder("RelationalExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(62).build(), Character.builder(61).build()).build()).build()).addSymbol(Nonterminal.builder("ShiftExpression").build()).build())
		// RelationalExpression ::= RelationalExpression (i n s t a n c e o f) ReferenceType 
		.addRule(Rule.withHead(Nonterminal.builder("RelationalExpression").build()).addSymbol(Nonterminal.builder("RelationalExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(110).build(), Character.builder(99).build(), Character.builder(101).build(), Character.builder(111).build(), Character.builder(102).build()).build()).build()).addSymbol(Nonterminal.builder("ReferenceType").build()).build())
		// RelationalExpression ::= RelationalExpression (>) ShiftExpression 
		.addRule(Rule.withHead(Nonterminal.builder("RelationalExpression").build()).addSymbol(Nonterminal.builder("RelationalExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(62).build()).build()).build()).addSymbol(Nonterminal.builder("ShiftExpression").build()).build())
		// RelationalExpression ::= RelationalExpression (<) ShiftExpression 
		.addRule(Rule.withHead(Nonterminal.builder("RelationalExpression").build()).addSymbol(Nonterminal.builder("RelationalExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(60).build()).build()).build()).addSymbol(Nonterminal.builder("ShiftExpression").build()).build())
		// LastFormalParameter ::= VariableModifier* Type (. . .) VariableDeclaratorId 
		.addRule(Rule.withHead(Nonterminal.builder("LastFormalParameter").build()).addSymbol(Star.builder(Nonterminal.builder("VariableModifier").build()).build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(46).build(), Character.builder(46).build(), Character.builder(46).build()).build()).build()).addSymbol(Nonterminal.builder("VariableDeclaratorId").build()).build())
		// LastFormalParameter ::= FormalParameter 
		.addRule(Rule.withHead(Nonterminal.builder("LastFormalParameter").build()).addSymbol(Nonterminal.builder("FormalParameter").build()).build())
		// FloatingPointLiteral ::= HexadecimalFloatingPointLiteral 
		.addRule(Rule.withHead(Nonterminal.builder("FloatingPointLiteral").build()).addSymbol(Nonterminal.builder("HexadecimalFloatingPointLiteral").build()).build())
		// FloatingPointLiteral ::= DecimalFloatingPointLiteral 
		.addRule(Rule.withHead(Nonterminal.builder("FloatingPointLiteral").build()).addSymbol(Nonterminal.builder("DecimalFloatingPointLiteral").build()).build())
		// DecimalIntegerLiteral ::= DecimalNumeral IntegerTypeSuffix? 
		.addRule(Rule.withHead(Nonterminal.builder("DecimalIntegerLiteral").build()).addSymbol(Nonterminal.builder("DecimalNumeral").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("IntegerTypeSuffix").build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// JavaLetter ::= ($ | A-Z | _ | a-z) 
		.addRule(Rule.withHead(Nonterminal.builder("JavaLetter").build()).addSymbol(Alt.builder(CharacterRange.builder(36, 36).build(), CharacterRange.builder(65, 90).build(), CharacterRange.builder(95, 95).build(), CharacterRange.builder(97, 122).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// BooleanLiteral ::= (f a l s e) 
		.addRule(Rule.withHead(Nonterminal.builder("BooleanLiteral").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(102).build(), Character.builder(97).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// BooleanLiteral ::= (t r u e) 
		.addRule(Rule.withHead(Nonterminal.builder("BooleanLiteral").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(114).build(), Character.builder(117).build(), Character.builder(101).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Identifier ::= IdentifierChars 
		.addRule(Rule.withHead(Nonterminal.builder("Identifier").build()).addSymbol(Nonterminal.builder("IdentifierChars").addPreConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_PRECEDE, Alt.builder(CharacterRange.builder(36, 36).build(), CharacterRange.builder(65, 90).build(), CharacterRange.builder(95, 95).build(), CharacterRange.builder(97, 122).build()).build()))).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Alt.builder(CharacterRange.builder(36, 36).build(), CharacterRange.builder(48, 57).build(), CharacterRange.builder(65, 90).build(), CharacterRange.builder(95, 95).build(), CharacterRange.builder(97, 122).build()).build()), new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(108).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(98).build(), Character.builder(114).build(), Character.builder(101).build(), Character.builder(97).build(), Character.builder(107).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(105).build(), Character.builder(115).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(102).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(99).build(), Character.builder(116).build(), Character.builder(102).build(), Character.builder(112).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(110).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(101).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(100).build(), Character.builder(111).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(114).build(), Character.builder(97).build(), Character.builder(110).build(), Character.builder(115).build(), Character.builder(105).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(116).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(116).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(119).build(), Character.builder(115).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(110).build(), Character.builder(99).build(), Character.builder(101).build(), Character.builder(111).build(), Character.builder(102).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(97).build(), Character.builder(98).build(), Character.builder(115).build(), Character.builder(116).build(), Character.builder(114).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(116).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(110).build(), Character.builder(101).build(), Character.builder(119).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(117).build(), Character.builder(112).build(), Character.builder(101).build(), Character.builder(114).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(99).build(), Character.builder(104).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(114).build(), Character.builder(121).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(101).build(), Character.builder(120).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(100).build(), Character.builder(115).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(102).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(101).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(101).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(102).build(), Character.builder(108).build(), Character.builder(111).build(), Character.builder(97).build(), Character.builder(116).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(108).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(103).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(118).build(), Character.builder(111).build(), Character.builder(108).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(108).build(), Character.builder(101).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(104).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(105).build(), Character.builder(108).build(), Character.builder(101).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(100).build(), Character.builder(111).build(), Character.builder(117).build(), Character.builder(98).build(), Character.builder(108).build(), Character.builder(101).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(101).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(108).build(), Character.builder(101).build(), Character.builder(109).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(116).build(), Character.builder(115).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(114).build(), Character.builder(101).build(), Character.builder(116).build(), Character.builder(117).build(), Character.builder(114).build(), Character.builder(110).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(104).build(), Character.builder(97).build(), Character.builder(114).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(102).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(97).build(), Character.builder(108).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(119).build(), Character.builder(105).build(), Character.builder(116).build(), Character.builder(99).build(), Character.builder(104).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(121).build(), Character.builder(110).build(), Character.builder(99).build(), Character.builder(104).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(105).build(), Character.builder(122).build(), Character.builder(101).build(), Character.builder(100).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(118).build(), Character.builder(111).build(), Character.builder(105).build(), Character.builder(100).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(117).build(), Character.builder(98).build(), Character.builder(108).build(), Character.builder(105).build(), Character.builder(99).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(102).build(), Character.builder(111).build(), Character.builder(114).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(98).build(), Character.builder(121).build(), Character.builder(116).build(), Character.builder(101).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(98).build(), Character.builder(111).build(), Character.builder(111).build(), Character.builder(108).build(), Character.builder(101).build(), Character.builder(97).build(), Character.builder(110).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(99).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(100).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(99).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(114).build(), Character.builder(117).build(), Character.builder(101).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(115).build(), Character.builder(116).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(103).build(), Character.builder(111).build(), Character.builder(116).build(), Character.builder(111).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(102).build(), Character.builder(97).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(102).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(97).build(), Character.builder(108).build(), Character.builder(108).build(), Character.builder(121).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(101).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(100).build(), Character.builder(101).build(), Character.builder(102).build(), Character.builder(97).build(), Character.builder(117).build(), Character.builder(108).build(), Character.builder(116).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(101).build(), Character.builder(110).build(), Character.builder(117).build(), Character.builder(109).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(110).build(), Character.builder(117).build(), Character.builder(108).build(), Character.builder(108).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(117).build(), Character.builder(101).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(116).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(119).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(107).build(), Character.builder(97).build(), Character.builder(103).build(), Character.builder(101).build()).build()).build()).build()))).build()).setLayoutStrategy(NO_LAYOUT).build())
		// MethodModifier ::= (p r i v a t e) 
		.addRule(Rule.withHead(Nonterminal.builder("MethodModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(101).build()).build()).build()).build())
		// MethodModifier ::= (s y n c h r o n i z e d) 
		.addRule(Rule.withHead(Nonterminal.builder("MethodModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(121).build(), Character.builder(110).build(), Character.builder(99).build(), Character.builder(104).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(105).build(), Character.builder(122).build(), Character.builder(101).build(), Character.builder(100).build()).build()).build()).build())
		// MethodModifier ::= (f i n a l) 
		.addRule(Rule.withHead(Nonterminal.builder("MethodModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(102).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(97).build(), Character.builder(108).build()).build()).build()).build())
		// MethodModifier ::= (s t r i c t f p) 
		.addRule(Rule.withHead(Nonterminal.builder("MethodModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(99).build(), Character.builder(116).build(), Character.builder(102).build(), Character.builder(112).build()).build()).build()).build())
		// MethodModifier ::= (a b s t r a c t) 
		.addRule(Rule.withHead(Nonterminal.builder("MethodModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(97).build(), Character.builder(98).build(), Character.builder(115).build(), Character.builder(116).build(), Character.builder(114).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(116).build()).build()).build()).build())
		// MethodModifier ::= (n a t i v e) 
		.addRule(Rule.withHead(Nonterminal.builder("MethodModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(110).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(101).build()).build()).build()).build())
		// MethodModifier ::= (p r o t e c t e d) 
		.addRule(Rule.withHead(Nonterminal.builder("MethodModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(99).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(100).build()).build()).build()).build())
		// MethodModifier ::= Annotation 
		.addRule(Rule.withHead(Nonterminal.builder("MethodModifier").build()).addSymbol(Nonterminal.builder("Annotation").build()).build())
		// MethodModifier ::= (p u b l i c) 
		.addRule(Rule.withHead(Nonterminal.builder("MethodModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(117).build(), Character.builder(98).build(), Character.builder(108).build(), Character.builder(105).build(), Character.builder(99).build()).build()).build()).build())
		// MethodModifier ::= (s t a t i c) 
		.addRule(Rule.withHead(Nonterminal.builder("MethodModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(99).build()).build()).build()).build())
		// VariableModifier ::= (f i n a l) 
		.addRule(Rule.withHead(Nonterminal.builder("VariableModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(102).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(97).build(), Character.builder(108).build()).build()).build()).build())
		// VariableModifier ::= Annotation 
		.addRule(Rule.withHead(Nonterminal.builder("VariableModifier").build()).addSymbol(Nonterminal.builder("Annotation").build()).build())
		// AndExpression ::= EqualityExpression 
		.addRule(Rule.withHead(Nonterminal.builder("AndExpression").build()).addSymbol(Nonterminal.builder("EqualityExpression").build()).build())
		// AndExpression ::= AndExpression (&) EqualityExpression 
		.addRule(Rule.withHead(Nonterminal.builder("AndExpression").build()).addSymbol(Nonterminal.builder("AndExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(38).build()).build()).build()).addSymbol(Nonterminal.builder("EqualityExpression").build()).build())
		// IntegerTypeSuffix ::= l 
		.addRule(Rule.withHead(Nonterminal.builder("IntegerTypeSuffix").build()).addSymbol(Character.builder(108).build()).setLayoutStrategy(NO_LAYOUT).build())
		// IntegerTypeSuffix ::= L 
		.addRule(Rule.withHead(Nonterminal.builder("IntegerTypeSuffix").build()).addSymbol(Character.builder(76).build()).setLayoutStrategy(NO_LAYOUT).build())
		// FieldModifier ::= (f i n a l) 
		.addRule(Rule.withHead(Nonterminal.builder("FieldModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(102).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(97).build(), Character.builder(108).build()).build()).build()).build())
		// FieldModifier ::= (t r a n s i e n t) 
		.addRule(Rule.withHead(Nonterminal.builder("FieldModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(114).build(), Character.builder(97).build(), Character.builder(110).build(), Character.builder(115).build(), Character.builder(105).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(116).build()).build()).build()).build())
		// FieldModifier ::= Annotation 
		.addRule(Rule.withHead(Nonterminal.builder("FieldModifier").build()).addSymbol(Nonterminal.builder("Annotation").build()).build())
		// FieldModifier ::= (p r o t e c t e d) 
		.addRule(Rule.withHead(Nonterminal.builder("FieldModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(99).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(100).build()).build()).build()).build())
		// FieldModifier ::= (v o l a t i l e) 
		.addRule(Rule.withHead(Nonterminal.builder("FieldModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(118).build(), Character.builder(111).build(), Character.builder(108).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(108).build(), Character.builder(101).build()).build()).build()).build())
		// FieldModifier ::= (p r i v a t e) 
		.addRule(Rule.withHead(Nonterminal.builder("FieldModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(101).build()).build()).build()).build())
		// FieldModifier ::= (s t a t i c) 
		.addRule(Rule.withHead(Nonterminal.builder("FieldModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(99).build()).build()).build()).build())
		// FieldModifier ::= (p u b l i c) 
		.addRule(Rule.withHead(Nonterminal.builder("FieldModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(117).build(), Character.builder(98).build(), Character.builder(108).build(), Character.builder(105).build(), Character.builder(99).build()).build()).build()).build())
		// IdentifierChars ::= JavaLetter 
		.addRule(Rule.withHead(Nonterminal.builder("IdentifierChars").build()).addSymbol(Nonterminal.builder("JavaLetter").build()).setLayoutStrategy(NO_LAYOUT).build())
		// IdentifierChars ::= IdentifierChars JavaLetterOrDigit 
		.addRule(Rule.withHead(Nonterminal.builder("IdentifierChars").build()).addSymbol(Nonterminal.builder("IdentifierChars").build()).addSymbol(Nonterminal.builder("JavaLetterOrDigit").build()).setLayoutStrategy(NO_LAYOUT).build())
		// ArrayType ::= Type ([) (]) 
		.addRule(Rule.withHead(Nonterminal.builder("ArrayType").build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(91).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(93).build()).build()).build()).build())
		// TypeList ::= Type+ 
		.addRule(Rule.withHead(Nonterminal.builder("TypeList").build()).addSymbol(Plus.builder(Nonterminal.builder("Type").build()).build()).build())
		// Arguments ::= (() ArgumentList? ()) 
		.addRule(Rule.withHead(Nonterminal.builder("Arguments").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ArgumentList").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// Resources ::= Resource+ 
		.addRule(Rule.withHead(Nonterminal.builder("Resources").build()).addSymbol(Plus.builder(Nonterminal.builder("Resource").build()).build()).build())
		// MethodInvocation ::= MethodName (() ArgumentList? ()) 
		.addRule(Rule.withHead(Nonterminal.builder("MethodInvocation").build()).addSymbol(Nonterminal.builder("MethodName").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ArgumentList").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// MethodInvocation ::= TypeName (.) NonWildTypeArguments Identifier (() ArgumentList? ()) 
		.addRule(Rule.withHead(Nonterminal.builder("MethodInvocation").build()).addSymbol(Nonterminal.builder("TypeName").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).addSymbol(Nonterminal.builder("NonWildTypeArguments").build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ArgumentList").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// MethodInvocation ::= ClassName (.) (s u p e r) (.) NonWildTypeArguments? Identifier (() ArgumentList? ()) 
		.addRule(Rule.withHead(Nonterminal.builder("MethodInvocation").build()).addSymbol(Nonterminal.builder("ClassName").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(117).build(), Character.builder(112).build(), Character.builder(101).build(), Character.builder(114).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("NonWildTypeArguments").build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ArgumentList").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// MethodInvocation ::= Primary (.) NonWildTypeArguments? Identifier (() ArgumentList? ()) 
		.addRule(Rule.withHead(Nonterminal.builder("MethodInvocation").build()).addSymbol(Nonterminal.builder("Primary").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("NonWildTypeArguments").build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ArgumentList").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// MethodInvocation ::= (s u p e r) (.) NonWildTypeArguments? Identifier (() ArgumentList? ()) 
		.addRule(Rule.withHead(Nonterminal.builder("MethodInvocation").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(117).build(), Character.builder(112).build(), Character.builder(101).build(), Character.builder(114).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("NonWildTypeArguments").build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ArgumentList").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// ElementValuePair ::= Identifier (=) ElementValue 
		.addRule(Rule.withHead(Nonterminal.builder("ElementValuePair").build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(61).build()).build()).build()).addSymbol(Nonterminal.builder("ElementValue").build()).build())
		// OctalDigitOrUnderscore ::= OctalDigit 
		.addRule(Rule.withHead(Nonterminal.builder("OctalDigitOrUnderscore").build()).addSymbol(Nonterminal.builder("OctalDigit").build()).setLayoutStrategy(NO_LAYOUT).build())
		// OctalDigitOrUnderscore ::= _ 
		.addRule(Rule.withHead(Nonterminal.builder("OctalDigitOrUnderscore").build()).addSymbol(Character.builder(95).build()).setLayoutStrategy(NO_LAYOUT).build())
		// RawInputCharacter ::= (\u0001-[ | ]-\u10FFFF) 
		.addRule(Rule.withHead(Nonterminal.builder("RawInputCharacter").build()).addSymbol(Alt.builder(CharacterRange.builder(1, 91).build(), CharacterRange.builder(93, 1114111).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// RawInputCharacter ::= \ 
		.addRule(Rule.withHead(Nonterminal.builder("RawInputCharacter").build()).addSymbol(Character.builder(92).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Character.builder(92).build()))).build()).setLayoutStrategy(NO_LAYOUT).build())
		// RawInputCharacter ::= \ \ 
		.addRule(Rule.withHead(Nonterminal.builder("RawInputCharacter").build()).addSymbol(Character.builder(92).build()).addSymbol(Character.builder(92).build()).setLayoutStrategy(NO_LAYOUT).build())
		// TypeDeclaration ::= InterfaceDeclaration 
		.addRule(Rule.withHead(Nonterminal.builder("TypeDeclaration").build()).addSymbol(Nonterminal.builder("InterfaceDeclaration").build()).build())
		// TypeDeclaration ::= (;) 
		.addRule(Rule.withHead(Nonterminal.builder("TypeDeclaration").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// TypeDeclaration ::= ClassDeclaration 
		.addRule(Rule.withHead(Nonterminal.builder("TypeDeclaration").build()).addSymbol(Nonterminal.builder("ClassDeclaration").build()).build())
		// LocalVariableDeclaration ::= VariableModifier* Type VariableDeclarator+ 
		.addRule(Rule.withHead(Nonterminal.builder("LocalVariableDeclaration").build()).addSymbol(Star.builder(Nonterminal.builder("VariableModifier").build()).build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Plus.builder(Nonterminal.builder("VariableDeclarator").build()).build()).build())
		// ReferenceType ::= TypeDeclSpecifier TypeArguments? 
		.addRule(Rule.withHead(Nonterminal.builder("ReferenceType").build()).addSymbol(Nonterminal.builder("TypeDeclSpecifier").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeArguments").build()).build()).build())
		// ReferenceType ::= ArrayType 
		.addRule(Rule.withHead(Nonterminal.builder("ReferenceType").build()).addSymbol(Nonterminal.builder("ArrayType").build()).build())
		// ConditionalExpression ::= ConditionalOrExpression (?) Expression (:) ConditionalExpression 
		.addRule(Rule.withHead(Nonterminal.builder("ConditionalExpression").build()).addSymbol(Nonterminal.builder("ConditionalOrExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(63).build()).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(58).build()).build()).build()).addSymbol(Nonterminal.builder("ConditionalExpression").build()).build())
		// ConditionalExpression ::= ConditionalOrExpression 
		.addRule(Rule.withHead(Nonterminal.builder("ConditionalExpression").build()).addSymbol(Nonterminal.builder("ConditionalOrExpression").build()).build())
		// ElementValueArrayInitializer ::= ({) ElementValues? (,)? (}) 
		.addRule(Rule.withHead(Nonterminal.builder("ElementValueArrayInitializer").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ElementValues").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).build())
		// SwitchLabel ::= (d e f a u l t) (:) 
		.addRule(Rule.withHead(Nonterminal.builder("SwitchLabel").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(100).build(), Character.builder(101).build(), Character.builder(102).build(), Character.builder(97).build(), Character.builder(117).build(), Character.builder(108).build(), Character.builder(116).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(58).build()).build()).build()).build())
		// SwitchLabel ::= (c a s e) ConstantExpression (:) 
		.addRule(Rule.withHead(Nonterminal.builder("SwitchLabel").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(101).build()).build()).build()).addSymbol(Nonterminal.builder("ConstantExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(58).build()).build()).build()).build())
		// ConstructorDeclarator ::= TypeParameters? Identifier (() FormalParameterList? ()) 
		.addRule(Rule.withHead(Nonterminal.builder("ConstructorDeclarator").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeParameters").build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("FormalParameterList").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// ExpressionName ::= QualifiedIdentifier 
		.addRule(Rule.withHead(Nonterminal.builder("ExpressionName").build()).addSymbol(Nonterminal.builder("QualifiedIdentifier").build()).build())
		// MethodName ::= QualifiedIdentifier 
		.addRule(Rule.withHead(Nonterminal.builder("MethodName").build()).addSymbol(Nonterminal.builder("QualifiedIdentifier").build()).build())
		// StringLiteral ::= " StringCharacter* " 
		.addRule(Rule.withHead(Nonterminal.builder("StringLiteral").build()).addSymbol(Character.builder(34).build()).addSymbol(Star.builder(Nonterminal.builder("StringCharacter").build()).build()).addSymbol(Character.builder(34).build()).setLayoutStrategy(NO_LAYOUT).build())
		// EnumBodyDeclarations ::= (;) ClassBodyDeclaration* 
		.addRule(Rule.withHead(Nonterminal.builder("EnumBodyDeclarations").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).addSymbol(Star.builder(Nonterminal.builder("ClassBodyDeclaration").build()).build()).build())
		// LineTerminator ::= \u000A 
		.addRule(Rule.withHead(Nonterminal.builder("LineTerminator").build()).addSymbol(Character.builder(10).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ClassModifier ::= Annotation 
		.addRule(Rule.withHead(Nonterminal.builder("ClassModifier").build()).addSymbol(Nonterminal.builder("Annotation").build()).build())
		// ClassModifier ::= (p r o t e c t e d) 
		.addRule(Rule.withHead(Nonterminal.builder("ClassModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(99).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(100).build()).build()).build()).build())
		// ClassModifier ::= (s t r i c t f p) 
		.addRule(Rule.withHead(Nonterminal.builder("ClassModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(99).build(), Character.builder(116).build(), Character.builder(102).build(), Character.builder(112).build()).build()).build()).build())
		// ClassModifier ::= (f i n a l) 
		.addRule(Rule.withHead(Nonterminal.builder("ClassModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(102).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(97).build(), Character.builder(108).build()).build()).build()).build())
		// ClassModifier ::= (p r i v a t e) 
		.addRule(Rule.withHead(Nonterminal.builder("ClassModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(101).build()).build()).build()).build())
		// ClassModifier ::= (a b s t r a c t) 
		.addRule(Rule.withHead(Nonterminal.builder("ClassModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(97).build(), Character.builder(98).build(), Character.builder(115).build(), Character.builder(116).build(), Character.builder(114).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(116).build()).build()).build()).build())
		// ClassModifier ::= (s t a t i c) 
		.addRule(Rule.withHead(Nonterminal.builder("ClassModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(99).build()).build()).build()).build())
		// ClassModifier ::= (p u b l i c) 
		.addRule(Rule.withHead(Nonterminal.builder("ClassModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(117).build(), Character.builder(98).build(), Character.builder(108).build(), Character.builder(105).build(), Character.builder(99).build()).build()).build()).build())
		// TypeVariable ::= Identifier 
		.addRule(Rule.withHead(Nonterminal.builder("TypeVariable").build()).addSymbol(Nonterminal.builder("Identifier").build()).build())
		// CharacterLiteral ::= ' EscapeSequence ' 
		.addRule(Rule.withHead(Nonterminal.builder("CharacterLiteral").build()).addSymbol(Character.builder(39).build()).addSymbol(Nonterminal.builder("EscapeSequence").build()).addSymbol(Character.builder(39).build()).setLayoutStrategy(NO_LAYOUT).build())
		// CharacterLiteral ::= ' SingleCharacter ' 
		.addRule(Rule.withHead(Nonterminal.builder("CharacterLiteral").build()).addSymbol(Character.builder(39).build()).addSymbol(Nonterminal.builder("SingleCharacter").build()).addSymbol(Character.builder(39).build()).setLayoutStrategy(NO_LAYOUT).build())
		// MethodDeclaration ::= MethodHeader MethodBody 
		.addRule(Rule.withHead(Nonterminal.builder("MethodDeclaration").build()).addSymbol(Nonterminal.builder("MethodHeader").build()).addSymbol(Nonterminal.builder("MethodBody").build()).build())
		// JavaLetterOrDigit ::= ($ | 0-9 | A-Z | _ | a-z) 
		.addRule(Rule.withHead(Nonterminal.builder("JavaLetterOrDigit").build()).addSymbol(Alt.builder(CharacterRange.builder(36, 36).build(), CharacterRange.builder(48, 57).build(), CharacterRange.builder(65, 90).build(), CharacterRange.builder(95, 95).build(), CharacterRange.builder(97, 122).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// UnicodeInputCharacter ::= RawInputCharacter 
		.addRule(Rule.withHead(Nonterminal.builder("UnicodeInputCharacter").build()).addSymbol(Nonterminal.builder("RawInputCharacter").build()).setLayoutStrategy(NO_LAYOUT).build())
		// UnicodeInputCharacter ::= UnicodeEscape 
		.addRule(Rule.withHead(Nonterminal.builder("UnicodeInputCharacter").build()).addSymbol(Nonterminal.builder("UnicodeEscape").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression 
		.addRule(Rule.withHead(Nonterminal.builder("Assignment").build()).addSymbol(Nonterminal.builder("LeftHandSide").build()).addSymbol(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Nonterminal.builder("AssignmentExpression").build()).build())
		// BinaryDigitOrUnderscore ::= BinaryDigit 
		.addRule(Rule.withHead(Nonterminal.builder("BinaryDigitOrUnderscore").build()).addSymbol(Nonterminal.builder("BinaryDigit").build()).setLayoutStrategy(NO_LAYOUT).build())
		// BinaryDigitOrUnderscore ::= _ 
		.addRule(Rule.withHead(Nonterminal.builder("BinaryDigitOrUnderscore").build()).addSymbol(Character.builder(95).build()).setLayoutStrategy(NO_LAYOUT).build())
		// PrimaryNoNewArray ::= Type (.) (c l a s s) 
		.addRule(Rule.withHead(Nonterminal.builder("PrimaryNoNewArray").build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(108).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build()).build()).build()).build())
		// PrimaryNoNewArray ::= ArrayAccess 
		.addRule(Rule.withHead(Nonterminal.builder("PrimaryNoNewArray").build()).addSymbol(Nonterminal.builder("ArrayAccess").build()).build())
		// PrimaryNoNewArray ::= MethodInvocation 
		.addRule(Rule.withHead(Nonterminal.builder("PrimaryNoNewArray").build()).addSymbol(Nonterminal.builder("MethodInvocation").build()).build())
		// PrimaryNoNewArray ::= ClassInstanceCreationExpression 
		.addRule(Rule.withHead(Nonterminal.builder("PrimaryNoNewArray").build()).addSymbol(Nonterminal.builder("ClassInstanceCreationExpression").build()).build())
		// PrimaryNoNewArray ::= ClassName (.) (t h i s) 
		.addRule(Rule.withHead(Nonterminal.builder("PrimaryNoNewArray").build()).addSymbol(Nonterminal.builder("ClassName").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(105).build(), Character.builder(115).build()).build()).build()).build())
		// PrimaryNoNewArray ::= (() Expression ()) 
		.addRule(Rule.withHead(Nonterminal.builder("PrimaryNoNewArray").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// PrimaryNoNewArray ::= FieldAccess 
		.addRule(Rule.withHead(Nonterminal.builder("PrimaryNoNewArray").build()).addSymbol(Nonterminal.builder("FieldAccess").build()).build())
		// PrimaryNoNewArray ::= (t h i s) 
		.addRule(Rule.withHead(Nonterminal.builder("PrimaryNoNewArray").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(105).build(), Character.builder(115).build()).build()).build()).build())
		// PrimaryNoNewArray ::= (v o i d) (.) (c l a s s) 
		.addRule(Rule.withHead(Nonterminal.builder("PrimaryNoNewArray").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(118).build(), Character.builder(111).build(), Character.builder(105).build(), Character.builder(100).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(108).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build()).build()).build()).build())
		// PrimaryNoNewArray ::= Literal 
		.addRule(Rule.withHead(Nonterminal.builder("PrimaryNoNewArray").build()).addSymbol(Nonterminal.builder("Literal").build()).build())
		// StatementNoShortIf ::= StatementWithoutTrailingSubstatement 
		.addRule(Rule.withHead(Nonterminal.builder("StatementNoShortIf").build()).addSymbol(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).build())
		// StatementNoShortIf ::= Identifier (:) StatementNoShortIf 
		.addRule(Rule.withHead(Nonterminal.builder("StatementNoShortIf").build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(58).build()).build()).build()).addSymbol(Nonterminal.builder("StatementNoShortIf").build()).build())
		// StatementNoShortIf ::= (f o r) (() ForInit? (;) Expression? (;) ForUpdate? ()) StatementNoShortIf 
		.addRule(Rule.withHead(Nonterminal.builder("StatementNoShortIf").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(102).build(), Character.builder(111).build(), Character.builder(114).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ForInit").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Expression").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ForUpdate").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).addSymbol(Nonterminal.builder("StatementNoShortIf").build()).build())
		// StatementNoShortIf ::= (w h i l e) (() Expression ()) StatementNoShortIf 
		.addRule(Rule.withHead(Nonterminal.builder("StatementNoShortIf").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(105).build(), Character.builder(108).build(), Character.builder(101).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).addSymbol(Nonterminal.builder("StatementNoShortIf").build()).build())
		// StatementNoShortIf ::= (i f) (() Expression ()) StatementNoShortIf (e l s e) StatementNoShortIf 
		.addRule(Rule.withHead(Nonterminal.builder("StatementNoShortIf").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(102).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).addSymbol(Nonterminal.builder("StatementNoShortIf").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(101).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build()).build()).addSymbol(Nonterminal.builder("StatementNoShortIf").build()).build())
		// DecimalNumeral ::= 0 
		.addRule(Rule.withHead(Nonterminal.builder("DecimalNumeral").build()).addSymbol(Character.builder(48).build()).setLayoutStrategy(NO_LAYOUT).build())
		// DecimalNumeral ::= NonZeroDigit _+ Digits 
		.addRule(Rule.withHead(Nonterminal.builder("DecimalNumeral").build()).addSymbol(Nonterminal.builder("NonZeroDigit").build()).addSymbol(Plus.builder(Character.builder(95).build()).build()).addSymbol(Nonterminal.builder("Digits").build()).setLayoutStrategy(NO_LAYOUT).build())
		// DecimalNumeral ::= NonZeroDigit Digits? 
		.addRule(Rule.withHead(Nonterminal.builder("DecimalNumeral").build()).addSymbol(Nonterminal.builder("NonZeroDigit").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Digits").build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// EnumDeclaration ::= ClassModifier* (e n u m) Identifier ((i m p l e m e n t s) TypeList)? EnumBody 
		.addRule(Rule.withHead(Nonterminal.builder("EnumDeclaration").build()).addSymbol(Star.builder(Nonterminal.builder("ClassModifier").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(101).build(), Character.builder(110).build(), Character.builder(117).build(), Character.builder(109).build()).build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(108).build(), Character.builder(101).build(), Character.builder(109).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(116).build(), Character.builder(115).build()).build()).build(), Nonterminal.builder("TypeList").build()).build()).build()).addSymbol(Nonterminal.builder("EnumBody").build()).build())
		// AssignmentOperator ::= (& =) 
		.addRule(Rule.withHead(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(38).build(), Character.builder(61).build()).build()).build()).build())
		// AssignmentOperator ::= (=) 
		.addRule(Rule.withHead(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(61).build()).build()).build()).build())
		// AssignmentOperator ::= (> > =) 
		.addRule(Rule.withHead(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(62).build(), Character.builder(62).build(), Character.builder(61).build()).build()).build()).build())
		// AssignmentOperator ::= (- =) 
		.addRule(Rule.withHead(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build(), Character.builder(61).build()).build()).build()).build())
		// AssignmentOperator ::= (/ =) 
		.addRule(Rule.withHead(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(47).build(), Character.builder(61).build()).build()).build()).build())
		// AssignmentOperator ::= (* =) 
		.addRule(Rule.withHead(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(42).build(), Character.builder(61).build()).build()).build()).build())
		// AssignmentOperator ::= (+ =) 
		.addRule(Rule.withHead(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(43).build(), Character.builder(61).build()).build()).build()).build())
		// AssignmentOperator ::= (^ =) 
		.addRule(Rule.withHead(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(94).build(), Character.builder(61).build()).build()).build()).build())
		// AssignmentOperator ::= (| =) 
		.addRule(Rule.withHead(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(124).build(), Character.builder(61).build()).build()).build()).build())
		// AssignmentOperator ::= (> > > =) 
		.addRule(Rule.withHead(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(62).build(), Character.builder(62).build(), Character.builder(62).build(), Character.builder(61).build()).build()).build()).build())
		// AssignmentOperator ::= (% =) 
		.addRule(Rule.withHead(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(37).build(), Character.builder(61).build()).build()).build()).build())
		// AssignmentOperator ::= (< < =) 
		.addRule(Rule.withHead(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(60).build(), Character.builder(60).build(), Character.builder(61).build()).build()).build()).build())
		// EnumConstant ::= Annotation* Identifier Arguments? ClassBody? 
		.addRule(Rule.withHead(Nonterminal.builder("EnumConstant").build()).addSymbol(Star.builder(Nonterminal.builder("Annotation").build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Arguments").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ClassBody").build()).build()).build())
		// PackageDeclaration ::= Annotation* (p a c k a g e) QualifiedIdentifier (;) 
		.addRule(Rule.withHead(Nonterminal.builder("PackageDeclaration").build()).addSymbol(Star.builder(Nonterminal.builder("Annotation").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(107).build(), Character.builder(97).build(), Character.builder(103).build(), Character.builder(101).build()).build()).build()).addSymbol(Nonterminal.builder("QualifiedIdentifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// ZeroToThree ::= (0-3) 
		.addRule(Rule.withHead(Nonterminal.builder("ZeroToThree").build()).addSymbol(Alt.builder(CharacterRange.builder(48, 51).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// FormalParameter ::= VariableModifier* Type VariableDeclaratorId 
		.addRule(Rule.withHead(Nonterminal.builder("FormalParameter").build()).addSymbol(Star.builder(Nonterminal.builder("VariableModifier").build()).build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Nonterminal.builder("VariableDeclaratorId").build()).build())
		// SignedInteger ::= Sign? Digits 
		.addRule(Rule.withHead(Nonterminal.builder("SignedInteger").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Sign").build()).build()).addSymbol(Nonterminal.builder("Digits").build()).setLayoutStrategy(NO_LAYOUT).build())
		// StringCharacter ::= EscapeSequence 
		.addRule(Rule.withHead(Nonterminal.builder("StringCharacter").build()).addSymbol(Nonterminal.builder("EscapeSequence").build()).setLayoutStrategy(NO_LAYOUT).build())
		// StringCharacter ::= InputCharacter 
		.addRule(Rule.withHead(Nonterminal.builder("StringCharacter").build()).addSymbol(Nonterminal.builder("InputCharacter").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Character.builder(34).build()).build()))).build()).setLayoutStrategy(NO_LAYOUT).build())
		// EqualityExpression ::= RelationalExpression 
		.addRule(Rule.withHead(Nonterminal.builder("EqualityExpression").build()).addSymbol(Nonterminal.builder("RelationalExpression").build()).build())
		// EqualityExpression ::= EqualityExpression (! =) RelationalExpression 
		.addRule(Rule.withHead(Nonterminal.builder("EqualityExpression").build()).addSymbol(Nonterminal.builder("EqualityExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(33).build(), Character.builder(61).build()).build()).build()).addSymbol(Nonterminal.builder("RelationalExpression").build()).build())
		// EqualityExpression ::= EqualityExpression (= =) RelationalExpression 
		.addRule(Rule.withHead(Nonterminal.builder("EqualityExpression").build()).addSymbol(Nonterminal.builder("EqualityExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(61).build(), Character.builder(61).build()).build()).build()).addSymbol(Nonterminal.builder("RelationalExpression").build()).build())
		// HexSignificand ::= 0 x HexDigits? . HexDigits 
		.addRule(Rule.withHead(Nonterminal.builder("HexSignificand").build()).addSymbol(Character.builder(48).build()).addSymbol(Character.builder(120).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("HexDigits").build()).build()).addSymbol(Character.builder(46).build()).addSymbol(Nonterminal.builder("HexDigits").build()).setLayoutStrategy(NO_LAYOUT).build())
		// HexSignificand ::= HexNumeral 
		.addRule(Rule.withHead(Nonterminal.builder("HexSignificand").build()).addSymbol(Nonterminal.builder("HexNumeral").build()).setLayoutStrategy(NO_LAYOUT).build())
		// HexSignificand ::= 0 X HexDigits? . HexDigits 
		.addRule(Rule.withHead(Nonterminal.builder("HexSignificand").build()).addSymbol(Character.builder(48).build()).addSymbol(Character.builder(88).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("HexDigits").build()).build()).addSymbol(Character.builder(46).build()).addSymbol(Nonterminal.builder("HexDigits").build()).setLayoutStrategy(NO_LAYOUT).build())
		// HexSignificand ::= HexNumeral . 
		.addRule(Rule.withHead(Nonterminal.builder("HexSignificand").build()).addSymbol(Nonterminal.builder("HexNumeral").build()).addSymbol(Character.builder(46).build()).setLayoutStrategy(NO_LAYOUT).build())
		// FloatTypeSuffix ::= D 
		.addRule(Rule.withHead(Nonterminal.builder("FloatTypeSuffix").build()).addSymbol(Character.builder(68).build()).setLayoutStrategy(NO_LAYOUT).build())
		// BinaryIntegerLiteral ::= BinaryNumeral IntegerTypeSuffix? 
		.addRule(Rule.withHead(Nonterminal.builder("BinaryIntegerLiteral").build()).addSymbol(Nonterminal.builder("BinaryNumeral").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("IntegerTypeSuffix").build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// HexDigits ::= HexDigit HexDigitOrUnderscore* HexDigit 
		.addRule(Rule.withHead(Nonterminal.builder("HexDigits").build()).addSymbol(Nonterminal.builder("HexDigit").build()).addSymbol(Star.builder(Nonterminal.builder("HexDigitOrUnderscore").build()).build()).addSymbol(Nonterminal.builder("HexDigit").build()).setLayoutStrategy(NO_LAYOUT).build())
		// HexDigits ::= HexDigit 
		.addRule(Rule.withHead(Nonterminal.builder("HexDigits").build()).addSymbol(Nonterminal.builder("HexDigit").build()).setLayoutStrategy(NO_LAYOUT).build())
		// EscapeSequence ::= \ u+ (0 0 5) C \ u+ (0 0 5) C 
		.addRule(Rule.withHead(Nonterminal.builder("EscapeSequence").build()).addSymbol(Character.builder(92).build()).addSymbol(Plus.builder(Character.builder(117).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(48).build(), Character.builder(48).build(), Character.builder(53).build()).build()).build()).addSymbol(Character.builder(67).build()).addSymbol(Character.builder(92).build()).addSymbol(Plus.builder(Character.builder(117).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(48).build(), Character.builder(48).build(), Character.builder(53).build()).build()).build()).addSymbol(Character.builder(67).build()).setLayoutStrategy(NO_LAYOUT).build())
		// EscapeSequence ::= OctalEscape 
		.addRule(Rule.withHead(Nonterminal.builder("EscapeSequence").build()).addSymbol(Nonterminal.builder("OctalEscape").build()).setLayoutStrategy(NO_LAYOUT).build())
		// EscapeSequence ::= Backslash n 
		.addRule(Rule.withHead(Nonterminal.builder("EscapeSequence").build()).addSymbol(Nonterminal.builder("Backslash").build()).addSymbol(Character.builder(110).build()).setLayoutStrategy(NO_LAYOUT).build())
		// EscapeSequence ::= Backslash t 
		.addRule(Rule.withHead(Nonterminal.builder("EscapeSequence").build()).addSymbol(Nonterminal.builder("Backslash").build()).addSymbol(Character.builder(116).build()).setLayoutStrategy(NO_LAYOUT).build())
		// EscapeSequence ::= Backslash f 
		.addRule(Rule.withHead(Nonterminal.builder("EscapeSequence").build()).addSymbol(Nonterminal.builder("Backslash").build()).addSymbol(Character.builder(102).build()).setLayoutStrategy(NO_LAYOUT).build())
		// EscapeSequence ::= Backslash ' 
		.addRule(Rule.withHead(Nonterminal.builder("EscapeSequence").build()).addSymbol(Nonterminal.builder("Backslash").build()).addSymbol(Character.builder(39).build()).setLayoutStrategy(NO_LAYOUT).build())
		// EscapeSequence ::= Backslash " 
		.addRule(Rule.withHead(Nonterminal.builder("EscapeSequence").build()).addSymbol(Nonterminal.builder("Backslash").build()).addSymbol(Character.builder(34).build()).setLayoutStrategy(NO_LAYOUT).build())
		// EscapeSequence ::= Backslash r 
		.addRule(Rule.withHead(Nonterminal.builder("EscapeSequence").build()).addSymbol(Nonterminal.builder("Backslash").build()).addSymbol(Character.builder(114).build()).setLayoutStrategy(NO_LAYOUT).build())
		// EscapeSequence ::= Backslash b 
		.addRule(Rule.withHead(Nonterminal.builder("EscapeSequence").build()).addSymbol(Nonterminal.builder("Backslash").build()).addSymbol(Character.builder(98).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Sign ::= + 
		.addRule(Rule.withHead(Nonterminal.builder("Sign").build()).addSymbol(Character.builder(43).build()).setLayoutStrategy(NO_LAYOUT).build())
		// InstanceInitializer ::= Block 
		.addRule(Rule.withHead(Nonterminal.builder("InstanceInitializer").build()).addSymbol(Nonterminal.builder("Block").build()).build())
		// MultiplicativeExpression ::= MultiplicativeExpression (%) UnaryExpression 
		.addRule(Rule.withHead(Nonterminal.builder("MultiplicativeExpression").build()).addSymbol(Nonterminal.builder("MultiplicativeExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(37).build()).build()).build()).addSymbol(Nonterminal.builder("UnaryExpression").build()).build())
		// MultiplicativeExpression ::= UnaryExpression 
		.addRule(Rule.withHead(Nonterminal.builder("MultiplicativeExpression").build()).addSymbol(Nonterminal.builder("UnaryExpression").build()).build())
		// MultiplicativeExpression ::= MultiplicativeExpression (*) UnaryExpression 
		.addRule(Rule.withHead(Nonterminal.builder("MultiplicativeExpression").build()).addSymbol(Nonterminal.builder("MultiplicativeExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(42).build()).build()).build()).addSymbol(Nonterminal.builder("UnaryExpression").build()).build())
		// MultiplicativeExpression ::= MultiplicativeExpression (/) UnaryExpression 
		.addRule(Rule.withHead(Nonterminal.builder("MultiplicativeExpression").build()).addSymbol(Nonterminal.builder("MultiplicativeExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(47).build()).build()).build()).addSymbol(Nonterminal.builder("UnaryExpression").build()).build())
		// ArrayCreationExpression ::= (n e w) (ReferenceTypeNonArrayType | PrimitiveType) (([) (]))+ ArrayInitializer 
		.addRule(Rule.withHead(Nonterminal.builder("ArrayCreationExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(110).build(), Character.builder(101).build(), Character.builder(119).build()).build()).build()).addSymbol(Alt.builder(Nonterminal.builder("ReferenceTypeNonArrayType").build(), Nonterminal.builder("PrimitiveType").build()).build()).addSymbol(Plus.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(91).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(93).build()).build()).build()).build()).build()).addSymbol(Nonterminal.builder("ArrayInitializer").build()).build())
		// ArrayCreationExpression ::= (n e w) (PrimitiveType | ReferenceType) DimExpr+ (([) (]))* 
		.addRule(Rule.withHead(Nonterminal.builder("ArrayCreationExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(110).build(), Character.builder(101).build(), Character.builder(119).build()).build()).build()).addSymbol(Alt.builder(Nonterminal.builder("PrimitiveType").build(), Nonterminal.builder("ReferenceType").build()).build()).addSymbol(Plus.builder(Nonterminal.builder("DimExpr").build()).build()).addSymbol(Star.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(91).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(93).build()).build()).build()).build()).build()).build())
		// VariableDeclaratorId ::= Identifier (([) (]))* 
		.addRule(Rule.withHead(Nonterminal.builder("VariableDeclaratorId").build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Star.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(91).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(93).build()).build()).build()).build()).build()).build())
		// MethodBody ::= Block 
		.addRule(Rule.withHead(Nonterminal.builder("MethodBody").build()).addSymbol(Nonterminal.builder("Block").build()).build())
		// MethodBody ::= (;) 
		.addRule(Rule.withHead(Nonterminal.builder("MethodBody").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// TypeArgument ::= (?) (((e x t e n d s) | (s u p e r)) Type)? 
		.addRule(Rule.withHead(Nonterminal.builder("TypeArgument").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(63).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Alt.builder(Terminal.builder(Sequence.builder(Character.builder(101).build(), Character.builder(120).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(100).build(), Character.builder(115).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(117).build(), Character.builder(112).build(), Character.builder(101).build(), Character.builder(114).build()).build()).build()).build(), Nonterminal.builder("Type").build()).build()).build()).build())
		// TypeArgument ::= Type 
		.addRule(Rule.withHead(Nonterminal.builder("TypeArgument").build()).addSymbol(Nonterminal.builder("Type").build()).build())
		// VariableDeclarator ::= VariableDeclaratorId ((=) VariableInitializer)? 
		.addRule(Rule.withHead(Nonterminal.builder("VariableDeclarator").build()).addSymbol(Nonterminal.builder("VariableDeclaratorId").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(61).build()).build()).build(), Nonterminal.builder("VariableInitializer").build()).build()).build()).build())
		// BinaryExponentIndicator ::= P 
		.addRule(Rule.withHead(Nonterminal.builder("BinaryExponentIndicator").build()).addSymbol(Character.builder(80).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Type ::= ReferenceType 
		.addRule(Rule.withHead(Nonterminal.builder("Type").build()).addSymbol(Nonterminal.builder("ReferenceType").build()).build())
		// Type ::= PrimitiveType 
		.addRule(Rule.withHead(Nonterminal.builder("Type").build()).addSymbol(Nonterminal.builder("PrimitiveType").build()).build())
		// ConditionalOrExpression ::= ConditionalOrExpression (| |) ConditionalAndExpression 
		.addRule(Rule.withHead(Nonterminal.builder("ConditionalOrExpression").build()).addSymbol(Nonterminal.builder("ConditionalOrExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(124).build(), Character.builder(124).build()).build()).build()).addSymbol(Nonterminal.builder("ConditionalAndExpression").build()).build())
		// ConditionalOrExpression ::= ConditionalAndExpression 
		.addRule(Rule.withHead(Nonterminal.builder("ConditionalOrExpression").build()).addSymbol(Nonterminal.builder("ConditionalAndExpression").build()).build())
		// CatchType ::= QualifiedIdentifier+ 
		.addRule(Rule.withHead(Nonterminal.builder("CatchType").build()).addSymbol(Plus.builder(Nonterminal.builder("QualifiedIdentifier").build()).build()).build())
		// HexNumeral ::= 0 X HexDigits 
		.addRule(Rule.withHead(Nonterminal.builder("HexNumeral").build()).addSymbol(Character.builder(48).build()).addSymbol(Character.builder(88).build()).addSymbol(Nonterminal.builder("HexDigits").build()).setLayoutStrategy(NO_LAYOUT).build())
		// HexNumeral ::= 0 x HexDigits 
		.addRule(Rule.withHead(Nonterminal.builder("HexNumeral").build()).addSymbol(Character.builder(48).build()).addSymbol(Character.builder(120).build()).addSymbol(Nonterminal.builder("HexDigits").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Layout ::= (WhiteSpace | Comment)* 
		.addRule(Rule.withHead(Nonterminal.builder("Layout").build()).addSymbol(Star.builder(Alt.builder(Nonterminal.builder("WhiteSpace").build(), Nonterminal.builder("Comment").build()).build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Alt.builder(CharacterRange.builder(9, 10).build(), CharacterRange.builder(12, 13).build(), CharacterRange.builder(32, 32).build()).build()), new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Terminal.builder(Sequence.builder(Character.builder(47).build(), Character.builder(42).build()).build()).build()), new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Terminal.builder(Sequence.builder(Character.builder(47).build(), Character.builder(47).build()).build()).build()))).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ArrayAccess ::= PrimaryNoNewArray ([) Expression (]) 
		.addRule(Rule.withHead(Nonterminal.builder("ArrayAccess").build()).addSymbol(Nonterminal.builder("PrimaryNoNewArray").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(91).build()).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(93).build()).build()).build()).build())
		// ArrayAccess ::= ExpressionName ([) Expression (]) 
		.addRule(Rule.withHead(Nonterminal.builder("ArrayAccess").build()).addSymbol(Nonterminal.builder("ExpressionName").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(91).build()).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(93).build()).build()).build()).build())
		// ExponentPart ::= ExponentIndicator SignedInteger 
		.addRule(Rule.withHead(Nonterminal.builder("ExponentPart").build()).addSymbol(Nonterminal.builder("ExponentIndicator").build()).addSymbol(Nonterminal.builder("SignedInteger").build()).setLayoutStrategy(NO_LAYOUT).build())
		// AdditiveExpression ::= MultiplicativeExpression 
		.addRule(Rule.withHead(Nonterminal.builder("AdditiveExpression").build()).addSymbol(Nonterminal.builder("MultiplicativeExpression").build()).build())
		// AdditiveExpression ::= AdditiveExpression (-) MultiplicativeExpression 
		.addRule(Rule.withHead(Nonterminal.builder("AdditiveExpression").build()).addSymbol(Nonterminal.builder("AdditiveExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build()).build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Terminal.builder(Sequence.builder(Character.builder(45).build()).build()).build()))).build()).addSymbol(Nonterminal.builder("MultiplicativeExpression").build()).build())
		// AdditiveExpression ::= AdditiveExpression (+) MultiplicativeExpression 
		.addRule(Rule.withHead(Nonterminal.builder("AdditiveExpression").build()).addSymbol(Nonterminal.builder("AdditiveExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(43).build()).build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Terminal.builder(Sequence.builder(Character.builder(43).build()).build()).build()))).build()).addSymbol(Nonterminal.builder("MultiplicativeExpression").build()).build())
		// AssignmentExpression ::= Assignment 
		.addRule(Rule.withHead(Nonterminal.builder("AssignmentExpression").build()).addSymbol(Nonterminal.builder("Assignment").build()).build())
		// AssignmentExpression ::= ConditionalExpression 
		.addRule(Rule.withHead(Nonterminal.builder("AssignmentExpression").build()).addSymbol(Nonterminal.builder("ConditionalExpression").build()).build())
		// FieldAccess ::= (s u p e r) (.) Identifier 
		.addRule(Rule.withHead(Nonterminal.builder("FieldAccess").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(117).build(), Character.builder(112).build(), Character.builder(101).build(), Character.builder(114).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).build())
		// FieldAccess ::= Primary (.) Identifier 
		.addRule(Rule.withHead(Nonterminal.builder("FieldAccess").build()).addSymbol(Nonterminal.builder("Primary").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).build())
		// FieldAccess ::= ClassName (.) (s u p e r) (.) Identifier 
		.addRule(Rule.withHead(Nonterminal.builder("FieldAccess").build()).addSymbol(Nonterminal.builder("ClassName").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(117).build(), Character.builder(112).build(), Character.builder(101).build(), Character.builder(114).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).build())
		// ElementValues ::= ElementValue+ 
		.addRule(Rule.withHead(Nonterminal.builder("ElementValues").build()).addSymbol(Plus.builder(Nonterminal.builder("ElementValue").build()).build()).build())
		// IntegerLiteral ::= BinaryIntegerLiteral 
		.addRule(Rule.withHead(Nonterminal.builder("IntegerLiteral").build()).addSymbol(Nonterminal.builder("BinaryIntegerLiteral").build()).build())
		// IntegerLiteral ::= DecimalIntegerLiteral 
		.addRule(Rule.withHead(Nonterminal.builder("IntegerLiteral").build()).addSymbol(Nonterminal.builder("DecimalIntegerLiteral").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Character.builder(46).build()))).build()).build())
		// IntegerLiteral ::= HexIntegerLiteral 
		.addRule(Rule.withHead(Nonterminal.builder("IntegerLiteral").build()).addSymbol(Nonterminal.builder("HexIntegerLiteral").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Character.builder(46).build()))).build()).build())
		// IntegerLiteral ::= OctalIntegerLiteral 
		.addRule(Rule.withHead(Nonterminal.builder("IntegerLiteral").build()).addSymbol(Nonterminal.builder("OctalIntegerLiteral").build()).build())
		// EnumBody ::= ({) EnumConstant* (,)? EnumBodyDeclarations? (}) 
		.addRule(Rule.withHead(Nonterminal.builder("EnumBody").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build()).addSymbol(Star.builder(Nonterminal.builder("EnumConstant").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("EnumBodyDeclarations").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).build())
		// LeftHandSide ::= (() LeftHandSide ()) 
		.addRule(Rule.withHead(Nonterminal.builder("LeftHandSide").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("LeftHandSide").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// LeftHandSide ::= FieldAccess 
		.addRule(Rule.withHead(Nonterminal.builder("LeftHandSide").build()).addSymbol(Nonterminal.builder("FieldAccess").build()).build())
		// LeftHandSide ::= ExpressionName 
		.addRule(Rule.withHead(Nonterminal.builder("LeftHandSide").build()).addSymbol(Nonterminal.builder("ExpressionName").build()).build())
		// LeftHandSide ::= ArrayAccess 
		.addRule(Rule.withHead(Nonterminal.builder("LeftHandSide").build()).addSymbol(Nonterminal.builder("ArrayAccess").build()).build())
		// ShiftExpression ::= ShiftExpression (< <) AdditiveExpression 
		.addRule(Rule.withHead(Nonterminal.builder("ShiftExpression").build()).addSymbol(Nonterminal.builder("ShiftExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(60).build(), Character.builder(60).build()).build()).build()).addSymbol(Nonterminal.builder("AdditiveExpression").build()).build())
		// ShiftExpression ::= ShiftExpression (> >) AdditiveExpression 
		.addRule(Rule.withHead(Nonterminal.builder("ShiftExpression").build()).addSymbol(Nonterminal.builder("ShiftExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(62).build(), Character.builder(62).build()).build()).build()).addSymbol(Nonterminal.builder("AdditiveExpression").build()).build())
		// ShiftExpression ::= AdditiveExpression 
		.addRule(Rule.withHead(Nonterminal.builder("ShiftExpression").build()).addSymbol(Nonterminal.builder("AdditiveExpression").build()).build())
		// ShiftExpression ::= ShiftExpression (> > >) AdditiveExpression 
		.addRule(Rule.withHead(Nonterminal.builder("ShiftExpression").build()).addSymbol(Nonterminal.builder("ShiftExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(62).build(), Character.builder(62).build(), Character.builder(62).build()).build()).build()).addSymbol(Nonterminal.builder("AdditiveExpression").build()).build())
		// Result ::= (v o i d) 
		.addRule(Rule.withHead(Nonterminal.builder("Result").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(118).build(), Character.builder(111).build(), Character.builder(105).build(), Character.builder(100).build()).build()).build()).build())
		// Result ::= Type 
		.addRule(Rule.withHead(Nonterminal.builder("Result").build()).addSymbol(Nonterminal.builder("Type").build()).build())
		// NormalClassDeclaration ::= ClassModifier* (c l a s s) Identifier TypeParameters? ((e x t e n d s) Type)? ((i m p l e m e n t s) TypeList)? ClassBody 
		.addRule(Rule.withHead(Nonterminal.builder("NormalClassDeclaration").build()).addSymbol(Star.builder(Nonterminal.builder("ClassModifier").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(108).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build()).build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeParameters").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(101).build(), Character.builder(120).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(100).build(), Character.builder(115).build()).build()).build(), Nonterminal.builder("Type").build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(108).build(), Character.builder(101).build(), Character.builder(109).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(116).build(), Character.builder(115).build()).build()).build(), Nonterminal.builder("TypeList").build()).build()).build()).addSymbol(Nonterminal.builder("ClassBody").build()).build())
		// NormalInterfaceDeclaration ::= InterfaceModifier* (i n t e r f a c e) Identifier TypeParameters? ((e x t e n d s) TypeList)? InterfaceBody 
		.addRule(Rule.withHead(Nonterminal.builder("NormalInterfaceDeclaration").build()).addSymbol(Star.builder(Nonterminal.builder("InterfaceModifier").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(102).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(101).build()).build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeParameters").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(101).build(), Character.builder(120).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(100).build(), Character.builder(115).build()).build()).build(), Nonterminal.builder("TypeList").build()).build()).build()).addSymbol(Nonterminal.builder("InterfaceBody").build()).build())
		// PreIncrementExpression ::= (+ +) UnaryExpression 
		.addRule(Rule.withHead(Nonterminal.builder("PreIncrementExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(43).build(), Character.builder(43).build()).build()).build()).addSymbol(Nonterminal.builder("UnaryExpression").build()).build())
		// TraditionalComment ::= (/ *) CommentTail 
		.addRule(Rule.withHead(Nonterminal.builder("TraditionalComment").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(47).build(), Character.builder(42).build()).build()).build()).addSymbol(Nonterminal.builder("CommentTail").build()).setLayoutStrategy(NO_LAYOUT).build())
		// BinaryDigits ::= BinaryDigit BinaryDigitOrUnderscore* BinaryDigit 
		.addRule(Rule.withHead(Nonterminal.builder("BinaryDigits").build()).addSymbol(Nonterminal.builder("BinaryDigit").build()).addSymbol(Star.builder(Nonterminal.builder("BinaryDigitOrUnderscore").build()).build()).addSymbol(Nonterminal.builder("BinaryDigit").build()).setLayoutStrategy(NO_LAYOUT).build())
		// BinaryDigits ::= BinaryDigit 
		.addRule(Rule.withHead(Nonterminal.builder("BinaryDigits").build()).addSymbol(Nonterminal.builder("BinaryDigit").build()).setLayoutStrategy(NO_LAYOUT).build())
		// AnnotationTypeBody ::= ({) AnnotationTypeElementDeclaration* (}) 
		.addRule(Rule.withHead(Nonterminal.builder("AnnotationTypeBody").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build()).addSymbol(Star.builder(Nonterminal.builder("AnnotationTypeElementDeclaration").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).build())
		// OctalEscape ::= \ ZeroToThree OctalDigit OctalDigit 
		.addRule(Rule.withHead(Nonterminal.builder("OctalEscape").build()).addSymbol(Character.builder(92).build()).addSymbol(Nonterminal.builder("ZeroToThree").build()).addSymbol(Nonterminal.builder("OctalDigit").build()).addSymbol(Nonterminal.builder("OctalDigit").build()).setLayoutStrategy(NO_LAYOUT).build())
		// OctalEscape ::= \ OctalDigit OctalDigit 
		.addRule(Rule.withHead(Nonterminal.builder("OctalEscape").build()).addSymbol(Character.builder(92).build()).addSymbol(Nonterminal.builder("OctalDigit").build()).addSymbol(Nonterminal.builder("OctalDigit").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Alt.builder(CharacterRange.builder(48, 55).build()).build()))).build()).setLayoutStrategy(NO_LAYOUT).build())
		// OctalEscape ::= \ OctalDigit 
		.addRule(Rule.withHead(Nonterminal.builder("OctalEscape").build()).addSymbol(Character.builder(92).build()).addSymbol(Nonterminal.builder("OctalDigit").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Alt.builder(CharacterRange.builder(48, 55).build()).build()))).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ConstructorModifier ::= (p r i v a t e) 
		.addRule(Rule.withHead(Nonterminal.builder("ConstructorModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(101).build()).build()).build()).build())
		// ConstructorModifier ::= (p u b l i c) 
		.addRule(Rule.withHead(Nonterminal.builder("ConstructorModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(117).build(), Character.builder(98).build(), Character.builder(108).build(), Character.builder(105).build(), Character.builder(99).build()).build()).build()).build())
		// ConstructorModifier ::= Annotation 
		.addRule(Rule.withHead(Nonterminal.builder("ConstructorModifier").build()).addSymbol(Nonterminal.builder("Annotation").build()).build())
		// ConstructorModifier ::= (p r o t e c t e d) 
		.addRule(Rule.withHead(Nonterminal.builder("ConstructorModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(99).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(100).build()).build()).build()).build())
		// OctalNumeral ::= 0 OctalDigits 
		.addRule(Rule.withHead(Nonterminal.builder("OctalNumeral").build()).addSymbol(Character.builder(48).build()).addSymbol(Nonterminal.builder("OctalDigits").build()).setLayoutStrategy(NO_LAYOUT).build())
		// OctalNumeral ::= 0 _+ OctalDigits 
		.addRule(Rule.withHead(Nonterminal.builder("OctalNumeral").build()).addSymbol(Character.builder(48).build()).addSymbol(Plus.builder(Character.builder(95).build()).build()).addSymbol(Nonterminal.builder("OctalDigits").build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReferenceTypeNonArrayType ::= TypeDeclSpecifier TypeArguments? 
		.addRule(Rule.withHead(Nonterminal.builder("ReferenceTypeNonArrayType").build()).addSymbol(Nonterminal.builder("TypeDeclSpecifier").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeArguments").build()).build()).build())
		// DecimalFloatingPointLiteral ::= . Digits ExponentPart? FloatTypeSuffix? 
		.addRule(Rule.withHead(Nonterminal.builder("DecimalFloatingPointLiteral").build()).addSymbol(Character.builder(46).build()).addSymbol(Nonterminal.builder("Digits").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ExponentPart").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("FloatTypeSuffix").build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// DecimalFloatingPointLiteral ::= Digits ExponentPart 
		.addRule(Rule.withHead(Nonterminal.builder("DecimalFloatingPointLiteral").build()).addSymbol(Nonterminal.builder("Digits").build()).addSymbol(Nonterminal.builder("ExponentPart").build()).setLayoutStrategy(NO_LAYOUT).build())
		// DecimalFloatingPointLiteral ::= Digits ExponentPart FloatTypeSuffix 
		.addRule(Rule.withHead(Nonterminal.builder("DecimalFloatingPointLiteral").build()).addSymbol(Nonterminal.builder("Digits").build()).addSymbol(Nonterminal.builder("ExponentPart").build()).addSymbol(Nonterminal.builder("FloatTypeSuffix").build()).setLayoutStrategy(NO_LAYOUT).build())
		// DecimalFloatingPointLiteral ::= Digits . Digits? ExponentPart? FloatTypeSuffix? 
		.addRule(Rule.withHead(Nonterminal.builder("DecimalFloatingPointLiteral").build()).addSymbol(Nonterminal.builder("Digits").build()).addSymbol(Character.builder(46).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Digits").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ExponentPart").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("FloatTypeSuffix").build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// DecimalFloatingPointLiteral ::= Digits FloatTypeSuffix 
		.addRule(Rule.withHead(Nonterminal.builder("DecimalFloatingPointLiteral").build()).addSymbol(Nonterminal.builder("Digits").build()).addSymbol(Nonterminal.builder("FloatTypeSuffix").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Digits ::= Digit 
		.addRule(Rule.withHead(Nonterminal.builder("Digits").build()).addSymbol(Nonterminal.builder("Digit").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Digits ::= Digit DigitOrUnderscore* Digit 
		.addRule(Rule.withHead(Nonterminal.builder("Digits").build()).addSymbol(Nonterminal.builder("Digit").build()).addSymbol(Star.builder(Nonterminal.builder("DigitOrUnderscore").build()).build()).addSymbol(Nonterminal.builder("Digit").build()).setLayoutStrategy(NO_LAYOUT).build())
		// DefaultValue ::= (d e f a u l t) ElementValue 
		.addRule(Rule.withHead(Nonterminal.builder("DefaultValue").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(100).build(), Character.builder(101).build(), Character.builder(102).build(), Character.builder(97).build(), Character.builder(117).build(), Character.builder(108).build(), Character.builder(116).build()).build()).build()).addSymbol(Nonterminal.builder("ElementValue").build()).build())
		// ForInit ::= StatementExpression+ 
		.addRule(Rule.withHead(Nonterminal.builder("ForInit").build()).addSymbol(Plus.builder(Nonterminal.builder("StatementExpression").build()).build()).build())
		// ForInit ::= LocalVariableDeclaration 
		.addRule(Rule.withHead(Nonterminal.builder("ForInit").build()).addSymbol(Nonterminal.builder("LocalVariableDeclaration").build()).build())
		// Block ::= ({) BlockStatement* (}) 
		.addRule(Rule.withHead(Nonterminal.builder("Block").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build()).addSymbol(Star.builder(Nonterminal.builder("BlockStatement").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).build())
		// DigitOrUnderscore ::= _ 
		.addRule(Rule.withHead(Nonterminal.builder("DigitOrUnderscore").build()).addSymbol(Character.builder(95).build()).setLayoutStrategy(NO_LAYOUT).build())
		// DigitOrUnderscore ::= Digit 
		.addRule(Rule.withHead(Nonterminal.builder("DigitOrUnderscore").build()).addSymbol(Nonterminal.builder("Digit").build()).setLayoutStrategy(NO_LAYOUT).build())
		// SwitchBlockStatementGroup ::= SwitchLabel+ BlockStatement+ 
		.addRule(Rule.withHead(Nonterminal.builder("SwitchBlockStatementGroup").build()).addSymbol(Plus.builder(Nonterminal.builder("SwitchLabel").build()).build()).addSymbol(Plus.builder(Nonterminal.builder("BlockStatement").build()).build()).build())
		// AnnotationTypeElementDeclaration ::= AnnotationTypeDeclaration 
		.addRule(Rule.withHead(Nonterminal.builder("AnnotationTypeElementDeclaration").build()).addSymbol(Nonterminal.builder("AnnotationTypeDeclaration").build()).build())
		// AnnotationTypeElementDeclaration ::= InterfaceDeclaration 
		.addRule(Rule.withHead(Nonterminal.builder("AnnotationTypeElementDeclaration").build()).addSymbol(Nonterminal.builder("InterfaceDeclaration").build()).build())
		// AnnotationTypeElementDeclaration ::= ConstantDeclaration 
		.addRule(Rule.withHead(Nonterminal.builder("AnnotationTypeElementDeclaration").build()).addSymbol(Nonterminal.builder("ConstantDeclaration").build()).build())
		// AnnotationTypeElementDeclaration ::= (;) 
		.addRule(Rule.withHead(Nonterminal.builder("AnnotationTypeElementDeclaration").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// AnnotationTypeElementDeclaration ::= AbstractMethodModifier* Type Identifier (() ()) (([) (]))* DefaultValue? (;) 
		.addRule(Rule.withHead(Nonterminal.builder("AnnotationTypeElementDeclaration").build()).addSymbol(Star.builder(Nonterminal.builder("AbstractMethodModifier").build()).build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).addSymbol(Star.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(91).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(93).build()).build()).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("DefaultValue").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// AnnotationTypeElementDeclaration ::= ClassDeclaration 
		.addRule(Rule.withHead(Nonterminal.builder("AnnotationTypeElementDeclaration").build()).addSymbol(Nonterminal.builder("ClassDeclaration").build()).build())
		// VariableDeclarators ::= VariableDeclarator+ 
		.addRule(Rule.withHead(Nonterminal.builder("VariableDeclarators").build()).addSymbol(Plus.builder(Nonterminal.builder("VariableDeclarator").build()).build()).build())
		// HexIntegerLiteral ::= HexNumeral IntegerTypeSuffix? 
		.addRule(Rule.withHead(Nonterminal.builder("HexIntegerLiteral").build()).addSymbol(Nonterminal.builder("HexNumeral").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("IntegerTypeSuffix").build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// InterfaceModifier ::= (a b s t r a c t) 
		.addRule(Rule.withHead(Nonterminal.builder("InterfaceModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(97).build(), Character.builder(98).build(), Character.builder(115).build(), Character.builder(116).build(), Character.builder(114).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(116).build()).build()).build()).build())
		// InterfaceModifier ::= (p r i v a t e) 
		.addRule(Rule.withHead(Nonterminal.builder("InterfaceModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(101).build()).build()).build()).build())
		// InterfaceModifier ::= (s t r i c t f p) 
		.addRule(Rule.withHead(Nonterminal.builder("InterfaceModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(99).build(), Character.builder(116).build(), Character.builder(102).build(), Character.builder(112).build()).build()).build()).build())
		// InterfaceModifier ::= (p r o t e c t e d) 
		.addRule(Rule.withHead(Nonterminal.builder("InterfaceModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(99).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(100).build()).build()).build()).build())
		// InterfaceModifier ::= Annotation 
		.addRule(Rule.withHead(Nonterminal.builder("InterfaceModifier").build()).addSymbol(Nonterminal.builder("Annotation").build()).build())
		// InterfaceModifier ::= (p u b l i c) 
		.addRule(Rule.withHead(Nonterminal.builder("InterfaceModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(112).build(), Character.builder(117).build(), Character.builder(98).build(), Character.builder(108).build(), Character.builder(105).build(), Character.builder(99).build()).build()).build()).build())
		// InterfaceModifier ::= (s t a t i c) 
		.addRule(Rule.withHead(Nonterminal.builder("InterfaceModifier").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(99).build()).build()).build()).build())
		// PostfixExpression ::= PostIncrementExpression 
		.addRule(Rule.withHead(Nonterminal.builder("PostfixExpression").build()).addSymbol(Nonterminal.builder("PostIncrementExpression").build()).build())
		// PostfixExpression ::= PostDecrementExpression 
		.addRule(Rule.withHead(Nonterminal.builder("PostfixExpression").build()).addSymbol(Nonterminal.builder("PostDecrementExpression").build()).build())
		// PostfixExpression ::= Primary 
		.addRule(Rule.withHead(Nonterminal.builder("PostfixExpression").build()).addSymbol(Nonterminal.builder("Primary").build()).build())
		// PostfixExpression ::= ExpressionName 
		.addRule(Rule.withHead(Nonterminal.builder("PostfixExpression").build()).addSymbol(Nonterminal.builder("ExpressionName").build()).build())
		// SingleCharacter ::= InputCharacter 
		.addRule(Rule.withHead(Nonterminal.builder("SingleCharacter").build()).addSymbol(Nonterminal.builder("InputCharacter").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Character.builder(39).build()).build()))).build()).setLayoutStrategy(NO_LAYOUT).build())
		// InterfaceMemberDeclaration ::= InterfaceDeclaration 
		.addRule(Rule.withHead(Nonterminal.builder("InterfaceMemberDeclaration").build()).addSymbol(Nonterminal.builder("InterfaceDeclaration").build()).build())
		// InterfaceMemberDeclaration ::= ConstantDeclaration 
		.addRule(Rule.withHead(Nonterminal.builder("InterfaceMemberDeclaration").build()).addSymbol(Nonterminal.builder("ConstantDeclaration").build()).build())
		// InterfaceMemberDeclaration ::= (;) 
		.addRule(Rule.withHead(Nonterminal.builder("InterfaceMemberDeclaration").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// InterfaceMemberDeclaration ::= AbstractMethodDeclaration 
		.addRule(Rule.withHead(Nonterminal.builder("InterfaceMemberDeclaration").build()).addSymbol(Nonterminal.builder("AbstractMethodDeclaration").build()).build())
		// InterfaceMemberDeclaration ::= ClassDeclaration 
		.addRule(Rule.withHead(Nonterminal.builder("InterfaceMemberDeclaration").build()).addSymbol(Nonterminal.builder("ClassDeclaration").build()).build())
		// InclusiveOrExpression ::= ExclusiveOrExpression 
		.addRule(Rule.withHead(Nonterminal.builder("InclusiveOrExpression").build()).addSymbol(Nonterminal.builder("ExclusiveOrExpression").build()).build())
		// InclusiveOrExpression ::= InclusiveOrExpression (|) ExclusiveOrExpression 
		.addRule(Rule.withHead(Nonterminal.builder("InclusiveOrExpression").build()).addSymbol(Nonterminal.builder("InclusiveOrExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(124).build()).build()).build()).addSymbol(Nonterminal.builder("ExclusiveOrExpression").build()).build())
		// NonWildTypeArguments ::= (<) ReferenceType+ (>) 
		.addRule(Rule.withHead(Nonterminal.builder("NonWildTypeArguments").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(60).build()).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("ReferenceType").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(62).build()).build()).build()).build())
		// PostIncrementExpression ::= PostfixExpression (+ +) 
		.addRule(Rule.withHead(Nonterminal.builder("PostIncrementExpression").build()).addSymbol(Nonterminal.builder("PostfixExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(43).build(), Character.builder(43).build()).build()).build()).build())
		// Literal ::= CharacterLiteral 
		.addRule(Rule.withHead(Nonterminal.builder("Literal").build()).addSymbol(Nonterminal.builder("CharacterLiteral").build()).build())
		// Literal ::= NullLiteral 
		.addRule(Rule.withHead(Nonterminal.builder("Literal").build()).addSymbol(Nonterminal.builder("NullLiteral").build()).build())
		// Literal ::= IntegerLiteral 
		.addRule(Rule.withHead(Nonterminal.builder("Literal").build()).addSymbol(Nonterminal.builder("IntegerLiteral").build()).build())
		// Literal ::= StringLiteral 
		.addRule(Rule.withHead(Nonterminal.builder("Literal").build()).addSymbol(Nonterminal.builder("StringLiteral").build()).build())
		// Literal ::= BooleanLiteral 
		.addRule(Rule.withHead(Nonterminal.builder("Literal").build()).addSymbol(Nonterminal.builder("BooleanLiteral").build()).build())
		// Literal ::= FloatingPointLiteral 
		.addRule(Rule.withHead(Nonterminal.builder("Literal").build()).addSymbol(Nonterminal.builder("FloatingPointLiteral").build()).build())
		// ClassMemberDeclaration ::= InterfaceDeclaration 
		.addRule(Rule.withHead(Nonterminal.builder("ClassMemberDeclaration").build()).addSymbol(Nonterminal.builder("InterfaceDeclaration").build()).build())
		// ClassMemberDeclaration ::= ClassDeclaration 
		.addRule(Rule.withHead(Nonterminal.builder("ClassMemberDeclaration").build()).addSymbol(Nonterminal.builder("ClassDeclaration").build()).build())
		// ClassMemberDeclaration ::= MethodDeclaration 
		.addRule(Rule.withHead(Nonterminal.builder("ClassMemberDeclaration").build()).addSymbol(Nonterminal.builder("MethodDeclaration").build()).build())
		// ClassMemberDeclaration ::= FieldDeclaration 
		.addRule(Rule.withHead(Nonterminal.builder("ClassMemberDeclaration").build()).addSymbol(Nonterminal.builder("FieldDeclaration").build()).build())
		// ClassMemberDeclaration ::= (;) 
		.addRule(Rule.withHead(Nonterminal.builder("ClassMemberDeclaration").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// PrimitiveType ::= (f l o a t) 
		.addRule(Rule.withHead(Nonterminal.builder("PrimitiveType").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(102).build(), Character.builder(108).build(), Character.builder(111).build(), Character.builder(97).build(), Character.builder(116).build()).build()).build()).build())
		// PrimitiveType ::= (l o n g) 
		.addRule(Rule.withHead(Nonterminal.builder("PrimitiveType").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(108).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(103).build()).build()).build()).build())
		// PrimitiveType ::= (d o u b l e) 
		.addRule(Rule.withHead(Nonterminal.builder("PrimitiveType").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(100).build(), Character.builder(111).build(), Character.builder(117).build(), Character.builder(98).build(), Character.builder(108).build(), Character.builder(101).build()).build()).build()).build())
		// PrimitiveType ::= (i n t) 
		.addRule(Rule.withHead(Nonterminal.builder("PrimitiveType").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(116).build()).build()).build()).build())
		// PrimitiveType ::= (c h a r) 
		.addRule(Rule.withHead(Nonterminal.builder("PrimitiveType").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(104).build(), Character.builder(97).build(), Character.builder(114).build()).build()).build()).build())
		// PrimitiveType ::= (b o o l e a n) 
		.addRule(Rule.withHead(Nonterminal.builder("PrimitiveType").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(98).build(), Character.builder(111).build(), Character.builder(111).build(), Character.builder(108).build(), Character.builder(101).build(), Character.builder(97).build(), Character.builder(110).build()).build()).build()).build())
		// PrimitiveType ::= (s h o r t) 
		.addRule(Rule.withHead(Nonterminal.builder("PrimitiveType").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(104).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build()).build()).build())
		// PrimitiveType ::= (b y t e) 
		.addRule(Rule.withHead(Nonterminal.builder("PrimitiveType").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(98).build(), Character.builder(121).build(), Character.builder(116).build(), Character.builder(101).build()).build()).build()).build())
		// VariableInitializer ::= Expression 
		.addRule(Rule.withHead(Nonterminal.builder("VariableInitializer").build()).addSymbol(Nonterminal.builder("Expression").build()).build())
		// VariableInitializer ::= ArrayInitializer 
		.addRule(Rule.withHead(Nonterminal.builder("VariableInitializer").build()).addSymbol(Nonterminal.builder("ArrayInitializer").build()).build())
		// NotStar ::= InputCharacter 
		.addRule(Rule.withHead(Nonterminal.builder("NotStar").build()).addSymbol(Nonterminal.builder("InputCharacter").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Character.builder(42).build()).build()))).build()).setLayoutStrategy(NO_LAYOUT).build())
		// NotStar ::= LineTerminator 
		.addRule(Rule.withHead(Nonterminal.builder("NotStar").build()).addSymbol(Nonterminal.builder("LineTerminator").build()).setLayoutStrategy(NO_LAYOUT).build())
		// ExplicitConstructorInvocation ::= NonWildTypeArguments? (t h i s) (() ArgumentList? ()) (;) 
		.addRule(Rule.withHead(Nonterminal.builder("ExplicitConstructorInvocation").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("NonWildTypeArguments").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(105).build(), Character.builder(115).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ArgumentList").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// ExplicitConstructorInvocation ::= Primary (.) NonWildTypeArguments? (s u p e r) (() ArgumentList? ()) (;) 
		.addRule(Rule.withHead(Nonterminal.builder("ExplicitConstructorInvocation").build()).addSymbol(Nonterminal.builder("Primary").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("NonWildTypeArguments").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(117).build(), Character.builder(112).build(), Character.builder(101).build(), Character.builder(114).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ArgumentList").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// ExplicitConstructorInvocation ::= NonWildTypeArguments? (s u p e r) (() ArgumentList? ()) (;) 
		.addRule(Rule.withHead(Nonterminal.builder("ExplicitConstructorInvocation").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("NonWildTypeArguments").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(117).build(), Character.builder(112).build(), Character.builder(101).build(), Character.builder(114).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ArgumentList").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// ExceptionType ::= TypeName 
		.addRule(Rule.withHead(Nonterminal.builder("ExceptionType").build()).addSymbol(Nonterminal.builder("TypeName").build()).build())
		// CastExpression ::= (() ReferenceType ()) UnaryExpressionNotPlusMinus 
		.addRule(Rule.withHead(Nonterminal.builder("CastExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("ReferenceType").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).addSymbol(Nonterminal.builder("UnaryExpressionNotPlusMinus").build()).build())
		// CastExpression ::= (() PrimitiveType ()) UnaryExpression 
		.addRule(Rule.withHead(Nonterminal.builder("CastExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("PrimitiveType").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).addSymbol(Nonterminal.builder("UnaryExpression").build()).build())
		// CompilationUnit ::= PackageDeclaration? ImportDeclaration* TypeDeclaration* 
		.addRule(Rule.withHead(Nonterminal.builder("CompilationUnit").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("PackageDeclaration").build()).build()).addSymbol(Star.builder(Nonterminal.builder("ImportDeclaration").build()).build()).addSymbol(Star.builder(Nonterminal.builder("TypeDeclaration").build()).build()).build())
		// CompilationUnit ::= PackageDeclaration? ImportDeclaration* TypeDeclaration* 
		.addRule(Rule.withHead(Nonterminal.builder("CompilationUnit").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("PackageDeclaration").build()).build()).addSymbol(Star.builder(Nonterminal.builder("ImportDeclaration").build()).build()).addSymbol(Star.builder(Nonterminal.builder("TypeDeclaration").build()).build()).build())
		// Finally ::= (f i n a l l y) Block 
		.addRule(Rule.withHead(Nonterminal.builder("Finally").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(102).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(97).build(), Character.builder(108).build(), Character.builder(108).build(), Character.builder(121).build()).build()).build()).addSymbol(Nonterminal.builder("Block").build()).build())
		// BinaryExponent ::= BinaryExponentIndicator SignedInteger 
		.addRule(Rule.withHead(Nonterminal.builder("BinaryExponent").build()).addSymbol(Nonterminal.builder("BinaryExponentIndicator").build()).addSymbol(Nonterminal.builder("SignedInteger").build()).setLayoutStrategy(NO_LAYOUT).build())
		// BinaryDigit ::= (0-1) 
		.addRule(Rule.withHead(Nonterminal.builder("BinaryDigit").build()).addSymbol(Alt.builder(CharacterRange.builder(48, 49).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// TypeDeclSpecifier ::= Identifier (TypeArguments? (.) Identifier)* 
		.addRule(Rule.withHead(Nonterminal.builder("TypeDeclSpecifier").build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Star.builder(Sequence.builder(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeArguments").build()).build(), Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build(), Nonterminal.builder("Identifier").build()).build()).build()).build())
		// Expression ::= AssignmentExpression 
		.addRule(Rule.withHead(Nonterminal.builder("Expression").build()).addSymbol(Nonterminal.builder("AssignmentExpression").build()).build())
		// ClassInstanceCreationExpression ::= (n e w) TypeArguments? TypeDeclSpecifier TypeArgumentsOrDiamond? (() ArgumentList? ()) ClassBody? 
		.addRule(Rule.withHead(Nonterminal.builder("ClassInstanceCreationExpression").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(110).build(), Character.builder(101).build(), Character.builder(119).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeArguments").build()).build()).addSymbol(Nonterminal.builder("TypeDeclSpecifier").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeArgumentsOrDiamond").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ArgumentList").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ClassBody").build()).build()).build())
		// ClassInstanceCreationExpression ::= (Primary | QualifiedIdentifier) (.) (n e w) TypeArguments? Identifier TypeArgumentsOrDiamond? (() ArgumentList? ()) ClassBody? 
		.addRule(Rule.withHead(Nonterminal.builder("ClassInstanceCreationExpression").build()).addSymbol(Alt.builder(Nonterminal.builder("Primary").build(), Nonterminal.builder("QualifiedIdentifier").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(110).build(), Character.builder(101).build(), Character.builder(119).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeArguments").build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeArgumentsOrDiamond").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ArgumentList").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ClassBody").build()).build()).build())
		// HexDigitOrUnderscore ::= _ 
		.addRule(Rule.withHead(Nonterminal.builder("HexDigitOrUnderscore").build()).addSymbol(Character.builder(95).build()).setLayoutStrategy(NO_LAYOUT).build())
		// HexDigitOrUnderscore ::= HexDigit 
		.addRule(Rule.withHead(Nonterminal.builder("HexDigitOrUnderscore").build()).addSymbol(Nonterminal.builder("HexDigit").build()).setLayoutStrategy(NO_LAYOUT).build())
		.build();
}
