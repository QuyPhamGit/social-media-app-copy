/**
 * Team Project -- Client Interface
 *
 * Lists methods which should be included in any client class
 * 
 * @author Ryan Jo, lab sec 002
 *
 * @version November 16, 2024
 */

public interface ClientInterface {
    boolean signUp(String username, String password, String bio, String[] interests);
    boolean logIn(String username, String password);
    boolean post(String message, String type);
    String search(String searchTerm);
    String getUser(String username);
    String getThisUser();
    boolean modifyUser(String newValue, String item, String username);
    boolean reactToPost(String postId, boolean like, boolean add);
    boolean addComment(String postId, String comment, String user);
    boolean reactToComment(String postId, String commentId, boolean liked, boolean add);
    String getPosts(String[] interests, String method);
    boolean logOut();
    void broadcastMessage(String messageToSend);
    boolean acceptablePassword(String thePassword);
}
