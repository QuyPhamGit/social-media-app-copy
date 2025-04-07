/**
 * Team Project -- User Tests Interface
 *
 * List the methods which should be included in any test case
 * class for a class which implements UserInterface.
 *
 * @author Omkar Govil-Nair, lab sec 002
 *
 * @version November 3, 2024
 */
public interface UserLocalTestInterface {
    // helper method to reset DB files
    public void resetFiles() throws Exception;

    // tests all User constructors
    public void runTestUserConstructors() throws Exception, BadUserException;
    
    // tests the acceptablePassword method
    public void runTestAcceptablePassword() throws Exception, BadUserException; 

    // tests if all getters return the correct information
    public void runTestGetters() throws Exception, BadUserException; 
    // tests the removal and adding of friends through modifyFriend
    public void runTestModifyFriend() throws Exception, BadUserException;

    // tests the removal and adding of blocked users through modifyBlocked
    public void runTestModifyBlocked() throws Exception, BadUserException; 

    // tests the User equals() method
    public void runTestEquals() throws Exception, BadUserException;  
    
    // tests if toString returns the correct data structure
    public void runTestToString() throws Exception, BadUserException;
}
