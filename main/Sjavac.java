package main;
import java.io.*;

public class Sjavac {
    public static void main(String[] args) {
//        String path = "D:/LIMUDIJM/שנה ג/OOP/ex6/OOP_ex6/test501.sjava";
        String path = "supplied_material/tests/test501OMRIKI.sjava";
        var file = new File(path);
        var verifier = new Verifier();
        try (BufferedReader reader =
                             new BufferedReader(new FileReader(file));){
            verifier.getMethods(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file));){
            verifier.printSymbolTable();
            verifier.verify(reader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("DONE :)");
    }
}
