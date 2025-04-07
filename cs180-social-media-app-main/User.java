import java.time.LocalDateTime;
import java.util.*;

/**
 * Team Project -- Social Media User Class
 *
 * This class represents a user encapsulating all of their respective data and methods, such as creating a new account,
 * logging-in, instantiating from data, and getter and modifier methods. This class works in tandem with Database.java
 * acting as an extension solely for User information, manipulation, and storage.
 *
 * @author Ryan Jo, lab sec 002
 * @author Omkar Govil-Nair, lab sec 002
 *
 * @version November 3, 2024
 *
 */

public class User implements UserInterface {

    private String username;
    private String id;
    private String password;
    private String bio;
    private ArrayList<String> friends = new ArrayList<String>();
    private ArrayList<String> blocked = new ArrayList<String>();
    private ArrayList<String> interests = new ArrayList<String>();
    private boolean isOnline;
    private String dateCreated;
    private String decodedPFP;

    //temporary in place of server class
    private Database database;

    /**
     * Instantiates the current user's information based on a given String of data
     * and throws a BadUserException on error
     *
     * @param data stored User data in String format
     * @param database database instance
     * @throws BadUserException
     */

    public User(String data, Database database) throws BadUserException {
        try {
            createUserFromData(data);
            this.database = database;
        } catch (Exception e) {
            throw new BadUserException(e.getMessage());
        }
    }

    /**
     * Creates a new User instance based on their inputted Username, Password, Bio, and Interests.
     * Throws a BadUserException if the username is taken or if the password is invalid
     *
     * @param username User's username
     * @param password User's password
     * @param bio User's bio
     * @param interests User's list of interests
     * @param database database instance
     * @throws BadUserException
     */
    public User(String username,
                String password,
                String bio,
                String[] interests,
                Database database) throws BadUserException {
        this.database = database;

        if (database.usernameExists(username))
            throw new BadUserException("User already exists");

        //check validity of password
        if (!acceptablePassword(password)) {
            throw new BadUserException("Password not acceptable!");
        }

        this.username = username;
        this.password = password;
        for (String i : interests) {
            this.interests.add(i);
        }
        this.bio = bio;
        String time = java.time.LocalDateTime.now().toString();
        this.dateCreated = time.substring(0, time.indexOf("T"));

        database.newUser(toString());
    }

    /**
     * Logs in a user if their inputted username matches a user in the database and if
     * their passwords match. Returns a BadUserException if the user does not exist or
     * if the password is incorrect.
     *
     * @param username User's username
     * @param password User's password
     * @param database database instance
     * @throws BadUserException
     */
    public User(String username, String password, Database database) throws BadUserException {
        this.database = database;

        String userInfo = "";

        try {
            userInfo = database.getUser(username);
        } catch (BadUserException bue) {
            throw new BadUserException("No User Found");
        }

        String correctPassword = userInfo.split(",", -1)[1]; //whichever index is for password

        if (password.equals(correctPassword)) {
            try {
                createUserFromData(userInfo);
            } catch (Exception e) {
                throw new BadUserException("Issue while creating user");
            }
        } else {
            throw new BadUserException("Incorrect Password");
        }
    }

    /**
     * Check's if the user inputted password follows acceptable password conventions such as
     * 7 or more characters, includes a special character, and has a capital letter.
     *
     * @param username password to check
     * @return A boolean representing a correct or incorrect password
     */
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

    /**
     * Switches the user to offline when they would like to log off
     */
    public void logOff() {
        this.isOnline = false;
    }

    /**
     * Getter method for the current user's username
     *
     * @return the user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Getter method for the current user's password
     *
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Getter method for the current user's biography
     *
     * @return the user's biography
     */
    public String getBio() {
        return bio;
    }

    /**
     * Getter method for the current user's creation date
     *
     * @return the user's creation date
     */
    public String getDateCreated() {
        return dateCreated;
    }

    /**
     * Getter method for the current user's list of friends
     *
     * @return the user's list of friends
     */
    public ArrayList<String> getFriends() {
        return friends;
    }

    /**
     * Getter method for the current user's list of blocked users
     *
     * @return the user's list of blocked users
     */
    public ArrayList<String> getBlocked() {
        return blocked;
    }

    /**
     * Getter method for the current user's list of interests
     *
     * @return the user's list of interests
     */
    public ArrayList<String> getInterests() {
        return interests;
    }

    /**
     * Getter method for the current user's profile image
     *
     * @return the user's decoded PFP
     */
    public String getPFP() {
        return decodedPFP;
    }

