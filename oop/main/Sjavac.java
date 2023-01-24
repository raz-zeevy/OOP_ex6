package oop.main;
import java.io.*;

/**
 * The oop.main class of the verifier.
 */
public class Sjavac {

    public static final int FAILURE = 1;
    public static final int SUCCESS = 0;
    public static final int IO_ERROR = 2;
    public static final String IO_ERROR_MSG = "IO ERROR";

    /**
     * The oop.main method of the verifier.
     * @param args the path of the file
     */
    public static void main(String[] args) {
        String path = args[0];
        var file = new File(path);
        var verifier = new Verifier();
        try (BufferedReader reader =
                             new BufferedReader(new FileReader(file));){
            verifier.firstRead(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (SJavaException e){
            System.out.println(FAILURE);
            System.err.println(e.getMessage());
            throw(e);
        }
        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file));){
            verifier.verify(reader);
            System.out.println(SUCCESS);
        } catch (IOException e) {
            System.out.println(IO_ERROR);
            System.err.println(IO_ERROR_MSG);
        }
        catch (SJavaException e){
            System.out.println(Sjavac.FAILURE);
            System.err.println(e.getMessage());
            throw(e);
        }
    }

    /**
     * this method is used in the tests to check the output of the verifier.
     * @param path
     * @return {0,1,2}
     */
    public static int test(String path){
        var file = new File(path);
        var verifier = new Verifier();
        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file));){
            verifier.firstRead(reader);
        } catch (IOException e) {
            return IO_ERROR;
        }
        catch (SJavaException e){
            return FAILURE;
        }
        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file));){
            verifier.verify(reader);
            return SUCCESS;

        } catch (IOException e) {
            return IO_ERROR;
        }
        catch (SJavaException e){
            return FAILURE;
        }
    }

}
