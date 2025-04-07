import java.util.ArrayList;
/**
 * Team Project -- Post Interface
 *
 * List methods which should be included in any
 * Post class which has likes/dislikes and comments.
 *
 * @author Omkar Govil-Nair, lab sec 002
 *
 * @version November 3, 2024
 */
public interface PostInterface {
    public String getPostID();
    public String getTimestamp();
    public String getPosterUsername();
    public String getMessage();
    public int getLikes();
    public int getDislikes();
    public boolean addComment(String comment, String user);
    public boolean removeComment(String commentId);
    public boolean changeCommentRatio(String commentId, boolean like, boolean add);
    public ArrayList<String> getComments();
    public boolean equals(Object o);
    public String toString();
}
