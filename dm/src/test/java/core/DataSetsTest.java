package test.java.core;

import main.java.core.DataSet;
import main.java.core.DataSets;
import main.java.utils.io.FileTool;
import org.junit.Test;

import java.util.Arrays;

public class DataSetsTest {

    @Test
    public void testFolds() {
        DataSet iris = FileTool.loadIris();
        DataSet[] folds = DataSets.folds(iris, 5);
        DataSets.print(folds);
    }

    @Test
    public void testVar() {
        DataSet iris = FileTool.loadIris();
        System.out.println(Arrays.toString(DataSets.var(iris)));
    }

}
