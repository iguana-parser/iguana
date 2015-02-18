package org.jgll.grammar;

import java.util.Arrays;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.condition.ConditionType;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Terminal;
import org.jgll.regex.Alt;
import org.jgll.regex.Plus;
import org.jgll.regex.Sequence;
import org.jgll.regex.Star;

import static org.jgll.grammar.symbol.LayoutStrategy.*;

import com.google.common.collect.Sets;

public class Haskell {

	public static Grammar grammar =	
		Grammar.builder()
		.setLayout(Nonterminal.builder("Whitespace").build())
		// CDecls ::= ({) CDecl* (}) 
		.addRule(Rule.withHead(Nonterminal.builder("CDecls").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build()).addSymbol(Star.builder(Nonterminal.builder("CDecl").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build())).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).build())
		// QVarId ::= (ModId (.))? VarId 
		.addRule(Rule.withHead(Nonterminal.builder("QVarId").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("ModId").build(), Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).build()).build()).addSymbol(Nonterminal.builder("VarId").build()).setLayoutStrategy(NO_LAYOUT).build())
		// GDPat ::= Guards (- >) Exp GDPat? 
		.addRule(Rule.withHead(Nonterminal.builder("GDPat").build()).addSymbol(Nonterminal.builder("Guards").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build(), Character.builder(62).build()).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("GDPat").build()).build()).build())
		// ImpSpec ::= (h i d i n g) (() Import* (,)? ()) 
		.addRule(Rule.withHead(Nonterminal.builder("ImpSpec").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(104).build(), Character.builder(105).build(), Character.builder(100).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(103).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Star.builder(Nonterminal.builder("Import").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build())).build()).addSymbol(org.jgll.regex.Opt.builder(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// ImpSpec ::= (() Import* (,)? ()) 
		.addRule(Rule.withHead(Nonterminal.builder("ImpSpec").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Star.builder(Nonterminal.builder("Import").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build())).build()).addSymbol(org.jgll.regex.Opt.builder(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// NComment ::= ({ -) (NComment | ({) | (-) | (\u0001-, | .-z | |-\u10FFFF))* (- }) 
		.addRule(Rule.withHead(Nonterminal.builder("NComment").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build(), Character.builder(45).build()).build()).build()).addSymbol(Star.builder(Alt.builder(Nonterminal.builder("NComment").build(), Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Character.builder(45).build()))).build(), Terminal.builder(Sequence.builder(Character.builder(45).build()).build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Character.builder(125).build()))).build(), Alt.builder(CharacterRange.builder(1, 44).build(), CharacterRange.builder(46, 122).build(), CharacterRange.builder(124, 1114111).build()).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build(), Character.builder(125).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// TyCls ::= ConId 
		.addRule(Rule.withHead(Nonterminal.builder("TyCls").build()).addSymbol(Nonterminal.builder("ConId").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Class ::= QTyCls TyVar 
		.addRule(Rule.withHead(Nonterminal.builder("Class").build()).addSymbol(Nonterminal.builder("QTyCls").build()).addSymbol(Nonterminal.builder("TyVar").build()).build())
		// Class ::= QTyCls (() TyVar AType+ ()) 
		.addRule(Rule.withHead(Nonterminal.builder("Class").build()).addSymbol(Nonterminal.builder("QTyCls").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("TyVar").build()).addSymbol(Plus.builder(Nonterminal.builder("AType").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// Guards ::= (|) Guard+ 
		.addRule(Rule.withHead(Nonterminal.builder("Guards").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(124).build()).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("Guard").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build())).build()).build())
		// GConSym ::= QConSym 
		.addRule(Rule.withHead(Nonterminal.builder("GConSym").build()).addSymbol(Nonterminal.builder("QConSym").build()).build())
		// GConSym ::= (:) 
		.addRule(Rule.withHead(Nonterminal.builder("GConSym").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(58).build()).build()).build()).build())
		// Inst ::= (() GTyCon TyVar* ()) 
		.addRule(Rule.withHead(Nonterminal.builder("Inst").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("GTyCon").build()).addSymbol(Star.builder(Nonterminal.builder("TyVar").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// Inst ::= GTyCon 
		.addRule(Rule.withHead(Nonterminal.builder("Inst").build()).addSymbol(Nonterminal.builder("GTyCon").build()).build())
		// Inst ::= (() TyVar (- >) TyVar ()) 
		.addRule(Rule.withHead(Nonterminal.builder("Inst").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("TyVar").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build(), Character.builder(62).build()).build()).build()).addSymbol(Nonterminal.builder("TyVar").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// Inst ::= (() TyVar (,) TyVar+ ()) 
		.addRule(Rule.withHead(Nonterminal.builder("Inst").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("TyVar").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("TyVar").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build())).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// Inst ::= ([) TyVar (]) 
		.addRule(Rule.withHead(Nonterminal.builder("Inst").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(91).build()).build()).build()).addSymbol(Nonterminal.builder("TyVar").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(93).build()).build()).build()).build())
		// QCon ::= (() GConSym ()) 
		.addRule(Rule.withHead(Nonterminal.builder("QCon").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("GConSym").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// QCon ::= QConId 
		.addRule(Rule.withHead(Nonterminal.builder("QCon").build()).addSymbol(Nonterminal.builder("QConId").build()).build())
		// WhiteChar ::=  
		.addRule(Rule.withHead(Nonterminal.builder("WhiteChar").build()).addSymbol(Alt.builder(CharacterRange.builder(9, 11).build(), CharacterRange.builder(13, 13).build(), CharacterRange.builder(32, 32).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// SimpleType ::= TyCon TyVar* 
		.addRule(Rule.withHead(Nonterminal.builder("SimpleType").build()).addSymbol(Nonterminal.builder("TyCon").build()).addSymbol(Star.builder(Nonterminal.builder("TyVar").build()).build()).build())
		// Small ::= AscSmall 
		.addRule(Rule.withHead(Nonterminal.builder("Small").build()).addSymbol(Nonterminal.builder("AscSmall").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Small ::= (_) 
		.addRule(Rule.withHead(Nonterminal.builder("Small").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(95).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// FieldDecl ::= Vars (: :) (Type | ((!) AType)) 
		.addRule(Rule.withHead(Nonterminal.builder("FieldDecl").build()).addSymbol(Nonterminal.builder("Vars").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(58).build(), Character.builder(58).build()).build()).build()).addSymbol(Alt.builder(Nonterminal.builder("Type").build(), Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(33).build()).build()).build(), Nonterminal.builder("AType").build()).build()).build()).build())
		// GTyCon ::= QTyCon 
		.addRule(Rule.withHead(Nonterminal.builder("GTyCon").build()).addSymbol(Nonterminal.builder("QTyCon").build()).build())
		// GTyCon ::= (() (- >) ()) 
		.addRule(Rule.withHead(Nonterminal.builder("GTyCon").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build(), Character.builder(62).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// GTyCon ::= (() ()) 
		.addRule(Rule.withHead(Nonterminal.builder("GTyCon").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// GTyCon ::= (() (,)+ ()) 
		.addRule(Rule.withHead(Nonterminal.builder("GTyCon").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Plus.builder(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// GTyCon ::= ([) (]) 
		.addRule(Rule.withHead(Nonterminal.builder("GTyCon").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(91).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(93).build()).build()).build()).build())
		// DClass ::= QTyCls 
		.addRule(Rule.withHead(Nonterminal.builder("DClass").build()).addSymbol(Nonterminal.builder("QTyCls").build()).build())
		// Stmt ::= Pat (< -) Exp (;) 
		.addRule(Rule.withHead(Nonterminal.builder("Stmt").build()).addSymbol(Nonterminal.builder("Pat").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(60).build(), Character.builder(45).build()).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// Stmt ::= Exp (;) 
		.addRule(Rule.withHead(Nonterminal.builder("Stmt").build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// Stmt ::= (l e t) Decls (;) 
		.addRule(Rule.withHead(Nonterminal.builder("Stmt").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(108).build(), Character.builder(101).build(), Character.builder(116).build()).build()).build()).addSymbol(Nonterminal.builder("Decls").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// Stmt ::= (;) 
		.addRule(Rule.withHead(Nonterminal.builder("Stmt").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		// Hexadecimal ::= HexIt HexIt* 
		.addRule(Rule.withHead(Nonterminal.builder("Hexadecimal").build()).addSymbol(Nonterminal.builder("HexIt").build()).addSymbol(Star.builder(Nonterminal.builder("HexIt").build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Context ::= (() Class* ()) 
		.addRule(Rule.withHead(Nonterminal.builder("Context").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Star.builder(Nonterminal.builder("Class").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build())).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// Context ::= Class 
		.addRule(Rule.withHead(Nonterminal.builder("Context").build()).addSymbol(Nonterminal.builder("Class").build()).build())
		// Large ::= AscLarge 
		.addRule(Rule.withHead(Nonterminal.builder("Large").build()).addSymbol(Nonterminal.builder("AscLarge").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Guard ::= (l e t) Decls 
		.addRule(Rule.withHead(Nonterminal.builder("Guard").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(108).build(), Character.builder(101).build(), Character.builder(116).build()).build()).build()).addSymbol(Nonterminal.builder("Decls").build()).build())
		// Guard ::= Pat (< -) InfixExp 
		.addRule(Rule.withHead(Nonterminal.builder("Guard").build()).addSymbol(Nonterminal.builder("Pat").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(60).build(), Character.builder(45).build()).build()).build()).addSymbol(Nonterminal.builder("InfixExp").build()).build())
		// Guard ::= InfixExp 
		.addRule(Rule.withHead(Nonterminal.builder("Guard").build()).addSymbol(Nonterminal.builder("InfixExp").build()).build())
		// SimpleClass ::= QTyCls TyVar 
		.addRule(Rule.withHead(Nonterminal.builder("SimpleClass").build()).addSymbol(Nonterminal.builder("QTyCls").build()).addSymbol(Nonterminal.builder("TyVar").build()).build())
		// AscSmall ::= (a-z) 
		.addRule(Rule.withHead(Nonterminal.builder("AscSmall").build()).addSymbol(Alt.builder(CharacterRange.builder(97, 122).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// QVarOp ::= (`) QVarId (`) 
		.addRule(Rule.withHead(Nonterminal.builder("QVarOp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(96).build()).build()).build()).addSymbol(Nonterminal.builder("QVarId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(96).build()).build()).build()).build())
		// QVarOp ::= QVarSym 
		.addRule(Rule.withHead(Nonterminal.builder("QVarOp").build()).addSymbol(Nonterminal.builder("QVarSym").build()).build())
		// $default$ ::= 
		.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).setLayoutStrategy(NO_LAYOUT).build())
		// FExp ::= FExp? AExp 
		.addRule(Rule.withHead(Nonterminal.builder("FExp").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("FExp").build()).build()).addSymbol(Nonterminal.builder("AExp").build()).build())
		// QTyCls ::= (ModId (.))? TyCls 
		.addRule(Rule.withHead(Nonterminal.builder("QTyCls").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("ModId").build(), Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).build()).build()).addSymbol(Nonterminal.builder("TyCls").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Decimal ::= Digit Digit* 
		.addRule(Rule.withHead(Nonterminal.builder("Decimal").build()).addSymbol(Nonterminal.builder("Digit").build()).addSymbol(Star.builder(Nonterminal.builder("Digit").build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// SContext ::= (() SimpleClass* ()) 
		.addRule(Rule.withHead(Nonterminal.builder("SContext").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Star.builder(Nonterminal.builder("SimpleClass").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build())).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// SContext ::= SimpleClass 
		.addRule(Rule.withHead(Nonterminal.builder("SContext").build()).addSymbol(Nonterminal.builder("SimpleClass").build()).build())
		// Export ::= QVar 
		.addRule(Rule.withHead(Nonterminal.builder("Export").build()).addSymbol(Nonterminal.builder("QVar").build()).build())
		// Export ::= Module ModId 
		.addRule(Rule.withHead(Nonterminal.builder("Export").build()).addSymbol(Nonterminal.builder("Module").build()).addSymbol(Nonterminal.builder("ModId").build()).build())
		// Export ::= ConId (((() (. .) ())) | ((() (QVar | Con)* ())))? 
		.addRule(Rule.withHead(Nonterminal.builder("Export").build()).addSymbol(Nonterminal.builder("ConId").build()).addSymbol(org.jgll.regex.Opt.builder(Alt.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(46).build(), Character.builder(46).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build(), Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build(), Star.builder(Alt.builder(Nonterminal.builder("QVar").build(), Nonterminal.builder("Con").build()).build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build())).build(), Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build()).build()).build()).build())
		// ImpDecls ::= ImpDecl+ 
		.addRule(Rule.withHead(Nonterminal.builder("ImpDecls").build()).addSymbol(Plus.builder(Nonterminal.builder("ImpDecl").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build())).build()).build())
		// Space ::= (\u0020) 
		.addRule(Rule.withHead(Nonterminal.builder("Space").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(32).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ImpDecl ::= (i m p o r t) (q u a l i f i e d)? ModId ((a s) ModId)? ImpSpec? 
		.addRule(Rule.withHead(Nonterminal.builder("ImpDecl").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Terminal.builder(Sequence.builder(Character.builder(113).build(), Character.builder(117).build(), Character.builder(97).build(), Character.builder(108).build(), Character.builder(105).build(), Character.builder(102).build(), Character.builder(105).build(), Character.builder(101).build(), Character.builder(100).build()).build()).build()).build()).addSymbol(Nonterminal.builder("ModId").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(97).build(), Character.builder(115).build()).build()).build(), Nonterminal.builder("ModId").build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ImpSpec").build()).build()).build())
		// ImpDecl ::= 
		.addRule(Rule.withHead(Nonterminal.builder("ImpDecl").build()).build())
		// Exp ::= InfixExp (: :) (Context (= >))? Type 
		.addRule(Rule.withHead(Nonterminal.builder("Exp").build()).addSymbol(Nonterminal.builder("InfixExp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(58).build(), Character.builder(58).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("Context").build(), Terminal.builder(Sequence.builder(Character.builder(61).build(), Character.builder(62).build()).build()).build()).build()).build()).addSymbol(Nonterminal.builder("Type").build()).build())
		// Exp ::= InfixExp 
		.addRule(Rule.withHead(Nonterminal.builder("Exp").build()).addSymbol(Nonterminal.builder("InfixExp").build()).build())
		// Symbol ::= AscSymbol 
		.addRule(Rule.withHead(Nonterminal.builder("Symbol").build()).addSymbol(Nonterminal.builder("AscSymbol").build()).setLayoutStrategy(NO_LAYOUT).build())
		// NewConstr ::= Con AType 
		.addRule(Rule.withHead(Nonterminal.builder("NewConstr").build()).addSymbol(Nonterminal.builder("Con").build()).addSymbol(Nonterminal.builder("AType").build()).build())
		// NewConstr ::= Con ({) Var (: :) Type (}) 
		.addRule(Rule.withHead(Nonterminal.builder("NewConstr").build()).addSymbol(Nonterminal.builder("Con").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build()).addSymbol(Nonterminal.builder("Var").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(58).build(), Character.builder(58).build()).build()).build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).build())
		// Ops ::= Op+ 
		.addRule(Rule.withHead(Nonterminal.builder("Ops").build()).addSymbol(Plus.builder(Nonterminal.builder("Op").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build())).build()).build())
		// NewLine ::=  
		.addRule(Rule.withHead(Nonterminal.builder("NewLine").build()).addSymbol(Alt.builder(CharacterRange.builder(10, 10).build(), CharacterRange.builder(13, 13).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ModId ::= (ConId (.))* ConId 
		.addRule(Rule.withHead(Nonterminal.builder("ModId").build()).addSymbol(Star.builder(Sequence.builder(Nonterminal.builder("ConId").build(), Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).build()).build()).addSymbol(Nonterminal.builder("ConId").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Digit ::= AscDigit 
		.addRule(Rule.withHead(Nonterminal.builder("Digit").build()).addSymbol(Nonterminal.builder("AscDigit").build()).setLayoutStrategy(NO_LAYOUT).build())
		// AscDigit ::= (0-9) 
		.addRule(Rule.withHead(Nonterminal.builder("AscDigit").build()).addSymbol(Alt.builder(CharacterRange.builder(48, 57).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// AscLarge ::= (A-Z) 
		.addRule(Rule.withHead(Nonterminal.builder("AscLarge").build()).addSymbol(Alt.builder(CharacterRange.builder(65, 90).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Deriving ::= (d e r i v i n g) (DClass | ((() DClass* ()))) 
		.addRule(Rule.withHead(Nonterminal.builder("Deriving").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(100).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(103).build()).build()).build()).addSymbol(Alt.builder(Nonterminal.builder("DClass").build(), Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build(), Star.builder(Nonterminal.builder("DClass").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build())).build(), Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build()).build()).build())
		// FAType ::= QTyCon AType* 
		.addRule(Rule.withHead(Nonterminal.builder("FAType").build()).addSymbol(Nonterminal.builder("QTyCon").build()).addSymbol(Star.builder(Nonterminal.builder("AType").build()).build()).build())
		// AscSymbol ::= (\u22C6) 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(8902).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// AscSymbol ::= (=) 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(61).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// AscSymbol ::= (-) 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// AscSymbol ::= (%) 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(37).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// AscSymbol ::= (!) 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(33).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// AscSymbol ::= (>) 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(62).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// AscSymbol ::= (:) 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(58).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// AscSymbol ::= (.) 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// AscSymbol ::= (*) 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(42).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// AscSymbol ::= (&) 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(38).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// AscSymbol ::= (?) 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(63).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// AscSymbol ::= (/) 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(47).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// AscSymbol ::= (+) 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(43).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// AscSymbol ::= (#) 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(35).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// AscSymbol ::= (<) 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(60).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// AscSymbol ::= ($) 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(36).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// AscSymbol ::= (^) 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(94).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// AscSymbol ::= (\) 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(92).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// AscSymbol ::= (@) 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(64).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// AscSymbol ::= (~) 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(126).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// AscSymbol ::= (|) 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(124).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Type ::= BType ((- >) Type)? 
		.addRule(Rule.withHead(Nonterminal.builder("Type").build()).addSymbol(Nonterminal.builder("BType").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(45).build(), Character.builder(62).build()).build()).build(), Nonterminal.builder("Type").build()).build()).build()).build())
		// Alt ::= 
		.addRule(Rule.withHead(Nonterminal.builder("Alt").build()).build())
		// Alt ::= Pat GDPat ((w h e r e) Decls)? 
		.addRule(Rule.withHead(Nonterminal.builder("Alt").build()).addSymbol(Nonterminal.builder("Pat").build()).addSymbol(Nonterminal.builder("GDPat").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(101).build()).build()).build(), Nonterminal.builder("Decls").build()).build()).build()).build())
		// Alt ::= Pat (- >) Exp ((w h e r e) Decls)? 
		.addRule(Rule.withHead(Nonterminal.builder("Alt").build()).addSymbol(Nonterminal.builder("Pat").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build(), Character.builder(62).build()).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(101).build()).build()).build(), Nonterminal.builder("Decls").build()).build()).build()).build())
		// Cntrl ::= AscLarge 
		.addRule(Rule.withHead(Nonterminal.builder("Cntrl").build()).addSymbol(Nonterminal.builder("AscLarge").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Cntrl ::= (^) 
		.addRule(Rule.withHead(Nonterminal.builder("Cntrl").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(94).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Cntrl ::= (]) 
		.addRule(Rule.withHead(Nonterminal.builder("Cntrl").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(93).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Cntrl ::= (@) 
		.addRule(Rule.withHead(Nonterminal.builder("Cntrl").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(64).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Cntrl ::= (\) 
		.addRule(Rule.withHead(Nonterminal.builder("Cntrl").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(92).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Cntrl ::= (_) 
		.addRule(Rule.withHead(Nonterminal.builder("Cntrl").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(95).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Cntrl ::= ([) 
		.addRule(Rule.withHead(Nonterminal.builder("Cntrl").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(91).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// FType ::= FRType 
		.addRule(Rule.withHead(Nonterminal.builder("FType").build()).addSymbol(Nonterminal.builder("FRType").build()).build())
		// FType ::= FAType (- >) FType 
		.addRule(Rule.withHead(Nonterminal.builder("FType").build()).addSymbol(Nonterminal.builder("FAType").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build(), Character.builder(62).build()).build()).build()).addSymbol(Nonterminal.builder("FType").build()).build())
		// GCon ::= ([) (]) 
		.addRule(Rule.withHead(Nonterminal.builder("GCon").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(91).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(93).build()).build()).build()).build())
		// GCon ::= QCon 
		.addRule(Rule.withHead(Nonterminal.builder("GCon").build()).addSymbol(Nonterminal.builder("QCon").build()).build())
		// GCon ::= (() ()) 
		.addRule(Rule.withHead(Nonterminal.builder("GCon").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// GCon ::= (() (,)+ ()) 
		.addRule(Rule.withHead(Nonterminal.builder("GCon").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Plus.builder(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// Octal ::= OctIt OctIt* 
		.addRule(Rule.withHead(Nonterminal.builder("Octal").build()).addSymbol(Nonterminal.builder("OctIt").build()).addSymbol(Star.builder(Nonterminal.builder("OctIt").build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// WhiteStuff ::= WhiteChar 
		.addRule(Rule.withHead(Nonterminal.builder("WhiteStuff").build()).addSymbol(Nonterminal.builder("WhiteChar").build()).setLayoutStrategy(NO_LAYOUT).build())
		// WhiteStuff ::= NComment 
		.addRule(Rule.withHead(Nonterminal.builder("WhiteStuff").build()).addSymbol(Nonterminal.builder("NComment").build()).setLayoutStrategy(NO_LAYOUT).build())
		// WhiteStuff ::= Comment 
		.addRule(Rule.withHead(Nonterminal.builder("WhiteStuff").build()).addSymbol(Nonterminal.builder("Comment").build()).setLayoutStrategy(NO_LAYOUT).build())
		// AExp ::= AExp ({) FBind+ (}) 
		.addRule(Rule.withHead(Nonterminal.builder("AExp").build()).addSymbol(Nonterminal.builder("AExp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("FBind").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build())).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).build())
		// AExp ::= ([) Exp ((,) Exp)? (. .) Exp? (]) 
		.addRule(Rule.withHead(Nonterminal.builder("AExp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(91).build()).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build(), Nonterminal.builder("Exp").build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(46).build(), Character.builder(46).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Exp").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(93).build()).build()).build()).build())
		// AExp ::= ([) Exp+ (]) 
		.addRule(Rule.withHead(Nonterminal.builder("AExp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(91).build()).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("Exp").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build())).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(93).build()).build()).build()).build())
		// AExp ::= QVar 
		.addRule(Rule.withHead(Nonterminal.builder("AExp").build()).addSymbol(Nonterminal.builder("QVar").build()).build())
		// AExp ::= (() Exp (,) Exp+ ()) 
		.addRule(Rule.withHead(Nonterminal.builder("AExp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("Exp").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build())).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// AExp ::= ([) Exp (|) Qual+ (]) 
		.addRule(Rule.withHead(Nonterminal.builder("AExp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(91).build()).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(124).build()).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("Qual").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build())).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(93).build()).build()).build()).build())
		// AExp ::= (() QOp InfixExp ()) 
		.addRule(Rule.withHead(Nonterminal.builder("AExp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("QOp").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Terminal.builder(Sequence.builder(Character.builder(45).build()).build()).build()).build()))).build()).addSymbol(Nonterminal.builder("InfixExp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// AExp ::= GCon 
		.addRule(Rule.withHead(Nonterminal.builder("AExp").build()).addSymbol(Nonterminal.builder("GCon").build()).build())
		// AExp ::= (() Exp ()) 
		.addRule(Rule.withHead(Nonterminal.builder("AExp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// AExp ::= Literal 
		.addRule(Rule.withHead(Nonterminal.builder("AExp").build()).addSymbol(Nonterminal.builder("Literal").build()).build())
		// AExp ::= (() InfixExp QOp ()) 
		.addRule(Rule.withHead(Nonterminal.builder("AExp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("InfixExp").build()).addSymbol(Nonterminal.builder("QOp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// AExp ::= QCon ({) FBind* (}) 
		.addRule(Rule.withHead(Nonterminal.builder("AExp").build()).addSymbol(Nonterminal.builder("QCon").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build()).addSymbol(Star.builder(Nonterminal.builder("FBind").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build())).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).build())
		// Comment ::= Dashes (Any Any*)? \n 
		.addRule(Rule.withHead(Nonterminal.builder("Comment").build()).addSymbol(Nonterminal.builder("Dashes").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("Any").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Terminal.builder(Sequence.builder(Character.builder(58).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(63).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(61).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(43).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(42).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(47).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(45).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(35).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(33).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(64).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(37).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(36).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(94).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(126).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(62).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(92).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(124).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(60).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(8902).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(38).build()).build()).build()).build()))).build(), Star.builder(Nonterminal.builder("Any").build()).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(10).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Var ::= (() VarSym ()) 
		.addRule(Rule.withHead(Nonterminal.builder("Var").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("VarSym").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// Var ::= VarId 
		.addRule(Rule.withHead(Nonterminal.builder("Var").build()).addSymbol(Nonterminal.builder("VarId").build()).build())
		// Exponent ::= (E | e) (+ | -) Decimal 
		.addRule(Rule.withHead(Nonterminal.builder("Exponent").build()).addSymbol(Alt.builder(CharacterRange.builder(69, 69).build(), CharacterRange.builder(101, 101).build()).build()).addSymbol(Alt.builder(CharacterRange.builder(43, 43).build(), CharacterRange.builder(45, 45).build()).build()).addSymbol(Nonterminal.builder("Decimal").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Pat ::= LPat 
		.addRule(Rule.withHead(Nonterminal.builder("Pat").build()).addSymbol(Nonterminal.builder("LPat").build()).build())
		// Pat ::= LPat QConOp Pat 
		.addRule(Rule.withHead(Nonterminal.builder("Pat").build()).addSymbol(Nonterminal.builder("LPat").build()).addSymbol(Nonterminal.builder("QConOp").build()).addSymbol(Nonterminal.builder("Pat").build()).build())
		// Float ::= Decimal Exponent 
		.addRule(Rule.withHead(Nonterminal.builder("Float").build()).addSymbol(Nonterminal.builder("Decimal").build()).addSymbol(Nonterminal.builder("Exponent").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Float ::= Decimal (.) Decimal Exponent? 
		.addRule(Rule.withHead(Nonterminal.builder("Float").build()).addSymbol(Nonterminal.builder("Decimal").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).addSymbol(Nonterminal.builder("Decimal").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Exponent").build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ConSym ::= ((:) Symbol+) 
		.addRule(Rule.withHead(Nonterminal.builder("ConSym").build()).addSymbol(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(58).build()).build()).build(), Plus.builder(Nonterminal.builder("Symbol").build()).build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Terminal.builder(Sequence.builder(Character.builder(58).build(), Character.builder(58).build()).build()).build()).build()))).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Con ::= (() ConSym ()) 
		.addRule(Rule.withHead(Nonterminal.builder("Con").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("ConSym").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// Con ::= ConId 
		.addRule(Rule.withHead(Nonterminal.builder("Con").build()).addSymbol(Nonterminal.builder("ConId").build()).build())
		// Constrs ::= Constr+ 
		.addRule(Rule.withHead(Nonterminal.builder("Constrs").build()).addSymbol(Plus.builder(Nonterminal.builder("Constr").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(124).build()).build()).build())).build()).build())
		// LExp ::= (d o) ({) Stmts (}) 
		.addRule(Rule.withHead(Nonterminal.builder("LExp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(100).build(), Character.builder(111).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build()).addSymbol(Nonterminal.builder("Stmts").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).build())
		// LExp ::= (i f) Exp (;)? (t h e n) Exp (;)? (e l s e) Exp 
		.addRule(Rule.withHead(Nonterminal.builder("LExp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(102).build()).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(org.jgll.regex.Opt.builder(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(110).build()).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(org.jgll.regex.Opt.builder(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(101).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).build())
		// LExp ::= (\) APat+ (- >) Exp 
		.addRule(Rule.withHead(Nonterminal.builder("LExp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(92).build()).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("APat").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build(), Character.builder(62).build()).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).build())
		// LExp ::= (l e t) Decls (i n) Exp 
		.addRule(Rule.withHead(Nonterminal.builder("LExp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(108).build(), Character.builder(101).build(), Character.builder(116).build()).build()).build()).addSymbol(Nonterminal.builder("Decls").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build()).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).build())
		// LExp ::= (c a s e) Exp (o f) ({) Alts (}) 
		.addRule(Rule.withHead(Nonterminal.builder("LExp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(101).build()).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(111).build(), Character.builder(102).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build()).addSymbol(Nonterminal.builder("Alts").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).build())
		// LExp ::= FExp 
		.addRule(Rule.withHead(Nonterminal.builder("LExp").build()).addSymbol(Nonterminal.builder("FExp").build()).build())
		// QConId ::= (ModId (.))? ConId 
		.addRule(Rule.withHead(Nonterminal.builder("QConId").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("ModId").build(), Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).build()).build()).addSymbol(Nonterminal.builder("ConId").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Body ::= ({) TopDecls (}) 
		.addRule(Rule.withHead(Nonterminal.builder("Body").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build()).addSymbol(Nonterminal.builder("TopDecls").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).build())
		// Body ::= ({) ImpDecls (;) TopDecls (}) 
		.addRule(Rule.withHead(Nonterminal.builder("Body").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build()).addSymbol(Nonterminal.builder("ImpDecls").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).addSymbol(Nonterminal.builder("TopDecls").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).build())
		// Body ::= ({) ImpDecls (}) 
		.addRule(Rule.withHead(Nonterminal.builder("Body").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build()).addSymbol(Nonterminal.builder("ImpDecls").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).build())
		// Any ::= Space 
		.addRule(Rule.withHead(Nonterminal.builder("Any").build()).addSymbol(Nonterminal.builder("Space").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Any ::= Graphic 
		.addRule(Rule.withHead(Nonterminal.builder("Any").build()).addSymbol(Nonterminal.builder("Graphic").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Any ::= Tab 
		.addRule(Rule.withHead(Nonterminal.builder("Any").build()).addSymbol(Nonterminal.builder("Tab").build()).setLayoutStrategy(NO_LAYOUT).build())
		// QTyCon ::= (ModId (.))? TyCon 
		.addRule(Rule.withHead(Nonterminal.builder("QTyCon").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("ModId").build(), Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).build()).build()).addSymbol(Nonterminal.builder("TyCon").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Stmts ::= Stmt* Exp (;)? 
		.addRule(Rule.withHead(Nonterminal.builder("Stmts").build()).addSymbol(Star.builder(Nonterminal.builder("Stmt").build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(org.jgll.regex.Opt.builder(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build()).build())
		// OctIt ::= (0-7) 
		.addRule(Rule.withHead(Nonterminal.builder("OctIt").build()).addSymbol(Alt.builder(CharacterRange.builder(48, 55).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Exports ::= (() Export* (,)? ()) 
		.addRule(Rule.withHead(Nonterminal.builder("Exports").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Star.builder(Nonterminal.builder("Export").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build())).build()).addSymbol(org.jgll.regex.Opt.builder(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// FDecl ::= (i m p o r t) CallConv Safety? Impent Var (: :) FType 
		.addRule(Rule.withHead(Nonterminal.builder("FDecl").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build()).build()).addSymbol(Nonterminal.builder("CallConv").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Safety").build()).build()).addSymbol(Nonterminal.builder("Impent").build()).addSymbol(Nonterminal.builder("Var").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(58).build(), Character.builder(58).build()).build()).build()).addSymbol(Nonterminal.builder("FType").build()).build())
		// FDecl ::= (e x p o r t) CallConv Expent Var (: :) FType 
		.addRule(Rule.withHead(Nonterminal.builder("FDecl").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(101).build(), Character.builder(120).build(), Character.builder(112).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build()).build()).addSymbol(Nonterminal.builder("CallConv").build()).addSymbol(Nonterminal.builder("Expent").build()).addSymbol(Nonterminal.builder("Var").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(58).build(), Character.builder(58).build()).build()).build()).addSymbol(Nonterminal.builder("FType").build()).build())
		// QConOp ::= GConSym 
		.addRule(Rule.withHead(Nonterminal.builder("QConOp").build()).addSymbol(Nonterminal.builder("GConSym").build()).build())
		// QConOp ::= (`) QConId (`) 
		.addRule(Rule.withHead(Nonterminal.builder("QConOp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(96).build()).build()).build()).addSymbol(Nonterminal.builder("QConId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(96).build()).build()).build()).build())
		// Module ::= Body 
		.addRule(Rule.withHead(Nonterminal.builder("Module").build()).addSymbol(Nonterminal.builder("Body").build()).build())
		// Module ::= (m o d u l e) ModId Exports? (w h e r e) Body 
		.addRule(Rule.withHead(Nonterminal.builder("Module").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(109).build(), Character.builder(111).build(), Character.builder(100).build(), Character.builder(117).build(), Character.builder(108).build(), Character.builder(101).build()).build()).build()).addSymbol(Nonterminal.builder("ModId").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Exports").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(101).build()).build()).build()).addSymbol(Nonterminal.builder("Body").build()).build())
		// Integer ::= (0 O) Octal 
		.addRule(Rule.withHead(Nonterminal.builder("Integer").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(48).build(), Character.builder(79).build()).build()).build()).addSymbol(Nonterminal.builder("Octal").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Integer ::= (0 o) Octal 
		.addRule(Rule.withHead(Nonterminal.builder("Integer").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(48).build(), Character.builder(111).build()).build()).build()).addSymbol(Nonterminal.builder("Octal").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Integer ::= Decimal 
		.addRule(Rule.withHead(Nonterminal.builder("Integer").build()).addSymbol(Nonterminal.builder("Decimal").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Integer ::= (0 x) Hexadecimal 
		.addRule(Rule.withHead(Nonterminal.builder("Integer").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(48).build(), Character.builder(120).build()).build()).build()).addSymbol(Nonterminal.builder("Hexadecimal").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Integer ::= (0 X) Hexadecimal 
		.addRule(Rule.withHead(Nonterminal.builder("Integer").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(48).build(), Character.builder(88).build()).build()).build()).addSymbol(Nonterminal.builder("Hexadecimal").build()).setLayoutStrategy(NO_LAYOUT).build())
		// CDecl ::= GenDecl 
		.addRule(Rule.withHead(Nonterminal.builder("CDecl").build()).addSymbol(Nonterminal.builder("GenDecl").build()).build())
		// CDecl ::= (FunLHS | Var) RHS 
		.addRule(Rule.withHead(Nonterminal.builder("CDecl").build()).addSymbol(Alt.builder(Nonterminal.builder("FunLHS").build(), Nonterminal.builder("Var").build()).build()).addSymbol(Nonterminal.builder("RHS").build()).build())
		// Import ::= TyCls (((() (. .) ())) | ((() Var* ())))? 
		.addRule(Rule.withHead(Nonterminal.builder("Import").build()).addSymbol(Nonterminal.builder("TyCls").build()).addSymbol(org.jgll.regex.Opt.builder(Alt.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(46).build(), Character.builder(46).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build(), Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build(), Star.builder(Nonterminal.builder("Var").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build())).build(), Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build()).build()).build()).build())
		// Import ::= Var 
		.addRule(Rule.withHead(Nonterminal.builder("Import").build()).addSymbol(Nonterminal.builder("Var").build()).build())
		// Import ::= TyCon (((() (. .) ())) | ((() CName* ())))? 
		.addRule(Rule.withHead(Nonterminal.builder("Import").build()).addSymbol(Nonterminal.builder("TyCon").build()).addSymbol(org.jgll.regex.Opt.builder(Alt.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(46).build(), Character.builder(46).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build(), Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build(), Star.builder(Nonterminal.builder("CName").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build())).build(), Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build()).build()).build()).build())
		// Decl ::= GenDecl 
		.addRule(Rule.withHead(Nonterminal.builder("Decl").build()).addSymbol(Nonterminal.builder("GenDecl").build()).build())
		// Decl ::= (FunLHS | Pat) RHS 
		.addRule(Rule.withHead(Nonterminal.builder("Decl").build()).addSymbol(Alt.builder(Nonterminal.builder("FunLHS").build(), Nonterminal.builder("Pat").build()).build()).addSymbol(Nonterminal.builder("RHS").build()).build())
		// QVarSym ::= (ModId (.))? VarSym 
		.addRule(Rule.withHead(Nonterminal.builder("QVarSym").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("ModId").build(), Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).build()).build()).addSymbol(Nonterminal.builder("VarSym").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (E T X) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(69).build(), Character.builder(84).build(), Character.builder(88).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (H T) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(72).build(), Character.builder(84).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (E S C) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(69).build(), Character.builder(83).build(), Character.builder(67).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (^) Cntrl 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(94).build()).build()).build()).addSymbol(Nonterminal.builder("Cntrl").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (B S) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(66).build(), Character.builder(83).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (C A N) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(67).build(), Character.builder(65).build(), Character.builder(78).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (D E L) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(68).build(), Character.builder(69).build(), Character.builder(76).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (N U L) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(78).build(), Character.builder(85).build(), Character.builder(76).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (U S) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(85).build(), Character.builder(83).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (S T X) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(83).build(), Character.builder(84).build(), Character.builder(88).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (S U B) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(83).build(), Character.builder(85).build(), Character.builder(66).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (S O H) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(83).build(), Character.builder(79).build(), Character.builder(72).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (L F) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(76).build(), Character.builder(70).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (E T B) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(69).build(), Character.builder(84).build(), Character.builder(66).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (D L E) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(68).build(), Character.builder(76).build(), Character.builder(69).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (E M) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(69).build(), Character.builder(77).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (E N Q) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(69).build(), Character.builder(78).build(), Character.builder(81).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (F S) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(70).build(), Character.builder(83).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (F F) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(70).build(), Character.builder(70).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (N A K) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(78).build(), Character.builder(65).build(), Character.builder(75).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (G S) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(71).build(), Character.builder(83).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (E O T) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(69).build(), Character.builder(79).build(), Character.builder(84).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (A C K) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(65).build(), Character.builder(67).build(), Character.builder(75).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (B E L) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(66).build(), Character.builder(69).build(), Character.builder(76).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (C R) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(67).build(), Character.builder(82).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (D C 1) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(68).build(), Character.builder(67).build(), Character.builder(49).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (D C 4) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(68).build(), Character.builder(67).build(), Character.builder(52).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (D C 3) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(68).build(), Character.builder(67).build(), Character.builder(51).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (D C 2) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(68).build(), Character.builder(67).build(), Character.builder(50).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (S Y N) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(83).build(), Character.builder(89).build(), Character.builder(78).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (V T) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(86).build(), Character.builder(84).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (S I) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(83).build(), Character.builder(73).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (S O) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(83).build(), Character.builder(79).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (R S) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(82).build(), Character.builder(83).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Ascii ::= (S P) 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(83).build(), Character.builder(80).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// LPat ::= APat 
		.addRule(Rule.withHead(Nonterminal.builder("LPat").build()).addSymbol(Nonterminal.builder("APat").build()).build())
		// LPat ::= GCon APat+ 
		.addRule(Rule.withHead(Nonterminal.builder("LPat").build()).addSymbol(Nonterminal.builder("GCon").build()).addSymbol(Plus.builder(Nonterminal.builder("APat").build()).build()).build())
		// LPat ::= (-) (Float | Integer) 
		.addRule(Rule.withHead(Nonterminal.builder("LPat").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build()).build()).build()).addSymbol(Alt.builder(Nonterminal.builder("Float").build(), Nonterminal.builder("Integer").build()).build()).build())
		// HexIt ::= Digit 
		.addRule(Rule.withHead(Nonterminal.builder("HexIt").build()).addSymbol(Nonterminal.builder("Digit").build()).setLayoutStrategy(NO_LAYOUT).build())
		// HexIt ::= (A-F | a-f) 
		.addRule(Rule.withHead(Nonterminal.builder("HexIt").build()).addSymbol(Alt.builder(CharacterRange.builder(65, 70).build(), CharacterRange.builder(97, 102).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// RHS ::= GDRHS ((w h e r e) Decls)? 
		.addRule(Rule.withHead(Nonterminal.builder("RHS").build()).addSymbol(Nonterminal.builder("GDRHS").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(101).build()).build()).build(), Nonterminal.builder("Decls").build()).build()).build()).build())
		// RHS ::= (=) Exp ((w h e r e) Decls)? 
		.addRule(Rule.withHead(Nonterminal.builder("RHS").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(61).build()).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(101).build()).build()).build(), Nonterminal.builder("Decls").build()).build()).build()).build())
		// CallConv ::= (j v m) 
		.addRule(Rule.withHead(Nonterminal.builder("CallConv").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(106).build(), Character.builder(118).build(), Character.builder(109).build()).build()).build()).build())
		// CallConv ::= (c c a l l) 
		.addRule(Rule.withHead(Nonterminal.builder("CallConv").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(99).build(), Character.builder(97).build(), Character.builder(108).build(), Character.builder(108).build()).build()).build()).build())
		// CallConv ::= (d o t n e t) 
		.addRule(Rule.withHead(Nonterminal.builder("CallConv").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(100).build(), Character.builder(111).build(), Character.builder(116).build(), Character.builder(110).build(), Character.builder(101).build(), Character.builder(116).build()).build()).build()).build())
		// CallConv ::= (c p l u s p l u s) 
		.addRule(Rule.withHead(Nonterminal.builder("CallConv").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(112).build(), Character.builder(108).build(), Character.builder(117).build(), Character.builder(115).build(), Character.builder(112).build(), Character.builder(108).build(), Character.builder(117).build(), Character.builder(115).build()).build()).build()).build())
		// CallConv ::= (s t d c a l l) 
		.addRule(Rule.withHead(Nonterminal.builder("CallConv").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(100).build(), Character.builder(99).build(), Character.builder(97).build(), Character.builder(108).build(), Character.builder(108).build()).build()).build()).build())
		// Fixity ::= (i n f i x l) 
		.addRule(Rule.withHead(Nonterminal.builder("Fixity").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(102).build(), Character.builder(105).build(), Character.builder(120).build(), Character.builder(108).build()).build()).build()).build())
		// Fixity ::= (i n f i x r) 
		.addRule(Rule.withHead(Nonterminal.builder("Fixity").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(102).build(), Character.builder(105).build(), Character.builder(120).build(), Character.builder(114).build()).build()).build()).build())
		// Fixity ::= (i n f i x) 
		.addRule(Rule.withHead(Nonterminal.builder("Fixity").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(102).build(), Character.builder(105).build(), Character.builder(120).build()).build()).build()).build())
		// Escape ::= (\) (CharEsc | ((o) Octal) | ((x) Hexadecimal) | Decimal | Ascii) 
		.addRule(Rule.withHead(Nonterminal.builder("Escape").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(92).build()).build()).build()).addSymbol(Alt.builder(Nonterminal.builder("CharEsc").build(), Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(111).build()).build()).build(), Nonterminal.builder("Octal").build()).build(), Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(120).build()).build()).build(), Nonterminal.builder("Hexadecimal").build()).build(), Nonterminal.builder("Decimal").build(), Nonterminal.builder("Ascii").build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Alts ::= Alt+ 
		.addRule(Rule.withHead(Nonterminal.builder("Alts").build()).addSymbol(Plus.builder(Nonterminal.builder("Alt").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build())).build()).build())
		// ReservedId ::= (d e f a u l t) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(100).build(), Character.builder(101).build(), Character.builder(102).build(), Character.builder(97).build(), Character.builder(117).build(), Character.builder(108).build(), Character.builder(116).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedId ::= (m o d u l e) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(109).build(), Character.builder(111).build(), Character.builder(100).build(), Character.builder(117).build(), Character.builder(108).build(), Character.builder(101).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedId ::= (d e r i v i n g) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(100).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(103).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedId ::= (o f) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(111).build(), Character.builder(102).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedId ::= (i n f i x l) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(102).build(), Character.builder(105).build(), Character.builder(120).build(), Character.builder(108).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedId ::= (i n f i x r) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(102).build(), Character.builder(105).build(), Character.builder(120).build(), Character.builder(114).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedId ::= (c l a s s) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(108).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedId ::= (i f) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(102).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedId ::= (w h e r e) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(101).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedId ::= (_) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(95).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedId ::= (i m p o r t) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedId ::= (d a t a) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(100).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(97).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedId ::= (c a s e) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(101).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedId ::= (t y p e) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(121).build(), Character.builder(112).build(), Character.builder(101).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedId ::= (t h e n) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(110).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedId ::= (l e t) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(108).build(), Character.builder(101).build(), Character.builder(116).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedId ::= (i n) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedId ::= (e l s e) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(101).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedId ::= (n e w t y p e) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(110).build(), Character.builder(101).build(), Character.builder(119).build(), Character.builder(116).build(), Character.builder(121).build(), Character.builder(112).build(), Character.builder(101).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedId ::= (f o r e i g n) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(102).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(101).build(), Character.builder(105).build(), Character.builder(103).build(), Character.builder(110).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedId ::= (i n f i x) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(102).build(), Character.builder(105).build(), Character.builder(120).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedId ::= (i n s t a n c e) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(110).build(), Character.builder(99).build(), Character.builder(101).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedId ::= (d o) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(100).build(), Character.builder(111).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Dashes ::= (- -) (-)* 
		.addRule(Rule.withHead(Nonterminal.builder("Dashes").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build(), Character.builder(45).build()).build()).build()).addSymbol(Star.builder(Terminal.builder(Sequence.builder(Character.builder(45).build()).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// VarId ::= (Small (Small | Digit | Large | ('))*) 
		.addRule(Rule.withHead(Nonterminal.builder("VarId").build()).addSymbol(Sequence.builder(Nonterminal.builder("Small").build(), Star.builder(Alt.builder(Nonterminal.builder("Small").build(), Nonterminal.builder("Digit").build(), Nonterminal.builder("Large").build(), Terminal.builder(Sequence.builder(Character.builder(39).build()).build()).build()).build()).build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Alt.builder(CharacterRange.builder(48, 57).build(), CharacterRange.builder(65, 90).build(), CharacterRange.builder(95, 95).build(), CharacterRange.builder(97, 122).build()).build()), new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Terminal.builder(Sequence.builder(Character.builder(108).build(), Character.builder(101).build(), Character.builder(116).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(101).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(108).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(95).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(102).build(), Character.builder(105).build(), Character.builder(120).build(), Character.builder(108).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(111).build(), Character.builder(102).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(110).build(), Character.builder(99).build(), Character.builder(101).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(102).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(101).build(), Character.builder(105).build(), Character.builder(103).build(), Character.builder(110).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(100).build(), Character.builder(111).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(100).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(97).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(109).build(), Character.builder(111).build(), Character.builder(100).build(), Character.builder(117).build(), Character.builder(108).build(), Character.builder(101).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(102).build(), Character.builder(105).build(), Character.builder(120).build(), Character.builder(114).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(100).build(), Character.builder(101).build(), Character.builder(102).build(), Character.builder(97).build(), Character.builder(117).build(), Character.builder(108).build(), Character.builder(116).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(101).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(100).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(103).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(121).build(), Character.builder(112).build(), Character.builder(101).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(110).build(), Character.builder(101).build(), Character.builder(119).build(), Character.builder(116).build(), Character.builder(121).build(), Character.builder(112).build(), Character.builder(101).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(110).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(102).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(101).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(102).build(), Character.builder(105).build(), Character.builder(120).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build()).build()).build()).build()))).build()).setLayoutStrategy(NO_LAYOUT).build())
		// VarOp ::= (`) VarId (`) 
		.addRule(Rule.withHead(Nonterminal.builder("VarOp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(96).build()).build()).build()).addSymbol(Nonterminal.builder("VarId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(96).build()).build()).build()).build())
		// VarOp ::= VarSym 
		.addRule(Rule.withHead(Nonterminal.builder("VarOp").build()).addSymbol(Nonterminal.builder("VarSym").build()).build())
		// ReservedOp ::= (= >) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedOp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(61).build(), Character.builder(62).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedOp ::= (: :) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedOp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(58).build(), Character.builder(58).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedOp ::= (< -) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedOp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(60).build(), Character.builder(45).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedOp ::= (- >) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedOp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build(), Character.builder(62).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedOp ::= (. .) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedOp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(46).build(), Character.builder(46).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedOp ::= (:) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedOp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(58).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedOp ::= (=) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedOp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(61).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedOp ::= (@) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedOp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(64).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedOp ::= (\) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedOp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(92).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedOp ::= (|) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedOp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(124).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// ReservedOp ::= (~) 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedOp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(126).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Whitespace ::= WhiteStuff* 
		.addRule(Rule.withHead(Nonterminal.builder("Whitespace").build()).addSymbol(Star.builder(Nonterminal.builder("WhiteStuff").build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Alt.builder(CharacterRange.builder(9, 11).build(), CharacterRange.builder(13, 13).build(), CharacterRange.builder(32, 32).build()).build()), new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Terminal.builder(Sequence.builder(Character.builder(45).build(), Character.builder(45).build()).build()).build()), new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Terminal.builder(Sequence.builder(Character.builder(123).build(), Character.builder(45).build()).build()).build()))).build()).setLayoutStrategy(NO_LAYOUT).build())
		// FBind ::= QVar (=) Exp 
		.addRule(Rule.withHead(Nonterminal.builder("FBind").build()).addSymbol(Nonterminal.builder("QVar").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(61).build()).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).build())
		// BType ::= BType? AType 
		.addRule(Rule.withHead(Nonterminal.builder("BType").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("BType").build()).build()).addSymbol(Nonterminal.builder("AType").build()).build())
		// InfixExp ::= LExp QOp InfixExp 
		.addRule(Rule.withHead(Nonterminal.builder("InfixExp").build()).addSymbol(Nonterminal.builder("LExp").build()).addSymbol(Nonterminal.builder("QOp").build()).addSymbol(Nonterminal.builder("InfixExp").build()).build())
		// InfixExp ::= LExp 
		.addRule(Rule.withHead(Nonterminal.builder("InfixExp").build()).addSymbol(Nonterminal.builder("LExp").build()).build())
		// InfixExp ::= (-) InfixExp 
		.addRule(Rule.withHead(Nonterminal.builder("InfixExp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(45).build()).build()).build()).addSymbol(Nonterminal.builder("InfixExp").build()).build())
		// Constr ::= Con ((!)? AType)* 
		.addRule(Rule.withHead(Nonterminal.builder("Constr").build()).addSymbol(Nonterminal.builder("Con").build()).addSymbol(Star.builder(Sequence.builder(org.jgll.regex.Opt.builder(Terminal.builder(Sequence.builder(Character.builder(33).build()).build()).build()).build(), Nonterminal.builder("AType").build()).build()).build()).build())
		// Constr ::= Con ({) FieldDecl* (}) 
		.addRule(Rule.withHead(Nonterminal.builder("Constr").build()).addSymbol(Nonterminal.builder("Con").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build()).addSymbol(Star.builder(Nonterminal.builder("FieldDecl").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build())).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).build())
		// Constr ::= (BType | ((!) AType)) ConOp (BType | ((!) AType)) 
		.addRule(Rule.withHead(Nonterminal.builder("Constr").build()).addSymbol(Alt.builder(Nonterminal.builder("BType").build(), Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(33).build()).build()).build(), Nonterminal.builder("AType").build()).build()).build()).addSymbol(Nonterminal.builder("ConOp").build()).addSymbol(Alt.builder(Nonterminal.builder("BType").build(), Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(33).build()).build()).build(), Nonterminal.builder("AType").build()).build()).build()).build())
		// String ::= (") (Gap | Escape | Graphic | Space)* (") 
		.addRule(Rule.withHead(Nonterminal.builder("String").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(34).build()).build()).build()).addSymbol(Star.builder(Alt.builder(Nonterminal.builder("Gap").build(), Nonterminal.builder("Escape").build(), Nonterminal.builder("Graphic").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Alt.builder(CharacterRange.builder(34, 34).build(), CharacterRange.builder(92, 92).build()).build()).build()))).build(), Nonterminal.builder("Space").build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(34).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Decls ::= ({) Decl+ (}) 
		.addRule(Rule.withHead(Nonterminal.builder("Decls").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("Decl").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build())).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).build())
		// FRType ::= FAType 
		.addRule(Rule.withHead(Nonterminal.builder("FRType").build()).addSymbol(Nonterminal.builder("FAType").build()).build())
		// FRType ::= (() ()) 
		.addRule(Rule.withHead(Nonterminal.builder("FRType").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// Graphic ::= Special 
		.addRule(Rule.withHead(Nonterminal.builder("Graphic").build()).addSymbol(Nonterminal.builder("Special").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Graphic ::= Small 
		.addRule(Rule.withHead(Nonterminal.builder("Graphic").build()).addSymbol(Nonterminal.builder("Small").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Graphic ::= Symbol 
		.addRule(Rule.withHead(Nonterminal.builder("Graphic").build()).addSymbol(Nonterminal.builder("Symbol").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Graphic ::= Digit 
		.addRule(Rule.withHead(Nonterminal.builder("Graphic").build()).addSymbol(Nonterminal.builder("Digit").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Graphic ::= Large 
		.addRule(Rule.withHead(Nonterminal.builder("Graphic").build()).addSymbol(Nonterminal.builder("Large").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Graphic ::= (") 
		.addRule(Rule.withHead(Nonterminal.builder("Graphic").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(34).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Graphic ::= (') 
		.addRule(Rule.withHead(Nonterminal.builder("Graphic").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(39).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// AType ::= (() Type ()) 
		.addRule(Rule.withHead(Nonterminal.builder("AType").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// AType ::= (() Type (,) Type+ ()) 
		.addRule(Rule.withHead(Nonterminal.builder("AType").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("Type").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build())).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// AType ::= ([) Type (]) 
		.addRule(Rule.withHead(Nonterminal.builder("AType").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(91).build()).build()).build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(93).build()).build()).build()).build())
		// AType ::= GTyCon 
		.addRule(Rule.withHead(Nonterminal.builder("AType").build()).addSymbol(Nonterminal.builder("GTyCon").build()).build())
		// AType ::= TyVar 
		.addRule(Rule.withHead(Nonterminal.builder("AType").build()).addSymbol(Nonterminal.builder("TyVar").build()).build())
		// APat ::= Literal 
		.addRule(Rule.withHead(Nonterminal.builder("APat").build()).addSymbol(Nonterminal.builder("Literal").build()).build())
		// APat ::= (() Pat (,) Pat+ ()) 
		.addRule(Rule.withHead(Nonterminal.builder("APat").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("Pat").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("Pat").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build())).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// APat ::= ([) Pat+ 
		.addRule(Rule.withHead(Nonterminal.builder("APat").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(91).build()).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("Pat").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build())).build()).build())
		// APat ::= GCon 
		.addRule(Rule.withHead(Nonterminal.builder("APat").build()).addSymbol(Nonterminal.builder("GCon").build()).build())
		// APat ::= (~) APat 
		.addRule(Rule.withHead(Nonterminal.builder("APat").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(126).build()).build()).build()).addSymbol(Nonterminal.builder("APat").build()).build())
		// APat ::= (_) 
		.addRule(Rule.withHead(Nonterminal.builder("APat").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(95).build()).build()).build()).build())
		// APat ::= Var ((@) APat)? 
		.addRule(Rule.withHead(Nonterminal.builder("APat").build()).addSymbol(Nonterminal.builder("Var").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(64).build()).build()).build(), Nonterminal.builder("APat").build()).build()).build()).build())
		// APat ::= QCon ({) FPat* (}) 
		.addRule(Rule.withHead(Nonterminal.builder("APat").build()).addSymbol(Nonterminal.builder("QCon").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build()).addSymbol(Star.builder(Nonterminal.builder("FPat").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build())).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).build())
		// APat ::= (() Pat ()) 
		.addRule(Rule.withHead(Nonterminal.builder("APat").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("Pat").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// QOp ::= QConOp 
		.addRule(Rule.withHead(Nonterminal.builder("QOp").build()).addSymbol(Nonterminal.builder("QConOp").build()).build())
		// QOp ::= QVarOp 
		.addRule(Rule.withHead(Nonterminal.builder("QOp").build()).addSymbol(Nonterminal.builder("QVarOp").build()).build())
		// Tab ::= \u0009 
		.addRule(Rule.withHead(Nonterminal.builder("Tab").build()).addSymbol(Character.builder(9).build()).setLayoutStrategy(NO_LAYOUT).build())
		// QConSym ::= (ModId (.))? ConSym 
		.addRule(Rule.withHead(Nonterminal.builder("QConSym").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("ModId").build(), Terminal.builder(Sequence.builder(Character.builder(46).build()).build()).build()).build()).build()).addSymbol(Nonterminal.builder("ConSym").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Literal ::= String 
		.addRule(Rule.withHead(Nonterminal.builder("Literal").build()).addSymbol(Nonterminal.builder("String").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Literal ::= Integer 
		.addRule(Rule.withHead(Nonterminal.builder("Literal").build()).addSymbol(Nonterminal.builder("Integer").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Literal ::= Float 
		.addRule(Rule.withHead(Nonterminal.builder("Literal").build()).addSymbol(Nonterminal.builder("Float").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Literal ::= Char 
		.addRule(Rule.withHead(Nonterminal.builder("Literal").build()).addSymbol(Nonterminal.builder("Char").build()).setLayoutStrategy(NO_LAYOUT).build())
		// VarSym ::= (Symbol Symbol*) 
		.addRule(Rule.withHead(Nonterminal.builder("VarSym").build()).addSymbol(Sequence.builder(Nonterminal.builder("Symbol").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Terminal.builder(Sequence.builder(Character.builder(58).build()).build()).build()).build()))).build(), Star.builder(Nonterminal.builder("Symbol").build()).build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Alt.builder(CharacterRange.builder(45, 46).build(), CharacterRange.builder(58, 58).build(), CharacterRange.builder(60, 62).build(), CharacterRange.builder(64, 64).build(), CharacterRange.builder(92, 92).build(), CharacterRange.builder(124, 124).build(), CharacterRange.builder(126, 126).build()).build()), new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Terminal.builder(Sequence.builder(Character.builder(58).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(126).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(61).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(61).build(), Character.builder(62).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(92).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(124).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(60).build(), Character.builder(45).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(45).build(), Character.builder(62).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(58).build(), Character.builder(58).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(46).build(), Character.builder(46).build()).build()).build(), Terminal.builder(Sequence.builder(Character.builder(64).build()).build()).build()).build()))).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Special ::= ()) 
		.addRule(Rule.withHead(Nonterminal.builder("Special").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Special ::= (;) 
		.addRule(Rule.withHead(Nonterminal.builder("Special").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Special ::= (() 
		.addRule(Rule.withHead(Nonterminal.builder("Special").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Special ::= (,) 
		.addRule(Rule.withHead(Nonterminal.builder("Special").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Special ::= (]) 
		.addRule(Rule.withHead(Nonterminal.builder("Special").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(93).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Special ::= ([) 
		.addRule(Rule.withHead(Nonterminal.builder("Special").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(91).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Special ::= (}) 
		.addRule(Rule.withHead(Nonterminal.builder("Special").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Special ::= (`) 
		.addRule(Rule.withHead(Nonterminal.builder("Special").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(96).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Special ::= ({) 
		.addRule(Rule.withHead(Nonterminal.builder("Special").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// IDecl ::= (FunLHS | Var) RHS 
		.addRule(Rule.withHead(Nonterminal.builder("IDecl").build()).addSymbol(Alt.builder(Nonterminal.builder("FunLHS").build(), Nonterminal.builder("Var").build()).build()).addSymbol(Nonterminal.builder("RHS").build()).build())
		// IDecl ::= 
		.addRule(Rule.withHead(Nonterminal.builder("IDecl").build()).build())
		// TopDecls ::= TopDecl+ 
		.addRule(Rule.withHead(Nonterminal.builder("TopDecls").build()).addSymbol(Plus.builder(Nonterminal.builder("TopDecl").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build())).build()).build())
		// QVar ::= (() QVarSym ()) 
		.addRule(Rule.withHead(Nonterminal.builder("QVar").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("QVarSym").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).build())
		// QVar ::= QVarId 
		.addRule(Rule.withHead(Nonterminal.builder("QVar").build()).addSymbol(Nonterminal.builder("QVarId").build()).build())
		// TyVar ::= VarId 
		.addRule(Rule.withHead(Nonterminal.builder("TyVar").build()).addSymbol(Nonterminal.builder("VarId").build()).setLayoutStrategy(NO_LAYOUT).build())
		// Safety ::= (u n s a f e) 
		.addRule(Rule.withHead(Nonterminal.builder("Safety").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(117).build(), Character.builder(110).build(), Character.builder(115).build(), Character.builder(97).build(), Character.builder(102).build(), Character.builder(101).build()).build()).build()).build())
		// Safety ::= (s a f e) 
		.addRule(Rule.withHead(Nonterminal.builder("Safety").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(115).build(), Character.builder(97).build(), Character.builder(102).build(), Character.builder(101).build()).build()).build()).build())
		// ConOp ::= (`) ConId (`) 
		.addRule(Rule.withHead(Nonterminal.builder("ConOp").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(96).build()).build()).build()).addSymbol(Nonterminal.builder("ConId").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(96).build()).build()).build()).build())
		// ConOp ::= ConSym 
		.addRule(Rule.withHead(Nonterminal.builder("ConOp").build()).addSymbol(Nonterminal.builder("ConSym").build()).build())
		// Vars ::= Var+ 
		.addRule(Rule.withHead(Nonterminal.builder("Vars").build()).addSymbol(Plus.builder(Nonterminal.builder("Var").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build())).build()).build())
		// Qual ::= (l e t) Decls 
		.addRule(Rule.withHead(Nonterminal.builder("Qual").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(108).build(), Character.builder(101).build(), Character.builder(116).build()).build()).build()).addSymbol(Nonterminal.builder("Decls").build()).build())
		// Qual ::= Exp 
		.addRule(Rule.withHead(Nonterminal.builder("Qual").build()).addSymbol(Nonterminal.builder("Exp").build()).build())
		// Qual ::= Pat (< -) Exp 
		.addRule(Rule.withHead(Nonterminal.builder("Qual").build()).addSymbol(Nonterminal.builder("Pat").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(60).build(), Character.builder(45).build()).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).build())
		// CName ::= Var 
		.addRule(Rule.withHead(Nonterminal.builder("CName").build()).addSymbol(Nonterminal.builder("Var").build()).build())
		// CName ::= Con 
		.addRule(Rule.withHead(Nonterminal.builder("CName").build()).addSymbol(Nonterminal.builder("Con").build()).build())
		// Expent ::= String? 
		.addRule(Rule.withHead(Nonterminal.builder("Expent").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("String").build()).build()).build())
		// ConId ::= Large (Small | Digit | Large | ('))* 
		.addRule(Rule.withHead(Nonterminal.builder("ConId").build()).addSymbol(Nonterminal.builder("Large").build()).addSymbol(Star.builder(Alt.builder(Nonterminal.builder("Small").build(), Nonterminal.builder("Digit").build(), Nonterminal.builder("Large").build(), Terminal.builder(Sequence.builder(Character.builder(39).build()).build()).build()).build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_FOLLOW, Alt.builder(CharacterRange.builder(48, 57).build(), CharacterRange.builder(65, 90).build(), CharacterRange.builder(95, 95).build(), CharacterRange.builder(97, 122).build()).build()))).build()).setLayoutStrategy(NO_LAYOUT).build())
		// TopDecl ::= Decl 
		.addRule(Rule.withHead(Nonterminal.builder("TopDecl").build()).addSymbol(Nonterminal.builder("Decl").build()).build())
		// TopDecl ::= (n e w t y p e) (Context (= >))? SimpleType (=) NewConstr Deriving? 
		.addRule(Rule.withHead(Nonterminal.builder("TopDecl").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(110).build(), Character.builder(101).build(), Character.builder(119).build(), Character.builder(116).build(), Character.builder(121).build(), Character.builder(112).build(), Character.builder(101).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("Context").build(), Terminal.builder(Sequence.builder(Character.builder(61).build(), Character.builder(62).build()).build()).build()).build()).build()).addSymbol(Nonterminal.builder("SimpleType").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(61).build()).build()).build()).addSymbol(Nonterminal.builder("NewConstr").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Deriving").build()).build()).build())
		// TopDecl ::= (i n s t a n c e) (SContext (= >))? QTyCls Inst ((w h e r e) IDecls)? 
		.addRule(Rule.withHead(Nonterminal.builder("TopDecl").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(110).build(), Character.builder(99).build(), Character.builder(101).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("SContext").build(), Terminal.builder(Sequence.builder(Character.builder(61).build(), Character.builder(62).build()).build()).build()).build()).build()).addSymbol(Nonterminal.builder("QTyCls").build()).addSymbol(Nonterminal.builder("Inst").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(101).build()).build()).build(), Nonterminal.builder("IDecls").build()).build()).build()).build())
		// TopDecl ::= (c l a s s) (SContext (= >))? TyCls TyVar ((w h e r e) CDecls)? 
		.addRule(Rule.withHead(Nonterminal.builder("TopDecl").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(99).build(), Character.builder(108).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("SContext").build(), Terminal.builder(Sequence.builder(Character.builder(61).build(), Character.builder(62).build()).build()).build()).build()).build()).addSymbol(Nonterminal.builder("TyCls").build()).addSymbol(Nonterminal.builder("TyVar").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(101).build()).build()).build(), Nonterminal.builder("CDecls").build()).build()).build()).build())
		// TopDecl ::= (t y p e) SimpleType (=) Type 
		.addRule(Rule.withHead(Nonterminal.builder("TopDecl").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(116).build(), Character.builder(121).build(), Character.builder(112).build(), Character.builder(101).build()).build()).build()).addSymbol(Nonterminal.builder("SimpleType").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(61).build()).build()).build()).addSymbol(Nonterminal.builder("Type").build()).build())
		// TopDecl ::= (d e f a u l t) Type* 
		.addRule(Rule.withHead(Nonterminal.builder("TopDecl").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(100).build(), Character.builder(101).build(), Character.builder(102).build(), Character.builder(97).build(), Character.builder(117).build(), Character.builder(108).build(), Character.builder(116).build()).build()).build()).addSymbol(Star.builder(Nonterminal.builder("Type").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(44).build()).build()).build())).build()).build())
		// TopDecl ::= (f o r e i g n) FDecl 
		.addRule(Rule.withHead(Nonterminal.builder("TopDecl").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(102).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(101).build(), Character.builder(105).build(), Character.builder(103).build(), Character.builder(110).build()).build()).build()).addSymbol(Nonterminal.builder("FDecl").build()).build())
		// TopDecl ::= (d a t a) (Context (= >))? SimpleType ((=) Constrs)? Deriving? 
		.addRule(Rule.withHead(Nonterminal.builder("TopDecl").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(100).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(97).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("Context").build(), Terminal.builder(Sequence.builder(Character.builder(61).build(), Character.builder(62).build()).build()).build()).build()).build()).addSymbol(Nonterminal.builder("SimpleType").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Terminal.builder(Sequence.builder(Character.builder(61).build()).build()).build(), Nonterminal.builder("Constrs").build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Deriving").build()).build()).build())
		// Op ::= ConOp 
		.addRule(Rule.withHead(Nonterminal.builder("Op").build()).addSymbol(Nonterminal.builder("ConOp").build()).build())
		// Op ::= VarOp 
		.addRule(Rule.withHead(Nonterminal.builder("Op").build()).addSymbol(Nonterminal.builder("VarOp").build()).build())
		// Impent ::= String? 
		.addRule(Rule.withHead(Nonterminal.builder("Impent").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("String").build()).build()).build())
		// FunLHS ::= (() FunLHS ()) APat ({) APat (}) 
		.addRule(Rule.withHead(Nonterminal.builder("FunLHS").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(40).build()).build()).build()).addSymbol(Nonterminal.builder("FunLHS").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(41).build()).build()).build()).addSymbol(Nonterminal.builder("APat").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build()).addSymbol(Nonterminal.builder("APat").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).build())
		// FunLHS ::= Var APat APat* 
		.addRule(Rule.withHead(Nonterminal.builder("FunLHS").build()).addSymbol(Nonterminal.builder("Var").build()).addSymbol(Nonterminal.builder("APat").build()).addSymbol(Star.builder(Nonterminal.builder("APat").build()).build()).build())
		// FunLHS ::= Pat VarOp Pat 
		.addRule(Rule.withHead(Nonterminal.builder("FunLHS").build()).addSymbol(Nonterminal.builder("Pat").build()).addSymbol(Nonterminal.builder("VarOp").build()).addSymbol(Nonterminal.builder("Pat").build()).build())
		// FPat ::= QVar (=) Pat 
		.addRule(Rule.withHead(Nonterminal.builder("FPat").build()).addSymbol(Nonterminal.builder("QVar").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(61).build()).build()).build()).addSymbol(Nonterminal.builder("Pat").build()).build())
		// TyCon ::= ConId 
		.addRule(Rule.withHead(Nonterminal.builder("TyCon").build()).addSymbol(Nonterminal.builder("ConId").build()).setLayoutStrategy(NO_LAYOUT).build())
		// CharEsc ::= (\) 
		.addRule(Rule.withHead(Nonterminal.builder("CharEsc").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(92).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// CharEsc ::= (a) 
		.addRule(Rule.withHead(Nonterminal.builder("CharEsc").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(97).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// CharEsc ::= (t) 
		.addRule(Rule.withHead(Nonterminal.builder("CharEsc").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(116).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// CharEsc ::= (r) 
		.addRule(Rule.withHead(Nonterminal.builder("CharEsc").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(114).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// CharEsc ::= (v) 
		.addRule(Rule.withHead(Nonterminal.builder("CharEsc").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(118).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// CharEsc ::= (b) 
		.addRule(Rule.withHead(Nonterminal.builder("CharEsc").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(98).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// CharEsc ::= (f) 
		.addRule(Rule.withHead(Nonterminal.builder("CharEsc").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(102).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// CharEsc ::= (n) 
		.addRule(Rule.withHead(Nonterminal.builder("CharEsc").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(110).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// CharEsc ::= (') 
		.addRule(Rule.withHead(Nonterminal.builder("CharEsc").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(39).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// CharEsc ::= (") 
		.addRule(Rule.withHead(Nonterminal.builder("CharEsc").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(34).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// CharEsc ::= (&) 
		.addRule(Rule.withHead(Nonterminal.builder("CharEsc").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(38).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Char ::= (') (Escape | Space | Graphic) (') 
		.addRule(Rule.withHead(Nonterminal.builder("Char").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(39).build()).build()).build()).addSymbol(Alt.builder(Nonterminal.builder("Escape").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Alt.builder(CharacterRange.builder(38, 38).build(), CharacterRange.builder(92, 92).build()).build()).build()))).build(), Nonterminal.builder("Space").build(), Nonterminal.builder("Graphic").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Alt.builder(CharacterRange.builder(39, 39).build(), CharacterRange.builder(92, 92).build()).build()).build()))).build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(39).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// Gap ::= (\) WhiteChar WhiteChar* (\) 
		.addRule(Rule.withHead(Nonterminal.builder("Gap").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(92).build()).build()).build()).addSymbol(Nonterminal.builder("WhiteChar").build()).addSymbol(Star.builder(Nonterminal.builder("WhiteChar").build()).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(92).build()).build()).build()).setLayoutStrategy(NO_LAYOUT).build())
		// GenDecl ::= Vars (: :) (Context (= >))? Type 
		.addRule(Rule.withHead(Nonterminal.builder("GenDecl").build()).addSymbol(Nonterminal.builder("Vars").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(58).build(), Character.builder(58).build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("Context").build(), Terminal.builder(Sequence.builder(Character.builder(61).build(), Character.builder(62).build()).build()).build()).build()).build()).addSymbol(Nonterminal.builder("Type").build()).build())
		// GenDecl ::= 
		.addRule(Rule.withHead(Nonterminal.builder("GenDecl").build()).build())
		// GenDecl ::= Fixity Integer? Ops 
		.addRule(Rule.withHead(Nonterminal.builder("GenDecl").build()).addSymbol(Nonterminal.builder("Fixity").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Integer").build()).build()).addSymbol(Nonterminal.builder("Ops").build()).build())
		// GDRHS ::= Guards (=) Exp GDRHS? 
		.addRule(Rule.withHead(Nonterminal.builder("GDRHS").build()).addSymbol(Nonterminal.builder("Guards").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(61).build()).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("GDRHS").build()).build()).build())
		// IDecls ::= ({) IDecl+ (}) 
		.addRule(Rule.withHead(Nonterminal.builder("IDecls").build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(123).build()).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("IDecl").build()).addSeparators(Arrays.asList(Terminal.builder(Sequence.builder(Character.builder(59).build()).build()).build())).build()).addSymbol(Terminal.builder(Sequence.builder(Character.builder(125).build()).build()).build()).build())
		.build();
}
