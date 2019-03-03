import java.util.*;

public class Core {

    public static Map<String, Integer> ngramFrequency = new HashMap<>();

    public static void main(String[] args) {
        CorpusReader reader = new CorpusReader();
        List<String> queries = reader.getQueries();

        for (String query : queries) {
            String[] words = query.split(" ");

            System.out.print("Ngrams for \"" + query + "\" are: [");
            for (int step = 0; step < words.length; step++) {
                // generate n-grams for each query
                String ngramStr = generateEndNgrams(step, words);
                // aggregate n-grams
                updateFrequencyMap(ngramStr);

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
            }

            //TODO: Merge with the historically popular queries
            //TODO: Rank them using MPC (for now)
            System.out.println("===================================");
            prefix = scanner.nextLine();
        }
    }

    private static Set<String> getMatchingCandidates(String prefixEndTerm) {
        Set<String> candidates = new HashSet<>();

        for (Map.Entry<String, Integer> entry : ngramFrequency.entrySet()) {
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

    private static void updateFrequencyMap(String ngramStr) {
        Integer count = ngramFrequency.get(ngramStr);
        if (count == null) {
            ngramFrequency.put(ngramStr, 1);
        } else {
            ngramFrequency.put(ngramStr, ++count);
        }
    }
}
