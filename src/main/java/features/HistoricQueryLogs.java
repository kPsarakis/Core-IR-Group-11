package features;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HistoricQueryLogs {

    private Map<String, Double> historicLogFreq; // Map containing the freq(g) of the paper's section 3.2

    private String logFileName; // The file name of the historic logs

    /**
     * Constructor the initializes the HashMap and gets the file name of the historic logs
     *
     * @param fname the file name of the historic logs
     */
    public HistoricQueryLogs(String fname) {
        historicLogFreq = new HashMap<>();
        logFileName = fname;
    }

    /**
     * Function the creates and outputs the historic log HashMap
     *
     * @return the historic log HashMap
     */
    public Map<String, Double> getHistoricLogs() {

        try (BufferedReader br = new BufferedReader(new FileReader(logFileName))) {
            for (String line; (line = br.readLine()) != null; )
                // for every line in the historic log file
                incrementMap(line.trim()); // increment the map with the line

        } catch (IOException e) {
            e.printStackTrace();
        }

        normalizeMap(); // Normalize the map

        return historicLogFreq; // return the map

    }

    /**
     * Function to increment entries of the map
     *
     * @param key the key of the HashMap that we increment its value
     */
    private void incrementMap(String key) {
        if (historicLogFreq.containsKey(key))
            // if the map contains the key increment it by one
            historicLogFreq.put(key, historicLogFreq.get(key) + 1.0);
        else
            // if the map does not contain the key set it to 1
            historicLogFreq.put(key, 1.0);
    }

    /**
     * Normalize the Map so that freq(g) resembles a probability density function
     */
    private void normalizeMap() {
        for (Map.Entry entry : historicLogFreq.entrySet())
            historicLogFreq.put((String) entry.getKey(), (double) entry.getValue() / historicLogFreq.keySet().size());
    }

}
