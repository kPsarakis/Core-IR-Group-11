import java.util.*;
import java.util.stream.Collectors;

/**
 * Reads the queries dataset and applies the pre-processing
 * steps
 */
public class CorpusReader {

    //TODO: The list type will need to be changed (based on the data that we read)
    private List<String> queries;
    private Map<String, Integer> queriesCount = new HashMap<>();

    public CorpusReader() {
        queries = new ArrayList<>();

        //TODO: Remove this once we read the actual data
        //TODO: Query pre-processing is needed (stopwords the least)
        queries.add("bank of america");
        queries.add("weather in america");
        queries.add("america");
        queries.add("bank of");
        queries.add("bank of");
        queries.add("bank of");
        queries.add("bank of");
        queries.add("ing bank");
        queries.add("of");
        queries.add("of");

        queries.forEach(this::updateQueriesCountMap);

        queriesCount = queriesCount.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public List<String> getQueries() {
        return queries;
    }

    public void setQueries(List<String> queries) {
        this.queries = queries;
    }

    public Map<String, Integer> getQueriesCount() {
        return queriesCount;
    }

    public void setQueriesCount(Map<String, Integer> queriesCount) {
        this.queriesCount = queriesCount;
    }

    private void updateQueriesCountMap(String query) {
        Integer count = queriesCount.get(query);
        if (count == null) {
            queriesCount.put(query, 1);
        } else {
            queriesCount.put(query, ++count);
        }
    }

    @Override
    public String toString() {
        return "CorpusReader{" +
                "queries=" + queries +
                '}';
    }
}
