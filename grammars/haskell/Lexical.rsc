module haskell::datadependent::Lexical

lexical LiteralH 
      = Integer !>> "." !>> "#"    // !>> "." is added to disambiguate 2.2
      | Integer "#" !>> "#"
      | Integer "##"
      | Float !>> "#"
      | Float "#" !>> "#"
      | Float "##"
      | Char  !>> "#"
	  | Char "#"      
      | String !>> "#"
      | String "#"
      ;

lexical Special 
      = "(" 
      | ")" 
      | "," 
      | ";" 
      | "["
      | "]" 
      | "`" 
      | "{" 
      | "}"
      ;
 
layout Whitespace  
     = WhiteStuff* !>> [\n \r \t \ \u000B] !>> "{-" !>> "--"
     ;

lexical WhiteStuff  
      = WhiteChar 
      | Comment 
      | NComment
      ;

lexical WhiteChar   
      = [\n \r \t \ \u000B]
      ;

lexical NewLine
      = [\r \n]
      ;
      
lexical Space
      = " "
      ;          
      
lexical Tab
      = [\t]
      ;        
 
lexical Comment 
      = Dashes (Any \ Symbol Any*)? "\n"
      ;

lexical Dashes  
      =   "--" "-"*
      ;

lexical NComment    
      = "{-" (![{\-] | NComment | "-" !>> [}] | "{" !>> [\-])* "-}"
      ;

lexical Any 
      = Graphic 
      | Space 
      | Tab
      ;

lexical Graphic 
      = Small 
      | Large 
      | Symbol 
      | Digit 
      | Special 
      | "\"" 
      | "\'"
 	  ;
 
lexical Small   
      = AscSmall 
//     | UniSmall 
      | "_"
      ;

lexical AscSmall    
     =  [a-z]
     ;

//lexical UniSmall    
//      =   any Unicode lowercase letter;
 
lexical Large   
      = AscLarge 
//    | UniLarge
      ;

lexical AscLarge    
      = [A-Z]
      ;

//lexical uniLarge    =   any uppercase or titlecase Unicode letter

lexical Symbol  
      = AscSymbol 
//    | UniSymbol \ Special \ "_" \ "\"" \ "'"
      ;
 
lexical AscSymbol   
      = "!" 
      | "#" 
      | "$" 
      | "%" 
      | "&" 
      | "+"
      | "*" 
      | "." 
      | "/" 
      | "\<" 
      | "=" 
      | "\>" 
      | "?" 
      | "@"
      | "\\" 
      | "^" 
      | "|" 
      | "-" 
      | "~" 
      | ":"
      ;

// lexical uniSymbol   =   any Unicode symbol or punctuation

lexical Digit   
      = AscDigit 
//    | UniDigit
      ;

lexical AscDigit    
     =  [0-9]
     ;

// lexical uniDigit    =   any Unicode decimal digit

lexical OctIt   
      =   [0-7]
      ;

lexical HexIt   
      =  Digit 
      |  [A-Fa-f]
      ;
      
