import java.awt.Color;
import java.awt.Font;

/**
 * Team Project -- Constants
 *
 * This class just contains public static variables
 * to be accessed by all other classes as needed.
 *
 * @author Ryan Jo, lab sec 002
 *
 * @version November 3, 2024
 */
public class Constants implements ConstantsInterface {
    public static final String USERFILE = "Users.txt";
    public static final String POSTFILE = "Posts.txt";

    //JFrame Constants
    public static final int WINDOW_HEIGHT = 800;
    public static final int WINDOW_WIDTH = 1200;
    public static final int HEADER_HEIGHT = 50;
    public static final Color COLOR_DARKEST = new Color(11, 14, 20);
    public static final Color COLOR_TONED = new Color(25, 30, 31);
    public static final Color COLOR_MID = new Color(38, 38, 38);
    public static final Color COLOR_LIGHT = new Color(111, 111, 111);
    public static final Color COLOR_LIGHTEST = new Color(225, 232, 245);
    public static final Color COLOR_LIGHTMODE_LIGHTEST = new Color(235, 242, 255);

    //Fonts
    public static final Font HEADER_FONT = new Font("Droid Sans Georgian", Font.PLAIN, 18);
    public static final Font SEARCH_BAR_FONT = new Font("Droid Sans Georgian", Font.ITALIC, 13);
    public static final Font LOGIN_FONT = new Font("Droid Sans Georgian", Font.BOLD, 20);
    public static final Font SIGNUP_LOGIN_BUTTON_FONT = new Font("Droid Sans Georgian", Font.ITALIC, 13);
    public static final Font SIGNUP_LOGIN_FIELD_FONT = new Font("Droid Sans Georgian", Font.PLAIN, 13);
    public static final Font PROFILE_DISPLAYNAME_FONT = new Font("Droid Sans Georgian", Font.PLAIN, 27);
    public static final Font PROFILE_USERNAME_FONT = new Font("Droid Sans Georgian", Font.ITALIC, 12);
    public static final Font PROFILE_STATISTIC_FONT = new Font("Droid Sans Georgian", Font.PLAIN, 14);
    public static final Font PROFILE_BIO_FONT = new Font("Droid Sans Georgian", Font.PLAIN, 13);
    public static final Font POST_HEADER_FONT = new Font("Droid Sans Georgian", Font.BOLD, 30);
    public static final Font POST_CONTENT_FONT = new Font("Droid Sans Georgian", Font.BOLD, 18);
}
