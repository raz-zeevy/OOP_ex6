package main;

import main.entities.ParamsContainer;
import main.entities.Variable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.SharedUtilis.*;



/**
 * TODOS
 // TODO : change the split in assignments and decleration to exclude "," when in "" or '' / OMRI
 */
public class Verifier {

    public static final int GLOBAL_SCOPE_INDEX = 0;
    public static final String CHAR_LITERAL = "charLiteral";
    public static final String r_OR_ANY_STRING_CHAR = "|\\\".*\\\"|\'.*\'|";
    public static final String r_SPLIT_BY_COMMA = "\\s*,\\s*";
    private final SymbolTable symbolTable;
    private BufferedReader reader;
    private String line;
    private int scope;
    private boolean firstReadMode = false;
    private int lineNumber;
    private boolean hasReturned;

    public Verifier() {
        symbolTable = new SymbolTable();
        scope = 0;
    }

//    public void printSymbolTable() {
//        symbolTable.printSymbolTable();
//    }

    public void initReader(BufferedReader reader) {
        this.lineNumber = 0;
        this.scope = 0;
        this.reader = reader;
        nextLine();
    }

    public void verify(BufferedReader reader) {
        initReader(reader);
        for (; line != null; nextLine()) {
            if (!(verifyVarDec() || verifyMethod() || verifyAssignment())){
                    throwVerifierException("Invalid method declaration");
            }
        }
    }

    public void firstRead(BufferedReader reader) {
        initReader(reader);
        firstReadMode = true;
        for (; line != null; nextLine()) {
            verifyMethod();
            if (scope == GLOBAL_SCOPE_INDEX) {
                verifyVarDec();
                verifyAssignment();
            }
        }
        symbolTable.setGlobalInit();
        firstReadMode = false;
        if (scope != 0){
            throwVerifierException("error: end of block missing");
        }
    }

