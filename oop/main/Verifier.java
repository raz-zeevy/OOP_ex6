package oop.main;

import oop.main.entities.ParamsContainer;
import oop.main.entities.Variable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static oop.main.SharedUtilis.*;


/**
 * This class is responsible for verifying the code
 * and throwing errors if needed (e.g. variable not declared)
 */
public class Verifier {

    /** errors **/

    public static final String ERROR_INVALID_METHOD_DECLARATION = "error: Invalid method declaration";
    public static final String ERROR_END_OF_BLOCK_MISSING = "error: end of block missing";
    public static final String ERROR_END_OF_LINE = "error: ';' expected";

    public static final String ERROR_EXPECTED_END_OF_BLOCK = "error: expected end of block";

    public static final String ERROR_INVALID_METHOD_PARAMETERS = "error: invalid method parameters";
    public static final String ERROR_METHOD_MUST_END_WITH_A_RETURN_STATEMENT = "error: method must end with a return statement";
    public static final String ERROR_INVALID_VARIABLE_DECLARATION = "error: invalid variable declaration";
    public static final String ERROR_VARIABLE_ALREADY_DECLARED = "error: variable %s already declared in this scope";
    public static final String ERROR_FINAL_VARIABLE_DECLARED_NO_VALUE = "error: final variable must be declared with a value";
    public static final String ERROR_TYPE_VALUE_MISMATCH = "error: variable type doesn't match value type";

    public static final String ERROR_UNKNOWN_VARIABLE = "error: variable %s is not defined";
    public static final String ERROR_ASSIGNMENT_TO_FINAL = "error: cannot assign value to final variable";
    public static final String ERROR_UNKNOWN_METHOD = "error: unknown Method";
    public static final String ERROR_INVALID_METHOD_CALL_PARAMETERS = "error: invalid method call parameters";
    public static final String ERROR_VARIABLE_NOT_INIT = "error: variable \"%s\" wasn't initiated";
    public static final String ERROR_INVALID_EXPRESSION = "error: invalid expression";
    public static final String ERROR_NOT_UNIQUE_PARAM_NAME = "error: each parameter name must be unique";
    public static final String ERROR_ILLEGAL_CONDITION = "error: illegal condition";
    public static final String THROWER_PREFIX_TO_EXCEPTION = "Line %s: %s";

