module csharp::preprocess::Lexical

lexical Input
     = InputSection*
     ;

lexical InputSection
     = InputSectionPart
     ;

lexical InputSectionPart
     = InputElement
     | PpDirective
     ;

lexical InputElement
     = Whitespace
     | Comment
     | Token
     ;
     
lexical Token
     = Identifier
     | Keyword
     | [A-Za-z] !<< IntegerLiteral
     | [A-Za-z] !<< RealLiteral
     | CharacterLiteral
     | StringLiteral
     | OperatorOrPunctuator
     ;     


layout Layout 
     = (Whitespace | Comment | DPpConditional | DPpGarbage)* !>> [\t \n \r \f  \ ] !>> "/*" !>> "//"
     ; 
       // hack: CPP outpus pragmas to the file and I haven't found a way to get rid of it yet. 

/* 
 * Carriage return character (U+000D)
 * Line feed character (U+000A)
 * Carriage return character (U+000D) followed by line feed character (U+000A)
 * Next line character (U+0085)
 * Line separator character (U+2028)
 * Paragraph separator character (U+2029)
 */
lexical NewLine
      = [\r \n] //[\r \n \u0085 \u2028 \u2029]
      ;
      
// Comments

lexical Comment
      = SingleLineComment
      | DelimitedComment
      ;
      
lexical SingleLineComment
      = "//" InputCharacter* !>> ![\n \r]
      ;      
      
lexical InputCharacter 
	      = ![] \ [\r \n] 		                    // ![] \ [\r \n \u0085 \u2028 \u2029]    // Any Unicode character Except NewLine
	      | [\a00]                            // to match zero        
      ;
      
lexical DelimitedComment
     = "/*"   DelimitedCommentSection*   [*]+   "/"
     ;

lexical DelimitedCommentSection
      = "/"
      | [*]*  NotSlashOrAsterisk
      ; 

lexical NotSlashOrAsterisk
      = ![] \ [/ *]
      ;
      
/* 
 * Any character with Unicode class Zs
 * Horizontal tab character (U+0009)
 * Vertical tab character (U+000B)
 * Form feed character (U+000C)
 */
 lexical Whitespace
       = [\ \t \f \r \n]+ !>> [\ \t \f \r \n] //[\u0020 \u00A0 \u1680 \u180E \u2000-\u200A \u202F \u205F \u3000 \u0009 \u000B \u000C]
       ;
      
lexical WhitespaceNoNL
      = [\ \t \f]+ !>> [\ \t \f] //[\u0020 \u00A0 \u1680 \u180E \u2000-\u200A \u202F \u205F \u3000 \u0009 \u000B \u000C]
      ;      
       
// B.1.5 Unicode character escape sequences       
 
lexical UnicodeEscapeSequence
      = "\\u"   HexDigit   HexDigit   HexDigit   HexDigit   !>> HexDigit
      | "\\U"   HexDigit   HexDigit   HexDigit   HexDigit   HexDigit   HexDigit   HexDigit   HexDigit
      ;
      
// Identifiers      
      
lexical Identifier
      = [A-Z _ a-z] !<< IdentifierOrKeyword !>> [0-9 A-Z _ a-z] \ Keyword
      | "@"  IdentifierOrKeyword !>> [0-9 A-Z _ a-z]
      ;
      
      
lexical IdentifierOrKeyword
      = IdentifierStartCharacter IdentifierPartCharacter*
      ;
      
lexical IdentifierStartCharacter
      = LetterCharacter
      | "_"
      ;

lexical IdentifierPartCharacter
      = LetterCharacter
      | DecimalDigitCharacter
      | "_"
      //| ConnectingCharacter
      //| CombiningCharacter
      //| FormattingCharacter
      ;
     
lexical LetterCharacter
     = [a-zA-Z] //Lu | Ll | Lt | Lm | Lo | Nl
     ; 
     
//lexical CombiningCharacter
//      = Mn | Mc
//      ;
      
lexical DecimalDigitCharacter
      = [0-9] //Nd
      ;
       
//lexical ConnectingCharacter  
//      = Pc
//      ;      
//      
//lexical FormattingCharacter  
//      = Cf
//      ;
      
      
// Keywords      
  
