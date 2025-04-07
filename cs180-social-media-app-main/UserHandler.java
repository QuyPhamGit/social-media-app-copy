import java.net.Socket;
import java.util.ArrayList;
import java.io.*;
import java.net.ServerSocket;
/**
 * Team Project -- User Handler
 *
 * Defines a runnable object which handles
 * network i/o with a client.
 *
 * @author Omkar Govil-Nair, lab sec 002
 * @author Ryan Jo, lab sec 002
 * @author Charlse Pittman, lab sec 002
 * @author Quy Pham Ngo Thien, lab sec 002
 * @author Ahmad Khalaf, lab sec 002
 *
 * @version November 17, 2024
 *
 */
public class UserHandler implements Runnable, UserHandlerInterface {
    private Server server;
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private User user;

    public UserHandler(Socket socket, BufferedWriter bw, BufferedReader br, Server server) {
        try {
            this.socket = socket;
            this.server = server;
            this.bufferedWriter = bw;
            this.bufferedReader = br;
        } catch (Exception e) {
            closeEverything();
            e.printStackTrace();
        }
    }

    public void run() {
        String message;

        while (socket.isConnected()) {

            try {
                message = bufferedReader.readLine();

                if (message == null) {
                    continue;
                }

                if (message.length() < 7) {
                    broadcastMessage("failed");
                    continue;
                }

                String[] data = message.substring(6).split(";", -1);

                switch (message.substring(0, 6)) {
                    case "create":
                        if (!isValidDataLen(data, 4))
                            continue;

                        try {
                            createAccount(data[0], data[1], data[2], data[3].split("~"));
                            broadcastMessage("success");
                        } catch (BadUserException e) {
                            broadcastMessage("failed," + e.getMessage());
                            e.printStackTrace();
                        }

                        break;
                    case "login-":
                        if (!isValidDataLen(data, 2))
                            continue;

                        try {
                            logIn(data[0], data[1]);
                            broadcastMessage("success");
                        } catch (BadUserException e) {
                            broadcastMessage("failed," + e.getMessage());
                            e.printStackTrace();
                        }

                        break;
                    case "post--":
                        if (!isAuthenticated() || !isValidDataLen(data, 2))
                            continue;

                        try {
                            Post p = publishPost(data[0], data[1]);
                            broadcastMessage("success" + p.toString());
                        } catch (BadPostException e) {
                            broadcastMessage("failed," + e.getMessage());
                        }

                        break;
                    case "search":
                        if (!isAuthenticated() || !isValidDataLen(data, 1))
                            continue;

                        broadcastMessage("success" + search(data[0]));
                        break;
                    case "getusr":
                        if (!isAuthenticated() || !isValidDataLen(data, 1))
                            continue;

                        try {
                            broadcastMessage("success" + getUser(data[0]));
                        } catch (BadUserException e) {
                            broadcastMessage("failed,user not found");
                        }

                        break;
                    case "modusr":
                        if (!isAuthenticated() || !isValidDataLen(data, 3))
                            continue;

                        try {
                           boolean res = modifyUser(data[0], Integer.parseInt(data[1]), data[2]);
                           broadcastMessage(res ? "success" : "failed,user mod fail");
                        } catch (Exception e) {
                           broadcastMessage("failed,invalid params");
                        }
                        break;
                    case "reactp":
                        if (!isAuthenticated() || !isValidDataLen(data, 3))
                            continue;

                        boolean res = reactPost(data[0], data[1].equals("true"), data[2].equals("true"));
                        broadcastMessage(res ? "success" : "failed,react fail");
                        break;
                    case "addcom":
                        if (!isAuthenticated() || !isValidDataLen(data, 2))
                            continue;

                        broadcastMessage(addComment(data[0], data[1], data[2]) ? "success" : "failed,add com fail");
                        break;
                    case "reactc":
                        if (!isAuthenticated() || !isValidDataLen(data, 4))
                            continue;

                        boolean res2 = reactComment(data[0], data[1], data[2].equals("true"), data[3].equals("true"));
                        broadcastMessage(res2 ? "success" : "failed,react fail");
                        break;
                    case "getpst":
                        if (!isAuthenticated() || !isValidDataLen(data, 2))
                            continue;

                        try {
                            broadcastMessage("success" + getPosts(data[0].split("~"), Integer.parseInt(data[1])));
                        } catch (Exception e) {
                            broadcastMessage("failed,invalid params");
                        }
                        break;
                    case "getths":
                        if (!isAuthenticated())
                            continue;

                        broadcastMessage("success" + user.toString());
                        break;
                    case "logout":
                        user = null;
                        break;
                    default:
                        broadcastMessage("invalid request");
                }

            } catch (IOException e) {
                closeEverything();
                break;
            }

        }

    }

