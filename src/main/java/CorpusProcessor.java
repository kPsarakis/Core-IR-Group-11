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

	/**
	 * For each query of the background dataset, it calculates the
	 * end-ngrams and aggregates them into the {@code ngramCount}
	 * map.
	 *
	 * @param queries the queries of the background dataset
	 */
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

		// top 10K to be used by scenario 2
		ngramCountTop10 = ngramCount.entrySet().stream()
				.limit(10000).collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);
		// top 100K to be used by scenario 2
		ngramCountTop100 = ngramCount.entrySet().stream()
				.limit(100000).collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);
	}

	/**
	 * Generates the end n-gram for the given step size.
	 *
	 * @param stepSize the step size
	 * @param words the words out of which the n-grams are generated
	 * @return the generated ngram
	 */
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

	/**
	 * Updates {@code queriesCount} map.
	 *
	 * @param query the key to be updated
	 */
	private void updateQueriesCountMap(String query) {
		updateKeyCount(queriesCount, query);
	}

	/**
	 * Updates {@code ngramCount} map.
	 *
	 * @param ngramStr the key to be updated
	 */
	private void updateNgramsCountMap(String ngramStr) {
		updateKeyCount(ngramCount, ngramStr);
	}

	/**
	 * Updates the corresponding map based on the {@code key} parameter.
	 * If the key exists, the count is increased. Otherwise it is set to 1.
	 *
	 * @param map the map to be updated
	 * @param key key the value of which is updated
	 */
	private void updateKeyCount(Map<String, Integer> map, String key) {
		Integer count = map.get(key);
		if (count == null) {
			map.put(key, 1);
		} else {
			map.put(key, ++count);
		}
	}

	/**
	 * Generates the synthetic query candidates for the given prefix.
	 * The {@code limit} parameter specifies whether the candidates should
	 * be generated using the 10K or the 100K ngram map.
	 *
	 * @param prefix the prefix to be matches
	 * @param limit indicates which ngramCount to use
	 * (either top10K or top100K)
	 * @return a {@link List} with the synthetic candidates
	 */
	public List<String> getSyntheticQueryCandidates(String prefix, Integer limit) {
		// Split the partial typed query and keep the last term
		String[] prefixTerms = prefix.split(" ");
		String prefixEndTerm = prefixTerms[prefixTerms.length - 1];

		if (prefix.charAt(prefix.length() - 1) == ' ') {
			// Handle whitespace case
			prefixEndTerm = prefixEndTerm + " ";
		}

		List<String> syntheticSuggestionCandidates = new ArrayList<>();
		List<String> syntheticCandidateSuffixes = getSyntheticCandidateSuffixes(prefixEndTerm, limit);

		for(String candidate : syntheticCandidateSuffixes) {
			// Merge every synthetic candidate suffix with the query prefix (e.g bank o with of america)
			String syntheticQuery = (prefix + "|" + candidate).trim();  // add | delimiter to distinguish prefix term
			syntheticSuggestionCandidates.add(syntheticQuery);
		}
		return syntheticSuggestionCandidates;
	}

	/**
	 * Returns the matching synthetic suffixes for the given prefix.
	 * The {@code limit} parameter specifies whether to use the 10K
	 * or the 100K ngram map.
	 *
	 * @param prefixEndTerm the prefix to be matched
	 * @param limit ndicates which ngramCount to use
	 * either top10K or top100K)
	 * @return a {@link List} with the matching suffixes
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
	 * @param prefix the prefix to be matched
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
				i++;
				fullQueryCandidates.add(fullQueryCandidate);
				if (i == 10) {
					break;
				}
			}
		}
		return fullQueryCandidates;
	}
}