lexical Keyword  
      = "abstract"   
      | "as"
      | "base"
      | "bool"
      | "break"
      | "byte"
      | "case"
      | "catch"
      | "char"
      | "checked"
      | "class"
      | "const"
      | "continue"
      | "decimal"
      | "default"
      | "delegate"
      | "do"
      | "double"
      | "else"
      | "enum"
      | "event"
      | "explicit"
      | "extern"
      | "false"
      | "finally"
      | "fixed"
      | "float"
      | "for"
      | "foreach"
      | "goto"
      | "if"
      | "implicit"
      | "in"
      | "int"
      | "interface"
      | "internal"
      | "is"
      | "lock"
      | "long"
      | "namespace"
      | "new"
      | "null"
      | "object"
      | "operator"
      | "out"
      | "override"
      | "params"
      | "private"
      | "protected"
      | "public"
      | "readonly"
      | "ref"
      | "return"
      | "sbyte"
      | "sealed"
      | "short"
      | "sizeof"
      | "stackalloc"
      | "static"
      | "string"
      | "struct"
      | "switch"
      | "this"
      | "throw"
      | "true"
      | "try"
      | "typeof"
      | "uint"
      | "ulong"
      | "unchecked"
      | "unsafe"
      | "ushort"
      | "using"
      | "virtual"
      | "void"
      | "volatile"
      | "while"
      //| "async"
      //| "await"
      ;

// Literals      
      
lexical Literal
     = BooleanLiteral
     | IntegerLiteral
     | RealLiteral
     | CharacterLiteral
     | StringLiteral
     | NullLiteral
     ;
     
lexical BooleanLiteral
      = "true"
      | "false"
      ;
     
lexical IntegerLiteral
     = DecimalIntegerLiteral
     | HexadecimalIntegerLiteral
     ;
     
lexical DecimalIntegerLiteral
     = DecimalDigit+ !>> [0-9 U u L l F  f  D  d  M  m xX] !>> "UL" !>> "Ul" !>> "uL" !>> "ul" !>> "LU" !>> "Lu" !>> "lU" !>> "lu" 
     | DecimalDigit+ IntegerTypeSuffix
     ;
     
lexical DecimalDigit
      = [0-9]
      ;     
     
lexicalIntegerTypeSuffix
      = "U" | "u" | "L" | "l" | "UL" | "Ul" | "uL" | "ul" | "LU" | "Lu" | "lU" | "lu"
      ;
      
lexical HexadecimalIntegerLiteral 
     = [0][xX]   HexDigit+ !>>[0-9  A-F  a-f U u L l] !>> "UL" !>> "Ul" !>> "uL" !>> "ul" !>> "LU" !>> "Lu" !>> "lU" !>> "lu"
     | [0][xX]   HexDigit+ IntegerTypeSuffix
     ;      
      
lexical HexDigit
      = [0-9  A-F  a-f]
      ;
      
lexical RealLiteral
     = DecimalDigit+  "."   DecimalDigit+   ExponentPart?   RealTypeSuffix
     | DecimalDigit+  "."   DecimalDigit+  !>> [0-9]  ExponentPart?   !>> [F  f  D  d  M  m]
     | [0-9] !<< "."  DecimalDigit+ !>> [0-9]  ExponentPart?   !>> [F  f  D  d  M  m]
     | [0-9] !<< "."  DecimalDigit+  ExponentPart?   RealTypeSuffix  !>> [F  f  D  d  M  m]
     | DecimalDigit+   ExponentPart   RealTypeSuffix?
     | DecimalDigit+   RealTypeSuffix
     ;
            
lexical  ExponentPart
     = [eE]   Sign?   DecimalDigit+ !>> [0-9]
     ;
      
lexical Sign 
     = [+  \-]
     ;

lexical RealTypeSuffix 
      = [F  f  D  d  M  m]
      ;
      
