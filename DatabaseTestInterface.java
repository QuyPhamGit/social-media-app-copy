/**
 * Team Project -- Database Tests Interface
 *
 * List the methods which should be included in any test case
 * class for a class which implements DatabaseInterface.
 *
 * @author Omkar Govil-Nair, lab sec 002
 *
 * @version November 3, 2024
 */
public interface DatabaseTestInterface {
    public void resetFiles() throws Exception;                    // helper method to reset DB files
    public void runTestDatabaseReadWriteLine() throws Exception;  // tests reading and writing to/from file
    public void runTestDatabaseModifyLine() throws Exception;     // tests modifying an existing line
    public void runTestDatabaseLoadUsers() throws Exception;      // tests to correctly load user from file into memory
    public void runTestDatabaseLoadPosts() throws Exception;      // tests to correctly load post from file into memory
    public void runTestDataPersistence() throws Exception;        // tests data persistence if program crashes
    public void runtTestThrows() throws Exception;                // test any methods which throw exceptions
}