lexical QuasiVarId
     =  [a-zA-Z0-9_] !<< "\'" "\'"? Small (Small | Large | Digit)* !>> [a-zA-Z0-9_\'] 
     ;
      
lexical QuasiConId
     =  [a-zA-Z0-9_] !<< "\'" "\'"? Large (Small | Large | Digit)* !>> [a-zA-Z0-9_\'] 
     ;
 
lexical VarId   
      = [a-zA-Z0-9_] !<< (Small (Small | Large | Digit | "\'")*) !>> [a-zA-Z0-9_\'] \ ReservedId !>> "#"
      | [a-zA-Z0-9_] !<< (Small (Small | Large | Digit | "\'")*) !>> [a-zA-Z0-9_\'] \ ReservedId "#"
      | QuasiVarId
      ;

lexical ConId   
      = [a-zA-Z0-9_] !<< Large (Small | Large | Digit | "\'")* !>> [a-zA-Z0-9_\'] !>> "#"
      | [a-zA-Z0-9_] !<< Large (Small | Large | Digit | "\'")* !>> [a-zA-Z0-9_\'] "#"
      | QuasiConId
      ;

lexical ReservedId  
      = "case" 
      | "class" 
      | "data" 
      | "default" 
      | "deriving" 
      | "do" 
      | "else"
      | "foreign" 
      | "if" 
      | "import" 
      | "in" 
      | "infix" 
      | "infixl"
      | "infixr" 
      | "instance" 
      | "let" 
      | "module" 
      | "newtype" 
      | "of"
      | "then" 
      | "type" 
      | "where" 
      | "_"
      ;
 
lexical VarSym  
      = ( Symbol \ ":" Symbol* ) !>> [! # $ % & . + * / \< = \> ? @ \\ ^ | \- ~ :] \ ReservedOp \ Dashes
      ;

lexical ConSym  
      =  ( ":" Symbol+) \ "::"
      ;

lexical ReservedOp  
      = ".."
      | ":" 
      | "::" 
      | "=" 
      | "\\" 
      | "|" 
      | "\<-" 
      | "-\>" 
      | "@" 
      | "~" 
      | "=\>"
      | "-\<"
	  | "\>-"
	  | "-\<\<" 
	  | "\>\>-" 
      ;

lexical TyVar
     =  VarId  \ "family" \ "forall"
     ;

lexical TyCon   
      = ConId
      ;

lexical TyCls   
      = ConId
      ;

lexical ModId   
      = (ConId ".")* ConId
      ;
 
lexical QVarId  
      = (ModId ".")? VarId
      ;

lexical QConId  
      = (ModId ".")? ConId
      ;

lexical QTyCon  
      = (ModId ".")? TyCon
      ;

lexical QTyCls  
      = (ModId ".")? TyCls
      ;

lexical QVarSym   
      = ( ModId "." )? VarSym
      ;

lexical QConSym 
      = (ModId ".")? ConSym
      ;
 
lexical Decimal 
      = Digit Digit* !>> [0-9]
      ;

lexical Octal   
     =  OctIt OctIt* !>> [0-7]
     ;

lexical Hexadecimal 
      = HexIt HexIt* !>> [0-9A-Fa-f]
      ;
 
lexical Integer 
      = Decimal
      | "0o" Octal 
      | "0O" Octal
      | "0x" Hexadecimal 
      | "0X" Hexadecimal
      ;

lexical Float   
      = Decimal "." Decimal Exponent?
      | Decimal Exponent
      ;

lexical Exponent    
      = [eE] [+\-]? Decimal
      ;
 
lexical Char    
      = "\'" (Graphic \ [\'\\] | Space | Escape \ [\\&]) "\'"
      ;

lexical String  
      =  "\"" (Graphic \ [\"\\] | Space | Escape | Gap)* "\""
      ;

lexical Escape  
      = "\\" ( CharEsc | Ascii | Decimal | ("o" Octal) | ("x" Hexadecimal) )
      ;
      
lexical CharEsc 
      = "a" 
      | "b" 
      | "f" 
      | "n" 
      | "r" 
      | "t" 
      | "v" 
      | "\\" 
      | "\"" 
      | "\'" 
      | "&"
      ;

lexical Ascii   
      = "^" Cntrl 
      | "NUL" 
      | "SOH" 
      | "STX" 
      | "ETX" 
      | "EOT" 
      | "ENQ" 
      | "ACK"
      | "BEL" 
      | "BS" 
      | "HT" 
      | "LF" 
      | "VT" 
      | "FF" 
      | "CR" 
      | "SO" !>> "H" 
      | "SI" 
      | "DLE"
      | "DC1" 
      | "DC2" 
      | "DC3" 
      | "DC4" 
      | "NAK" 
      | "SYN" 
      | "ETB" 
      | "CAN"
      | "EM" 
      | "SUB" 
      | "ESC" 
      | "FS" 
      | "GS" 
      | "RS" 
      | "US" 
      | "SP" 
      | "DEL"
      ;

lexical Cntrl   
      = AscLarge 
      | "@" 
      | "[" 
      | "\\" 
      | "]" 
      | "^" 
      | "_"
      ;

lexical Gap 
      = "\\" WhiteChar WhiteChar* "\\"
      ;
      