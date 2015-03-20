module haskell::specification::HaskellDD

extend haskell::specification::Lexical;

syntax Module
     = "module" ModId Exports? "where" Body 
     | Body
     ;

syntax Body	
     = "{" ";"* ImpDecls ";" TopDecls "}"
 	 | "{" ";"* ImpDecls "}"
	 | "{" ";"* TopDecls "}"
	 | "{" ";"* "}"
	 
	 | align (DImpDecls DTopDecls)
	 ; 

syntax ImpDecls	
     = ImpDecl (";" ImpDecl?)*
     ;
     
syntax DImpDecls
     = align (offside (ImpDecl (";" ImpDecl?)*))* 
     ;
 
syntax Exports	
     = "(" {Export ","}* ","? ")"
     ;
 
syntax Export	
     = QVar
	 | QCon ( ("(" ".." ")") | ("(" { (QVar | Con) "," }* ")") )? 
	 | "module" ModId
	 ;
 
syntax ImpDecl	
     = "import" "qualified"? ModId ("as" ModId)? ImpSpec?
	 ;
 
syntax ImpSpec	
     = "(" {Import ","}* ","? ")"
	 | "hiding" "(" { Import ","}* ","? ")"
	 ;
 
syntax Import	
     = Var
	 | ConId ( ("(" ".." ")") | ("(" { (QVar | Con) "," }* ")") )?
	 ;

syntax CName	
     = Var 
     | Con
     ;
 
syntax TopDecls	
     = TopDecl (";" TopDecl?)*
     ;
     
syntax DTopDecls
     = align (offside (TopDecl (";" TopDecl?)*))*
     ;

syntax TopDecl	
     = "data" (Context "=\>")? Type ("=" Constrs)? Deriving?
	 | "data" (Context "=\>")? Type ("::" Kind)? "where" GADTDecls      			// Generalized Abstarct Data Types extension
	 | "newtype" (Context "=\>")? Type "=" NewConstr Deriving?
	 | "class" (Context "=\>")? Type Fds? ("where" CDecls)?
	 | "instance" CType ("where" CDecls)?            // Flexible instances
	 | "deriving" "instance" (Context "=\>")? QTyCls Inst 				// Extension
	 | "default" {Type ","}*
	 | "foreign" FDecl
	 | Decl
	 | InfixExp
	 ;
 
syntax Fds 
     = "|" {(VarId "-\>" VarId) ","}+
     ;                                        
 
syntax Decls	
     = "{" {Decl? ";"}+ "}"
     | align DDeclsLngstMtch*
     ;
     
lexical DDeclsLngstMtch
      = (offside DDecls) ds Whitespace wsp 
      		when(endsWith(ds.rExt,";") || 
      			   endOfFile(wsp.rExt) || startsWith(wsp.rExt, "in") || indent(wsp.rExt) <= indent(ds.lExt))
      ;

/*
syntax DDecls 
     = Decl (";" Decl?)*
     | (";" Decl?)+
     ;
*/     
syntax DDecls
     = DDecls ";" Decl 
     | DDecls ";"
     | ";" Decl
     | Decl
     | ";"
     ;
     
syntax Decl	
     = GenDecl
	 | FunLHS RHS
	 | AssociatedTypeDecl
	 ;

syntax CDecls
     = "{" {CDecl? ";"}+ "}"
     | align CDDeclsLngstMtch*
     ;
     
lexical CDDeclsLngstMtch
      = (offside DCDecls) ds Whitespace wsp 
      		when(endsWith(ds.rExt,";") ||
      			   endOfFile(wsp.rExt) || indent(wsp.rExt) <= indent(ds.lExt))
      ;

/*     
syntax DCDecls
     = CDecl (";" CDecl?)*
     | (";" CDecl?)+
     ;
*/
syntax DCDecls
     = DCDecls ";" CDecl
     | DCDecls ";"
     | ";" CDecl
     | CDecl
     | ";"
     ;

syntax CDecl	
     = GenDecl
     | FunLHS RHS
     | AssociatedTypeDecl
	 ;
 