    /** regexes **/
    public static final String R_METHOD_DECLERATION = "\\s*void\\s+([a-zA-Z]\\w*)\\s*\\(\\s*(.*)\\s*\\)\\s*\\{\\s*";
    public static final String R_PIPE = "|";
    public static final String R_VAR_DEC_SPLIT = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?=(?:[^']*'[^']*')*[^']*$)";

    public static final String R_COMMENT = "//.*";
    public static final String R_OR_ANY_STRING_CHAR = "|\\\".*\\\"|\'.*\'|";
    public static final String R_END_OF_LINE = "(.*(?:;|\\{)\\s*)|\\s*}\\s*";
    public static final String R_OPEN_SCOPE = ".*([{]\\s*)$";
    public static final String R_CLOSE_SCOPE = ".*([}]\\s*)$";
    public static final String R_CLOSE_BRACKET = "}";
    public static final String R_VARIABLE_DECLERATION = "\\s*(_?[a-zA-Z]\\w*)\\s*(=\\s*([\\S^;]+|\".*\"|))" +
            "?\\s*;?";
    public static final String R_ASSIGNMENT_SPLIT = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?=(?:[^']*'[^']*')*[^']*$)";
    public static final String R_ASSIGNMENT = "\\s*([a-zA-z]\\w*)\\s*=\\s*([\\S*^]";
    public static final String R_METHOD_CALL = "\\s*([a-zA-Z]\\w*)\\s*\\((([a-zA-Z]\\w*|\".*\"|\\d+)(,\\s*([a-zA-Z]\\w*|\".*\"|\\d+))*)?\\);";
    public static final String R_SPACES = "\\s";
    public static final String R_EMPTY_STRING = "";
    public static final String R_METHOD_PARAMS_SPLIT = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    public static final String R_CHAR_LITERAL = "'.?'";
    public static final String R_STRING_LITERAL = "\".*\"";
    public static final String R_INT_LITERAL = "(-|\\+)?\\d+";
    public static final String R_DOUBLE_LITERAL = "(-|\\+)?((\\d*\\.?\\d+)|(\\d+\\.?\\d*))";
    public static final String R_BOOL_LITERAL = "true|false";
    public static final String R_VARIABLE = "[a-zA-Z]\\w*";
    public static final String R_IF = "\\s*if\\s*\\(\\s*(.*)\\s*\\)\\s*\\{\\s*";
    public static final String R_WHILE = "\\s*while\\s*\\(\\s*(.*)\\s*\\)\\s*\\{\\s*";
    public static final String R_CONDITION_SPLIT = "\\|\\||&&(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)(?=(?:[^']*'[^']*')*[^']*$)";
    public static final String R_CONDITION = "(\\s*([a-zA-Z]\\w*)|(-?\\d*.?\\d+)|(\\|\\||&&)\\s*)";

    public static final String R_RETURN = "\\s*return\\s*;\\s*";

    /** verifier constants **/
    public static final int GLOBAL_SCOPE_INDEX = 0;
    public static final String CHAR_LITERAL = "charLiteral";

    /** Verifier Fields **/
    private final SymbolTable symbolTable;
    private BufferedReader reader;

    private String line;
    private int scope;
    private boolean firstReadMode = false;
    private int lineNumber;
    private boolean hasReturned;

    /** constructor **/
    public Verifier() {
        symbolTable = new SymbolTable();
        scope = GLOBAL_SCOPE_INDEX;
    }

    /**
     * this method initializes the line number and the scope to 0 and the reader.
     * @param  reader
     * @return {0,1,2}
     */
    public void initReader(BufferedReader reader) {
        this.lineNumber = 0;
        this.scope = GLOBAL_SCOPE_INDEX;
        this.reader = reader;
        nextLine();
    }

    /**
     * This method verifies that the code is a valid Sjava code, and throws errors if needed (e.g. variable
     * not declared)
     * @param reader
     */
    public void verify(BufferedReader reader) {
        initReader(reader);
        for (; line != null; nextLine()) {
            if (!(verifyVarDec() || verifyMethod() || verifyAssignment())){
                    throwVerifierException(ERROR_INVALID_METHOD_DECLARATION);
            }
        }
    }

    /**
     * This method scans the entrie code and initializes the symbol table with the global variables and
     * methods.
     * @param reader
     */
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
        if (scope != GLOBAL_SCOPE_INDEX){
            throwVerifierException(ERROR_END_OF_BLOCK_MISSING);
        }
    }

    /**
     * this method gets the next line from the reader and skips empty lines and comments.
     */
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

    /**
     * this method verifies that the line ends with a semicolon or a closing bracket.
     */
    private void verifyEndOfLine() {
        Pattern endOfLinePattern = Pattern.compile(R_END_OF_LINE);
        if (!endOfLinePattern.matcher(line).matches()){
            throwVerifierException(ERROR_END_OF_LINE);
        }
        Pattern openScopePattern = Pattern.compile(R_OPEN_SCOPE);
        Pattern closeScopePattern = Pattern.compile(R_CLOSE_SCOPE);
        if (openScopePattern.matcher(line).matches()){
            scope++;
        }
        if (closeScopePattern.matcher(line).matches()){
            scope--;
        }
    }

    /**
     * this method verifies that there is an end of block in the end of the line.
     */
    private void verifyEndBlock() {
        if (!line.strip().equals(R_CLOSE_BRACKET)) {
            throwVerifierException(ERROR_EXPECTED_END_OF_BLOCK);
        }
        symbolTable.removeLocalSymbolTable();
    }

    /**
     * this method verifies that the line is a comment
     * @return true if the line is a comment, false otherwise
     */
    public boolean verifyComment() {
        Pattern pattern = Pattern.compile(R_COMMENT);
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
     * returns: true if the line is a valid method declaration, false otherwise
     */
    private boolean verifyMethod() {
        Pattern pattern = Pattern.compile(R_METHOD_DECLERATION);
        Matcher match = pattern.matcher(line);
        if (match.matches()) {
            String methodName = match.group(1);
            String methodParams = match.group(2);
            if (!verifyParameterList(methodParams)) {
                throwVerifierException(ERROR_INVALID_METHOD_PARAMETERS);
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
                    throwVerifierException(ERROR_METHOD_MUST_END_WITH_A_RETURN_STATEMENT);
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

    /**
     * eg: "int a = 5, b = 3, c;"
     * Variable declaration lines
     * check variable naming conventions in sJava
     * returns: true if the line is a valid variable declaration, false otherwise
     */
    private boolean verifyVarDec() {
        String varsTypesRx = String.join(R_PIPE, variableTypes);
        Pattern pattern = Pattern.compile("\\s*(final\\s*)?\\s*("+ varsTypesRx +")\\s+(.*)\\s*;\\s*");
        Matcher match = pattern.matcher(line);
        if (match.matches()) {
            // firstReadMode validation
            if (!firstReadMode && scope == GLOBAL_SCOPE_INDEX){
                return true;
            }
            String varFinal = match.group(1);
            String varType = match.group(2);
            for (String dec : match.group(3).split(R_VAR_DEC_SPLIT, -1)){
//            for (String dec : match.group(3).split(r_SPLIT_BY_COMMA)){
                Pattern varPattern = Pattern.compile(R_VARIABLE_DECLERATION);
                Matcher varMatch = varPattern.matcher(dec);
                // needed in order for the groups to be calculated
                if (!varMatch.matches()){
                    throwVerifierException(ERROR_INVALID_VARIABLE_DECLARATION);
                }
                String varName = varMatch.group(1);
                String varValue = varMatch.group(3);
                // check if variable wasn't declared in the scope yet:
                if (symbolTable.inLocalScope(varName)) {
                    throwVerifierException(String.format(ERROR_VARIABLE_ALREADY_DECLARED,varName));
                }
                // check if final and has value
                if (varValue == null && varFinal != null)
                    throwVerifierException(ERROR_FINAL_VARIABLE_DECLARED_NO_VALUE);
                // check if value matches type
                if (varValue != null && !checkType(varValue, varType))
                    throwVerifierException(ERROR_TYPE_VALUE_MISMATCH);
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

    /**
     * verify that the line is a valid assignment line
     * @return true if the line is a valid assignment line, false otherwise
     */
    private boolean verifyAssignment() {

//        for (String assignment : line.split(r_SPLIT_BY_COMMA)) {
        for (String assignment : line.split(R_ASSIGNMENT_SPLIT, -1)) {
//            Pattern pattern = Pattern.compile("\\s*([a-zA-z]\\w*)\\s*=\\s*(\".*\")\\s*;");
            Pattern pattern = Pattern.compile(R_ASSIGNMENT + R_OR_ANY_STRING_CHAR +");?");
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
                    throwVerifierException(String.format(ERROR_UNKNOWN_VARIABLE, varName));
                }
                // check if type of varValue is the same type as variable
                if (!checkType(varValue, var.getType())) {
                    throwVerifierException(ERROR_TYPE_VALUE_MISMATCH);
                }
                // check if not final
                if (var.isInit() && var.isFinal()) {
                    throwVerifierException(ERROR_ASSIGNMENT_TO_FINAL);
                }
                if (!var.isInit())
                    symbolTable.initVariable(varName);
            }
            else {return false;}
        }
        return true;
    }

    /**
     * only void methods are supported
     * eg: "foo(a,b); a and b must be existing variables"
     * variables without type
     * @return: true if the line is a valid method call, false otherwise
     */
    private boolean verifyCallingMethod() {
        Pattern pattern = Pattern.compile(R_METHOD_CALL);
        Matcher match = pattern.matcher(line.replaceAll(R_SPACES, R_EMPTY_STRING));
        if (match.matches()) {
            String methodName = match.group(1);
            // check if method exist in symbolTable
            if (!symbolTable.isMethodExists(methodName)) {
                throwVerifierException(ERROR_UNKNOWN_METHOD + methodName);
            }
            String methodArgs = match.group(2);
            // check if arguments match method parameters
            if (!checkMethodsCallParams(methodName, methodArgs)){
                throwVerifierException(ERROR_INVALID_METHOD_CALL_PARAMETERS);
            }
        }
        return match.matches();
    }

    /**
     * verify the parameters of a method call
     * @param methodName
     * @param methodArgs
     * @return true if the parameters match the method parameters, false otherwise
     */
    private boolean checkMethodsCallParams(String methodName, String methodArgs){
        if (methodArgs == null){
            return symbolTable.getMethodParams(methodName).size() == 0;
        }
        String[] vars = methodArgs.split(R_METHOD_PARAMS_SPLIT);
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

    /**
     * returns the TYPE of the given string
     * @param expression
     * @returns: the type of the given string
     */
    public String getExpressionType(String expression) {
        Pattern charLiteralPattern = Pattern.compile(R_CHAR_LITERAL);
        Pattern stringLiteralPattern = Pattern.compile(R_STRING_LITERAL);
        Pattern intLiteralPattern = Pattern.compile(R_INT_LITERAL);
        Pattern doubleLiteralPattern = Pattern.compile(R_DOUBLE_LITERAL);
        Pattern boolLiteralPattern = Pattern.compile(R_BOOL_LITERAL);
        Pattern varPattern = Pattern.compile(R_VARIABLE);
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
                throwVerifierException(ERROR_UNKNOWN_VARIABLE);
            } else {
                if (!var.isInit()){
                    throwVerifierException(String.format(ERROR_VARIABLE_NOT_INIT,expression));
                }
                return var.getType();
            }
        }
        else
         throwVerifierException(ERROR_INVALID_EXPRESSION);
        return null;
    }

    /**
     * verify if the string is a valid return parameter list
     * @param params - the string to verify
     * @return true if the string is a valid parameters list, false otherwise
     */
    private boolean verifyParameterList(String params) {
        String varsTypesRx = String.join(R_PIPE, variableTypes);
        String regPattern = "(\\s*(final\\s)?(" + varsTypesRx + ")\\s+([a-zA-Z]\\w*)\\s*[,]?)*|\\s*";
        Pattern pattern = Pattern.compile(regPattern);
        Matcher match = pattern.matcher(params);
        return match.matches();
    }

    /**
     * gets the paramters out of the paramters string
     * @param paramsString
     * @return ParamsContainer with the parameters
     */
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
                throwVerifierException(ERROR_NOT_UNIQUE_PARAM_NAME);
            }
            params.add(new Variable(varName, varType, varFinal != null));
        }
        return params;
    }

    /**
     * verify if the line is a valid if statement
     * @return true if the line is a valid if statement, false otherwise
     */
    private boolean verifyIf() {
        Pattern pattern = Pattern.compile(R_IF);
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

    /**
     * verify if the line is a valid while statement
     * @return true if the line is a valid while statement, false otherwise
     */
    private boolean verifyWhile() {
        Pattern pattern = Pattern.compile(R_WHILE);
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

    /**
     * this method verifies the condition of the if statement
     * the condition is strictly defined by the s-java description (Section 5.4)
     * eg: "if (false) {"
     * eg: "if (a) {"
     * eg: "if (3) {"
     * eg: "if (isFantastic && -3.25 || true) {"
     @returns: true if the condition is valid, false otherwise
     */
    private boolean verifyCondition(String TheCondition) {
        for (String condition : TheCondition.split(R_CONDITION_SPLIT, -1)) {
            condition = condition.trim();
            Pattern pattern = Pattern.compile(R_CONDITION);
            Matcher match = pattern.matcher(condition);
            if (match.find()) {
                String type = getExpressionType(condition);
                if (type.equals(INT) || type.equals(DOUBLE)) {
                    if (symbolTable.getVariable(condition) == null) {
                        throwVerifierException(ERROR_ILLEGAL_CONDITION);
                    }
                }
                if (type.equals(STRING) || type.equals(CHAR) || type.equals(STRING_LITERAL)){
                    throwVerifierException(ERROR_ILLEGAL_CONDITION);
                }
            }
            if (!match.matches()){throwVerifierException(ERROR_ILLEGAL_CONDITION);}
        }
        return true;
    }

    /**
     * verify if the line is a valid return statement
     * @return true if the line is a valid return statement, false otherwise
     */
    private boolean verifyReturn() {
        Pattern pattern = Pattern.compile(R_RETURN);
        Matcher match = pattern.matcher(line);
        return match.matches();
    }

    /**
     * Throws a VerifierException with the given message
     * @param message
     */
    private void throwVerifierException(String message) {
        throw new SJavaException(String.format(THROWER_PREFIX_TO_EXCEPTION,lineNumber,message));
    }

}