    /**
     * A method that toggles a specified User on/off the current user's friend list if they exist
     *
     * @param friend username of the friend to change
     * @return true if correctly modified, false if an issue was encountered
     */
    public boolean modifyFriend(String friend) {
        String oldData = this.toString();

        try {
            database.getUser(friend);
        } catch (BadUserException bue) {
            return false;
        }

        try {
            String[] data = oldData.split(",", -1);

            if (this.getFriends() == null) {
                this.friends.add(friend);
            } else {

                if (this.getFriends().contains(friend)) {
                    this.friends.remove(this.friends.indexOf(friend));
                } else {
                    friends.add(friend);
                }

            }

            return database.modifyUser(oldData, this.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * A method that toggles a specified User on/off the current user's blocked list if they exist
     *
     * @param blocked username of the blocked user to change
     * @return true if correctly modified, false if an issue was encountered
     */
    public boolean modifyBlocked(String theBlocked) {
        String oldData = this.toString();

        try {
            database.getUser(theBlocked);
        } catch (BadUserException bue) {
            return false;
        }

        try {
            String[] data = oldData.split(",", -1);

            if (this.getBlocked() == null) {
                this.blocked.add(theBlocked);
            } else {

                if (this.getBlocked().contains(theBlocked)) {
                    this.blocked.remove(this.blocked.indexOf(theBlocked));
                } else {
                    this.blocked.add(theBlocked);
                }

            }

            return database.modifyUser(oldData, this.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * A method that sets the current user's bio in their stored database line
     *
     * @param bio String of the new biography
     * @return true if correctly modified, false if an issue was encountered
     */
    public boolean setBio(String newBio) {
        String oldData = this.toString();

        try {
            String[] data = oldData.split(",", -1);
            String newData = "";

            data[2] = newBio;

            //rebuild the data
            int index = 0;
            for (String s : data) {
                newData += s + ((index < data.length - 1) ? "," : " ");
                index++;
            }

            this.bio = newBio;

            return database.modifyUser(oldData, newData);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * A method that sets the current user's profile picture in their stored database line
     *
     * @param pfp Base 64 image decription String
     * @return true if correctly modified, false if an issue was encountered
     */
    public boolean setPFP(String pfp) {
        String oldData = this.toString();

        try {
            String[] data = oldData.split(",", -1);
            String newData = "";

            //rebuild data
            for (int i = 0; i < 7; i++) {
                if (i < data.length && data[i] != null) {
                    newData += data[i] + ",";
                } else {
                    newData += ",";
                }
            }

            newData += pfp;
            this.decodedPFP = pfp;

            return database.modifyUser(oldData, newData);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * A method that instantiates a user's fields based on a String of data
     *
     * @param data String of data used to instantiate
     * @return true if correctly instantiated
     * @throws Exception on error instantiating values
     */
    public boolean createUserFromData(String data) throws Exception {
        String[] dataArr = data.split(",", -1);

        if (!acceptablePassword(dataArr[1])) {
            throw new Exception("incorrect password type");
        }

        this.username = dataArr[0];
        this.password = dataArr[1];
        if (!dataArr[2].isEmpty()) {
            this.bio = dataArr[2];
        }

        this.dateCreated = dataArr[3];

        if (dataArr.length > 4 && !dataArr[4].isEmpty()) {
            for (String i : dataArr[4].split("~")) {
                interests.add(i);
            }
        }
        if (dataArr.length > 5 && dataArr.length > 5 && !dataArr[5].isEmpty()) {
            for (String f : dataArr[5].split("~")) {
                friends.add(f);
            }
        }
        if (dataArr.length > 6 && dataArr.length > 6 && !dataArr[6].isEmpty()) {
            for (String b : dataArr[6].split("~")) {
                blocked.add(b);
            }
        }
        if (dataArr.length > 7 && !dataArr[7].isEmpty()) {
            decodedPFP = dataArr[7];
        }
        isOnline = true;
        return true;
    }

    /**
     * A method that compares an inputted Object and returns true if they are both Users and their usernames match
     *
     * @param o Object to compare
     * @return true if users have matching usernames, false if not
     */
    public boolean equals(Object o) {
        if (o == null || !(o instanceof User)) return false;
        User a = (User) o;
        return (a.getUsername().equals(this.username));
    }

    /**
     * A method that returns a String representation of the current user's information
     *
     * @return String of data
     */
    public String toString() {
        String ret = username + "," + password + "," + bio + "," + dateCreated + ",";
        if (interests != null) {
            for (int i = 0; i < interests.size(); i++) {
                ret += interests.get(i);
                if (i != interests.size() - 1) {
                    ret += "~";
                }
            }
        }
        ret += ",";
        if (friends != null) {
            for (int i = 0; i < friends.size(); i++) {
                ret += friends.get(i);
                if (i != friends.size() - 1) {
                    ret += "~";
                }
            }
        }
        ret += ",";
        if (blocked != null) {
            for (int i = 0; i < blocked.size(); i++) {
                ret += blocked.get(i);
                if (i != blocked.size() - 1) {
                    ret += "~";
                }
            }
        }
        ret += ",";
        if (decodedPFP != null) {
            ret += decodedPFP;
        }
        return ret;
    }
}
