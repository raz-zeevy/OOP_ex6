package main;


import main.entities.ParamsContainer;
import main.entities.Variable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static main.SharedUtilis.*;


// TODO: add a mechanism to conclude the final output of the verifer
// TODO: check if there is next line in the reader
// TODO: and conclude the final output

/**
 * TODOS
 * in the symbole table change the dsat and add final
 * add function to check types (a == b etc')
 * in the first read take out all the functions
 * add functions to the Stable
 *
 */
public class Verifier {

    private final SymbolTable symbolTable;
    private BufferedReader reader;
    private String line;
    private boolean methodReadMode = false;
    private int lineNumber;

    public Verifier() {;
        symbolTable = new SymbolTable();
    }

    public void printSymbolTable(){
        symbolTable.printSymbolTable();
    }

    public void initReader(BufferedReader reader) {
        this.lineNumber = 0;
        this.reader = reader;
        nextLine();
    }

    public void verify(BufferedReader reader){
        initReader(reader);
        verifyGlobalVarDec();
        for( ;line != null; nextLine()){
            verifyMethod();

        }
    }

    //TODO: Raz
    public void getMethods(BufferedReader reader){
        initReader(reader);
        methodReadMode = true;
        for( ;line != null; nextLine()){
            verifyMethod();
        }
    }

    //TODO: Raz
    public void nextLine(){
        try {
            line = reader.readLine();
            lineNumber++;
            while (line != null && (verifyComment() || line.trim().isEmpty())) {
                line = reader.readLine();
                lineNumber++;
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
        Matcher match = pattern.matcher(line);
        if(match.matches()){
            String methodName = match.group(1);
            String methodParams = match.group(2);
            if (!verifyParameterList(methodParams)){
                throw new RuntimeException("Line "+lineNumber+": Invalid method parameters");
            }
            if (methodReadMode) {
                ParamsContainer params = getParameters(methodParams);
                symbolTable.addMethod(methodName, params);
            }
            String methodArgs = match.group(2);
//            System.out.println("verifyMethod! "+"methodName: "+methodName+" methodArgs: "+methodArgs);

//           symbolTable.addLocalVariable(methodName,methodArgs);
        }
//        return match.matches();
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
    private void verifyAssignment() {
        Pattern pattern = Pattern.compile("\\s*([a-zA-Z]\\w*)\\s*(=)\\s*([a-zA-Z]\\w*|\".*\")");
        Matcher match = pattern.matcher(line);
        if (match.matches()) {
            String varName = match.group(1);
            String varValue = match.group(3);
            //            symbolTable.addLocalVariable(varName, varValue);
        }
    }

    //TODO: Omri
    /**
     * only void methods are supported
     * eg: "foo(a,b); a and b must be existing variables"
     * variables without type
     */
    private void verifyCallingMethod(){
            Pattern pattern = Pattern.compile("\\s*([a-zA-Z]\\w*)\\s*\\((([a-zA-Z]\\w*|\".*\"|\\d+)(,\\s*([a-zA-Z]\\w*|\".*\"|\\d+))*)?\\)(;)");
            Matcher match = pattern.matcher(line);
        if (match.matches()) {
            String funcName = match.group(1);
            String funcArgs = match.group(2);
            //            symbolTable.addLocalVariable(funcName, funcArgs);
        }
    }

    // TODO: Raz
    private boolean verifyParameterList(String params){
        String varsTypesRx = String.join("|", variableTypes);
        String regPattern = "(\\s*(final\\s)?("+varsTypesRx+")\\s+([a-zA-Z]\\w*)\\s*[,]?)*";
        Pattern pattern = Pattern.compile(regPattern);
        Matcher match = pattern.matcher(params);
        return match.matches();
    }

    //TODO: Raz
    private ParamsContainer getParameters(String paramsString){
        ParamsContainer params = new ParamsContainer();
        String varsTypesRx = String.join("|", variableTypes);
        Pattern pattern = Pattern.compile("(final\\s)?("+varsTypesRx+")\\s+([a-zA-Z]\\w*)(,)?");
        Matcher match = pattern.matcher(paramsString);
        while(match.find()){
            String varFinal = match.group(1);
            String varType = match.group(2);
            String varName = match.group(3);
            params.add(new Variable(varName, varType, varFinal != null));
        }
        return params;
    }

    //TODO: Omri
    private void verifyIf(){
        Pattern pattern = Pattern.compile("\\s*if\\s*\\((.*)\\)\\s*\\{");
        Matcher match = pattern.matcher(line);
        if (match.matches()) {
            String condition = match.group(1);
            //TODO check condition
        }
    }

    //TODO: Omri
    private void verifyWhile(){
        Pattern pattern = Pattern.compile("\\s*while\\s*\\((.*)\\)\\s*\\{");
        Matcher match = pattern.matcher(line);
        if (match.matches()) {
            String condition = match.group(1);
            //TODO check condition
        }
    }

    /**
     * this method verifies the condition of the if statement
     * the condition is strictly defined by the s-java description (Section 5.4)
     * eg: "if (false) {"
     * eg: "if (a) {"
     * eg: "if (3) {"
     * eg: "if (isFantastic && -3.25 || true) {"
     */
    private void verifyCondition(){
        String boolTypesRx = String.join("|", conditionables);
        Pattern pattern = Pattern.compile("\\s*\\(\\s*"+boolTypesRx+"|[a-zA-Z]\\w*|-?\\d+\\s*");
        Matcher match = pattern.matcher(line);
    }

    //TODO: Omri
    private void verifyReturn(){
        Pattern pattern = Pattern.compile("\\s*return\\s*\\{");
        Matcher match = pattern.matcher(line);
        if (match.matches()) {
            String streturn = match.group(1);
            //TODO works
        }
    }

}
