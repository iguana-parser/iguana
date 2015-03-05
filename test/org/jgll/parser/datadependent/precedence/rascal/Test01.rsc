module precedence::Test01

syntax S = E;

syntax E = E "+" "a"
         | E "-"
         | "a";
          
public loc l = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/Test01.java|;
public str input ="a+a-";

public void main() {
	generate(#S,input,l);
}
