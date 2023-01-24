package oop.main.entities;

/**
 * This class represents a global variable in the verifier.
 * inherits from Variable
 */
public class GlobalVariable extends Variable{

    /**
     * true if the variable was initialized, false otherwise.
     */
    private boolean globalInit;

    /**
     * Constructor.
     * @param name
     * @param type
     */
    public GlobalVariable(String name, String type, boolean varFinal) {
        super(name, type, varFinal);
        this.globalInit = false;
    }

    /**
     * reset the global variable to its initial state
     */
    public void resetInit() {
        super.init = globalInit;
    }

    /**
     * set the global variable to be initialized
     */
    public void setGlobalInit(boolean init) {
        globalInit = init;
    }
}
