import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Core {

    public static void main(String[] args) {

        final CorpusReader reader = new CorpusReader();     // initialize the reader
        final List<String> queries = reader.getQueries();   // get the read queries

        final CorpusProcessor processor = new CorpusProcessor(queries); // Initialize the processor
	    processor.buildQueriesNgrams(queries);  // build n-grams for each query

	    // Read user input (Simulate partial query typing)
	    Scanner scanner = new Scanner(System.in);
	    String filepath = "";
	    String pattern = "yyyyMMddHHmm-";
	    DateFormat df = new SimpleDateFormat(pattern);

        while(true) {
		    System.out.print("File to generate candidates for (1:train, 2:valid, 3:test): ");
	        int chosenFile = Integer.parseInt(scanner.nextLine());
		    System.out.print("Choose scenario (1, 2 or 3): ");
		    int scenario = Integer.parseInt(scanner.nextLine());

		    switch (chosenFile) {
	            case 1:
		            filepath = "src/main/java/data/training-queries.txt";
		            break;
		        case 2:
		            filepath = "src/main/java/data/validation-queries.txt";
			        break;
		        case 3:
			        filepath = "src/main/java/data/test-queries.txt";
			        break;
	        }
	        List<String> userInputQueries;

		    try {
			    String now = df.format(Calendar.getInstance().getTime());

			    BufferedWriter writer = new BufferedWriter(new FileWriter(now + "results_" + filepath.substring(filepath.lastIndexOf("/") + 1)));

			    Stream<String> lines = Files.lines(Paths.get(filepath));
			    userInputQueries = lines.collect(Collectors.toList());
			    lines.close();

			    int processedCount = 0;
			    double meanRR = 0;

			    // iterate over the queries of the read file
			    for (String userInputQuery : userInputQueries) {
				    processedCount ++;

					String[] userInputQuerySplit = userInputQuery.split(" ", 2);
				    String currentQuery = userInputQuerySplit[0]; // the first work as a whole

				    List<String> mergedCandidates = getCandidatesForScenario(processor, scenario, currentQuery);

					double rr = calculateReciprocalRank(userInputQuery, mergedCandidates);
					meanRR += rr;

				    Map<String, Integer> aggregatedCandidates = aggregateCandidates(mergedCandidates);
				    writeCandidatesInFile(writer, userInputQuery, aggregatedCandidates);

				    if (userInputQuerySplit.length > 1) {
				        // not a single word case
					    String restOfQuery = " " + userInputQuerySplit[1];

					    for (int i = 0; i < restOfQuery.length(); i++) {
						    processedCount ++;

						    char currentChar = restOfQuery.charAt(i);
						    currentQuery = currentQuery + currentChar;

						    mergedCandidates = getCandidatesForScenario(processor, scenario, currentQuery);
						    rr = calculateReciprocalRank(userInputQuery, mergedCandidates);
						    meanRR += rr;

						    aggregatedCandidates = aggregateCandidates(mergedCandidates);
						    writeCandidatesInFile(writer, userInputQuery, aggregatedCandidates);
					    }
				    }
			    }
			    meanRR = meanRR / processedCount;
			    System.out.println("Mean Reciprocal Rank = " + meanRR);

		    } catch(IOException io) {
			    io.printStackTrace();
		    }
        }
    }

	/**
	 * Calculates the reciprocal rank of the given {@code query}
	 * based on the {@code candidates} list.
	 *
	 * @param query the query for which the reciprocal rank is calculated
	 * @param candidates the candidates list
	 * @return the calculated reciprocal rank
	 */
	private static double calculateReciprocalRank(String query, List<String> candidates) {
    	double reciprocalRank = 0;
    	for (int i = 0; i < candidates.size(); i++) {
		    String candidate = candidates.get(i).replace("|", "");
		    if (query.equals(candidate)) {
    			reciprocalRank = 1.0/ (i + 1);
		    }
	    }
	    return reciprocalRank;
	}

	/**
	 * Fetches the related candidates for each scenario for the given query.
	 * Scenario1: full-query candidates only
	 * Scenario2: full-query + top 10 synthetic
	 * Scenario3: full-query + top 100 synthetic
	 *
	 * @param processor a {@link CorpusProcessor} instance
	 * @param scenario the scenario for which the candidates will be fetched
	 * @param query the query prefix to be matched
	 * @return a {@link List} of the fetched candidates
	 */
	private static List<String> getCandidatesForScenario(CorpusProcessor processor, int scenario, String query) {
		List<String> mergedCandidates = new ArrayList<>();
		mergedCandidates.addAll(processor.getFullQueryCandidates(query));  // full-query candidates are for all scenarios

		List<String> syntheticQueryCandidates;

		if (scenario == 2) {
			// Full-query based candidates + Suffix based candidates (top 10)
			syntheticQueryCandidates = processor.getSyntheticQueryCandidates(query, 10000, 10);
			mergedCandidates.addAll(syntheticQueryCandidates);
		} else if (scenario == 3){
			// Full-query based candidates + Suffix based candidates (top 100)
			syntheticQueryCandidates = processor.getSyntheticQueryCandidates(query, 100000, 100);
			mergedCandidates.addAll(syntheticQueryCandidates);
		}
		return mergedCandidates;
	}

	/**
	 * Aggregates the elements in the {@code mergedCandidates} list that
	 * were found to be the same. The resulting {@link Map} contains a key
	 * to count mapping for each unique candidate. Moreover, the map is
	 * sort in descending order based on the resulting values.
	 *
	 * @param mergedCandidates the {@link List} of candidates to be aggregated
	 * @return the resulting {@link Map} instance
	 */
    private static Map<String, Integer> aggregateCandidates(List<String> mergedCandidates) {
        Map<String, Integer> m = new HashMap<>();
        for (String candidate : mergedCandidates) {
            Integer c = m.get(candidate);
	        m.put(candidate, c != null ? ++c : Integer.valueOf(1));
        }

        m = m.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return m;
    }

	/**
	 * Writes the candidates for a specific query to a file. Apart
	 * from the candidates, a relevance judgement (0 or 1) that
	 * indicates whether the particular candidate matches the original
	 * query is included.
	 *
	 * @param writer a {@link BufferedWriter} instance
	 * @param query the original query
	 * @param aggregatedCandidates a {@link Map} instance containing the
	 * the count for each candidate
	 * @throws IOException in case the file writing fails
	 */
	private static void writeCandidatesInFile(BufferedWriter writer, String query,
			Map<String, Integer> aggregatedCandidates) throws IOException {

		for (Map.Entry<String, Integer> candidate : aggregatedCandidates.entrySet()) {
			String candidateWithoutDelim = candidate.getKey().replace("|", "");
			Integer relevanceJudgement = (candidateWithoutDelim.equals(query)) ? 1 : 0;

			String line = candidate.getKey() + "\t" + relevanceJudgement.toString() + "\n";
			writer.write(line);
		}
	}
}
