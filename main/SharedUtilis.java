package main;

public class SharedUtilis {

    /** Keywords Types **/
    static final String NUM_CONSTANT = "NumConstant";
    static final String STRING_CONSTANT = "stringConstant";

    /**  types  **/
    public static final String INT = "int";
    public static final String BOOLEAN = "boolean";
    public static final String DOUBLE = "double";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String STRING = "String";
    public static final String CHAR =  "char";

    /**  compiling constants  **/
    enum Types { BOOLEAN, DOUBLE, INT, NUM_CONSTANT, STRING, CHAR }
    public static final String[] variableTypes = {INT, DOUBLE, STRING, BOOLEAN, CHAR};

    public static final String[] keywords = {"void", "final"," if", "while", TRUE, FALSE
            , "return"};
    public static final String[] endLine = {";","{","}"};

    /**   If and While conditions   **/
    public static final String[] conditionables = {TRUE, FALSE};
    public static final String[] conditionableTypes = {BOOLEAN, DOUBLE, INT, NUM_CONSTANT};
    public static final String[] concditionOperators = {"||", "&&"};
    public static final int MAX_RECURSIVE_CONDITIONS = java.lang.Integer.MAX_VALUE;

}
