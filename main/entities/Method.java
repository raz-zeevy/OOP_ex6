package main.entities;

import java.util.HashSet;

public class Method {
    private String name;
    private ParamsContainer parameters;

    public Method(String name, ParamsContainer parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }


    public ParamsContainer getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)",name, parameters);
    }
}
