/**
 * 
 * Extracted from the OCaml reference manual 4.00
 *
 * Ali Afroozeh 
 * 
 */

module ocaml::specification::OCaml

extend ocaml::specification::Lexical;

// Top-level     		

syntax TopLevel 
     = TopLevelPhrase*
     ;

syntax TopLevelPhrase 
	 = Definition
   	 | Expr
   	 ;
   	 
syntax CompilationUnit
     = ModuleItems?
     ;    	 
   	  
// Names
syntax ValuePath 
     = valuePath: (ModulePath ".")? ValueName
     ;

syntax ValueName 
     = LowercaseIdentifier 
     | "(" OperatorName \ Keywords ")"
     ;
     
syntax OperatorName
     = PrefixSymbol
     | InfixSymbol
     ;     

syntax TagName 
     = Ident
     ;

syntax TypeconstrName 
     = LowercaseIdentifier
     ;

syntax TypeConstr 
     = (ExtendedModulePath ".")? TypeconstrName
     ;

syntax ConstrName 
     = CapitalizedIdentifier
     ;

syntax LabelName 
     = LowercaseIdentifier
     ;

syntax ModuleName 
     = CapitalizedIdentifier
     ;

syntax FieldName 
     = LowercaseIdentifier
     ;

syntax ClassName 
     = LowercaseIdentifier
     ;

syntax InstVarName 
     = LowercaseIdentifier
     ;

syntax MethodName 
     = LowercaseIdentifier
     ;

syntax ModTypeName 
     = Ident
     ;

syntax ModulePath 
     = (ModuleName ".")* ModuleName
     ;

syntax Constr 
     = (ModulePath ".")? ConstrName 
     ;

syntax Field 
     = (ModulePath ".")? FieldName
     ;

syntax ClassPath 
     = (ExtendedModulePath "." )? ClassName
     ;

syntax ModTypePath 
     = (ExtendedModulePath "." )? ModTypName
     ;

syntax ModTypName 
     = Ident
     ;

syntax ExtendedModulePath 
     = { ExtendedModuleName  "." }+
     ;
     
syntax ExtendedModuleName
     = ModuleName ("(" ExtendedModulePath ")")*
     ;     


// Type expressions

syntax Typexpr 
     = non-assoc "?"? LabelName ":" Typexpr 
     > Typexpr TypeConstr
     > non-assoc star: Typexpr "*" {Typexpr_ "*"}+
     > right arrow: Typexpr "-\>" Typexpr
     > Typexpr "as" "\'" Ident 
     > "private" Typexpr
     | "\'" Ident
     | "_" !>> [a-zA-Z0-9]
     | "(" Typexpr ")"
     | TypeConstr
     | "(" Typexpr ("," Typexpr)+ ")" TypeConstr
     | PolymorphicVariantType
     | "\<" ".."? "\>"
     | "\<" {MethodType ";"}+ (";" "..")? "\>"
     | "#" ClassPath
     | Typexpr "#" ClassPath
     | "(" {Typexpr ","}+ ")" "#" ClassPath
     | "(" "module" PackageType ")"  
     ;

syntax Typexpr_
     = Typexpr !star !arrow
     ;
    
syntax PolymorphicVariantType
     = "[" "|"? {TagSpec "|"}* "]"
     | "[\>" {TagSpec "|"}* "]"
     | "[\<"  "|"? {TagSpecFull "|"}+ ("\>" ("`" TagName)+ )?  "]"
     ;
       
syntax PolyTypExpr 
	= Typexpr
     | ("\'" Ident)+ "." Typexpr
     ;
       
syntax MethodType 
	= MethodName ":" PolyTypExpr
     ;  
       
syntax TagSpec
     = "`" TagName ("of" Typexpr)?
     | Typexpr
     ;

syntax TagSpecFull 
     = "`" TagName ("of" Typexpr)? ("&" Typexpr)*
     | Typexpr
     ;


// Expressions

