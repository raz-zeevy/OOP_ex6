package oop.main.entities;

/**
 * This class represents a variable in the verifier.
 */
public class Variable {
    protected boolean init;
    private String name;
    private String type;
    private final boolean varFinal;

    /**
     * Constructor.
     * @param name
     * @param type
     * @param varFinal
     */
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

    /**
     * @return true if the variable is final, false otherwise.
     */
    public boolean isFinal() {
        return varFinal;
    }

    @Override
    public String toString() {
        return String.format("%s%s %s", varFinal ? "final " : "", type, name);
    }

    /**
     * inits the variable
     */
    public void init() {
        init = true;
    }

    /**
     * @return true if the variable was initialized, false otherwise.
     */
    public boolean isInit() {
        return init;
    }


}
