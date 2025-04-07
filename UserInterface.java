import java.util.ArrayList;
/**
 * Team Project -- User Interface
 *
 * List methods which should be included in any
 * User class
 *
 * @author Ryan Jo, lab sec 002
 *
 * @version November 3, 2024
 */
public interface UserInterface {

    //checks if the password has all necessities
    boolean acceptablePassword(String password); 

    //logs off the user
    void logOff(); 

    //returns the user's username
    String getUsername(); 

    //returns the user's password
    String getPassword(); 

    //returns the user's bio
    String getBio(); 

    //returns the user's date of creation
    String getDateCreated();

    //returns the user's list of friends
    ArrayList<String> getFriends(); 

    //returns the user's list of blocked users
    ArrayList<String> getBlocked(); 

    //returns the user's list of interests
    ArrayList<String> getInterests(); 

    //calls on the database to toggle a friend and edit the user's data respectively
    boolean modifyFriend(String friendUsername); 

    //calls on the database to toggle a blocked user and edit the user's data respectively
    boolean modifyBlocked(String blockedUsername); 

    //instantiate's all user variables based on a string of data
    boolean createUserFromData(String data) throws Exception; 

    //returns true if the passed in User has the same username
    boolean equals(Object o); 

    //returns a data representation of the User's information
    String toString(); 
}