    private boolean isAuthenticated() {
        if (user == null) {
            broadcastMessage("no-auth");
            return false;
        }
        return true;
    }

    private boolean isValidDataLen(String[] data, int len) {
        if (data.length < len) {
            broadcastMessage("failed");
            return false;
        }
        return true;
    }

    public void broadcastMessage(String messageToSend) {
        try {
            bufferedWriter.write(messageToSend);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException ioe) {
            closeEverything();
        }
    }

    public void closeEverything() {
        server.removeActiveUser(this);
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public User createAccount(String username,
                              String password,
                              String bio,
                              String[] interests) throws BadUserException {
        user = new User(username, password, bio, interests, server.db);
        return user;
    }

    //// Charles - logs in to the User
    public User logIn(String username, String password) throws BadUserException {
        user = new User(username, password, server.db);
        return user;
    }


    public Post publishPost(String message, String type) throws BadPostException {
        return new Post(type, user.getUsername(), message, server.db);
    }

    /**
     * Charles
     * Returns users and posts ordered by similarity to the searchword
     * Case insensitive search.
     *
     * @param searchTerm the search term
     * @return a String of User and Post info
     * Posts separated with ","
     * Users separated with ","
     * the Users and Posts are separated by a "~"
     */
    public String search(String searchTerm) { 
        String list = "";
        ArrayList<User> users = new ArrayList<>();
        ArrayList<Post> posts = new ArrayList<>();

        users = server.db.searchUsers(searchTerm);
        posts = server.db.searchPosts(searchTerm, 1);

        if (users.size() > 0) {
            for (User u : users) {
                list += u.toString() + "><";
            }
            list = list.substring(0, list.length() - 2);
        }

        list += "<>"; //separator between Users and Posts

        if (posts.size() > 0) {
            for (Post p : posts) {
                list += p.toString() + "><";
            }
            list = list.substring(0, list.length() - 2);
        }

        return list;
    }

    // returns string containing the user's string and all posts by the user
    public String getUser(String username) throws BadUserException {
        String list = "";

        list += server.db.getUser(username);

        ArrayList<Post> posts = server.db.searchPosts(username, 0);
        list += "<>";
        for (Post p: posts)
            list += p.toString() + "><";

        return list;
    }

    // Charles: makes an array of the data in user then changes that one part then modifies the user
    public boolean modifyUser(String newValue, int item, String username) {
        // if (!username.equals(user.getUsername()) && !username.isEmpty())
        //     return false;

        User targetedUser = null;

        if (username.isEmpty()) {
            targetedUser = this.user;
        } else {
            try {
                targetedUser = new User(server.db.getUser(username), this.server.db);
            } catch (BadUserException bue) {
                return false;
            }
        }

        //check if modifying bio
        if (item == 2) {
            return targetedUser.setBio(newValue);
        }
        //check if modifying friend
        if (item == 5) {
            return targetedUser.modifyFriend(newValue);
        }
        //check if modifying blocked
        if (item == 6) {
            return targetedUser.modifyBlocked(newValue);
        }

        String[] data = targetedUser.toString().split(",", -1);
        data[item] = newValue;
        try {
            targetedUser = new User(String.join(",", data), server.db);
        } catch (BadUserException e) {
            return false;
        }

        boolean ret = server.db.modifyUser(targetedUser.toString(), String.join(",", data));

        if (ret) {
            try {
                //user = new User(server.db.getUser(username), server.db);
                user = targetedUser;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

    // React to a post (like/dislike)
    public boolean reactPost(String postId, boolean like, boolean add) {
        return server.db.changePostRatio(postId, like, add);
    }

    // Add a comment to a post
    public boolean addComment(String postId, String comment, String user) {
        if (comment == null || comment.isEmpty())
            return false;

        return server.db.addComment(postId, comment, user);
    }

    // React to a comment (like/dislike)
    public boolean reactComment(String postId, String commentID, boolean like, boolean add) {
        return server.db.changeCommentRatio(postId, commentID, like, add);
    }

    /**
     * Ahmad
     * Charles
     *
     * Retrieves posts based on specified interests or friends.
     * The method searches the database for posts either by interests or by friends
     * depending on the method parameter. This helps to display posts tailored to the user's preferences.
     *
     * @param key an array containing either interest categories or friend usernames
     * @param method indicates the type of search (by interests or friends)
     * @return a formatted String of post data retrieved from the database,
     * with each post represented by its string format
     */
    public String getPosts(String[] key, int method) {
        ArrayList<Post> postList;

        if (method == 0) {
            postList = server.db.getPosts(key);
        } else {
            postList = server.db.getPostsByFriends(user.getFriends());
        }

        String posts = "";
        for (Post post : postList) {
            posts += post.toString() + "~";
        }

        if (posts.isEmpty()) {
            return null;
        } else {
            return posts.substring(0, posts.length() - 1);
        }
    }
}