syntax GenDecl	
     = Vars "::" CType	    
	 | Fixity Integer? Ops
	 ;

syntax GADTDecls
     = "{" {GADTDecl ";"}+ "}"
     | align DGADTDeclsLngstMtch+
     ;

lexical DGADTDeclsLngstMtch
      = (offside DGADTDecls) ds Whitespace wsp 
      		when(endsWith(ds.rExt,";") || 
     			   endOfFile(wsp.rExt) || indent(wsp.rExt) <= indent(ds.lExt))
      ;

syntax DGADTDecls
     = DGADTDecls ";" GADTDecl
     | DGADTDecls ";"
     | ";" GADTDecl
     | GADTDecl
     | ";"
     ;

syntax GADTDecl
     = TyCon "::" CType
     | TyCon "::" "{" {(Var ("::" CType)?) ","}+ "}" "-\>" CType
     ;
     
     
// Associated type declarations
syntax AssociatedTypeDecl
     = "type" "family"? Type ("::" Kind)?  ("where" CDecls)? 
     | "type" "instance"? TypeFamilyInstEqn
     ;
     
syntax TypeFamilyInstEqn
     = Type "=" CType
     ;
     
syntax CType
     = "forall" TVBinder* "." CType
     | Context "=\>" CType
     | Type
     ;     
     
syntax TVBinder
     = TyVar                  
	 | "(" TyVar "::" Kind ")"
	 ;     
	 
syntax Kind
     = AKind* ("-\>" Kind)?
     ;

syntax AKind
     = "*"
     | "(" Kind ")"
     | PKind
     | TyVar
     ;

syntax PKind
     = QTyCon
     | "(" ")"
     | "(" Kind "," { Kind "," }+ ")"
     | "[" Kind "]"
     ;
     
 
syntax Ops	
     = { Op "," }+
     ;

syntax Vars
     = { Var ","}+
     ;

syntax Fixity	
     = "infixl"
     | "infixr" 
     | "infix"
     ;
 
syntax Type	
     = BType 
     | BType "-\>" CType 
     | BType "~" BType
     | BType Op \ "." Type    
     ;
 
syntax BType	
     = BType? "!"? AType	    
     ;
 
syntax AType	
     = GTyCon
	 | TyVar
	 | "(" "#"? { CType "," }+ "#"? ")"  // GHC Extension: unboxed tuples	    
	 | "[" CType "]"
	 | "(" CType "::" Kind ")"	    				    
	 ;
	 
syntax GTyCon	
     = QTyCon
	 | "(" ")"	    		
	 | "[" "]"	    		
	 | "(" "-\>" ")"	     
 	 | "(" ","+ ")"	
 	 | "(#" ","+ "#)"
 	 | "[:" ":]" 
 	 | "(" "~#" ")"
 	 | "(" "~" ")"   
 	 | "(" QTyConSym ")" 
 	 ;
 
syntax Context	
     = BType "~" BType
	 | BType	
	 ;

syntax Class	
     = QTyCls AType* ("~" Class)? // To deal with flexible contexts and type equality
	 ;
	 
syntax SContext	
     = SimpleClass
	 | "(" { SimpleClass "," }* ")"
	 ;

syntax SimpleClass	
     = QTyCls TyVar
     ;
 
syntax Constrs	
     = { Constr "|" }+
     ;

syntax Constr	
     = Con ("!"? AType)*                                   
	 | BType ConOp BType	    
	 | ("forall" TVBinder* ".")? Con "{" { FieldDecl ","}* "}"
	 | "forall" TVBinder* "." CType
	 ;

syntax NewConstr	
     = Con AType
	 | Con "{" Var "::" CType "}"
	 ;

syntax FieldDecl	
     = Vars "::" CType
     ;

syntax Deriving	
     = "deriving" (DClass | ("(" { DClass "," }* ")") )	    
     ;

syntax DClass	
     = QTyCls
     ;
 
