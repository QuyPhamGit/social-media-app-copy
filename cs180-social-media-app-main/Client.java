import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

/**
 * Team Project -- Social Media Client Class
 * 
 * A class that contains the main method for client-side connection. This class initializes the applications
 * JFrame as well as a socket connection to the server-side database and UserHandler. Through communicating
 * to its UserHandler thread with socket broadcasting, commands can be inputted through the GUI and returned 
 * back with all modifications and searches being handled server-side.
 * 
 * @author Ryan Jo, lab sec 002
 * @author Omkar Govil-Nair, lab sec 002
 *
 * @version November 16, 2024
 */

public class Client implements ClientInterface {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;

    /**
     * The client constructor which initializes the given socket and a reader/writer to communicate
     */
    public Client() {
        try {
            socket = new Socket(HOST, PORT);
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Couldn't connect to server!", "Error", 0);
        }

        JFrameHandler frame = new JFrameHandler(socket, this);
    }

    /**
     * A method which communicates with UserHandler to create a new account 
     * 
     * @param username
     * @param password
     * @param bio
     * @param interests
     * @return false if an issue ocurred, true elsewise
     */
    public boolean signUp(String username, String password, String bio, String[] interests) {

        String recieved;
        String interestData = "";

        for (int i = 0; i < interests.length; i++) {
            interestData += interests[i];
            if (i + 1 != interests.length) {
                interestData += "~";
            }
        }

        broadcastMessage("create" + username + ";" + password + ";" + bio + ";" + interestData);

        try {
            recieved = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (recieved == null || recieved.contains("failed")) {
            return false;
        }

        return recieved.contains("success");
    }

    /**
     *  A method which communicates with UserHandler to log a user in 
     * 
     * @param username
     * @param password
     * @return false if an issue ocurred, true elsewise
     */
    public boolean logIn(String username, String password) {
        String recieved;
        broadcastMessage("login-" + username + ";" + password);
        try {
            recieved = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (recieved == null || recieved.contains("failed")) {
            return false;
        }

        return recieved.contains("success");
    }

    /**
     * A method which communicates with UserHandler to publish a new post
     * 
     * @return false if an issue ocurred, true elsewise
     */
    public boolean post(String message, String type) {
        String recieved;
        broadcastMessage("post--" + message  + ';' + type);
        try {
            recieved = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (recieved == null || recieved.contains("failed")) {
            return false;
        }

        return recieved.contains("success");
    }

    /**
     * A method which communicates with UserHandler to search both stored users and posts
     * based on an input term and returns entries with similarities to the keyword
     * 
     * @param searchTerm
     * @return false if an issue ocurred, true elsewise
     */
    public String search(String searchTerm) {
        String recieved;
        broadcastMessage("search" + searchTerm);
        try {
            recieved = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (recieved == null || recieved.contains("failed")) {
            return null;
        }

        if (recieved.contains("success")) {
            //split recieved data into posts and users
            return recieved.substring(7);
        }

        return null;
    }

    /**
     * A method which communicates with UserHandler to search for a stored User
     * and return it's data structure
     * 
     * @param username
     * @return false if an issue ocurred, true elsewise
     */
    public String getUser(String username) {
        String recieved;
        broadcastMessage("getusr" + username);
        try {
            recieved = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (recieved == null || recieved.contains("failed")) {
            return null;
        }

        if (recieved.substring(0, 7).equals("success")) {
            return recieved.substring(7);
        }

        return null;
    }

    public String getThisUser() {
        String recieved;
        broadcastMessage("getths--");
        try {
            recieved = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (recieved == null || recieved.contains("failed")) {
            return null;
        }

        if (recieved.contains("success")) {
            return recieved.substring(7);
        }

        return null;
    }

    public boolean modifyUser(String newValue, String item, String username) {
        String recieved;
        broadcastMessage("modusr" + newValue + ";" + item + ";" + username);
        try {
            recieved = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (recieved == null || recieved.contains("failed")) {
            return false;
        }

        return recieved.contains("success");
    }

    public boolean reactToPost(String postId, boolean like, boolean add) {
        String recieved;
        broadcastMessage("reactp" + postId + ";" + like + ";" + add);
        try {
            recieved = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (recieved == null || recieved.contains("failed")) {
            return false;
        }

        return recieved.contains("success");
    }

    public boolean addComment(String postId, String comment, String user) {
        String recieved;
        broadcastMessage("addcom" + postId + ";" + comment + ";" + user);
        try {
            recieved = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (recieved == null || recieved.contains("failed")) {
            return false;
        }

        return recieved.contains("success");
    }

    public boolean reactToComment(String postId, String commentId, boolean like, boolean add) {
        String recieved;
        broadcastMessage("reactc" + postId + ";" + commentId + ";" + like + ";" + add);
        try {
            recieved = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (recieved == null || recieved.contains("failed")) { 
            return false;
        }

        return recieved.contains("success");
    }
    
    public String getPosts(String[] interests, String method) {
        String recieved;

        String keyData = "";

        for (int i = 0; i < interests.length; i++) {
            keyData += interests[i];
            if (i + 1 != interests.length) {
                keyData += "~";
            }
        }

        broadcastMessage("getpst" + keyData + ";" + method);
        try {
            recieved = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (recieved == null || recieved.contains("failed")) {
            return null;
        }

        if (recieved.contains("success")) {
            return recieved.substring(7);
        }

        return null;
    }

    public boolean logOut() {
        String recieved;

        broadcastMessage("logout");
        try {
            recieved = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return !(recieved == null || recieved.contains("invalid request"));
    }

    public void broadcastMessage(String messageToSend) {
        try {
            bw.write(messageToSend);
            bw.newLine();
            bw.flush();
        } catch (IOException ioe) {
            //closeEverything();
        }
    }

    public boolean acceptablePassword(String thePassword) {
        //requirements: must be 7 digits or more and must contain a capital letter and special character

        String acceptedSpecialChars = ". - _ ! ( ) { }";
        boolean foundSpecialCharacter = false;
        boolean foundCapital = false;

        for (int i = 0; i < thePassword.length(); i++) {
            char character = thePassword.charAt(i);
            if (acceptedSpecialChars.indexOf(character) != -1) {
                foundSpecialCharacter = true;
            }
            if (Character.isUpperCase(character)) {
                foundCapital = true;
            }
            if (character == ' ') {
                return false;
            }
        }

        return thePassword.length() >= 7 && foundCapital && foundSpecialCharacter;
    }

    public static void main(String[] args) {
        new Client();
    }
}
