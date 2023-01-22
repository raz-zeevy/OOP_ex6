package main.entities;

public class GlobalVariable extends Variable{

    private boolean globalInit;

    public GlobalVariable(String name, String type, boolean varFinal) {
        super(name, type, varFinal);
        this.globalInit = false;
    }

    public void resetInit() {
        super.init = globalInit;
    }

    public void setGlobalInit(boolean init) {
        globalInit = init;
    }
}
