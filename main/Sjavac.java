package main;
import java.io.*;

public class Sjavac {
    public static void main(String[] args) {
        String path = "supplied_material/tests/test501.sjava";
        var file = new File(path);
        try (BufferedReader reader =
                             new BufferedReader(new FileReader(file));){
            var verifier = new Verifier(reader);
            verifier.verify();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
