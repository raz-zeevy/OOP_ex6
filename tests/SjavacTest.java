package tests;
import main.Sjavac;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class SjavacTest {

    private List<String> testNames;

    @Test
    void main() {
        String testRootPath =  "supplied_material/tests/";
        try {
            File myObj = new File("supplied_material/presubmission_sjavac_tests.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNext()){
                String[] testData = myReader.nextLine().split(" ");
                int sol = Integer.parseInt(testData[1]);
                int res = Sjavac.test(testRootPath+testData[0]);
                try {
                    assertEquals(sol, res);
                }
                catch (AssertionError  e){
                    System.out.println("test: "+testData[0]+"failed.");
                    System.out.println("name: "+testData[2]);
                    System.out.println("expected: "+sol+"\ngot: "+res);
                    throw (e);
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}