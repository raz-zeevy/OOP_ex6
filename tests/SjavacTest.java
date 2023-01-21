package tests;

import main.Sjavac;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class SjavacTest {

    private List<String> testNames;

    void getTestNames(){
        testNames = new ArrayList<String>();
        String testRootPath =  "supplied_material/tests";
        File[] files = new File(testRootPath).listFiles();
        //If this pathname does not denote a directory, then listFiles() returns null.

        for (File file : files) {
            if (file.isFile()) {
                testNames.add(testRootPath+"/"+file.getName());
            }
        }
    }

    @Test
    void main() {
        getTestNames();
        try {
            File myObj = new File("supplied_material/presubmission_sjavac_tests.txt");
            Scanner myReader = new Scanner(myObj);
            for (String testPath : testNames){
                String data = myReader.nextLine();
                int sol = Integer.parseInt(data.split(" ")[1]);
                int res = Sjavac.test(testPath);
                assertEquals(sol, res);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}