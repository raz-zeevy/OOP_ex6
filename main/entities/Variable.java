package main.entities;

public class Variable {
    private String name;
    private String type;
    private final boolean varFinal;

    public Variable(String name, String type, boolean varfinal) {
        this.name = name;
        this.type = type;
        this.varFinal = varfinal;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isVarFinal() {
        return varFinal;
    }

    @Override
    public String toString() {
        return String.format("%s%s %s", varFinal ? "final " : "", type, name);
    }
}
