module precedence::Test11

syntax S = E;

syntax E = left E "*" E
         > "-" E
         > left E "+" E
         > "+" E
         | "a";
         
public str input1 = "a*-a+a"; // (a*-a)+a
public str input2 = "a*-a*a"; // a*-(a*a)
public str input3 = "a*+a+a"; // a*+(a+a)
public str input4 = "a*+a*a"; // a*+(a*a)  

public loc l1 = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/Test11_1.java|;
public loc l2 = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/Test11_2.java|;
public loc l3 = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/Test11_3.java|;
public loc l4 = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/Test11_4.java|;

public void main() {
	generate(#S,input1,l1);
	generate(#S,input2,l2);
	generate(#S,input3,l3);
	generate(#S,input4,l4);
}