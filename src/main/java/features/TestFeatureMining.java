package features;

import java.io.IOException;

public class TestFeatureMining {

    /**
     * Demo that shows how we do the feature mining and output them ready for the LambdaMART step
     *
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        FeatureMining fm = new FeatureMining("src/main/java/data/history.txt"); // initiate the feature mining class with the location of the historic logs
        System.out.println("Historic logs initialized");
        fm.readFile("src/main/java/data/vali.txt"); // read the file that we want to mine the features from
        System.out.println("Mining Completed proceeding to witting");
        fm.writeFeatureVectors("LambdaMART/data_lambdaMART/vali.txt"); // output the features to an svmlight file readay for the LambdaMART step
    }

}
