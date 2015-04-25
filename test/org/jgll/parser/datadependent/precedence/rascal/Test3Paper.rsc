module precedence::Test3Paper

layout L = [\t \n \r \ ]* !>> [\t \n \r \ ];

syntax S = E;

syntax E = left E "+" E
         > "if" E "then" E "else" E
         | "a";
         
public str input1 = "a + if a then a else a + a"; // a + (if a then a else (a + a)) 

public loc l1 = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/Test3Paper.java|;

public void main() {
	generate(#S,input1,l1);
}