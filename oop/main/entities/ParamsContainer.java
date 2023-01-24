package oop.main.entities;
import java.util.LinkedHashSet;

/**
 * This class represents the parameters of a method in the verifier.
 */
public class ParamsContainer extends LinkedHashSet<Variable> {
    /**
     * check if a variable name is already in the parameters.
     * @param varName
     * @return
     */
    public boolean nameExists(String varName){
        for (Variable var : this){
            if (var.getName().equals(varName)){
                return true;
            }
        }
        return false;
    }
}