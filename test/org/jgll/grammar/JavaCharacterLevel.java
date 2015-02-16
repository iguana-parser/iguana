package org.jgll.grammar;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.condition.ConditionType;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.regex.Alt;
import org.jgll.regex.Plus;
import org.jgll.regex.Sequence;
import org.jgll.regex.Star;

import com.google.common.collect.Sets;

public class JavaCharacterLevel {

	public static Grammar grammar = 
			Grammar.builder()
			//InputCharacter ::= UnicodeInputCharacter 
			.addRule(Rule.withHead(Nonterminal.builder("InputCharacter").build()).addSymbol(Nonterminal.builder("UnicodeInputCharacter").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Character.builder(10).build()).build()))).build()).build())
			//InputCharacter ::= \u0000 
			.addRule(Rule.withHead(Nonterminal.builder("InputCharacter").build()).addSymbol(Character.builder(0).build()).build())
			//TypeName ::= QualifiedIdentifier 
			.addRule(Rule.withHead(Nonterminal.builder("TypeName").build()).addSymbol(Nonterminal.builder("QualifiedIdentifier").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Primary ::= PrimaryNoNewArray 
			.addRule(Rule.withHead(Nonterminal.builder("Primary").build()).addSymbol(Nonterminal.builder("PrimaryNoNewArray").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Primary ::= ArrayCreationExpression 
			.addRule(Rule.withHead(Nonterminal.builder("Primary").build()).addSymbol(Nonterminal.builder("ArrayCreationExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//CommentTail ::= "*" CommentTailStar 
			.addRule(Rule.withHead(Nonterminal.builder("CommentTail").build()).addSymbol(Sequence.builder(Character.builder(42).build()).build()).addSymbol(Nonterminal.builder("CommentTailStar").build()).build())
			//CommentTail ::= NotStar CommentTail 
			.addRule(Rule.withHead(Nonterminal.builder("CommentTail").build()).addSymbol(Nonterminal.builder("NotStar").build()).addSymbol(Nonterminal.builder("CommentTail").build()).build())
			//StatementWithoutTrailingSubstatement ::= "try" ResourceSpecification Block CatchClause* Finally? 
			.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Sequence.builder(Character.builder(116).build(), Character.builder(114).build(), Character.builder(121).build()).build()).addSymbol(Nonterminal.builder("ResourceSpecification").build()).addSymbol(Nonterminal.builder("Block").build()).addSymbol(Star.builder(Nonterminal.builder("CatchClause").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Finally").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StatementWithoutTrailingSubstatement ::= "throw" Expression ";" 
			.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(119).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StatementWithoutTrailingSubstatement ::= "try" Block (CatchClause+ | (CatchClause* Finally)) 
			.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Sequence.builder(Character.builder(116).build(), Character.builder(114).build(), Character.builder(121).build()).build()).addSymbol(Nonterminal.builder("Block").build()).addSymbol(Alt.builder(Plus.builder(Nonterminal.builder("CatchClause").build()).build(), Sequence.builder(Star.builder(Nonterminal.builder("CatchClause").build()).build(), Nonterminal.builder("Finally").build()).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StatementWithoutTrailingSubstatement ::= "switch" "(" Expression ")" "{" SwitchBlockStatementGroup* SwitchLabel* "}" 
			.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(119).build(), Character.builder(105).build(), Character.builder(116).build(), Character.builder(99).build(), Character.builder(104).build()).build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).addSymbol(Star.builder(Nonterminal.builder("SwitchBlockStatementGroup").build()).build()).addSymbol(Star.builder(Nonterminal.builder("SwitchLabel").build()).build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StatementWithoutTrailingSubstatement ::= "return" Expression? ";" 
			.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Sequence.builder(Character.builder(114).build(), Character.builder(101).build(), Character.builder(116).build(), Character.builder(117).build(), Character.builder(114).build(), Character.builder(110).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Expression").build()).build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StatementWithoutTrailingSubstatement ::= ";" 
			.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StatementWithoutTrailingSubstatement ::= "do" Statement "while" "(" Expression ")" ";" 
			.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Sequence.builder(Character.builder(100).build(), Character.builder(111).build()).build()).addSymbol(Nonterminal.builder("Statement").build()).addSymbol(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(105).build(), Character.builder(108).build(), Character.builder(101).build()).build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StatementWithoutTrailingSubstatement ::= "break" Identifier? ";" 
			.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Sequence.builder(Character.builder(98).build(), Character.builder(114).build(), Character.builder(101).build(), Character.builder(97).build(), Character.builder(107).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Identifier").build()).build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StatementWithoutTrailingSubstatement ::= "synchronized" "(" Expression ")" Block 
			.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(121).build(), Character.builder(110).build(), Character.builder(99).build(), Character.builder(104).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(105).build(), Character.builder(122).build(), Character.builder(101).build(), Character.builder(100).build()).build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).addSymbol(Nonterminal.builder("Block").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StatementWithoutTrailingSubstatement ::= "assert" Expression (":" Expression)? ";" 
			.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Sequence.builder(Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(116).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(58).build()).build(), Nonterminal.builder("Expression").build()).build()).build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StatementWithoutTrailingSubstatement ::= StatementExpression ";" 
			.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Nonterminal.builder("StatementExpression").build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StatementWithoutTrailingSubstatement ::= "continue" Identifier? ";" 
			.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Sequence.builder(Character.builder(99).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(117).build(), Character.builder(101).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Identifier").build()).build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StatementWithoutTrailingSubstatement ::= Block 
			.addRule(Rule.withHead(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).addSymbol(Nonterminal.builder("Block").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ExplicitGenericInvocationSuffix ::= "super" SuperSuffix 
			.addRule(Rule.withHead(Nonterminal.builder("ExplicitGenericInvocationSuffix").build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(117).build(), Character.builder(112).build(), Character.builder(101).build(), Character.builder(114).build()).build()).addSymbol(Nonterminal.builder("SuperSuffix").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ExplicitGenericInvocationSuffix ::= Identifier Arguments 
			.addRule(Rule.withHead(Nonterminal.builder("ExplicitGenericInvocationSuffix").build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Nonterminal.builder("Arguments").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PreDecrementExpression ::= "--" UnaryExpression 
			.addRule(Rule.withHead(Nonterminal.builder("PreDecrementExpression").build()).addSymbol(Sequence.builder(Character.builder(45).build(), Character.builder(45).build()).build()).addSymbol(Nonterminal.builder("UnaryExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//HexDigit ::= (0-9 | A-F | a-f) 
			.addRule(Rule.withHead(Nonterminal.builder("HexDigit").build()).addSymbol(Alt.builder(CharacterRange.builder(48, 57).build(), CharacterRange.builder(65, 70).build(), CharacterRange.builder(97, 102).build()).build()).build())
			//InterfaceBody ::= "{" InterfaceMemberDeclaration* "}" 
			.addRule(Rule.withHead(Nonterminal.builder("InterfaceBody").build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).addSymbol(Star.builder(Nonterminal.builder("InterfaceMemberDeclaration").build()).build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ClassBody ::= "{" ClassBodyDeclaration* "}" 
			.addRule(Rule.withHead(Nonterminal.builder("ClassBody").build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).addSymbol(Star.builder(Nonterminal.builder("ClassBodyDeclaration").build()).build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//MethodDeclarator ::= MethodDeclarator "[" "]" 
			.addRule(Rule.withHead(Nonterminal.builder("MethodDeclarator").build()).addSymbol(Nonterminal.builder("MethodDeclarator").build()).addSymbol(Sequence.builder(Character.builder(91).build()).build()).addSymbol(Sequence.builder(Character.builder(93).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//MethodDeclarator ::= Identifier "(" FormalParameterList? ")" 
			.addRule(Rule.withHead(Nonterminal.builder("MethodDeclarator").build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("FormalParameterList").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ConditionalAndExpression ::= ConditionalAndExpression "&&" InclusiveOrExpression 
			.addRule(Rule.withHead(Nonterminal.builder("ConditionalAndExpression").build()).addSymbol(Nonterminal.builder("ConditionalAndExpression").build()).addSymbol(Sequence.builder(Character.builder(38).build(), Character.builder(38).build()).build()).addSymbol(Nonterminal.builder("InclusiveOrExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ConditionalAndExpression ::= InclusiveOrExpression 
			.addRule(Rule.withHead(Nonterminal.builder("ConditionalAndExpression").build()).addSymbol(Nonterminal.builder("InclusiveOrExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "double" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(100).build(), Character.builder(111).build(), Character.builder(117).build(), Character.builder(98).build(), Character.builder(108).build(), Character.builder(101).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "int" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(116).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "catch" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(99).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(99).build(), Character.builder(104).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "throw" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(119).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "strictfp" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(99).build(), Character.builder(116).build(), Character.builder(102).build(), Character.builder(112).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "continue" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(99).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(117).build(), Character.builder(101).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "for" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(102).build(), Character.builder(111).build(), Character.builder(114).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "break" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(98).build(), Character.builder(114).build(), Character.builder(101).build(), Character.builder(97).build(), Character.builder(107).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "native" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(110).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(101).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "package" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(112).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(107).build(), Character.builder(97).build(), Character.builder(103).build(), Character.builder(101).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "short" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(104).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "import" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "protected" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(99).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(100).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "implements" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(108).build(), Character.builder(101).build(), Character.builder(109).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(116).build(), Character.builder(115).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "case" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(99).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(101).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "while" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(105).build(), Character.builder(108).build(), Character.builder(101).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "switch" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(119).build(), Character.builder(105).build(), Character.builder(116).build(), Character.builder(99).build(), Character.builder(104).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "assert" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(116).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "char" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(99).build(), Character.builder(104).build(), Character.builder(97).build(), Character.builder(114).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "super" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(117).build(), Character.builder(112).build(), Character.builder(101).build(), Character.builder(114).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "const" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(99).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(115).build(), Character.builder(116).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "this" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(105).build(), Character.builder(115).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "transient" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(116).build(), Character.builder(114).build(), Character.builder(97).build(), Character.builder(110).build(), Character.builder(115).build(), Character.builder(105).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(116).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "default" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(100).build(), Character.builder(101).build(), Character.builder(102).build(), Character.builder(97).build(), Character.builder(117).build(), Character.builder(108).build(), Character.builder(116).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "throws" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(119).build(), Character.builder(115).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "float" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(102).build(), Character.builder(108).build(), Character.builder(111).build(), Character.builder(97).build(), Character.builder(116).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "long" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(108).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(103).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "static" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(99).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "public" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(112).build(), Character.builder(117).build(), Character.builder(98).build(), Character.builder(108).build(), Character.builder(105).build(), Character.builder(99).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "volatile" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(118).build(), Character.builder(111).build(), Character.builder(108).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(108).build(), Character.builder(101).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "void" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(118).build(), Character.builder(111).build(), Character.builder(105).build(), Character.builder(100).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "finally" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(102).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(97).build(), Character.builder(108).build(), Character.builder(108).build(), Character.builder(121).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "private" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(101).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "try" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(116).build(), Character.builder(114).build(), Character.builder(121).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "new" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(110).build(), Character.builder(101).build(), Character.builder(119).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "byte" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(98).build(), Character.builder(121).build(), Character.builder(116).build(), Character.builder(101).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "enum" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(101).build(), Character.builder(110).build(), Character.builder(117).build(), Character.builder(109).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "synchronized" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(121).build(), Character.builder(110).build(), Character.builder(99).build(), Character.builder(104).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(105).build(), Character.builder(122).build(), Character.builder(101).build(), Character.builder(100).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "if" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(102).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "interface" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(102).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(101).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "instanceof" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(110).build(), Character.builder(99).build(), Character.builder(101).build(), Character.builder(111).build(), Character.builder(102).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "return" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(114).build(), Character.builder(101).build(), Character.builder(116).build(), Character.builder(117).build(), Character.builder(114).build(), Character.builder(110).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "else" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(101).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "final" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(102).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(97).build(), Character.builder(108).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "class" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(99).build(), Character.builder(108).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "goto" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(103).build(), Character.builder(111).build(), Character.builder(116).build(), Character.builder(111).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "extends" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(101).build(), Character.builder(120).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(100).build(), Character.builder(115).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "do" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(100).build(), Character.builder(111).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "abstract" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(97).build(), Character.builder(98).build(), Character.builder(115).build(), Character.builder(116).build(), Character.builder(114).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(116).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Keyword ::= "boolean" 
			.addRule(Rule.withHead(Nonterminal.builder("Keyword").build()).addSymbol(Sequence.builder(Character.builder(98).build(), Character.builder(111).build(), Character.builder(111).build(), Character.builder(108).build(), Character.builder(101).build(), Character.builder(97).build(), Character.builder(110).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//OctalIntegerLiteral ::= OctalNumeral IntegerTypeSuffix? 
			.addRule(Rule.withHead(Nonterminal.builder("OctalIntegerLiteral").build()).addSymbol(Nonterminal.builder("OctalNumeral").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("IntegerTypeSuffix").build()).build()).build())
			//TypeParameters ::= "<" TypeParameter+ ">" 
			.addRule(Rule.withHead(Nonterminal.builder("TypeParameters").build()).addSymbol(Sequence.builder(Character.builder(60).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("TypeParameter").build()).build()).addSymbol(Sequence.builder(Character.builder(62).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//TypeArgumentsOrDiamond ::= "<" ">" 
			.addRule(Rule.withHead(Nonterminal.builder("TypeArgumentsOrDiamond").build()).addSymbol(Sequence.builder(Character.builder(60).build()).build()).addSymbol(Sequence.builder(Character.builder(62).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//TypeArgumentsOrDiamond ::= TypeArguments 
			.addRule(Rule.withHead(Nonterminal.builder("TypeArgumentsOrDiamond").build()).addSymbol(Nonterminal.builder("TypeArguments").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Backslash ::= \ 
			.addRule(Rule.withHead(Nonterminal.builder("Backslash").build()).addSymbol(Character.builder(92).build()).build())
			//Backslash ::= \ u+ "005" C 
			.addRule(Rule.withHead(Nonterminal.builder("Backslash").build()).addSymbol(Character.builder(92).build()).addSymbol(Plus.builder(Character.builder(117).build()).build()).addSymbol(Sequence.builder(Character.builder(48).build(), Character.builder(48).build(), Character.builder(53).build()).build()).addSymbol(Character.builder(67).build()).build())
			//ArgumentList ::= Expression+ 
			.addRule(Rule.withHead(Nonterminal.builder("ArgumentList").build()).addSymbol(Plus.builder(Nonterminal.builder("Expression").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//BinaryNumeral ::= 0 b BinaryDigits 
			.addRule(Rule.withHead(Nonterminal.builder("BinaryNumeral").build()).addSymbol(Character.builder(48).build()).addSymbol(Character.builder(98).build()).addSymbol(Nonterminal.builder("BinaryDigits").build()).build())
			//BinaryNumeral ::= 0 B BinaryDigits 
			.addRule(Rule.withHead(Nonterminal.builder("BinaryNumeral").build()).addSymbol(Character.builder(48).build()).addSymbol(Character.builder(66).build()).addSymbol(Nonterminal.builder("BinaryDigits").build()).build())
			//OctalDigit ::= (0-7) 
			.addRule(Rule.withHead(Nonterminal.builder("OctalDigit").build()).addSymbol(Alt.builder(CharacterRange.builder(48, 55).build()).build()).build())
			//$default$ ::= 
			.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).build())
			//UnaryExpressionNotPlusMinus ::= CastExpression 
			.addRule(Rule.withHead(Nonterminal.builder("UnaryExpressionNotPlusMinus").build()).addSymbol(Nonterminal.builder("CastExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//UnaryExpressionNotPlusMinus ::= PostfixExpression 
			.addRule(Rule.withHead(Nonterminal.builder("UnaryExpressionNotPlusMinus").build()).addSymbol(Nonterminal.builder("PostfixExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//UnaryExpressionNotPlusMinus ::= "~" UnaryExpression 
			.addRule(Rule.withHead(Nonterminal.builder("UnaryExpressionNotPlusMinus").build()).addSymbol(Sequence.builder(Character.builder(126).build()).build()).addSymbol(Nonterminal.builder("UnaryExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//UnaryExpressionNotPlusMinus ::= "!" UnaryExpression 
			.addRule(Rule.withHead(Nonterminal.builder("UnaryExpressionNotPlusMinus").build()).addSymbol(Sequence.builder(Character.builder(33).build()).build()).addSymbol(Nonterminal.builder("UnaryExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PostDecrementExpression ::= PostfixExpression "--" 
			.addRule(Rule.withHead(Nonterminal.builder("PostDecrementExpression").build()).addSymbol(Nonterminal.builder("PostfixExpression").build()).addSymbol(Sequence.builder(Character.builder(45).build(), Character.builder(45).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ClassDeclaration ::= NormalClassDeclaration 
			.addRule(Rule.withHead(Nonterminal.builder("ClassDeclaration").build()).addSymbol(Nonterminal.builder("NormalClassDeclaration").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ClassDeclaration ::= EnumDeclaration 
			.addRule(Rule.withHead(Nonterminal.builder("ClassDeclaration").build()).addSymbol(Nonterminal.builder("EnumDeclaration").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ResourceSpecification ::= "(" Resources (;)? ")" 
			.addRule(Rule.withHead(Nonterminal.builder("ResourceSpecification").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("Resources").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Character.builder(59).build()).build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StaticInitializer ::= "static" Block 
			.addRule(Rule.withHead(Nonterminal.builder("StaticInitializer").build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(99).build()).build()).addSymbol(Nonterminal.builder("Block").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ImportDeclaration ::= "import" (s t a t i c)? Identifier+ ("." "*")? ";" 
			.addRule(Rule.withHead(Nonterminal.builder("ImportDeclaration").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(99).build()).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("Identifier").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(46).build()).build(), Sequence.builder(Character.builder(42).build()).build()).build()).build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//SuperSuffix ::= "." Identifier Arguments? 
			.addRule(Rule.withHead(Nonterminal.builder("SuperSuffix").build()).addSymbol(Sequence.builder(Character.builder(46).build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Arguments").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//SuperSuffix ::= Arguments 
			.addRule(Rule.withHead(Nonterminal.builder("SuperSuffix").build()).addSymbol(Nonterminal.builder("Arguments").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//UnaryExpression ::= PreIncrementExpression 
			.addRule(Rule.withHead(Nonterminal.builder("UnaryExpression").build()).addSymbol(Nonterminal.builder("PreIncrementExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//UnaryExpression ::= "-" UnaryExpression 
			.addRule(Rule.withHead(Nonterminal.builder("UnaryExpression").build()).addSymbol(Sequence.builder(Character.builder(45).build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Sequence.builder(Character.builder(45).build()).build()))).build()).addSymbol(Nonterminal.builder("UnaryExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//UnaryExpression ::= "+" UnaryExpression 
			.addRule(Rule.withHead(Nonterminal.builder("UnaryExpression").build()).addSymbol(Sequence.builder(Character.builder(43).build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Sequence.builder(Character.builder(43).build()).build()))).build()).addSymbol(Nonterminal.builder("UnaryExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//UnaryExpression ::= PreDecrementExpression 
			.addRule(Rule.withHead(Nonterminal.builder("UnaryExpression").build()).addSymbol(Nonterminal.builder("PreDecrementExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//UnaryExpression ::= UnaryExpressionNotPlusMinus 
			.addRule(Rule.withHead(Nonterminal.builder("UnaryExpression").build()).addSymbol(Nonterminal.builder("UnaryExpressionNotPlusMinus").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ElementValue ::= ElementValueArrayInitializer 
			.addRule(Rule.withHead(Nonterminal.builder("ElementValue").build()).addSymbol(Nonterminal.builder("ElementValueArrayInitializer").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ElementValue ::= ConditionalExpression 
			.addRule(Rule.withHead(Nonterminal.builder("ElementValue").build()).addSymbol(Nonterminal.builder("ConditionalExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ElementValue ::= Annotation 
			.addRule(Rule.withHead(Nonterminal.builder("ElementValue").build()).addSymbol(Nonterminal.builder("Annotation").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Resource ::= VariableModifier* ReferenceType VariableDeclaratorId "=" Expression 
			.addRule(Rule.withHead(Nonterminal.builder("Resource").build()).addSymbol(Star.builder(Nonterminal.builder("VariableModifier").build()).build()).addSymbol(Nonterminal.builder("ReferenceType").build()).addSymbol(Nonterminal.builder("VariableDeclaratorId").build()).addSymbol(Sequence.builder(Character.builder(61).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//CatchClause ::= "catch" "(" VariableModifier* CatchType Identifier ")" Block 
			.addRule(Rule.withHead(Nonterminal.builder("CatchClause").build()).addSymbol(Sequence.builder(Character.builder(99).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(99).build(), Character.builder(104).build()).build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Star.builder(Nonterminal.builder("VariableModifier").build()).build()).addSymbol(Nonterminal.builder("CatchType").build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).addSymbol(Nonterminal.builder("Block").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//QualifiedIdentifierList ::= QualifiedIdentifier+ 
			.addRule(Rule.withHead(Nonterminal.builder("QualifiedIdentifierList").build()).addSymbol(Plus.builder(Nonterminal.builder("QualifiedIdentifier").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Statement ::= StatementWithoutTrailingSubstatement 
			.addRule(Rule.withHead(Nonterminal.builder("Statement").build()).addSymbol(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Statement ::= ForStatement 
			.addRule(Rule.withHead(Nonterminal.builder("Statement").build()).addSymbol(Nonterminal.builder("ForStatement").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Statement ::= Identifier ":" Statement 
			.addRule(Rule.withHead(Nonterminal.builder("Statement").build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Sequence.builder(Character.builder(58).build()).build()).addSymbol(Nonterminal.builder("Statement").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Statement ::= "while" "(" Expression ")" Statement 
			.addRule(Rule.withHead(Nonterminal.builder("Statement").build()).addSymbol(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(105).build(), Character.builder(108).build(), Character.builder(101).build()).build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).addSymbol(Nonterminal.builder("Statement").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Statement ::= "if" "(" Expression ")" Statement 
			.addRule(Rule.withHead(Nonterminal.builder("Statement").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(102).build()).build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).addSymbol(Nonterminal.builder("Statement").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Statement ::= "if" "(" Expression ")" StatementNoShortIf "else" Statement 
			.addRule(Rule.withHead(Nonterminal.builder("Statement").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(102).build()).build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).addSymbol(Nonterminal.builder("StatementNoShortIf").build()).addSymbol(Sequence.builder(Character.builder(101).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build()).addSymbol(Nonterminal.builder("Statement").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//QualifiedIdentifier ::= Identifier+ 
			.addRule(Rule.withHead(Nonterminal.builder("QualifiedIdentifier").build()).addSymbol(Plus.builder(Nonterminal.builder("Identifier").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Digit ::= 0 
			.addRule(Rule.withHead(Nonterminal.builder("Digit").build()).addSymbol(Character.builder(48).build()).build())
			//Digit ::= NonZeroDigit 
			.addRule(Rule.withHead(Nonterminal.builder("Digit").build()).addSymbol(Nonterminal.builder("NonZeroDigit").build()).build())
			//UnicodeEscape ::= \ u+ HexDigit HexDigit HexDigit HexDigit 
			.addRule(Rule.withHead(Nonterminal.builder("UnicodeEscape").build()).addSymbol(Character.builder(92).build()).addSymbol(Plus.builder(Character.builder(117).build()).build()).addSymbol(Nonterminal.builder("HexDigit").build()).addSymbol(Nonterminal.builder("HexDigit").build()).addSymbol(Nonterminal.builder("HexDigit").build()).addSymbol(Nonterminal.builder("HexDigit").build()).build())
			//ExclusiveOrExpression ::= ExclusiveOrExpression "^" AndExpression 
			.addRule(Rule.withHead(Nonterminal.builder("ExclusiveOrExpression").build()).addSymbol(Nonterminal.builder("ExclusiveOrExpression").build()).addSymbol(Sequence.builder(Character.builder(94).build()).build()).addSymbol(Nonterminal.builder("AndExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ExclusiveOrExpression ::= AndExpression 
			.addRule(Rule.withHead(Nonterminal.builder("ExclusiveOrExpression").build()).addSymbol(Nonterminal.builder("AndExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AnnotationTypeDeclaration ::= InterfaceModifier* "@" "interface" Identifier AnnotationTypeBody 
			.addRule(Rule.withHead(Nonterminal.builder("AnnotationTypeDeclaration").build()).addSymbol(Star.builder(Nonterminal.builder("InterfaceModifier").build()).build()).addSymbol(Sequence.builder(Character.builder(64).build()).build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(102).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(101).build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Nonterminal.builder("AnnotationTypeBody").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//TypeArguments ::= "<" TypeArgument+ ">" 
			.addRule(Rule.withHead(Nonterminal.builder("TypeArguments").build()).addSymbol(Sequence.builder(Character.builder(60).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("TypeArgument").build()).build()).addSymbol(Sequence.builder(Character.builder(62).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Annotation ::= "@" TypeName ("(" ElementValue ")")? 
			.addRule(Rule.withHead(Nonterminal.builder("Annotation").build()).addSymbol(Sequence.builder(Character.builder(64).build()).build()).addSymbol(Nonterminal.builder("TypeName").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(40).build()).build(), Nonterminal.builder("ElementValue").build(), Sequence.builder(Character.builder(41).build()).build()).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Annotation ::= "@" TypeName "(" ElementValuePair* ")" 
			.addRule(Rule.withHead(Nonterminal.builder("Annotation").build()).addSymbol(Sequence.builder(Character.builder(64).build()).build()).addSymbol(Nonterminal.builder("TypeName").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Star.builder(Nonterminal.builder("ElementValuePair").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ClassBodyDeclaration ::= InstanceInitializer 
			.addRule(Rule.withHead(Nonterminal.builder("ClassBodyDeclaration").build()).addSymbol(Nonterminal.builder("InstanceInitializer").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ClassBodyDeclaration ::= StaticInitializer 
			.addRule(Rule.withHead(Nonterminal.builder("ClassBodyDeclaration").build()).addSymbol(Nonterminal.builder("StaticInitializer").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ClassBodyDeclaration ::= ClassMemberDeclaration 
			.addRule(Rule.withHead(Nonterminal.builder("ClassBodyDeclaration").build()).addSymbol(Nonterminal.builder("ClassMemberDeclaration").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ClassBodyDeclaration ::= ConstructorDeclaration 
			.addRule(Rule.withHead(Nonterminal.builder("ClassBodyDeclaration").build()).addSymbol(Nonterminal.builder("ConstructorDeclaration").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ForUpdate ::= StatementExpression+ 
			.addRule(Rule.withHead(Nonterminal.builder("ForUpdate").build()).addSymbol(Plus.builder(Nonterminal.builder("StatementExpression").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//NotStarNotSlash ::= InputCharacter 
			.addRule(Rule.withHead(Nonterminal.builder("NotStarNotSlash").build()).addSymbol(Nonterminal.builder("InputCharacter").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Character.builder(42).build()).build()))).build()).build())
			//NotStarNotSlash ::= LineTerminator 
			.addRule(Rule.withHead(Nonterminal.builder("NotStarNotSlash").build()).addSymbol(Nonterminal.builder("LineTerminator").build()).build())
			//ForStatement ::= "for" "(" FormalParameter ":" Expression ")" Statement 
			.addRule(Rule.withHead(Nonterminal.builder("ForStatement").build()).addSymbol(Sequence.builder(Character.builder(102).build(), Character.builder(111).build(), Character.builder(114).build()).build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("FormalParameter").build()).addSymbol(Sequence.builder(Character.builder(58).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).addSymbol(Nonterminal.builder("Statement").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ForStatement ::= "for" "(" ForInit? ";" Expression? ";" ForUpdate? ")" Statement 
			.addRule(Rule.withHead(Nonterminal.builder("ForStatement").build()).addSymbol(Sequence.builder(Character.builder(102).build(), Character.builder(111).build(), Character.builder(114).build()).build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ForInit").build()).build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Expression").build()).build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ForUpdate").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).addSymbol(Nonterminal.builder("Statement").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Throws ::= "throws" ExceptionType+ 
			.addRule(Rule.withHead(Nonterminal.builder("Throws").build()).addSymbol(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(119).build(), Character.builder(115).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("ExceptionType").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StatementExpression ::= PostIncrementExpression 
			.addRule(Rule.withHead(Nonterminal.builder("StatementExpression").build()).addSymbol(Nonterminal.builder("PostIncrementExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StatementExpression ::= PostDecrementExpression 
			.addRule(Rule.withHead(Nonterminal.builder("StatementExpression").build()).addSymbol(Nonterminal.builder("PostDecrementExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StatementExpression ::= Assignment 
			.addRule(Rule.withHead(Nonterminal.builder("StatementExpression").build()).addSymbol(Nonterminal.builder("Assignment").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StatementExpression ::= ClassInstanceCreationExpression 
			.addRule(Rule.withHead(Nonterminal.builder("StatementExpression").build()).addSymbol(Nonterminal.builder("ClassInstanceCreationExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StatementExpression ::= PreIncrementExpression 
			.addRule(Rule.withHead(Nonterminal.builder("StatementExpression").build()).addSymbol(Nonterminal.builder("PreIncrementExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StatementExpression ::= PreDecrementExpression 
			.addRule(Rule.withHead(Nonterminal.builder("StatementExpression").build()).addSymbol(Nonterminal.builder("PreDecrementExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StatementExpression ::= MethodInvocation 
			.addRule(Rule.withHead(Nonterminal.builder("StatementExpression").build()).addSymbol(Nonterminal.builder("MethodInvocation").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Comment ::= TraditionalComment 
			.addRule(Rule.withHead(Nonterminal.builder("Comment").build()).addSymbol(Nonterminal.builder("TraditionalComment").build()).build())
			//Comment ::= EndOfLineComment 
			.addRule(Rule.withHead(Nonterminal.builder("Comment").build()).addSymbol(Nonterminal.builder("EndOfLineComment").build()).build())
			//ConstantModifier ::= "final" 
			.addRule(Rule.withHead(Nonterminal.builder("ConstantModifier").build()).addSymbol(Sequence.builder(Character.builder(102).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(97).build(), Character.builder(108).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ConstantModifier ::= Annotation 
			.addRule(Rule.withHead(Nonterminal.builder("ConstantModifier").build()).addSymbol(Nonterminal.builder("Annotation").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ConstantModifier ::= "public" 
			.addRule(Rule.withHead(Nonterminal.builder("ConstantModifier").build()).addSymbol(Sequence.builder(Character.builder(112).build(), Character.builder(117).build(), Character.builder(98).build(), Character.builder(108).build(), Character.builder(105).build(), Character.builder(99).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ConstantModifier ::= "static" 
			.addRule(Rule.withHead(Nonterminal.builder("ConstantModifier").build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(99).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//WhiteSpace ::=  
			.addRule(Rule.withHead(Nonterminal.builder("WhiteSpace").build()).addSymbol(Alt.builder(CharacterRange.builder(9, 10).build(), CharacterRange.builder(12, 13).build(), CharacterRange.builder(26, 26).build(), CharacterRange.builder(32, 32).build()).build()).build())
			//TypeParameter ::= TypeVariable TypeBound? 
			.addRule(Rule.withHead(Nonterminal.builder("TypeParameter").build()).addSymbol(Nonterminal.builder("TypeVariable").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeBound").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//MethodHeader ::= MethodModifier* TypeParameters? Result MethodDeclarator Throws? 
			.addRule(Rule.withHead(Nonterminal.builder("MethodHeader").build()).addSymbol(Star.builder(Nonterminal.builder("MethodModifier").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeParameters").build()).build()).addSymbol(Nonterminal.builder("Result").build()).addSymbol(Nonterminal.builder("MethodDeclarator").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Throws").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AbstractMethodModifier ::= "abstract" 
			.addRule(Rule.withHead(Nonterminal.builder("AbstractMethodModifier").build()).addSymbol(Sequence.builder(Character.builder(97).build(), Character.builder(98).build(), Character.builder(115).build(), Character.builder(116).build(), Character.builder(114).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(116).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AbstractMethodModifier ::= Annotation 
			.addRule(Rule.withHead(Nonterminal.builder("AbstractMethodModifier").build()).addSymbol(Nonterminal.builder("Annotation").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AbstractMethodModifier ::= "public" 
			.addRule(Rule.withHead(Nonterminal.builder("AbstractMethodModifier").build()).addSymbol(Sequence.builder(Character.builder(112).build(), Character.builder(117).build(), Character.builder(98).build(), Character.builder(108).build(), Character.builder(105).build(), Character.builder(99).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ClassName ::= QualifiedIdentifier 
			.addRule(Rule.withHead(Nonterminal.builder("ClassName").build()).addSymbol(Nonterminal.builder("QualifiedIdentifier").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//HexadecimalFloatingPointLiteral ::= HexSignificand BinaryExponent FloatTypeSuffix? 
			.addRule(Rule.withHead(Nonterminal.builder("HexadecimalFloatingPointLiteral").build()).addSymbol(Nonterminal.builder("HexSignificand").build()).addSymbol(Nonterminal.builder("BinaryExponent").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("FloatTypeSuffix").build()).build()).build())
			//CommentTailStar ::= "*" CommentTailStar 
			.addRule(Rule.withHead(Nonterminal.builder("CommentTailStar").build()).addSymbol(Sequence.builder(Character.builder(42).build()).build()).addSymbol(Nonterminal.builder("CommentTailStar").build()).build())
			//CommentTailStar ::= "/" 
			.addRule(Rule.withHead(Nonterminal.builder("CommentTailStar").build()).addSymbol(Sequence.builder(Character.builder(47).build()).build()).build())
			//CommentTailStar ::= NotStarNotSlash CommentTail 
			.addRule(Rule.withHead(Nonterminal.builder("CommentTailStar").build()).addSymbol(Nonterminal.builder("NotStarNotSlash").build()).addSymbol(Nonterminal.builder("CommentTail").build()).build())
			//ConstructorBody ::= "{" ExplicitConstructorInvocation? BlockStatement* "}" 
			.addRule(Rule.withHead(Nonterminal.builder("ConstructorBody").build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ExplicitConstructorInvocation").build()).build()).addSymbol(Star.builder(Nonterminal.builder("BlockStatement").build()).build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//EndOfLineComment ::= "//" InputCharacter* 
			.addRule(Rule.withHead(Nonterminal.builder("EndOfLineComment").build()).addSymbol(Sequence.builder(Character.builder(47).build(), Character.builder(47).build()).build()).addSymbol(Star.builder(Nonterminal.builder("InputCharacter").build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Alt.builder(CharacterRange.builder(1, 9).build(), CharacterRange.builder(11, 12).build(), CharacterRange.builder(14, 1114111).build()).build()))).build()).build())
			//OctalDigits ::= OctalDigit OctalDigitOrUnderscore* OctalDigit 
			.addRule(Rule.withHead(Nonterminal.builder("OctalDigits").build()).addSymbol(Nonterminal.builder("OctalDigit").build()).addSymbol(Star.builder(Nonterminal.builder("OctalDigitOrUnderscore").build()).build()).addSymbol(Nonterminal.builder("OctalDigit").build()).build())
			//OctalDigits ::= OctalDigit 
			.addRule(Rule.withHead(Nonterminal.builder("OctalDigits").build()).addSymbol(Nonterminal.builder("OctalDigit").build()).build())
			//ConstantExpression ::= Expression 
			.addRule(Rule.withHead(Nonterminal.builder("ConstantExpression").build()).addSymbol(Nonterminal.builder("Expression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//FieldDeclaration ::= FieldModifier* Type VariableDeclarators ";" 
			.addRule(Rule.withHead(Nonterminal.builder("FieldDeclaration").build()).addSymbol(Star.builder(Nonterminal.builder("FieldModifier").build()).build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Nonterminal.builder("VariableDeclarators").build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//TypeBound ::= "extends" ReferenceType+ 
			.addRule(Rule.withHead(Nonterminal.builder("TypeBound").build()).addSymbol(Sequence.builder(Character.builder(101).build(), Character.builder(120).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(100).build(), Character.builder(115).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("ReferenceType").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//BlockStatement ::= LocalVariableDeclarationStatement 
			.addRule(Rule.withHead(Nonterminal.builder("BlockStatement").build()).addSymbol(Nonterminal.builder("LocalVariableDeclarationStatement").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//BlockStatement ::= ClassDeclaration 
			.addRule(Rule.withHead(Nonterminal.builder("BlockStatement").build()).addSymbol(Nonterminal.builder("ClassDeclaration").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//BlockStatement ::= Statement 
			.addRule(Rule.withHead(Nonterminal.builder("BlockStatement").build()).addSymbol(Nonterminal.builder("Statement").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//NullLiteral ::= "null" 
			.addRule(Rule.withHead(Nonterminal.builder("NullLiteral").build()).addSymbol(Sequence.builder(Character.builder(110).build(), Character.builder(117).build(), Character.builder(108).build(), Character.builder(108).build()).build()).build())
			//DimExpr ::= "[" Expression "]" 
			.addRule(Rule.withHead(Nonterminal.builder("DimExpr").build()).addSymbol(Sequence.builder(Character.builder(91).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Sequence.builder(Character.builder(93).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ConstantDeclaration ::= ConstantModifier* Type VariableDeclarators ";" 
			.addRule(Rule.withHead(Nonterminal.builder("ConstantDeclaration").build()).addSymbol(Star.builder(Nonterminal.builder("ConstantModifier").build()).build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Nonterminal.builder("VariableDeclarators").build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//InterfaceDeclaration ::= NormalInterfaceDeclaration 
			.addRule(Rule.withHead(Nonterminal.builder("InterfaceDeclaration").build()).addSymbol(Nonterminal.builder("NormalInterfaceDeclaration").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//InterfaceDeclaration ::= AnnotationTypeDeclaration 
			.addRule(Rule.withHead(Nonterminal.builder("InterfaceDeclaration").build()).addSymbol(Nonterminal.builder("AnnotationTypeDeclaration").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AbstractMethodDeclaration ::= AbstractMethodModifier* TypeParameters? Result MethodDeclarator Throws? ";" 
			.addRule(Rule.withHead(Nonterminal.builder("AbstractMethodDeclaration").build()).addSymbol(Star.builder(Nonterminal.builder("AbstractMethodModifier").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeParameters").build()).build()).addSymbol(Nonterminal.builder("Result").build()).addSymbol(Nonterminal.builder("MethodDeclarator").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Throws").build()).build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//FormalParameterList ::= (FormalParameter ",")* LastFormalParameter 
			.addRule(Rule.withHead(Nonterminal.builder("FormalParameterList").build()).addSymbol(Star.builder(Sequence.builder(Nonterminal.builder("FormalParameter").build(), Sequence.builder(Character.builder(44).build()).build()).build()).build()).addSymbol(Nonterminal.builder("LastFormalParameter").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//NonZeroDigit ::= (1-9) 
			.addRule(Rule.withHead(Nonterminal.builder("NonZeroDigit").build()).addSymbol(Alt.builder(CharacterRange.builder(49, 57).build()).build()).build())
			//ArrayInitializer ::= "{" VariableInitializer* (,)? "}" 
			.addRule(Rule.withHead(Nonterminal.builder("ArrayInitializer").build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).addSymbol(Star.builder(Nonterminal.builder("VariableInitializer").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Character.builder(44).build()).build()).build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ConstructorDeclaration ::= ConstructorModifier* ConstructorDeclarator Throws? ConstructorBody 
			.addRule(Rule.withHead(Nonterminal.builder("ConstructorDeclaration").build()).addSymbol(Star.builder(Nonterminal.builder("ConstructorModifier").build()).build()).addSymbol(Nonterminal.builder("ConstructorDeclarator").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Throws").build()).build()).addSymbol(Nonterminal.builder("ConstructorBody").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//LocalVariableDeclarationStatement ::= VariableModifier* Type VariableDeclarators ";" 
			.addRule(Rule.withHead(Nonterminal.builder("LocalVariableDeclarationStatement").build()).addSymbol(Star.builder(Nonterminal.builder("VariableModifier").build()).build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Nonterminal.builder("VariableDeclarators").build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ExponentIndicator ::= E 
			.addRule(Rule.withHead(Nonterminal.builder("ExponentIndicator").build()).addSymbol(Character.builder(69).build()).build())
			//RelationalExpression ::= ShiftExpression 
			.addRule(Rule.withHead(Nonterminal.builder("RelationalExpression").build()).addSymbol(Nonterminal.builder("ShiftExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//RelationalExpression ::= RelationalExpression "<=" ShiftExpression 
			.addRule(Rule.withHead(Nonterminal.builder("RelationalExpression").build()).addSymbol(Nonterminal.builder("RelationalExpression").build()).addSymbol(Sequence.builder(Character.builder(60).build(), Character.builder(61).build()).build()).addSymbol(Nonterminal.builder("ShiftExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//RelationalExpression ::= RelationalExpression ">=" ShiftExpression 
			.addRule(Rule.withHead(Nonterminal.builder("RelationalExpression").build()).addSymbol(Nonterminal.builder("RelationalExpression").build()).addSymbol(Sequence.builder(Character.builder(62).build(), Character.builder(61).build()).build()).addSymbol(Nonterminal.builder("ShiftExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//RelationalExpression ::= RelationalExpression "instanceof" ReferenceType 
			.addRule(Rule.withHead(Nonterminal.builder("RelationalExpression").build()).addSymbol(Nonterminal.builder("RelationalExpression").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(110).build(), Character.builder(99).build(), Character.builder(101).build(), Character.builder(111).build(), Character.builder(102).build()).build()).addSymbol(Nonterminal.builder("ReferenceType").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//RelationalExpression ::= RelationalExpression ">" ShiftExpression 
			.addRule(Rule.withHead(Nonterminal.builder("RelationalExpression").build()).addSymbol(Nonterminal.builder("RelationalExpression").build()).addSymbol(Sequence.builder(Character.builder(62).build()).build()).addSymbol(Nonterminal.builder("ShiftExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//RelationalExpression ::= RelationalExpression "<" ShiftExpression 
			.addRule(Rule.withHead(Nonterminal.builder("RelationalExpression").build()).addSymbol(Nonterminal.builder("RelationalExpression").build()).addSymbol(Sequence.builder(Character.builder(60).build()).build()).addSymbol(Nonterminal.builder("ShiftExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//LastFormalParameter ::= VariableModifier* Type "..." VariableDeclaratorId 
			.addRule(Rule.withHead(Nonterminal.builder("LastFormalParameter").build()).addSymbol(Star.builder(Nonterminal.builder("VariableModifier").build()).build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Sequence.builder(Character.builder(46).build(), Character.builder(46).build(), Character.builder(46).build()).build()).addSymbol(Nonterminal.builder("VariableDeclaratorId").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//LastFormalParameter ::= FormalParameter 
			.addRule(Rule.withHead(Nonterminal.builder("LastFormalParameter").build()).addSymbol(Nonterminal.builder("FormalParameter").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//FloatingPointLiteral ::= HexadecimalFloatingPointLiteral 
			.addRule(Rule.withHead(Nonterminal.builder("FloatingPointLiteral").build()).addSymbol(Nonterminal.builder("HexadecimalFloatingPointLiteral").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//FloatingPointLiteral ::= DecimalFloatingPointLiteral 
			.addRule(Rule.withHead(Nonterminal.builder("FloatingPointLiteral").build()).addSymbol(Nonterminal.builder("DecimalFloatingPointLiteral").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//DecimalIntegerLiteral ::= DecimalNumeral IntegerTypeSuffix? 
			.addRule(Rule.withHead(Nonterminal.builder("DecimalIntegerLiteral").build()).addSymbol(Nonterminal.builder("DecimalNumeral").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("IntegerTypeSuffix").build()).build()).build())
			//JavaLetter ::= ($ | A-Z | _ | a-z) 
			.addRule(Rule.withHead(Nonterminal.builder("JavaLetter").build()).addSymbol(Alt.builder(CharacterRange.builder(36, 36).build(), CharacterRange.builder(65, 90).build(), CharacterRange.builder(95, 95).build(), CharacterRange.builder(97, 122).build()).build()).build())
			//BooleanLiteral ::= "false" 
			.addRule(Rule.withHead(Nonterminal.builder("BooleanLiteral").build()).addSymbol(Sequence.builder(Character.builder(102).build(), Character.builder(97).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build()).build())
			//BooleanLiteral ::= "true" 
			.addRule(Rule.withHead(Nonterminal.builder("BooleanLiteral").build()).addSymbol(Sequence.builder(Character.builder(116).build(), Character.builder(114).build(), Character.builder(117).build(), Character.builder(101).build()).build()).build())
			//Identifier ::= IdentifierChars 
			.addRule(Rule.withHead(Nonterminal.builder("Identifier").build()).addSymbol(Nonterminal.builder("IdentifierChars").addPreConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_PRECEDE, Alt.builder(CharacterRange.builder(36, 36).build(), CharacterRange.builder(65, 90).build(), CharacterRange.builder(95, 95).build(), CharacterRange.builder(97, 122).build()).build()))).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Sequence.builder(Character.builder(99).build(), Character.builder(108).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build()).build(), Sequence.builder(Character.builder(98).build(), Character.builder(114).build(), Character.builder(101).build(), Character.builder(97).build(), Character.builder(107).build()).build(), Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(105).build(), Character.builder(115).build()).build(), Sequence.builder(Character.builder(105).build(), Character.builder(102).build()).build(), Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(99).build(), Character.builder(116).build(), Character.builder(102).build(), Character.builder(112).build()).build(), Sequence.builder(Character.builder(110).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(101).build()).build(), Sequence.builder(Character.builder(100).build(), Character.builder(111).build()).build(), Sequence.builder(Character.builder(116).build(), Character.builder(114).build(), Character.builder(97).build(), Character.builder(110).build(), Character.builder(115).build(), Character.builder(105).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(116).build()).build(), Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(116).build()).build(), Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(119).build(), Character.builder(115).build()).build(), Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(110).build(), Character.builder(99).build(), Character.builder(101).build(), Character.builder(111).build(), Character.builder(102).build()).build(), Sequence.builder(Character.builder(97).build(), Character.builder(98).build(), Character.builder(115).build(), Character.builder(116).build(), Character.builder(114).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(116).build()).build(), Sequence.builder(Character.builder(110).build(), Character.builder(101).build(), Character.builder(119).build()).build(), Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build(), Sequence.builder(Character.builder(115).build(), Character.builder(117).build(), Character.builder(112).build(), Character.builder(101).build(), Character.builder(114).build()).build(), Sequence.builder(Character.builder(99).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(99).build(), Character.builder(104).build()).build(), Sequence.builder(Character.builder(116).build(), Character.builder(114).build(), Character.builder(121).build()).build(), Sequence.builder(Character.builder(101).build(), Character.builder(120).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(100).build(), Character.builder(115).build()).build(), Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(102).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(101).build()).build(), Sequence.builder(Character.builder(101).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build(), Sequence.builder(Character.builder(102).build(), Character.builder(108).build(), Character.builder(111).build(), Character.builder(97).build(), Character.builder(116).build()).build(), Sequence.builder(Character.builder(108).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(103).build()).build(), Sequence.builder(Character.builder(118).build(), Character.builder(111).build(), Character.builder(108).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(108).build(), Character.builder(101).build()).build(), Sequence.builder(Character.builder(115).build(), Character.builder(104).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build(), Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(105).build(), Character.builder(108).build(), Character.builder(101).build()).build(), Sequence.builder(Character.builder(100).build(), Character.builder(111).build(), Character.builder(117).build(), Character.builder(98).build(), Character.builder(108).build(), Character.builder(101).build()).build(), Sequence.builder(Character.builder(99).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(101).build()).build(), Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(108).build(), Character.builder(101).build(), Character.builder(109).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(116).build(), Character.builder(115).build()).build(), Sequence.builder(Character.builder(114).build(), Character.builder(101).build(), Character.builder(116).build(), Character.builder(117).build(), Character.builder(114).build(), Character.builder(110).build()).build(), Sequence.builder(Character.builder(99).build(), Character.builder(104).build(), Character.builder(97).build(), Character.builder(114).build()).build(), Sequence.builder(Character.builder(102).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(97).build(), Character.builder(108).build()).build(), Sequence.builder(Character.builder(115).build(), Character.builder(119).build(), Character.builder(105).build(), Character.builder(116).build(), Character.builder(99).build(), Character.builder(104).build()).build(), Sequence.builder(Character.builder(115).build(), Character.builder(121).build(), Character.builder(110).build(), Character.builder(99).build(), Character.builder(104).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(105).build(), Character.builder(122).build(), Character.builder(101).build(), Character.builder(100).build()).build(), Sequence.builder(Character.builder(118).build(), Character.builder(111).build(), Character.builder(105).build(), Character.builder(100).build()).build(), Sequence.builder(Character.builder(112).build(), Character.builder(117).build(), Character.builder(98).build(), Character.builder(108).build(), Character.builder(105).build(), Character.builder(99).build()).build(), Sequence.builder(Character.builder(102).build(), Character.builder(111).build(), Character.builder(114).build()).build(), Sequence.builder(Character.builder(98).build(), Character.builder(121).build(), Character.builder(116).build(), Character.builder(101).build()).build(), Sequence.builder(Character.builder(98).build(), Character.builder(111).build(), Character.builder(111).build(), Character.builder(108).build(), Character.builder(101).build(), Character.builder(97).build(), Character.builder(110).build()).build(), Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(99).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(100).build()).build(), Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(99).build()).build(), Sequence.builder(Character.builder(116).build(), Character.builder(114).build(), Character.builder(117).build(), Character.builder(101).build()).build(), Sequence.builder(Character.builder(99).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(115).build(), Character.builder(116).build()).build(), Sequence.builder(Character.builder(103).build(), Character.builder(111).build(), Character.builder(116).build(), Character.builder(111).build()).build(), Sequence.builder(Character.builder(102).build(), Character.builder(97).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build(), Sequence.builder(Character.builder(102).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(97).build(), Character.builder(108).build(), Character.builder(108).build(), Character.builder(121).build()).build(), Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(101).build()).build(), Sequence.builder(Character.builder(100).build(), Character.builder(101).build(), Character.builder(102).build(), Character.builder(97).build(), Character.builder(117).build(), Character.builder(108).build(), Character.builder(116).build()).build(), Sequence.builder(Character.builder(101).build(), Character.builder(110).build(), Character.builder(117).build(), Character.builder(109).build()).build(), Sequence.builder(Character.builder(110).build(), Character.builder(117).build(), Character.builder(108).build(), Character.builder(108).build()).build(), Sequence.builder(Character.builder(99).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(117).build(), Character.builder(101).build()).build(), Sequence.builder(Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(116).build()).build(), Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(119).build()).build(), Sequence.builder(Character.builder(112).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(107).build(), Character.builder(97).build(), Character.builder(103).build(), Character.builder(101).build()).build()).build()), new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Alt.builder(CharacterRange.builder(36, 36).build(), CharacterRange.builder(48, 57).build(), CharacterRange.builder(65, 90).build(), CharacterRange.builder(95, 95).build(), CharacterRange.builder(97, 122).build()).build()))).build()).build())
			//MethodModifier ::= "private" 
			.addRule(Rule.withHead(Nonterminal.builder("MethodModifier").build()).addSymbol(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(101).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//MethodModifier ::= "synchronized" 
			.addRule(Rule.withHead(Nonterminal.builder("MethodModifier").build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(121).build(), Character.builder(110).build(), Character.builder(99).build(), Character.builder(104).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(105).build(), Character.builder(122).build(), Character.builder(101).build(), Character.builder(100).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//MethodModifier ::= "final" 
			.addRule(Rule.withHead(Nonterminal.builder("MethodModifier").build()).addSymbol(Sequence.builder(Character.builder(102).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(97).build(), Character.builder(108).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//MethodModifier ::= "strictfp" 
			.addRule(Rule.withHead(Nonterminal.builder("MethodModifier").build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(99).build(), Character.builder(116).build(), Character.builder(102).build(), Character.builder(112).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//MethodModifier ::= "abstract" 
			.addRule(Rule.withHead(Nonterminal.builder("MethodModifier").build()).addSymbol(Sequence.builder(Character.builder(97).build(), Character.builder(98).build(), Character.builder(115).build(), Character.builder(116).build(), Character.builder(114).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(116).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//MethodModifier ::= "native" 
			.addRule(Rule.withHead(Nonterminal.builder("MethodModifier").build()).addSymbol(Sequence.builder(Character.builder(110).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(101).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//MethodModifier ::= "protected" 
			.addRule(Rule.withHead(Nonterminal.builder("MethodModifier").build()).addSymbol(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(99).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(100).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//MethodModifier ::= Annotation 
			.addRule(Rule.withHead(Nonterminal.builder("MethodModifier").build()).addSymbol(Nonterminal.builder("Annotation").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//MethodModifier ::= "public" 
			.addRule(Rule.withHead(Nonterminal.builder("MethodModifier").build()).addSymbol(Sequence.builder(Character.builder(112).build(), Character.builder(117).build(), Character.builder(98).build(), Character.builder(108).build(), Character.builder(105).build(), Character.builder(99).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//MethodModifier ::= "static" 
			.addRule(Rule.withHead(Nonterminal.builder("MethodModifier").build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(99).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//VariableModifier ::= "final" 
			.addRule(Rule.withHead(Nonterminal.builder("VariableModifier").build()).addSymbol(Sequence.builder(Character.builder(102).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(97).build(), Character.builder(108).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//VariableModifier ::= Annotation 
			.addRule(Rule.withHead(Nonterminal.builder("VariableModifier").build()).addSymbol(Nonterminal.builder("Annotation").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AndExpression ::= EqualityExpression 
			.addRule(Rule.withHead(Nonterminal.builder("AndExpression").build()).addSymbol(Nonterminal.builder("EqualityExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AndExpression ::= AndExpression "&" EqualityExpression 
			.addRule(Rule.withHead(Nonterminal.builder("AndExpression").build()).addSymbol(Nonterminal.builder("AndExpression").build()).addSymbol(Sequence.builder(Character.builder(38).build()).build()).addSymbol(Nonterminal.builder("EqualityExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//IntegerTypeSuffix ::= l 
			.addRule(Rule.withHead(Nonterminal.builder("IntegerTypeSuffix").build()).addSymbol(Character.builder(108).build()).build())
			//IntegerTypeSuffix ::= L 
			.addRule(Rule.withHead(Nonterminal.builder("IntegerTypeSuffix").build()).addSymbol(Character.builder(76).build()).build())
			//FieldModifier ::= "final" 
			.addRule(Rule.withHead(Nonterminal.builder("FieldModifier").build()).addSymbol(Sequence.builder(Character.builder(102).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(97).build(), Character.builder(108).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//FieldModifier ::= "transient" 
			.addRule(Rule.withHead(Nonterminal.builder("FieldModifier").build()).addSymbol(Sequence.builder(Character.builder(116).build(), Character.builder(114).build(), Character.builder(97).build(), Character.builder(110).build(), Character.builder(115).build(), Character.builder(105).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(116).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//FieldModifier ::= Annotation 
			.addRule(Rule.withHead(Nonterminal.builder("FieldModifier").build()).addSymbol(Nonterminal.builder("Annotation").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//FieldModifier ::= "protected" 
			.addRule(Rule.withHead(Nonterminal.builder("FieldModifier").build()).addSymbol(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(99).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(100).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//FieldModifier ::= "volatile" 
			.addRule(Rule.withHead(Nonterminal.builder("FieldModifier").build()).addSymbol(Sequence.builder(Character.builder(118).build(), Character.builder(111).build(), Character.builder(108).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(108).build(), Character.builder(101).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//FieldModifier ::= "private" 
			.addRule(Rule.withHead(Nonterminal.builder("FieldModifier").build()).addSymbol(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(101).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//FieldModifier ::= "static" 
			.addRule(Rule.withHead(Nonterminal.builder("FieldModifier").build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(99).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//FieldModifier ::= "public" 
			.addRule(Rule.withHead(Nonterminal.builder("FieldModifier").build()).addSymbol(Sequence.builder(Character.builder(112).build(), Character.builder(117).build(), Character.builder(98).build(), Character.builder(108).build(), Character.builder(105).build(), Character.builder(99).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//IdentifierChars ::= JavaLetter 
			.addRule(Rule.withHead(Nonterminal.builder("IdentifierChars").build()).addSymbol(Nonterminal.builder("JavaLetter").build()).build())
			//IdentifierChars ::= IdentifierChars JavaLetterOrDigit 
			.addRule(Rule.withHead(Nonterminal.builder("IdentifierChars").build()).addSymbol(Nonterminal.builder("IdentifierChars").build()).addSymbol(Nonterminal.builder("JavaLetterOrDigit").build()).build())
			//ArrayType ::= Type "[" "]" 
			.addRule(Rule.withHead(Nonterminal.builder("ArrayType").build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Sequence.builder(Character.builder(91).build()).build()).addSymbol(Sequence.builder(Character.builder(93).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//TypeList ::= Type+ 
			.addRule(Rule.withHead(Nonterminal.builder("TypeList").build()).addSymbol(Plus.builder(Nonterminal.builder("Type").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Arguments ::= "(" ArgumentList? ")" 
			.addRule(Rule.withHead(Nonterminal.builder("Arguments").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ArgumentList").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Resources ::= Resource+ 
			.addRule(Rule.withHead(Nonterminal.builder("Resources").build()).addSymbol(Plus.builder(Nonterminal.builder("Resource").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//MethodInvocation ::= MethodName "(" ArgumentList? ")" 
			.addRule(Rule.withHead(Nonterminal.builder("MethodInvocation").build()).addSymbol(Nonterminal.builder("MethodName").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ArgumentList").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//MethodInvocation ::= TypeName "." NonWildTypeArguments Identifier "(" ArgumentList? ")" 
			.addRule(Rule.withHead(Nonterminal.builder("MethodInvocation").build()).addSymbol(Nonterminal.builder("TypeName").build()).addSymbol(Sequence.builder(Character.builder(46).build()).build()).addSymbol(Nonterminal.builder("NonWildTypeArguments").build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ArgumentList").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//MethodInvocation ::= ClassName "." "super" "." NonWildTypeArguments? Identifier "(" ArgumentList? ")" 
			.addRule(Rule.withHead(Nonterminal.builder("MethodInvocation").build()).addSymbol(Nonterminal.builder("ClassName").build()).addSymbol(Sequence.builder(Character.builder(46).build()).build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(117).build(), Character.builder(112).build(), Character.builder(101).build(), Character.builder(114).build()).build()).addSymbol(Sequence.builder(Character.builder(46).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("NonWildTypeArguments").build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ArgumentList").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//MethodInvocation ::= Primary "." NonWildTypeArguments? Identifier "(" ArgumentList? ")" 
			.addRule(Rule.withHead(Nonterminal.builder("MethodInvocation").build()).addSymbol(Nonterminal.builder("Primary").build()).addSymbol(Sequence.builder(Character.builder(46).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("NonWildTypeArguments").build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ArgumentList").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//MethodInvocation ::= "super" "." NonWildTypeArguments? Identifier "(" ArgumentList? ")" 
			.addRule(Rule.withHead(Nonterminal.builder("MethodInvocation").build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(117).build(), Character.builder(112).build(), Character.builder(101).build(), Character.builder(114).build()).build()).addSymbol(Sequence.builder(Character.builder(46).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("NonWildTypeArguments").build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ArgumentList").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ElementValuePair ::= Identifier "=" ElementValue 
			.addRule(Rule.withHead(Nonterminal.builder("ElementValuePair").build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Sequence.builder(Character.builder(61).build()).build()).addSymbol(Nonterminal.builder("ElementValue").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//OctalDigitOrUnderscore ::= OctalDigit 
			.addRule(Rule.withHead(Nonterminal.builder("OctalDigitOrUnderscore").build()).addSymbol(Nonterminal.builder("OctalDigit").build()).build())
			//OctalDigitOrUnderscore ::= _ 
			.addRule(Rule.withHead(Nonterminal.builder("OctalDigitOrUnderscore").build()).addSymbol(Character.builder(95).build()).build())
			//RawInputCharacter ::= (\u0001-[ | ]-\u10FFFF) 
			.addRule(Rule.withHead(Nonterminal.builder("RawInputCharacter").build()).addSymbol(Alt.builder(CharacterRange.builder(1, 91).build(), CharacterRange.builder(93, 1114111).build()).build()).build())
			//RawInputCharacter ::= \ 
			.addRule(Rule.withHead(Nonterminal.builder("RawInputCharacter").build()).addSymbol(Character.builder(92).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Character.builder(92).build()))).build()).build())
			//RawInputCharacter ::= \ \ 
			.addRule(Rule.withHead(Nonterminal.builder("RawInputCharacter").build()).addSymbol(Character.builder(92).build()).addSymbol(Character.builder(92).build()).build())
			//TypeDeclaration ::= InterfaceDeclaration 
			.addRule(Rule.withHead(Nonterminal.builder("TypeDeclaration").build()).addSymbol(Nonterminal.builder("InterfaceDeclaration").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//TypeDeclaration ::= ";" 
			.addRule(Rule.withHead(Nonterminal.builder("TypeDeclaration").build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//TypeDeclaration ::= ClassDeclaration 
			.addRule(Rule.withHead(Nonterminal.builder("TypeDeclaration").build()).addSymbol(Nonterminal.builder("ClassDeclaration").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//LocalVariableDeclaration ::= VariableModifier* Type VariableDeclarator+ 
			.addRule(Rule.withHead(Nonterminal.builder("LocalVariableDeclaration").build()).addSymbol(Star.builder(Nonterminal.builder("VariableModifier").build()).build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Plus.builder(Nonterminal.builder("VariableDeclarator").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ReferenceType ::= TypeDeclSpecifier TypeArguments? 
			.addRule(Rule.withHead(Nonterminal.builder("ReferenceType").build()).addSymbol(Nonterminal.builder("TypeDeclSpecifier").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeArguments").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ReferenceType ::= ArrayType 
			.addRule(Rule.withHead(Nonterminal.builder("ReferenceType").build()).addSymbol(Nonterminal.builder("ArrayType").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ConditionalExpression ::= ConditionalOrExpression "?" Expression ":" ConditionalExpression 
			.addRule(Rule.withHead(Nonterminal.builder("ConditionalExpression").build()).addSymbol(Nonterminal.builder("ConditionalOrExpression").build()).addSymbol(Sequence.builder(Character.builder(63).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Sequence.builder(Character.builder(58).build()).build()).addSymbol(Nonterminal.builder("ConditionalExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ConditionalExpression ::= ConditionalOrExpression 
			.addRule(Rule.withHead(Nonterminal.builder("ConditionalExpression").build()).addSymbol(Nonterminal.builder("ConditionalOrExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ElementValueArrayInitializer ::= "{" ElementValues? (,)? "}" 
			.addRule(Rule.withHead(Nonterminal.builder("ElementValueArrayInitializer").build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ElementValues").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Character.builder(44).build()).build()).build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//SwitchLabel ::= "default" ":" 
			.addRule(Rule.withHead(Nonterminal.builder("SwitchLabel").build()).addSymbol(Sequence.builder(Character.builder(100).build(), Character.builder(101).build(), Character.builder(102).build(), Character.builder(97).build(), Character.builder(117).build(), Character.builder(108).build(), Character.builder(116).build()).build()).addSymbol(Sequence.builder(Character.builder(58).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//SwitchLabel ::= "case" ConstantExpression ":" 
			.addRule(Rule.withHead(Nonterminal.builder("SwitchLabel").build()).addSymbol(Sequence.builder(Character.builder(99).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(101).build()).build()).addSymbol(Nonterminal.builder("ConstantExpression").build()).addSymbol(Sequence.builder(Character.builder(58).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ConstructorDeclarator ::= TypeParameters? Identifier "(" FormalParameterList? ")" 
			.addRule(Rule.withHead(Nonterminal.builder("ConstructorDeclarator").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeParameters").build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("FormalParameterList").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ExpressionName ::= QualifiedIdentifier 
			.addRule(Rule.withHead(Nonterminal.builder("ExpressionName").build()).addSymbol(Nonterminal.builder("QualifiedIdentifier").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//MethodName ::= QualifiedIdentifier 
			.addRule(Rule.withHead(Nonterminal.builder("MethodName").build()).addSymbol(Nonterminal.builder("QualifiedIdentifier").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StringLiteral ::= " StringCharacter* " 
			.addRule(Rule.withHead(Nonterminal.builder("StringLiteral").build()).addSymbol(Character.builder(34).build()).addSymbol(Star.builder(Nonterminal.builder("StringCharacter").build()).build()).addSymbol(Character.builder(34).build()).build())
			//EnumBodyDeclarations ::= ";" ClassBodyDeclaration* 
			.addRule(Rule.withHead(Nonterminal.builder("EnumBodyDeclarations").build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).addSymbol(Star.builder(Nonterminal.builder("ClassBodyDeclaration").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//LineTerminator ::= \u000A 
			.addRule(Rule.withHead(Nonterminal.builder("LineTerminator").build()).addSymbol(Character.builder(10).build()).build())
			//ClassModifier ::= Annotation 
			.addRule(Rule.withHead(Nonterminal.builder("ClassModifier").build()).addSymbol(Nonterminal.builder("Annotation").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ClassModifier ::= "protected" 
			.addRule(Rule.withHead(Nonterminal.builder("ClassModifier").build()).addSymbol(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(99).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(100).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ClassModifier ::= "strictfp" 
			.addRule(Rule.withHead(Nonterminal.builder("ClassModifier").build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(99).build(), Character.builder(116).build(), Character.builder(102).build(), Character.builder(112).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ClassModifier ::= "final" 
			.addRule(Rule.withHead(Nonterminal.builder("ClassModifier").build()).addSymbol(Sequence.builder(Character.builder(102).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(97).build(), Character.builder(108).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ClassModifier ::= "private" 
			.addRule(Rule.withHead(Nonterminal.builder("ClassModifier").build()).addSymbol(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(101).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ClassModifier ::= "abstract" 
			.addRule(Rule.withHead(Nonterminal.builder("ClassModifier").build()).addSymbol(Sequence.builder(Character.builder(97).build(), Character.builder(98).build(), Character.builder(115).build(), Character.builder(116).build(), Character.builder(114).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(116).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ClassModifier ::= "static" 
			.addRule(Rule.withHead(Nonterminal.builder("ClassModifier").build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(99).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ClassModifier ::= "public" 
			.addRule(Rule.withHead(Nonterminal.builder("ClassModifier").build()).addSymbol(Sequence.builder(Character.builder(112).build(), Character.builder(117).build(), Character.builder(98).build(), Character.builder(108).build(), Character.builder(105).build(), Character.builder(99).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//TypeVariable ::= Identifier 
			.addRule(Rule.withHead(Nonterminal.builder("TypeVariable").build()).addSymbol(Nonterminal.builder("Identifier").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//CharacterLiteral ::= ' EscapeSequence ' 
			.addRule(Rule.withHead(Nonterminal.builder("CharacterLiteral").build()).addSymbol(Character.builder(39).build()).addSymbol(Nonterminal.builder("EscapeSequence").build()).addSymbol(Character.builder(39).build()).build())
			//CharacterLiteral ::= ' SingleCharacter ' 
			.addRule(Rule.withHead(Nonterminal.builder("CharacterLiteral").build()).addSymbol(Character.builder(39).build()).addSymbol(Nonterminal.builder("SingleCharacter").build()).addSymbol(Character.builder(39).build()).build())
			//MethodDeclaration ::= MethodHeader MethodBody 
			.addRule(Rule.withHead(Nonterminal.builder("MethodDeclaration").build()).addSymbol(Nonterminal.builder("MethodHeader").build()).addSymbol(Nonterminal.builder("MethodBody").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//JavaLetterOrDigit ::= ($ | 0-9 | A-Z | _ | a-z) 
			.addRule(Rule.withHead(Nonterminal.builder("JavaLetterOrDigit").build()).addSymbol(Alt.builder(CharacterRange.builder(36, 36).build(), CharacterRange.builder(48, 57).build(), CharacterRange.builder(65, 90).build(), CharacterRange.builder(95, 95).build(), CharacterRange.builder(97, 122).build()).build()).build())
			//UnicodeInputCharacter ::= RawInputCharacter 
			.addRule(Rule.withHead(Nonterminal.builder("UnicodeInputCharacter").build()).addSymbol(Nonterminal.builder("RawInputCharacter").build()).build())
			//UnicodeInputCharacter ::= UnicodeEscape 
			.addRule(Rule.withHead(Nonterminal.builder("UnicodeInputCharacter").build()).addSymbol(Nonterminal.builder("UnicodeEscape").build()).build())
			//Assignment ::= LeftHandSide AssignmentOperator AssignmentExpression 
			.addRule(Rule.withHead(Nonterminal.builder("Assignment").build()).addSymbol(Nonterminal.builder("LeftHandSide").build()).addSymbol(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Nonterminal.builder("AssignmentExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//BinaryDigitOrUnderscore ::= BinaryDigit 
			.addRule(Rule.withHead(Nonterminal.builder("BinaryDigitOrUnderscore").build()).addSymbol(Nonterminal.builder("BinaryDigit").build()).build())
			//BinaryDigitOrUnderscore ::= _ 
			.addRule(Rule.withHead(Nonterminal.builder("BinaryDigitOrUnderscore").build()).addSymbol(Character.builder(95).build()).build())
			//PrimaryNoNewArray ::= Type "." "class" 
			.addRule(Rule.withHead(Nonterminal.builder("PrimaryNoNewArray").build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Sequence.builder(Character.builder(46).build()).build()).addSymbol(Sequence.builder(Character.builder(99).build(), Character.builder(108).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PrimaryNoNewArray ::= ArrayAccess 
			.addRule(Rule.withHead(Nonterminal.builder("PrimaryNoNewArray").build()).addSymbol(Nonterminal.builder("ArrayAccess").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PrimaryNoNewArray ::= MethodInvocation 
			.addRule(Rule.withHead(Nonterminal.builder("PrimaryNoNewArray").build()).addSymbol(Nonterminal.builder("MethodInvocation").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PrimaryNoNewArray ::= ClassInstanceCreationExpression 
			.addRule(Rule.withHead(Nonterminal.builder("PrimaryNoNewArray").build()).addSymbol(Nonterminal.builder("ClassInstanceCreationExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PrimaryNoNewArray ::= ClassName "." "this" 
			.addRule(Rule.withHead(Nonterminal.builder("PrimaryNoNewArray").build()).addSymbol(Nonterminal.builder("ClassName").build()).addSymbol(Sequence.builder(Character.builder(46).build()).build()).addSymbol(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(105).build(), Character.builder(115).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PrimaryNoNewArray ::= "(" Expression ")" 
			.addRule(Rule.withHead(Nonterminal.builder("PrimaryNoNewArray").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PrimaryNoNewArray ::= FieldAccess 
			.addRule(Rule.withHead(Nonterminal.builder("PrimaryNoNewArray").build()).addSymbol(Nonterminal.builder("FieldAccess").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PrimaryNoNewArray ::= "this" 
			.addRule(Rule.withHead(Nonterminal.builder("PrimaryNoNewArray").build()).addSymbol(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(105).build(), Character.builder(115).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PrimaryNoNewArray ::= "void" "." "class" 
			.addRule(Rule.withHead(Nonterminal.builder("PrimaryNoNewArray").build()).addSymbol(Sequence.builder(Character.builder(118).build(), Character.builder(111).build(), Character.builder(105).build(), Character.builder(100).build()).build()).addSymbol(Sequence.builder(Character.builder(46).build()).build()).addSymbol(Sequence.builder(Character.builder(99).build(), Character.builder(108).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PrimaryNoNewArray ::= Literal 
			.addRule(Rule.withHead(Nonterminal.builder("PrimaryNoNewArray").build()).addSymbol(Nonterminal.builder("Literal").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StatementNoShortIf ::= StatementWithoutTrailingSubstatement 
			.addRule(Rule.withHead(Nonterminal.builder("StatementNoShortIf").build()).addSymbol(Nonterminal.builder("StatementWithoutTrailingSubstatement").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StatementNoShortIf ::= Identifier ":" StatementNoShortIf 
			.addRule(Rule.withHead(Nonterminal.builder("StatementNoShortIf").build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Sequence.builder(Character.builder(58).build()).build()).addSymbol(Nonterminal.builder("StatementNoShortIf").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StatementNoShortIf ::= "for" "(" ForInit? ";" Expression? ";" ForUpdate? ")" StatementNoShortIf 
			.addRule(Rule.withHead(Nonterminal.builder("StatementNoShortIf").build()).addSymbol(Sequence.builder(Character.builder(102).build(), Character.builder(111).build(), Character.builder(114).build()).build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ForInit").build()).build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Expression").build()).build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ForUpdate").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).addSymbol(Nonterminal.builder("StatementNoShortIf").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StatementNoShortIf ::= "while" "(" Expression ")" StatementNoShortIf 
			.addRule(Rule.withHead(Nonterminal.builder("StatementNoShortIf").build()).addSymbol(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(105).build(), Character.builder(108).build(), Character.builder(101).build()).build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).addSymbol(Nonterminal.builder("StatementNoShortIf").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//StatementNoShortIf ::= "if" "(" Expression ")" StatementNoShortIf "else" StatementNoShortIf 
			.addRule(Rule.withHead(Nonterminal.builder("StatementNoShortIf").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(102).build()).build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).addSymbol(Nonterminal.builder("StatementNoShortIf").build()).addSymbol(Sequence.builder(Character.builder(101).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build()).addSymbol(Nonterminal.builder("StatementNoShortIf").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//DecimalNumeral ::= 0 
			.addRule(Rule.withHead(Nonterminal.builder("DecimalNumeral").build()).addSymbol(Character.builder(48).build()).build())
			//DecimalNumeral ::= NonZeroDigit _+ Digits 
			.addRule(Rule.withHead(Nonterminal.builder("DecimalNumeral").build()).addSymbol(Nonterminal.builder("NonZeroDigit").build()).addSymbol(Plus.builder(Character.builder(95).build()).build()).addSymbol(Nonterminal.builder("Digits").build()).build())
			//DecimalNumeral ::= NonZeroDigit Digits? 
			.addRule(Rule.withHead(Nonterminal.builder("DecimalNumeral").build()).addSymbol(Nonterminal.builder("NonZeroDigit").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Digits").build()).build()).build())
			//EnumDeclaration ::= ClassModifier* "enum" Identifier ("implements" TypeList)? EnumBody 
			.addRule(Rule.withHead(Nonterminal.builder("EnumDeclaration").build()).addSymbol(Star.builder(Nonterminal.builder("ClassModifier").build()).build()).addSymbol(Sequence.builder(Character.builder(101).build(), Character.builder(110).build(), Character.builder(117).build(), Character.builder(109).build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(108).build(), Character.builder(101).build(), Character.builder(109).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(116).build(), Character.builder(115).build()).build(), Nonterminal.builder("TypeList").build()).build()).build()).addSymbol(Nonterminal.builder("EnumBody").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AssignmentOperator ::= "&=" 
			.addRule(Rule.withHead(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Sequence.builder(Character.builder(38).build(), Character.builder(61).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AssignmentOperator ::= "=" 
			.addRule(Rule.withHead(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Sequence.builder(Character.builder(61).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AssignmentOperator ::= ">>=" 
			.addRule(Rule.withHead(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Sequence.builder(Character.builder(62).build(), Character.builder(62).build(), Character.builder(61).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AssignmentOperator ::= "-=" 
			.addRule(Rule.withHead(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Sequence.builder(Character.builder(45).build(), Character.builder(61).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AssignmentOperator ::= "/=" 
			.addRule(Rule.withHead(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Sequence.builder(Character.builder(47).build(), Character.builder(61).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AssignmentOperator ::= "*=" 
			.addRule(Rule.withHead(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Sequence.builder(Character.builder(42).build(), Character.builder(61).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AssignmentOperator ::= "+=" 
			.addRule(Rule.withHead(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Sequence.builder(Character.builder(43).build(), Character.builder(61).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AssignmentOperator ::= "^=" 
			.addRule(Rule.withHead(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Sequence.builder(Character.builder(94).build(), Character.builder(61).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AssignmentOperator ::= "|=" 
			.addRule(Rule.withHead(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Sequence.builder(Character.builder(124).build(), Character.builder(61).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AssignmentOperator ::= ">>>=" 
			.addRule(Rule.withHead(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Sequence.builder(Character.builder(62).build(), Character.builder(62).build(), Character.builder(62).build(), Character.builder(61).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AssignmentOperator ::= "%=" 
			.addRule(Rule.withHead(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Sequence.builder(Character.builder(37).build(), Character.builder(61).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AssignmentOperator ::= "<<=" 
			.addRule(Rule.withHead(Nonterminal.builder("AssignmentOperator").build()).addSymbol(Sequence.builder(Character.builder(60).build(), Character.builder(60).build(), Character.builder(61).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//EnumConstant ::= Annotation* Identifier Arguments? ClassBody? 
			.addRule(Rule.withHead(Nonterminal.builder("EnumConstant").build()).addSymbol(Star.builder(Nonterminal.builder("Annotation").build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Arguments").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ClassBody").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PackageDeclaration ::= Annotation* "package" QualifiedIdentifier ";" 
			.addRule(Rule.withHead(Nonterminal.builder("PackageDeclaration").build()).addSymbol(Star.builder(Nonterminal.builder("Annotation").build()).build()).addSymbol(Sequence.builder(Character.builder(112).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(107).build(), Character.builder(97).build(), Character.builder(103).build(), Character.builder(101).build()).build()).addSymbol(Nonterminal.builder("QualifiedIdentifier").build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ZeroToThree ::= (0-3) 
			.addRule(Rule.withHead(Nonterminal.builder("ZeroToThree").build()).addSymbol(Alt.builder(CharacterRange.builder(48, 51).build()).build()).build())
			//FormalParameter ::= VariableModifier* Type VariableDeclaratorId 
			.addRule(Rule.withHead(Nonterminal.builder("FormalParameter").build()).addSymbol(Star.builder(Nonterminal.builder("VariableModifier").build()).build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Nonterminal.builder("VariableDeclaratorId").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//SignedInteger ::= Sign? Digits 
			.addRule(Rule.withHead(Nonterminal.builder("SignedInteger").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Sign").build()).build()).addSymbol(Nonterminal.builder("Digits").build()).build())
			//StringCharacter ::= EscapeSequence 
			.addRule(Rule.withHead(Nonterminal.builder("StringCharacter").build()).addSymbol(Nonterminal.builder("EscapeSequence").build()).build())
			//StringCharacter ::= InputCharacter 
			.addRule(Rule.withHead(Nonterminal.builder("StringCharacter").build()).addSymbol(Nonterminal.builder("InputCharacter").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Character.builder(34).build()).build()))).build()).build())
			//EqualityExpression ::= RelationalExpression 
			.addRule(Rule.withHead(Nonterminal.builder("EqualityExpression").build()).addSymbol(Nonterminal.builder("RelationalExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//EqualityExpression ::= EqualityExpression "!=" RelationalExpression 
			.addRule(Rule.withHead(Nonterminal.builder("EqualityExpression").build()).addSymbol(Nonterminal.builder("EqualityExpression").build()).addSymbol(Sequence.builder(Character.builder(33).build(), Character.builder(61).build()).build()).addSymbol(Nonterminal.builder("RelationalExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//EqualityExpression ::= EqualityExpression "==" RelationalExpression 
			.addRule(Rule.withHead(Nonterminal.builder("EqualityExpression").build()).addSymbol(Nonterminal.builder("EqualityExpression").build()).addSymbol(Sequence.builder(Character.builder(61).build(), Character.builder(61).build()).build()).addSymbol(Nonterminal.builder("RelationalExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//HexSignificand ::= 0 x HexDigits? . HexDigits 
			.addRule(Rule.withHead(Nonterminal.builder("HexSignificand").build()).addSymbol(Character.builder(48).build()).addSymbol(Character.builder(120).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("HexDigits").build()).build()).addSymbol(Character.builder(46).build()).addSymbol(Nonterminal.builder("HexDigits").build()).build())
			//HexSignificand ::= HexNumeral 
			.addRule(Rule.withHead(Nonterminal.builder("HexSignificand").build()).addSymbol(Nonterminal.builder("HexNumeral").build()).build())
			//HexSignificand ::= 0 X HexDigits? . HexDigits 
			.addRule(Rule.withHead(Nonterminal.builder("HexSignificand").build()).addSymbol(Character.builder(48).build()).addSymbol(Character.builder(88).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("HexDigits").build()).build()).addSymbol(Character.builder(46).build()).addSymbol(Nonterminal.builder("HexDigits").build()).build())
			//HexSignificand ::= HexNumeral . 
			.addRule(Rule.withHead(Nonterminal.builder("HexSignificand").build()).addSymbol(Nonterminal.builder("HexNumeral").build()).addSymbol(Character.builder(46).build()).build())
			//FloatTypeSuffix ::= D 
			.addRule(Rule.withHead(Nonterminal.builder("FloatTypeSuffix").build()).addSymbol(Character.builder(68).build()).build())
			//BinaryIntegerLiteral ::= BinaryNumeral IntegerTypeSuffix? 
			.addRule(Rule.withHead(Nonterminal.builder("BinaryIntegerLiteral").build()).addSymbol(Nonterminal.builder("BinaryNumeral").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("IntegerTypeSuffix").build()).build()).build())
			//HexDigits ::= HexDigit HexDigitOrUnderscore* HexDigit 
			.addRule(Rule.withHead(Nonterminal.builder("HexDigits").build()).addSymbol(Nonterminal.builder("HexDigit").build()).addSymbol(Star.builder(Nonterminal.builder("HexDigitOrUnderscore").build()).build()).addSymbol(Nonterminal.builder("HexDigit").build()).build())
			//HexDigits ::= HexDigit 
			.addRule(Rule.withHead(Nonterminal.builder("HexDigits").build()).addSymbol(Nonterminal.builder("HexDigit").build()).build())
			//EscapeSequence ::= \ u+ "005" C \ u+ "005" C 
			.addRule(Rule.withHead(Nonterminal.builder("EscapeSequence").build()).addSymbol(Character.builder(92).build()).addSymbol(Plus.builder(Character.builder(117).build()).build()).addSymbol(Sequence.builder(Character.builder(48).build(), Character.builder(48).build(), Character.builder(53).build()).build()).addSymbol(Character.builder(67).build()).addSymbol(Character.builder(92).build()).addSymbol(Plus.builder(Character.builder(117).build()).build()).addSymbol(Sequence.builder(Character.builder(48).build(), Character.builder(48).build(), Character.builder(53).build()).build()).addSymbol(Character.builder(67).build()).build())
			//EscapeSequence ::= OctalEscape 
			.addRule(Rule.withHead(Nonterminal.builder("EscapeSequence").build()).addSymbol(Nonterminal.builder("OctalEscape").build()).build())
			//EscapeSequence ::= Backslash n 
			.addRule(Rule.withHead(Nonterminal.builder("EscapeSequence").build()).addSymbol(Nonterminal.builder("Backslash").build()).addSymbol(Character.builder(110).build()).build())
			//EscapeSequence ::= Backslash t 
			.addRule(Rule.withHead(Nonterminal.builder("EscapeSequence").build()).addSymbol(Nonterminal.builder("Backslash").build()).addSymbol(Character.builder(116).build()).build())
			//EscapeSequence ::= Backslash f 
			.addRule(Rule.withHead(Nonterminal.builder("EscapeSequence").build()).addSymbol(Nonterminal.builder("Backslash").build()).addSymbol(Character.builder(102).build()).build())
			//EscapeSequence ::= Backslash ' 
			.addRule(Rule.withHead(Nonterminal.builder("EscapeSequence").build()).addSymbol(Nonterminal.builder("Backslash").build()).addSymbol(Character.builder(39).build()).build())
			//EscapeSequence ::= Backslash " 
			.addRule(Rule.withHead(Nonterminal.builder("EscapeSequence").build()).addSymbol(Nonterminal.builder("Backslash").build()).addSymbol(Character.builder(34).build()).build())
			//EscapeSequence ::= Backslash r 
			.addRule(Rule.withHead(Nonterminal.builder("EscapeSequence").build()).addSymbol(Nonterminal.builder("Backslash").build()).addSymbol(Character.builder(114).build()).build())
			//EscapeSequence ::= Backslash b 
			.addRule(Rule.withHead(Nonterminal.builder("EscapeSequence").build()).addSymbol(Nonterminal.builder("Backslash").build()).addSymbol(Character.builder(98).build()).build())
			//Sign ::= + 
			.addRule(Rule.withHead(Nonterminal.builder("Sign").build()).addSymbol(Character.builder(43).build()).build())
			//InstanceInitializer ::= Block 
			.addRule(Rule.withHead(Nonterminal.builder("InstanceInitializer").build()).addSymbol(Nonterminal.builder("Block").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//MultiplicativeExpression ::= MultiplicativeExpression "%" UnaryExpression 
			.addRule(Rule.withHead(Nonterminal.builder("MultiplicativeExpression").build()).addSymbol(Nonterminal.builder("MultiplicativeExpression").build()).addSymbol(Sequence.builder(Character.builder(37).build()).build()).addSymbol(Nonterminal.builder("UnaryExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//MultiplicativeExpression ::= UnaryExpression 
			.addRule(Rule.withHead(Nonterminal.builder("MultiplicativeExpression").build()).addSymbol(Nonterminal.builder("UnaryExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//MultiplicativeExpression ::= MultiplicativeExpression "*" UnaryExpression 
			.addRule(Rule.withHead(Nonterminal.builder("MultiplicativeExpression").build()).addSymbol(Nonterminal.builder("MultiplicativeExpression").build()).addSymbol(Sequence.builder(Character.builder(42).build()).build()).addSymbol(Nonterminal.builder("UnaryExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//MultiplicativeExpression ::= MultiplicativeExpression "/" UnaryExpression 
			.addRule(Rule.withHead(Nonterminal.builder("MultiplicativeExpression").build()).addSymbol(Nonterminal.builder("MultiplicativeExpression").build()).addSymbol(Sequence.builder(Character.builder(47).build()).build()).addSymbol(Nonterminal.builder("UnaryExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ArrayCreationExpression ::= "new" (ReferenceTypeNonArrayType | PrimitiveType) ("[" "]")+ ArrayInitializer 
			.addRule(Rule.withHead(Nonterminal.builder("ArrayCreationExpression").build()).addSymbol(Sequence.builder(Character.builder(110).build(), Character.builder(101).build(), Character.builder(119).build()).build()).addSymbol(Alt.builder(Nonterminal.builder("ReferenceTypeNonArrayType").build(), Nonterminal.builder("PrimitiveType").build()).build()).addSymbol(Plus.builder(Sequence.builder(Sequence.builder(Character.builder(91).build()).build(), Sequence.builder(Character.builder(93).build()).build()).build()).build()).addSymbol(Nonterminal.builder("ArrayInitializer").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ArrayCreationExpression ::= "new" (PrimitiveType | ReferenceType) DimExpr+ ("[" "]")* 
			.addRule(Rule.withHead(Nonterminal.builder("ArrayCreationExpression").build()).addSymbol(Sequence.builder(Character.builder(110).build(), Character.builder(101).build(), Character.builder(119).build()).build()).addSymbol(Alt.builder(Nonterminal.builder("PrimitiveType").build(), Nonterminal.builder("ReferenceType").build()).build()).addSymbol(Plus.builder(Nonterminal.builder("DimExpr").build()).build()).addSymbol(Star.builder(Sequence.builder(Sequence.builder(Character.builder(91).build()).build(), Sequence.builder(Character.builder(93).build()).build()).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//VariableDeclaratorId ::= Identifier ("[" "]")* 
			.addRule(Rule.withHead(Nonterminal.builder("VariableDeclaratorId").build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Star.builder(Sequence.builder(Sequence.builder(Character.builder(91).build()).build(), Sequence.builder(Character.builder(93).build()).build()).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//MethodBody ::= Block 
			.addRule(Rule.withHead(Nonterminal.builder("MethodBody").build()).addSymbol(Nonterminal.builder("Block").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//MethodBody ::= ";" 
			.addRule(Rule.withHead(Nonterminal.builder("MethodBody").build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//TypeArgument ::= "?" (("extends" | "super") Type)? 
			.addRule(Rule.withHead(Nonterminal.builder("TypeArgument").build()).addSymbol(Sequence.builder(Character.builder(63).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Alt.builder(Sequence.builder(Character.builder(101).build(), Character.builder(120).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(100).build(), Character.builder(115).build()).build(), Sequence.builder(Character.builder(115).build(), Character.builder(117).build(), Character.builder(112).build(), Character.builder(101).build(), Character.builder(114).build()).build()).build(), Nonterminal.builder("Type").build()).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//TypeArgument ::= Type 
			.addRule(Rule.withHead(Nonterminal.builder("TypeArgument").build()).addSymbol(Nonterminal.builder("Type").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//VariableDeclarator ::= VariableDeclaratorId ("=" VariableInitializer)? 
			.addRule(Rule.withHead(Nonterminal.builder("VariableDeclarator").build()).addSymbol(Nonterminal.builder("VariableDeclaratorId").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(61).build()).build(), Nonterminal.builder("VariableInitializer").build()).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//BinaryExponentIndicator ::= P 
			.addRule(Rule.withHead(Nonterminal.builder("BinaryExponentIndicator").build()).addSymbol(Character.builder(80).build()).build())
			//Type ::= ReferenceType 
			.addRule(Rule.withHead(Nonterminal.builder("Type").build()).addSymbol(Nonterminal.builder("ReferenceType").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Type ::= PrimitiveType 
			.addRule(Rule.withHead(Nonterminal.builder("Type").build()).addSymbol(Nonterminal.builder("PrimitiveType").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ConditionalOrExpression ::= ConditionalOrExpression "||" ConditionalAndExpression 
			.addRule(Rule.withHead(Nonterminal.builder("ConditionalOrExpression").build()).addSymbol(Nonterminal.builder("ConditionalOrExpression").build()).addSymbol(Sequence.builder(Character.builder(124).build(), Character.builder(124).build()).build()).addSymbol(Nonterminal.builder("ConditionalAndExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ConditionalOrExpression ::= ConditionalAndExpression 
			.addRule(Rule.withHead(Nonterminal.builder("ConditionalOrExpression").build()).addSymbol(Nonterminal.builder("ConditionalAndExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//CatchType ::= QualifiedIdentifier+ 
			.addRule(Rule.withHead(Nonterminal.builder("CatchType").build()).addSymbol(Plus.builder(Nonterminal.builder("QualifiedIdentifier").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//HexNumeral ::= 0 X HexDigits 
			.addRule(Rule.withHead(Nonterminal.builder("HexNumeral").build()).addSymbol(Character.builder(48).build()).addSymbol(Character.builder(88).build()).addSymbol(Nonterminal.builder("HexDigits").build()).build())
			//HexNumeral ::= 0 x HexDigits 
			.addRule(Rule.withHead(Nonterminal.builder("HexNumeral").build()).addSymbol(Character.builder(48).build()).addSymbol(Character.builder(120).build()).addSymbol(Nonterminal.builder("HexDigits").build()).build())
			//Layout ::= (WhiteSpace | Comment)* 
			.addRule(Rule.withHead(Nonterminal.builder("Layout").build()).addSymbol(Star.builder(Alt.builder(Nonterminal.builder("WhiteSpace").build(), Nonterminal.builder("Comment").build()).build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Sequence.builder(Character.builder(47).build(), Character.builder(42).build()).build()), new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Sequence.builder(Character.builder(47).build(), Character.builder(47).build()).build()), new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Alt.builder(CharacterRange.builder(9, 10).build(), CharacterRange.builder(12, 13).build(), CharacterRange.builder(32, 32).build()).build()))).build()).build())
			//ArrayAccess ::= PrimaryNoNewArray "[" Expression "]" 
			.addRule(Rule.withHead(Nonterminal.builder("ArrayAccess").build()).addSymbol(Nonterminal.builder("PrimaryNoNewArray").build()).addSymbol(Sequence.builder(Character.builder(91).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Sequence.builder(Character.builder(93).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ArrayAccess ::= ExpressionName "[" Expression "]" 
			.addRule(Rule.withHead(Nonterminal.builder("ArrayAccess").build()).addSymbol(Nonterminal.builder("ExpressionName").build()).addSymbol(Sequence.builder(Character.builder(91).build()).build()).addSymbol(Nonterminal.builder("Expression").build()).addSymbol(Sequence.builder(Character.builder(93).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ExponentPart ::= ExponentIndicator SignedInteger 
			.addRule(Rule.withHead(Nonterminal.builder("ExponentPart").build()).addSymbol(Nonterminal.builder("ExponentIndicator").build()).addSymbol(Nonterminal.builder("SignedInteger").build()).build())
			//AdditiveExpression ::= MultiplicativeExpression 
			.addRule(Rule.withHead(Nonterminal.builder("AdditiveExpression").build()).addSymbol(Nonterminal.builder("MultiplicativeExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AdditiveExpression ::= AdditiveExpression "-" MultiplicativeExpression 
			.addRule(Rule.withHead(Nonterminal.builder("AdditiveExpression").build()).addSymbol(Nonterminal.builder("AdditiveExpression").build()).addSymbol(Sequence.builder(Character.builder(45).build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Sequence.builder(Character.builder(45).build()).build()))).build()).addSymbol(Nonterminal.builder("MultiplicativeExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AdditiveExpression ::= AdditiveExpression "+" MultiplicativeExpression 
			.addRule(Rule.withHead(Nonterminal.builder("AdditiveExpression").build()).addSymbol(Nonterminal.builder("AdditiveExpression").build()).addSymbol(Sequence.builder(Character.builder(43).build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Sequence.builder(Character.builder(43).build()).build()))).build()).addSymbol(Nonterminal.builder("MultiplicativeExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AssignmentExpression ::= Assignment 
			.addRule(Rule.withHead(Nonterminal.builder("AssignmentExpression").build()).addSymbol(Nonterminal.builder("Assignment").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AssignmentExpression ::= ConditionalExpression 
			.addRule(Rule.withHead(Nonterminal.builder("AssignmentExpression").build()).addSymbol(Nonterminal.builder("ConditionalExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//FieldAccess ::= "super" "." Identifier 
			.addRule(Rule.withHead(Nonterminal.builder("FieldAccess").build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(117).build(), Character.builder(112).build(), Character.builder(101).build(), Character.builder(114).build()).build()).addSymbol(Sequence.builder(Character.builder(46).build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//FieldAccess ::= Primary "." Identifier 
			.addRule(Rule.withHead(Nonterminal.builder("FieldAccess").build()).addSymbol(Nonterminal.builder("Primary").build()).addSymbol(Sequence.builder(Character.builder(46).build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//FieldAccess ::= ClassName "." "super" "." Identifier 
			.addRule(Rule.withHead(Nonterminal.builder("FieldAccess").build()).addSymbol(Nonterminal.builder("ClassName").build()).addSymbol(Sequence.builder(Character.builder(46).build()).build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(117).build(), Character.builder(112).build(), Character.builder(101).build(), Character.builder(114).build()).build()).addSymbol(Sequence.builder(Character.builder(46).build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ElementValues ::= ElementValue+ 
			.addRule(Rule.withHead(Nonterminal.builder("ElementValues").build()).addSymbol(Plus.builder(Nonterminal.builder("ElementValue").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//IntegerLiteral ::= BinaryIntegerLiteral 
			.addRule(Rule.withHead(Nonterminal.builder("IntegerLiteral").build()).addSymbol(Nonterminal.builder("BinaryIntegerLiteral").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//IntegerLiteral ::= DecimalIntegerLiteral 
			.addRule(Rule.withHead(Nonterminal.builder("IntegerLiteral").build()).addSymbol(Nonterminal.builder("DecimalIntegerLiteral").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Character.builder(46).build()))).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//IntegerLiteral ::= HexIntegerLiteral 
			.addRule(Rule.withHead(Nonterminal.builder("IntegerLiteral").build()).addSymbol(Nonterminal.builder("HexIntegerLiteral").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Character.builder(46).build()))).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//IntegerLiteral ::= OctalIntegerLiteral 
			.addRule(Rule.withHead(Nonterminal.builder("IntegerLiteral").build()).addSymbol(Nonterminal.builder("OctalIntegerLiteral").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//EnumBody ::= "{" EnumConstant* (,)? EnumBodyDeclarations? "}" 
			.addRule(Rule.withHead(Nonterminal.builder("EnumBody").build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).addSymbol(Star.builder(Nonterminal.builder("EnumConstant").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Character.builder(44).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("EnumBodyDeclarations").build()).build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//LeftHandSide ::= "(" LeftHandSide ")" 
			.addRule(Rule.withHead(Nonterminal.builder("LeftHandSide").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("LeftHandSide").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//LeftHandSide ::= FieldAccess 
			.addRule(Rule.withHead(Nonterminal.builder("LeftHandSide").build()).addSymbol(Nonterminal.builder("FieldAccess").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//LeftHandSide ::= ExpressionName 
			.addRule(Rule.withHead(Nonterminal.builder("LeftHandSide").build()).addSymbol(Nonterminal.builder("ExpressionName").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//LeftHandSide ::= ArrayAccess 
			.addRule(Rule.withHead(Nonterminal.builder("LeftHandSide").build()).addSymbol(Nonterminal.builder("ArrayAccess").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ShiftExpression ::= ShiftExpression "<<" AdditiveExpression 
			.addRule(Rule.withHead(Nonterminal.builder("ShiftExpression").build()).addSymbol(Nonterminal.builder("ShiftExpression").build()).addSymbol(Sequence.builder(Character.builder(60).build(), Character.builder(60).build()).build()).addSymbol(Nonterminal.builder("AdditiveExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ShiftExpression ::= ShiftExpression ">>" AdditiveExpression 
			.addRule(Rule.withHead(Nonterminal.builder("ShiftExpression").build()).addSymbol(Nonterminal.builder("ShiftExpression").build()).addSymbol(Sequence.builder(Character.builder(62).build(), Character.builder(62).build()).build()).addSymbol(Nonterminal.builder("AdditiveExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ShiftExpression ::= AdditiveExpression 
			.addRule(Rule.withHead(Nonterminal.builder("ShiftExpression").build()).addSymbol(Nonterminal.builder("AdditiveExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ShiftExpression ::= ShiftExpression ">>>" AdditiveExpression 
			.addRule(Rule.withHead(Nonterminal.builder("ShiftExpression").build()).addSymbol(Nonterminal.builder("ShiftExpression").build()).addSymbol(Sequence.builder(Character.builder(62).build(), Character.builder(62).build(), Character.builder(62).build()).build()).addSymbol(Nonterminal.builder("AdditiveExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Result ::= "void" 
			.addRule(Rule.withHead(Nonterminal.builder("Result").build()).addSymbol(Sequence.builder(Character.builder(118).build(), Character.builder(111).build(), Character.builder(105).build(), Character.builder(100).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Result ::= Type 
			.addRule(Rule.withHead(Nonterminal.builder("Result").build()).addSymbol(Nonterminal.builder("Type").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//NormalClassDeclaration ::= ClassModifier* "class" Identifier TypeParameters? ("extends" Type)? ("implements" TypeList)? ClassBody 
			.addRule(Rule.withHead(Nonterminal.builder("NormalClassDeclaration").build()).addSymbol(Star.builder(Nonterminal.builder("ClassModifier").build()).build()).addSymbol(Sequence.builder(Character.builder(99).build(), Character.builder(108).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeParameters").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(101).build(), Character.builder(120).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(100).build(), Character.builder(115).build()).build(), Nonterminal.builder("Type").build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(108).build(), Character.builder(101).build(), Character.builder(109).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(116).build(), Character.builder(115).build()).build(), Nonterminal.builder("TypeList").build()).build()).build()).addSymbol(Nonterminal.builder("ClassBody").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//NormalInterfaceDeclaration ::= InterfaceModifier* "interface" Identifier TypeParameters? ("extends" TypeList)? InterfaceBody 
			.addRule(Rule.withHead(Nonterminal.builder("NormalInterfaceDeclaration").build()).addSymbol(Star.builder(Nonterminal.builder("InterfaceModifier").build()).build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(102).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(101).build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeParameters").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(101).build(), Character.builder(120).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(110).build(), Character.builder(100).build(), Character.builder(115).build()).build(), Nonterminal.builder("TypeList").build()).build()).build()).addSymbol(Nonterminal.builder("InterfaceBody").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PreIncrementExpression ::= "++" UnaryExpression 
			.addRule(Rule.withHead(Nonterminal.builder("PreIncrementExpression").build()).addSymbol(Sequence.builder(Character.builder(43).build(), Character.builder(43).build()).build()).addSymbol(Nonterminal.builder("UnaryExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//TraditionalComment ::= "/*" CommentTail 
			.addRule(Rule.withHead(Nonterminal.builder("TraditionalComment").build()).addSymbol(Sequence.builder(Character.builder(47).build(), Character.builder(42).build()).build()).addSymbol(Nonterminal.builder("CommentTail").build()).build())
			//BinaryDigits ::= BinaryDigit BinaryDigitOrUnderscore* BinaryDigit 
			.addRule(Rule.withHead(Nonterminal.builder("BinaryDigits").build()).addSymbol(Nonterminal.builder("BinaryDigit").build()).addSymbol(Star.builder(Nonterminal.builder("BinaryDigitOrUnderscore").build()).build()).addSymbol(Nonterminal.builder("BinaryDigit").build()).build())
			//BinaryDigits ::= BinaryDigit 
			.addRule(Rule.withHead(Nonterminal.builder("BinaryDigits").build()).addSymbol(Nonterminal.builder("BinaryDigit").build()).build())
			//AnnotationTypeBody ::= "{" AnnotationTypeElementDeclaration* "}" 
			.addRule(Rule.withHead(Nonterminal.builder("AnnotationTypeBody").build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).addSymbol(Star.builder(Nonterminal.builder("AnnotationTypeElementDeclaration").build()).build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//OctalEscape ::= \ ZeroToThree OctalDigit OctalDigit 
			.addRule(Rule.withHead(Nonterminal.builder("OctalEscape").build()).addSymbol(Character.builder(92).build()).addSymbol(Nonterminal.builder("ZeroToThree").build()).addSymbol(Nonterminal.builder("OctalDigit").build()).addSymbol(Nonterminal.builder("OctalDigit").build()).build())
			//OctalEscape ::= \ OctalDigit OctalDigit 
			.addRule(Rule.withHead(Nonterminal.builder("OctalEscape").build()).addSymbol(Character.builder(92).build()).addSymbol(Nonterminal.builder("OctalDigit").build()).addSymbol(Nonterminal.builder("OctalDigit").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Alt.builder(CharacterRange.builder(48, 55).build()).build()))).build()).build())
			//OctalEscape ::= \ OctalDigit 
			.addRule(Rule.withHead(Nonterminal.builder("OctalEscape").build()).addSymbol(Character.builder(92).build()).addSymbol(Nonterminal.builder("OctalDigit").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Alt.builder(CharacterRange.builder(48, 55).build()).build()))).build()).build())
			//ConstructorModifier ::= "private" 
			.addRule(Rule.withHead(Nonterminal.builder("ConstructorModifier").build()).addSymbol(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(101).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ConstructorModifier ::= "public" 
			.addRule(Rule.withHead(Nonterminal.builder("ConstructorModifier").build()).addSymbol(Sequence.builder(Character.builder(112).build(), Character.builder(117).build(), Character.builder(98).build(), Character.builder(108).build(), Character.builder(105).build(), Character.builder(99).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ConstructorModifier ::= Annotation 
			.addRule(Rule.withHead(Nonterminal.builder("ConstructorModifier").build()).addSymbol(Nonterminal.builder("Annotation").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ConstructorModifier ::= "protected" 
			.addRule(Rule.withHead(Nonterminal.builder("ConstructorModifier").build()).addSymbol(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(99).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(100).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//OctalNumeral ::= 0 OctalDigits 
			.addRule(Rule.withHead(Nonterminal.builder("OctalNumeral").build()).addSymbol(Character.builder(48).build()).addSymbol(Nonterminal.builder("OctalDigits").build()).build())
			//OctalNumeral ::= 0 _+ OctalDigits 
			.addRule(Rule.withHead(Nonterminal.builder("OctalNumeral").build()).addSymbol(Character.builder(48).build()).addSymbol(Plus.builder(Character.builder(95).build()).build()).addSymbol(Nonterminal.builder("OctalDigits").build()).build())
			//ReferenceTypeNonArrayType ::= TypeDeclSpecifier TypeArguments? 
			.addRule(Rule.withHead(Nonterminal.builder("ReferenceTypeNonArrayType").build()).addSymbol(Nonterminal.builder("TypeDeclSpecifier").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeArguments").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//DecimalFloatingPointLiteral ::= . Digits ExponentPart? FloatTypeSuffix? 
			.addRule(Rule.withHead(Nonterminal.builder("DecimalFloatingPointLiteral").build()).addSymbol(Character.builder(46).build()).addSymbol(Nonterminal.builder("Digits").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ExponentPart").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("FloatTypeSuffix").build()).build()).build())
			//DecimalFloatingPointLiteral ::= Digits ExponentPart 
			.addRule(Rule.withHead(Nonterminal.builder("DecimalFloatingPointLiteral").build()).addSymbol(Nonterminal.builder("Digits").build()).addSymbol(Nonterminal.builder("ExponentPart").build()).build())
			//DecimalFloatingPointLiteral ::= Digits ExponentPart FloatTypeSuffix 
			.addRule(Rule.withHead(Nonterminal.builder("DecimalFloatingPointLiteral").build()).addSymbol(Nonterminal.builder("Digits").build()).addSymbol(Nonterminal.builder("ExponentPart").build()).addSymbol(Nonterminal.builder("FloatTypeSuffix").build()).build())
			//DecimalFloatingPointLiteral ::= Digits . Digits? ExponentPart? FloatTypeSuffix? 
			.addRule(Rule.withHead(Nonterminal.builder("DecimalFloatingPointLiteral").build()).addSymbol(Nonterminal.builder("Digits").build()).addSymbol(Character.builder(46).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Digits").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ExponentPart").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("FloatTypeSuffix").build()).build()).build())
			//DecimalFloatingPointLiteral ::= Digits FloatTypeSuffix 
			.addRule(Rule.withHead(Nonterminal.builder("DecimalFloatingPointLiteral").build()).addSymbol(Nonterminal.builder("Digits").build()).addSymbol(Nonterminal.builder("FloatTypeSuffix").build()).build())
			//Digits ::= Digit 
			.addRule(Rule.withHead(Nonterminal.builder("Digits").build()).addSymbol(Nonterminal.builder("Digit").build()).build())
			//Digits ::= Digit DigitOrUnderscore* Digit 
			.addRule(Rule.withHead(Nonterminal.builder("Digits").build()).addSymbol(Nonterminal.builder("Digit").build()).addSymbol(Star.builder(Nonterminal.builder("DigitOrUnderscore").build()).build()).addSymbol(Nonterminal.builder("Digit").build()).build())
			//DefaultValue ::= "default" ElementValue 
			.addRule(Rule.withHead(Nonterminal.builder("DefaultValue").build()).addSymbol(Sequence.builder(Character.builder(100).build(), Character.builder(101).build(), Character.builder(102).build(), Character.builder(97).build(), Character.builder(117).build(), Character.builder(108).build(), Character.builder(116).build()).build()).addSymbol(Nonterminal.builder("ElementValue").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ForInit ::= StatementExpression+ 
			.addRule(Rule.withHead(Nonterminal.builder("ForInit").build()).addSymbol(Plus.builder(Nonterminal.builder("StatementExpression").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ForInit ::= LocalVariableDeclaration 
			.addRule(Rule.withHead(Nonterminal.builder("ForInit").build()).addSymbol(Nonterminal.builder("LocalVariableDeclaration").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Block ::= "{" BlockStatement* "}" 
			.addRule(Rule.withHead(Nonterminal.builder("Block").build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).addSymbol(Star.builder(Nonterminal.builder("BlockStatement").build()).build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//DigitOrUnderscore ::= _ 
			.addRule(Rule.withHead(Nonterminal.builder("DigitOrUnderscore").build()).addSymbol(Character.builder(95).build()).build())
			//DigitOrUnderscore ::= Digit 
			.addRule(Rule.withHead(Nonterminal.builder("DigitOrUnderscore").build()).addSymbol(Nonterminal.builder("Digit").build()).build())
			//SwitchBlockStatementGroup ::= SwitchLabel+ BlockStatement+ 
			.addRule(Rule.withHead(Nonterminal.builder("SwitchBlockStatementGroup").build()).addSymbol(Plus.builder(Nonterminal.builder("SwitchLabel").build()).build()).addSymbol(Plus.builder(Nonterminal.builder("BlockStatement").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AnnotationTypeElementDeclaration ::= AnnotationTypeDeclaration 
			.addRule(Rule.withHead(Nonterminal.builder("AnnotationTypeElementDeclaration").build()).addSymbol(Nonterminal.builder("AnnotationTypeDeclaration").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AnnotationTypeElementDeclaration ::= InterfaceDeclaration 
			.addRule(Rule.withHead(Nonterminal.builder("AnnotationTypeElementDeclaration").build()).addSymbol(Nonterminal.builder("InterfaceDeclaration").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AnnotationTypeElementDeclaration ::= ConstantDeclaration 
			.addRule(Rule.withHead(Nonterminal.builder("AnnotationTypeElementDeclaration").build()).addSymbol(Nonterminal.builder("ConstantDeclaration").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AnnotationTypeElementDeclaration ::= ";" 
			.addRule(Rule.withHead(Nonterminal.builder("AnnotationTypeElementDeclaration").build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AnnotationTypeElementDeclaration ::= AbstractMethodModifier* Type Identifier "(" ")" ("[" "]")* DefaultValue? ";" 
			.addRule(Rule.withHead(Nonterminal.builder("AnnotationTypeElementDeclaration").build()).addSymbol(Star.builder(Nonterminal.builder("AbstractMethodModifier").build()).build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).addSymbol(Star.builder(Sequence.builder(Sequence.builder(Character.builder(91).build()).build(), Sequence.builder(Character.builder(93).build()).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("DefaultValue").build()).build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//AnnotationTypeElementDeclaration ::= ClassDeclaration 
			.addRule(Rule.withHead(Nonterminal.builder("AnnotationTypeElementDeclaration").build()).addSymbol(Nonterminal.builder("ClassDeclaration").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//VariableDeclarators ::= VariableDeclarator+ 
			.addRule(Rule.withHead(Nonterminal.builder("VariableDeclarators").build()).addSymbol(Plus.builder(Nonterminal.builder("VariableDeclarator").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//HexIntegerLiteral ::= HexNumeral IntegerTypeSuffix? 
			.addRule(Rule.withHead(Nonterminal.builder("HexIntegerLiteral").build()).addSymbol(Nonterminal.builder("HexNumeral").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("IntegerTypeSuffix").build()).build()).build())
			//InterfaceModifier ::= "abstract" 
			.addRule(Rule.withHead(Nonterminal.builder("InterfaceModifier").build()).addSymbol(Sequence.builder(Character.builder(97).build(), Character.builder(98).build(), Character.builder(115).build(), Character.builder(116).build(), Character.builder(114).build(), Character.builder(97).build(), Character.builder(99).build(), Character.builder(116).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//InterfaceModifier ::= "private" 
			.addRule(Rule.withHead(Nonterminal.builder("InterfaceModifier").build()).addSymbol(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(101).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//InterfaceModifier ::= "strictfp" 
			.addRule(Rule.withHead(Nonterminal.builder("InterfaceModifier").build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(99).build(), Character.builder(116).build(), Character.builder(102).build(), Character.builder(112).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//InterfaceModifier ::= "protected" 
			.addRule(Rule.withHead(Nonterminal.builder("InterfaceModifier").build()).addSymbol(Sequence.builder(Character.builder(112).build(), Character.builder(114).build(), Character.builder(111).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(99).build(), Character.builder(116).build(), Character.builder(101).build(), Character.builder(100).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//InterfaceModifier ::= Annotation 
			.addRule(Rule.withHead(Nonterminal.builder("InterfaceModifier").build()).addSymbol(Nonterminal.builder("Annotation").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//InterfaceModifier ::= "public" 
			.addRule(Rule.withHead(Nonterminal.builder("InterfaceModifier").build()).addSymbol(Sequence.builder(Character.builder(112).build(), Character.builder(117).build(), Character.builder(98).build(), Character.builder(108).build(), Character.builder(105).build(), Character.builder(99).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//InterfaceModifier ::= "static" 
			.addRule(Rule.withHead(Nonterminal.builder("InterfaceModifier").build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(105).build(), Character.builder(99).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PostfixExpression ::= PostIncrementExpression 
			.addRule(Rule.withHead(Nonterminal.builder("PostfixExpression").build()).addSymbol(Nonterminal.builder("PostIncrementExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PostfixExpression ::= PostDecrementExpression 
			.addRule(Rule.withHead(Nonterminal.builder("PostfixExpression").build()).addSymbol(Nonterminal.builder("PostDecrementExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PostfixExpression ::= Primary 
			.addRule(Rule.withHead(Nonterminal.builder("PostfixExpression").build()).addSymbol(Nonterminal.builder("Primary").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PostfixExpression ::= ExpressionName 
			.addRule(Rule.withHead(Nonterminal.builder("PostfixExpression").build()).addSymbol(Nonterminal.builder("ExpressionName").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//SingleCharacter ::= InputCharacter 
			.addRule(Rule.withHead(Nonterminal.builder("SingleCharacter").build()).addSymbol(Nonterminal.builder("InputCharacter").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Character.builder(39).build()).build()))).build()).build())
			//InterfaceMemberDeclaration ::= InterfaceDeclaration 
			.addRule(Rule.withHead(Nonterminal.builder("InterfaceMemberDeclaration").build()).addSymbol(Nonterminal.builder("InterfaceDeclaration").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//InterfaceMemberDeclaration ::= ConstantDeclaration 
			.addRule(Rule.withHead(Nonterminal.builder("InterfaceMemberDeclaration").build()).addSymbol(Nonterminal.builder("ConstantDeclaration").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//InterfaceMemberDeclaration ::= ";" 
			.addRule(Rule.withHead(Nonterminal.builder("InterfaceMemberDeclaration").build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//InterfaceMemberDeclaration ::= AbstractMethodDeclaration 
			.addRule(Rule.withHead(Nonterminal.builder("InterfaceMemberDeclaration").build()).addSymbol(Nonterminal.builder("AbstractMethodDeclaration").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//InterfaceMemberDeclaration ::= ClassDeclaration 
			.addRule(Rule.withHead(Nonterminal.builder("InterfaceMemberDeclaration").build()).addSymbol(Nonterminal.builder("ClassDeclaration").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//InclusiveOrExpression ::= ExclusiveOrExpression 
			.addRule(Rule.withHead(Nonterminal.builder("InclusiveOrExpression").build()).addSymbol(Nonterminal.builder("ExclusiveOrExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//InclusiveOrExpression ::= InclusiveOrExpression "|" ExclusiveOrExpression 
			.addRule(Rule.withHead(Nonterminal.builder("InclusiveOrExpression").build()).addSymbol(Nonterminal.builder("InclusiveOrExpression").build()).addSymbol(Sequence.builder(Character.builder(124).build()).build()).addSymbol(Nonterminal.builder("ExclusiveOrExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//NonWildTypeArguments ::= "<" ReferenceType+ ">" 
			.addRule(Rule.withHead(Nonterminal.builder("NonWildTypeArguments").build()).addSymbol(Sequence.builder(Character.builder(60).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("ReferenceType").build()).build()).addSymbol(Sequence.builder(Character.builder(62).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PostIncrementExpression ::= PostfixExpression "++" 
			.addRule(Rule.withHead(Nonterminal.builder("PostIncrementExpression").build()).addSymbol(Nonterminal.builder("PostfixExpression").build()).addSymbol(Sequence.builder(Character.builder(43).build(), Character.builder(43).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Literal ::= CharacterLiteral 
			.addRule(Rule.withHead(Nonterminal.builder("Literal").build()).addSymbol(Nonterminal.builder("CharacterLiteral").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Literal ::= NullLiteral 
			.addRule(Rule.withHead(Nonterminal.builder("Literal").build()).addSymbol(Nonterminal.builder("NullLiteral").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Literal ::= IntegerLiteral 
			.addRule(Rule.withHead(Nonterminal.builder("Literal").build()).addSymbol(Nonterminal.builder("IntegerLiteral").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Literal ::= StringLiteral 
			.addRule(Rule.withHead(Nonterminal.builder("Literal").build()).addSymbol(Nonterminal.builder("StringLiteral").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Literal ::= BooleanLiteral 
			.addRule(Rule.withHead(Nonterminal.builder("Literal").build()).addSymbol(Nonterminal.builder("BooleanLiteral").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Literal ::= FloatingPointLiteral 
			.addRule(Rule.withHead(Nonterminal.builder("Literal").build()).addSymbol(Nonterminal.builder("FloatingPointLiteral").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ClassMemberDeclaration ::= InterfaceDeclaration 
			.addRule(Rule.withHead(Nonterminal.builder("ClassMemberDeclaration").build()).addSymbol(Nonterminal.builder("InterfaceDeclaration").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ClassMemberDeclaration ::= ClassDeclaration 
			.addRule(Rule.withHead(Nonterminal.builder("ClassMemberDeclaration").build()).addSymbol(Nonterminal.builder("ClassDeclaration").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ClassMemberDeclaration ::= MethodDeclaration 
			.addRule(Rule.withHead(Nonterminal.builder("ClassMemberDeclaration").build()).addSymbol(Nonterminal.builder("MethodDeclaration").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ClassMemberDeclaration ::= FieldDeclaration 
			.addRule(Rule.withHead(Nonterminal.builder("ClassMemberDeclaration").build()).addSymbol(Nonterminal.builder("FieldDeclaration").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ClassMemberDeclaration ::= ";" 
			.addRule(Rule.withHead(Nonterminal.builder("ClassMemberDeclaration").build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PrimitiveType ::= "float" 
			.addRule(Rule.withHead(Nonterminal.builder("PrimitiveType").build()).addSymbol(Sequence.builder(Character.builder(102).build(), Character.builder(108).build(), Character.builder(111).build(), Character.builder(97).build(), Character.builder(116).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PrimitiveType ::= "long" 
			.addRule(Rule.withHead(Nonterminal.builder("PrimitiveType").build()).addSymbol(Sequence.builder(Character.builder(108).build(), Character.builder(111).build(), Character.builder(110).build(), Character.builder(103).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PrimitiveType ::= "double" 
			.addRule(Rule.withHead(Nonterminal.builder("PrimitiveType").build()).addSymbol(Sequence.builder(Character.builder(100).build(), Character.builder(111).build(), Character.builder(117).build(), Character.builder(98).build(), Character.builder(108).build(), Character.builder(101).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PrimitiveType ::= "int" 
			.addRule(Rule.withHead(Nonterminal.builder("PrimitiveType").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(116).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PrimitiveType ::= "char" 
			.addRule(Rule.withHead(Nonterminal.builder("PrimitiveType").build()).addSymbol(Sequence.builder(Character.builder(99).build(), Character.builder(104).build(), Character.builder(97).build(), Character.builder(114).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PrimitiveType ::= "boolean" 
			.addRule(Rule.withHead(Nonterminal.builder("PrimitiveType").build()).addSymbol(Sequence.builder(Character.builder(98).build(), Character.builder(111).build(), Character.builder(111).build(), Character.builder(108).build(), Character.builder(101).build(), Character.builder(97).build(), Character.builder(110).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PrimitiveType ::= "short" 
			.addRule(Rule.withHead(Nonterminal.builder("PrimitiveType").build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(104).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//PrimitiveType ::= "byte" 
			.addRule(Rule.withHead(Nonterminal.builder("PrimitiveType").build()).addSymbol(Sequence.builder(Character.builder(98).build(), Character.builder(121).build(), Character.builder(116).build(), Character.builder(101).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//VariableInitializer ::= Expression 
			.addRule(Rule.withHead(Nonterminal.builder("VariableInitializer").build()).addSymbol(Nonterminal.builder("Expression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//VariableInitializer ::= ArrayInitializer 
			.addRule(Rule.withHead(Nonterminal.builder("VariableInitializer").build()).addSymbol(Nonterminal.builder("ArrayInitializer").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//NotStar ::= InputCharacter 
			.addRule(Rule.withHead(Nonterminal.builder("NotStar").build()).addSymbol(Nonterminal.builder("InputCharacter").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Character.builder(42).build()).build()))).build()).build())
			//NotStar ::= LineTerminator 
			.addRule(Rule.withHead(Nonterminal.builder("NotStar").build()).addSymbol(Nonterminal.builder("LineTerminator").build()).build())
			//ExplicitConstructorInvocation ::= NonWildTypeArguments? "this" "(" ArgumentList? ")" ";" 
			.addRule(Rule.withHead(Nonterminal.builder("ExplicitConstructorInvocation").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("NonWildTypeArguments").build()).build()).addSymbol(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(105).build(), Character.builder(115).build()).build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ArgumentList").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ExplicitConstructorInvocation ::= Primary "." NonWildTypeArguments? "super" "(" ArgumentList? ")" ";" 
			.addRule(Rule.withHead(Nonterminal.builder("ExplicitConstructorInvocation").build()).addSymbol(Nonterminal.builder("Primary").build()).addSymbol(Sequence.builder(Character.builder(46).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("NonWildTypeArguments").build()).build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(117).build(), Character.builder(112).build(), Character.builder(101).build(), Character.builder(114).build()).build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ArgumentList").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ExplicitConstructorInvocation ::= NonWildTypeArguments? "super" "(" ArgumentList? ")" ";" 
			.addRule(Rule.withHead(Nonterminal.builder("ExplicitConstructorInvocation").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("NonWildTypeArguments").build()).build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(117).build(), Character.builder(112).build(), Character.builder(101).build(), Character.builder(114).build()).build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ArgumentList").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ExceptionType ::= TypeName 
			.addRule(Rule.withHead(Nonterminal.builder("ExceptionType").build()).addSymbol(Nonterminal.builder("TypeName").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//CastExpression ::= "(" ReferenceType ")" UnaryExpressionNotPlusMinus 
			.addRule(Rule.withHead(Nonterminal.builder("CastExpression").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("ReferenceType").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).addSymbol(Nonterminal.builder("UnaryExpressionNotPlusMinus").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//CastExpression ::= "(" PrimitiveType ")" UnaryExpression 
			.addRule(Rule.withHead(Nonterminal.builder("CastExpression").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("PrimitiveType").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).addSymbol(Nonterminal.builder("UnaryExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//CompilationUnit ::= PackageDeclaration? ImportDeclaration* TypeDeclaration* 
			.addRule(Rule.withHead(Nonterminal.builder("CompilationUnit").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("PackageDeclaration").build()).build()).addSymbol(Star.builder(Nonterminal.builder("ImportDeclaration").build()).build()).addSymbol(Star.builder(Nonterminal.builder("TypeDeclaration").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//CompilationUnit ::= PackageDeclaration? ImportDeclaration* TypeDeclaration* 
			.addRule(Rule.withHead(Nonterminal.builder("CompilationUnit").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("PackageDeclaration").build()).build()).addSymbol(Star.builder(Nonterminal.builder("ImportDeclaration").build()).build()).addSymbol(Star.builder(Nonterminal.builder("TypeDeclaration").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Finally ::= "finally" Block 
			.addRule(Rule.withHead(Nonterminal.builder("Finally").build()).addSymbol(Sequence.builder(Character.builder(102).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(97).build(), Character.builder(108).build(), Character.builder(108).build(), Character.builder(121).build()).build()).addSymbol(Nonterminal.builder("Block").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//BinaryExponent ::= BinaryExponentIndicator SignedInteger 
			.addRule(Rule.withHead(Nonterminal.builder("BinaryExponent").build()).addSymbol(Nonterminal.builder("BinaryExponentIndicator").build()).addSymbol(Nonterminal.builder("SignedInteger").build()).build())
			//BinaryDigit ::= (0-1) 
			.addRule(Rule.withHead(Nonterminal.builder("BinaryDigit").build()).addSymbol(Alt.builder(CharacterRange.builder(48, 49).build()).build()).build())
			//TypeDeclSpecifier ::= Identifier (TypeArguments? "." Identifier)* 
			.addRule(Rule.withHead(Nonterminal.builder("TypeDeclSpecifier").build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(Star.builder(Sequence.builder(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeArguments").build()).build(), Sequence.builder(Character.builder(46).build()).build(), Nonterminal.builder("Identifier").build()).build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//Expression ::= AssignmentExpression 
			.addRule(Rule.withHead(Nonterminal.builder("Expression").build()).addSymbol(Nonterminal.builder("AssignmentExpression").build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ClassInstanceCreationExpression ::= "new" TypeArguments? TypeDeclSpecifier TypeArgumentsOrDiamond? "(" ArgumentList? ")" ClassBody? 
			.addRule(Rule.withHead(Nonterminal.builder("ClassInstanceCreationExpression").build()).addSymbol(Sequence.builder(Character.builder(110).build(), Character.builder(101).build(), Character.builder(119).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeArguments").build()).build()).addSymbol(Nonterminal.builder("TypeDeclSpecifier").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeArgumentsOrDiamond").build()).build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ArgumentList").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ClassBody").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//ClassInstanceCreationExpression ::= (Primary | QualifiedIdentifier) "." "new" TypeArguments? Identifier TypeArgumentsOrDiamond? "(" ArgumentList? ")" ClassBody? 
			.addRule(Rule.withHead(Nonterminal.builder("ClassInstanceCreationExpression").build()).addSymbol(Alt.builder(Nonterminal.builder("Primary").build(), Nonterminal.builder("QualifiedIdentifier").build()).build()).addSymbol(Sequence.builder(Character.builder(46).build()).build()).addSymbol(Sequence.builder(Character.builder(110).build(), Character.builder(101).build(), Character.builder(119).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeArguments").build()).build()).addSymbol(Nonterminal.builder("Identifier").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("TypeArgumentsOrDiamond").build()).build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ArgumentList").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ClassBody").build()).build()).setLayout(Nonterminal.builder("Layout").build()).build())
			//HexDigitOrUnderscore ::= _ 
			.addRule(Rule.withHead(Nonterminal.builder("HexDigitOrUnderscore").build()).addSymbol(Character.builder(95).build()).build())
			//HexDigitOrUnderscore ::= HexDigit 
			.addRule(Rule.withHead(Nonterminal.builder("HexDigitOrUnderscore").build()).addSymbol(Nonterminal.builder("HexDigit").build()).build())
			.build();
}
