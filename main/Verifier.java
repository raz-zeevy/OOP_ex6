package main;


import main.entities.ParamsContainer;
import main.entities.Variable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.SharedUtilis.*;


/**
 1. Verify entire code
 2. if no exceptions were thrown, return 0
 3. else, return 1 and print the exception msg
/**

/**
 * TODOS
 * add function to check types (a == b etc') TODO: Raz
 */
public class Verifier {

    private final SymbolTable symbolTable;
    private BufferedReader reader;
    private String line;
    private boolean methodReadMode = false;
    private int lineNumber;

    public Verifier() {
        ;
        symbolTable = new SymbolTable();
    }

    public void printSymbolTable() {
        symbolTable.printSymbolTable();
    }

    public void initReader(BufferedReader reader) {
        this.lineNumber = 0;
        this.reader = reader;
        nextLine();
    }

    public void verify(BufferedReader reader) {
        initReader(reader);
        verifyGlobalVarDec();
        for (; line != null; nextLine()) {
            verifyMethod();
        }
    }

    //TODO: Raz
    public void getMethods(BufferedReader reader) {
        initReader(reader);
        methodReadMode = true;
        for (; line != null; nextLine()) {
            verifyMethod();
        }
        methodReadMode = false;
    }

    //TODO: Raz
    public void nextLine() {
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

    private void verifyEndBlock() {
        if (!line.strip().equals("}")) {
            throwVerifierException("Invalid method body");
        }
    }

    public boolean verifyComment() {
        Pattern pattern = Pattern.compile("//.*");
        return pattern.matcher(line).matches();
    }

    /**
     *
     * @param type
     * @param expression
     * @return true if expression is of type "type"
     */
    public boolean verifyType(String expression, String type){
    // check if in symbol table
        // if yes, check if type is the same
    // if not regex to check if it's a literal or a number

        return true;
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
    private void verifyMethod() {
        Pattern pattern = Pattern.compile("void\\s+([a-zA-Z]\\w*)\\s*\\((.*)\\)\\s*\\{");
        Matcher match = pattern.matcher(line);
        if (match.matches()) {
            String methodName = match.group(1);
            String methodParams = match.group(2);
            if (!verifyParameterList(methodParams)) {
                throwVerifierException("Invalid method parameters");
            }
            if (methodReadMode) {
                ParamsContainer params = getParameters(methodParams);
                symbolTable.addMethod(methodName, params);
            } else {
                nextLine();
                verifyMethodBody();
                verifyEndBlock();
            }
        } else if (!methodReadMode) {
            throwVerifierException("Invalid method declaration");
        }
    }

    /**
     * Local variable declaration lines
     * Variable assignment lines
     */
    private void verifyMethodBody() {
        while (verifyStatement()) {
            nextLine();
        }
    }

    private void verifyGlobalVarDec() {
        while (verifyVarDec()) {
            nextLine();
        }
    }

    //TODO: Raz
    /**
     * eg: "int a = 5;"
     * Variable declaration lines
     * check variable naming conventions in sJava
     */
    private boolean verifyVarDec() {
        String varsTypesRx = String.join("|", variableTypes);
        Pattern pattern = Pattern.compile("\\s*(final\\s)?(" + varsTypesRx + ")\\s+([a-zA-Z]\\w*)\\s*(;" +
                "|=\\s*\\S*;)?");
        Matcher match = pattern.matcher(line);
        if (match.matches()) {
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
    private boolean verifyStatement() {
        return verifyAssignment() || verifyVarDec() || verifyIf() ||
                verifyCallingMethod() || verifyWhile() || verifyReturn();
    }

    //TODO: Omri
    private boolean verifyAssignment() {
        Pattern pattern = Pattern.compile("\\s*([a-zA-z]\\w*)\\s*=\\s*(\"?[a-zA-Z]\\w*\"?|-?\\d*.?\\d+);?");
        Matcher match = pattern.matcher(line);
        if (match.matches()) {
            String varName = match.group(1);
            String varValue = match.group(2);
        }
        // TODO: check if exist in symbolTable
        // TODO: check if type of varValue is the same type as variable
        // TODO: check if not final
        return match.matches();
    }

    //TODO: Omri
    /**
     * only void methods are supported
     * eg: "foo(a,b); a and b must be existing variables"
     * variables without type
     */
    private boolean verifyCallingMethod() {
        Pattern pattern = Pattern.compile("\\s*([a-zA-Z]\\w*)\\s*\\((([a-zA-Z]\\w*|\".*\"|\\d+)(,\\s*([a-zA-Z]\\w*|\".*\"|\\d+))*)?\\);");
        Matcher match = pattern.matcher(line);
        // TODO: extract arguments
        // TODO: check if arguments fit function paramters
        // TODO: check if methodName exists
        return match.matches();
    }

    // TODO: Raz
    private boolean verifyParameterList(String params) {
        String varsTypesRx = String.join("|", variableTypes);
        String regPattern = "(\\s*(final\\s)?(" + varsTypesRx + ")\\s+([a-zA-Z]\\w*)\\s*[,]?)*";
        Pattern pattern = Pattern.compile(regPattern);
        Matcher match = pattern.matcher(params);
        return match.matches();
    }

    //TODO: Raz
    private ParamsContainer getParameters(String paramsString) {
        ParamsContainer params = new ParamsContainer();
        String varsTypesRx = String.join("|", variableTypes);
        Pattern pattern = Pattern.compile("(final\\s)?(" + varsTypesRx + ")\\s+([a-zA-Z]\\w*)(,)?");
        Matcher match = pattern.matcher(paramsString);
        while (match.find()) {
            String varFinal = match.group(1);
            String varType = match.group(2);
            String varName = match.group(3);
            params.add(new Variable(varName, varType, varFinal != null));
        }
        return params;
    }

    //TODO: Omri
    private boolean verifyIf() {
        Pattern pattern = Pattern.compile("\\s*if\\s*\\((.*)\\)\\s*\\{");
        Matcher match = pattern.matcher(line);
        if (match.matches()) {
            String condition = match.group(1);
            verifyCondition(condition);
            nextLine();
            while (verifyStatement()) {
                nextLine();
            }
            verifyEndBlock();
        }
        return match.matches();
    }

    //TODO: Omri
    private boolean verifyWhile() {
        Pattern pattern = Pattern.compile("\\s*while\\s*\\((.*)\\)\\s*\\{");
        Matcher match = pattern.matcher(line);
        if (match.matches()) {
            String condition = match.group(1);
            verifyCondition(condition);
            nextLine();
            while (verifyStatement()) {
                nextLine();
            }
            verifyEndBlock();
        }
        return match.matches();
    }

    // Todo: Omri
    /**
     * this method verifies the condition of the if statement
     * the condition is strictly defined by the s-java description (Section 5.4)
     * eg: "if (false) {"
     * eg: "if (a) {"
     * eg: "if (3) {"
     * eg: "if (isFantastic && -3.25 || true) {"
     */
    private boolean verifyCondition(String condition) {
        String boolTypesRx = String.join("|", conditionables);
        Pattern pattern = Pattern.compile("((([a-zA-Z]\\w*)|(-?\\d*.?\\d+))\\s*(\\|\\||&&)\\s)+");
        Matcher match = pattern.matcher(line);
        // TODO: raise Exception when condition is not valid
        // TODO: check types of variables with symboltable
        return match.matches();
    }

    //TODO: Omri
    private boolean verifyReturn() {
        Pattern pattern = Pattern.compile("\\s*return\\s*;\\s*");
        Matcher match = pattern.matcher(line);
        return match.matches();
    }

    private void throwVerifierException(String message) {
        throw new RuntimeException("Line " + lineNumber + ": "+message);
    }



}
