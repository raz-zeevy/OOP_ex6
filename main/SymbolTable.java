package main;
import main.entities.GlobalVariable;
import main.entities.Method;
import main.entities.ParamsContainer;
import main.entities.Variable;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * <variable name, variable type>
 */
class variableMap extends HashMap<String, Variable> {} // dict: <variable name> -> <Variable>
class globalVariableMap extends HashMap<String, GlobalVariable> {} // dict: <variable name> -> <Variable>
class methodMap extends HashMap<String, Method> {} // dict: <method name> -> <Method>

// TODO: adjust the symbol table to contain the information of final
public class SymbolTable {
    private final globalVariableMap globalSymbolTable;
    private final LinkedList<variableMap> localSymbolTables;
    private final methodMap methodSymbolTable;

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
     *
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

    public void addVariable(String varName, String varType, boolean varFinal) {
        if (localSymbolTables.size() != 0)
            localSymbolTables.getLast().put(varName, new Variable(varName, varType, varFinal));
        else
            globalSymbolTable.put(varName, new GlobalVariable(varName, varType, varFinal));
    }
    public void addVariable(Variable var) {
        addVariable(var.getName(), var.getType(), var.isFinal());
    }

    public void addMethod(String methodName, ParamsContainer parameters) {
        methodSymbolTable.put(methodName, new Method(methodName, parameters));
    }

    public boolean isMethodExists(String methodName) {
        return methodSymbolTable.containsKey(methodName);
    }

    public ParamsContainer getMethodParams(String methodName) {
        return methodSymbolTable.get(methodName).getParameters();
    }

    /**
     * when a new block is opened, a new local symbol table is created
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

    public void initVariable(String varName) {
        Variable var = getVariable(varName);
        if (var != null) {
            var.init();
        } else {
            throw new RuntimeException("BAD CODE USAGE: first check if variable exists before initializing " +
                    "it");
        }
    }

//    public void printSymbolTable() {
//        System.out.println("Global Symbol Table:");
//        for (Variable variable : globalSymbolTable.values()) {
//            System.out.println(variable);
//        }
//        System.out.println("Local Symbol Table:");
//        for (variableMap localSymbolTable : localSymbolTables) {
//            for (Variable variable : localSymbolTable.values()) {
//                System.out.println(variable);
//            }
//        }
//        System.out.println("Method Symbol Table:");
//        for (Method method : methodSymbolTable.values()) {
//            System.out.println(method);
//        }
//    }

    public void addMethodLocals(String methodName) {
        for (Variable var : getMethodParams(methodName)){
            addVariable(var);
            initVariable(var.getName());
        }
    }

    public void resetGlobalInit() {
        for (GlobalVariable variable : globalSymbolTable.values()) {
            variable.resetInit();
        }
    }
    public void setGlobalInit(){
        for (GlobalVariable var : globalSymbolTable.values()){
            var.setGlobalInit(var.isInit());
        }
    }
}
