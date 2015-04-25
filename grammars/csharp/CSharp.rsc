/**
 * 
 * Derived from the C# language specification version 5:
 * http://www.microsoft.com/en-us/download/details.aspx?id=7029
 *
 *
 *  author: Ali Afroozeh
 */
 
module csharp::specification::CSharp

extend csharp::preprocess::Lexical;

// Basic concepts

syntax NamespaceName
     = NamespaceOrTypeName
     ;

syntax TypeName 
     = NamespaceOrTypeName
     ;

syntax NamespaceOrTypeName
     = Identifier      
     | Identifier   TypeArgumentList
     | NamespaceOrTypeName   "."   Identifier   TypeArgumentList?
     | QualifiedAliasMember
     ;

// Types

syntax Type
     = TypeName
     | PredefinedType
     | NullableType
     | ArrayType 
     | PointerType
     ;
     
syntax PointerType
     = UnmanagedType   "*"
     | "void"   "*"
     ;
     
syntax UnmanagedType
     = Type
     ;
     

syntax PredefinedType
     = "bool"
     | "byte"
     | "char"
     | "decimal"
     | "double"
     | "float"
     | "int"
     | "long"
     | "object"
     | "sbyte"
     | "short"
     | "string"
     | "uint"
     | "ulong"
     | "ushort"
     ; 


syntax IntegralType
     = "sbyte"
     | "byte"
     | "short"
     | "ushort"
     | "int"
     | "uint"
     | "long"
     | "ulong"
     | "char"
     ;

syntax NullableType
     = Type  "?"
     ;

syntax ReferenceType
     = TypeName
     | ArrayType
     ;
     
syntax RankSpecifier
     = "["   ","*   "]"
     ;

syntax DelegateType
     = TypeName
     ;

syntax TypeArgumentList
     = "\<"   {TypeArgument ","}+   "\>"
     ;

syntax TypeArgument
     = Type
     ;

syntax TypeParameter
     = Identifier
     ;

// Variables

syntax VariableReference
     = Expression
     ;


// Expressions

syntax ArgumentList
     = { Argument "," }+
     ;

syntax Argument
     = ArgumentName?   ArgumentValue
     ;

syntax ArgumentName
     = Identifier  ":" 
     ;

syntax ArgumentValue
     = Expression
     | "ref" !>> [0-9]   VariableReference  // To avoid ambiguities in case of ref1
     | "out" !>> [0-9]  VariableReference
     ;

syntax PrimaryExpression 
     = PrimaryNoArrayCreationExpression
     | ArrayCreationExpression
     ;

syntax PrimaryNoArrayCreationExpression
     = Literal_
     | SimpleName
     | ParenthesizedExpression
     | MemberAccess
     | InvocationExpression
     | ElementAccess
     | ThisAccess
     | BaseAccess
     | PostIncrementExpression
     | PostDecrementExpression
     | ObjectCreationExpression
     | AnonymousObjectCreationExpression
     | TypeofExpression
     | CheckedExpression
     | UncheckedExpression 
     | DefaultValueExpression
     | AnonymousMethodExpression
     | PointerMemberAccess
     | SizeofExpression
     ;

syntax SimpleName
     = Identifier   TypeArgumentList?
     ;

syntax ParenthesizedExpression
     = "("   Expression   ")"
     ;

syntax MemberAccess
     = PrimaryExpression   "."   Identifier  TypeArgumentList?
     | PredefinedType   "."   Identifier  TypeArgumentList?
     | QualifiedAliasMember   "."   Identifier
     ;

syntax InvocationExpression
     = PrimaryExpression   "("   ArgumentList?   ")"
     ;

syntax ElementAccess
     = PrimaryNoArrayCreationExpression   "["   ArgumentList   "]"
     | ArrayCreationExpression   "["   ArgumentList   "]"  // To allow parsing string s = new string[] {"a", "b"} [1]
     ;

syntax ThisAccess
     = "this"
     ;

syntax BaseAccess
     = "base"   "."   Identifier TypeArgumentList?
     | "base"   "["   ArgumentList   "]"
     ;

syntax PostIncrementExpression
     = PrimaryExpression   "++"
     ;

syntax PostDecrementExpression
     = PrimaryExpression   "--"
     ;

syntax ObjectCreationExpression
     = "new"   NonArrayType   "("   ArgumentList?   ")"   ObjectOrCollectionInitializer? 
     | "new"   NonArrayType   ObjectOrCollectionInitializer
     ;

syntax ObjectOrCollectionInitializer
     = ObjectInitializer
     | CollectionInitializer
     ;

syntax ObjectInitializer
     = "{"  MemberInitializerList?   "}"
     | "{"   MemberInitializerList   ","   "}"
     ;

syntax MemberInitializerList
     = { MemberInitializer ","}+
     ;

