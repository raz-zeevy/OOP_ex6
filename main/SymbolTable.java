package main;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * <variable name, variable type>
 */
class variableMap extends HashMap<String, String> {} // dict: <variable name> -> <variable type>

public class SymbolTable {
    private final variableMap globalSymbolTable;
    private final LinkedList<variableMap> localSymbolTables;

    public SymbolTable() {
        globalSymbolTable = new variableMap();
        localSymbolTables = new LinkedList<>();
    }

    /**
     * return null if variable is not in symbol table
     * @param name
     * @return
     */
    public Variable getVariable(String name) {
        for (variableMap localSymbolTable : localSymbolTables) {
            if (localSymbolTable.containsKey(name)) {
                return new Variable(name, localSymbolTable.get(name));
            }
        }
        if (globalSymbolTable.containsKey(name)) {
            return new Variable(name, globalSymbolTable.get(name));
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

    public void addGlobalVariable(String variableName, String variableType) {
        globalSymbolTable.put(variableName, variableType);
    }

    public void addLocalVariable(String variableName, String variableType) {
        localSymbolTables.getLast().put(variableName, variableType);
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

}
