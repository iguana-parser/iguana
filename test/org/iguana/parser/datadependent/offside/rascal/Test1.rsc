module offsidee::Test1

layout Layout 
     = WhiteSpace* !>> [\t \n \r \f  \ ];

lexical WhiteSpace 
      = [\ \t \f \r \n \a1a];
      

syntax S = {offside Stat ";"}+;

syntax Stat = "x" "=" Exp;

syntax Exp = Exp "+" "a" | "a";


public str input1 = "x = a;
                    '    x = 
                    '         a +
                           a
                    ";
public str input2 = "";
public str input3 = "";
public str input4 = "";

public loc l1 = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/offside/Test1_1.java|;
public loc l2 = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/offside/Test1_2.java|;
public loc l3 = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/offside/Test1_3.java|;
public loc l4 = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/offside/Test1_4.java|;

public void main() {
	generate(#S,input1,l1);
	generate(#S,input2,l2);
	generate(#S,input3,l3);
	generate(#S,input4,l4);
}