package features;

import java.io.*;
import java.util.*;

public class FeatureMining {

    private Map<String, Double> historicLogFreq;
    private LinkedHashMap<String, Features> featureVectors;

    /**
     * Constructor
     */
    public FeatureMining() {
        historicLogFreq = new HashMap<>();
        featureVectors = new LinkedHashMap<>();
    }
    /**
     * Reads file and creates the feature vectors
     */
    public void readFile(String fname) throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(fname))) {

            for (String line; (line = br.readLine()) != null; ) { // Lines

                String[] s = line.split("\t");

                String[] query = s[0].trim().split("\\|");


                if (query.length != 1) {
                    //check for single word

                    String prefix = query[0];
                    String suffix = query[1];
                    String candidate = prefix + suffix;

                    Features fv = new Features(); // Init frequency vector

                    if (s[1].equals("1"))
                        fv.makeItRelevant();

                    setNGrams(candidate, fv);
                    setOtherFrequency(candidate, fv);
                    setLengthBasedFeatures(prefix, suffix, candidate, fv);
                    setSpaceFeature(prefix, fv);

                    featureVectors.put(prefix+"|"+candidate, fv);  //TODO: What is the key?

                }
            }
        }

    }

    private void setSpaceFeature(String prefix, Features f){

        if(prefix.charAt(prefix.length()-1) == ' ')
            f.setSpace((short)1);
        else
            f.setSpace((short)0);
    }

    private void setLengthBasedFeatures(String prefix, String suffix, String candidate, Features f){

        f.setPrefCLength(prefix.length());
        f.setFullCLength(candidate.length());
        f.setSuffCLength(suffix.length());

        f.setPrefWLength(prefix.trim().split(" ").length);
        f.setFullWLength(candidate.trim().split(" ").length);
        f.setSuffWLength(suffix.trim().split(" ").length);

    }

    private void setNGrams(String candidate, Features f){

        String[] lineVector = candidate.trim().split(" "); // Line as a vector of words

        for (int i = 1; i <= Math.min(lineVector.length, 6); i++) { // Clever 1 - 6 ngram size

            for (int j = 0; j <= lineVector.length - i; j++) { // For every word in the vector

                StringBuilder nGram = new StringBuilder();

                for (int k = 0; k < i; k++)  // Create the ngram
                    nGram.append(lineVector[j + k]).append(" ");

                if (historicLogFreq.containsKey(nGram.toString().trim()))  // If it was previously observed
                    f.incrementNGram(i-1, historicLogFreq.get(nGram.toString().trim()));
            }

        }
    }

    private void setOtherFrequency(String candidate, Features f){
        if(historicLogFreq.containsKey(candidate)) // Other features frequency of the candidate queryin the historical logs
            f.setOtherFrequency(historicLogFreq.get(candidate));
    }

    /**
     * Write the feature vectors to disk ready for the lambdaMART step
     */
    public void writeFeatureVectors(String fname) throws FileNotFoundException {

        try (PrintWriter out = new PrintWriter(fname)) {

            for (Map.Entry <String,Features> entry : featureVectors.entrySet()) {

                String[] s = entry.getKey().split("\\|");
                Features fv = entry.getValue();
                out.println(fv.getRelevanceJudgment()+" prefix:"+s[0].replace(" ","-")+" "+ fv.getLambdaFeatures()+ " #Candidate: "+s[1]);

            }
        }
    }

    public void initHistoricLogs(String fname) throws IOException{

        try (BufferedReader br = new BufferedReader(new FileReader(fname))) {

            for (String line; (line = br.readLine()) != null; ) // Lines
                incrementMap(line.trim(),historicLogFreq);

        }

        normalizeMap(historicLogFreq);

    }

    /**
     * Function to increment entries of the map
     */
    private void incrementMap(String key, Map<String, Double> mp) {
        if (mp.containsKey(key))
            mp.put(key, mp.get(key) + 1.0);
        else
            mp.put(key, 1.0);
    }

    /**
     * Normalize the Map so the frequencies resemble a pdf
     */
    private void normalizeMap(Map<String, Double> mp) {
        for (Map.Entry entry : mp.entrySet())
            mp.put((String) entry.getKey(), (double) entry.getValue() / mp.keySet().size());
    }

}
