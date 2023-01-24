package oop.main.entities;
/**
 * This class represents a method in the verifier.
 */
public class Method {
    private String name;
    private ParamsContainer parameters;

    public Method(String name, ParamsContainer parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }


    /**
     * @return the parameters
     */
    public ParamsContainer getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)",name, parameters);
    }
}
