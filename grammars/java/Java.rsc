/**
 *  Derived from  the main text of the Java Language Specification
 *
 *  author: Ali Afroozeh
 */
module java::natural::Java

extend java::specification::Lexical;


/************************************************************************************************************************
 * Types, Values, and Variables
 ***********************************************************************************************************************/

syntax Type 
     = PrimitiveType 
     | ReferenceType
     ;

syntax PrimitiveType 
     = "byte"
     | "short"
     | "char"
     | "int"
     | "long"
     | "float"
     | "double"
     | "boolean"
     ;
     
syntax ReferenceType
     = TypeDeclSpecifier TypeArguments?
     | ArrayType
     ;
     
syntax ReferenceTypeNonArrayType
     = TypeDeclSpecifier TypeArguments?
     ;
     
syntax TypeList
     = {Type ","}+
     ;     
     
syntax TypeName
     = QualifiedIdentifier
     ;
     
syntax TypeVariable
     = Identifier
     ;
     
syntax ArrayType
     = Type "[" "]"
     ;
     
syntax TypeParameters 
     = "\<" {TypeParameter ","}+ "\>"
     ;

syntax TypeParameter 
     = TypeVariable TypeBound?
     ;     
     
syntax TypeBound 
     = "extends" {ReferenceType "&"}+
     ;  
       
syntax TypeArguments 
     = "\<" {TypeArgument ","}+ "\>" 
     ;
        
syntax TypeArgument  // fix: changed ReferenceType to Type to deal with primitive array types such as < String[] > 
     = Type
     | "?" (("extends" | "super") Type)?  
     ;

syntax QualifiedIdentifier 
     = {Identifier "."}+;

syntax QualifiedIdentifierList 
     = {QualifiedIdentifier  ","}+;

/************************************************************************************************************************
 * Top level
 ***********************************************************************************************************************/

start syntax CompilationUnit 
    = PackageDeclaration? ImportDeclaration* TypeDeclaration*
      ;
  
syntax PackageDeclaration 
     = Annotation* "package"  QualifiedIdentifier ";" 
     ;  
  
syntax ImportDeclaration 
    = "import"  "static"?  {Identifier "."}+ ("." "*")? ";" 
    ;
  
syntax TypeDeclaration 
    = ClassDeclaration
    | InterfaceDeclaration 
    | ";" 
    ;  
    
 /************************************************************************************************************************
 * Classes
 ***********************************************************************************************************************/
  
syntax ClassDeclaration 
     = NormalClassDeclaration
     | EnumDeclaration
     ;
    
syntax NormalClassDeclaration 
    = ClassModifier* "class" Identifier TypeParameters? ("extends" Type)? ("implements" TypeList)? ClassBody;

syntax ClassModifier
     = Annotation
     | "public"
     | "protected" 
     | "private"
     | "abstract" 
     | "static" 
     | "final" 
     | "strictfp"
     ;
     
syntax ClassBody 
    = "{" ClassBodyDeclaration* "}"
    ;

syntax ClassBodyDeclaration
     = ClassMemberDeclaration
     | InstanceInitializer
     | StaticInitializer 
     | ConstructorDeclaration
     ;
     
syntax InstanceInitializer
     = Block
     ;
     
syntax StaticInitializer
     = "static" Block
     ;

syntax ConstructorDeclaration
     = ConstructorModifier* ConstructorDeclarator Throws? ConstructorBody
     ;

syntax ConstructorModifier
     = Annotation 
     | "public"
     | "protected"
     | "private"
     ;
     
syntax ConstructorDeclarator
     = TypeParameters? Identifier "(" FormalParameterList? ")"
     ;
     
syntax ConstructorBody
     = Block
     ;
     
syntax NonWildTypeArguments
     = "\<" { ReferenceType ","}+ "\>"
     ;

syntax ClassMemberDeclaration
     = FieldDeclaration 
     | MethodDeclaration 
     | ClassDeclaration 
     | InterfaceDeclaration
     | ";"
	 ;
	 
/************************************************************************************************************************
 * Interfaces
 ***********************************************************************************************************************/	 

syntax InterfaceDeclaration 
     = NormalInterfaceDeclaration
     | AnnotationTypeDeclaration
     ;  
	 
syntax NormalInterfaceDeclaration 
     =  InterfaceModifier* "interface" Identifier TypeParameters? ("extends" TypeList)? InterfaceBody
     ;
     
syntax InterfaceModifier
     = Annotation
     | "public"
     | "protected"
     | "private"
     | "abstract"
     | "static"
     | "strictfp"
     ;
     
syntax InterfaceBody
     = "{" InterfaceMemberDeclaration* "}"
     ;
     
syntax InterfaceMemberDeclaration
     = ConstantDeclaration 
     | AbstractMethodDeclaration 
     | ClassDeclaration 
     | InterfaceDeclaration
     | ";"
     ;
     
syntax ConstantDeclaration
     = ConstantModifier* Type VariableDeclarators ";"
     ;
     
syntax ConstantModifier
     = Annotation
     | "public"
     | "static"
     | "final"
     ;
     
