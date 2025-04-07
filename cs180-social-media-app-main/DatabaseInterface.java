/**
 * Team Project -- Database Interface
 *
 * List methods which should be included in any
 * Database class with a User object.
 *
 * @author Omkar Govil-Nair, lab sec 002
 * @author Ryan Jo, lab sec 002
 * @author Quy Pham Ngo Thien, lab sec 002
 * @author Charles Pittman, lab sec 002
 *
 * @version November 3, 2024
 */
public interface DatabaseInterface {
    boolean loadFiles();                                    // loads all files into ArrayLists of object ids
    String readLine(int lineNum, int file);                 // returns data from certain line in file
    boolean writeLine(int lineNum, String data, int file);  // updates or writes a new line to the file
    String getUser(String username) throws Exception;       // returns User data string
    boolean modifyUser(String oldUser, String newUser);     // updates an existing User entry
    boolean newUser(String data);                           // adds a new line with a new User
}


