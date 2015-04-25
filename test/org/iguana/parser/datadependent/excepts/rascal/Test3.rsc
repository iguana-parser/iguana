module excepts::Test3

// Interesting case: see Test2_3.

syntax S = E!star;

syntax E = star: E!plus "*" E!star!hat
         | hat: E!minus!plus "^" E!hat
         | minus: "-" E
         | plus: "+" E
         | "a";
         
public str input1 = "a^+a*a"; // a^(+(a*a))

public loc l1 = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/excepts/Test3_1.java|;

public void main() {
	generate(#S,input1,l1);
}