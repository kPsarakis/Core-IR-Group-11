import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Core {

    public static void main(String[] args) throws FileNotFoundException {

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

			    for (String userInputQuery : userInputQueries) {
			    	userInputQuery = "www ngoforum org tr";

					String[] userInputQuerySplit = userInputQuery.split(" ", 2);
					String firstWord = userInputQuerySplit[0]; // the first work as a whole

				    if (userInputQuerySplit.length > 1) {
				        // not a single word case

					    String restOfQuery = " " + userInputQuerySplit[1];
					    String currentQuery = firstWord;

					    for (int i = 0; i < restOfQuery.length(); i++){
						    List<String> mergedCandidates = new ArrayList<>();
						    mergedCandidates.addAll(processor.getFullQueryCandidates(currentQuery));  // full-query candidates are for all scenarios

						    List<String> syntheticQueryCandidates;

						    if (scenario == 2) {
							    // Full-query based candidates + Suffix based candidates (top 10k)
							    syntheticQueryCandidates = processor.getSyntheticQueryCandidates(currentQuery, 10000);
							    mergedCandidates.addAll(syntheticQueryCandidates);
						    } else if (scenario == 3){
							    // Full-query based candidates + Suffix based candidates (top 100k)
							    syntheticQueryCandidates = processor.getSyntheticQueryCandidates(currentQuery, 100000);
							    mergedCandidates.addAll(syntheticQueryCandidates);
						    }

						    Map<String, Integer> aggregatedCandidates = aggregateCandidates(mergedCandidates);

						    for (Map.Entry<String, Integer> candidate : aggregatedCandidates.entrySet()) {
						        String candidateWithoutDelim = candidate.getKey().replace("|", "");
						        Integer relevanceJudgement = (candidateWithoutDelim.equals(userInputQuery)) ? 1 : 0;

							    String line = candidate.getKey() + "\t" + relevanceJudgement.toString() + "\n";
							    writer.write(line);
						    }
						    char currentChar = restOfQuery.charAt(i);
						    currentQuery = currentQuery + currentChar;
					    }
					    writer.close();
				    }

				    //TODO: REMOVE ME
				    break;
			    }

		    } catch(IOException io) {
			    io.printStackTrace();
		    }
        }
    }

    private static void test(CorpusProcessor processor, String prefix) {
    	Scanner scanner = new Scanner(System.in);
    	Integer scenario = 2;

	    while(!prefix.equals("exit")) {
		    System.out.print("\nEnter query prefix: ");
		    prefix = scanner.nextLine();
		    System.out.println("\nGenerate candidates for scenario (1, 2, 3): ");

		    List<String> mergedCandidates = new ArrayList<>();
		    mergedCandidates.addAll(processor.getFullQueryCandidates(prefix));  // full-query candidates are for all scenarios

		    List<String> syntheticQueryCandidates;

		    if (scenario.equals(2)) {
			    // Full-query based candidates + Suffix based candidates (top 10k)
			    syntheticQueryCandidates = processor.getSyntheticQueryCandidates(prefix, 10000);
			    mergedCandidates.addAll(syntheticQueryCandidates);
		    } else if (scenario.equals(3)){
			    // Full-query based candidates + Suffix based candidates (top 100k)
			    syntheticQueryCandidates = processor.getSyntheticQueryCandidates(prefix, 100000);
			    mergedCandidates.addAll(syntheticQueryCandidates);
		    }

		    //TODO: To be used by LAMBDA MART i guess
		    Map<String, Integer> aggregatedCandidates = aggregateCandidates(mergedCandidates);
	    }
    }

    private static Map<String, Integer> aggregateCandidates(List<String> mergedCandidates) {
        Map<String, Integer> m = new HashMap<>();
        for (String candidate : mergedCandidates) {
            Integer c = m.get(candidate);
            if (c != null) {
                m.put(candidate, ++c);
            } else {
                m.put(candidate, 1);
            }
        }

        m = m.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return m;
    }
}
