package main;
import java.io.*;

public class Sjavac {

    public static void main(String[] args) {
        String path = args[0];
        var file = new File(path);
        var verifier = new Verifier();
        try (BufferedReader reader =
                             new BufferedReader(new FileReader(file));){
            verifier.firstRead(reader);
//            verifier.printSymbolTable();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (SJavaException e){
            System.out.println(1);
            System.err.println(e.getMessage());
            throw(e);
        }
        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file));){
            verifier.verify(reader);
            System.out.println(0);
        } catch (IOException e) {
            System.out.println(2);
            System.err.println("BAD FILE OR SOMETHING");
        }
        catch (SJavaException e){
            System.out.println(1);
            System.err.println(e.getMessage());
            throw(e);
        }
    }

    public static int test(String path){
        var file = new File(path);
        var verifier = new Verifier();
        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file));){
            verifier.firstRead(reader);
        } catch (IOException e) {
            return 2;
        }
        catch (SJavaException e){
//            System.out.println(1);
//            System.err.println(e.getMessage());
            return 1;
        }
        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file));){
            verifier.verify(reader);
//            System.out.println(0);
            return 0;

        } catch (IOException e) {
//            System.out.println(2);
//            System.err.println("BAD FILE OR SOMETHING");
            return 2;
        }
        catch (SJavaException e){
//            System.out.println(1);
//            System.err.println(e.getMessage());
            return 1;
        }
    }

}
