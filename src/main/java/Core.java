import java.util.*;
import java.util.stream.Collectors;

public class Core {

    public static Map<String, Integer> ngramFrequency = new HashMap<>();

    public static void main(String[] args) {

        CorpusReader reader = new CorpusReader();
        List<String> queries = reader.getQueries();

        for (String query : queries) {
            String[] words = query.split(" ");

            for (int step = 0; step < words.length; step++) {
                String ngramStr = generateEndNgrams(step, words);
                updateFrequencyMap(ngramStr);

                System.out.println(ngramStr);
                System.out.println();
            }
        }

        // FIXME: might not need this here
        Map<String, Integer> sortedMap = ngramFrequency.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));


        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter query prefix");
        String prefix = scanner.nextLine();

        while(!prefix.equals("exit")) {
            String[] prefixTerms = prefix.split(" ");
            String prefixEndTerm = prefixTerms[prefixTerms.length - 1];
            System.out.println("Looking to match word \"" + prefixEndTerm + "\"");

            List<String> matchingList = getMatchingList(prefixEndTerm);

            //TODO: Generate the synthetic queries
            //TODO: Rank them using MPC (for now)

            prefix = scanner.nextLine();
        }
    }

    private static List<String> getMatchingList(String prefixEndTerm) {
        List<String> candidates = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : ngramFrequency.entrySet()) {
            String possibleMatch = entry.getKey();
            String[] possibleMatchTerms = possibleMatch.split(" ");
            String possibleMatchStartTerm = possibleMatchTerms[0];

            if (possibleMatchStartTerm.startsWith(prefixEndTerm)) {
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
