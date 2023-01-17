package main;
import main.entities.Method;
import main.entities.ParamsContainer;
import main.entities.Variable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * <variable name, variable type>
 */
class variableMap extends HashMap<String, Variable> {} // dict: <variable name> -> <Variable>
class methodMap extends HashMap<String, Method> {} // dict: <method name> -> <Method>

// TODO: adjust the symbol table to contain the information of final
public class SymbolTable {
    private final variableMap globalSymbolTable;
    private final LinkedList<variableMap> localSymbolTables;
    private final methodMap methodSymbolTable;

    public SymbolTable() {
        globalSymbolTable = new variableMap();
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
            return false;
        }
        variableMap localSymbolTable = localSymbolTables.getLast();
        return localSymbolTable.containsKey(name);
    }

    public void addGlobalVariable(String varName, String varType, boolean varFinal) {
        globalSymbolTable.put(varName,
                new Variable(varName, varType, varFinal));
    }

    public void addLocalVariable(String varName, String varType, boolean varFinal) {
        localSymbolTables.getLast().put(varName, new Variable(varName, varType, varFinal));
    }

    // TOOD: finish this method
    public void addMethod(String methodName, ParamsContainer parameters) {
        methodSymbolTable.put(methodName, new Method(methodName, parameters));
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

    public void printSymbolTable() {
        System.out.println("Global Symbol Table:");
        for (Variable variable : globalSymbolTable.values()) {
            System.out.println(variable);
        }
        System.out.println("Local Symbol Table:");
        for (variableMap localSymbolTable : localSymbolTables) {
            for (Variable variable : localSymbolTable.values()) {
                System.out.println(variable);
            }
        }
        System.out.println("Method Symbol Table:");
        for (Method method : methodSymbolTable.values()) {
            System.out.println(method);
        }
    }
}
