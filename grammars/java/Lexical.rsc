module java::specification::Lexical

//----------------------------------------------------------------------------------------------------------------
// Lexical Definititions
//----------------------------------------------------------------------------------------------------------------

lexical UnicodeInputCharacter 
     = UnicodeEscape
     | RawInputCharacter
     ;

lexical UnicodeEscape 
     = [\\] [u]+ HexDigit HexDigit HexDigit HexDigit
     ;
 
lexical RawInputCharacter 
     = ![\\]
     | [\\] !>> [\\ u]
     | [\\][\\]   // Java Language Specification §3.3  
     ;

lexical InputCharacter 
	  = UnicodeInputCharacter \ [\n \r]    // UnicodeInputCharacter but not CR or LF 
	  | [\a00]                             // to match zero        
      ;

//----------------------------------------------------------------------------------------------------------------

layout Layout 
     = (WhiteSpace | Comment)* !>> [\t \n \r \f  \ ] !>> "/*" !>> "//";

lexical WhiteSpace 
      = [\ \t \f \r \n \a1a]  // to match SUB in the end, but we need a better solution for this when we added better
                              // suuport for layout
      ;
      
lexical LineTerminator
      = [\r \n]
      ;
     
//----------------------------------------------------------------------------------------------------------------
    
lexical Comment 
    = TraditionalComment
    | EndOfLineComment
    ;

lexical TraditionalComment 
     = "/*" CommentTail
     ;

lexical EndOfLineComment 
    = "//" InputCharacter* !>> ![\n \r];

//  NotStar* "*" CommentTailStar
lexical CommentTail 
    = "*" CommentTailStar
    | NotStar CommentTail
    ;

lexical CommentTailStar 
    = "/"
    | "*" CommentTailStar
    | NotStarNotSlash CommentTail
    ;

lexical NotStar 
    = InputCharacter \ [*]
    | LineTerminator;

lexical NotStarNotSlash 
    = InputCharacter \ [* /]
    | LineTerminator;

    
//----------------------------------------------------------------------------------------------------------------      

lexical Identifier = [$ A-Z _ a-z] !<< IdentifierChars \Keyword \BooleanLiteral \NullLiteral !>> [$ 0-9 A-Z _ a-z];

lexical IdentifierChars 
    = JavaLetter
    | IdentifierChars JavaLetterOrDigit
    ;

lexical JavaLetter = [A-Za-z$_];

lexical JavaLetterOrDigit = [A-Za-z$_0-9];

//----------------------------------------------------------------------------------------------------------------      

keyword Keyword 
      = "abstract"
      | "continue"
      | "for"
      | "new"
      | "switch"
      | "assert"
      | "default"
      | "if"
      | "package"
      | "synchronized"
      | "boolean"
      | "do"
      | "goto"
      | "private"
      | "this"
      | "break"
      | "double"
      | "implements"
      | "protected"
      | "throw"
      | "byte"
      | "else"
      | "import"
      | "public"
      | "throws"
      | "case"
      | "enum"
      | "instanceof"
      | "return"
      | "transient"
      | "catch"
      | "extends"
      | "int"
      | "short"
      | "try"
      | "char"
      | "final"
      | "interface"
      | "static"
      | "void"
      | "class"
      | "finally"
      | "long"
      | "strictfp"
      | "volatile"
      | "const"
      | "float"
      | "native"
      | "super"
      | "while"
      ;
          
//----------------------------------------------------------------------------------------------------------------

syntax Literal 
    = IntegerLiteral
    | FloatingPointLiteral
    | BooleanLiteral
    | CharacterLiteral
    | StringLiteral
    | NullLiteral
    ; 
    
syntax IntegerLiteral 
    = DecimalIntegerLiteral !>> [.]
    | HexIntegerLiteral !>> [.]
    | OctalIntegerLiteral
    | BinaryIntegerLiteral
    ; 
    
syntax FloatingPointLiteral 
    = DecimalFloatingPointLiteral
    | HexadecimalFloatingPointLiteral
    ;     

lexical DecimalIntegerLiteral 
    = DecimalNumeral IntegerTypeSuffix?
    ;

lexical HexIntegerLiteral 
    = HexNumeral IntegerTypeSuffix?
    ;

lexical OctalIntegerLiteral 
    = OctalNumeral IntegerTypeSuffix?
    ;

lexical BinaryIntegerLiteral 
    = BinaryNumeral IntegerTypeSuffix?
    ;

lexical IntegerTypeSuffix 
    = [l] | [L];
    
//----------------------------------------------------------------------------------------------------------------
    
lexical DecimalNumeral 
    = [0]
    | NonZeroDigit Digits?
    | NonZeroDigit [_]+ Digits
    ; 

