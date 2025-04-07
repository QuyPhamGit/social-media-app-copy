
/**
 * Team Project -- UserHandlerInterface
 *
 * Lists methods which should be included in any UserHandler class
 * 
 * @author (idk who created this)
 *
 * @version November 17, 2024
 */

public interface UserHandlerInterface {
    public User createAccount(String username, String password, 
                              String bio, String[] interests) throws BadUserException;
    public User logIn(String username, String password) throws BadUserException;

    //assumes user authenticated
    public Post publishPost(String message, String type) throws BadPostException;

    // returns string containing the users which are found and the posts that are found
    public String search(String searchTerm);

    // returns string containing the user's string and all posts by the user
    public String getUser(String username) throws BadUserException;

    // item int designates what to modify/what new value is for, assumes authenticated
    public boolean modifyUser(String newValue, int item, String username);

    // if boolean is false, dislike. if add is false, remove like/dislike
    public boolean reactPost(String postId, boolean like, boolean add);
    public boolean addComment(String postId, String comment, String user); // assumes authenticated
    public boolean reactComment(String postId, String commentId, boolean like, boolean add);

    // method stores how to find posts (either by interests or friends)
    public String getPosts(String[] key, int method);
}

