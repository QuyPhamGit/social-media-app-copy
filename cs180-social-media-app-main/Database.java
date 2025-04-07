import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.util.stream.Stream;
/**
 * Team Project -- Database
 *
 * Defines the Database object which is responsible for reading and writing
 * Users and Posts to their respective files. Each User and Post is on its
 * own line in the files, and ArrayLists are used ot make line number identification
 * quicker. All methods are implemented to be thread-safe so that multiple clients
 * can initiate conflicting database changes without issues.
 *
 * @author Omkar Govil-Nair, lab sec 002
 * @author Quy Pham Ngo Thien, lab sec 002
 * @author Charles Pittman, lab sec 002
 * @author Ryan Jo, lab sec 002
 *
 * @version November 3, 2024
 *
 */
public class Database implements DatabaseInterface {
    private static Object[] lockers = {new Object(), new Object()}; // file access locks, 0: userfile, 1: postfile
    private static String[] files = new String[2];                  // stores filenames, 0: user file, 1: post file
    private static ArrayList<String> users = new ArrayList<>();     // Stores usernames, index = line num
    private static ArrayList<String> posts = new ArrayList<>();     // Stores post IDs, index = line num

    /**
     * Constructor for Database, sets filenames and calls loadFiles
     * to fill ArrayLists with latest values from the files.
     *
     * @param userFile the filename for storing User values
     * @param postFile the filename for storing Post values
     */
    public Database(String userFile, String postFile) {
        files[0] = userFile;
        files[1] = postFile;
        loadFiles();
    }

    /**
     * Clears ArrayLists so loadFiles doesn't add extraneous
     * values to the ArrayLists.
     */
    public void reset() {
        users = new ArrayList<>();
        posts = new ArrayList<>();
    }

    /**
     * Calls loadUsers and loadPosts to fill ArrayLists with
     * latest values from files. Clears ArrayLists before calling
     * other load methods.
     *
     * @return true if load methods were successful,
     *         false otherwise
     */
    public boolean loadFiles() {
        reset();
        return loadUsers() && loadPosts(); // Load both users and posts
    }

    /**
     * Attempts to read the user file and add the usernames
     * from each line to the ArrayList. Assumes each line is
     * comma separated with first value being the username.
     *
     * @return true if the file read was successful,
     *         false otherwise
     */
    public boolean loadUsers() {
        synchronized (lockers[0]) {
            try (BufferedReader br = new BufferedReader(new FileReader(files[0]))) {
                String line;
                while ((line = br.readLine()) != null) {
                    users.add(line.split(",")[0]);
                }
                return true; // Successfully loaded users
            } catch (Exception e) {
                e.printStackTrace();
                return false; // Error loading users
            }
        }
    }

    /**
     * Attempts to read the post file and add the post IDs
     * from each line to the ArrayList. Assumes each line is
     * comma separated with first value being the post ID.
     *
     * @return true if the file read was successful,
     *         false otherwise
     */
    public boolean loadPosts() {
        synchronized (lockers[1]) {
            try (BufferedReader br = new BufferedReader(new FileReader(files[1]))) {
                String line;
                while ((line = br.readLine()) != null) {
                    posts.add(line.split(",")[0]);
                }
                return true; // Successfully loaded posts
            } catch (Exception e) {
                e.printStackTrace();
                return false; // Error loading posts
            }
        }
    }

    /**
     * Attempts to get the User string from the file. Throws
     * BadUserException if the User was not found in the file or
     * file read failed.
     *
     * @param username the String username of the User to find
     * @return the User data string
     */
    public String getUser(String username) throws BadUserException {
        int userIndex = -1;

        synchronized (lockers[0]) {
            userIndex = users.indexOf(username);
        }

        if (userIndex == -1)
            throw new BadUserException("User '" + username + "' not found in the database.");

        String ret = readLine(userIndex, 0);

        if (ret == null)
            throw new BadUserException("Unable to read User from file.");

        return ret;
    }