lexical Digits 
    = Digit
    | Digit DigitOrUnderscore* Digit
    ; 

lexical Digit 
     = [0]
     | NonZeroDigit
     ;

lexical NonZeroDigit = [1-9];

lexical DigitOrUnderscore 
    = Digit
    | [_]
    ;

//----------------------------------------------------------------------------------------------------------------

lexical HexNumeral 
    = [0] [x] HexDigits
    | [0] [X] HexDigits
    ;

lexical HexDigits 
    = HexDigit
    | HexDigit HexDigitOrUnderscore* HexDigit; 

lexical HexDigit = [0-9 a-f A-F];

lexical HexDigitOrUnderscore 
    = HexDigit
    | [_]
    ;
    
//----------------------------------------------------------------------------------------------------------------    
    
lexical OctalNumeral 
    = [0] OctalDigits
    | [0] [_]+ OctalDigits
    ;

lexical OctalDigits 
    = OctalDigit
    | OctalDigit OctalDigitOrUnderscore* OctalDigit 
    ;

lexical OctalDigit = [0-7];

lexical OctalDigitOrUnderscore 
    = OctalDigit
    | [_]
    ;
    
//----------------------------------------------------------------------------------------------------------------        
    
lexical BinaryNumeral 
    = [0] [b] BinaryDigits 
    | [0] [B] BinaryDigits
    ;

lexical BinaryDigits 
    = BinaryDigit 
    | BinaryDigit BinaryDigitOrUnderscore* BinaryDigit
    ;

lexical BinaryDigit = [0-1]; 

lexical BinaryDigitOrUnderscore
    = BinaryDigit
    | [_]
    ;
    
//----------------------------------------------------------------------------------------------------------------        


lexical DecimalFloatingPointLiteral 
    = Digits [.] Digits? ExponentPart? FloatTypeSuffix?
    | [.] Digits ExponentPart? FloatTypeSuffix?
    | Digits ExponentPart
    | Digits FloatTypeSuffix
    | Digits ExponentPart FloatTypeSuffix
    ;

lexical ExponentPart 
    = ExponentIndicator SignedInteger
    ;

lexical ExponentIndicator = [e E];

lexical SignedInteger = Sign? Digits;

lexical Sign = [+ \-];

lexical FloatTypeSuffix = [f F d D];     
    
//----------------------------------------------------------------------------------------------------------------

lexical HexadecimalFloatingPointLiteral 
     =  HexSignificand BinaryExponent FloatTypeSuffix?;

lexical HexSignificand 
    = HexNumeral
    | HexNumeral [.]
    | [0] [x] HexDigits? [.] HexDigits
    | [0] [X] HexDigits? [.] HexDigits
    ;

lexical BinaryExponent 
    = BinaryExponentIndicator SignedInteger;

lexical BinaryExponentIndicator = [p P];

//----------------------------------------------------------------------------------------------------------------

lexical BooleanLiteral 
     = "true" 
     | "false"
     ;

lexical CharacterLiteral 
    = [\'] SingleCharacter [\']
    | [\'] EscapeSequence [\']
    ;

lexical SingleCharacter 
      = InputCharacter \ [\' \\]
      ;

lexical StringLiteral 
     = [\"] StringCharacter* [\"]
     ;

lexical StringCharacter 
     = InputCharacter \ [\" \\]
     | EscapeSequence
     ;
        
lexical EscapeSequence 
     = Backslash [b]                 /* \u0008: backspace BS */
     | Backslash [t]                 /* \u0009: horizontal tab HT */
     | Backslash [n]                 /* \u000a: linefeed LF */
     | Backslash [f]                 /* \u000c: form feed FF */
     | Backslash [r]                 /* \u000d: carriage return CR */
     | Backslash [\"]                /* \u0022: double quote " */
     | Backslash [\']                /* \u0027: single quote ' */
     | [\\] [u]+ "005" [cC] [\\] [u]+ "005" [cC]   // Todo: merge it with [\\][\\]
     | OctalEscape                   /* \u0000 to \u00ff: from octal value */
     ;
     
lexical Backslash
     = [\\] [u]+ "005" [cC]
     | [\\];    

lexical OctalEscape 
     = [\\] OctalDigit !>> [0-7]
     | [\\] OctalDigit OctalDigit !>> [0-7]
     | [\\] ZeroToThree OctalDigit OctalDigit
     ;

lexical ZeroToThree = [0-3];
    
lexical NullLiteral = "null";

lexical AssignmentOperator 
    = "=" !>> "="
    | "+="
    | "-=" 
    | "*="
    | "/="
    | "&="
    | "|="
    | "^="
    | "%="
    | "\<\<="
    | "\>\>="
    | "\>\>\>="
    ;