syntax MemberInitializer
     = Identifier   "="   InitializerValue
     ;

syntax InitializerValue
     = Expression
     | ObjectOrCollectionInitializer
     ;

syntax CollectionInitializer
     = "{"   ElementInitializerList   "}"
     | "{"   ElementInitializerList   ","   "}"
     ;

syntax ElementInitializerList
     = { ElementInitializer "," }+
     ;

syntax ElementInitializer
     = NonAssignmentExpression
     | "{"   ExpressionList   "}"
     ;

syntax ExpressionList
     = { Expression ","}+
     ;

syntax ArrayCreationExpression
     = "new"   NonArrayType   "["   ExpressionList   "]"   RankSpecifier*   ArrayInitializer?
     | "new"   ArrayType   ArrayInitializer 
     | "new"   RankSpecifier   ArrayInitializer
     ;
     
syntax AnonymousObjectCreationExpression
     = "new"   AnonymousObjectInitializer
     ;

syntax AnonymousObjectInitializer
     = "{"   MemberDeclaratorList?   "}"
     | "{"   MemberDeclaratorList   ","   "}"
     ;

syntax MemberDeclaratorList
     = { MemberDeclarator "," }+
     ;

syntax MemberDeclarator
     = SimpleName
     | MemberAccess
     | Identifier   "="   Expression
     ;

syntax TypeofExpression
     = "typeof"   "("   Type   ")"
     | "typeof"   "("   UnboundTypeName   ")"
     | "typeof" "(" "void" ")"
     ; 

syntax UnboundTypeName
     = Identifier   GenericDimensionSpecifier // GenericDimensionSpecifier? changed to GenericDimensionSpecifier to avoid ambiguity with TypeName 
     | Identifier   "::"   Identifier   GenericDimensionSpecifier?
     | UnboundTypeName   "."   Identifier   GenericDimensionSpecifier?
     ;

syntax GenericDimensionSpecifier
     = "\<"   ","*   "\>"
     ;

syntax CheckedExpression
     = "checked"   "("   Expression   ")"
     ;

syntax UncheckedExpression
     = "unchecked"  "("   Expression   ")"
     ;

syntax DefaultValueExpression
     = "default"   "("   Type   ")"
     ;

syntax UnaryExpression
     = PrimaryExpression
     | "+" !>> "+"   UnaryExpression
     | "-" !>> "-"  UnaryExpression
     | "!"   UnaryExpression
     | "~"   UnaryExpression
     | PreIncrementExpression
     | PreDecrementExpression
     | CastExpression
     | PointerIndirectionExpression
     | AddressofExpression
     | AwaitExpression
     ;
     
syntax PointerIndirectionExpression
     = "*"   UnaryExpression
     ;

syntax PointerMemberAccess
     = PrimaryExpression   "-\>"   Identifier  TypeArgumentList?
     ;
     
syntax PointerElementAccess
	 = PrimaryNoArrayCreationExpression   "["   Expression   "]"
	 ;

syntax AddressofExpression
     = "&" !>> "&"   UnaryExpression
     ;

syntax SizeofExpression
     = "sizeof"   "("   Type   ")"
     ;

syntax FixedStatement
     = "fixed"   "("   PointerType   FixedPointerDeclarators   ")"   EmbeddedStatement
     ;

syntax FixedPointerDeclarators
     = { FixedPointerDeclarator ","}+
     ;

syntax FixedPointerDeclarator
     = Identifier   "="   FixedPointerInitializer
     ;

syntax FixedPointerInitializer
     = Expression
     ;
     

syntax PreIncrementExpression
     = "++"   UnaryExpression
     ;

syntax PreDecrementExpression
     = "--"   UnaryExpression
     ;