    public void nextLine() {
        try {
            line = reader.readLine();
            lineNumber++;
            while (line != null && (verifyComment() || line.trim().isEmpty())) {
                line = reader.readLine();
                lineNumber++;
            }
            if (line != null){
                verifyEndOfLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void verifyEndOfLine() {
        Pattern endOfLinePattern = Pattern.compile("(.*(?:;|\\{)\\s*)|\\s*}\\s*");
        if (!endOfLinePattern.matcher(line).matches()){
            throwVerifierException("error: ';' expected");
        }
        Pattern openScopePattern = Pattern.compile(".*([{]\\s*)$");
        Pattern closeScopePattern = Pattern.compile(".*([}]\\s*)$");
        if (openScopePattern.matcher(line).matches()){
            scope++;
        }
        if (closeScopePattern.matcher(line).matches()){
            scope--;
        }
    }

    private void verifyEndBlock() {
        if (!line.strip().equals("}")) {
            throwVerifierException("Expected end of block");
        }
        symbolTable.removeLocalSymbolTable();
    }

    public boolean verifyComment() {
        Pattern pattern = Pattern.compile("//.*");
        return pattern.matcher(line).matches();
    }

    /**
     * this method verifies that type(var) = type(expression)
     * casts according to the following:
     * string <- string, string literal String a = "asd", String a = stringVar
     * int <- int, int literal
     * double <- int, int literal, double, double literal
     * boolean <- int, int literal, double, double literal, boolean, boolean literal
     * @param type
     * @param expression
     * @return true if expression is of type "type"
     */
    public boolean checkType(String expression, String type){
        String expType = getExpressionType(expression);
        switch (type) {
            case STRING:
                return expType.equals(STRING) || expType.equals(STRING_LITERAL);
            case INT:
                return expType.equals(INT_LITERAL) || expType.equals(INT);
            case DOUBLE:
                return expType.equals(INT_LITERAL) || expType.equals(DOUBLE_LITERAL)
                        || expType.equals(DOUBLE) || expType.equals(INT);
            case BOOLEAN:
                return expType.equals(INT_LITERAL) || expType.equals(DOUBLE_LITERAL)
                        || expType.equals(BOOLEAN_LITERAL) || expType.equals(BOOLEAN)
                        || expType.equals(INT) || expType.equals(DOUBLE);

            case CHAR:
                return expType.equals(CHAR) || expType.equals(CHAR_LITERAL);
            default:
                return expType.equals(type);
        }
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
    private boolean verifyMethod() {
        Pattern pattern = Pattern.compile("\\s*void\\s+([a-zA-Z]\\w*)\\s*\\(\\s*(.*)\\s*\\)\\s*\\{\\s*");
        Matcher match = pattern.matcher(line);
        if (match.matches()) {
            String methodName = match.group(1);
            String methodParams = match.group(2);
            if (!verifyParameterList(methodParams)) {
                throwVerifierException("Invalid method parameters");
            }
            if (firstReadMode) {
                ParamsContainer params = getParameters(methodParams);
                symbolTable.addMethod(methodName, params);
            } else {
                symbolTable.addLocalSymbolTable();
                symbolTable.addMethodLocals(methodName);
                nextLine();
                verifyMethodBody();
                if (!hasReturned)
                    throwVerifierException("Method must end with a return statement");
                verifyEndBlock();
                symbolTable.resetGlobalInit();
            }
        }
        return match.matches();
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

    //TODO: Raz
    /**
     * eg: "int a = 5, b = 3, c;"
     * Variable declaration lines
     * check variable naming conventions in sJava
     */
    private boolean verifyVarDec() {
        String varsTypesRx = String.join("|", variableTypes);
        Pattern pattern2 = Pattern.compile("\\s*(final\\s*)?\\s*("+ varsTypesRx +")\\s+(((_[[a-zA-Z]\\w*])|" +
                "([a-zA-Z]\\w*)" +
                "\\s*(?:;|=\\s*(\\S*"+r_OR_ANY_STRING_CHAR+"\\s*)\\s*)?)(\\s*,\\s*[^;]+)?)+\\s*;\\s*");
        Pattern pattern = Pattern.compile("\\s*(final\\s*)?\\s*("+ varsTypesRx +")\\s+(.*)\\s*;\\s*");
        Matcher match = pattern.matcher(line);
        if (match.matches()) {
            // firstReadMode validation
            if (!firstReadMode && scope == 0){
                return true;
            }
            String varFinal = match.group(1);
            String varType = match.group(2);

            for (String dec : match.group(3).split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?=(?:[^']*'[^']*')*[^']*$)", -1)){
//            for (String dec : match.group(3).split(r_SPLIT_BY_COMMA)){
                Pattern varPattern = Pattern.compile("\\s*(_?[a-zA-Z]\\w*)\\s*(=\\s*([\\S^;]+|\".*\"|))" +
                        "?\\s*;?");
                Matcher varMatch = varPattern.matcher(dec);
                // needed in order for the groups to be calculated
                if (!varMatch.matches()){
                    throwVerifierException("invalid variable declaration");
                }
                String varName = varMatch.group(1);
                String varValue = varMatch.group(3);
                // check if variable wasn't declared in the scope yet:
                if (symbolTable.inLocalScope(varName)) {
                    throwVerifierException("Variable " + varName + " already declared in this scope");
                }
                // check if final and has value
                if (varValue == null && varFinal != null)
                    throwVerifierException("Final variable must be declared with a value");
                // check if value matches type
                if (varValue != null && !checkType(varValue, varType))
                    throwVerifierException("Variable type doesn't match value type");
                // adds variable to SymbolTable
                symbolTable.addVariable(varName, varType, varFinal != null);
                if (varValue != null) {
                    symbolTable.initVariable(varName);
                }
            }

        }
        return match.matches();
    }

    /**
     * Governs hasReturend field which is used to verify that all methods end
     * with a return statement.
     * that mechanism is implemented like that becuase it's impossible to
     * verify that a method has ended until the a } is reached.
     * and when that happens the last line has already been processed.
     * eg: "a = 5;" variable assignment lines
     * eg: "int a = 5;" variable declaration line
     * eg: "if (a == 5) {"
     * eg: "while (a == 5) {"
     * eg: "return 5;"
     * eg: "foo(5,6);"
     */
    private boolean verifyStatement() {
        if (verifyReturn()) {
            hasReturned = true;
            return true;
        }
        if (verifyAssignment() || verifyVarDec() || verifyIf() ||
                verifyCallingMethod() || verifyWhile()) {
            hasReturned = false;
            return true;
        }
        return false;
    }

    //TODO: Omri
    private boolean verifyAssignment() {

//        for (String assignment : line.split(r_SPLIT_BY_COMMA)) {
        for (String assignment : line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?=(?:[^']*'[^']*')*[^']*$)", -1)) {
//            Pattern pattern = Pattern.compile("\\s*([a-zA-z]\\w*)\\s*=\\s*(\".*\")\\s*;");
            Pattern pattern = Pattern.compile("\\s*([a-zA-z]\\w*)\\s*=\\s*([\\S*^]"+r_OR_ANY_STRING_CHAR+");?");
            Matcher match = pattern.matcher(assignment);
            if (match.matches()) {
                // verify InnerScope
                if (!firstReadMode && scope == 0){
                    return true;
                }
                String varName = match.group(1);
                String varValue = match.group(2);
                // check if exist in symbolTable
                Variable var = symbolTable.getVariable(varName);
                if (var == null) {
                    throwVerifierException("Variable " + varName + " is not defined");
                }
                // check if type of varValue is the same type as variable
                if (!checkType(varValue, var.getType())) {
                    throwVerifierException("Variable type doesn't match value type");
                }
                // check if not final
                if (var.isInit() && var.isFinal()) {
                    throwVerifierException("Cannot assign value to final variable");
                }
                if (!var.isInit())
                    symbolTable.initVariable(varName);
            }
            else {return false;}
        }
        return true;
    }

    //TODO: Omri
    // TODO: add support for: boo(1,'3',"hello"); right now 'x' is not supported
    /**
     * only void methods are supported
     * eg: "foo(a,b); a and b must be existing variables"
     * variables without type
     */
    private boolean verifyCallingMethod() {
        Pattern pattern = Pattern.compile("\\s*([a-zA-Z]\\w*)\\s*\\((([a-zA-Z]\\w*|\".*\"|\\d+)(,\\s*" +
                "([a-zA-Z]\\w*|\".*\"|\\d+))*)?\\);");
        Matcher match = pattern.matcher(line.replaceAll("\\s",""));
        if (match.matches()) {
            String methodName = match.group(1);
            // check if method exist in symbolTable
            if (!symbolTable.isMethodExists(methodName)) {
                throwVerifierException("Unknown Method" + methodName);
            }
            String methodArgs = match.group(2);
            // check if arguments match method parameters
            if (!checkMethodsCallParams(methodName, methodArgs)){
                throwVerifierException("Invalid method call parameters");
            }
        }
        return match.matches();
    }

    private boolean checkMethodsCallParams(String methodName, String methodArgs){
        if (methodArgs == null){
            return symbolTable.getMethodParams(methodName).size() == 0;
        }
        String[] vars = methodArgs.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        ParamsContainer params =  symbolTable.getMethodParams(methodName);
        if (params.size() != vars.length) { return false;}; // check if number of params of the method are equal to the vars
        int i = 0;
        for (Variable param: params) {
            if (!checkType(vars[i++], param.getType())){
                return false; //check if arguments fit function paramters
            }
        }
        return true;
    }

    //TODO: Changed this function
    /**
     * returns the TYPE of the given string
     * @param expression
     * @return
     */
    public String getExpressionType(String expression) {
        //TODO check if empty char is valid
        Pattern charLiteralPattern = Pattern.compile("'.?'");
        Pattern stringLiteralPattern = Pattern.compile("\".*\"");
        Pattern intLiteralPattern = Pattern.compile("(-|\\+)?\\d+");
        Pattern doubleLiteralPattern = Pattern.compile("(-|\\+)?((\\d*\\.?\\d+)|(\\d+\\.?\\d*))");
        Pattern boolLiteralPattern = Pattern.compile("true|false");
        Pattern varPattern = Pattern.compile("[a-zA-Z]\\w*");
        if (charLiteralPattern.matcher(expression).matches()) {
            return CHAR_LITERAL;
        } else if (stringLiteralPattern.matcher(expression).matches()) {
            return STRING_LITERAL;
        } else if (intLiteralPattern.matcher(expression).matches()) {
            return INT_LITERAL;
        } else if (doubleLiteralPattern.matcher(expression).matches()) {
            return DOUBLE_LITERAL;
        } else if (boolLiteralPattern.matcher(expression).matches()) {
            return BOOLEAN;
        } else if (varPattern.matcher(expression).matches()) {
            Variable var = symbolTable.getVariable(expression);
            if (var == null) {
                throwVerifierException("Variable \"" + expression + "\" is not defined");
            } else {
                if (!var.isInit()){
                    throwVerifierException("Variable \"" + expression + "\" wasn't initiated");
                }
                return var.getType();
            }
        }
        else
         throwVerifierException("Invalid expression");
        return null;
    }

    // TODO: Raz
    private boolean verifyParameterList(String params) {
        String varsTypesRx = String.join("|", variableTypes);
        String regPattern = "(\\s*(final\\s)?(" + varsTypesRx + ")\\s+([a-zA-Z]\\w*)\\s*[,]?)*|\\s*";
        Pattern pattern = Pattern.compile(regPattern);
        Matcher match = pattern.matcher(params);


//        HashMap<String, Variable> Args = new HashMap<String, Variable>();
//
//        for (String str : params.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?=(?:[^']*'[^']*')*[^']*$)", -1)){
//            String[] argument =  str.trim().split(" ");
//            Variable var = new Variable(argument[1], argument[0], false);
//            if (Args.containsKey(argument[1]) && Args.get(argument[1]).getType().equals(argument[0])){
//                return false;
//            }
//            Args.put(argument[1],var); //TODO how?!?!
//        }

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
            if (params.nameExists(varName)) {
                throwVerifierException("each parameter name must be unique");
            }
            params.add(new Variable(varName, varType, varFinal != null));
        }
        return params;
    }

    //TODO: Omri
    private boolean verifyIf() {
        Pattern pattern = Pattern.compile("\\s*if\\s*\\(\\s*(.*)\\s*\\)\\s*\\{\\s*");
        Matcher match = pattern.matcher(line);
        if (match.matches()) {
            String condition = match.group(1);
            verifyCondition(condition);
            symbolTable.addLocalSymbolTable();
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
        Pattern pattern = Pattern.compile("\\s*while\\s*\\(\\s*(.*)\\s*\\)\\s*\\{\\s*");
        Matcher match = pattern.matcher(line);
        if (match.matches()) {
            String condition = match.group(1);
            verifyCondition(condition);
            symbolTable.addLocalSymbolTable();
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
    private boolean verifyCondition(String TheCondition) {
        for (String condition : TheCondition.split("\\|\\||&&(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)" +
                "(?=(?:[^']*'[^']*')*[^']*$)", -1)) {
            condition = condition.trim();
            Pattern pattern = Pattern.compile("(\\s*([a-zA-Z]\\w*)|(-?\\d*.?\\d+)|(\\|\\||&&)\\s*)");
            Matcher match = pattern.matcher(condition);
            if (match.find()) {
                String type = getExpressionType(condition);
                if (type.equals(INT) || type.equals(DOUBLE)) {
                    if (symbolTable.getVariable(condition) == null) {
                        throwVerifierException("illegal condition");
                    }
                }
                if (type.equals(STRING) || type.equals(CHAR) || type.equals(STRING_LITERAL)){throwVerifierException("illegal condition");}
            }

            // TODO: raise Exception when condition is not valid
            // TODO: check types of variables with symboltable
            if (!match.matches()){throwVerifierException("illegal condition");}
        }
        return true;
    }

    //TODO: Omri
    private boolean verifyReturn() {
        Pattern pattern = Pattern.compile("\\s*return\\s*;\\s*");
        Matcher match = pattern.matcher(line);
        return match.matches();
    }

    private void throwVerifierException(String message) {
        throw new SJavaException("Line " + lineNumber + ": "+message);
    }

}
