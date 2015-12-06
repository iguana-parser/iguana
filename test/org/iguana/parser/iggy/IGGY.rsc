module iggy::IGGY

keyword Keywords = "regex" | "var" | "left" | "right" | "non-assoc" | "align" | "offside" | "ignore";

token CommentChar = [/] | [*]* ![/ *];
                  
token Comment = "/*" CommentChar* [*]+  "/" | "//" ![\r \n]* [\r\n];

token WhiteSpaces = [\ \t \f \r \n]+;

token Letter = [A-Za-z$_];
token LetterOrDigit = [A-Za-z$_0-9];
token LetterOrDigits = Letter (Letter | LetterOrDigit)*;

token Number = [0] | [1-9] [0-9]*;

token Character = "\\" [\' \" \\ t f r n] | ![\' \" \\];
token RangeChar = "\\" [\\ \[ \] \- t f r n \ ] | ![\\ \[ \] \- \ ];

token Char = "\'" Character* "\'";
token String = "\"" Character* "\"";

lexical WhiteSpaceOrComment = WhiteSpaces | Comment;              
lexical Layout = WhiteSpaceOrComment* !>> [\ \t \f \r \n] !>> "/*" !>> "//";

lexical Identifier = Ident: [$A-Z_a-z] !<< LetterOrDigits \ Keywords;
lexical NontName = Identifier;
lexical VarName = Identifier;
lexical Label = Identifier;

lexical Tag = "@NoLayout" | "@Layout";

token Associativity = "left" | "right" | "non-assoc";

syntax LAttribute = Label: "\<" Label "\>";
syntax AAttribute = Assoc: Associativity;

syntax Attribute = LAttribute | AAttribute;

syntax Definition = @scope="NontName" Rule+;

syntax Rule = @scope="VarName" Syntax: Tag? NontName@0 Parameters? "::=" Body
            | "regex" RegexRule
            | "regex" "{" RegexRule+ "}"
            ;

syntax RegexRule = Regex: NontName@0 "::=" RegexBody;
            
syntax Body = {Alternates "\>"}*;

syntax Alternates = Prec: {Alternate "|"}+;
            
syntax Alternate = Sequence: Sequence 
                 | Assoc: "(" Sequence ("|" Sequence)+ ")" AAttribute
                 ;            
            
syntax RegexBody = {RegexSequence "|"}*;
                
syntax Sequence = MoreThanTwo: Symbol Symbol+ ReturnExpression? Attribute*
                | Single: Symbol ReturnExpression? LAttribute?
                ;
                
syntax RegexSequence = Regex+;

syntax Parameters = "(" {VarName@0 ","}* ")"; 

syntax Symbol =                  Call        : Symbol Arguments
              >                  Offside     : "offside" Symbol   
              >                  Star        : Symbol "*"
              |                  Plus        : Symbol "+"
              |                  Option      : Symbol "?"
              | @scope="VarName" Sequence    : "(" Symbol Symbol+ ")"
              | @scope="VarName" Alternation : "(" Symbols ("|" Symbols)+ ")"
              >                  Align       : "align"   Symbol
              |                  Ignore      : "ignore"  Symbol
              |                  Conditional : Expression "?" Symbol ":" Symbol 
              
              >                  Variable    : VarName@0 "=" Symbol
              |                  Labeled     : VarName@0 ":" Symbol
              
              |                  Constraints : "[" {Expression ","}+ "]"
              |                  Bindings    : "{" {Binding ","}+ "}"
              
              |                  Precede     : Regex "\<\<" Symbol
              |                  NotPrecede  : Regex "!\<\<" Symbol
              >                  Follow      : Symbol "\>\>" Regex
              |                  NotFollow   : Symbol "!\>\>" Regex
              |                  Exclude     : Symbol "\\" Regex
              |                  Except      : Symbol "!" !>> "\>" Label
              
              |                  Nont        : NontName@1
              |                  String      : String
              |                  Character   : Char
              ;

syntax Symbols = Sequence: Symbol*;

syntax Arguments = "(" {Expression ","}* ")";
              
syntax Regex = Star        : Regex "*"
             | Plus        : Regex "+"
             | Option      : Regex "?"
             | Bracket     : "(" Regex ")"
             | Sequence    : "(" Regex Regex+ ")"  
             | Alternation : "(" Regexs ("|" Regexs)+ ")"
               
             | Nont        : NontName@1
             | CharClass   : CharClass
             | String      : String
             | Character   : Char
             ;
             
syntax Regexs = Sequence: Regex+;
             
syntax CharClass = Chars: "[" Range* "]" | NotChars: "[^" Range* "]";

syntax Range = RangeChar "-" RangeChar | RangeChar;

syntax Expression =            Call           : Expression Arguments
                  > left      (Multiplication : Expression "*" Expression
                  |            Division       : Expression "/" Expression)
                  > left      (Plus           : Expression "+" Expression
                  |            Minus          : Expression "-" Expression)
                  > non-assoc (GreaterEq      : Expression "\>=" Expression  
		          |            LessEq         : Expression "\<=" Expression
		          |            Greater        : Expression "\>" Expression
		          |            Less           : Expression "\<" Expression)
	              > non-assoc (Equal          : Expression "==" Expression
	              |            NotEqual       : Expression "!=" Expression)
	              > left      (And            : Expression "&&" Expression 
	              |            Or             : Expression "||" Expression)
	              |            LExtent        : VarName@1 ".l"
	              |            RExtent        : VarName@1 ".r"
	              |            Yield          : VarName@1 ".yield"
                  |            Name           : VarName@1
                  |            Number         : Number
                  |            Bracket        : "(" Expression ")"
                  ;

syntax ReturnExpession = ReturnExpession: "{" Expression "}";

syntax Binding = Assign  : VarName@1 "=" Expression
               | Declare : "var" VarName@0 "=" Expression
               ;

str input = "";

public void main() {
	save(#Definition,"/Users/anastasiaizmaylova/git/iguana/test/org/iguana/parser/iggy/IGGY");
}