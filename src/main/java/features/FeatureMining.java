package features;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FeatureMining {

    private Map<String, Double> historicLogFreq;
    private Map<String, Features> featureVectors;

    /**
     * Constructor
     */
    public FeatureMining() {
        historicLogFreq = new HashMap<>();
        featureVectors = new HashMap<>();
    }

    /**
     * Reads file and creates 1-6 size nGrams
     */
    public void readFile(String fname) throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(fname))) {

            for (String line; (line = br.readLine()) != null; ) { // Lines

                Features fv = new Features(); // Init frequency vector

                String[] lineVector = line.trim().split(" "); // Line as a vector of words

                for (int i = 1; i <= Math.min(lineVector.length, 6); i++) { // Clever 1 - 6 ngram size

                    for (int j = 0; j <= lineVector.length - i; j++) { // For every word in the vector

                        StringBuilder nGram = new StringBuilder();

                        for (int k = 0; k < i; k++)  // Create the ngram
                            nGram.append(lineVector[j + k]).append(" ");

                        if (historicLogFreq.containsKey(nGram.toString().trim())) // If it was previously observed
                            fv.incrementNGram(i, historicLogFreq.get(nGram.toString().trim()));

                    }

                }

                featureVectors.put(line, fv);
            }
        }

    }

    public void initHistoricLogs(String fname) throws IOException{

        try (BufferedReader br = new BufferedReader(new FileReader(fname))) {

            for (String line; (line = br.readLine()) != null; ) { // Lines
                incrementMap(line.trim(),historicLogFreq);
            }
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

    /**
     * Function to print the map
     */
    private void printMap(Map<String, Double> mp) {
        for (Map.Entry entry : mp.entrySet())
            System.out.println("nGram: " + entry.getKey() + " | Frequency: " + entry.getValue());
    }

    public Map<String, Double> getHistoricLogFreq() {
        return historicLogFreq;
    }

    public Map<String, Features> getFeatureVectors() {
        return featureVectors;
    }
}