syntax Inst	
     = GTyCon
	 | "(" GTyCon TyVar* ")"	              
	 | "(" TyVar "," { TyVar "," }+ ")"	    
	 | "[" TyVar "]"
	 | "(" TyVar "-\>" TyVar ")"	             
	 ;
 
syntax FDecl	
     = "import" CallConv Safety? Impent Var "::" FType	    
	 | "export" CallConv Expent Var "::" FType	         
	 ;

syntax CallConv
     = "ccall" 
     | "stdcall" 
     | "cplusplus"
	 | "jvm" 
     | "dotnet"
     | "javascript"
     | "capi"
     | "prim"
	 ;

syntax Impent	
     = String?	    
     ;

syntax Expent
     = String?	    
     ;

syntax Safety	
     = "unsafe" 
     | "safe"
     ;
 
syntax FType	
     = FRType
	 | FAType  "-\>"  FType
	 ;

syntax FRType	
     = FAType
	 | "("")"
	 ;

syntax FAType	
     = QTyCon AType*
     ;
 
syntax FunLHS
     = Var APat+
	 | Pat ( VarOp \ "!" Pat)?
	 | "(" FunLHS ")" APat+
	 ;
 
syntax RHS	
     = "=" Exp ("where" Decls)?
	 | GDRHS ("where" Decls)?
	 ;
 
syntax GDRHS
     = Guards "=" Exp GDRHS?
     ;
 
syntax Guards	
     = "|" { Guard "," }+
     ;

syntax Guard	
     = Pat "\<-" InfixExp	    
	 | "let" Decls	         
	 | InfixExp	         
	 ;
 
syntax Exp	
     = InfixExp1 "::" CType
     | InfixExp1 "-\<" Exp
     | InfixExp1 "\>-" Exp 
     | InfixExp1 "-\<\<" Exp 
     | InfixExp1 "\>\>-" Exp
	 | InfixExp
	 ;
 
syntax InfixExp1
     = LExp1 QOp InfixExp
	 | "-" InfixExp	         
	 | LExp1
	 ;
 
syntax InfixExp	
     = LExp1 QOp InfixExp
	 | "-" InfixExp	         
	 | LExp
	 | "$(" Exp ")"					// Template Haskell
	 ;
 
syntax LExp	
     = "\\" APat+ "-\>" Exp
	 | "let" Decls "in" Exp
	 | "if" Exp ";"? "then" Exp ";"? "else" Exp
	 | "case" Exp "of" Alts
	 | "do" Stmts
	 | "proc" AExp "-\>" Exp         // Arrow notation extension                 
	 | FExp
	 ;
	 
syntax LExp1	
     = "case" Exp "of" Alts
     | "do" Stmts         
	 | FExp
	 ;
	 
syntax FExp	
     = FExp? AExp	    
     ;
 
syntax AExp	
     = QVar	                             
	 | GCon !>> "."							   			// To disambiguate with "." in QualifiedNames        
	 | LiteralH
	 | "(" "#"? ","? { Exp "," }+ ","? "#"? ")"		   // GHC Extension: Unboxed tuples
	 | "[" { Exp ","}+ "]"
	 | "[" Exp ("," Exp)? ".." Exp? "]"	    
	 | "[" Exp "|" { Qual "," }+ "]"
	 | "(" InfixExp QOp ")"
	 | "(" QOp \ "-" InfixExp ")" 
	 | AExp "{" { FBind "," }* "}"
	 | "[|" Exp "|]"
	 | "[||" Exp "||]"
	 | "[t|" CType "|]"
	 | "[p|" InfixExp "|]"
	 | "[d|" TopDecls "|]"
	 ;
 
syntax Qual	
     = Pat "\<-" Exp
     | (Var | "_") "::" CType "\<-" Exp
	 | "let" Decls
	 | Exp	    
	 ;
 
syntax Alts	
     = "{" {Alt? ";"}+ "}"
     | align DAltsLngstMtch*
     ;
     
lexical DAltsLngstMtch
      = (offside DAlts) as Whitespace wsp 
      		when(endsWith(as.rExt,";") || 
     			   endOfFile(wsp.rExt) || indent(wsp.rExt) <= indent(as.lExt) || startsWith(wsp.rExt,")"))
      ;

