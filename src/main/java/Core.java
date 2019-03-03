import java.util.*;

public class Core {

    public static Map<String, Integer> ngramCount = new HashMap<>();

    public static void main(String[] args) {
        final CorpusReader reader = new CorpusReader();
        final List<String> queries = reader.getQueries();
        final Map<String,Integer> queriesCount = reader.getQueriesCount();

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

        // Read user input
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nEnter query prefix: ");
        String prefix = scanner.nextLine();

        while(!prefix.equals("exit")) {
            // Split the partial typed query and keep the last term
            String[] prefixTerms = prefix.split(" ");
            String prefixEndTerm = prefixTerms[prefixTerms.length - 1];
            if (prefix.charAt(prefix.length()-1) == ' ') {
                prefixEndTerm = prefixEndTerm + " ";  // Handle whitespace case
            }
            System.out.println("Looking to match word \"" + prefixEndTerm + "\"");

            Set<String> syntheticSuggestionCandidates = new HashSet<>();

            Set<String> matchingCandidates = getMatchingCandidates(prefixEndTerm);
            for(String candidate : matchingCandidates) {
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
                System.out.println("Resulting query: \"" + syntheticQuery + "\"");

                syntheticSuggestionCandidates.add(syntheticQuery);
            }

            // Add the 10 most popular full-query candidates
            int i = 0;
            for (Map.Entry<String, Integer> entry : queriesCount.entrySet()) {
                i++;
                syntheticSuggestionCandidates.add(entry.getKey());

                if (i == 10) {
                    break;
                }
            }

            //TODO: Rank them using MPC (for now)
            System.out.println("===================================");
            prefix = scanner.nextLine();
        }
    }

    private static Set<String> getMatchingCandidates(String prefixEndTerm) {
        Set<String> candidates = new HashSet<>();

        for (Map.Entry<String, Integer> entry : ngramCount.entrySet()) {
            String possibleMatch = entry.getKey();

            if (possibleMatch.startsWith(prefixEndTerm)) {
                candidates.add(possibleMatch);
            }
        }
        return candidates;
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

    private static void updateNgramsCountMap(String ngramStr) {
        Integer count = ngramCount.get(ngramStr);
        if (count == null) {
            ngramCount.put(ngramStr, 1);
        } else {
            ngramCount.put(ngramStr, ++count);
        }
    }
}
