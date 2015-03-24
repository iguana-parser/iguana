module haskell::HaskellPaper1

layout L = [\t \n \r \ ]* !>> [\t \n \r \ ];

syntax Module 
    = Decls
    ;

syntax Decls 
	= ignore ( "{" Decl (";" Decl)* "}" )
    | align (offside Decl)*
    ;
    
syntax Decl
    = FunLHS RHS
    ;
    
syntax RHS
    = "=" Exp "where" Decls
    ;
    
syntax Exp
    = "a"
    ;
    
lexical FunLHS = [a-zA-Z]+ !>> [a-zA-Z];

public loc l = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/haskell/HaskellPaper1.java|;

public void main() {
	generate(#Module, "", l);
}