syntax AbstractMethodDeclaration
     = AbstractMethodModifier* TypeParameters? Result MethodDeclarator Throws? ";"
     ;
     
syntax AbstractMethodModifier
     = Annotation
     | "public"
     | "abstract"
     ;          

syntax AnnotationTypeDeclaration 
     = InterfaceModifier* "@" "interface" Identifier AnnotationTypeBody
     ;
     
syntax AnnotationTypeBody
     = "{" AnnotationTypeElementDeclaration* "}"
     ;

syntax AnnotationTypeElementDeclaration 
     = AbstractMethodModifier* Type Identifier "(" ")" ("[" "]")* DefaultValue? ";" 
     | ConstantDeclaration
     | ClassDeclaration
     | InterfaceDeclaration
     | AnnotationTypeDeclaration
     | ";"
     ;
     
syntax DefaultValue
     = "default" ElementValue
     ;
	 
/************************************************************************************************************************
 * Fields
 ***********************************************************************************************************************/
	 
syntax FieldDeclaration
     = FieldModifier* Type VariableDeclarators ";"
     ;

syntax FieldModifier
     = Annotation 
     | "public" 
     | "protected" 
     | "private"
     | "static"
     | "final" 
     | "transient"
     | "volatile"
     ;

syntax VariableDeclarators 
    = {VariableDeclarator ","}+
    ;

syntax VariableDeclarator
     = VariableDeclaratorId ("=" VariableInitializer)?
     ;
     
syntax VariableDeclaratorId 
    = Identifier ("[" "]")*
    ;

syntax VariableInitializer 
    = ArrayInitializer
    | Expression
    ;
    
syntax ArrayInitializer 
    = "{"  {VariableInitializer ","}* ","? "}"
    ;
    
/************************************************************************************************************************
 * Methods
 ***********************************************************************************************************************/

syntax MethodDeclaration
     = MethodHeader MethodBody
     ;
     
syntax MethodHeader 
     = MethodModifier* TypeParameters? Result MethodDeclarator Throws?
     ;
     
syntax MethodDeclarator
     = Identifier "(" FormalParameterList? ")"
     | MethodDeclarator "[" "]"
     ;
     
syntax FormalParameterList
     = (FormalParameter ",")* LastFormalParameter
     ;
     
syntax FormalParameter
     = VariableModifier* Type VariableDeclaratorId
     ;         

syntax VariableModifier 
    = "final"
    | Annotation
    ;

syntax LastFormalParameter
     = VariableModifier* Type "..." VariableDeclaratorId
     | FormalParameter
     ;
     
syntax MethodModifier 
     = Annotation
     | "public" 
     | "protected"
     | "private"
     | "abstract" 
     | "static"
     | "final"
     | "synchronized"
     | "native"
     | "strictfp"
     ;
     
syntax Result
     = Type
     | "void"
     ;

syntax Throws
     = "throws" {ExceptionType ","}+
     ;
     
syntax ExceptionType
     = TypeName
     ;    
            
syntax MethodBody
     = Block
     | ";"
     ;

    
/************************************************************************************************************************
 * Annotations
 ***********************************************************************************************************************/
  
syntax Annotation 
    = "@" TypeName  "(" {ElementValuePair ","}* ")"
    | "@" TypeName ( "(" ElementValue ")" )? 
    ;

syntax ElementValuePair 
    = Identifier "=" ElementValue
    ;

syntax ElementValue 
    = Expression
    | Annotation
    | ElementValueArrayInitializer
    ;

syntax ElementValueArrayInitializer 
    = "{" ElementValues? ","? "}"
    ;

syntax ElementValues 
     = {ElementValue ","}+
     ;

/************************************************************************************************************************
 * Enums
 ***********************************************************************************************************************/
     
syntax EnumDeclaration
     = ClassModifier* "enum" Identifier ("implements" TypeList)? EnumBody
     ;

syntax EnumBody
     = "{" {EnumConstant ","}* ","? EnumBodyDeclarations? "}"
     ;     
     
syntax EnumConstant
     = Annotation* Identifier Arguments? ClassBody?
     ;
     
syntax Arguments
     = "(" ArgumentList? ")"
     ;
     
syntax EnumBodyDeclarations
     = ";" ClassBodyDeclaration*
     ;

/************************************************************************************************************************
 * Statements
 ***********************************************************************************************************************/

syntax Block 
     = "{" BlockStatement* "}"
     ;

syntax BlockStatement 
     = LocalVariableDeclarationStatement
     | ClassDeclaration
     | Statement
     ;

syntax LocalVariableDeclarationStatement 
     = VariableModifier*  Type VariableDeclarators ";"
     ;

