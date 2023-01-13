package main;


public class Verifier {

    private final SymbolTable symbolTable;

    public Verifier() {
        symbolTable = new SymbolTable();
    }

    /**
     * eg: "void boo(int a, int b, String s) {"
     * parameters is a comma-separated list of parameters.
     * Each parameter is a pair of a valid type
     * and a valid variable name, without a value.
     * *******************
     * method name is defined similarly to variable names (i.e., a sequence of length > 0,
     * containing letters (uppercase or lowercase), digits and underscore),
     * with the exception that method  names must start with a letter
     * (i.e., they may not start with a digit or an underscore).
     * *******************
     * Only void methods are supported.
     */
    private void verifyMethod(){

    }

    /**
     * Local variable declaration lines
     * Variable assignment lines
     */
    private void verifyMethodBody(){

    }

    /**
     * eg: "int a = 5;"
     * Variable declaration lines
     */
    private void verifyVarDec(){

    }

    /**
     * eg: "a = 5;" variable assignment lines
     * eg: "int a = 5;" variable declaration line
     * eg: "if (a == 5) {"
     * eg: "while (a == 5) {"
     * eg: "return 5;"
     * eg: "foo(5,6);"
     */
    private void verifyStatement(){

    }

    private void verifyAssignment(){

    }

    /**
     * only void methods are supported
     * eg: "foo(a,b); a and b must be existing variables"
     * variables without type
     */
    private void verifyCallingMethod(){

    }


    private void verifyParameterList(){

    }

    private void verifyIf(){

    }

    private void verifyWhile(){

    }

    /**
     * this method verifies the condition of the if statement
     * the condition is strictly defined by the s-java description (Section 5.4)
     * eg: "if (a) {"
     * eg: "if (isFantastic && -3.25 || true) {"
     */
    private void verifyCondition(){

    }

    private void verifyReturn(){

    }

}
