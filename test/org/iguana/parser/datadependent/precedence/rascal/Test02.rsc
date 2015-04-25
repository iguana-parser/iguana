module precedence::Test02

syntax S = E;

syntax E = left E "+" E
         | "a";
          
public loc l = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/Test02.java|;
public str input ="a+a+a";

public void main() {
	generate(#S,input,l);
}