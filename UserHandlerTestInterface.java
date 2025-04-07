import java.io.IOException;
/**
 * Team Project -- User Handler Test Interface
 *
 * Interface for User Handler Tests
 *
 * @author Charles Pittman, lab sec 002
 * @author Omkar Govil-Nair, lab sec 002
 *
 * @version November 17, 2024
 *
 */
public interface UserHandlerTestInterface {

    public void runTestUserHandleConstructors() throws  BadUserException;   // tests Post constructors
    public void runTestCreateAccPublishPostGetUser() throws BadUserException,
                                                            BadPostException,
                                                            IOException;  // test liking and disliking a Post
    public void runTestReactPost() throws  BadUserException,
                                           BadPostException,
                                           IOException;     // test adding, liking, disliking, removing comments
    public void runTestAddComment() throws  BadUserException, BadPostException, IOException;
    public void runTestReactComment() throws  BadUserException, BadPostException, IOException;
    public void runTestSearch() throws  BadUserException, BadPostException, IOException;
    public void runTestModifyUser() throws BadUserException, IOException;
}
