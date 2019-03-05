import java.util.*;
import java.util.stream.Collectors;

public class Core {

    public static void main(String[] args) {

        final CorpusReader reader = new CorpusReader(); // Initialize the reader
        final List<String> queries = reader.getQueries();

        final CorpusProcessor processor = new CorpusProcessor(queries); // Initialize the processor
	    processor.buildQueriesNgrams(queries);

	    System.out.print("\nEnter query prefix: ");

	    // Read user input (Simulate partial query typing)
	    Scanner scanner = new Scanner(System.in);
        String prefix = scanner.nextLine();

        while(!prefix.equals("exit")) {

	        List<String> syntheticQueryCandidates = processor.getSyntheticQueryCandidates(prefix, 10000);
	        List<String> fullQueryCandidates = processor.getFullQueryCandidates(prefix, 10);

            List<String> mergedCandidates = new ArrayList<>();
	        mergedCandidates.addAll(syntheticQueryCandidates);
	        mergedCandidates.addAll(fullQueryCandidates);

	        //TODO: in case i decide to return them as maps
//	        Map<String, Integer> mergedCandidates = new HashMap<>(m1);
//	        m2.forEach(
//			        (key, value) -> map3.merge(key, value, (v1, v2) -> v1 + v2)
//	        );

            List<String> mrrCandidates = getMrrRankingCandidates(mergedCandidates, 8);

            //TODO: Rank them using MPC (for now)
            System.out.println("===================================");
            prefix = scanner.nextLine();
        }
    }

    private static List<String> getMrrRankingCandidates(List<String> syntheticSuggestionCandidates, int num) {
        Map<String, Integer> m = new HashMap<>();
        for (String candidate : syntheticSuggestionCandidates) {
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

        List<String> keys = m.entrySet().stream()
            .map(Map.Entry::getKey)
            .limit(num)
            .collect(Collectors.toList());

        return keys;
    }
}
