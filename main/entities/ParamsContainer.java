package main.entities;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class ParamsContainer extends LinkedHashSet<Variable> {
    public boolean nameExists(String varName){
        for (Variable var : this){
            if (var.getName().equals(varName)){
                return true;
            }
        }
        return false;
    }
}