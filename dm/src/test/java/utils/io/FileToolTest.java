package test.java.utils.io;

import main.java.core.DataSet;
import main.java.utils.io.FileTool;
import org.junit.Test;

import java.io.File;
import java.io.IOException;


public class FileToolTest {

    @Test
    public void testLoadDataSet() {
        File file = new File("data/numeric/iris.csv");
        try {
            DataSet dataset = FileTool.loadDataSet(file, ",", true);
            System.out.println(dataset);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(FileTool.loadTicTacToe());
        System.out.println(FileTool.loadBreastCancer());
    }
}
