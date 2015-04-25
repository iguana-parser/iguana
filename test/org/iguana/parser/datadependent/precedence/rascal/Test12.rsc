module precedence::Test12

syntax S = E;

syntax E = "-" E
         > E "+"
         > "*" E
         | "a";
         
public str input1 = "-*a+"; // -(*(a+))

public loc l1 = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/Test12.java|;

public void main() {
	generate(#S,input1,l1);
}