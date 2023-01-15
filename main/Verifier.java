package main;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.SharedUtilis.*;


// TODO: add a mechanism to conclude the final output of the verifer
// TODO: check if there is next line in the reader
// TODO: and conclude the final output
public class Verifier {

    private final SymbolTable symbolTable;
    private final BufferedReader reader;
    private String line;

    public Verifier(BufferedReader reader) throws IOException {
        this.reader = reader;
        symbolTable = new SymbolTable();
        nextLine();
    }
    public void verify(){
        verifyGlobalVarDec();
        for( ;line != null; nextLine()){
            verifyMethod();
        }
    }
    public void nextLine(){
        try {
            line = reader.readLine();
            while (verifyComment()) {
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifyComment(){
        Pattern pattern = Pattern.compile("//.*");
        return pattern.matcher(line).matches();
    }

    //TODO: Omri
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
        Pattern pattern = Pattern.compile("void\\s+([a-zA-Z]\\w*)\\s*\\((.*)\\)\\s*\\{");

    }

    /**
     * Local variable declaration lines
     * Variable assignment lines
     */
    private void verifyMethodBody(){

    }

    private void verifyGlobalVarDec(){
        while (verifyVarDec("GLOBAL")){
            nextLine();
        }
    }


    /**
     * eg: "int a = 5;"
     * Variable declaration lines
     * check variable naming conventions in sJava
     */
    private boolean verifyVarDec(String scope){
        String varsTypesRx = String.join("|", variableTypes);
        Pattern pattern = Pattern.compile("(final\\s)?("+varsTypesRx+")\\s+([a-zA-Z]\\w*)\\s*(;|=\\s*\\S;)?");
        Matcher match = pattern.matcher(line);
        if(match.matches()){
            String varFinal = match.group(1);
            String varType = match.group(2);
            String varName = match.group(3);
            String varValue = match.group(4);
//            symbolTable.addLocalVariable(varName, varType);
        }
        return match.matches();
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

    //TODO: Omri
    private void verifyAssignment(){

    }

    //TODO: Omri
    /**
     * only void methods are supported
     * eg: "foo(a,b); a and b must be existing variables"
     * variables without type
     */
    private void verifyCallingMethod(){

    }

    private void verifyParameterList(){

    }

    //TODO: Omri
    private void verifyIf(){

    }

    //TODO: Omri
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

    //TODO: Omri
    private void verifyReturn(){

    }

}
