import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Team Project -- JFrameHandler Interface
 *
 * Lists methods which should be included in any JFrameHandler class
 * 
 * @author Ryan Jo, lab sec 002
 *
 * @version November 16, 2024
 */

public interface JFrameHandlerInterface {
    void resetFrame();
    JButton createButton(String text, ImageIcon icon, Dimension dimension, int styling);
    void addLoginPanel();
    void addSignupPanel();
    void addInterestsPanel();
    void refreshInterestList(JPanel interestListPanel, ArrayList<String> interestsArray);
    JPanel createPost(String postType, boolean minimized, Post postObj) throws BadUserException;
    String timeAgo(String postedDateTime);
    void addPostPanel(String searchTerm, String mode);
    void addHeaderPanel();
    void addProfilePanel(String username);
    void addPostWindow();
    void addMenuPanel();
    void fileChooser(String[] data);
}
