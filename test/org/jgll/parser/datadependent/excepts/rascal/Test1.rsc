module excepts::Test1

syntax S = E;

syntax E = E!a "-"
         | left a: E "+" E
         | "a"
         ;
         
public str input1 = "a+a-";

public loc l1 = |file:///Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/excepts/Test1_1.java|;

public void main() {
	generate(#S,input1,l1);
}