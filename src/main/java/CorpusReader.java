import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Reads the background queries dataset
 */
public class CorpusReader {

    private List<String> queries;
    private final String FILEPATH = "src/main/java/data/background-queries.txt";

    public CorpusReader() {
        queries = new ArrayList<>();

        try {
            Stream<String> lines = Files.lines(Paths.get(FILEPATH));
            queries = lines.collect(Collectors.toList());
            lines.close();
        } catch(IOException io) {
            io.printStackTrace();
        }
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
