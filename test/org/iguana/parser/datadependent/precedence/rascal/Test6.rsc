module precedence::Test6

syntax S = E;

syntax E = E "z"
         > "x" E
         > E "w"
         | "a";
         
public str input1 = "xawz"; // ((xa)w)z; not: x((aw)z)

public loc l1 = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/Test6_1.java|;

public void main() {
	generate(#S,input1,l1);
}