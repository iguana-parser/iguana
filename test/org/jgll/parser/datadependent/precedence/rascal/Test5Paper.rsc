module precedence::Test5Paper

syntax S = E;

syntax E = "-" E
         > left E "*" E
         > left E "/" E
         > "+" E
         > left E "+" E
         > left E "-" E
         | "(" E ")"
         | "a";

public str input = "a*+a*a+a--a/a"; // ((a*(+(a*a)))+a)-((-a)/a)

public loc l = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/precedence/Test5Paper.java|;

void main() {
    generate(#S,input,l);
}