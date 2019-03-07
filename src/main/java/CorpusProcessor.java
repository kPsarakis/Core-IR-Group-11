import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CorpusProcessor {

	private Map<String, Integer> queriesCount = new HashMap<>();  // contains original-query to count mapping
	private Map<String, Integer> ngramCount = new HashMap<>();    // contains all n-gram to count mapping
	private Map<String, Integer> ngramCountTop10 = new HashMap<>();    // contains top 10K n-gram to count mapping
	private Map<String, Integer> ngramCountTop100 = new HashMap<>();   // contains top 100K n-gram to count mapping

	public CorpusProcessor(List<String> queries) {
		queries.forEach(this::updateQueriesCountMap);

		queriesCount = queriesCount.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	public void buildQueriesNgrams(List<String> queries) {
		for (String query : queries) {
			String[] words = query.split(" ");
			for (int step = 0; step < words.length; step++) {
				String ngramStr = generateEndNgrams(step, words);   // generate n-grams for each query
				updateNgramsCountMap(ngramStr); // aggregate n-grams
			}
		}

		// sort to find the most popular n-grams
		ngramCount = ngramCount.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		ngramCountTop10 = ngramCount.entrySet().stream()
				.limit(10000).collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);
		ngramCountTop100 = ngramCount.entrySet().stream()
				.limit(100000).collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);
	}

	private static String generateEndNgrams(int stepSize, String[] words) {
		Stack<String> ngrams = new Stack<>();

		StringBuilder bobTheBuilder = new StringBuilder();
		for (int i = words.length-1; i >= words.length-1-stepSize; i--) {
			ngrams.push(words[i]);
		}

		while (!ngrams.empty()) {
			bobTheBuilder.append(ngrams.pop());
			if (!ngrams.isEmpty()) {
				bobTheBuilder.append(" ");
			}
		}
		return bobTheBuilder.toString();
	}

	private void updateQueriesCountMap(String query) {
		updateKeyCount(queriesCount, query);
	}

	private void updateNgramsCountMap(String ngramStr) {
		updateKeyCount(ngramCount, ngramStr);
	}

	private void updateKeyCount(Map<String, Integer> map, String key) {
		Integer count = map.get(key);
		if (count == null) {
			map.put(key, 1);
		} else {
			map.put(key, ++count);
		}
	}

	public List<String> getSyntheticQueryCandidates(String prefix, Integer limit) {
		// Split the partial typed query and keep the last term
		String[] prefixTerms = prefix.split(" ");
		String prefixEndTerm = prefixTerms[prefixTerms.length - 1];

		if (prefix.charAt(prefix.length() - 1) == ' ') {
			// Handle whitespace case
			prefixEndTerm = prefixEndTerm + " ";
		}

		System.out.println("Looking to match word \"" + prefixEndTerm + "\"");

		List<String> syntheticSuggestionCandidates = new ArrayList<>();
		List<String> syntheticCandidateSuffixes = getSyntheticCandidateSuffixes(prefixEndTerm, limit);

		// Merge every synthetic candidate suffix with the query prefix (e.g bank o with of america)
		for(String candidate : syntheticCandidateSuffixes) {
//			System.out.println("Synthetic query term 1: \"" + prefix + "\"");
//			System.out.println("Synthetic query term 2: \"" + candidate + "\"");

			String syntheticQuery = (prefix + "|" + candidate).trim();  // add | splitter to distinguish prefix term
//			System.out.println("=> Resulting synthetic-query candidate: \"" + syntheticQuery + "\"");

			syntheticSuggestionCandidates.add(syntheticQuery);
		}
		return syntheticSuggestionCandidates;
	}

	/**
	 *
	 * @param prefixEndTerm
	 * @param limit
	 * @return
	 */
	private List<String> getSyntheticCandidateSuffixes(String prefixEndTerm, Integer limit) {
		Map<String, Integer> ref = (limit == 10000) ? ngramCountTop10: ngramCountTop100;

		List<String> candidates = new ArrayList<>();

		for (Map.Entry<String, Integer> entry : ref.entrySet()) {
			String possibleMatch = entry.getKey();
			if (possibleMatch.startsWith(prefixEndTerm)) {
				String replaced = possibleMatch.replaceFirst(Pattern.quote(prefixEndTerm), "");
				candidates.add(replaced);
			}
		}
		return candidates;
	}

	/**
	 * Generates the full-query candidates for the given prefix.
	 *
	 * @param prefix the prefix to match
	 * @return the generated full-query candidates
	 */
	public List<String> getFullQueryCandidates(String prefix) {
		List<String> fullQueryCandidates = new ArrayList<>();

		int i = 0;
		for (Map.Entry<String, Integer> entry : queriesCount.entrySet()) {
			String key = entry.getKey();
			if (key.startsWith(prefix)) {
				String replaced = key.replaceFirst(Pattern.quote(prefix), "");
				String fullQueryCandidate = prefix + "|" + replaced;
//				System.out.println("=> Full-query candidate: " + fullQueryCandidate);
				i++;
				for (int j = 0; j < queriesCount.get(key); j++) {
					fullQueryCandidates.add(fullQueryCandidate);
				}
			}
		}
		return fullQueryCandidates;
	}
}
