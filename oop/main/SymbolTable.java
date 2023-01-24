package oop.main;
import oop.main.entities.GlobalVariable;
import oop.main.entities.Method;
import oop.main.entities.ParamsContainer;
import oop.main.entities.Variable;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * <variable name, Variable>
 */
class variableMap extends HashMap<String, Variable> {} // dict: <variable name> -> <Variable>

/**
 * <variable name, GlobalVariable>
 */
class globalVariableMap extends HashMap<String, GlobalVariable> {} // dict: <variable name> -> <Variable>

/**
 * <method name, Method>
 */
class methodMap extends HashMap<String, Method> {} // dict: <method name> -> <Method>

/**
 * This class represents the symbol table of the verifier.
 */
public class SymbolTable {
    public static final String ERROR_INIT_VAR_BEFORE_ADDITION = "BAD CODE USAGE: first check if variable exists before initializing it";
    private final globalVariableMap globalSymbolTable;
    private final LinkedList<variableMap> localSymbolTables;
    private final methodMap methodSymbolTable;

    /**
     * Constructor.
     */
    public SymbolTable() {
        globalSymbolTable = new globalVariableMap();
        localSymbolTables = new LinkedList<>();
        methodSymbolTable = new methodMap();
    }

    /**
     * return null if variable is not in symbol table
     * @param name
     * @return
     */
    public Variable getVariable(String name) {
        for (variableMap localSymbolTable : localSymbolTables) {
            if (localSymbolTable.containsKey(name)) {
                return localSymbolTable.get(name);
            }
        }
        if (globalSymbolTable.containsKey(name)) {
            return globalSymbolTable.get(name);
        }
        return null;
    }

    /**
     * check if variable is in the local scope
     * @param name
     * @return
     */
    public boolean inLocalScope(String name) {
        if (localSymbolTables.isEmpty()) {
            return getVariable(name) != null;
        }
        variableMap localSymbolTable = localSymbolTables.getLast();
        return localSymbolTable.containsKey(name);
    }

    /**
     * adds a variable to the symbol table
     * @param varName
     * @param varType
     * @param varFinal
     */
    public void addVariable(String varName, String varType, boolean varFinal) {
        if (localSymbolTables.size() != 0)
            localSymbolTables.getLast().put(varName, new Variable(varName, varType, varFinal));
        else
            globalSymbolTable.put(varName, new GlobalVariable(varName, varType, varFinal));
    }

    /**
     * adds a Variable to the symbol table
     * @param var
     */
    public void addVariable(Variable var) {
        addVariable(var.getName(), var.getType(), var.isFinal());
    }

    /**
     * adds a method to the symbol table
     * @param methodName
     * @param parameters
     */
    public void addMethod(String methodName, ParamsContainer parameters) {
        methodSymbolTable.put(methodName, new Method(methodName, parameters));
    }

    /**
     * checks if a method is in the symbol table
     * @param methodName the name of the method
     * @return true if the method is in the symbol table, false otherwise
     */
    public boolean isMethodExists(String methodName) {
        return methodSymbolTable.containsKey(methodName);
    }

    /**
     * gets the parameters of a method.
     * @param methodName
     * @return the parameters of the method
     */
    public ParamsContainer getMethodParams(String methodName) {
        return methodSymbolTable.get(methodName).getParameters();
    }

    /**
     * when a new block is opened, a new local symbol table is created
     * adds a new local symbol table to the symbol table
     */
    public void addLocalSymbolTable() {
        localSymbolTables.add(new variableMap());
    }

    /**
     * when a block is closed, the last local symbol table is removed
     */
    public void removeLocalSymbolTable() {
        localSymbolTables.removeLast();
    }

    /**
     * inits a variable in the symbol table
     * @param varName
     */
    public void initVariable(String varName) {
        Variable var = getVariable(varName);
        if (var != null) {
            var.init();
        } else {
            throw new RuntimeException(ERROR_INIT_VAR_BEFORE_ADDITION);
        }
    }

    /**
     * adds a method to the symbol table
     * @param methodName
     */
    public void addMethodLocals(String methodName) {
        for (Variable var : getMethodParams(methodName)){
            addVariable(var);
            initVariable(var.getName());
        }
    }

    /**
     * resets all the global variables in the symbol table
     * to their initial state.
     */
    public void resetGlobalInit() {
        for (GlobalVariable variable : globalSymbolTable.values()) {
            variable.resetInit();
        }
    }

    /**
     * sets the global init of a variable to their final state.
     */
    public void setGlobalInit(){
        for (GlobalVariable var : globalSymbolTable.values()){
            var.setGlobalInit(var.isInit());
        }
    }
}
