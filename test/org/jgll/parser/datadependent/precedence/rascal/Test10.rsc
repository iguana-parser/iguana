module precedence::Test10

syntax S = E;

syntax E = left E "*" E
         > left E "+" E
         | "a"
         ;
         
public loc l1 = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/Test10_1.java|;
public str input1 ="a+a*a";

public void main() {
	generate(#S,input1,l1);
}