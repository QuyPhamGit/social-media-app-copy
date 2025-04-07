/**
 * Team Project -- Bad Post Exception Class
 *
 * This class represents an exception that is thrown when a post
 * cannot be created or is cconsidered invalid. It extends the Exception
 * class to provide specific error messaging related to post operations.
 *
 * @author Charles Pittman, lab sec 002
 * @author Quy Pham, lab sec 002
 *
 * @version November 3, 2024
 *
 */
public class BadPostException extends Exception implements BadPostExceptionInterface  {
    public BadPostException(String message) {
        super(message);
    }
}