syntax Expr 
     = prefix: 				PrefixSymbol Expr
     > non-assoc field: 			Expr "." Ident  
     | non-assoc dotBracket1: 		Expr ".(" Expr ")"
     | non-assoc dotBracket2: 		Expr ".[" Expr "]"
     | non-assoc dotBracket3: 		Expr ".{" Expr "}"
     > hash: 				Expr "#" MethodName
     > non-assoc 
     (
     functionApplication: 	Expr !comma  Arg+
     //| constrExp: 			Constr Expr    To avoid ambiguities with Expr Arg+ as Expr can derive Constr
     //| polyVariant:	 		"`" TagName Expr  To Avoid ambiguities with Constant("`" TagName) Arg(Expr)
     | lazy: 				"lazy" Expr
     | assertExpr: 			"assert" Expr
     )
     > unaryMinus: 			"-"  Expr 
     | floatUnaryMinus:     "-." Expr
     > right infix1: 		Expr InfixSymbol1 Expr
     > left  infix2: 		Expr InfixSymbol2 Expr
     > left  infix3: 		Expr InfixSymbol3 Expr   // to disambiguate [|   5.2026032092;     19132e-10;  -39e-10 |];
     > right coloncolon:	Expr "::" Expr
     > right infix4: 		Expr InfixSymbol4 Expr
     > left  infix5: 		Expr InfixSymbol5 Expr
     | left  uneq:   		Expr "!=" Expr
     > right infix6: 		Expr InfixSymbol6 Expr
     > right infix7: 		Expr InfixSymbol7 Expr
     > non-assoc comma: 	Expr ("," Expr_2)+
     > right assign: 	    Expr "\<-" Expr
     > right infix8: 		Expr InfixSymbol8 Expr
     > ifThenElse: 	 		"if" Expr  "then" Expr_1 "else" Expr
     | ifThen: 		 		"if"  Expr "then" Expr !>>> "else"
     //> semicolon: 	 		Expr ";" !>>  ";"
     > right sep: 	 		Expr ";" Expr
     > match: 		 		"match" Expr "with" PatternMatching
     | function: 	 		"function" PatternMatching
     | fun: 		 		"fun" MultipleMatching
     | tryBlock: 	 		"try" Expr ";"? "with" PatternMatching     
     | letbinding: 	 		"let" "rec"? LetBinding ";"? ("and" LetBinding ";"?)* "in" Expr
     | letModule:	 		"let" "module" ModuleExpr "="  ModuleExpr "in"  Expr 
     | letOpen:             "let" "open" ModulePath "in"  Expr
     | brackets: 			"(" Expr ";"? ")"
  	 | beginEnd: 	 		"begin" Expr ";"? "end"
  	 | brackets1: 	 		"(" Expr ":" Typexpr ")"
  	 | brackets2:	 		"(" Expr ":\>"  Typexpr ")"  
 	 | brackets3: 	 		"(" Expr ":"  Typexpr ":\>"  Typexpr ")"  
 	 | brackets4: 	 		"{\<" InstVarName "=" Expr_1  (";" InstVarName "="  Expr)*  ";"? "\>}"  
  	 | tupl: 		 		"["  {Expr_1 ";"}+ ";"? "]"
     | array: 		 		"[|" {Expr_1 ";"}+ ";"? "|]"
     | record1:	     		"{" Field ("=" Expr_1)? (";" Field ("=" Expr_1)?)* ";"? "}"
     | record2: 	 		"{" Expr "with" Field ("=" Expr_1 )? (";" Field ("=" Expr_1)?)* ";"? "}"
     | whileloop: 	 		"while" Expr "do" Expr ";"? "done"
     | forloop: 			"for" ValueName "=" Expr ("to" | "downto") Expr "do" Expr ";"? "done"
     | new: 				"new" ClassPath
     | object: 		 		"object" ClassBody "end"  
     | moduleExpr: 	 		"(" "module" ModuleExpr  (":" PackageType)? ")"  
     //| valuePath: 	 		ValuePath
     | 						ValueName
	 | constant: 			Constant !constr   // To avoid ambiguities with Expr "." Field
	 |                      ConstrName
     //| 						InstVarName
     ; 
     
syntax Expr_1 
     = Expr !sep
     ;

syntax Expr_2
     = Expr !sep !comma
     ;     
  
     
