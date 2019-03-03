import java.util.ArrayList;
import java.util.List;

/**
 * Reads the queries dataset and applies the pre-processing
 * steps
 */
public class CorpusReader {

    //TODO: The list type will need to be changed (based on the data that we read)
    private List<String> queries;

    public CorpusReader() {
        queries = new ArrayList<>();

        //TODO: Remove this once we read the actual data
        //TODO: Query pre-processing is needed (stopwords the least)
        queries.add("bank of america");
        queries.add("weather in america");
        queries.add("america");
        queries.add("bank of");
        queries.add("ing bank");
        queries.add("of");
    }

    public List<String> getQueries() {
        return queries;
    }

    public void setQueries(List<String> queries) {
        this.queries = queries;
    }

    @Override
    public String toString() {
        return "CorpusReader{" +
                "queries=" + queries +
                '}';
    }
}
