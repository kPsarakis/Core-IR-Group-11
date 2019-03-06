package features;

import java.io.IOException;

public class TestFeatureMining {

    public static void main(String[] args) throws IOException {

        FeatureMining fm = new FeatureMining();

        //fm.readFile("src/main/java/data/dummy.txt");
        //fm.readFile("src/main/java/data/validation-queries.txt");
        fm.initHistoricLogs("src/main/java/data/validation-queries.txt");
        fm.readFile("src/main/java/data/dummy.txt");
        fm.getFeatureVectors().get("bank of america").printNGrams();

    }


}