syntax Arg 
 	 =                Expr !functionApplication !lazy !assertExpr !unaryMinus !floatUnaryMinus !infix1 !infix2 !infix3 
 	                       !coloncolon !infix4 !infix5 !uneq !infix6 !infix7 !comma 
 	                       !infix8 !ifThenElse !ifThen !sep !match !function !fun !tryBlock !letbinding !letModule 
 	| label: 		  Label
     | labelColon:    LabelColon Expr !functionApplication !lazy !assertExpr !unaryMinus !floatUnaryMinus !infix1 !infix2 !infix3 
 	                       			  !coloncolon !infix4 !infix5 !uneq !infix6 !infix7 !comma  
 	                       			  !infix8 !ifThenElse !ifThen !sep !match !function !fun !tryBlock !letbinding !letModule
     | optlabel:      OptLabel
     | optlabelColon: OptLabelColon Expr !functionApplication !lazy !assertExpr !unaryMinus !floatUnaryMinus !infix1 !infix2 !infix3 
 	                       				 !coloncolon !infix4 !infix5 !uneq !infix6 !infix7 !comma  
 	                       				 !infix8 !ifThenElse !ifThen !sep !match !function !fun !tryBlock !letbinding !letModule
     ;
           
syntax PatternMatching 
     =  "|"? Pattern ("when" Expr)? "-\>" Expr InnerPatternMatching* !>>> "|" 
     ;
     
syntax InnerPatternMatching
     = ";"? "|" Pattern ("when" Expr)? "-\>" Expr
     ;     
           
syntax LetBinding 
     =                  Pattern !patternValueName "="  Expr  
     | letBinding:      ValueName Parameter* (":" PolyTypExpr)? (":\>" Typexpr)? "=" Expr 
     | bindingNew:		ValueName ":" "type"  TypeConstr* "."  Typexpr "="  Expr
     ;
	 
syntax MultipleMatching
     = multipleMatching: Parameter+ ("when" Expr)? "-\>" Expr
     ;	 

syntax Parameter 
	= patternParam: Pattern !constrPattern  // Paramter always comes in lists and can match another pattern
     | param1: 		 Label 
     | param2:		 "~" "(" LabelName (":" Typexpr)? ")"
     | param3:		 LabelColon Pattern
     | param4:		 OptLabel 
     | param5:		 "?" "(" LabelName (":" Typexpr)? ("=" Expr)? ")"
     | param6: 		 OptLabelColon Pattern
     | param7: 		 OptLabelColon "(" Pattern ":" Typexpr "=" Expr ")"
     | typeParam:	 "(" "type" TypeconstrName ")"  
     ;

// Patterns

syntax Pattern 
     = lazyPattern:           "lazy" Pattern 
     > constrPattern: 		  Constr Pattern
     > tagNamePattern: 		  "`" TagName Pattern
     > right listCons: 		  Pattern "::" Pattern
     > non-assoc patterns: 	  Pattern "," {Pattern_ ","}+
     > left patternBar: 	  Pattern "|" Pattern
     > patternAs: 			  Pattern "as" ValueName
     | patternValueName: 	  ValueName
     | anyPattern: 			  "_" !>> [a-zA-Z0-9]   // To enforce longest match with identifiers
     | patternConstant: 	  Constant
     | patternRange: 		  CharLiteral ".." CharLiteral   // Extensions
     | patternBrackets: 	  "(" Pattern ")"
     | patternTypxprBrackets: "(" Pattern ":" Typexpr ")"
     | patternHash: 		  "#" TypeconstrName
     | patternRec: 			  "{" Field ("=" Pattern)? (";" Field ("=" Pattern)?)* (";" "_")? ";"? "}"
     | patternTuple: 		  "["  {Pattern ";"}+ ";"? "]"
     | patternArray: 		  "[|" {Pattern ";"}+ ";"? "|]"
     | patternPackage: 		  "(" "module" ModuleName  (":" PackageType)? ")"  
     ;
     
syntax Pattern_
     = Pattern !patterns !patternBar !patternAs
     ;            
         