    /**
     * Attempts to get the Post string from the file. Throws
     * BadPostException if the Post was not found in the file or
     * file read failed.
     *
     * @param postId the String post ID of the Post to find
     * @return the Post data string
     */
    public String getPost(String postId) throws BadPostException {
        int postIndex = -1;

        synchronized (lockers[1]) {
            postIndex = posts.indexOf(postId);
        }

        if (postIndex == -1)
            throw new BadPostException("Post ID '" + postId + "' not found in the database.");

        String ret = readLine(postIndex, 1);

        if (ret == null)
            throw new BadPostException("Unable to read Post from file.");

        return ret;
    }

    /**
     * Reads a line from a file.
     *
     * @param lineNum the line number to read
     * @param file the file to read (0: userfile, 1: postfile)
     * @return the String at that line in the file, null if unable
     */
    public String readLine(int lineNum, int file) {
        if (lineNum < 0)
            return null;

        synchronized (lockers[file]) {
            if ((file == 0 && lineNum >= users.size()) ||
                (file == 1 && lineNum >= posts.size()))
                return null;

            try (Stream<String> lines = Files.lines(Paths.get(files[file]))) {
                return lines.skip(lineNum).findFirst().get();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * Writes a String to a line number in a file. Appends a new line
     * to the file if the line number is larger than existing lines.
     *
     * @param lineNum the line number to write to
     * @param data the String to write
     * @param file the file to overwrite (0: userfile, 1: postfile)
     * @return true if the operation was successful,
     *         false otherwise
     */
    public boolean writeLine(int lineNum, String data, int file) {
        // required for entire operation
        synchronized (lockers[file]) {
            // handle case where appending to end of file
            if ((file == 0 && lineNum >= users.size()) ||
                (file == 1 && lineNum >= posts.size())) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(files[file], true))) {
                    bw.write(data);
                    bw.newLine();

                    if (file == 0) {
                        users.add(data.split(",")[0]);
                    } else if (file == 1) {
                        posts.add(data.split(",")[0]);
                    }

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else {  // handle case where modifying existing line in file
                try {
                    BufferedReader br = new BufferedReader(new FileReader(files[file]));
                    BufferedWriter tw = new BufferedWriter(new FileWriter("tmp_" + files[file]));

                    int i = 0;
                    String ln = br.readLine();

                    // writes lines + modification into temp file
                    while (ln != null) {
                        if (i == lineNum)
                            tw.write(data);
                        else
                            tw.write(ln);

                        tw.newLine();

                        ln = br.readLine();
                        i++;
                    }

                    br.close();
                    tw.close();

                    BufferedWriter bw = new BufferedWriter(new FileWriter(files[file]));
                    BufferedReader tr = new BufferedReader(new FileReader("tmp_" + files[file]));

                    ln = tr.readLine();

                    // copies temp file back into original db file
                    while (ln != null) {
                        bw.write(ln);
                        bw.newLine();
                        ln = tr.readLine();
                    }

                    bw.close();
                    tr.close();

                    if (file == 0) {
                        users.set(lineNum, data.split(",")[0]);
                    } else if (file == 1) {
                        posts.set(lineNum, data.split(",")[0]);
                    }

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
    }

    /**
     * Finds users whose usernames contain a search term.
     * Case insensitive search.
     *
     * @param search the search term
     * @return an ArrayList of User objects which match the search
     */
    public ArrayList<User> searchUsers(String search) {
        ArrayList<User> foundUsers = new ArrayList<User>();
        search = search.toLowerCase();

        synchronized (lockers[0]) {
            for (String username : users) {
                if (username.toLowerCase().contains(search)) {
                    try {
                        User tmp = new User(getUser(username), this);
                        foundUsers.add(tmp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return foundUsers;
    }

    /**
     * Finds posts whose message or poster's username contain
     * a search term. Case insensitive search. Returned posts
     * are in chronological order by when they were added.
     *
     * @param search the search term
     * @param mode the method of searching (0 = posts limited to keyterm, 1 = all posts but ordered by similarity to keyterm)
     * @return an ArrayList of Post objects which match the search,
     *         null if file read failed
     */
    public ArrayList<Post> searchPosts(String search, int mode) {
        ArrayList<Post> foundPosts = new ArrayList<Post>();
        search = search.toLowerCase();

        synchronized (lockers[1]) {
            switch (mode) {
                case 0:
                    try (BufferedReader br = new BufferedReader(new FileReader(files[1]))) {
                        String ln = br.readLine();
        
                        while (ln != null) {
                            Post tmp = new Post(ln, this);
        
                            if (tmp.getMessage().toLowerCase().contains(search) ||
                                tmp.getPosterUsername().toLowerCase().contains(search))
                                foundPosts.add(tmp);
        
                            ln = br.readLine();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                    
                    Collections.reverse(foundPosts);
                    break;
                case 1:
                    foundPosts = getPosts(search.split(" "));
                    break;
                default:
                    foundPosts = null;
                    break;
            }
        }

        return foundPosts;
    }

    /**
     * Finds posts whose post type is included in an array
     * of interest categories. Returned posts are in chronological
     * order by when they were added.
     *
     * @param categories array of categories to check for
     * @return ArrayList of Post objects which match the search,
     *         null if file read failed
     */
    public ArrayList<Post> getPosts(String[] categories) {
        ArrayList<Post> foundPosts = new ArrayList<Post>();
        ArrayList<Double> cosineSimilarities = new ArrayList<>();

        synchronized (lockers[1]) {
            try (BufferedReader br = new BufferedReader(new FileReader(files[1]))) {
                String ln = br.readLine();

                while (ln != null) {
                    Post tmp = new Post(ln, this);

                    double cosSimilarity = new CosineSimilarity(categories, new String[]{}, 
                                                    tmp.getMessage().split("<>", -1)[0]).getCosineSimilarity();

                    int index = 0;

                    if (foundPosts.isEmpty()) {
                        foundPosts.add(tmp);
                        cosineSimilarities.add(cosSimilarity);
                        ln = br.readLine();
                        continue;
                    }

                    for (Double sim : cosineSimilarities) {
                        if (cosSimilarity > sim) {
                            cosineSimilarities.add(index, cosSimilarity);
                            foundPosts.add(index, tmp);
                            break;
                        } else if (index == foundPosts.size() - 1) {
                            cosineSimilarities.add(cosSimilarity);
                            foundPosts.add(tmp);
                            break;
                        }
                        index++;
                    }

                    ln = br.readLine(); 
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        //Collections.reverse(foundPosts);
        return foundPosts;
    }

    /**
     * Finds posts whose poster is included in an ArrayList of
     * friends. Returned posts are in chronological order by
     * when they were added.
     *
     * @param friends the ArrayList of friends' usernames to check for
     * @return ArrayList of Post objects which match the search,
     *         null if file read failed
     */
    public ArrayList<Post> getPostsByFriends(ArrayList<String> friends) {
        ArrayList<Post> foundPosts = new ArrayList<Post>();

        synchronized (lockers[1]) {
            try (BufferedReader br = new BufferedReader(new FileReader(files[1]))) {
                String ln = br.readLine();

                while (ln != null) {
                    Post tmp = new Post(ln, this);

                    if (friends.contains(tmp.getPosterUsername()))
                        foundPosts.add(tmp);

                    ln = br.readLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        Collections.reverse(foundPosts);
        return foundPosts;
    }

    /**
     * Updates a User entry in the database.
     *
     * @param oldUser the old user's data string
     * @param newUser the new user's data string
     * @return true if the operation succeeded,
     *         false otherwise
     */
    public boolean modifyUser(String oldUser, String newUser) {
        String uname = oldUser.split(",")[0];
        int ind = -1;

        synchronized (lockers[0]) {
            ind = users.indexOf(uname);
        }

        if (ind == -1)
            return false;

        writeLine(ind, newUser, 0);

        return true;
    }

    /**
     * Changes the post type for a post.
     *
     * @param postId the id of the post
     * @param type the new post type to set
     * @return true if the operation was successful,
     *         false otherwise
     */
    public boolean changePostType(String postId, String type) {
        int postIndex = -1;

        synchronized (lockers[1]) {
            postIndex = posts.indexOf(postId);
        }

        if (postIndex == -1) {
            return false;
        }

        String curr = "";
        String changed = "";

        synchronized (lockers[1]) {
            try (Stream<String> lines = Files.lines(Paths.get(files[1]))) {
                curr = lines.skip(postIndex).findFirst().get();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            try {
                String[] data = curr.split(",");
                data[1] = type;
                changed = String.join(",", data);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return writeLine(postIndex, changed, 1);
    }

    /**
     * Adds one to the view count for a post.
     *
     * @param postId the id of the post to change
     * @return true if the operation was successful,
     *         false otherwise
     */
    public boolean incrementViews(String postId) {
        int postIndex = -1;

        synchronized (lockers[1]) {
            postIndex = posts.indexOf(postId);
        }

        if (postIndex == -1) {
            return false;
        }

        String curr = "";
        String changed = "";

        synchronized (lockers[1]) {
            try (Stream<String> lines = Files.lines(Paths.get(files[1]))) {
                curr = lines.skip(postIndex).findFirst().get();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            try {
                String[] data = curr.split(",");
                data[5] = "" + (Integer.parseInt(data[5]) + 1);
                changed = String.join(",", data);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return writeLine(postIndex, changed, 1);
    }

    /**
     * Adds or removes likes or dislikes from a Post.
     *
     * @param postId the id of the post to change
     * @param like true if a like should be added/removed,
     *             false if a dislike should be added/removed
     * @param add true if being added, false if being removed
     * @return true if the operation was successful,
     *         false otherwise
     */
    public boolean changePostRatio(String postId, boolean like, boolean add) {
        int postIndex = -1;

        synchronized (lockers[1]) {
            postIndex = posts.indexOf(postId);
        }

        if (postIndex == -1) {
            return false;
        }

        String curr = "";
        String changed = "";

        synchronized (lockers[1]) {
            try (Stream<String> lines = Files.lines(Paths.get(files[1]))) {
                curr = lines.skip(postIndex).findFirst().get();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            try {
                String[] data = curr.split(",");

                if (like) {
                    if (add)
                        data[6] = "" + (Integer.parseInt(data[6]) + 1);
                    else
                        data[6] = "" + (Integer.parseInt(data[6]) - 1);
                } else {
                    if (add)
                        data[7] = "" + (Integer.parseInt(data[7]) + 1);
                    else
                        data[7] = "" + (Integer.parseInt(data[7]) - 1);
                }

                changed = String.join(",", data);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return writeLine(postIndex, changed, 1);
    }

    /**
     * Adds a comment to a post's data string.
     *
     * @param postId the id of the post to change
     * @param comment the comment to add
     * @return true if the operation was successful,
     *         false otherwise
     */
    public boolean addComment(String postId, String comment, String user) {
        int postIndex = -1;

        synchronized (lockers[1]) {
            postIndex = posts.indexOf(postId);
        }

        if (postIndex == -1) {
            return false;
        }

        String curr = "";
        String cString = "";

        synchronized (lockers[1]) {
            try (Stream<String> lines = Files.lines(Paths.get(files[1]))) {
                curr = lines.skip(postIndex).findFirst().get();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            try {
                String[] data = curr.split(",");
                int commId = 0;

                if (data.length == 8) {
                    commId = 0;
                } else {
                    String prevCommentId = data[data.length - 1].split(";")[1];
                    commId = Integer.parseInt(prevCommentId) + 1;
                }

                cString = user + ";" + commId + ";0;0;" + comment;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return writeLine(postIndex, curr + "," + cString, 1);
    }

    /**
     * Removes a comment from a post's data string.
     *
     * @param postId the id of the post to remove
     * @param commId the id of the comment to remove
     * @return true if the operation was successful,
     *         false otherwise
     */
    public boolean removeComment(String postId, String commId) {
        int postIndex = -1;

        synchronized (lockers[1]) {
            postIndex = posts.indexOf(postId);
        }

        if (postIndex == -1) {
            return false;
        }

        String curr = "";
        String changed = "";

        synchronized (lockers[1]) {
            try (Stream<String> lines = Files.lines(Paths.get(files[1]))) {
                curr = lines.skip(postIndex).findFirst().get();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            try {
                String[] data = curr.split(",");

                if (data.length == 8) {
                    return false;
                }

                changed = data[0];

                for (int i = 1; i < 8; i++) {
                    changed += "," + data[i];
                }

                for (int i = 8; i < data.length; i++) {
                    String[] commData = data[i].split(";");
                    String id = commData[1];

                    if (!id.equals(commId)) {
                        changed += "," + data[i];
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return writeLine(postIndex, changed, 1);
    }

    /**
     * Adds or removes likes or dislikes from a comment on a Post.
     *
     * @param postId the id of the post to change
     * @param commId the id of the comment to change
     * @param like true if a like should be added/removed,
     *             false if a dislike should be added/removed
     * @param add true if being added, false if being removed
     * @return true if the operation was successful,
     *         false otherwise
     */
    public boolean changeCommentRatio(String postId, String commId, boolean like, boolean add) {
        int postIndex = -1;

        synchronized (lockers[1]) {
            postIndex = posts.indexOf(postId);
        }

        if (postIndex == -1) {
            return false;
        }

        String curr = "";
        String changed = "";

        synchronized (lockers[1]) {
            try (Stream<String> lines = Files.lines(Paths.get(files[1]))) {
                curr = lines.skip(postIndex).findFirst().get();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            try {
                String[] data = curr.split(",");

                if (data.length == 8) {
                    return false;
                }

                for (int i = 8; i < data.length; i++) {
                    String[] commData = data[i].split(";");
                    String id = commData[1];

                    if (id.equals(commId)) {
                        if (like) {
                            if (add)
                                commData[2] = "" + (Integer.parseInt(commData[2]) + 1);
                            else
                                commData[2] = "" + (Integer.parseInt(commData[2]) - 1);
                        } else {
                            if (add)
                                commData[3] = "" + (Integer.parseInt(commData[3]) + 1);
                            else
                                commData[3] = "" + (Integer.parseInt(commData[3]) - 1);
                        }

                        data[i] = String.join(";", commData);
                        break;
                    }
                }

                changed = String.join(",", data);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return writeLine(postIndex, changed, 1);
    }

    /**
     * Adds a new User to the database file.
     *
     * @param data the User's data string
     * @return true if the operation was successful,
     *         false otherwise
     */
    public boolean newUser(String data) {
        if (data == null)
            return false;

        synchronized (lockers[0]) {
            // check if user exists already
            if (users.contains(data.split(",")[0]))
                return false;

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(files[0], true))) {
                bw.write(data);
                bw.newLine();

                users.add(data.split(",")[0]);

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Adds a new Post to the database file.
     *
     * @param data the Post's data string
     * @return the Post ID if successful,
     *         null otherwise
     */
    public String newPost(String data) {
        if (data == null)
            return null;

        synchronized (lockers[1]) {
            String postId = "";

            if (posts.size() == 0) {
                postId = "0";
            } else {
                int curr = Integer.parseInt(posts.get(posts.size() - 1));
                postId = "" + (curr + 1);
            }

            data = postId + data;

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(files[1], true))) {
                bw.write(data);
                bw.newLine();

                posts.add(data.split(",")[0]);

                return postId;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * Checks if a user exists already by checking
     * username.
     *
     * @param username the username to check
     * @return true if the user exists already,
     *         false otherwise
     */
    public boolean usernameExists(String username) {
        synchronized (lockers[0]) {
            return users.contains(username);
        }
    }
}
