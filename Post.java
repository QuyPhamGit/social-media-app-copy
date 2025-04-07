import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Team Project -- Social Media Post Class
 *
 * This class represents a social media post, encapsulating the post's details
 * such as the ID, type, timestamp, poster's username, message, views, likes, dislikes,
 * and comments. It utilizes an ArrayList to manage comments and interacts with a
 * Database class to store and retrieve post-related data. The class includes
 * functionality to create a new post, hide a post, increment views, and manage
 * comments and their associated ratios.
 *
 * @author Omkar Govil-Nair, lab sec 002
 * @author Ahmad Khalaf, lab sec 002
 * @author Charles Pittman, lab sec 002
 * @author Quy Pham, lab sec 002
 *
 * @version November 3, 2024
 *
 */
public class Post implements PostInterface {
    private String postID;
    private String postType;
    private String timestamp;
    private String posterUsername;
    private String message;
    private int views;
    private int likes;
    private int dislikes;
    private ArrayList<String> comments;

    private Database database;
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Constructor for creating a new post
    public Post(String postType, String posterUsername, String message, Database database) throws BadPostException {
        this.postID = "";
        this.database = database; // Store database reference
        this.postType = postType;
        this.posterUsername = posterUsername;
        this.message = message;
        this.timestamp = getDateString(new Date()); // Timestamp for current time
        this.views = 0;
        this.likes = 0;
        this.dislikes = 0;
        this.comments = new ArrayList<String>();

        String id = database.newPost(this.toString()); // Add the post to database

        if (id == null)
            throw new BadPostException("Couldn't create Post!");

        this.postID = id;
    }

    // constructor for a long data string from file: "PostId,postType,posterUsername,message"
    public Post(String dataLine, Database database) throws BadPostException {
        this.database = database;

        try {
            String[] data = dataLine.split(",", -1);

            this.postID = data[0];
            this.postType = data[1];
            this.posterUsername = data[2];
            this.message = data[3];
            this.timestamp = data[4];
            this.views = Integer.parseInt(data[5]);
            this.likes = Integer.parseInt(data[6]);
            this.dislikes = Integer.parseInt(data[7]);
            this.comments = new ArrayList<>();

            for (int i = 8; i < data.length; i++) {
                comments.add(data[i]);
            }
        } catch (Exception e) {
            throw new BadPostException("Bad data, unable to construct Post!");
        }
    }

    public String getPostID() {
        return postID;
    }

    public boolean hidePost() {
        return setPostType("hidden");
    }

    // server should validate that only poster can change posttype
    public boolean setPostType(String newPostType) {
        boolean res = database.changePostType(postID, newPostType);

        if (res)
            this.postType = newPostType;

        return res;
    }

    public String getPostType() {
        return postType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getPosterUsername() {
        return posterUsername;
    }

    public String getMessage() {
        return message;
    }

    public int getViews() {
        return views;
    }

    public int getLikes() {
        return likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public boolean incrementViews() {
        return database.incrementViews(postID);
    }

    public boolean addComment(String comment, String user) {
        return database.addComment(postID, comment, user);
    }

    public boolean removeComment(String commentId) {
        return database.removeComment(postID, commentId);
    }

    public boolean changeCommentRatio(String commentId, boolean like, boolean add) {
        return database.changeCommentRatio(postID, commentId, like, add);
    }

    public boolean changePostRatio(boolean like, boolean add) {
        return database.changePostRatio(postID, like, add);
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public String getDateString(Date d) {
        return dateFormatter.format(d);
    }

    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Post a = (Post) o;
        return (a.getMessage().equals(this.message) &&
                a.getPosterUsername().equals(this.posterUsername) &&
                a.getTimestamp().equals(this.timestamp));
    }

    @Override
    public String toString() {
        String ret = String.format("%s,%s,%s,%s,%s,%s,%s,%s",
                                    postID,
                                    postType,
                                    posterUsername,
                                    message,
                                    timestamp,
                                    "" + views,
                                    "" + likes,
                                    "" + dislikes); // Format for database storage

        for (String c : comments) {
            ret += "," + c;
        }

        return ret;
    }
}