syntax Constant 
     = posInt: 			 [\-] !<< IntegerLiteral
     | floatLiteral: 	 [\-] !<< FloatLiteral
     | charLiteral: 	 CharLiteral
     | stringLiteral: 	 StringLiteral1
     | constr: 			 Constr
     | falseConstant: 	 "false"
     | trueConstant: 	 "true"
     | emptyParenthesis: "(" ")"
     | emptyBrackets:	 "[" "]"
     | emptyArray: 		 "[|" "|]"
     | emptyCurly: 		 "{\<" "\>}"
     | 					 "`" TagName
     | int32: 			 [\-] !<< Int32Literal  
	 | int64: 			 [\-] !<< Int64Literal  
     | nativeInt: 		 [\-] !<< NativeIntLiteral
     |                   "begin" "end"     // An alias for "(" ")"
     ;

// ModuleExpressions 

syntax Definition 
     = "let" "rec"? LetBinding (";"? "and" LetBinding)* 
     | "val" ValueName ":" Typexpr
     | "external" ValueName ":" Typexpr "=" ExternalDeclaration
     | TypeDefinition
     | ExceptionDefinition
     | ClassDefinition
     | ClassSpecification
     | ClassTypeDefinition
     | "module" ModuleName ( "(" ModuleName ":" ModuleType ")" )* ( ":" ModuleType )? "=" ModuleExpr
     | "module" ModuleName ("(" ModuleName ":" ModuleType ")")* ":" ModuleType
     | "module" "type" ModTypeName "=" ModuleType
     | "module" "type" ModTypeName
     | "module" "rec" ModuleName ":"  ModuleType "="  ModuleExpr  ("and" ModuleName ":"  ModuleType "="  ModuleExpr)*
     | "module" "rec" ModuleName ":"  ModuleType  ("and" ModuleName ":"  ModuleType)*
     | "open" ModulePath
     | "include" ModuleExpr !modulePath 
     | "include" ModuleType
     ;
     
syntax ModuleExpr 
     = modulePath: ModulePath
     |             "struct" ";;"? ModuleItems? "end"
     | 			   "functor" "(" ModuleName ":" ModuleType ")" "-\>" ModuleExpr
     |  		   "functor" "(" ")" "-\>" ModuleExpr
     | 		       ModuleExpr "(" ModuleExpr? ")"
     |   		   ModuleExpr "(" ModuleExpr ":" ModuleType ")"
     |             "(" ModuleExpr ")"
     | 			   "(" ModuleExpr ":" ModuleType ")"
     | 			   "(" "val" Expr  (":" PackageType)? ")" 
     ;      
     
syntax ModuleItems
     = ";;"? ( (Definition ";"?) | (Expr ";"?)) ((";;"? Definition ";"?) | (";;" Expr ";"?))* ";;"?
     ;     
     
// ModuleTypes
     
syntax ModuleType 
     = modTypePath:       ModTypePath
     | sig:               "sig" ( Definition ";;"? )* "end"
     | modTypeOf:         "module" "type" "of" ModuleExpr
     | modTypeWith:       ModuleType "with" ModConstraint ("and" ModConstraint)*
     | bracketModType2:   ModuleType !modTypeWith "(" ModuleType ")"
     > functor:           "functor" "(" ModuleName ":" ModuleType ")" "-\>" ModuleType
     | bracketModType1:   "(" ModuleType ")"
     ;


syntax ModConstraint 
     = "type" TypeParams? TypeConstr "=" Typexpr
     | "type" TypeParameters?  TypeConstr ":="  TypeParameters?  TypeConstr
     | "module" ModulePath "=" ExtendedModulePath
     | "module" ModuleName ":="  ExtendedModulePath  
     ; 	


// Type And Exceptions

syntax TypeDefinition 
     = "type" {TypeDef "and"}+
     ;
     
syntax TypeDef 
     = TypeParams? TypeconstrName TypeInformation
     ;

syntax TypeInformation 
     = TypeEquation? TypeRepresentation? TypeConstraint*
     ;

syntax TypeEquation 
     = "=" Typexpr
     ;
          
syntax TypeRepresentation 
     = "=" "private"? "|"? {ConstrDecl "|"}+
     | "=" "private"? "{" {FieldDecl ";"}+ ";"? "}"
     | "=" "[" {ConstrDecl "|"}* "]"   // Revised syntax
     ;

syntax TypeParams 
     = TypeParam
     | "(" {TypeParam ","}+ ")"
     ;

syntax TypeParam 
     = Variance? "#"? "\'" Ident
     | Variance? "_" !>> [a-zA-Z0-9]
     ;     
     
