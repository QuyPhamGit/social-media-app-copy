/**
 * Team Project -- BadUserException
 *
 * A custom exception class for handling user-related errors in the application.
 * This exception is thrown when there is an issue related to a user,
 * such as when a user is not found in the database or when there 
 * is an invalid user operation.
 *
 * @author Quy Pham Ngo Thien, lab sec 002
 * @author Charles Pittman, lab sec 002
 *
 * @version November 3, 2024
 */
public class BadUserException extends Exception implements BadUserExceptionInterface  {
    public BadUserException(String message) {
        super(message);
    }
}
