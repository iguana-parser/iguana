module precedence::Test7

syntax S = E;

syntax E = E "z"
         > "x" E
         > E "w"
         > "y" E
         | "a";
         
public str input1 = "xyaw"; // x(y(aw))
public str input2 = "xawz"; // ((xa)w))z

public loc l1 = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/Test7_1.java|;
public loc l2 = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/Test7_2.java|;

public void main() {
	generate(#S,input1,l1);
	generate(#S,input2,l2);
}