syntax Variance 
     = "+" 
     | "-"
     ;
     
syntax ConstrDecl 
     = (ConstrName | ("(" ")")) ("of" { Typexpr_ "*" }+)?    
     | ConstrName ":" { Typexpr_ "*" }+ ("-\>"  Typexpr)?
     ;

syntax FieldDecl 
	= "mutable"? FieldName ":" PolyTypExpr
	;

syntax TypeConstraint 
	= "constraint" "\'" Ident "=" Typexpr
	;
     
syntax ExceptionDefinition 
     = "exception" ConstrName ("of" Typexpr_ ("*" Typexpr_ )* )?
     | "exception" ConstrName "=" Constr
     ;
     
// Classes

syntax ClassType 
     = ( Typexpr_ "-\>")* ClassBodyType
     ;

syntax ClassBodyType 
     = "object" ("(" Typexpr ")")? ClassFieldSpec* "end"
     | ("[" Typexpr ("," Typexpr)* "]")? ClassPath
     ;

syntax ClassFieldSpec 
     = "inherit" ClassType
     | "val" "mutable"? "virtual"? InstVarName ":" PolyTypExpr
     | "val" "virtual" "mutable" InstVarName ":" PolyTypExpr
     | "method" "private"? "virtual"? MethodName ":" PolyTypExpr
     | "method" "virtual" "private" MethodName ":" PolyTypExpr
     | "constraint" Typexpr "=" Typexpr
     ;
     
syntax ClassExpr 
     = ClassPath
     | classExprBrackets1: "[" Typexpr ("," Typexpr)* "]" ClassPath
     | classExprBrackets2: "(" ClassExpr ")"
     | classExprBrackets3: "(" ClassExpr ":" ClassType ")"
     | classArgs: ClassExpr ! classArgs Arg+
     | classFun: "fun" Parameter+ "-\>" ClassExpr
     | letClass: "let" "rec"? LetBinding ("and" LetBinding)* "in" ClassExpr
     | object: "object" ClassBody "end"
     ;

syntax ClassField 
     = ("inherit" | "inherit!") ClassExpr ("as" ValueName)?
     | ("val"|"val!") "mutable"? InstVarName (":" Typexpr)? "=" Expr
     | "val" "mutable"? "virtual" InstVarName ":" Typexpr
     | "val" "virtual" "mutable" InstVarName ":" Typexpr
     | ("method" | "method!") "private"? MethodName Parameter* (":" PolyTypExpr)? "=" Expr     
     | "method" "private"? "virtual" MethodName ":" PolyTypExpr
     | "method" "virtual" "private"  MethodName ":" PolyTypExpr
     | "constraint" Typexpr "=" Typexpr
     | "initializer" Expr
     | ("method"|"method!") "private"? MethodName ":" "type"  TypeconstrName+ "."  Typexpr "="  Expr   // Extensions §7.13
     ;
     
syntax ClassDefinition 
	= "class" {ClassBinding "and"}+
	;     
     
syntax ClassBody 
	= ("(" Pattern (":" Typexpr)? ")")? (ClassField ";"?)*
	;

syntax ClassBinding 
	= "virtual"? ("[" TypeParameters "]")? ClassName Parameter* (":" ClassType)? "=" ClassExpr
	;

syntax TypeParameters 
	= "\'" Ident ("," "\'" Ident)*
	;
	 
syntax ClassSpecification 
	= "class" ClassSpec ("and" ClassSpec)*
	;

syntax ClassSpec 
	= "virtual"? ("[" TypeParameters "]")? ClassName ":" ClassType
	;

syntax ClassTypeDefinition 
	= "class" "type" ClasstypeDef ("and" ClasstypeDef)*
	;

syntax ClasstypeDef 
	= "virtual"? ("[" TypeParameters "]")? ClassName "=" ClassBodyType
	;
     
syntax ExternalDeclaration 
     = StringLiteral1+
     ;	 
     

// Extensions

syntax PackageType
     = ModTypePath  
     | ModTypePath "with"  PackageConstraint  ("and" PackageConstraint)*
     ;  

syntax PackageConstraint
     = "type" TypeConstr "="  Typexpr
     ;
