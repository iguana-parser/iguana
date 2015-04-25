module precedence::Test3

syntax S = E;

syntax E = right E "^" E
         > left E "*" E
         > left E "+" E
         | "a";
         
public str input1 = "a+a^a^a*a"; // a+((a^(a^a))*a)

public loc l1 = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/Test3_1.java|;

public void main() {
	generate(#S,input1,l1);
}