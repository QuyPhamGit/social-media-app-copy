/**
 * Team Project -- Post Tests Interface
 *
 * List the methods which should be included in any test case
 * class for a class which implements PostInterface.
 *
 * @author Omkar Govil-Nair, lab sec 002
 *
 * @version November 3, 2024
 */
public interface PostTestInterface {
    public void resetFiles() throws Exception;                // helper method to reset DB files
    public void runTestPostConstructors() throws Exception;   // tests Post constructors
    public void runTestPostLikesDislikes() throws Exception;  // test liking and disliking a Post
    public void runTestComments() throws Exception;           // test adding, liking, disliking, removing comments
    public void runTestBadData() throws Exception;            // test feeding bad data to constructors
}

