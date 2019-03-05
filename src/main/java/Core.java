import java.util.*;
import java.util.stream.Collectors;

public class Core {

    public static void main(String[] args) {

        final CorpusReader reader = new CorpusReader();     // initialize the reader
        final List<String> queries = reader.getQueries();   // get the read queries

        final CorpusProcessor processor = new CorpusProcessor(queries); // Initialize the processor
	    processor.buildQueriesNgrams(queries);  // build n-grams for each query

	    // Read user input (Simulate partial query typing)
	    Scanner scanner = new Scanner(System.in);
        String prefix = "";
        Integer scenario = null;

        while(!prefix.equals("exit")) {
	        System.out.print("\nEnter query prefix: ");
	        prefix = scanner.nextLine();
	        System.out.println("\nGenerate candidates for scenario (1, 2, 3): ");
	        scenario = Integer.parseInt(scanner.nextLine());
	        if (!scenario.equals(1) && !scenario.equals(2) && !scenario.equals(3)) {
		        System.out.println("man mou dwse swsto scenario tin epomeni");
		        return;
	        }

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
	        System.out.println("yolo");
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
