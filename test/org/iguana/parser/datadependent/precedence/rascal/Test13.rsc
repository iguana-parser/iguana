module precedence::Test13

syntax S = E;

syntax E = left E "*" E
         > left E "+" E
         > "-" E
         | "a";
         
public str input1 = "a+-a+a"; //

public loc l1 = |file:///Users/anastasiaizmaylova/git/iguana/test/org/iguana/parser/datadependent/precedence/Test13.java|;

public void main() {
	generate(#S,input1,l1);
}