module precedence::Test2Paper

syntax S = E;

syntax E = "-" E
         > left E "*" E
         > left E "+" E
         | "a";
         
public str input1 = "-a+-a*a+-a*a*-a+-a+a"; // (((-a+(-a*a))+((-a*a)*-a))+-a)+a 

public loc l1 = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/Test2Paper.java|;

public void main() {
	generate(#S,input1,l1);
}