syntax CastExpression
     = "(" (TypeName | ArrayType | PointerType) ")" >>> [~ ! ( A-Z _ a-z 0-9 \" \' @]  UnaryExpression
     | "(" (PredefinedType | NullableType) ")" UnaryExpression
     ;
     
syntax MultiplicativeExpression
     = UnaryExpression
     | MultiplicativeExpression   "*"   UnaryExpression
     | MultiplicativeExpression   "/"   UnaryExpression
     | MultiplicativeExpression   "%"   UnaryExpression
     ;

syntax AdditiveExpression
     = MultiplicativeExpression
     | AdditiveExpression   "+" !>> "+"   MultiplicativeExpression
     | AdditiveExpression   "-" !>> "-"   MultiplicativeExpression
     ;
     
syntax ShiftExpression
     = AdditiveExpression 
     | ShiftExpression   "\<\<"   AdditiveExpression
     | ShiftExpression   RightShift   AdditiveExpression
     ;

syntax RelationalExpression
     = ShiftExpression
     | RelationalExpression1   "\<"   ShiftExpression
     | RelationalExpression1   "\>"   ShiftExpression
     | RelationalExpression1   "\<="  ShiftExpression
     | RelationalExpression1   "\>="  ShiftExpression
     | RelationalExpression    "is"   Type
     | RelationalExpression    "as"   Type
     ;
     
// Disallowing nesting relational expressions such as A<B> C as it is a type error anyway
// and leads to an ambiguity with generic types     
syntax RelationalExpression1
     = ShiftExpression
     | RelationalExpression   "is"   Type
     | RelationalExpression   "as"   Type
     ;

syntax EqualityExpression
     = RelationalExpression
     | EqualityExpression   "=="   RelationalExpression
     | EqualityExpression   "!="   RelationalExpression
     ;

syntax AndExpression
     = EqualityExpression
     | AndExpression   "&" !>> "&"   EqualityExpression
     ;

syntax ExclusiveOrExpression
     = AndExpression
     | ExclusiveOrExpression   "^"   AndExpression
     ;

syntax InclusiveOrExpression
     = ExclusiveOrExpression
     | InclusiveOrExpression   "|"   ExclusiveOrExpression
     ;

syntax ConditionalAndExpression
     = InclusiveOrExpression
     | ConditionalAndExpression   "&&"   InclusiveOrExpression
     ;

syntax ConditionalOrExpression
     = ConditionalAndExpression
     | ConditionalOrExpression   "||"   ConditionalAndExpression
     ;

syntax NullCoalescingExpression
     = ConditionalOrExpression
     | ConditionalOrExpression   "??"   NullCoalescingExpression
     ;

syntax ConditionalExpression
     = NullCoalescingExpression
     | NullCoalescingExpression   "?"   Expression   ":"   Expression
     ;

syntax LambdaExpression
     = "async"? AnonymousFunctionSignature   "=\>"   AnonymousFunctionBody
     ;

syntax AnonymousMethodExpression
     = "async"? "delegate"   ExplicitAnonymousFunctionSignature?   Block
     ;

syntax AnonymousFunctionSignature
     = ExplicitAnonymousFunctionSignature 
     | ImplicitAnonymousFunctionSignature
     ;
     
     
syntax ExplicitAnonymousFunctionSignature
     = "("   { ExplicitAnonymousFunctionParameter "," }*   ")"
     ;

syntax ExplicitAnonymousFunctionParameter
     = AnonymousFunctionParameterModifier?   Type   Identifier
     ;

syntax AnonymousFunctionParameterModifier 
     = "ref"
     | "out"
     ;

syntax ImplicitAnonymousFunctionSignature
     = "("   { ImplicitAnonymousFunctionParameter ","}+   ")"
     | ImplicitAnonymousFunctionParameter
     ;

syntax ImplicitAnonymousFunctionParameter
     = Identifier
     ;

syntax AnonymousFunctionBody
     = Expression
     | Block
     ;

syntax QueryExpression
     = FromClause   QueryBody
     ;

syntax FromClause
     = "from"   Type?   Identifier   "in"   Expression
     ;

syntax QueryBody
     = QueryBodyClause*   SelectOrGroupClause   QueryContinuation?
     ;

syntax QueryBodyClause
     = FromClause
     | LetClause
     | WhereClause
     | JoinClause
     | JoinIntoClause
     | OrderbyClause
     ;

syntax LetClause
     = "let"   Identifier   "="   Expression
     ; 

syntax WhereClause
     = "where"   BooleanExpression
     ;

syntax JoinClause
     = "join"   Type?   Identifier   "in"   Expression   "on"   Expression   "equals"   Expression 
     ;
     

syntax JoinIntoClause
     = "join"   Type?   Identifier   "in"   Expression   "on"   Expression   "equals"   Expression   "into"   Identifier
     ;

syntax OrderbyClause
     = "orderby"   Orderings
     ;

syntax Orderings
     = { Ordering ","}+
     ;

syntax Ordering
     = Expression    OrderingDirection?
     ;

syntax OrderingDirection
     = "ascending"
     | "descending"
     ;

syntax SelectOrGroupClause
     = SelectClause
     | GroupClause
     ;

syntax SelectClause
     = "select"   Expression
     ;

syntax GroupClause
     = "group"   Expression   "by"   Expression
     ;

syntax QueryContinuation
     = "into"   Identifier   QueryBody
     ;

syntax Assignment
     = UnaryExpression   AssignmentOperator   Expression
     ;

syntax AssignmentOperator
     = "="
     | "+="
     | "-="
     | "*="
     | "/="
     | "%="
     | "&="
     | "|="
     | "^="
     | "\<\<="
     | RightShiftAssignment
     ;

syntax Expression 
     = NonAssignmentExpression
     | Assignment
     ;

syntax NonAssignmentExpression
     = ConditionalExpression
     | LambdaExpression
     | QueryExpression
     ;

syntax ConstantExpression
     = Expression
     ;


syntax BooleanExpression
     = Expression
     ;
     
// Statements

syntax Statement
     = LabeledStatement
     | DeclarationStatement
     | EmbeddedStatement
     ;

syntax EmbeddedStatement
     = Block
     | EmptyStatement
     | ExpressionStatement
     | SelectionStatement
     | IterationStatement
     | JumpStatement
     | TryStatement
     | CheckedStatement
     | UncheckedStatement
     | LockStatement
     | UsingStatement 
     | YieldStatement
     | UnsafeStatement 
     | FixedStatement
     ;

syntax UnsafeStatement
     = "unsafe"   Block
     ;

syntax Block
     = "{"   Statement*   "}"
     ;

syntax EmptyStatement
     = ";"
     ;

syntax LabeledStatement
     = Identifier  ":"    Statement
     ;

syntax DeclarationStatement
     = LocalVariableDeclaration   ";"
     | LocalConstantDeclaration   ";"
     ;

syntax LocalVariableDeclaration
     = LocalVariableType   LocalVariableDeclarators
     ;

syntax LocalVariableType
     = Type \ "var" \ "await"
     | "var"
     ;
     
syntax LocalVariableDeclarators
     = { LocalVariableDeclarator "," }+
     ;     

syntax LocalVariableDeclarator
     = Identifier
	 | Identifier   "="   LocalVariableInitializer
	     ;
	 
syntax LocalVariableInitializer
     = Expression
     | ArrayInitializer
     | StackallocInitializer
     ;

syntax StackallocInitializer
     = "stackalloc"   UnmanagedType "["  Expression "]"
     ;

syntax LocalConstantDeclaration
     = "const"   Type   ConstantDeclarators
     ;

syntax ConstantDeclarator
     = Identifier   "="   ConstantExpression
     ;

syntax ExpressionStatement
     = StatementExpression   ";"
     ;

syntax StatementExpression
     = InvocationExpression
     | ObjectCreationExpression
     | Assignment
     | PostIncrementExpression
     | PostDecrementExpression
     | PreIncrementExpression
     | PreDecrementExpression
     | AwaitExpression
     ;
     
syntax AwaitExpression
     = "await"  UnaryExpression
     ;

syntax SelectionStatement
     = IfStatement
     | SwitchStatement
     ;

syntax IfStatement
     = "if"   "("   BooleanExpression   ")"   EmbeddedStatement !>>> "else"
     | "if"   "("   BooleanExpression   ")"   EmbeddedStatement   "else"   EmbeddedStatement
     ;

syntax SwitchStatement
     = "switch"   "("   Expression   ")"   SwitchBlock
     ;

syntax SwitchBlock
     = "{"   SwitchSection*   "}"
     ;


syntax SwitchSection
     = SwitchLabel+   Statement+
     ;

syntax SwitchLabel
     = "case"   ConstantExpression ":"  
     | "default"  ":"
     ;
     
syntax IterationStatement
     = WhileStatement
     | DoStatement
     | ForStatement
     | ForeachStatement
     ;

syntax WhileStatement
     = "while"   "("   BooleanExpression   ")"   EmbeddedStatement
     ;

syntax DoStatement
     = "do"   EmbeddedStatement   "while"   "("   BooleanExpression   ")"   ";"
     ;

syntax ForStatement
     = "for"   "("   ForInitializer?   ";"   ForCondition?   ";"   ForIterator?   ")"   EmbeddedStatement
     ;

syntax ForInitializer
     = LocalVariableDeclaration
     | StatementExpressionList
     ;

syntax ForCondition
     = BooleanExpression
     ;

syntax ForIterator
     = StatementExpressionList
     ;

syntax StatementExpressionList
     = { StatementExpression "," }+
     ;

syntax ForeachStatement
     = "foreach"   "("   LocalVariableType   Identifier   "in"   Expression   ")"   EmbeddedStatement
     ;

syntax JumpStatement
     = BreakStatement
     | ContinueStatement
     | GotoStatement
     | ReturnStatement
     | ThrowStatement
     ;

syntax BreakStatement
     = "break"   ";"
     ;

syntax ContinueStatement
     = "continue"   ";"
     ;

syntax GotoStatement
     = "goto"   Identifier   ";"
     | "goto"   "case"   ConstantExpression   ";"
     | "goto"   "default"   ";"
     ;

syntax ReturnStatement
     = "return"   Expression?   ";"
     ;

syntax ThrowStatement
     = "throw"   Expression?   ";"
     ;
     
syntax TryStatement
     = "try"   Block   CatchClauses
     | "try"   Block   FinallyClause
     | "try"   Block   CatchClauses   FinallyClause
     ;

syntax CatchClauses
     = SpecificCatchClause+   GeneralCatchClause?
     | GeneralCatchClause
     ;

syntax SpecificCatchClause
     = "catch"   "("   TypeName   Identifier?   ")"  ("if"   "("   BooleanExpression   ")")?   Block  // if condition added to comply with C# 6
     ;

syntax GeneralCatchClause
     = "catch"   Block
     ;

syntax FinallyClause
     = "finally"   Block
     ;

syntax CheckedStatement
     = "checked"   Block
     ;

syntax UncheckedStatement
     = "unchecked"   Block
     ;

syntax LockStatement
     = "lock"   "("   Expression   ")"   EmbeddedStatement
     ;

syntax UsingStatement
     = "using"   "("    ResourceAcquisition   ")"    EmbeddedStatement
     ;

syntax ResourceAcquisition
     = LocalVariableDeclaration
     | Expression
     ;

syntax YieldStatement
     = "yield"   "return"   Expression   ";"
     | "yield"   "break"   ";"
     ;
     
     
// Namespaces

start syntax CompilationUnit
     = ExternAliasDirective*   UsingDirective*  GlobalAttributes? NamespaceMemberDeclaration*
	; 

syntax NamespaceDeclaration
     = "namespace"   QualifiedIdentifier   NamespaceBody   ";"?
     ;

syntax QualifiedIdentifier
     = { Identifier "." }+
     ;

syntax NamespaceBody
     = "{"   ExternAliasDirective*   UsingDirective*   NamespaceMemberDeclaration*   "}"
     ;

syntax ExternAliasDirective
     = "extern"   "alias"   Identifier   ";"
     ;

syntax UsingDirective
     = UsingAliasDirective
     | UsingNamespaceDirective
     ;

syntax UsingAliasDirective
     = "using"   Identifier   "="   NamespaceOrTypeName   ";"
     ;

syntax UsingNamespaceDirective
     = "using"   NamespaceName   ";"
     ;


syntax NamespaceMemberDeclaration
     = NamespaceDeclaration
     | TypeDeclaration
     ;

syntax TypeDeclaration
     = ClassDeclaration
     | StructDeclaration
     | InterfaceDeclaration
     | EnumDeclaration
     | DelegateDeclaration
     ;

syntax QualifiedAliasMember
     = Identifier  "::"    Identifier   TypeArgumentList?
     ;
     
     
// Classes
syntax ClassDeclaration
     = Attributes?   ClassModifier*   "partial"?   "class"   Identifier   TypeParameterList? ClassBase?   TypeParameterConstraintsClause*   ClassBody   ";"?
     ;

syntax ClassModifier
     = "new"
     | "public"
     | "protected"
     | "internal"
     | "private"
     | "abstract"
     | "sealed"
     | "static"
     | "unsafe"
     ;

syntax TypeParameterList
     = "\<"   TypeParameters   "\>"
     ;

syntax TypeParameters
     = Attributes?   TypeParameter
     | TypeParameters   ","   Attributes?   TypeParameter
     ;

syntax ClassBase
     = ":" TypeNameList
     ;
     
syntax TypeNameList
     = { TypeName "," }+
     ; 

syntax TypeParameterConstraintsClause
     = "where"   TypeParameter   ":"   TypeParameterConstraints
     ;

syntax TypeParameterConstraints
     = PrimaryConstraint
     | SecondaryConstraints
     | ConstructorConstraint
     | PrimaryConstraint   ","   SecondaryConstraints
     | PrimaryConstraint   ","   ConstructorConstraint
     | SecondaryConstraints   ","   ConstructorConstraint
     | PrimaryConstraint   ","   SecondaryConstraints   ","   ConstructorConstraint
     ;

syntax PrimaryConstraint
     = "class"
     | "struct"
     ;

syntax SecondaryConstraints
     = { TypeName "," }+
     ;

syntax ConstructorConstraint
     = "new"   "("   ")"
     ;

syntax ClassBody
     = "{"   ClassMemberDeclaration*   "}"
     ;

 syntax ClassMemberDeclaration
     = ConstantDeclaration
     | FieldDeclaration
     | MethodDeclaration
     | PropertyDeclaration
     | EventDeclaration
     | IndexerDeclaration
     | OperatorDeclaration
     | ConstructorDeclaration
     | DestructorDeclaration
     | StaticConstructorDeclaration
     | TypeDeclaration
     ;

syntax ConstantDeclaration
     = Attributes?   ConstantModifier*   "const"   Type   ConstantDeclarators   ";"
     ;

syntax ConstantModifier
     = "new"
     | "public"
     | "protected"
     | "internal"
     | "private"
     ;

syntax ConstantDeclarators
     = { ConstantDeclarator "," }+
     ;

syntax ConstantDeclarator
     = Identifier   "="   ConstantExpression
     ;

syntax FieldDeclaration
     = Attributes?   FieldModifier*   Type   VariableDeclarators   ";"
     ;

syntax FieldModifier
     = "new"
     | "public"
     | "protected"
     | "internal"
     | "private"
     | "static"
     | "readonly"
     | "volatile"
     | "unsafe"
     ;

syntax VariableDeclarators
     = { VariableDeclarator ","}+
     ;

 syntax VariableDeclarator
     = Identifier
     | Identifier   "="   VariableInitializer
     ;

syntax VariableInitializer
     = Expression
     | ArrayInitializer
     ;

syntax MethodDeclaration
     = MethodHeader   MethodBody
     ;

syntax MethodHeader
     = Attributes?   MethodModifier*   "partial"?   ReturnType   MemberName   TypeParameterList?
		"("   FormalParameterList?   ")"   TypeParameterConstraintsClause*
	  ;

syntax MethodModifier
     = "new"
     | "public"
     | "protected"
     | "internal"
     | "private"
     | "static"
     | "virtual"
     | "sealed"
     | "override"
     | "abstract"
     | "extern"
     | "unsafe"
     | "async"
     ;

syntax ReturnType
     = Type
     | "void"
     ;

syntax MemberName
     = Identifier
     | TypeName   "."   Identifier
     ;

syntax MethodBody
     = Block
     | ";"
     ;

syntax FormalParameterList
     = FixedParameters
     | FixedParameters   ","   ParameterArray
     | ParameterArray
     ;

syntax FixedParameters
     = { FixedParameter ","}+
     ;

syntax FixedParameter
     = Attributes?   ParameterModifier?   Type   Identifier   DefaultArgument?
     | "__arglist" // Undocumented keyword, appears in some source files
     ;

syntax DefaultArgument
     = "=" Expression
     ;

syntax ParameterModifier
     = "ref"
     | "out"
     | "this"
     ;

syntax ParameterArray
     = Attributes?   "params"   ArrayType   Identifier
     ;

syntax PropertyDeclaration
     = Attributes?   PropertyModifier*   Type   MemberName   "{"   AccessorDeclarations   "}"
     ;

syntax PropertyModifier
     = "new"
     | "public"
     | "protected"
     | "internal"
     | "private"
     | "static"
     | "virtual"
     | "sealed"
     | "override"
     | "abstract"
     | "extern"
     | "unsafe"
     ;

syntax MemberName
     = Identifier
     | TypeName   "."   Identifier
     ;

syntax AccessorDeclarations
     = GetAccessorDeclaration   SetAccessorDeclaration?
     | SetAccessorDeclaration   GetAccessorDeclaration?
     ;

syntax GetAccessorDeclaration
     = Attributes?   AccessorModifier?    "get"   AccessorBody
     ;

syntax SetAccessorDeclaration
     = Attributes?   AccessorModifier?   "set"   AccessorBody
     ;

syntax AccessorModifier
     = "protected"
     | "internal"
     | "private"
     | "protected"   "internal"
     | "internal"   "protected"
     ;

syntax AccessorBody
     = Block
     | ";"
     ;

syntax EventDeclaration
     = Attributes?   EventModifier*   "event"   Type   VariableDeclarators   ";"
     | Attributes?   EventModifier*   "event"   Type   MemberName   "{"   EventAccessorDeclarations   "}"
     ;
     

syntax EventModifier
     = "new"
     | "public"
     | "protected"
     | "internal"
     | "private"
     | "static"
     | "virtual"
     | "sealed"
     | "override"
     | "abstract"
     | "extern"
     | "unsafe"
     ;

syntax EventAccessorDeclarations
     = AddAccessorDeclaration   RemoveAccessorDeclaration
     | RemoveAccessorDeclaration   AddAccessorDeclaration
     ;

syntax AddAccessorDeclaration
     = Attributes?   "add"   Block
     ;

syntax RemoveAccessorDeclaration
     = Attributes?   "remove"   Block
     ;

syntax IndexerDeclaration
     = Attributes?   IndexerModifier*   IndexerDeclarator   "{"   AccessorDeclarations   "}"
     ;

syntax IndexerModifier
     = "new"
     | "public"
     | "protected"
     | "internal"
     | "private "
     | "virtual"
     | "sealed"
     | "override"
     | "abstract"
     | "extern"
     | "unsafe"
     ;

syntax IndexerDeclarator
     = Type   "this"   "["   FormalParameterList   "]"
     | Type   TypeName   "."   "this"   "["   FormalParameterList   "]"
     ;

syntax OperatorDeclaration
     = Attributes?   OperatorModifier+   OperatorDeclarator   OperatorBody
     ;


syntax OperatorModifier
     = "public"
     | "static"
     | "extern"
     | "unsafe"
     ;

syntax OperatorDeclarator
     = UnaryOperatorDeclarator
     | BinaryOperatorDeclarator
     | ConversionOperatorDeclarator
     ;

syntax UnaryOperatorDeclarator
     = Type   "operator"   OverloadableUnaryOperator   "("   Type   Identifier   ")"
     ;

syntax OverloadableUnaryOperator
     = "+"
     | "-"
     | "!"
     | "~"   
     | "++" 
     | "--"   
     | "true"
     | "false"
     ;

syntax BinaryOperatorDeclarator
     = Type   "operator"   OverloadableBinaryOperator   "("   Type   Identifier   ","   Type   Identifier   ")"
     ;

syntax OverloadableBinaryOperator
     = "+"
     | "-"
     | "*"
     | "/"
     | "%"
     | "&"
     | "|"
     | "^"
     | "\<\<"
     | RightShift
     | "=="
     | "!="
     | "\>"
     | "\<"
     | "\>="
     | "\<="
     ;

syntax ConversionOperatorDeclarator
     = "implicit"   "operator"   Type   "("   Type   Identifier   ")"
     | "explicit"   "operator"   Type   "("   Type   Identifier   ")"
     ;

syntax OperatorBody
     = Block
     | ";"
     ;

syntax ConstructorDeclaration
     = Attributes?   ConstructorModifier*   ConstructorDeclarator   ConstructorBody
     ;


syntax ConstructorModifier
     = "public"
     | "protected"
     | "internal"
     | "private"
     | "extern"
     | "unsafe"
     ;

syntax ConstructorDeclarator
     = Identifier   "("   FormalParameterList?   ")"   ConstructorInitializer?
     ;

syntax ConstructorInitializer
        = ":" "base"   "("   ArgumentList?   ")"
        | ":" "this"   "("   ArgumentList?   ")"
        ;

syntax ConstructorBody
     = Block
     | ";"
     ;

syntax StaticConstructorDeclaration
     = Attributes?   StaticConstructorModifiers  Identifier   "("   ")"   StaticConstructorBody
     ;

syntax StaticConstructorModifiers
     = "static"?    "unsafe"?   "extern"?
     ;

syntax StaticConstructorBody
     = Block
     | ";"
     ;

syntax DestructorDeclaration
     = Attributes?   "extern"?   "~"   Identifier   "("   ")"    DestructorBody
     | Attributes?   "extern"?   "unsafe"?   "~"   "identifier"   "("   ")"    DestructorBody
     | Attributes?   "unsafe"?   "extern"?   "~"   "identifier"   "("   ")"    DestructorBody
     ;

syntax DestructorBody
     = Block
     | ";"
     ;
     

// Structs     

syntax StructDeclaration
     = Attributes?   StructModifier*   "partial"?   "struct"   Identifier   TypeParameterList?
        StructInterfaces?   TypeParameterConstraintsClause*   StructBody   ";"?
     ;

syntax StructModifier
     = "new"
     | "public"
     | "protected"
     | "internal"
     | "private"
     | "unsafe"
     ; 

syntax StructInterfaces
     = ":" TypeNameList
     ;

syntax StructBody
     = "{"   StructMemberDeclarations?   "}"
     ;

syntax StructMemberDeclarations
     = StructMemberDeclaration
     | StructMemberDeclarations   StructMemberDeclaration
     | FixedSizeBufferDeclaration
     ;

syntax StructMemberDeclaration
     = ConstantDeclaration
     | FieldDeclaration
     | MethodDeclaration
     | PropertyDeclaration
     | EventDeclaration
     | IndexerDeclaration
     | OperatorDeclaration
     | ConstructorDeclaration
     | StaticConstructorDeclaration
     | TypeDeclaration
     ;
     
syntax FixedSizeBufferDeclaration
     = Attributes?   FixedSizeBufferModifier*   "fixed"   BufferElementType
        FixedSizeBufferDeclarator+   ";"
     ;

syntax FixedSizeBufferModifier
     = "new"
     | "public"
     | "protected"
     | "internal"
     | "private"
     | "unsafe"
     ;

syntax BufferElementType
     = Type
     ;

syntax FixedSizeBufferDeclarator
     = Identifier   "["   ConstantExpression   "]"
     ;     

// Arrays
syntax ArrayType
     = NonArrayType   RankSpecifier+
     ;

syntax NonArrayType
     = TypeName
     | PredefinedType
     | NullableType
     | PointerType
     ;


syntax RankSpecifier
     = "["   ","*   "]"
     ;


syntax ArrayInitializer
     = "{"   VariableInitializerList?   "}"
     | "{"   VariableInitializerList   ","   "}"
     ;

syntax VariableInitializerList
     = { VariableInitializer ","}+
     ;

// Interfaces

syntax InterfaceDeclaration
     = Attributes?   InterfaceModifier*   "partial"?   "interface"   
        Identifier   VariantTypeParameterList?   InterfaceBase?
        TypeParameterConstraintsClause*   InterfaceBody   ";"?
     ;

syntax InterfaceModifier
     = "new"
     | "public"
     | "protected"
     | "internal"
     | "private"
     | "unsafe"
     ;

syntax VariantTypeParameterList
     = "\<"   VariantTypeParameters   "\>"
     ;

syntax VariantTypeParameters
     = Attributes?  VarianceAnnotation?  TypeParameter
     | VariantTypeParameters   ","   Attributes?   VarianceAnnotation?  TypeParameter
     ;

syntax VarianceAnnotation
     = "in"
     | "out"
     ;

syntax InterfaceBase
     = ":"   TypeNameList
     ;

syntax InterfaceBody
     = "{"   InterfaceMemberDeclaration*   "}"
     ;

syntax InterfaceMemberDeclaration
     = InterfaceMethodDeclaration
     | InterfacePropertyDeclaration
     | InterfaceEventDeclaration
     | InterfaceIndexerDeclaration
     ;

syntax InterfaceMethodDeclaration
     = Attributes?   "new"? "unsafe"?   ReturnType   Identifier   TypeParameterList?
        "("   FormalParameterList?   ")"   TypeParameterConstraintsClause*   ";"
     ;

syntax InterfacePropertyDeclaration
     = Attributes?   "new"?   Type   Identifier   "{"   InterfaceAccessors   "}"
     ;

syntax InterfaceAccessors
     = Attributes?   "get"   ";"
     | Attributes?   "set"   ";"
     | Attributes?   "get"   ";"   Attributes?   "set"   ";"
     | Attributes?   "set"   ";"   Attributes?   "get"   ";"
     ;

syntax InterfaceEventDeclaration
     = Attributes?   "new"?   "event"   Type   Identifier   ";"
     ;

syntax InterfaceIndexerDeclaration
     = Attributes?   "new"?   Type   "this"   "["   FormalParameterList   "]"   "{"   InterfaceAccessors   "}"
     ; 

// Enums

syntax EnumDeclaration
     = Attributes?   EnumModifier*   "enum"   Identifier   EnumBase?   EnumBody   ";"?
     ;

syntax EnumBase
     = ":"   IntegralType
     ;

syntax EnumBody
     = "{"   EnumMemberDeclarations?   "}"
     | "{"   EnumMemberDeclarations   ","   "}"
     ;

syntax EnumModifier
     = "new"
     | "public"
     | "protected"
     | "internal"
     | "private"
     ;

syntax EnumMemberDeclarations
     = { EnumMemberDeclaration "," }+
     ;

syntax EnumMemberDeclaration
     = Attributes?   Identifier
     | Attributes?   Identifier   "="   ConstantExpression
     ;


// Delegates

syntax DelegateDeclaration
     = Attributes?   DelegateModifier*   "delegate"   ReturnType   
        Identifier  VariantTypeParameterList?   
        "("   FormalParameterList?   ")"   TypeParameterConstraintsClause*   ";"
     ;

syntax DelegateModifier
     = "new"
     | "public"
     | "protected"
     | "internal"
     | "private"
     | "unsafe"
     ;
     
// Attributes

syntax GlobalAttributes
    = GlobalAttributeSection+
    ;

syntax GlobalAttributeSection
    = "["   GlobalAttributeTargetSpecifier   AttributeList   ","?   "]"
    ;


syntax GlobalAttributeTargetSpecifier
    = GlobalAttributeTarget  ":"
    ;

syntax GlobalAttributeTarget
    = "assembly"
    | "module"
    ;

syntax Attributes
    = AttributeSection+
    ;

syntax AttributeSection
    = "["   AttributeTargetSpecifier?   AttributeList   ","?   "]"
    ;

syntax AttributeTargetSpecifier
    = AttributeTarget   ":"
    ;

syntax AttributeTarget
    = "field"
    | "event"
    | "method"
    | "param"
    | "property"
    | "return"
    | "type"
    | "typevar"
    ;

syntax AttributeList
    = { Attribute ","}+
    ;

syntax Attribute
    = AttributeName   AttributeArguments?
    ;

syntax AttributeName
    = TypeName
    ;

syntax AttributeArguments
    = "("   PositionalArgumentList?   ")"
    | "("   PositionalArgumentList   ","   NamedArgumentList   ")"
    | "("   NamedArgumentList   ")"
    ;

syntax PositionalArgumentList
    = { PositionalArgument "," }+
    ;

syntax PositionalArgument
    = ArgumentName?   AttributeArgumentExpression
    ;

syntax NamedArgumentList
    = { NamedArgument ","}+
    ;

syntax NamedArgument
    = Identifier   "="   AttributeArgumentExpression
    ;

syntax AttributeArgumentExpression
    = NonAssignmentExpression    // Changed from Expression to deal ambiguity
    ;
