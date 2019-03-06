package features;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FeatureMining {

    // Map containing the nGram probabilities
    private Map<String, Double> nGramFreq;

    /**
     * Constructor
     */
    public FeatureMining() {
        nGramFreq = new HashMap<>();
    }

    /**
     * Reads file and creates 1-6 size nGrams
     */
    public void readFile(String fname) throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(fname))) {

            for (String line; (line = br.readLine()) != null; ) { // Lines

                String[] lineVector = line.trim().split(" ");

                for (int i = 1; i <= Math.min(lineVector.length, 6); i++) { // clever 1 - 6 ngram size

                    for (int j = 0; j <= lineVector.length - i; j++) {

                        StringBuilder nGram = new StringBuilder();

                        for (int k = 0; k < i; k++) {
                            nGram.append(lineVector[j + k]).append(" ");
                        }

                        incrementMap(nGram.toString().trim());
                    }

                }

            }
        }

        normalizeMap();
        printMap();

    }

    /**
     * Function to increment entries of the map
     */
    private void incrementMap(String key) {
        if (nGramFreq.containsKey(key)) {
            nGramFreq.put(key, nGramFreq.get(key) + 1.0);
        } else {
            nGramFreq.put(key, 1.0);
        }
    }

    /**
     * Normalize the Map so the frequencies resemble a pdf
     */
    private void normalizeMap() {
        for (Map.Entry entry : nGramFreq.entrySet())
            nGramFreq.put((String) entry.getKey(), (double) entry.getValue() / nGramFreq.keySet().size());
    }

    /**
     * Function to print the map
     */
    private void printMap() {
        for (Map.Entry entry : nGramFreq.entrySet()) {
            System.out.println("nGram: " + entry.getKey() + " | Frequency: " + entry.getValue());
        }
    }


}
