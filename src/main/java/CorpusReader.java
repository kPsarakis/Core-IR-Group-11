import java.util.ArrayList;
import java.util.List;

/**
 * Reads the queries dataset(s)
 */
public class CorpusReader {

    private List<String> queries;

    public CorpusReader() {
        queries = new ArrayList<>();

        //TODO: Remove this once we read the actual data
        queries.add("bank of america");
        queries.add("national bank of china");
        queries.add("weather in america");
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
