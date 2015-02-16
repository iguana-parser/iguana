package org.jgll.grammar;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.condition.ConditionType;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Character;
import org.jgll.regex.Alt;
import org.jgll.regex.Plus;
import org.jgll.regex.Sequence;
import org.jgll.regex.Star;

import com.google.common.collect.Sets;

public class Haskell {

	public static Grammar grammar =	
		Grammar.builder()
		//CDecls ::= "{" CDecl* "}" 
		.addRule(Rule.withHead(Nonterminal.builder("CDecls").build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).addSymbol(Star.builder(Nonterminal.builder("CDecl").build()).build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).build())
		//QVarId ::= (ModId ".")? VarId 
		.addRule(Rule.withHead(Nonterminal.builder("QVarId").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("ModId").build(), Sequence.builder(Character.builder(46).build()).build()).build()).build()).addSymbol(Nonterminal.builder("VarId").build()).build())
		//GDPat ::= Guards "->" Exp GDPat? 
		.addRule(Rule.withHead(Nonterminal.builder("GDPat").build()).addSymbol(Nonterminal.builder("Guards").build()).addSymbol(Sequence.builder(Character.builder(45).build(), Character.builder(62).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("GDPat").build()).build()).build())
		//ImpSpec ::= "hiding" "(" Import* (,)? ")" 
		.addRule(Rule.withHead(Nonterminal.builder("ImpSpec").build()).addSymbol(Sequence.builder(Character.builder(104).build(), Character.builder(105).build(), Character.builder(100).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(103).build()).build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Star.builder(Nonterminal.builder("Import").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Character.builder(44).build()).build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//ImpSpec ::= "(" Import* (,)? ")" 
		.addRule(Rule.withHead(Nonterminal.builder("ImpSpec").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Star.builder(Nonterminal.builder("Import").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Character.builder(44).build()).build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//NComment ::= OpenCom ANY* (NComment ANY)* CloseCom 
		.addRule(Rule.withHead(Nonterminal.builder("NComment").build()).addSymbol(Nonterminal.builder("OpenCom").build()).addSymbol(Star.builder(Nonterminal.builder("ANY").build()).build()).addSymbol(Star.builder(Sequence.builder(Nonterminal.builder("NComment").build(), Nonterminal.builder("ANY").build()).build()).build()).addSymbol(Nonterminal.builder("CloseCom").build()).build())
		//TyCls ::= ConId 
		.addRule(Rule.withHead(Nonterminal.builder("TyCls").build()).addSymbol(Nonterminal.builder("ConId").build()).build())
		//Class ::= QTyCls TyVar 
		.addRule(Rule.withHead(Nonterminal.builder("Class").build()).addSymbol(Nonterminal.builder("QTyCls").build()).addSymbol(Nonterminal.builder("TyVar").build()).build())
		//Class ::= QTyCls "(" TyVar AType+ ")" 
		.addRule(Rule.withHead(Nonterminal.builder("Class").build()).addSymbol(Nonterminal.builder("QTyCls").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("TyVar").build()).addSymbol(Plus.builder(Nonterminal.builder("AType").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//Guards ::= "|" Guard+ 
		.addRule(Rule.withHead(Nonterminal.builder("Guards").build()).addSymbol(Sequence.builder(Character.builder(124).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("Guard").build()).build()).build())
		//GConSym ::= QConSym 
		.addRule(Rule.withHead(Nonterminal.builder("GConSym").build()).addSymbol(Nonterminal.builder("QConSym").build()).build())
		//GConSym ::= ":" 
		.addRule(Rule.withHead(Nonterminal.builder("GConSym").build()).addSymbol(Sequence.builder(Character.builder(58).build()).build()).build())
		//Inst ::= "(" TyVar "->" TyVar ")" 
		.addRule(Rule.withHead(Nonterminal.builder("Inst").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("TyVar").build()).addSymbol(Sequence.builder(Character.builder(45).build(), Character.builder(62).build()).build()).addSymbol(Nonterminal.builder("TyVar").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//Inst ::= "(" GTyCon TyVar* ")" 
		.addRule(Rule.withHead(Nonterminal.builder("Inst").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("GTyCon").build()).addSymbol(Star.builder(Nonterminal.builder("TyVar").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//Inst ::= "(" TyVar "," TyVar+ ")" 
		.addRule(Rule.withHead(Nonterminal.builder("Inst").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("TyVar").build()).addSymbol(Sequence.builder(Character.builder(44).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("TyVar").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//Inst ::= GTyCon 
		.addRule(Rule.withHead(Nonterminal.builder("Inst").build()).addSymbol(Nonterminal.builder("GTyCon").build()).build())
		//Inst ::= "[" TyVar "]" 
		.addRule(Rule.withHead(Nonterminal.builder("Inst").build()).addSymbol(Sequence.builder(Character.builder(91).build()).build()).addSymbol(Nonterminal.builder("TyVar").build()).addSymbol(Sequence.builder(Character.builder(93).build()).build()).build())
		//QCon ::= "(" GConSym ")" 
		.addRule(Rule.withHead(Nonterminal.builder("QCon").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("GConSym").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//QCon ::= QConId 
		.addRule(Rule.withHead(Nonterminal.builder("QCon").build()).addSymbol(Nonterminal.builder("QConId").build()).build())
		//WhiteChar ::=) 
		.addRule(Rule.withHead(Nonterminal.builder("WhiteChar").build()).addSymbol(Alt.builder(CharacterRange.builder(9, 11).build(), CharacterRange.builder(13, 13).build(), CharacterRange.builder(32, 32).build()).build()).build())
		//SimpleType ::= TyCon TyVar* 
		.addRule(Rule.withHead(Nonterminal.builder("SimpleType").build()).addSymbol(Nonterminal.builder("TyCon").build()).addSymbol(Star.builder(Nonterminal.builder("TyVar").build()).build()).build())
		//Small ::= AscSmall 
		.addRule(Rule.withHead(Nonterminal.builder("Small").build()).addSymbol(Nonterminal.builder("AscSmall").build()).build())
		//Small ::= "_" 
		.addRule(Rule.withHead(Nonterminal.builder("Small").build()).addSymbol(Sequence.builder(Character.builder(95).build()).build()).build())
		//FieldDecl ::= Vars "::" (Type | ("!" AType)) 
		.addRule(Rule.withHead(Nonterminal.builder("FieldDecl").build()).addSymbol(Nonterminal.builder("Vars").build()).addSymbol(Sequence.builder(Character.builder(58).build(), Character.builder(58).build()).build()).addSymbol(Alt.builder(Nonterminal.builder("Type").build(), Sequence.builder(Sequence.builder(Character.builder(33).build()).build(), Nonterminal.builder("AType").build()).build()).build()).build())
		//GTyCon ::= QTyCon 
		.addRule(Rule.withHead(Nonterminal.builder("GTyCon").build()).addSymbol(Nonterminal.builder("QTyCon").build()).build())
		//GTyCon ::= "[" "]" 
		.addRule(Rule.withHead(Nonterminal.builder("GTyCon").build()).addSymbol(Sequence.builder(Character.builder(91).build()).build()).addSymbol(Sequence.builder(Character.builder(93).build()).build()).build())
		//GTyCon ::= "(" ")" 
		.addRule(Rule.withHead(Nonterminal.builder("GTyCon").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//GTyCon ::= "(" "->" ")" 
		.addRule(Rule.withHead(Nonterminal.builder("GTyCon").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Sequence.builder(Character.builder(45).build(), Character.builder(62).build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//GTyCon ::= "(" (,)+ ")" 
		.addRule(Rule.withHead(Nonterminal.builder("GTyCon").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Plus.builder(Sequence.builder(Character.builder(44).build()).build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//DClass ::= QTyCls 
		.addRule(Rule.withHead(Nonterminal.builder("DClass").build()).addSymbol(Nonterminal.builder("QTyCls").build()).build())
		//Stmt ::= Exp ";" 
		.addRule(Rule.withHead(Nonterminal.builder("Stmt").build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).build())
		//Stmt ::= ";" 
		.addRule(Rule.withHead(Nonterminal.builder("Stmt").build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).build())
		//Stmt ::= "let" Decls ";" 
		.addRule(Rule.withHead(Nonterminal.builder("Stmt").build()).addSymbol(Sequence.builder(Character.builder(108).build(), Character.builder(101).build(), Character.builder(116).build()).build()).addSymbol(Nonterminal.builder("Decls").build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).build())
		//Stmt ::= Pat "<-" Exp ";" 
		.addRule(Rule.withHead(Nonterminal.builder("Stmt").build()).addSymbol(Nonterminal.builder("Pat").build()).addSymbol(Sequence.builder(Character.builder(60).build(), Character.builder(45).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).build())
		//Hexadecimal ::= HexIt HexIt* 
		.addRule(Rule.withHead(Nonterminal.builder("Hexadecimal").build()).addSymbol(Nonterminal.builder("HexIt").build()).addSymbol(Star.builder(Nonterminal.builder("HexIt").build()).build()).build())
		//Context ::= "(" Class* ")" 
		.addRule(Rule.withHead(Nonterminal.builder("Context").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Star.builder(Nonterminal.builder("Class").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//Context ::= Class 
		.addRule(Rule.withHead(Nonterminal.builder("Context").build()).addSymbol(Nonterminal.builder("Class").build()).build())
		//Large ::= AscLarge 
		.addRule(Rule.withHead(Nonterminal.builder("Large").build()).addSymbol(Nonterminal.builder("AscLarge").build()).build())
		//Guard ::= Pat "<-" InfixExp 
		.addRule(Rule.withHead(Nonterminal.builder("Guard").build()).addSymbol(Nonterminal.builder("Pat").build()).addSymbol(Sequence.builder(Character.builder(60).build(), Character.builder(45).build()).build()).addSymbol(Nonterminal.builder("InfixExp").build()).build())
		//Guard ::= InfixExp 
		.addRule(Rule.withHead(Nonterminal.builder("Guard").build()).addSymbol(Nonterminal.builder("InfixExp").build()).build())
		//Guard ::= "let" Decls 
		.addRule(Rule.withHead(Nonterminal.builder("Guard").build()).addSymbol(Sequence.builder(Character.builder(108).build(), Character.builder(101).build(), Character.builder(116).build()).build()).addSymbol(Nonterminal.builder("Decls").build()).build())
		//SimpleClass ::= QTyCls TyVar 
		.addRule(Rule.withHead(Nonterminal.builder("SimpleClass").build()).addSymbol(Nonterminal.builder("QTyCls").build()).addSymbol(Nonterminal.builder("TyVar").build()).build())
		//AscSmall ::= (a-z) 
		.addRule(Rule.withHead(Nonterminal.builder("AscSmall").build()).addSymbol(Alt.builder(CharacterRange.builder(97, 122).build()).build()).build())
		//QVarOp ::= "`" QVarId "`" 
		.addRule(Rule.withHead(Nonterminal.builder("QVarOp").build()).addSymbol(Sequence.builder(Character.builder(96).build()).build()).addSymbol(Nonterminal.builder("QVarId").build()).addSymbol(Sequence.builder(Character.builder(96).build()).build()).build())
		//QVarOp ::= QVarSym 
		.addRule(Rule.withHead(Nonterminal.builder("QVarOp").build()).addSymbol(Nonterminal.builder("QVarSym").build()).build())
		//$default$ ::= 
		.addRule(Rule.withHead(Nonterminal.builder("$default$").build()).build())
		//FExp ::= FExp? AExp 
		.addRule(Rule.withHead(Nonterminal.builder("FExp").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("FExp").build()).build()).addSymbol(Nonterminal.builder("AExp").build()).build())
		//QTyCls ::= (ModId ".")? TyCls 
		.addRule(Rule.withHead(Nonterminal.builder("QTyCls").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("ModId").build(), Sequence.builder(Character.builder(46).build()).build()).build()).build()).addSymbol(Nonterminal.builder("TyCls").build()).build())
		//OpenCom ::= "{-" 
		.addRule(Rule.withHead(Nonterminal.builder("OpenCom").build()).addSymbol(Sequence.builder(Character.builder(123).build(), Character.builder(45).build()).build()).build())
		//Decimal ::= Digit Digit* 
		.addRule(Rule.withHead(Nonterminal.builder("Decimal").build()).addSymbol(Nonterminal.builder("Digit").build()).addSymbol(Star.builder(Nonterminal.builder("Digit").build()).build()).build())
		//SContext ::= "(" Simpleclass* ")" 
		.addRule(Rule.withHead(Nonterminal.builder("SContext").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Star.builder(Nonterminal.builder("Simpleclass").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//SContext ::= SimpleClass 
		.addRule(Rule.withHead(Nonterminal.builder("SContext").build()).addSymbol(Nonterminal.builder("SimpleClass").build()).build())
		//Export ::= QTyCon (("(" ".." ")") | ("(" CName* ")"))? 
		.addRule(Rule.withHead(Nonterminal.builder("Export").build()).addSymbol(Nonterminal.builder("QTyCon").build()).addSymbol(org.jgll.regex.Opt.builder(Alt.builder(Sequence.builder(Sequence.builder(Character.builder(40).build()).build(), Sequence.builder(Character.builder(46).build(), Character.builder(46).build()).build(), Sequence.builder(Character.builder(41).build()).build()).build(), Sequence.builder(Sequence.builder(Character.builder(40).build()).build(), Star.builder(Nonterminal.builder("CName").build()).build(), Sequence.builder(Character.builder(41).build()).build()).build()).build()).build()).build())
		//Export ::= QTyCls (("(" ".." ")") | ("(" QVar* ")"))? 
		.addRule(Rule.withHead(Nonterminal.builder("Export").build()).addSymbol(Nonterminal.builder("QTyCls").build()).addSymbol(org.jgll.regex.Opt.builder(Alt.builder(Sequence.builder(Sequence.builder(Character.builder(40).build()).build(), Sequence.builder(Character.builder(46).build(), Character.builder(46).build()).build(), Sequence.builder(Character.builder(41).build()).build()).build(), Sequence.builder(Sequence.builder(Character.builder(40).build()).build(), Star.builder(Nonterminal.builder("QVar").build()).build(), Sequence.builder(Character.builder(41).build()).build()).build()).build()).build()).build())
		//Export ::= QVar 
		.addRule(Rule.withHead(Nonterminal.builder("Export").build()).addSymbol(Nonterminal.builder("QVar").build()).build())
		//Export ::= Module ModId 
		.addRule(Rule.withHead(Nonterminal.builder("Export").build()).addSymbol(Nonterminal.builder("Module").build()).addSymbol(Nonterminal.builder("ModId").build()).build())
		//ImpDecls ::= ImpDecl+ 
		.addRule(Rule.withHead(Nonterminal.builder("ImpDecls").build()).addSymbol(Plus.builder(Nonterminal.builder("ImpDecl").build()).build()).build())
		//Space ::= "\u0020" 
		.addRule(Rule.withHead(Nonterminal.builder("Space").build()).addSymbol(Sequence.builder(Character.builder(32).build()).build()).build())
		//ImpDecl ::= "import" (q u a l i f i e d)? ModId ("as" ModId)? ImpSpec? 
		.addRule(Rule.withHead(Nonterminal.builder("ImpDecl").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Character.builder(113).build(), Character.builder(117).build(), Character.builder(97).build(), Character.builder(108).build(), Character.builder(105).build(), Character.builder(102).build(), Character.builder(105).build(), Character.builder(101).build(), Character.builder(100).build()).build()).build()).addSymbol(Nonterminal.builder("ModId").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(97).build(), Character.builder(115).build()).build(), Nonterminal.builder("ModId").build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("ImpSpec").build()).build()).build())
		//ImpDecl ::= 
		.addRule(Rule.withHead(Nonterminal.builder("ImpDecl").build()).build())
		//Exp ::= InfixExp 
		.addRule(Rule.withHead(Nonterminal.builder("Exp").build()).addSymbol(Nonterminal.builder("InfixExp").build()).build())
		//Exp ::= InfixExp "::" (Context "=>")? Type 
		.addRule(Rule.withHead(Nonterminal.builder("Exp").build()).addSymbol(Nonterminal.builder("InfixExp").build()).addSymbol(Sequence.builder(Character.builder(58).build(), Character.builder(58).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("Context").build(), Sequence.builder(Character.builder(61).build(), Character.builder(62).build()).build()).build()).build()).addSymbol(Nonterminal.builder("Type").build()).build())
		//Symbol ::= AscSymbol 
		.addRule(Rule.withHead(Nonterminal.builder("Symbol").build()).addSymbol(Nonterminal.builder("AscSymbol").build()).build())
		//NewConstr ::= Con AType 
		.addRule(Rule.withHead(Nonterminal.builder("NewConstr").build()).addSymbol(Nonterminal.builder("Con").build()).addSymbol(Nonterminal.builder("AType").build()).build())
		//NewConstr ::= Con "{" Var "::" Type "}" 
		.addRule(Rule.withHead(Nonterminal.builder("NewConstr").build()).addSymbol(Nonterminal.builder("Con").build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).addSymbol(Nonterminal.builder("Var").build()).addSymbol(Sequence.builder(Character.builder(58).build(), Character.builder(58).build()).build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).build())
		//Ops ::= OP+ 
		.addRule(Rule.withHead(Nonterminal.builder("Ops").build()).addSymbol(Plus.builder(Nonterminal.builder("OP").build()).build()).build())
		//NewLine ::= \u000A 
		.addRule(Rule.withHead(Nonterminal.builder("NewLine").build()).addSymbol(Character.builder(10).build()).build())
		//ModId ::= (ConId ".")* ConId 
		.addRule(Rule.withHead(Nonterminal.builder("ModId").build()).addSymbol(Star.builder(Sequence.builder(Nonterminal.builder("ConId").build(), Sequence.builder(Character.builder(46).build()).build()).build()).build()).addSymbol(Nonterminal.builder("ConId").build()).build())
		//Digit ::= AscDigit 
		.addRule(Rule.withHead(Nonterminal.builder("Digit").build()).addSymbol(Nonterminal.builder("AscDigit").build()).build())
		//AscDigit ::= (0-9) 
		.addRule(Rule.withHead(Nonterminal.builder("AscDigit").build()).addSymbol(Alt.builder(CharacterRange.builder(48, 57).build()).build()).build())
		//AscLarge ::= (A-Z) 
		.addRule(Rule.withHead(Nonterminal.builder("AscLarge").build()).addSymbol(Alt.builder(CharacterRange.builder(65, 90).build()).build()).build())
		//Deriving ::= "deriving" (DClass | ("(" DClass* ")")) 
		.addRule(Rule.withHead(Nonterminal.builder("Deriving").build()).addSymbol(Sequence.builder(Character.builder(100).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(103).build()).build()).addSymbol(Alt.builder(Nonterminal.builder("DClass").build(), Sequence.builder(Sequence.builder(Character.builder(40).build()).build(), Star.builder(Nonterminal.builder("DClass").build()).build(), Sequence.builder(Character.builder(41).build()).build()).build()).build()).build())
		//FAType ::= QTyCon AType* 
		.addRule(Rule.withHead(Nonterminal.builder("FAType").build()).addSymbol(Nonterminal.builder("QTyCon").build()).addSymbol(Star.builder(Nonterminal.builder("AType").build()).build()).build())
		//AscSymbol ::= "\u22C6" 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Sequence.builder(Character.builder(8902).build()).build()).build())
		//AscSymbol ::= "=" 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Sequence.builder(Character.builder(61).build()).build()).build())
		//AscSymbol ::= "-" 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Sequence.builder(Character.builder(45).build()).build()).build())
		//AscSymbol ::= "%" 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Sequence.builder(Character.builder(37).build()).build()).build())
		//AscSymbol ::= "!" 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Sequence.builder(Character.builder(33).build()).build()).build())
		//AscSymbol ::= ">" 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Sequence.builder(Character.builder(62).build()).build()).build())
		//AscSymbol ::= ":" 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Sequence.builder(Character.builder(58).build()).build()).build())
		//AscSymbol ::= "." 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Sequence.builder(Character.builder(46).build()).build()).build())
		//AscSymbol ::= "&" 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Sequence.builder(Character.builder(38).build()).build()).build())
		//AscSymbol ::= "?" 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Sequence.builder(Character.builder(63).build()).build()).build())
		//AscSymbol ::= "/" 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Sequence.builder(Character.builder(47).build()).build()).build())
		//AscSymbol ::= "+" 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Sequence.builder(Character.builder(43).build()).build()).build())
		//AscSymbol ::= "#" 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Sequence.builder(Character.builder(35).build()).build()).build())
		//AscSymbol ::= "<" 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Sequence.builder(Character.builder(60).build()).build()).build())
		//AscSymbol ::= "$" 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Sequence.builder(Character.builder(36).build()).build()).build())
		//AscSymbol ::= "^" 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Sequence.builder(Character.builder(94).build()).build()).build())
		//AscSymbol ::= "\" 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Sequence.builder(Character.builder(92).build()).build()).build())
		//AscSymbol ::= "@" 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Sequence.builder(Character.builder(64).build()).build()).build())
		//AscSymbol ::= "~" 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Sequence.builder(Character.builder(126).build()).build()).build())
		//AscSymbol ::= "|" 
		.addRule(Rule.withHead(Nonterminal.builder("AscSymbol").build()).addSymbol(Sequence.builder(Character.builder(124).build()).build()).build())
		//Type ::= BType ("->" Type)? 
		.addRule(Rule.withHead(Nonterminal.builder("Type").build()).addSymbol(Nonterminal.builder("BType").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(45).build(), Character.builder(62).build()).build(), Nonterminal.builder("Type").build()).build()).build()).build())
		//Alt ::= 
		.addRule(Rule.withHead(Nonterminal.builder("Alt").build()).build())
		//Alt ::= Pat "->" Exp ("where" Decls)? 
		.addRule(Rule.withHead(Nonterminal.builder("Alt").build()).addSymbol(Nonterminal.builder("Pat").build()).addSymbol(Sequence.builder(Character.builder(45).build(), Character.builder(62).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(101).build()).build(), Nonterminal.builder("Decls").build()).build()).build()).build())
		//Alt ::= Pat GDPat ("where" Decls)? 
		.addRule(Rule.withHead(Nonterminal.builder("Alt").build()).addSymbol(Nonterminal.builder("Pat").build()).addSymbol(Nonterminal.builder("GDPat").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(101).build()).build(), Nonterminal.builder("Decls").build()).build()).build()).build())
		//Cntrl ::= AscLarge 
		.addRule(Rule.withHead(Nonterminal.builder("Cntrl").build()).addSymbol(Nonterminal.builder("AscLarge").build()).build())
		//Cntrl ::= "^" 
		.addRule(Rule.withHead(Nonterminal.builder("Cntrl").build()).addSymbol(Sequence.builder(Character.builder(94).build()).build()).build())
		//Cntrl ::= "]" 
		.addRule(Rule.withHead(Nonterminal.builder("Cntrl").build()).addSymbol(Sequence.builder(Character.builder(93).build()).build()).build())
		//Cntrl ::= "@" 
		.addRule(Rule.withHead(Nonterminal.builder("Cntrl").build()).addSymbol(Sequence.builder(Character.builder(64).build()).build()).build())
		//Cntrl ::= "\" 
		.addRule(Rule.withHead(Nonterminal.builder("Cntrl").build()).addSymbol(Sequence.builder(Character.builder(92).build()).build()).build())
		//Cntrl ::= "_" 
		.addRule(Rule.withHead(Nonterminal.builder("Cntrl").build()).addSymbol(Sequence.builder(Character.builder(95).build()).build()).build())
		//Cntrl ::= "[" 
		.addRule(Rule.withHead(Nonterminal.builder("Cntrl").build()).addSymbol(Sequence.builder(Character.builder(91).build()).build()).build())
		//FType ::= FRType 
		.addRule(Rule.withHead(Nonterminal.builder("FType").build()).addSymbol(Nonterminal.builder("FRType").build()).build())
		//FType ::= FAType "->" FType 
		.addRule(Rule.withHead(Nonterminal.builder("FType").build()).addSymbol(Nonterminal.builder("FAType").build()).addSymbol(Sequence.builder(Character.builder(45).build(), Character.builder(62).build()).build()).addSymbol(Nonterminal.builder("FType").build()).build())
		//GCon ::= "(" (,)+ ")" 
		.addRule(Rule.withHead(Nonterminal.builder("GCon").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Plus.builder(Sequence.builder(Character.builder(44).build()).build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//GCon ::= QCon 
		.addRule(Rule.withHead(Nonterminal.builder("GCon").build()).addSymbol(Nonterminal.builder("QCon").build()).build())
		//GCon ::= "(" ")" 
		.addRule(Rule.withHead(Nonterminal.builder("GCon").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//GCon ::= "[" "]" 
		.addRule(Rule.withHead(Nonterminal.builder("GCon").build()).addSymbol(Sequence.builder(Character.builder(91).build()).build()).addSymbol(Sequence.builder(Character.builder(93).build()).build()).build())
		//Octal ::= OctIt OctIt* 
		.addRule(Rule.withHead(Nonterminal.builder("Octal").build()).addSymbol(Nonterminal.builder("OctIt").build()).addSymbol(Star.builder(Nonterminal.builder("OctIt").build()).build()).build())
		//AExp ::= QVar 
		.addRule(Rule.withHead(Nonterminal.builder("AExp").build()).addSymbol(Nonterminal.builder("QVar").build()).build())
		//AExp ::= AExp "{" FBind+ "}" 
		.addRule(Rule.withHead(Nonterminal.builder("AExp").build()).addSymbol(Nonterminal.builder("AExp").build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("FBind").build()).build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).build())
		//AExp ::= "(" Exp "," Exp+ ")" 
		.addRule(Rule.withHead(Nonterminal.builder("AExp").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(Sequence.builder(Character.builder(44).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("Exp").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//AExp ::= "[" Exp ("," Exp)? ".." Exp? "]" 
		.addRule(Rule.withHead(Nonterminal.builder("AExp").build()).addSymbol(Sequence.builder(Character.builder(91).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(44).build()).build(), Nonterminal.builder("Exp").build()).build()).build()).addSymbol(Sequence.builder(Character.builder(46).build(), Character.builder(46).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Exp").build()).build()).addSymbol(Sequence.builder(Character.builder(93).build()).build()).build())
		//AExp ::= "(" QOp InfixExp ")" 
		.addRule(Rule.withHead(Nonterminal.builder("AExp").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("QOp").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Sequence.builder(Character.builder(45).build()).build()).build()))).build()).addSymbol(Nonterminal.builder("InfixExp").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//AExp ::= "[" Exp+ "]" 
		.addRule(Rule.withHead(Nonterminal.builder("AExp").build()).addSymbol(Sequence.builder(Character.builder(91).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("Exp").build()).build()).addSymbol(Sequence.builder(Character.builder(93).build()).build()).build())
		//AExp ::= GCon 
		.addRule(Rule.withHead(Nonterminal.builder("AExp").build()).addSymbol(Nonterminal.builder("GCon").build()).build())
		//AExp ::= "[" Exp "|" Qual+ "]" 
		.addRule(Rule.withHead(Nonterminal.builder("AExp").build()).addSymbol(Sequence.builder(Character.builder(91).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(Sequence.builder(Character.builder(124).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("Qual").build()).build()).addSymbol(Sequence.builder(Character.builder(93).build()).build()).build())
		//AExp ::= "(" Exp ")" 
		.addRule(Rule.withHead(Nonterminal.builder("AExp").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//AExp ::= QCon "{" FBind* "}" 
		.addRule(Rule.withHead(Nonterminal.builder("AExp").build()).addSymbol(Nonterminal.builder("QCon").build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).addSymbol(Star.builder(Nonterminal.builder("FBind").build()).build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).build())
		//AExp ::= "(" InfixExp QOp ")" 
		.addRule(Rule.withHead(Nonterminal.builder("AExp").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("InfixExp").build()).addSymbol(Nonterminal.builder("QOp").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//AExp ::= Literal 
		.addRule(Rule.withHead(Nonterminal.builder("AExp").build()).addSymbol(Nonterminal.builder("Literal").build()).build())
		//Comment ::= Dashes (Any Any*)? 
		.addRule(Rule.withHead(Nonterminal.builder("Comment").build()).addSymbol(Nonterminal.builder("Dashes").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("Any").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Sequence.builder(Character.builder(58).build()).build(), Sequence.builder(Character.builder(63).build()).build(), Sequence.builder(Character.builder(61).build()).build(), Sequence.builder(Character.builder(43).build()).build(), Sequence.builder(Character.builder(47).build()).build(), Sequence.builder(Character.builder(46).build()).build(), Sequence.builder(Character.builder(45).build()).build(), Sequence.builder(Character.builder(35).build()).build(), Sequence.builder(Character.builder(33).build()).build(), Sequence.builder(Character.builder(64).build()).build(), Sequence.builder(Character.builder(37).build()).build(), Sequence.builder(Character.builder(36).build()).build(), Sequence.builder(Character.builder(94).build()).build(), Sequence.builder(Character.builder(126).build()).build(), Sequence.builder(Character.builder(62).build()).build(), Sequence.builder(Character.builder(92).build()).build(), Sequence.builder(Character.builder(124).build()).build(), Sequence.builder(Character.builder(60).build()).build(), Sequence.builder(Character.builder(8902).build()).build(), Sequence.builder(Character.builder(38).build()).build()).build()))).build(), Star.builder(Nonterminal.builder("Any").build()).build()).build()).build()).build())
		//Var ::= "(" VarSym ")" 
		.addRule(Rule.withHead(Nonterminal.builder("Var").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("VarSym").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//Var ::= VarId 
		.addRule(Rule.withHead(Nonterminal.builder("Var").build()).addSymbol(Nonterminal.builder("VarId").build()).build())
		//Exponent ::= E + Decimal 
		.addRule(Rule.withHead(Nonterminal.builder("Exponent").build()).addSymbol(Character.builder(69).build()).addSymbol(Character.builder(43).build()).addSymbol(Nonterminal.builder("Decimal").build()).build())
		//Pat ::= LPat QConOp Pat 
		.addRule(Rule.withHead(Nonterminal.builder("Pat").build()).addSymbol(Nonterminal.builder("LPat").build()).addSymbol(Nonterminal.builder("QConOp").build()).addSymbol(Nonterminal.builder("Pat").build()).build())
		//Pat ::= LPat 
		.addRule(Rule.withHead(Nonterminal.builder("Pat").build()).addSymbol(Nonterminal.builder("LPat").build()).build())
		//Float ::= Decimal Exponent 
		.addRule(Rule.withHead(Nonterminal.builder("Float").build()).addSymbol(Nonterminal.builder("Decimal").build()).addSymbol(Nonterminal.builder("Exponent").build()).build())
		//Float ::= Decimal "." Decimal Exponent? 
		.addRule(Rule.withHead(Nonterminal.builder("Float").build()).addSymbol(Nonterminal.builder("Decimal").build()).addSymbol(Sequence.builder(Character.builder(46).build()).build()).addSymbol(Nonterminal.builder("Decimal").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Exponent").build()).build()).build())
		//ConSym ::= (":" Symbol*) 
		.addRule(Rule.withHead(Nonterminal.builder("ConSym").build()).addSymbol(Sequence.builder(Sequence.builder(Character.builder(58).build()).build(), Star.builder(Nonterminal.builder("Symbol").build()).build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Sequence.builder(Character.builder(58).build()).build(), Sequence.builder(Character.builder(126).build()).build(), Sequence.builder(Character.builder(61).build()).build(), Sequence.builder(Character.builder(61).build(), Character.builder(62).build()).build(), Sequence.builder(Character.builder(92).build()).build(), Sequence.builder(Character.builder(124).build()).build(), Sequence.builder(Character.builder(60).build(), Character.builder(45).build()).build(), Sequence.builder(Character.builder(45).build(), Character.builder(62).build()).build(), Sequence.builder(Character.builder(58).build(), Character.builder(58).build()).build(), Sequence.builder(Character.builder(46).build(), Character.builder(46).build()).build(), Sequence.builder(Character.builder(64).build()).build()).build()))).build()).build())
		//Con ::= "(" ConSym ")" 
		.addRule(Rule.withHead(Nonterminal.builder("Con").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("ConSym").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//Con ::= ConId 
		.addRule(Rule.withHead(Nonterminal.builder("Con").build()).addSymbol(Nonterminal.builder("ConId").build()).build())
		//Constrs ::= Constr+ 
		.addRule(Rule.withHead(Nonterminal.builder("Constrs").build()).addSymbol(Plus.builder(Nonterminal.builder("Constr").build()).build()).build())
		//LExp ::= "\" APat+ "->" Exp 
		.addRule(Rule.withHead(Nonterminal.builder("LExp").build()).addSymbol(Sequence.builder(Character.builder(92).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("APat").build()).build()).addSymbol(Sequence.builder(Character.builder(45).build(), Character.builder(62).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).build())
		//LExp ::= "case" Exp "of" "{" Alts "}" 
		.addRule(Rule.withHead(Nonterminal.builder("LExp").build()).addSymbol(Sequence.builder(Character.builder(99).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(101).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(Sequence.builder(Character.builder(111).build(), Character.builder(102).build()).build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).addSymbol(Nonterminal.builder("Alts").build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).build())
		//LExp ::= "let" Decls "in" Exp 
		.addRule(Rule.withHead(Nonterminal.builder("LExp").build()).addSymbol(Sequence.builder(Character.builder(108).build(), Character.builder(101).build(), Character.builder(116).build()).build()).addSymbol(Nonterminal.builder("Decls").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(110).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).build())
		//LExp ::= "do" "{" Stmts "}" 
		.addRule(Rule.withHead(Nonterminal.builder("LExp").build()).addSymbol(Sequence.builder(Character.builder(100).build(), Character.builder(111).build()).build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).addSymbol(Nonterminal.builder("Stmts").build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).build())
		//LExp ::= FExp 
		.addRule(Rule.withHead(Nonterminal.builder("LExp").build()).addSymbol(Nonterminal.builder("FExp").build()).build())
		//LExp ::= "if" Exp (;)? "then" Exp (;)? "else" Exp 
		.addRule(Rule.withHead(Nonterminal.builder("LExp").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(102).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Character.builder(59).build()).build()).build()).addSymbol(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(110).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Character.builder(59).build()).build()).build()).addSymbol(Sequence.builder(Character.builder(101).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).build())
		//QConId ::= (ModId ".")? ConId 
		.addRule(Rule.withHead(Nonterminal.builder("QConId").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("ModId").build(), Sequence.builder(Character.builder(46).build()).build()).build()).build()).addSymbol(Nonterminal.builder("ConId").build()).build())
		//Body ::= "{" TopDecls "}" 
		.addRule(Rule.withHead(Nonterminal.builder("Body").build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).addSymbol(Nonterminal.builder("TopDecls").build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).build())
		//Body ::= "{" ImpDecls "}" 
		.addRule(Rule.withHead(Nonterminal.builder("Body").build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).addSymbol(Nonterminal.builder("ImpDecls").build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).build())
		//Body ::= "{" ImpDecls ";" TopDecls "}" 
		.addRule(Rule.withHead(Nonterminal.builder("Body").build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).addSymbol(Nonterminal.builder("ImpDecls").build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).addSymbol(Nonterminal.builder("TopDecls").build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).build())
		//Any ::= Space 
		.addRule(Rule.withHead(Nonterminal.builder("Any").build()).addSymbol(Nonterminal.builder("Space").build()).build())
		//Any ::= Graphic 
		.addRule(Rule.withHead(Nonterminal.builder("Any").build()).addSymbol(Nonterminal.builder("Graphic").build()).build())
		//Any ::= Tab 
		.addRule(Rule.withHead(Nonterminal.builder("Any").build()).addSymbol(Nonterminal.builder("Tab").build()).build())
		//ANY ::= (WhiteChar | Graphic) 
		.addRule(Rule.withHead(Nonterminal.builder("ANY").build()).addSymbol(Alt.builder(Nonterminal.builder("WhiteChar").build(), Nonterminal.builder("Graphic").build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Sequence.builder(Character.builder(123).build(), Character.builder(45).build()).build(), Sequence.builder(Character.builder(45).build(), Character.builder(125).build()).build()).build()))).build()).build())
		//QTyCon ::= (ModId ".")? TyCon 
		.addRule(Rule.withHead(Nonterminal.builder("QTyCon").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("ModId").build(), Sequence.builder(Character.builder(46).build()).build()).build()).build()).addSymbol(Nonterminal.builder("TyCon").build()).build())
		//Stmts ::= Stmt* Exp (;)? 
		.addRule(Rule.withHead(Nonterminal.builder("Stmts").build()).addSymbol(Star.builder(Nonterminal.builder("Stmt").build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Character.builder(59).build()).build()).build()).build())
		//OctIt ::= (0-7) 
		.addRule(Rule.withHead(Nonterminal.builder("OctIt").build()).addSymbol(Alt.builder(CharacterRange.builder(48, 55).build()).build()).build())
		//Exports ::= "(" Export* (,)? ")" 
		.addRule(Rule.withHead(Nonterminal.builder("Exports").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Star.builder(Nonterminal.builder("Export").build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Character.builder(44).build()).build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//FDecl ::= "export" CallConv Expent Var "::" FType 
		.addRule(Rule.withHead(Nonterminal.builder("FDecl").build()).addSymbol(Sequence.builder(Character.builder(101).build(), Character.builder(120).build(), Character.builder(112).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build()).addSymbol(Nonterminal.builder("CallConv").build()).addSymbol(Nonterminal.builder("Expent").build()).addSymbol(Nonterminal.builder("Var").build()).addSymbol(Sequence.builder(Character.builder(58).build(), Character.builder(58).build()).build()).addSymbol(Nonterminal.builder("FType").build()).build())
		//FDecl ::= "import" CallConv Safety? Impent Var "::" FType 
		.addRule(Rule.withHead(Nonterminal.builder("FDecl").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build()).addSymbol(Nonterminal.builder("CallConv").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Safety").build()).build()).addSymbol(Nonterminal.builder("Impent").build()).addSymbol(Nonterminal.builder("Var").build()).addSymbol(Sequence.builder(Character.builder(58).build(), Character.builder(58).build()).build()).addSymbol(Nonterminal.builder("FType").build()).build())
		//QConOp ::= GConSym 
		.addRule(Rule.withHead(Nonterminal.builder("QConOp").build()).addSymbol(Nonterminal.builder("GConSym").build()).build())
		//QConOp ::= "`" QConId "`" 
		.addRule(Rule.withHead(Nonterminal.builder("QConOp").build()).addSymbol(Sequence.builder(Character.builder(96).build()).build()).addSymbol(Nonterminal.builder("QConId").build()).addSymbol(Sequence.builder(Character.builder(96).build()).build()).build())
		//Module ::= "module" ModId Exports? "where" Body 
		.addRule(Rule.withHead(Nonterminal.builder("Module").build()).addSymbol(Sequence.builder(Character.builder(109).build(), Character.builder(111).build(), Character.builder(100).build(), Character.builder(117).build(), Character.builder(108).build(), Character.builder(101).build()).build()).addSymbol(Nonterminal.builder("ModId").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Exports").build()).build()).addSymbol(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(101).build()).build()).addSymbol(Nonterminal.builder("Body").build()).build())
		//Module ::= Body 
		.addRule(Rule.withHead(Nonterminal.builder("Module").build()).addSymbol(Nonterminal.builder("Body").build()).build())
		//Integer ::= "0O" Octal 
		.addRule(Rule.withHead(Nonterminal.builder("Integer").build()).addSymbol(Sequence.builder(Character.builder(48).build(), Character.builder(79).build()).build()).addSymbol(Nonterminal.builder("Octal").build()).build())
		//Integer ::= "0o" Octal 
		.addRule(Rule.withHead(Nonterminal.builder("Integer").build()).addSymbol(Sequence.builder(Character.builder(48).build(), Character.builder(111).build()).build()).addSymbol(Nonterminal.builder("Octal").build()).build())
		//Integer ::= Decimal 
		.addRule(Rule.withHead(Nonterminal.builder("Integer").build()).addSymbol(Nonterminal.builder("Decimal").build()).build())
		//Integer ::= "0x" Hexadecimal 
		.addRule(Rule.withHead(Nonterminal.builder("Integer").build()).addSymbol(Sequence.builder(Character.builder(48).build(), Character.builder(120).build()).build()).addSymbol(Nonterminal.builder("Hexadecimal").build()).build())
		//Integer ::= "0X" Hexadecimal 
		.addRule(Rule.withHead(Nonterminal.builder("Integer").build()).addSymbol(Sequence.builder(Character.builder(48).build(), Character.builder(88).build()).build()).addSymbol(Nonterminal.builder("Hexadecimal").build()).build())
		//CDecl ::= (FunLHS | Var) RHS 
		.addRule(Rule.withHead(Nonterminal.builder("CDecl").build()).addSymbol(Alt.builder(Nonterminal.builder("FunLHS").build(), Nonterminal.builder("Var").build()).build()).addSymbol(Nonterminal.builder("RHS").build()).build())
		//CDecl ::= GenDecl 
		.addRule(Rule.withHead(Nonterminal.builder("CDecl").build()).addSymbol(Nonterminal.builder("GenDecl").build()).build())
		//Import ::= TyCls (("(" ".." ")") | ("(" Var* ")"))? 
		.addRule(Rule.withHead(Nonterminal.builder("Import").build()).addSymbol(Nonterminal.builder("TyCls").build()).addSymbol(org.jgll.regex.Opt.builder(Alt.builder(Sequence.builder(Sequence.builder(Character.builder(40).build()).build(), Sequence.builder(Character.builder(46).build(), Character.builder(46).build()).build(), Sequence.builder(Character.builder(41).build()).build()).build(), Sequence.builder(Sequence.builder(Character.builder(40).build()).build(), Star.builder(Nonterminal.builder("Var").build()).build(), Sequence.builder(Character.builder(41).build()).build()).build()).build()).build()).build())
		//Import ::= Var 
		.addRule(Rule.withHead(Nonterminal.builder("Import").build()).addSymbol(Nonterminal.builder("Var").build()).build())
		//Import ::= TyCon (("(" ".." ")") | ("(" CName* ")"))? 
		.addRule(Rule.withHead(Nonterminal.builder("Import").build()).addSymbol(Nonterminal.builder("TyCon").build()).addSymbol(org.jgll.regex.Opt.builder(Alt.builder(Sequence.builder(Sequence.builder(Character.builder(40).build()).build(), Sequence.builder(Character.builder(46).build(), Character.builder(46).build()).build(), Sequence.builder(Character.builder(41).build()).build()).build(), Sequence.builder(Sequence.builder(Character.builder(40).build()).build(), Star.builder(Nonterminal.builder("CName").build()).build(), Sequence.builder(Character.builder(41).build()).build()).build()).build()).build()).build())
		//Decl ::= GenDecl 
		.addRule(Rule.withHead(Nonterminal.builder("Decl").build()).addSymbol(Nonterminal.builder("GenDecl").build()).build())
		//Decl ::= (FunLHS | Pat) RHS 
		.addRule(Rule.withHead(Nonterminal.builder("Decl").build()).addSymbol(Alt.builder(Nonterminal.builder("FunLHS").build(), Nonterminal.builder("Pat").build()).build()).addSymbol(Nonterminal.builder("RHS").build()).build())
		//QVarSym ::= (ModId ".")? VarSym 
		.addRule(Rule.withHead(Nonterminal.builder("QVarSym").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("ModId").build(), Sequence.builder(Character.builder(46).build()).build()).build()).build()).addSymbol(Nonterminal.builder("VarSym").build()).build())
		//Ascii ::= "ETX" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(69).build(), Character.builder(84).build(), Character.builder(88).build()).build()).build())
		//Ascii ::= "HT" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(72).build(), Character.builder(84).build()).build()).build())
		//Ascii ::= "ESC" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(69).build(), Character.builder(83).build(), Character.builder(67).build()).build()).build())
		//Ascii ::= "^" Cntrl 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(94).build()).build()).addSymbol(Nonterminal.builder("Cntrl").build()).build())
		//Ascii ::= "BS" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(66).build(), Character.builder(83).build()).build()).build())
		//Ascii ::= "CAN" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(67).build(), Character.builder(65).build(), Character.builder(78).build()).build()).build())
		//Ascii ::= "DEL" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(68).build(), Character.builder(69).build(), Character.builder(76).build()).build()).build())
		//Ascii ::= "NUL" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(78).build(), Character.builder(85).build(), Character.builder(76).build()).build()).build())
		//Ascii ::= "US" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(85).build(), Character.builder(83).build()).build()).build())
		//Ascii ::= "STX" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(83).build(), Character.builder(84).build(), Character.builder(88).build()).build()).build())
		//Ascii ::= "SUB" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(83).build(), Character.builder(85).build(), Character.builder(66).build()).build()).build())
		//Ascii ::= "SOH" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(83).build(), Character.builder(79).build(), Character.builder(72).build()).build()).build())
		//Ascii ::= "LF" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(76).build(), Character.builder(70).build()).build()).build())
		//Ascii ::= "ETB" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(69).build(), Character.builder(84).build(), Character.builder(66).build()).build()).build())
		//Ascii ::= "DLE" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(68).build(), Character.builder(76).build(), Character.builder(69).build()).build()).build())
		//Ascii ::= "EM" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(69).build(), Character.builder(77).build()).build()).build())
		//Ascii ::= "ENQ" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(69).build(), Character.builder(78).build(), Character.builder(81).build()).build()).build())
		//Ascii ::= "FS" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(70).build(), Character.builder(83).build()).build()).build())
		//Ascii ::= "FF" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(70).build(), Character.builder(70).build()).build()).build())
		//Ascii ::= "NAK" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(78).build(), Character.builder(65).build(), Character.builder(75).build()).build()).build())
		//Ascii ::= "GS" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(71).build(), Character.builder(83).build()).build()).build())
		//Ascii ::= "EOT" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(69).build(), Character.builder(79).build(), Character.builder(84).build()).build()).build())
		//Ascii ::= "ACK" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(65).build(), Character.builder(67).build(), Character.builder(75).build()).build()).build())
		//Ascii ::= "BEL" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(66).build(), Character.builder(69).build(), Character.builder(76).build()).build()).build())
		//Ascii ::= "CR" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(67).build(), Character.builder(82).build()).build()).build())
		//Ascii ::= "DC1" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(68).build(), Character.builder(67).build(), Character.builder(49).build()).build()).build())
		//Ascii ::= "DC4" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(68).build(), Character.builder(67).build(), Character.builder(52).build()).build()).build())
		//Ascii ::= "DC3" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(68).build(), Character.builder(67).build(), Character.builder(51).build()).build()).build())
		//Ascii ::= "DC2" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(68).build(), Character.builder(67).build(), Character.builder(50).build()).build()).build())
		//Ascii ::= "SYN" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(83).build(), Character.builder(89).build(), Character.builder(78).build()).build()).build())
		//Ascii ::= "VT" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(86).build(), Character.builder(84).build()).build()).build())
		//Ascii ::= "SI" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(83).build(), Character.builder(73).build()).build()).build())
		//Ascii ::= "SO" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(83).build(), Character.builder(79).build()).build()).build())
		//Ascii ::= "RS" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(82).build(), Character.builder(83).build()).build()).build())
		//Ascii ::= "SP" 
		.addRule(Rule.withHead(Nonterminal.builder("Ascii").build()).addSymbol(Sequence.builder(Character.builder(83).build(), Character.builder(80).build()).build()).build())
		//LPat ::= "-" (Float | Integer) 
		.addRule(Rule.withHead(Nonterminal.builder("LPat").build()).addSymbol(Sequence.builder(Character.builder(45).build()).build()).addSymbol(Alt.builder(Nonterminal.builder("Float").build(), Nonterminal.builder("Integer").build()).build()).build())
		//LPat ::= APat 
		.addRule(Rule.withHead(Nonterminal.builder("LPat").build()).addSymbol(Nonterminal.builder("APat").build()).build())
		//LPat ::= GCon Apat+ 
		.addRule(Rule.withHead(Nonterminal.builder("LPat").build()).addSymbol(Nonterminal.builder("GCon").build()).addSymbol(Plus.builder(Nonterminal.builder("Apat").build()).build()).build())
		//HexIt ::= Digit 
		.addRule(Rule.withHead(Nonterminal.builder("HexIt").build()).addSymbol(Nonterminal.builder("Digit").build()).build())
		//HexIt ::= (A-F | a-f) 
		.addRule(Rule.withHead(Nonterminal.builder("HexIt").build()).addSymbol(Alt.builder(CharacterRange.builder(65, 70).build(), CharacterRange.builder(97, 102).build()).build()).build())
		//RHS ::= "=" Exp ("where" Decls)? 
		.addRule(Rule.withHead(Nonterminal.builder("RHS").build()).addSymbol(Sequence.builder(Character.builder(61).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(101).build()).build(), Nonterminal.builder("Decls").build()).build()).build()).build())
		//RHS ::= GDRHS ("where" Decls)? 
		.addRule(Rule.withHead(Nonterminal.builder("RHS").build()).addSymbol(Nonterminal.builder("GDRHS").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(101).build()).build(), Nonterminal.builder("Decls").build()).build()).build()).build())
		//CallConv ::= "jvm" 
		.addRule(Rule.withHead(Nonterminal.builder("CallConv").build()).addSymbol(Sequence.builder(Character.builder(106).build(), Character.builder(118).build(), Character.builder(109).build()).build()).build())
		//CallConv ::= "ccall" 
		.addRule(Rule.withHead(Nonterminal.builder("CallConv").build()).addSymbol(Sequence.builder(Character.builder(99).build(), Character.builder(99).build(), Character.builder(97).build(), Character.builder(108).build(), Character.builder(108).build()).build()).build())
		//CallConv ::= "dotnet" 
		.addRule(Rule.withHead(Nonterminal.builder("CallConv").build()).addSymbol(Sequence.builder(Character.builder(100).build(), Character.builder(111).build(), Character.builder(116).build(), Character.builder(110).build(), Character.builder(101).build(), Character.builder(116).build()).build()).build())
		//CallConv ::= "cplusplus" 
		.addRule(Rule.withHead(Nonterminal.builder("CallConv").build()).addSymbol(Sequence.builder(Character.builder(99).build(), Character.builder(112).build(), Character.builder(108).build(), Character.builder(117).build(), Character.builder(115).build(), Character.builder(112).build(), Character.builder(108).build(), Character.builder(117).build(), Character.builder(115).build()).build()).build())
		//CallConv ::= "stdcall" 
		.addRule(Rule.withHead(Nonterminal.builder("CallConv").build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(116).build(), Character.builder(100).build(), Character.builder(99).build(), Character.builder(97).build(), Character.builder(108).build(), Character.builder(108).build()).build()).build())
		//Fixity ::= "infixl" 
		.addRule(Rule.withHead(Nonterminal.builder("Fixity").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(102).build(), Character.builder(105).build(), Character.builder(120).build(), Character.builder(108).build()).build()).build())
		//Fixity ::= "infixr" 
		.addRule(Rule.withHead(Nonterminal.builder("Fixity").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(102).build(), Character.builder(105).build(), Character.builder(120).build(), Character.builder(114).build()).build()).build())
		//Fixity ::= "infix" 
		.addRule(Rule.withHead(Nonterminal.builder("Fixity").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(102).build(), Character.builder(105).build(), Character.builder(120).build()).build()).build())
		//Escape ::= "\" (CharEsc | ("o" Octal) | ("x" Hexadecimal) | Decimal | Ascii) 
		.addRule(Rule.withHead(Nonterminal.builder("Escape").build()).addSymbol(Sequence.builder(Character.builder(92).build()).build()).addSymbol(Alt.builder(Nonterminal.builder("CharEsc").build(), Sequence.builder(Sequence.builder(Character.builder(111).build()).build(), Nonterminal.builder("Octal").build()).build(), Sequence.builder(Sequence.builder(Character.builder(120).build()).build(), Nonterminal.builder("Hexadecimal").build()).build(), Nonterminal.builder("Decimal").build(), Nonterminal.builder("Ascii").build()).build()).build())
		//Alts ::= Alt+ 
		.addRule(Rule.withHead(Nonterminal.builder("Alts").build()).addSymbol(Plus.builder(Nonterminal.builder("Alt").build()).build()).build())
		//CloseCom ::= "-}" 
		.addRule(Rule.withHead(Nonterminal.builder("CloseCom").build()).addSymbol(Sequence.builder(Character.builder(45).build(), Character.builder(125).build()).build()).build())
		//ReservedId ::= "default" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Sequence.builder(Character.builder(100).build(), Character.builder(101).build(), Character.builder(102).build(), Character.builder(97).build(), Character.builder(117).build(), Character.builder(108).build(), Character.builder(116).build()).build()).build())
		//ReservedId ::= "module" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Sequence.builder(Character.builder(109).build(), Character.builder(111).build(), Character.builder(100).build(), Character.builder(117).build(), Character.builder(108).build(), Character.builder(101).build()).build()).build())
		//ReservedId ::= "deriving" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Sequence.builder(Character.builder(100).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(103).build()).build()).build())
		//ReservedId ::= "of" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Sequence.builder(Character.builder(111).build(), Character.builder(102).build()).build()).build())
		//ReservedId ::= "infixl" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(102).build(), Character.builder(105).build(), Character.builder(120).build(), Character.builder(108).build()).build()).build())
		//ReservedId ::= "infixr" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(102).build(), Character.builder(105).build(), Character.builder(120).build(), Character.builder(114).build()).build()).build())
		//ReservedId ::= "class" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Sequence.builder(Character.builder(99).build(), Character.builder(108).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build()).build()).build())
		//ReservedId ::= "if" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(102).build()).build()).build())
		//ReservedId ::= "where" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(101).build()).build()).build())
		//ReservedId ::= "_" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Sequence.builder(Character.builder(95).build()).build()).build())
		//ReservedId ::= "import" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build()).build())
		//ReservedId ::= "data" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Sequence.builder(Character.builder(100).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(97).build()).build()).build())
		//ReservedId ::= "case" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Sequence.builder(Character.builder(99).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(101).build()).build()).build())
		//ReservedId ::= "type" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Sequence.builder(Character.builder(116).build(), Character.builder(121).build(), Character.builder(112).build(), Character.builder(101).build()).build()).build())
		//ReservedId ::= "then" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(110).build()).build()).build())
		//ReservedId ::= "let" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Sequence.builder(Character.builder(108).build(), Character.builder(101).build(), Character.builder(116).build()).build()).build())
		//ReservedId ::= "in" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(110).build()).build()).build())
		//ReservedId ::= "else" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Sequence.builder(Character.builder(101).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build()).build())
		//ReservedId ::= "newtype" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Sequence.builder(Character.builder(110).build(), Character.builder(101).build(), Character.builder(119).build(), Character.builder(116).build(), Character.builder(121).build(), Character.builder(112).build(), Character.builder(101).build()).build()).build())
		//ReservedId ::= "foreign" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Sequence.builder(Character.builder(102).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(101).build(), Character.builder(105).build(), Character.builder(103).build(), Character.builder(110).build()).build()).build())
		//ReservedId ::= "infix" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(102).build(), Character.builder(105).build(), Character.builder(120).build()).build()).build())
		//ReservedId ::= "instance" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(110).build(), Character.builder(99).build(), Character.builder(101).build()).build()).build())
		//ReservedId ::= "do" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedId").build()).addSymbol(Sequence.builder(Character.builder(100).build(), Character.builder(111).build()).build()).build())
		//Dashes ::= "--" "-"* 
		.addRule(Rule.withHead(Nonterminal.builder("Dashes").build()).addSymbol(Sequence.builder(Character.builder(45).build(), Character.builder(45).build()).build()).addSymbol(Star.builder(Sequence.builder(Character.builder(45).build()).build()).build()).build())
		//VarId ::= (Small (Small | Digit | Large | "'")*) 
		.addRule(Rule.withHead(Nonterminal.builder("VarId").build()).addSymbol(Sequence.builder(Nonterminal.builder("Small").build(), Star.builder(Alt.builder(Nonterminal.builder("Small").build(), Nonterminal.builder("Digit").build(), Nonterminal.builder("Large").build(), Sequence.builder(Character.builder(39).build()).build()).build()).build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Sequence.builder(Character.builder(108).build(), Character.builder(101).build(), Character.builder(116).build()).build(), Sequence.builder(Character.builder(101).build(), Character.builder(108).build(), Character.builder(115).build(), Character.builder(101).build()).build(), Sequence.builder(Character.builder(99).build(), Character.builder(108).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build()).build(), Sequence.builder(Character.builder(95).build()).build(), Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(102).build(), Character.builder(105).build(), Character.builder(120).build(), Character.builder(108).build()).build(), Sequence.builder(Character.builder(111).build(), Character.builder(102).build()).build(), Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(110).build(), Character.builder(99).build(), Character.builder(101).build()).build(), Sequence.builder(Character.builder(102).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(101).build(), Character.builder(105).build(), Character.builder(103).build(), Character.builder(110).build()).build(), Sequence.builder(Character.builder(100).build(), Character.builder(111).build()).build(), Sequence.builder(Character.builder(100).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(97).build()).build(), Sequence.builder(Character.builder(109).build(), Character.builder(111).build(), Character.builder(100).build(), Character.builder(117).build(), Character.builder(108).build(), Character.builder(101).build()).build(), Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(102).build(), Character.builder(105).build(), Character.builder(120).build(), Character.builder(114).build()).build(), Sequence.builder(Character.builder(100).build(), Character.builder(101).build(), Character.builder(102).build(), Character.builder(97).build(), Character.builder(117).build(), Character.builder(108).build(), Character.builder(116).build()).build(), Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(101).build()).build(), Sequence.builder(Character.builder(100).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(105).build(), Character.builder(118).build(), Character.builder(105).build(), Character.builder(110).build(), Character.builder(103).build()).build(), Sequence.builder(Character.builder(116).build(), Character.builder(121).build(), Character.builder(112).build(), Character.builder(101).build()).build(), Sequence.builder(Character.builder(110).build(), Character.builder(101).build(), Character.builder(119).build(), Character.builder(116).build(), Character.builder(121).build(), Character.builder(112).build(), Character.builder(101).build()).build(), Sequence.builder(Character.builder(116).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(110).build()).build(), Sequence.builder(Character.builder(105).build(), Character.builder(102).build()).build(), Sequence.builder(Character.builder(99).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(101).build()).build(), Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(102).build(), Character.builder(105).build(), Character.builder(120).build()).build(), Sequence.builder(Character.builder(105).build(), Character.builder(109).build(), Character.builder(112).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(116).build()).build(), Sequence.builder(Character.builder(105).build(), Character.builder(110).build()).build()).build()))).build()).build())
		//VarOp ::= "`" VarId "`" 
		.addRule(Rule.withHead(Nonterminal.builder("VarOp").build()).addSymbol(Sequence.builder(Character.builder(96).build()).build()).addSymbol(Nonterminal.builder("VarId").build()).addSymbol(Sequence.builder(Character.builder(96).build()).build()).build())
		//VarOp ::= VarSym 
		.addRule(Rule.withHead(Nonterminal.builder("VarOp").build()).addSymbol(Nonterminal.builder("VarSym").build()).build())
		//Whitespace ::= WhiteStuff+ 
		.addRule(Rule.withHead(Nonterminal.builder("Whitespace").build()).addSymbol(Plus.builder(Nonterminal.builder("WhiteStuff").build()).build()).build())
		//ReservedOp ::= "=>" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedOp").build()).addSymbol(Sequence.builder(Character.builder(61).build(), Character.builder(62).build()).build()).build())
		//ReservedOp ::= "::" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedOp").build()).addSymbol(Sequence.builder(Character.builder(58).build(), Character.builder(58).build()).build()).build())
		//ReservedOp ::= "<-" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedOp").build()).addSymbol(Sequence.builder(Character.builder(60).build(), Character.builder(45).build()).build()).build())
		//ReservedOp ::= "->" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedOp").build()).addSymbol(Sequence.builder(Character.builder(45).build(), Character.builder(62).build()).build()).build())
		//ReservedOp ::= ".." 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedOp").build()).addSymbol(Sequence.builder(Character.builder(46).build(), Character.builder(46).build()).build()).build())
		//ReservedOp ::= ":" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedOp").build()).addSymbol(Sequence.builder(Character.builder(58).build()).build()).build())
		//ReservedOp ::= "=" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedOp").build()).addSymbol(Sequence.builder(Character.builder(61).build()).build()).build())
		//ReservedOp ::= "@" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedOp").build()).addSymbol(Sequence.builder(Character.builder(64).build()).build()).build())
		//ReservedOp ::= "\" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedOp").build()).addSymbol(Sequence.builder(Character.builder(92).build()).build()).build())
		//ReservedOp ::= "|" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedOp").build()).addSymbol(Sequence.builder(Character.builder(124).build()).build()).build())
		//ReservedOp ::= "~" 
		.addRule(Rule.withHead(Nonterminal.builder("ReservedOp").build()).addSymbol(Sequence.builder(Character.builder(126).build()).build()).build())
		//FBind ::= QVar "=" Exp 
		.addRule(Rule.withHead(Nonterminal.builder("FBind").build()).addSymbol(Nonterminal.builder("QVar").build()).addSymbol(Sequence.builder(Character.builder(61).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).build())
		//BType ::= BType? AType 
		.addRule(Rule.withHead(Nonterminal.builder("BType").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("BType").build()).build()).addSymbol(Nonterminal.builder("AType").build()).build())
		//InfixExp ::= LExp QOp InfixExp 
		.addRule(Rule.withHead(Nonterminal.builder("InfixExp").build()).addSymbol(Nonterminal.builder("LExp").build()).addSymbol(Nonterminal.builder("QOp").build()).addSymbol(Nonterminal.builder("InfixExp").build()).build())
		//InfixExp ::= LExp 
		.addRule(Rule.withHead(Nonterminal.builder("InfixExp").build()).addSymbol(Nonterminal.builder("LExp").build()).build())
		//InfixExp ::= "-" InfixExp 
		.addRule(Rule.withHead(Nonterminal.builder("InfixExp").build()).addSymbol(Sequence.builder(Character.builder(45).build()).build()).addSymbol(Nonterminal.builder("InfixExp").build()).build())
		//Constr ::= Con "{" FieldDecl* "}" 
		.addRule(Rule.withHead(Nonterminal.builder("Constr").build()).addSymbol(Nonterminal.builder("Con").build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).addSymbol(Star.builder(Nonterminal.builder("FieldDecl").build()).build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).build())
		//Constr ::= Con ((!)? AType)* 
		.addRule(Rule.withHead(Nonterminal.builder("Constr").build()).addSymbol(Nonterminal.builder("Con").build()).addSymbol(Star.builder(Sequence.builder(org.jgll.regex.Opt.builder(Sequence.builder(Character.builder(33).build()).build()).build(), Nonterminal.builder("AType").build()).build()).build()).build())
		//Constr ::= (((!)? AType) | BType) ConOp (BType | ("!" AType)) 
		.addRule(Rule.withHead(Nonterminal.builder("Constr").build()).addSymbol(Alt.builder(Sequence.builder(org.jgll.regex.Opt.builder(Sequence.builder(Character.builder(33).build()).build()).build(), Nonterminal.builder("AType").build()).build(), Nonterminal.builder("BType").build()).build()).addSymbol(Nonterminal.builder("ConOp").build()).addSymbol(Alt.builder(Nonterminal.builder("BType").build(), Sequence.builder(Sequence.builder(Character.builder(33).build()).build(), Nonterminal.builder("AType").build()).build()).build()).build())
		//String ::= """ (Gap | Escape | Graphic | Space)* """ 
		.addRule(Rule.withHead(Nonterminal.builder("String").build()).addSymbol(Sequence.builder(Character.builder(34).build()).build()).addSymbol(Star.builder(Alt.builder(Nonterminal.builder("Gap").build(), Nonterminal.builder("Escape").build(), Nonterminal.builder("Graphic").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Character.builder(34).build()).build()))).build(), Nonterminal.builder("Space").build()).build()).build()).addSymbol(Sequence.builder(Character.builder(34).build()).build()).build())
		//Decls ::= "{" Decl* "}" 
		.addRule(Rule.withHead(Nonterminal.builder("Decls").build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).addSymbol(Star.builder(Nonterminal.builder("Decl").build()).build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).build())
		//FRType ::= FAType 
		.addRule(Rule.withHead(Nonterminal.builder("FRType").build()).addSymbol(Nonterminal.builder("FAType").build()).build())
		//FRType ::= "(" ")" 
		.addRule(Rule.withHead(Nonterminal.builder("FRType").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//Graphic ::= Special 
		.addRule(Rule.withHead(Nonterminal.builder("Graphic").build()).addSymbol(Nonterminal.builder("Special").build()).build())
		//Graphic ::= Small 
		.addRule(Rule.withHead(Nonterminal.builder("Graphic").build()).addSymbol(Nonterminal.builder("Small").build()).build())
		//Graphic ::= Symbol 
		.addRule(Rule.withHead(Nonterminal.builder("Graphic").build()).addSymbol(Nonterminal.builder("Symbol").build()).build())
		//Graphic ::= Digit 
		.addRule(Rule.withHead(Nonterminal.builder("Graphic").build()).addSymbol(Nonterminal.builder("Digit").build()).build())
		//Graphic ::= Large 
		.addRule(Rule.withHead(Nonterminal.builder("Graphic").build()).addSymbol(Nonterminal.builder("Large").build()).build())
		//Graphic ::= """ 
		.addRule(Rule.withHead(Nonterminal.builder("Graphic").build()).addSymbol(Sequence.builder(Character.builder(34).build()).build()).build())
		//Graphic ::= "'" 
		.addRule(Rule.withHead(Nonterminal.builder("Graphic").build()).addSymbol(Sequence.builder(Character.builder(39).build()).build()).build())
		//AType ::= "(" Type ")" 
		.addRule(Rule.withHead(Nonterminal.builder("AType").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//AType ::= "(" Type "," Type+ ")" 
		.addRule(Rule.withHead(Nonterminal.builder("AType").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Sequence.builder(Character.builder(44).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("Type").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//AType ::= "[" Type "]" 
		.addRule(Rule.withHead(Nonterminal.builder("AType").build()).addSymbol(Sequence.builder(Character.builder(91).build()).build()).addSymbol(Nonterminal.builder("Type").build()).addSymbol(Sequence.builder(Character.builder(93).build()).build()).build())
		//AType ::= GTyCon 
		.addRule(Rule.withHead(Nonterminal.builder("AType").build()).addSymbol(Nonterminal.builder("GTyCon").build()).build())
		//AType ::= TyVar 
		.addRule(Rule.withHead(Nonterminal.builder("AType").build()).addSymbol(Nonterminal.builder("TyVar").build()).build())
		//APat ::= Literal 
		.addRule(Rule.withHead(Nonterminal.builder("APat").build()).addSymbol(Nonterminal.builder("Literal").build()).build())
		//APat ::= "(" Pat ")" 
		.addRule(Rule.withHead(Nonterminal.builder("APat").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("Pat").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//APat ::= "~" APat 
		.addRule(Rule.withHead(Nonterminal.builder("APat").build()).addSymbol(Sequence.builder(Character.builder(126).build()).build()).addSymbol(Nonterminal.builder("APat").build()).build())
		//APat ::= GCon 
		.addRule(Rule.withHead(Nonterminal.builder("APat").build()).addSymbol(Nonterminal.builder("GCon").build()).build())
		//APat ::= "[" Pat+ 
		.addRule(Rule.withHead(Nonterminal.builder("APat").build()).addSymbol(Sequence.builder(Character.builder(91).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("Pat").build()).build()).build())
		//APat ::= "_" 
		.addRule(Rule.withHead(Nonterminal.builder("APat").build()).addSymbol(Sequence.builder(Character.builder(95).build()).build()).build())
		//APat ::= QCon "{" FPat* "}" 
		.addRule(Rule.withHead(Nonterminal.builder("APat").build()).addSymbol(Nonterminal.builder("QCon").build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).addSymbol(Star.builder(Nonterminal.builder("FPat").build()).build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).build())
		//APat ::= Var ("@" APat)? 
		.addRule(Rule.withHead(Nonterminal.builder("APat").build()).addSymbol(Nonterminal.builder("Var").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(64).build()).build(), Nonterminal.builder("APat").build()).build()).build()).build())
		//APat ::= "(" Pat "," Pat+ ")" 
		.addRule(Rule.withHead(Nonterminal.builder("APat").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("Pat").build()).addSymbol(Sequence.builder(Character.builder(44).build()).build()).addSymbol(Plus.builder(Nonterminal.builder("Pat").build()).build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//QOp ::= QConOp 
		.addRule(Rule.withHead(Nonterminal.builder("QOp").build()).addSymbol(Nonterminal.builder("QConOp").build()).build())
		//QOp ::= QVarOp 
		.addRule(Rule.withHead(Nonterminal.builder("QOp").build()).addSymbol(Nonterminal.builder("QVarOp").build()).build())
		//Tab ::= \u0009 
		.addRule(Rule.withHead(Nonterminal.builder("Tab").build()).addSymbol(Character.builder(9).build()).build())
		//QConSym ::= (ModId ".")? ConSym 
		.addRule(Rule.withHead(Nonterminal.builder("QConSym").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("ModId").build(), Sequence.builder(Character.builder(46).build()).build()).build()).build()).addSymbol(Nonterminal.builder("ConSym").build()).build())
		//Literal ::= String 
		.addRule(Rule.withHead(Nonterminal.builder("Literal").build()).addSymbol(Nonterminal.builder("String").build()).build())
		//Literal ::= Integer 
		.addRule(Rule.withHead(Nonterminal.builder("Literal").build()).addSymbol(Nonterminal.builder("Integer").build()).build())
		//Literal ::= Float 
		.addRule(Rule.withHead(Nonterminal.builder("Literal").build()).addSymbol(Nonterminal.builder("Float").build()).build())
		//Literal ::= Char 
		.addRule(Rule.withHead(Nonterminal.builder("Literal").build()).addSymbol(Nonterminal.builder("Char").build()).build())
		//VarSym ::= (Symbol Symbol*) 
		.addRule(Rule.withHead(Nonterminal.builder("VarSym").build()).addSymbol(Sequence.builder(Nonterminal.builder("Symbol").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Sequence.builder(Character.builder(58).build()).build()).build()))).build(), Star.builder(Nonterminal.builder("Symbol").build()).build()).addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Sequence.builder(Character.builder(58).build()).build(), Sequence.builder(Character.builder(126).build()).build(), Sequence.builder(Character.builder(61).build()).build(), Sequence.builder(Character.builder(61).build(), Character.builder(62).build()).build(), Sequence.builder(Character.builder(92).build()).build(), Sequence.builder(Character.builder(124).build()).build(), Sequence.builder(Character.builder(60).build(), Character.builder(45).build()).build(), Sequence.builder(Character.builder(45).build(), Character.builder(62).build()).build(), Sequence.builder(Character.builder(58).build(), Character.builder(58).build()).build(), Sequence.builder(Character.builder(46).build(), Character.builder(46).build()).build(), Sequence.builder(Character.builder(64).build()).build()).build()))).build()).build())
		//Special ::= ")" 
		.addRule(Rule.withHead(Nonterminal.builder("Special").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//Special ::= ";" 
		.addRule(Rule.withHead(Nonterminal.builder("Special").build()).addSymbol(Sequence.builder(Character.builder(59).build()).build()).build())
		//Special ::= "(" 
		.addRule(Rule.withHead(Nonterminal.builder("Special").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).build())
		//Special ::= "," 
		.addRule(Rule.withHead(Nonterminal.builder("Special").build()).addSymbol(Sequence.builder(Character.builder(44).build()).build()).build())
		//Special ::= "]" 
		.addRule(Rule.withHead(Nonterminal.builder("Special").build()).addSymbol(Sequence.builder(Character.builder(93).build()).build()).build())
		//Special ::= "[" 
		.addRule(Rule.withHead(Nonterminal.builder("Special").build()).addSymbol(Sequence.builder(Character.builder(91).build()).build()).build())
		//Special ::= "}" 
		.addRule(Rule.withHead(Nonterminal.builder("Special").build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).build())
		//Special ::= "`" 
		.addRule(Rule.withHead(Nonterminal.builder("Special").build()).addSymbol(Sequence.builder(Character.builder(96).build()).build()).build())
		//Special ::= "{" 
		.addRule(Rule.withHead(Nonterminal.builder("Special").build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).build())
		//IDecl ::= 
		.addRule(Rule.withHead(Nonterminal.builder("IDecl").build()).build())
		//IDecl ::= (FunLHS | Var) RHS 
		.addRule(Rule.withHead(Nonterminal.builder("IDecl").build()).addSymbol(Alt.builder(Nonterminal.builder("FunLHS").build(), Nonterminal.builder("Var").build()).build()).addSymbol(Nonterminal.builder("RHS").build()).build())
		//TopDecls ::= TopDecl* 
		.addRule(Rule.withHead(Nonterminal.builder("TopDecls").build()).addSymbol(Star.builder(Nonterminal.builder("TopDecl").build()).build()).build())
		//QVar ::= "(" QVarSym ")" 
		.addRule(Rule.withHead(Nonterminal.builder("QVar").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("QVarSym").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).build())
		//QVar ::= QVarId 
		.addRule(Rule.withHead(Nonterminal.builder("QVar").build()).addSymbol(Nonterminal.builder("QVarId").build()).build())
		//TyVar ::= VarId 
		.addRule(Rule.withHead(Nonterminal.builder("TyVar").build()).addSymbol(Nonterminal.builder("VarId").build()).build())
		//Safety ::= "unsafe" 
		.addRule(Rule.withHead(Nonterminal.builder("Safety").build()).addSymbol(Sequence.builder(Character.builder(117).build(), Character.builder(110).build(), Character.builder(115).build(), Character.builder(97).build(), Character.builder(102).build(), Character.builder(101).build()).build()).build())
		//Safety ::= "safe" 
		.addRule(Rule.withHead(Nonterminal.builder("Safety").build()).addSymbol(Sequence.builder(Character.builder(115).build(), Character.builder(97).build(), Character.builder(102).build(), Character.builder(101).build()).build()).build())
		//ConOp ::= "`" ConId "`" 
		.addRule(Rule.withHead(Nonterminal.builder("ConOp").build()).addSymbol(Sequence.builder(Character.builder(96).build()).build()).addSymbol(Nonterminal.builder("ConId").build()).addSymbol(Sequence.builder(Character.builder(96).build()).build()).build())
		//ConOp ::= ConSym 
		.addRule(Rule.withHead(Nonterminal.builder("ConOp").build()).addSymbol(Nonterminal.builder("ConSym").build()).build())
		//Vars ::= Var+ 
		.addRule(Rule.withHead(Nonterminal.builder("Vars").build()).addSymbol(Plus.builder(Nonterminal.builder("Var").build()).build()).build())
		//Qual ::= "let" Decls 
		.addRule(Rule.withHead(Nonterminal.builder("Qual").build()).addSymbol(Sequence.builder(Character.builder(108).build(), Character.builder(101).build(), Character.builder(116).build()).build()).addSymbol(Nonterminal.builder("Decls").build()).build())
		//Qual ::= Exp 
		.addRule(Rule.withHead(Nonterminal.builder("Qual").build()).addSymbol(Nonterminal.builder("Exp").build()).build())
		//Qual ::= Pat "<-" Exp 
		.addRule(Rule.withHead(Nonterminal.builder("Qual").build()).addSymbol(Nonterminal.builder("Pat").build()).addSymbol(Sequence.builder(Character.builder(60).build(), Character.builder(45).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).build())
		//CName ::= Var 
		.addRule(Rule.withHead(Nonterminal.builder("CName").build()).addSymbol(Nonterminal.builder("Var").build()).build())
		//CName ::= Con 
		.addRule(Rule.withHead(Nonterminal.builder("CName").build()).addSymbol(Nonterminal.builder("Con").build()).build())
		//Expent ::= String? 
		.addRule(Rule.withHead(Nonterminal.builder("Expent").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("String").build()).build()).build())
		//ConId ::= Large (Small | Digit | Large | "'")* 
		.addRule(Rule.withHead(Nonterminal.builder("ConId").build()).addSymbol(Nonterminal.builder("Large").build()).addSymbol(Star.builder(Alt.builder(Nonterminal.builder("Small").build(), Nonterminal.builder("Digit").build(), Nonterminal.builder("Large").build(), Sequence.builder(Character.builder(39).build()).build()).build()).build()).build())
		//TopDecl ::= "newtype" (Context "=>")? SimpleType "=" NewConstr DeriTing? 
		.addRule(Rule.withHead(Nonterminal.builder("TopDecl").build()).addSymbol(Sequence.builder(Character.builder(110).build(), Character.builder(101).build(), Character.builder(119).build(), Character.builder(116).build(), Character.builder(121).build(), Character.builder(112).build(), Character.builder(101).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("Context").build(), Sequence.builder(Character.builder(61).build(), Character.builder(62).build()).build()).build()).build()).addSymbol(Nonterminal.builder("SimpleType").build()).addSymbol(Sequence.builder(Character.builder(61).build()).build()).addSymbol(Nonterminal.builder("NewConstr").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("DeriTing").build()).build()).build())
		//TopDecl ::= "class" (SContext "=>")? TyCls TyVar ("where" CDecls)? 
		.addRule(Rule.withHead(Nonterminal.builder("TopDecl").build()).addSymbol(Sequence.builder(Character.builder(99).build(), Character.builder(108).build(), Character.builder(97).build(), Character.builder(115).build(), Character.builder(115).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("SContext").build(), Sequence.builder(Character.builder(61).build(), Character.builder(62).build()).build()).build()).build()).addSymbol(Nonterminal.builder("TyCls").build()).addSymbol(Nonterminal.builder("TyVar").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(101).build()).build(), Nonterminal.builder("CDecls").build()).build()).build()).build())
		//TopDecl ::= "type" SimpleType "=" Type 
		.addRule(Rule.withHead(Nonterminal.builder("TopDecl").build()).addSymbol(Sequence.builder(Character.builder(116).build(), Character.builder(121).build(), Character.builder(112).build(), Character.builder(101).build()).build()).addSymbol(Nonterminal.builder("SimpleType").build()).addSymbol(Sequence.builder(Character.builder(61).build()).build()).addSymbol(Nonterminal.builder("Type").build()).build())
		//TopDecl ::= Decl 
		.addRule(Rule.withHead(Nonterminal.builder("TopDecl").build()).addSymbol(Nonterminal.builder("Decl").build()).build())
		//TopDecl ::= "default" Type* 
		.addRule(Rule.withHead(Nonterminal.builder("TopDecl").build()).addSymbol(Sequence.builder(Character.builder(100).build(), Character.builder(101).build(), Character.builder(102).build(), Character.builder(97).build(), Character.builder(117).build(), Character.builder(108).build(), Character.builder(116).build()).build()).addSymbol(Star.builder(Nonterminal.builder("Type").build()).build()).build())
		//TopDecl ::= "instance" (SContext "=>")? QTyCls Inst ("where" IDecls)? 
		.addRule(Rule.withHead(Nonterminal.builder("TopDecl").build()).addSymbol(Sequence.builder(Character.builder(105).build(), Character.builder(110).build(), Character.builder(115).build(), Character.builder(116).build(), Character.builder(97).build(), Character.builder(110).build(), Character.builder(99).build(), Character.builder(101).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("SContext").build(), Sequence.builder(Character.builder(61).build(), Character.builder(62).build()).build()).build()).build()).addSymbol(Nonterminal.builder("QTyCls").build()).addSymbol(Nonterminal.builder("Inst").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(119).build(), Character.builder(104).build(), Character.builder(101).build(), Character.builder(114).build(), Character.builder(101).build()).build(), Nonterminal.builder("IDecls").build()).build()).build()).build())
		//TopDecl ::= "foreign" FDecl 
		.addRule(Rule.withHead(Nonterminal.builder("TopDecl").build()).addSymbol(Sequence.builder(Character.builder(102).build(), Character.builder(111).build(), Character.builder(114).build(), Character.builder(101).build(), Character.builder(105).build(), Character.builder(103).build(), Character.builder(110).build()).build()).addSymbol(Nonterminal.builder("FDecl").build()).build())
		//TopDecl ::= "data" (Context "=>")? SimpleType ("=" Constrs)? DeriTing? 
		.addRule(Rule.withHead(Nonterminal.builder("TopDecl").build()).addSymbol(Sequence.builder(Character.builder(100).build(), Character.builder(97).build(), Character.builder(116).build(), Character.builder(97).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("Context").build(), Sequence.builder(Character.builder(61).build(), Character.builder(62).build()).build()).build()).build()).addSymbol(Nonterminal.builder("SimpleType").build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Sequence.builder(Character.builder(61).build()).build(), Nonterminal.builder("Constrs").build()).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("DeriTing").build()).build()).build())
		//Op ::= ConOp 
		.addRule(Rule.withHead(Nonterminal.builder("Op").build()).addSymbol(Nonterminal.builder("ConOp").build()).build())
		//Op ::= VarOp 
		.addRule(Rule.withHead(Nonterminal.builder("Op").build()).addSymbol(Nonterminal.builder("VarOp").build()).build())
		//Whitestuff ::= WhiteChar 
		.addRule(Rule.withHead(Nonterminal.builder("Whitestuff").build()).addSymbol(Nonterminal.builder("WhiteChar").build()).build())
		//Whitestuff ::= NComment 
		.addRule(Rule.withHead(Nonterminal.builder("Whitestuff").build()).addSymbol(Nonterminal.builder("NComment").build()).build())
		//Whitestuff ::= Comment 
		.addRule(Rule.withHead(Nonterminal.builder("Whitestuff").build()).addSymbol(Nonterminal.builder("Comment").build()).build())
		//Impent ::= String? 
		.addRule(Rule.withHead(Nonterminal.builder("Impent").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("String").build()).build()).build())
		//FunLHS ::= Var APat Apat* 
		.addRule(Rule.withHead(Nonterminal.builder("FunLHS").build()).addSymbol(Nonterminal.builder("Var").build()).addSymbol(Nonterminal.builder("APat").build()).addSymbol(Star.builder(Nonterminal.builder("Apat").build()).build()).build())
		//FunLHS ::= "(" FunLHS ")" APat "{" APat "}" 
		.addRule(Rule.withHead(Nonterminal.builder("FunLHS").build()).addSymbol(Sequence.builder(Character.builder(40).build()).build()).addSymbol(Nonterminal.builder("FunLHS").build()).addSymbol(Sequence.builder(Character.builder(41).build()).build()).addSymbol(Nonterminal.builder("APat").build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).addSymbol(Nonterminal.builder("APat").build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).build())
		//FunLHS ::= Pat VarOp Pat 
		.addRule(Rule.withHead(Nonterminal.builder("FunLHS").build()).addSymbol(Nonterminal.builder("Pat").build()).addSymbol(Nonterminal.builder("VarOp").build()).addSymbol(Nonterminal.builder("Pat").build()).build())
		//FPat ::= QVar "=" Pat 
		.addRule(Rule.withHead(Nonterminal.builder("FPat").build()).addSymbol(Nonterminal.builder("QVar").build()).addSymbol(Sequence.builder(Character.builder(61).build()).build()).addSymbol(Nonterminal.builder("Pat").build()).build())
		//TyCon ::= ConId 
		.addRule(Rule.withHead(Nonterminal.builder("TyCon").build()).addSymbol(Nonterminal.builder("ConId").build()).build())
		//CharEsc ::= "\" 
		.addRule(Rule.withHead(Nonterminal.builder("CharEsc").build()).addSymbol(Sequence.builder(Character.builder(92).build()).build()).build())
		//CharEsc ::= "a" 
		.addRule(Rule.withHead(Nonterminal.builder("CharEsc").build()).addSymbol(Sequence.builder(Character.builder(97).build()).build()).build())
		//CharEsc ::= "t" 
		.addRule(Rule.withHead(Nonterminal.builder("CharEsc").build()).addSymbol(Sequence.builder(Character.builder(116).build()).build()).build())
		//CharEsc ::= "r" 
		.addRule(Rule.withHead(Nonterminal.builder("CharEsc").build()).addSymbol(Sequence.builder(Character.builder(114).build()).build()).build())
		//CharEsc ::= "v" 
		.addRule(Rule.withHead(Nonterminal.builder("CharEsc").build()).addSymbol(Sequence.builder(Character.builder(118).build()).build()).build())
		//CharEsc ::= "b" 
		.addRule(Rule.withHead(Nonterminal.builder("CharEsc").build()).addSymbol(Sequence.builder(Character.builder(98).build()).build()).build())
		//CharEsc ::= "f" 
		.addRule(Rule.withHead(Nonterminal.builder("CharEsc").build()).addSymbol(Sequence.builder(Character.builder(102).build()).build()).build())
		//CharEsc ::= "n" 
		.addRule(Rule.withHead(Nonterminal.builder("CharEsc").build()).addSymbol(Sequence.builder(Character.builder(110).build()).build()).build())
		//CharEsc ::= "'" 
		.addRule(Rule.withHead(Nonterminal.builder("CharEsc").build()).addSymbol(Sequence.builder(Character.builder(39).build()).build()).build())
		//CharEsc ::= """ 
		.addRule(Rule.withHead(Nonterminal.builder("CharEsc").build()).addSymbol(Sequence.builder(Character.builder(34).build()).build()).build())
		//CharEsc ::= "&" 
		.addRule(Rule.withHead(Nonterminal.builder("CharEsc").build()).addSymbol(Sequence.builder(Character.builder(38).build()).build()).build())
		//Char ::= "'" (Escape | Space | Graphic) "'" 
		.addRule(Rule.withHead(Nonterminal.builder("Char").build()).addSymbol(Sequence.builder(Character.builder(39).build()).build()).addSymbol(Alt.builder(Nonterminal.builder("Escape").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Character.builder(38).build()).build()))).build(), Nonterminal.builder("Space").build(), Nonterminal.builder("Graphic").addPostConditions(Sets.newHashSet(new RegularExpressionCondition(ConditionType.NOT_MATCH, Alt.builder(Character.builder(39).build()).build()))).build()).build()).addSymbol(Sequence.builder(Character.builder(39).build()).build()).build())
		//Gap ::= "\" WhiteChar WhiteChar* "\" 
		.addRule(Rule.withHead(Nonterminal.builder("Gap").build()).addSymbol(Sequence.builder(Character.builder(92).build()).build()).addSymbol(Nonterminal.builder("WhiteChar").build()).addSymbol(Star.builder(Nonterminal.builder("WhiteChar").build()).build()).addSymbol(Sequence.builder(Character.builder(92).build()).build()).build())
		//GenDecl ::= Fixity Integer? Ops 
		.addRule(Rule.withHead(Nonterminal.builder("GenDecl").build()).addSymbol(Nonterminal.builder("Fixity").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("Integer").build()).build()).addSymbol(Nonterminal.builder("Ops").build()).build())
		//GenDecl ::= Vars "::" (Context "=>")? Type 
		.addRule(Rule.withHead(Nonterminal.builder("GenDecl").build()).addSymbol(Nonterminal.builder("Vars").build()).addSymbol(Sequence.builder(Character.builder(58).build(), Character.builder(58).build()).build()).addSymbol(org.jgll.regex.Opt.builder(Sequence.builder(Nonterminal.builder("Context").build(), Sequence.builder(Character.builder(61).build(), Character.builder(62).build()).build()).build()).build()).addSymbol(Nonterminal.builder("Type").build()).build())
		//GenDecl ::= 
		.addRule(Rule.withHead(Nonterminal.builder("GenDecl").build()).build())
		//GDRHS ::= Guards "=" Exp GDRHS? 
		.addRule(Rule.withHead(Nonterminal.builder("GDRHS").build()).addSymbol(Nonterminal.builder("Guards").build()).addSymbol(Sequence.builder(Character.builder(61).build()).build()).addSymbol(Nonterminal.builder("Exp").build()).addSymbol(org.jgll.regex.Opt.builder(Nonterminal.builder("GDRHS").build()).build()).build())
		//IDecls ::= "{" IDecl* "}" 
		.addRule(Rule.withHead(Nonterminal.builder("IDecls").build()).addSymbol(Sequence.builder(Character.builder(123).build()).build()).addSymbol(Star.builder(Nonterminal.builder("IDecl").build()).build()).addSymbol(Sequence.builder(Character.builder(125).build()).build()).build())
		.build();
}
