package main.entities;

public class Variable {
    private boolean init;
    private String name;
    private String type;
    private final boolean varFinal;

    public Variable(String name, String type, boolean varFinal) {
        this.name = name;
        this.type = type;
        this.varFinal = varFinal;
        this.init = false;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isFinal() {
        return varFinal;
    }

    @Override
    public String toString() {
        return String.format("%s%s %s", varFinal ? "final " : "", type, name);
    }

    public void init() {
        init = true;
    }
    public boolean isInit() {
        return init;
    }
}
