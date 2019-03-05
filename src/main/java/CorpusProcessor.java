import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

public class CorpusProcessor {

	private Map<String, Integer> queriesCount = new HashMap<>();  // contains original-query to count mapping
	private Map<String, Integer> ngramCount = new HashMap<>();    // contains n-gram to count mapping

	public CorpusProcessor(List<String> queries) {
		queries.forEach(this::updateQueriesCountMap);

		queriesCount = queriesCount.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	public void buildQueriesNgrams(List<String> queries) {
		for (String query : queries) {
			String[] words = query.split(" ");

			System.out.print("Ngrams for \"" + query + "\" are: [");
			for (int step = 0; step < words.length; step++) {
				// generate n-grams for each query
				String ngramStr = generateEndNgrams(step, words);
				// aggregate n-grams
				updateNgramsCountMap(ngramStr);

				System.out.print("\"" + ngramStr + "\"");
				System.out.print(step != words.length - 1 ? ", " : "]");
			}
			System.out.println();
		}
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
			prefix = prefix.trim();
			String firstWords = prefix;
			if (firstWords.contains(" ")) {
				firstWords = prefix.substring(0, prefix.lastIndexOf(" "));
			} else {
				firstWords = "";
			}
			System.out.println("Typed query: \"" + prefix + "\"");
			System.out.println("Synthetic query term 1: \"" + firstWords + "\"");
			System.out.println("Synthetic query term 2: \"" + candidate + "\"");

			String syntheticQuery = (firstWords + " " + candidate).trim();
			System.out.println("=> Resulting synthetic-query candidate: \"" + syntheticQuery + "\"");

			syntheticSuggestionCandidates.add(syntheticQuery);
		}
		return syntheticSuggestionCandidates;
	}

	/**
	 * Generates the full-query candidates for the given prefix.
	 * The number of candidates to be returned is determined by the
	 * {@code limit} parameter. Default is 10.
	 *
	 * @param prefixEndTerm
	 * @param limit
	 * @return
	 */
	private List<String> getSyntheticCandidateSuffixes(String prefixEndTerm, Integer limit) {
		limit = (limit == null) ? 10000 : limit;
		List<String> candidates = new ArrayList<>();

		int i = 0;
		for (Map.Entry<String, Integer> entry : ngramCount.entrySet()) {
			String possibleMatch = entry.getKey();
			if (possibleMatch.startsWith(prefixEndTerm)) {
				i++;
				candidates.add(possibleMatch);
				if (i == limit) {
					break;
				}
			}
		}
		return candidates;
	}

	/**
	 * Generates the full-query candidates for the given prefix.
	 * The number of candidates to be returned is determined by the
	 * {@code limit} parameter. Default is 10.
	 *
	 * @param prefix the prefix to match
	 * @param limit limits the number of candidates to be returned
	 * @return the generated full-query candidates
	 */
	public List<String> getFullQueryCandidates(String prefix, Integer limit) {
		limit = (limit == null) ? 10 : limit;
		List<String> fullQueryCandidates = new ArrayList<>();

		int i = 0;
		for (Map.Entry<String, Integer> entry : queriesCount.entrySet()) {
			if (entry.getKey().startsWith(prefix)) {
				System.out.println("=> Full-query candidate: " + entry.getKey());
				i++;
				fullQueryCandidates.add(entry.getKey());
				if (i == limit) {
					break;
				}
			}
		}
		return fullQueryCandidates;
	}
}
