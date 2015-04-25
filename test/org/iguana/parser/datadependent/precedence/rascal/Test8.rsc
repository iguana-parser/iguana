module precedence::Test8

syntax S = E;

syntax E = E "*" E
         > left (E "^" E
                | "-" E
                | "+" E)
         | "a";
         
public str input1 = "-a^a";
public str input2 = "--a";
public str input3 = "-+a";

public loc l1 = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/Test8_1.java|;
public loc l2 = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/Test8_2.java|;
public loc l3 = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/Test8_3.java|;

public void main() {
	generate(#S,input1,l1);
	generate(#S,input2,l2);
	generate(#S,input3,l3);
}