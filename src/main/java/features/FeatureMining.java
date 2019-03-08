package features;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class FeatureMining {

    private Map<String, Double> historicLogFreq; // Map containing the freq(g) of the paper's section 3.2

    private LinkedHashMap<String, Features> featureVectors; // LinkedHashMap containing the features in the same order as the originating file

    /**
     * Constructor that instantiates the HashMaps
     *
     * @param fname location of the historic logs
     */
    public FeatureMining(String fname) {
        historicLogFreq = new HistoricQueryLogs(fname).getHistoricLogs();
        featureVectors = new LinkedHashMap<>();
    }

    /**
     * Reads file and creates the feature vectors
     *
     * @param fname Name of the file that we extract the features
     * @throws IOException
     */
    public void readFile(String fname) throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(fname))) {
            for (String line; (line = br.readLine()) != null; ) {
                // For every line in the file

                String[] s = line.split("\t"); // Split to get the query and the relevance judgment

                String[] query = s[0].trim().split("\\|"); // Split the query to get prefix, suffix and full candidate

                if (query.length != 1) {
                    //check for queries without suffix

                    String prefix = query[0];  // get prefix
                    String suffix = query[1];  // get suffix
                    String candidate = prefix + suffix; // create the full candidate

                    Features fv = new Features(); // init frequency vector

                    if (s[1].equals("1"))
                        // if relevance judgment equals 1 make the query relevant
                        fv.makeItRelevant();

                    setNGrams(candidate, fv); // set the n-gram features
                    setOtherFrequency(candidate, fv); // set frequency (Other features)
                    setLengthBasedFeatures(prefix, suffix, candidate, fv); // set the length based features (Other features)
                    setSpaceFeature(prefix, fv); // set the whitespace feature (Other features)

                    featureVectors.put(prefix+"|"+candidate, fv); // put the new feature vector in the LinkedHashMap with a unique hey

                }
            }
        }

    }

    /**
     * Function that generates the N-gram based features as specified in the 3.2 section
     *
     * @param candidate the suggested candidate that we generate the n-grams from
     * @param f the feature vector that will be modified
     */
    private void setNGrams(String candidate, Features f){

        String[] lineVector = candidate.trim().split(" "); // Line as a vector of words

        for (int i = 1; i <= Math.min(lineVector.length, 6); i++) {
            // Clever 1 - 6 ngram size iteration that does out of bounds check in the loop statement

            for (int j = 0; j <= lineVector.length - i; j++) {
                // For every word in the vector

                StringBuilder nGram = new StringBuilder(); // the n-gram of the current timestep

                for (int k = 0; k < i; k++)
                    // Create the ngram
                    nGram.append(lineVector[j + k]).append(" ");

                if (historicLogFreq.containsKey(nGram.toString().trim()))
                    // If it was previously observed
                    f.incrementNGram(i-1, historicLogFreq.get(nGram.toString().trim())); // increase its n-gram freq

            }
        }
    }

    /**
     * Function that creates the frequency of the query in historical logs feature (Other features)
     *
     * @param candidate the query candidate
     * @param f the feature vector that will be modified
     */
    private void setOtherFrequency(String candidate, Features f){
        if(historicLogFreq.containsKey(candidate))
            // Other features frequency of the candidate query in the historical logs
            f.setOtherFrequency(historicLogFreq.get(candidate));
    }

    /**
     * Function that creates the length based features (Other features)
     *
     * @param prefix the query prefix
     * @param suffix the query suffix
     * @param candidate the query candidate
     * @param f the feature vector that will be modified
     */
    private void setLengthBasedFeatures(String prefix, String suffix, String candidate, Features f){

        f.setPrefCLength(prefix.length()); // set prefix character length
        f.setFullCLength(candidate.length()); // set full suggestion character length
        f.setSuffCLength(suffix.length()); // set suffix character length

        f.setPrefWLength(prefix.trim().split(" ").length); // set prefix word length
        f.setFullWLength(candidate.trim().split(" ").length); // set full suggestion word length
        f.setSuffWLength(suffix.trim().split(" ").length);  // set suffix word length

    }

    /**
     * If the prefix ends with whitespace set the feature to 1 else to 0
     *
     * @param prefix the query prefix
     * @param f the feature vector that will be modified
     */
    private void setSpaceFeature(String prefix, Features f){
        if(prefix.charAt(prefix.length()-1) == ' ')
            // If the last character is the whitespace character
            f.setSpace((short)1);
        else
            f.setSpace((short)0);
    }

    /**
     * Write the feature vectors to disk in svmlight format, ready for the LambdaMART step
     */
    public void writeFeatureVectors(String fname) throws FileNotFoundException {

        try (PrintWriter out = new PrintWriter(fname)) {
            for (Map.Entry <String,Features> entry : featureVectors.entrySet()) {
                // for every entry in the feature map write the feature vector in a separate line on disk

                String[] s = entry.getKey().split("\\|"); // The full query candidate splits in prefix and suffix

                Features fv = entry.getValue(); // The feature vector

                out.println(fv.getRelevanceJudgment()+" prefix:"+s[0].replace(" ","-")+
                        " "+ fv.getLambdaFeatures()+ " #Candidate: "+s[1]); // write the feature vectors in a svmlight string format

            }
        }
    }

}
