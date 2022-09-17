package test.java.core;

import main.java.core.DataSet;
import main.java.core.DataSets;
import main.java.utils.io.FileTool;
import org.junit.Test;

public class DataSetsTest {

    @Test
    public void testFolds() {
        DataSet iris = FileTool.loadIris();
        DataSet[] folds = DataSets.folds(iris, 5);
        DataSets.print(folds);
    }

}