/*
syntax DAlts
     = Alt (";" Alt?)*
     | (";" Alt?)+
     ;
*/
syntax DAlts
     = DAlts ";" Alt
     | DAlts ";"
     | ";" Alt
     | Alt
     | ";"
     ; 
    
syntax Alt
     = Pat "-\>" Exp ("where" Decls)?
	 | Pat GDPat ("where" Decls)?
	 ;
 
syntax GDPat	
     = Guards "-\>" Exp GDPat?
     ;

syntax Stmts	
     = "{" Stmt* Exp ";"? "}"
     | align (align (offside DStmts)* DExpLngstMtch)
     | offside((align (offside DStmts)*) stmts when(endsWith(stmts.rExt, ";")) (Exp ";"?)) body Whitespace wsp
     			when(endOfFile(wsp.rExt) || indent(wsp.rExt) <= indent(body.lExt) || startsWith(wsp.rExt,")"))
     ;

syntax Stmt
     = Qual? ";"
     | "rec" Stmts				// Arrow notation extension
	 ;
	 	 
lexical DExpLngstMtch
      = (offside (Exp ";"?)) exp Whitespace wsp 
      		when(endsWith(exp.rExt,";") || 
      			    endOfFile(wsp.rExt) || indent(wsp.rExt) <= indent(exp.lExt) || startsWith(wsp.rExt,")"))
      ;

/*  
syntax DStmts
     = DStmt (";" DStmt?)*
     | (";" DStmt?)+
     ;
*/
syntax DStmts
     = DStmts ";" DStmt
     | DStmts ";"
     | ";" DStmt
     | DStmt
     | ";"
     ;

syntax DStmt
     = Qual
     | "rec" Stmts				// Arrow notation extension
	 ;
	 
syntax FBind	
     = QVar ("=" Exp)?
     ;
 
syntax Pat	
     = LPat QConOp Pat
	 | LPat
	 ;
 
syntax LPat	
     = APat
	 | "-" (Integer | Float)
	 | GCon APat+
	 ;
 
syntax APat
     = Var ( "@" APat)?
     | "!" APat						 // GHC Extension: Bang patterns
	 | GCon
	 | QCon "{" { FPat "," }* "}"
	 | LiteralH
	 | "_" !>> [A-Za-z_\-]
	 | "(" "#"? {Pat ","}+ "#"? ")"  // GHC Extension: unboxed types
	 | "[" { Pat "," }+ "]"
	 | "~" APat
	 | "(" (Var | "_") "::" CType ")"        // Scoped Type Variables
	 ;
 
syntax FPat	
     = QVar ("=" Pat)?
     | ".."					// GHC Extension RecordWildCards
     ;
 
syntax GCon
     = "(" ")"
	 | "[" "]"
	 | "(" ","+ ")"
	 | QCon
	 ;
 
syntax Var	
     = VarId
     | "(" VarSym ")"
     ;     

syntax QVar	
     = QVarId 
     | "(" QVarSym ")"
     ;

syntax Con	
     = ConId 
     | "(" ConSym ")"
     ;

syntax QCon	
     = QConId 
     | "(" GConSym ")"
     ;

syntax VarOp
     = VarSym 
     | "`"  VarId "`"
     ;

syntax QVarOp	
     = QVarSym 
     | "`" QVarId "`"
     ;

syntax ConOp	
     = ConSym 
     | "`" ConId "`"
     ;

syntax QConOp
     = GConSym 
     | "`" QConId "`"
     ;

syntax Op	
     = VarOp 
     | ConOp
     ;

syntax QOp	
     = QVarOp 
     | QConOp
     ;

syntax GConSym	
     = ":" 
     | QConSym
     ;
     
syntax QTyConSym
     = "*"
     | QConSym
     | QVarSym
     ;
     
public loc l = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/haskell/Haskell.java|;

public void main() {
	generate(#Module, "", l);
}