lexical CharacterLiteral
     = [\']   Character   [\']
     ;
     
lexical Character
      = SingleCharacter
      | SimpleEscapeSequence
      | HexadecimalEscapeSequence
      | UnicodeEscapeSequence
      ; 

lexical SingleCharacter
      = ![] \ [\' \\ \r \n \u0085 \u2028 \u2029]
      ;
      
lexical SimpleEscapeSequence
      = [\\][\']
      | [\\][\"]
      | [\\][\\]
      | [\\][0]
      | [\\][a]
      | [\\][b]
      | [\\][f]
      | [\\][n]
      | [\\][r]
      | [\\][t]
      | [\\][v]
      ;
      
lexical HexadecimalEscapeSequence
     = "\\x"   HexDigit   !>> HexDigit
     | "\\x"   HexDigit   HexDigit !>> HexDigit
     | "\\x"   HexDigit   HexDigit   HexDigit !>> HexDigit
     | "\\x"   HexDigit   HexDigit   HexDigit    HexDigit
     ;
     
lexical StringLiteral
      = RegularStringLiteral
      | VerbatimStringLiteral
      ;
      
lexical RegularStringLiteral
      = [\"]   RegularStringLiteralCharacter*   [\"]
      ;
      
lexical RegularStringLiteralCharacter
      = SingleRegularStringLiteralCharacter
      | SimpleEscapeSequence
      | HexadecimalEscapeSequence
      | UnicodeEscapeSequence
      ;
     
lexical SingleRegularStringLiteralCharacter
      = ![] \ [\" \\  \r \n \u0085 \u2028 \u2029]
      ;

lexical VerbatimStringLiteral 
      = "@" [\"]   VerbatimStringLiteralCharacter*   [\"] !>> [\"]
      ;

lexical VerbatimStringLiteralCharacter
      = SingleVerbatimStringLiteralCharacter
      | QuoteEscapeSequence
      ;

lexical SingleVerbatimStringLiteralCharacter
     = ![] \ [\"]
     ;

lexical QuoteEscapeSequence
      = [\"][\"]
      ;

lexical NullLiteral
      = "null"
      ;
      
      
// Operators and punctuators   
   
lexical OperatorOrPunctuator
     = "{"
     | "}"
     | "["
     | "]"
     | "("
     | ")"
     | "." !>> [0-9]
     | ","
     | ":"  !>> [:]
     | ";"
     | "+"  !>> [+ =]
     | "-"  !>> [\- =]
     | "*"  !>> [= *]
     | "/"  !>> [/ = *]
     | "%"  !>> [=]
     | "&"  !>> [& =]
     | "|"  !>> [= |]
     | "^"  !>> [=]
     | "!"  !>> [=]
     | "~"
     | "="  !>> [= \>]
     | "\<" !>> [= \<]
     | "\>" !>> [=]
     | "?"  !>> [?]
     | "??"
     | "::"
     | "++"
     | "--"
     | "&&"
     | "||"
     | "-\>"
     | "=="
     | "!="
     | "\<="
     | "\>="
     | "+="
     | "-="
     | "*="
     | "/="
     | "%=:"
     | "&=" !>> [:]
     | "|="
     | "^="
     | "\<\<" !>> [=]
     | "\<\<="
     | "=\>"
     ;
      
lexical RightShift
      = "\>\>"
      ;
      
lexical RightShiftAssignment
     = "\>\>="
     ;
     
// Conditional directives with evalutation

lexical DPpConditional 
      = DPpIfSection
      ;

lexical DPpIfSection 
      = "#"   Whitespace?   "if"   Whitespace   DPpExpression exp if(true) Layout else (Input (DPpElifSection | DPpElseSection))
      ;

lexical DPpElifSection
      = "#"   Whitespace?   "elif"   Whitespace   DPpExpression exp if(true) Layout else (Input (DPpElifSection | DPpElseSection))
      ;

lexical DPpElseSection
      = "#"   Whitespace?   "else"   Layout
      ;
      
lexical DPpGarbage
      = DPpElif* DPpElse? "#"   Whitespace?   "endif"
      ;
      
lexical DPpElif 
      = "#"   Whitespace?   "elif" Input
      ;
      
lexical DPpElse 
      = "#"   Whitespace?   "else" Input
      ;

// Pre-processing directives

lexical PpDirective
      = PpDeclaration
      | PpConditional
      | PpLine
      | PpDiagnostic
      | PpRegion 
      | PpPragma
      ;
      
lexical ConditionalSymbol
      = IdentifierOrKeyword \ "true" \ "false"
      ;
      
lexical PpExpression
      = Whitespace?  PpOrExpression   Whitespace?
      ;

lexical PpOrExpression
      = PpAndExpression
      | PpOrExpression   Whitespace?   "||"   Whitespace?   PpAndExpression
      ;

lexical PpAndExpression
      = PpEqualityExpression
      | PpAndExpression   Whitespace?   "&&"   Whitespace?   PpEqualityExpression
      ;

lexical PpEqualityExpression
      = PpUnaryExpression
      | PpEqualityExpression   Whitespace?   "=="   Whitespace?   PpUnaryExpression
      | PpEqualityExpression   Whitespace?   "!="   Whitespace?   PpUnaryExpression
      ;

lexical PpUnaryExpression
      = PpPrimaryExpression
      | "!"   Whitespace?   PpUnaryExpression
      ;
      
lexical PpPrimaryExpression
     = "true"
     | "false"
     | ConditionalSymbol
     | "("   Whitespace?   PpExpression   Whitespace?  ")"
     ;
     
lexical PpDeclaration
      = "#"   Whitespace?   ("define" | "undef")   Whitespace   ConditionalSymbol   PpNewLine
      ;
      
lexical PpNewLine 
      = WhitespaceNoNL? NewLine
      ; //Whitespace?   SingleLineComment   NewLine

lexical PpConditional 
      = PpIfSection   PpElifSection*   PpElseSection?   PpEndif
      ;

lexical PpIfSection 
      = "#"   Whitespace?   "if"   Whitespace   PpExpression   PpNewLine   ConditionalSection?
      ;

lexical PpElifSection
      = "#"   Whitespace?   "elif"   Whitespace   PpExpression   PpNewLine   ConditionalSection?
      ;

lexical PpElseSection
      = "#"   Whitespace?   "else"   PpNewLine   ConditionalSection?
      ;

lexical PpEndif
      = "#"   Whitespace?   "endif"   PpNewLine
      ;

lexical ConditionalSection 
     = //InputSection
       SkippedSectionPart+
     ;

lexical SkippedSectionPart 
      = SkippedCharacters?   NewLine
      | PpDirective
      ;

lexical SkippedCharacters
     = NoHash+
     ;
     
lexical NoHash
      = ![#] \ [\r\n]
      ;     

lexical PpDiagnostic
     = "#"   Whitespace?   ("error" | "warning")   PpMessage
     ;

lexical PpMessage
      = InputCharacter*   NewLine
      ;

lexical PpRegion 
      = PpStartRegion   ConditionalSection? WhitespaceNoNL?  PpEndRegion
      ;

lexical PpStartRegion 
      = "#"   Whitespace?   "region"   PpMessage
      ;

lexical PpEndRegion 
      = "#"   Whitespace?   "endregion"   PpMessage
      ;
      
      
lexical PpLine
     =  "#"   Whitespace?   "line"   Whitespace   LineIndicator   PpNewLine
     ;
     
lexical LineIndicator 
     = DecimalDigit+   Whitespace   FileName 
     | DecimalDigit+
     | "default" 
     | "hidden"
     ;

lexical FileName 
     = "\""   FileNameCharacter+   "\""
     ;

lexical FileNameCharacter 
     = ![] \ [\"]
     ;

lexical PpPragma 
     = "#"   Whitespace?   "pragma"   Whitespace   PragmaBody
     ;

lexical PragmaBody 
     = PragmaWarningBody
     ;

lexical PragmaWarningBody 
     = "warning"   Whitespace   WarningAction   NewLine
     | "warning"   Whitespace   WarningAction   Whitespace   WarningList
     ;

lexical WarningAction 
      = "disable"
      | "restore"
      ;

lexical WarningList 
      = DecimalDigit+ !>> [0-9]
      | WarningList   Whitespace?   ","   Whitespace?   DecimalDigit+ !>> [0-9]
      ;
  
  