package main.entities;

import java.util.HashSet;

public class Method {
    private String name;
    private HashSet<Variable> parameters;

    public Method(String name, HashSet<Variable> parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }


    public HashSet<Variable> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)",name, parameters);
    }
}