syntax Statement
     = Block
     | ";" 
     | StatementExpression ";"
     | "assert" Expression (":" Expression)? ";" 
     | "switch" "(" Expression ")" "{" SwitchBlockStatementGroup* SwitchLabel* "}" 
     | "do" Statement "while" "(" Expression ")" ";" 
     | "break" Identifier? ";" 
     | "continue" Identifier? ";" 
     | "return" Expression? ";" 
     | "synchronized" "(" Expression ")" Block 
     | "throw" Expression ";" 
     | "try" Block (CatchClause+ | (CatchClause* Finally))
     | "try" ResourceSpecification Block CatchClause* Finally? 
     | Identifier ":" Statement
     | "if" "(" Expression ")" Statement !>>> "else"
     | "if" "(" Expression ")" Statement "else" Statement
     | "while" "(" Expression ")" Statement
     | "for" "(" ForInit? ";" Expression? ";" ForUpdate? ")" Statement
     | "for" "(" FormalParameter ":" Expression ")" Statement
     ;

syntax StatementExpression
     = Expression !lt !gt
     ;
    
syntax CatchClause 
    = "catch" "(" VariableModifier* CatchType Identifier ")" Block
    ;

syntax CatchType  
    =  {QualifiedIdentifier "|"}+
    ;

syntax Finally 
    = "finally" Block
    ;

syntax ResourceSpecification 
    = "(" Resources ";"? ")"
    ;

syntax Resources 
    = {Resource ";"}+
    ;

syntax Resource 
     = VariableModifier* ReferenceType VariableDeclaratorId "=" Expression
     ; 

syntax SwitchBlockStatementGroup 
	     = SwitchLabel+ BlockStatement+
     ;

syntax SwitchLabel 
     = "case" ConstantExpression ":"
     | "default" ":"
     ;

syntax LocalVariableDeclaration 
    = VariableModifier* Type { VariableDeclarator ","}+
    ;

syntax ForInit 
     = {StatementExpression ","}+
     | LocalVariableDeclaration
     ;
     
syntax ForUpdate 
     = {StatementExpression ","}+
     ;    

/************************************************************************************************************************
 * Expressions
 ***********************************************************************************************************************/

syntax Expression
     = Expression !io "." Identifier
     | Expression "." "this"
	 | Expression "." "new" TypeArguments? Identifier TypeArgumentsOrDiamond? "(" ArgumentList? ")" ClassBody?
	 | Expression "." NonWildTypeArguments ExplicitGenericInvocationSuffix     
     | Expression "." "super" ("." Identifier)? Arguments
     | Type "." "class"
     | "void" "." "class"
	 | Expression !brackets "(" ArgumentList? ")"     
     | Expression !newArray "[" Expression "]"
     > Expression "++"
     | Expression "--"
     > up: "+" !>> "+" Expression
     | um: "-" !>> "-" Expression
     | "++" Expression
     | "--" Expression 
     | "!" Expression
     | "~" Expression
     | "new" ClassInstanceCreationExpression
     | newArray: "new" ArrayCreationExpression
     | "(" PrimitiveType ")" Expression
     | "(" ReferenceType ")" Expression !up !um 
     > left( Expression "*" Expression 
     |       Expression "/" Expression
     |       Expression "%" Expression )
     > left( Expression "+" !>> "+" Expression
     |       Expression "-" !>> "-" Expression )
     > left( Expression "\<\<" Expression 
     |       Expression "\>\>" !>> "\>" Expression
     |       Expression "\>\>\>" Expression )
     > left( 
       lt:   Expression "\<" !>> "=" !>> "\<" Expression
     | gt:   Expression "\>" !>> "=" !>> "\>" Expression 
     |       Expression "\<=" Expression
     |       Expression "\>=" Expression
     | io:   Expression "instanceof" Type ) 
     > left( Expression "==" Expression
     |       Expression "!=" Expression )
     > left  Expression "&" !>> "&" Expression
     > left  Expression "^" Expression
     > left  Expression "|" !>> "|" Expression 
     > left  Expression "&&" Expression
     > left  Expression "||" Expression
     > right Expression "?" Expression ":" Expression 
     > right Expression !lt !gt AssignmentOperator Expression
     | brackets: "(" Expression ")"
     | Primary
     ;
     
syntax Primary
	 = Literal
     | "this"
     | "super"
     | Identifier
     ;     

syntax ClassInstanceCreationExpression
     =  TypeArguments? TypeDeclSpecifier TypeArgumentsOrDiamond? "(" ArgumentList? ")" ClassBody? 
     ;
     
syntax TypeArgumentsOrDiamond 
     = "\<" "\>" 
     | TypeArguments
     ;     
     
syntax ArgumentList
     = {Expression ","}+
     ;     

syntax ArrayCreationExpression
	 = (PrimitiveType | ReferenceType) DimExpr+ ("[" "]")*
	 | (PrimitiveType | ReferenceTypeNonArrayType) ("[" "]")+ ArrayInitializer
     ;

syntax DimExpr
     = "[" Expression "]"
     ;
     
syntax ConstantExpression
     = Expression
     ;
     
syntax ClassName
	 = QualifiedIdentifier
	 ;
	 
syntax MethodName
     = QualifiedIdentifier
     ;
     
syntax TypeDeclSpecifier
     = Identifier (TypeArguments? "." Identifier)*
     ;     

syntax SuperSuffix 
     =  Arguments 
     | "." Identifier Arguments?
     ;

 syntax ExplicitGenericInvocationSuffix 
 	  = "super" SuperSuffix
 	  | Identifier Arguments
 	  ;
     
     