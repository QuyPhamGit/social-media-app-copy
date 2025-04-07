import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A class which takes an input of user interests and compares its vector frequency
 * with a post's title and tags, returning an average similarity value. This class
 * is used to order the posts in a way that appeals to each individual user.
 * 
 * @author Ryan Jo, lab sec 002
 * @version november 17, 2024
 */

public class CosineSimilarity {

    private double similarity;

    public CosineSimilarity(String[] interests, String[] postTags, String postTitle) {
        similarity = calculateCosineSimilarity(interests, postTags, postTitle);
    }

    public double getCosineSimilarity() {
        return similarity;
    }

    public double calculateCosineSimilarity(String[] interests, String[] postTags, String postTitle) {
        double simVal = 0;

        for (String interest : interests) {
            for (String postTag : postTags) {
                if (interest.equalsIgnoreCase(postTag)) {
                    simVal += 1.5; 
                    continue;
                }

                simVal += calculatePairwiseCosineSimilarity(interest, postTag);
            }
        }

        String[] titleWords = postTitle.split("\\s+"); 
        double titleSimVal = 0; // Track title similarity separately
        int totalTitleComparisons = interests.length * titleWords.length;

        for (String interest : interests) {
            for (String titleWord : titleWords) {
                if (interest.equalsIgnoreCase(titleWord)) {
                    titleSimVal += 1.25; 
                    continue;
                }

                titleSimVal += calculatePairwiseCosineSimilarity(interest, titleWord);
            }
        }

        if (totalTitleComparisons > 0) {
            titleSimVal /= totalTitleComparisons;
        }
    
        return simVal + titleSimVal;
    }

    private double calculatePairwiseCosineSimilarity(String str1, String str2) {
        String lowercaseStr1 = str1.toLowerCase();
        String lowercaseStr2 = str2.toLowerCase();

        Map<Character, Integer> freqMap1 = buildFrequencyMap(lowercaseStr1);
        Map<Character, Integer> freqMap2 = buildFrequencyMap(lowercaseStr2);

        Set<Character> allCharacters = new HashSet<>(freqMap1.keySet());
        allCharacters.addAll(freqMap2.keySet());

        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;

        for (Character c : allCharacters) {
            int freq1 = freqMap1.getOrDefault(c, 0);
            int freq2 = freqMap2.getOrDefault(c, 0);

            dotProduct += freq1 * freq2;
            magnitude1 += freq1 * freq1;
            magnitude2 += freq2 * freq2;
        }

        if (magnitude1 == 0 || magnitude2 == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(magnitude1) * Math.sqrt(magnitude2));
    }

    private Map<Character, Integer> buildFrequencyMap(String str) {
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : str.toCharArray()) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }
        return freqMap;
    }
}
