module precedence::Test4Paper

layout L = [\t \n \r \ ]* !>> [\t \n \r \ ];

syntax S = E;

syntax E = "-" E
         > left E "*" E
         > left E "+" E
         > "if" E "then" E "else" E
         | "a";
         
public str input1 = "a + if a then a else a + a"; // a + (if a then a else (a + a)) 
public str input2 = "- if a then a else a + a";   // -(if a then a else (a + a))

public loc l1 = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/Test4Paper_1.java|;
public loc l2 = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/Test4Paper_2.java|;

public void main() {
	generate(#S,input1,l1);
	generate(#S,input2,l2);
}