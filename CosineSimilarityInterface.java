/**
 * Team Project -- CosineSimilarityInterface
 *
 * An interface for  Cosine Similarity.
 * This interface defines the public methods getCosineSimilarity
 * and calculateCosinesSimilarity, which are accessible
 * to implementers of this interface.
 *
 * @author Ahmad Khalaf, lab sec 002
 *
 * @version November 17, 2024
 */
public interface CosineSimilarityInterface {
    double getCosineSimilarity();
    double calculateCosineSimilairty(String[] interests, String[] postTags, String postTitle);
}
