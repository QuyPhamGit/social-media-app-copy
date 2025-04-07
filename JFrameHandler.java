import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64; 

/**
 * Team Project -- Client Interface
 *
 * The JFrameHandler class takes care of GUI related visuals and inputs. By communicating
 * with a connected Client instance, this GUI can directly recieve input from the user and 
 * respond adaptively and visually by getting information straight from the server-side database.
 * 
 * @author Ryan Jo, lab sec 002
 *
 * @version November 17, 2024
 */

public class JFrameHandler extends JComponent implements JFrameHandlerInterface {

    private ArrayList<String> interestArray = new ArrayList<>();

    private JButton loginButton;
    private JButton signupButton;
    private JButton homeButton;
    private JButton profileButton;
    private JButton loginPageButton;
    private JButton signupPageButtonOne;
    private JButton signupPageButtonFinal;
    private JButton friendButton;
    private JButton menuButton;
    private JButton logOffButton;
    private JButton createPostButton;
    private JButton forYouButton;
    private JButton followingButton;

    private JPasswordField passwordField;
    private JTextField usernameField;
    private JTextField interestsField;
    private JTextArea bioField;

    private boolean isEditing;
    private boolean isOtherUserBlocked;
    private boolean inForYouPage = true;

    private JFrame menuFrame;
    private JFrame frame;
    private Container content;

    private ArrayList<String> lpList = new ArrayList<>();
    private ArrayList<String> dpList = new ArrayList<>();
    private ArrayList<String> lcList = new ArrayList<>();
    private ArrayList<String> dcList = new ArrayList<>();

    private ImageIcon upvoteIcon;
    private ImageIcon upvoteSelectedIcon;
    private ImageIcon downvoteIcon;
    private ImageIcon downvoteSelectedIcon;

    private Color darkestColor = Constants.COLOR_DARKEST.darker();
    private Color darkColor = Constants.COLOR_DARKEST;
    private Color tonedColor = Constants.COLOR_TONED;
    private Color midColor = Constants.COLOR_MID;
    private Color lightColor = Constants.COLOR_LIGHT;
    private Color lightestColor = Constants.COLOR_LIGHTEST;

    private Socket socket;
    private Client client;

    //temporary
    private Database tempDatabase;

    /**
     * Constructor which initializes the JFrame and content pane, as well as
     * adding the login JPanel which shows on boot-up
     */
    public JFrameHandler(Socket socket, Client client) {

        //temporary
        tempDatabase = new Database(Constants.USERFILE, Constants.POSTFILE);
            
        this.socket = socket;
        this.client = client;

        isEditing = false;
        isOtherUserBlocked = false;

        frame = new JFrame("Social Media App");
        
        content = frame.getContentPane();
        content.setLayout(new BorderLayout());
        content.setBackground(tonedColor);

        frame.setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);

        Image upvoteImage = new ImageIcon("." + File.separator + "icons" + File.separator + 
                                            "upvote-icon.png").getImage();
        Image resizedUpvoteImage = upvoteImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        upvoteIcon = new ImageIcon(resizedUpvoteImage);

        Image upvoteSelectedImage = new ImageIcon("." + File.separator + "icons" + File.separator + 
                                            "upvote-selected-icon.png").getImage();
        Image resizedUpvoteSelectedImage = upvoteSelectedImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        upvoteSelectedIcon = new ImageIcon(resizedUpvoteSelectedImage);

        Image downvoteImage = new ImageIcon("." + File.separator + "icons" + File.separator + 
                                            "downvote-icon.png").getImage();
        Image resizedDownvoteImage = downvoteImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        downvoteIcon = new ImageIcon(resizedDownvoteImage);

        Image downvoteSelectedImage = new ImageIcon("." + File.separator + "icons" + File.separator + 
                                            "downvote-selected-icon.png").getImage();
        Image resizedDownvoteSelectedImage = downvoteSelectedImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        downvoteSelectedIcon = new ImageIcon(resizedDownvoteSelectedImage);

        addLoginPanel();
    }

    public void resetFrame() {
        frame.getContentPane().removeAll();
        content.setBackground(tonedColor);
        frame.revalidate();
        frame.repaint();
    }
    
    /**
     * A helper method which creates preset JButton stylings 
     * 
     * @param text button display text
     * @param dimension the height and width dimensions
     * @param styling determines the type of styling from 0 to ___
     */
    public JButton createButton(String text, ImageIcon icon, Dimension dimension, int styling) {

        JButton button;

        switch (styling) {

            //login button
            case 0:
                button = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        if (getModel().isPressed()) {
                            g.setColor(lightestColor.darker());  // Darker shade for pressed effect
                        } else if (getModel().isRollover()) {
                            g.setColor(lightestColor.brighter());  // Lighter shade for hover effect
                        } else {
                            g.setColor(lightestColor);  // Default color
                        }
                        g.fillRect(0, 0, getWidth(), getHeight());
                        super.paintComponent(g);
                    }
                };
                button.setPreferredSize(dimension);
                button.setFont(Constants.HEADER_FONT);
                button.setForeground(tonedColor);
                button.setText(text);
                button.setBorder(BorderFactory.createLineBorder(midColor, 3));
                button.setBorderPainted(true);
                button.setFocusPainted(false);
                button.setContentAreaFilled(false);
                break;
                
            //header button
            case 1:
                button = new JButton(icon) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        if (getModel().isPressed()) {
                            g.setColor(midColor.darker());  // Darker shade for pressed effect
                        } else if (getModel().isRollover()) {
                            g.setColor(midColor.brighter());  // Lighter shade for hover effect
                        } else {
                            g.setColor(darkColor);  // Default color
                        }
                        g.fillRect(0, 0, getWidth(), getHeight());
                        super.paintComponent(g);
                    }
                };
                    
                button.setPreferredSize(dimension);
                button.setFont(Constants.HEADER_FONT);
                button.setForeground(lightestColor);
                button.setBorder(BorderFactory.createLineBorder(midColor, 3));
                button.setBorderPainted(true);
                button.setFocusPainted(false);
                button.setContentAreaFilled(false);
                break;

            //profile button (friend/unfriend)
            case 2:
                button = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        if (getModel().isPressed()) {
                            g.setColor(lightestColor.darker());  // Darker shade for pressed effect
                        } else if (getModel().isRollover()) {
                            g.setColor(lightestColor.brighter());  // Lighter shade for hover effect
                        } else {
                            g.setColor(lightestColor);  // Default color
                        }
                        g.fillRect(0, 0, getWidth(), getHeight());
                        super.paintComponent(g);
                    }
                };
                button.setPreferredSize(dimension);
                button.setFont(Constants.HEADER_FONT);
                button.setForeground(tonedColor);
                button.setText(text);
                button.setBorder(BorderFactory.createLineBorder(midColor, 3));
                button.setBorderPainted(true);
                button.setFocusPainted(false);
                button.setContentAreaFilled(false);
                break;
            
                
            //Setting option
            case 3:
            button = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    if (getModel().isPressed()) {
                        g.setColor(lightestColor.darker()); 
                    } else if (getModel().isRollover()) {
                        g.setColor(lightestColor.brighter()); 
                    } else {
                        g.setColor(lightestColor); 
                    }
                    g.fillRect(0, 0, getWidth(), getHeight());
                    super.paintComponent(g);
                }
            };
            button.setPreferredSize(dimension);
            button.setFont(Constants.HEADER_FONT);
            button.setForeground(tonedColor);
            button.setText(text);
            button.setBorder(BorderFactory.createLineBorder(midColor, 3));
            button.setBorderPainted(true);
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
            break;

            //Post filter header
            case 4:
            button = new JButton();
            button.setPreferredSize(dimension);
            button.setOpaque(false);
            button.setFont(Constants.HEADER_FONT);
            button.setForeground(lightColor);
            button.setText(text);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
            break;
            default:
                button = new JButton(text);
                break;
        }

        return button;
    } 

    /**
     * A method which creates and applies a login page to the JFrame
     */
    public void addLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        loginPanel.setBackground(tonedColor);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        //username label
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(Constants.LOGIN_FONT);
        usernameLabel.setForeground(lightestColor);
        loginPanel.add(usernameLabel, gbc);

        //username text field
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        usernameField.setFont(Constants.SIGNUP_LOGIN_FIELD_FONT);
        loginPanel.add(usernameField, gbc);

        //password label
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(Constants.LOGIN_FONT);
        passwordLabel.setForeground(lightestColor);
        loginPanel.add(passwordLabel, gbc);

        //password text field
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setFont(Constants.SIGNUP_LOGIN_FIELD_FONT);
        loginPanel.add(passwordField, gbc);

        //login button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; 
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton = createButton("Log In", null, new Dimension(400, 50), 0);
        loginButton.addActionListener(actionListener);
        loginPanel.add(loginButton, gbc);

        //sign-up label
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel signupLabel = new JLabel("No Account?");
        signupLabel.setFont(Constants.SIGNUP_LOGIN_BUTTON_FONT);
        signupLabel.setForeground(lightestColor);
        loginPanel.add(signupLabel, gbc);

        //signup button
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1; 
        gbc.weightx = 0; 
        gbc.anchor = GridBagConstraints.SOUTH;
        signupButton = createButton("Sign Up", null, new Dimension(10, 30), 0);
        signupButton.addActionListener(actionListener);
        loginPanel.add(signupButton, gbc);

        //outer panel that gives padding
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(darkColor);
        outerPanel.setBorder(BorderFactory.createEmptyBorder(200, 300, 200, 300));
        outerPanel.add(loginPanel, BorderLayout.CENTER);

        content.add(outerPanel, BorderLayout.CENTER);

        frame.revalidate();
        frame.repaint();
    }

    /**
     * A method which creates and applies a signup page to the JFrame
     */
    public void addSignupPanel() {
        JPanel signupPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        signupPanel.setBackground(tonedColor);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        //username label
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(Constants.LOGIN_FONT);
        usernameLabel.setForeground(lightestColor);
        signupPanel.add(usernameLabel, gbc);

        //username text field
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        usernameField.setFont(Constants.SIGNUP_LOGIN_FIELD_FONT);
        signupPanel.add(usernameField, gbc);

        //password Label
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(Constants.LOGIN_FONT);
        passwordLabel.setForeground(lightestColor);
        signupPanel.add(passwordLabel, gbc);

        //password text field
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setFont(Constants.SIGNUP_LOGIN_FIELD_FONT);
        signupPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel bioLabel = new JLabel("Biography:");
        bioLabel.setFont(Constants.LOGIN_FONT);
        bioLabel.setForeground(lightestColor);
        signupPanel.add(bioLabel, gbc);

        //bio text area (NEED TO DECORATE)
        gbc.gridx = 1;
        gbc.gridheight = 2;
        bioField = new JTextArea(5, 15); 
        bioField.setFont(Constants.SIGNUP_LOGIN_FIELD_FONT);
        bioField.setLineWrap(true);
        bioField.setWrapStyleWord(true); 

        JScrollPane scrollPane = new JScrollPane(bioField);
        scrollPane.setPreferredSize(new Dimension(200, 100)); 

        signupPanel.add(scrollPane, gbc);

        //signup button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        // gbc.anchor = GridBagConstraints.CENTER;
        signupPageButtonOne = createButton("Sign Up", null, new Dimension(400, 50), 0);
        signupPageButtonOne.addActionListener(actionListener);
        signupPanel.add(signupPageButtonOne, gbc);

        //log-in label
        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel signupLabel = new JLabel("Have an Account?");
        signupLabel.setFont(Constants.SIGNUP_LOGIN_BUTTON_FONT);
        signupLabel.setForeground(lightestColor);
        signupPanel.add(signupLabel, gbc);

        //login button
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 1; 
        loginPageButton = createButton("Log In", null, new Dimension(200, 30), 0);
        loginPageButton.addActionListener(actionListener);
        signupPanel.add(loginPageButton, gbc);

        //outer panel that gives padding
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(darkColor);
        outerPanel.setBorder(BorderFactory.createEmptyBorder(150, 300, 150, 300));
        outerPanel.add(signupPanel, BorderLayout.CENTER);

        content.add(outerPanel, BorderLayout.CENTER);

        frame.revalidate();
        frame.repaint();
    }

    public void addInterestsPanel() {
        JPanel interestsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        interestsPanel.setBackground(tonedColor);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        //header
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.gridy = 0;
        JLabel interestsLabel = new JLabel("Enter Your Interests");
        interestsLabel.setFont(Constants.LOGIN_FONT);
        interestsLabel.setForeground(lightestColor);
        interestsPanel.add(interestsLabel, gbc);

        //interest text area
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        interestsField = new JTextField("Press Enter to Submit an Interest"); 
        interestsField.setFont(Constants.SIGNUP_LOGIN_FIELD_FONT);

        //panel to display interests below field in a flow display
        JPanel interestListPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        interestListPanel.setPreferredSize(new Dimension(380, 0));

        //add submissions
        interestsField.addActionListener(e -> {
            String interestInput = interestsField.getText();
            interestArray.add(interestInput);
            interestsField.setText("");

            refreshInterestList(interestListPanel, interestArray);
        });

        interestsPanel.add(interestsField, gbc);

        JScrollPane scrollPane = new JScrollPane(interestListPanel);
        scrollPane.setPreferredSize(new Dimension(400, 150));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        interestsPanel.add(scrollPane, gbc);

        //signup button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        // gbc.anchor = GridBagConstraints.CENTER;
        signupPageButtonFinal = createButton("Sign Up", null, new Dimension(400, 50), 0);
        signupPageButtonFinal.addActionListener(actionListener);
        interestsPanel.add(signupPageButtonFinal, gbc);

        //outer panel that gives padding
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(darkColor);
        outerPanel.setBorder(BorderFactory.createEmptyBorder(150, 300, 150, 300));
        outerPanel.add(interestsPanel, BorderLayout.CENTER);

        content.add(outerPanel, BorderLayout.CENTER);

        frame.revalidate();
        frame.repaint();
    }

    public void refreshInterestList(JPanel interestListPanel, ArrayList<String> theInterestArray) {
        interestListPanel.removeAll(); 
    
        for (String interest : theInterestArray) {
            JLabel interestLabel = new JLabel(interest);
            interestLabel.setFont(Constants.SIGNUP_LOGIN_BUTTON_FONT);
            interestLabel.setOpaque(true);
            interestLabel.setBackground(lightestColor);
            interestLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    
            interestLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    theInterestArray.remove(interest);
                    refreshInterestList(interestListPanel, theInterestArray);
                }
            });
    
            interestListPanel.add(interestLabel);
        }
    

        interestListPanel.revalidate();
        interestListPanel.repaint();
    }

    public String timeAgo(String postedDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime postTime = LocalDateTime.parse(postedDateTime, formatter);

        LocalDateTime now = LocalDateTime.now();

        long years = ChronoUnit.YEARS.between(postTime, now);
        if (years > 0) return years + "y";

        long months = ChronoUnit.MONTHS.between(postTime, now);
        if (months > 0) return months + "M";

        long days = ChronoUnit.DAYS.between(postTime, now);
        if (days > 0) return days + "D";

        long hours = ChronoUnit.HOURS.between(postTime, now);
        if (hours > 0) return hours + "h";

        long minutes = ChronoUnit.MINUTES.between(postTime, now);
        if (minutes > 0) return minutes + "m";

        long seconds = ChronoUnit.SECONDS.between(postTime, now);
        return seconds + "s";
    }

    public JPanel createUserPanel(String userData) {

        JPanel userPanel = new JPanel();
        Dimension fixedSize = new Dimension(500, 50); 
        userPanel.setPreferredSize(fixedSize);
        userPanel.setMinimumSize(fixedSize);
        userPanel.setMaximumSize(fixedSize);
        userPanel.setBackground(tonedColor); 
        userPanel.setLayout(new BorderLayout(5, 5));
        
        String[] userDataArr = userData.split(",", -1);
        String username = userDataArr[0];
        User user = null;
    
        try {
            user = new User(userData, null);
        } catch (BadUserException e) {
            e.printStackTrace();
            return null;
        }
    
        ImageIcon profileImageIcon;
        String pfp = user.getPFP();
    
        if (pfp == null || pfp.isEmpty()) {
            profileImageIcon = new ImageIcon("." + File.separator + "icons" + File.separator + "default-profile-picture.jpg");
        } else {
            byte[] decodedBytes = Base64.getDecoder().decode(user.getPFP().trim());
            profileImageIcon = new ImageIcon(decodedBytes);
        }
    
        Image profileImage = profileImageIcon.getImage();
        Image resizedImage = profileImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        profileImageIcon = new ImageIcon(resizedImage);

        JLabel profilePictureLabel = new JLabel(profileImageIcon);
        profilePictureLabel.setPreferredSize(new Dimension(40, 40));
        profilePictureLabel.setBackground(darkestColor);
        
    
        JLabel usernameLabel = new JLabel("@" + username);
        usernameLabel.setFont(Constants.PROFILE_STATISTIC_FONT);
        usernameLabel.setPreferredSize(new Dimension(300, 50));
        usernameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        usernameLabel.setForeground(lightestColor);
        usernameLabel.setBackground(darkestColor);
    
        userPanel.add(profilePictureLabel, BorderLayout.WEST);
    
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout());
        textPanel.setBackground(darkestColor);
        textPanel.setPreferredSize(new Dimension(450, 50));

        textPanel.add(usernameLabel, BorderLayout.NORTH);
        userPanel.add(textPanel, BorderLayout.CENTER);

        userPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                resetFrame();
                addHeaderPanel();
                addProfilePanel(username);
            }
    
            @Override
            public void mouseEntered(MouseEvent e) {
                userPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                userPanel.setForeground(lightColor);
            }
    
            @Override
            public void mouseExited(MouseEvent e) {
                userPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                userPanel.setForeground(tonedColor);
            }
        });
    
        return userPanel;
    }
    
    public JPanel createPost(String postType, boolean minimized, Post postObj) {

        JPanel post = new JPanel();
    
        String postTitle;
        String postMessage;

        if (postObj.getMessage().contains("<->")) {
            postTitle = postObj.getMessage().split("<->", -1)[0].replace("\\n", "\n").replace("\\|", ",");
            postMessage = postObj.getMessage().split("<->", -1)[1].replace("\\n", "\n").replace("\\|", ",");
        } else {
            postTitle = postObj.getMessage().replace("\\n", "\n").replace("\\|", ",");
            postMessage = "";
        }
        
        String postId = postObj.getPostID();
        String postCreator = postObj.getPosterUsername();
        String postTime = timeAgo(postObj.getTimestamp());
        String postLikes = Integer.toString(postObj.getLikes());
        String postDislikes = Integer.toString(postObj.getDislikes());
        User user;
    
        try {
            user = new User(client.getUser(postCreator).split("<>")[0], null);
        } catch (Exception e) {
            return null;
        }

        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BorderLayout());
        profilePanel.setBackground(darkestColor);
    
        ImageIcon profileImageIcon;
        String pfp = user.getPFP();
    
        if (pfp == null || pfp.isEmpty()) {
            profileImageIcon = new ImageIcon("." + File.separator + "icons" + File.separator + "default-profile-picture.jpg");
        } else {
            byte[] decodedBytes = Base64.getDecoder().decode(user.getPFP().trim());
            profileImageIcon = new ImageIcon(decodedBytes);
        }
    
        Image profileImage = profileImageIcon.getImage();
        Image resizedImage = profileImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        profileImageIcon = new ImageIcon(resizedImage);
    
        JButton postProfileButton = new JButton(profileImageIcon);
        postProfileButton.setPreferredSize(new Dimension(50, 50));
        postProfileButton.setFocusPainted(false);
        postProfileButton.setBorderPainted(false);
        postProfileButton.setContentAreaFilled(false);
    
        postProfileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetFrame();
                addHeaderPanel();
                addProfilePanel(postCreator);
            }
        });
    
        JLabel usernameLabel = new JLabel("@" + postCreator);
        usernameLabel.setFont(Constants.PROFILE_STATISTIC_FONT);
        usernameLabel.setForeground(lightColor);
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
    
        profilePanel.add(postProfileButton, BorderLayout.NORTH);
        profilePanel.add(usernameLabel, BorderLayout.SOUTH);
    
        JLabel dateLabel = new JLabel(postTime);
        dateLabel.setForeground(lightestColor);
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
    
        switch (postType) {
            case "default-post":
                post.setLayout(new GridBagLayout());
                post.setBackground(darkestColor);
                GridBagConstraints gbc = new GridBagConstraints();
            
                gbc.insets = new Insets(10, 10, 10, 10);
            
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.gridheight = 2; 
                gbc.anchor = GridBagConstraints.CENTER;
                post.add(dateLabel, gbc);
            
                gbc.gridx = 1;
                gbc.gridy = 0;
                gbc.gridheight = 1;
                gbc.anchor = GridBagConstraints.CENTER;
                post.add(profilePanel, gbc);
            
                JLabel postHeader = new JLabel(postTitle);
                postHeader.setFont(Constants.POST_HEADER_FONT);
                postHeader.setForeground(lightestColor);
                gbc.gridx = 2; 
                gbc.gridy = 0; 
                gbc.gridwidth = 2;  
                post.add(postHeader, gbc);
            
                JLabel postContent = new JLabel(postMessage);
                postContent.setFont(Constants.POST_CONTENT_FONT);
                postContent.setForeground(lightColor);
                gbc.gridx = 2;
                gbc.gridy = 1; 
                gbc.gridwidth = 2;  
                post.add(postContent, gbc);
            
                JPanel votePanel = new JPanel(new GridLayout(2, 1, 0, 5)); 
                votePanel.setOpaque(false); 

                JPanel upvoteRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0)); 
                upvoteRow.setOpaque(false);

                JButton upvoteButton = new JButton((lpList.contains(postId)) ? upvoteSelectedIcon : upvoteIcon);
                upvoteButton.setFocusPainted(false);
                upvoteButton.setContentAreaFilled(false);
                upvoteButton.setBorderPainted(false);
                upvoteRow.add(upvoteButton); 

                JLabel likesLabel = new JLabel(postLikes);
                likesLabel.setFont(Constants.POST_CONTENT_FONT);
                likesLabel.setForeground(lightestColor);
                upvoteRow.add(likesLabel);

                JPanel downvoteRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
                downvoteRow.setOpaque(false);

                JButton downvoteButton = new JButton((dpList.contains(postId)) ? downvoteSelectedIcon : downvoteIcon);
                downvoteButton.setFocusPainted(false);
                downvoteButton.setContentAreaFilled(false);
                downvoteButton.setBorderPainted(false);
                downvoteRow.add(downvoteButton);

                JLabel dislikesLabel = new JLabel(postDislikes);
                dislikesLabel.setFont(Constants.POST_CONTENT_FONT);
                dislikesLabel.setForeground(lightestColor);
                downvoteRow.add(dislikesLabel);

                upvoteButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (!lpList.contains(postId)) {
                            lpList.add(postId);
                            client.reactToPost(postId, true, true);
                            likesLabel.setText(Integer.toString(Integer.parseInt(likesLabel.getText()) + 1));
                            upvoteButton.setIcon(upvoteSelectedIcon);

                            if (dpList.contains(postId)) {
                                dislikesLabel.setText(Integer.toString(Integer.parseInt(dislikesLabel.getText()) - 1));
                                client.reactToPost(postId, false, false);
                                dpList.remove(postId);
                                downvoteButton.setIcon(downvoteIcon);
                            }              
                        } else {
                            lpList.remove(postId);
                            client.reactToPost(postId, true, false);
                            likesLabel.setText(Integer.toString(Integer.parseInt(likesLabel.getText()) - 1));
                            upvoteButton.setIcon(upvoteIcon);
                        }
                    }
                });

                downvoteButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (!dpList.contains(postId)) {
                            dpList.add(postId);
                            client.reactToPost(postId, false, true);
                            dislikesLabel.setText(Integer.toString(Integer.parseInt(dislikesLabel.getText()) + 1));
                            downvoteButton.setIcon(downvoteSelectedIcon);

                            if (lpList.contains(postId)) {
                                likesLabel.setText(Integer.toString(Integer.parseInt(likesLabel.getText()) - 1));
                                client.reactToPost(postId, true, false);
                                lpList.remove(postId);
                                upvoteButton.setIcon(upvoteIcon);
                            }              
                        } else {
                            dpList.remove(postId);
                            client.reactToPost(postId, false, false);
                            dislikesLabel.setText(Integer.toString(Integer.parseInt(likesLabel.getText()) - 1));
                            downvoteButton.setIcon(downvoteIcon);
                        }
                    }
                });

                votePanel.add(upvoteRow);
                votePanel.add(downvoteRow);

                gbc.gridx = 4;
                gbc.gridy = 0;
                gbc.gridheight = 2;
                gbc.anchor = GridBagConstraints.CENTER;
                post.add(votePanel, gbc);

                post.setPreferredSize(new Dimension(500, 300));
                break;
            default:
                break;
        }
    
        post.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                resetFrame();
                addHeaderPanel();
                addPostPage(postObj);
            }
    
            @Override
            public void mouseEntered(MouseEvent e) {
                post.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
    
            @Override
            public void mouseExited(MouseEvent e) {
                post.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });

        return post;
    }

    public JPanel createComment(String[] commentInfo, String postId) {

        JPanel comment = new JPanel();

        String creator = commentInfo[0];
        String commentId = commentInfo[1];
        String likes = commentInfo[2];
        String dislikes = commentInfo[3];
        String message = commentInfo[4];

        String pfp = client.getUser(creator).split("<>")[0].split(",", -1)[7];
    
        ImageIcon profileImageIcon;
    
        if (pfp == null || pfp.equals("")) {
            profileImageIcon = new ImageIcon("." + File.separator + "icons" + File.separator + "default-profile-picture.jpg");
        } else {
            byte[] decodedBytes = Base64.getDecoder().decode(pfp.trim());
            profileImageIcon = new ImageIcon(decodedBytes);
        }
    
        Image profileImage = profileImageIcon.getImage();
        Image resizedImage = profileImage.getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        profileImageIcon = new ImageIcon(resizedImage);

        JPanel votePanel = new JPanel(new GridLayout(2, 1, 0, 5)); 
        votePanel.setOpaque(false); 

        JPanel upvoteRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0)); 
        upvoteRow.setOpaque(false);

        JButton upvoteButton = new JButton((lpList.contains(commentId)) ? upvoteSelectedIcon : upvoteIcon);
        upvoteButton.setFocusPainted(false);
        upvoteButton.setContentAreaFilled(false);
        upvoteButton.setBorderPainted(false);
        upvoteRow.add(upvoteButton); 

        JLabel likesLabel = new JLabel(likes);
        likesLabel.setFont(Constants.POST_CONTENT_FONT);
        likesLabel.setForeground(lightestColor);
        upvoteRow.add(likesLabel);

        JPanel downvoteRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        downvoteRow.setOpaque(false);

        JButton downvoteButton = new JButton((dpList.contains(commentId)) ? downvoteSelectedIcon : downvoteIcon);
        downvoteButton.setFocusPainted(false);
        downvoteButton.setContentAreaFilled(false);
        downvoteButton.setBorderPainted(false);
        downvoteRow.add(downvoteButton);

        JLabel dislikesLabel = new JLabel(dislikes);
        dislikesLabel.setFont(Constants.POST_CONTENT_FONT);
        dislikesLabel.setForeground(lightestColor);
        downvoteRow.add(dislikesLabel);

        upvoteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!lcList.contains(commentId)) {
                    lcList.add(commentId);
                    client.reactToComment(postId, commentId, true, true);
                    likesLabel.setText(Integer.toString(Integer.parseInt(likesLabel.getText()) + 1));
                    upvoteButton.setIcon(upvoteSelectedIcon);

                    if (dcList.contains(commentId)) {
                        dislikesLabel.setText(Integer.toString(Integer.parseInt(dislikesLabel.getText()) - 1));
                        client.reactToComment(postId, commentId, false, false);
                        dcList.remove(commentId);
                        downvoteButton.setIcon(downvoteIcon);
                    }              
                } else {
                    lcList.remove(commentId);
                    client.reactToComment(postId, commentId, true, false);
                    likesLabel.setText(Integer.toString(Integer.parseInt(likesLabel.getText()) - 1));
                    upvoteButton.setIcon(upvoteIcon);
                }
            }
        });

        downvoteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!dcList.contains(commentId)) {
                    dcList.add(commentId);
                    client.reactToComment(postId, commentId, false, true);
                    dislikesLabel.setText(Integer.toString(Integer.parseInt(dislikesLabel.getText()) + 1));
                    downvoteButton.setIcon(downvoteSelectedIcon);

                    if (lcList.contains(commentId)) {
                        likesLabel.setText(Integer.toString(Integer.parseInt(likesLabel.getText()) - 1));
                        client.reactToComment(postId, commentId, true, false);
                        lcList.remove(commentId);
                        upvoteButton.setIcon(upvoteIcon);
                    }              
                } else {
                    dcList.remove(commentId);
                    client.reactToComment(postId, commentId, false, false);
                    dislikesLabel.setText(Integer.toString(Integer.parseInt(dislikesLabel.getText()) - 1));
                    downvoteButton.setIcon(downvoteIcon);
                }
            }
        });

        votePanel.add(upvoteRow);
        votePanel.add(downvoteRow);
    
        JButton commentProfileButton = new JButton(profileImageIcon);
        commentProfileButton.setPreferredSize(new Dimension(35, 35));
        commentProfileButton.setFocusPainted(false);
        commentProfileButton.setBorderPainted(false);
        commentProfileButton.setContentAreaFilled(false);
    
        commentProfileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetFrame();
                addHeaderPanel();
                addProfilePanel(creator);
            }
        });

        JLabel usernameLabel = new JLabel("@" + creator);
        usernameLabel.setFont(Constants.PROFILE_STATISTIC_FONT);
        usernameLabel.setForeground(lightColor);
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
    
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BorderLayout());
        profilePanel.setBackground(darkestColor);
        profilePanel.add(commentProfileButton, BorderLayout.NORTH);
        profilePanel.add(usernameLabel, BorderLayout.SOUTH);

        comment.setLayout(new GridBagLayout());
        comment.setBackground(darkestColor);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        comment.add(profilePanel, gbc);

        JLabel commentMessage = new JLabel(message);
        commentMessage.setFont(Constants.POST_CONTENT_FONT);
        commentMessage.setForeground(lightColor);
        gbc.gridx = 1;
        gbc.gridy = 1; 
        gbc.gridwidth = 3;  
        comment.add(commentMessage, gbc);

        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        comment.add(votePanel, gbc);

        comment.setPreferredSize(new Dimension(500, 200));

        return comment;
    }
    

    /**
     * A method which creates and applies a post JPanel to the JFrame which contains a scrollable feed of Posts
     * 
     * @param searchTerm the text entered in the search bar that sorts the displayed posts
     * @param mode "for you" shows the for you page, "following" shows the following page
     */
    public void addPostPanel(String searchTerm, String mode) {

        //type: 0 = default recommended posts, 1 = searched posts

        try {

            ArrayList<String> friendsList = new ArrayList<>();
            ArrayList<String> blockedList = new ArrayList<>();
                
            if (mode.equals("following")) {
                for (String friendedUser : client.getThisUser().split(",", -1)[5].split("~")) {
                    friendsList.add(friendedUser);
                }
            } else {
                for (String blockedUser : client.getThisUser().split(",", -1)[6].split("~")) {
                    blockedList.add(blockedUser);
                }
            }

            byte[] imageBytes = Files.readAllBytes(Paths.get("." + File.separator + "icons" + File.separator + "Temp_Profile_Picture.jpg"));
            String encodedString = Base64.getEncoder().encodeToString(imageBytes);

            JPanel postPanel = new JPanel();
            postPanel.setLayout(new BoxLayout(postPanel, BoxLayout.PAGE_AXIS));
            postPanel.setBackground(darkColor);
            postPanel.setBorder(null);

            JPanel filterPanel = new JPanel(new GridBagLayout());
            filterPanel.setPreferredSize(new Dimension(500, 50));
            filterPanel.setBackground(darkestColor);

            filterPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            //decorate these buttons
            forYouButton = createButton("For You", null, new Dimension(200, 20), 4);
            forYouButton.addActionListener(actionListener);
            followingButton = createButton("Following", null, new Dimension(200, 20), 4);
            followingButton.addActionListener(actionListener);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 10, 0, 10);
            gbc.gridx = 0;
            filterPanel.add(forYouButton, gbc);

            gbc.gridx = 1;
            filterPanel.add(followingButton, gbc);

            JScrollPane scrollPane = new JScrollPane(postPanel);
            scrollPane.setPreferredSize(new Dimension(600, 400));
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            scrollPane.getVerticalScrollBar().setBlockIncrement(100);
            scrollPane.setBorder(null);

            if (searchTerm.equals("")) {
                String[] interests = client.getThisUser().split(",", -1)[4].split("~");
                String postString = client.getPosts(interests, "0");

                if (postString != null) {
                    String[] postDataArr = postString.split("~");
                    
                    for (int i = 0; i < postDataArr.length; i++) {
                        try {
                            String postCreator = postDataArr[i].split(",", -1)[2];
                            if (mode.equals("following")) {
                                if (postCreator == null || !friendsList.contains(postCreator)) {
                                    continue;
                                }
                            } else {
                                if (blockedList.contains(postCreator)) {
                                    continue;
                                }
                            }
                            if (postDataArr[i].length() <= 0) {
                                continue;
                            }
                            JPanel post = createPost("default-post", false, new Post(postDataArr[i], null));  
                            post.setAlignmentX(Component.LEFT_ALIGNMENT);
                            postPanel.add(post);
                            postPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                String entries = client.search(searchTerm);

                if (entries != null && entries.split("<>").length > 1) {
                    String[] postDataArr = entries.split("<>", -1)[1].split("><");
                    String[] userDataArr = entries.split("<>", -1)[0].split("><");
                    
                    //add users first
                    for (int i = 0; i < userDataArr.length; i++) {
                        try {
                            if (mode.equals("following")) {
                                String foundUser = userDataArr[i].split(",", -1)[0];
                                if (foundUser == null || !friendsList.contains(foundUser)) {
                                    continue;
                                }
                            }
                            if (userDataArr[i] == null || userDataArr[i].isEmpty()) {
                                continue;
                            }
                            if (userDataArr[i].length() <= 0) {
                                continue;
                            }
                            JPanel userPanel = createUserPanel(userDataArr[i]);
                            userPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                            postPanel.add(userPanel);
                            postPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    //add posts second
                    for (int i = 0; i < postDataArr.length; i++) {
                        try {
                            String postCreator = postDataArr[i].split(",", -1)[2];
                            if (mode.equals("following")) {
                                if (postCreator == null || !friendsList.contains(postCreator)) {
                                    continue;
                                }
                            } else {
                                if (blockedList.contains(postCreator)) {
                                    continue;
                                }       
                            }
                            if (postDataArr[i].length() <= 0) {
                                continue;
                            }
                            JPanel post = createPost("default-post", false, new Post(postDataArr[i], null));
                            post.setAlignmentX(Component.LEFT_ALIGNMENT);
                            postPanel.add(post);
                            postPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            
                        
            postPanel.add(Box.createVerticalGlue());

            JPanel outerPanel = new JPanel(new BorderLayout());
            outerPanel.setBorder(BorderFactory.createEmptyBorder(0, 150, 0, 150));
            outerPanel.setBackground(tonedColor);
            outerPanel.add(filterPanel, BorderLayout.NORTH);
            outerPanel.add(scrollPane, BorderLayout.CENTER);

            content.add(outerPanel, BorderLayout.CENTER);

            frame.revalidate();
            frame.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    /**
     * A method which creates and applies a header JPanel to the JFrame
     */
    public void addHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setSize(new Dimension(Constants.WINDOW_WIDTH, Constants.HEADER_HEIGHT));
        headerPanel.setBackground(darkColor);

        JPanel headerButtonPanel = new JPanel(new GridBagLayout());
        headerButtonPanel.setLayout(new FlowLayout());
        headerButtonPanel.setBackground(darkColor);

        JPanel searchBarPanel = new JPanel(new BorderLayout());
        searchBarPanel.setBackground(darkColor); 
        searchBarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //creating home icon
        ImageIcon homeIcon = new ImageIcon("." + File.separator + "icons" + File.separator + "home-icon.png");
        Image homeScaledImage = homeIcon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH); 
        ImageIcon homeScaledIcon = new ImageIcon(homeScaledImage);

        homeButton = createButton("HOME", homeScaledIcon, new Dimension(50, Constants.HEADER_HEIGHT), 1);
        homeButton.addActionListener(actionListener);

        //creating menu icon        
        ImageIcon menuIcon = new ImageIcon("." + File.separator + "icons" + File.separator + "menu-icon.png");
        Image menuScaledImage = menuIcon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH); 
        ImageIcon menuScaledIcon = new ImageIcon(menuScaledImage);

        menuButton = createButton("MENU", menuScaledIcon, new Dimension(50, Constants.HEADER_HEIGHT), 1);
        menuButton.addActionListener(actionListener);

        //creating profile icon
        ImageIcon profileIcon = new ImageIcon("." + File.separator + "icons" + File.separator + "profile-icon.png");
        Image profileScaledImage = profileIcon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH); 
        ImageIcon profileScaledIcon = new ImageIcon(profileScaledImage);

        profileButton = createButton("PROFILE", profileScaledIcon, new Dimension(50, Constants.HEADER_HEIGHT), 1);
        profileButton.addActionListener(actionListener);

        ImageIcon postIcon = new ImageIcon("." + File.separator + "icons" + File.separator + "add-icon.png");
        Image postScaledImage = postIcon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH); 
        ImageIcon postScaledIcon = new ImageIcon(postScaledImage);

        createPostButton = createButton("ADD POST", postScaledIcon, new Dimension(50, Constants.HEADER_HEIGHT), 1);
        createPostButton.addActionListener(actionListener);

        JTextField searchBar = new JTextField("Search...");
        searchBar.setPreferredSize(new Dimension(300, Constants.HEADER_HEIGHT - 7));

        //set border color, width, and margins
        searchBar.setBorder(BorderFactory.createLineBorder(midColor, 3));
        searchBar.setBorder(BorderFactory.createCompoundBorder(
            searchBar.getBorder(), BorderFactory.createEmptyBorder(0, 10, 0, 0)
        ));

        //set colors
        searchBar.setBackground(darkColor);
        searchBar.setFont(Constants.SEARCH_BAR_FONT);
        searchBar.setForeground(lightestColor);

        //searchBar focus event to set and get rid of placeholder text
        searchBar.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchBar.getText().equals("Search...")) {
                    searchBar.setText("");
                    searchBar.setForeground(lightestColor);
                }
            }
        
            @Override
            public void focusLost(FocusEvent e) {
                if (searchBar.getText().isEmpty()) {
                    searchBar.setText("Search...");
                    searchBar.setForeground(lightestColor); 
                }
            }
        });

        searchBar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String searchText = searchBar.getText();
                if (!searchText.isEmpty()) {
                    resetFrame();
                    addHeaderPanel();
                    addPostPanel(searchText, (inForYouPage ? "for you" : "following"));
                }
            }
        });

        searchBarPanel.add(searchBar);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 5, 0, 50);
        headerButtonPanel.add(menuButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 50, 0, 5);
        headerButtonPanel.add(homeButton, gbc);
    
        gbc.gridx = 2;
        headerButtonPanel.add(profileButton, gbc);

        gbc.gridx = 3;
        gbc.insets = new Insets(0, 15, 0, 5);
        headerButtonPanel.add(createPostButton, gbc);
    
        headerPanel.add(headerButtonPanel, BorderLayout.WEST);
        headerPanel.add(searchBarPanel, BorderLayout.EAST);
    
        content.add(headerPanel, BorderLayout.NORTH);

        frame.revalidate();
        frame.repaint();
    }

    public void addProfilePanel(String username) {

        String[] posts;
        String[] userData;
        boolean isFriended = false;
        boolean isBlocked = false;
        Boolean othUsername = false;

        //check if it's the same as the current logged in user
        if (username != null) {
            othUsername = !client.getThisUser().split(",", -1)[0].equals(username);
        }

        if (!othUsername) {
            userData = client.getThisUser().split(",", -1);
            posts = client.getUser(userData[0]).split("<>", -1)[1].split("><");
        } else {
            String userDataString = client.getUser(username);
            userData = userDataString.split("<>", -1)[0].split(",", -1);
            posts = userDataString.split("<>", -1)[1].split("><");
            isOtherUserBlocked = false;
            String[] blockedUsers = null;
            blockedUsers = client.getThisUser().split(",", -1)[6].split("~");
            for (String blockedUser : blockedUsers) {
                if (blockedUser.equals(username)) {
                    isOtherUserBlocked = true;
                    break;
                }
            }

        }

        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapperPanel.setBackground(tonedColor);
    
        JPanel profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setPreferredSize(new Dimension(Constants.WINDOW_WIDTH - 500, 
                                                    Constants.WINDOW_HEIGHT - Constants.HEADER_HEIGHT));
        profilePanel.setBackground(darkestColor);
    
        ImageIcon originalIcon;

        //encode image bytes to ImageIcon
        if (userData[7].length() > 1) {
            byte[] decodedBytes = Base64.getDecoder().decode(userData[7].trim());   
            originalIcon = new ImageIcon(decodedBytes);
        } else {
            originalIcon = new ImageIcon("." + File.separator + "icons" + File.separator + 
                                        "default-profile-picture.jpg");
        }

        Image scaledImage = originalIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH); 
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel profileImageLabel = new JLabel(scaledIcon);

        if (isEditing) {
            profileImageLabel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent me) {
                    fileChooser(userData);
                }
            });
        }

        //instantiate bioLabel here for confirmation scope
        String bio = userData[2];
        JTextArea bioLabel = new JTextArea((bio == null) ? "" : bio.replace("\\n", "\n").replace("\\|", ","));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weighty = 0.1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(20, 100, 0, 0);
        profilePanel.add(profileImageLabel, gbc);

        if (!othUsername) {
            ImageIcon editIcon = new ImageIcon((isEditing) ? "." + File.separator + "icons" + 
                                                File.separator + "confirm-icon.png" : 
                                                "." + File.separator + "icons" + File.separator + "edit-icon.png");
            Image scaledEditImage = editIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH); 
            ImageIcon scaledEditIcon = new ImageIcon(scaledEditImage);
            JLabel iconLabel = new JLabel(scaledEditIcon);
            iconLabel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent me) {
                    if (isEditing) {
                        String normalizedText = bioLabel.getText().replace("\n", "\\n").replace(",", "\\|");
                        client.modifyUser(normalizedText, "2", "");
                    }

                    isEditing = !isEditing;
                    
                    resetFrame();
                    addHeaderPanel();
                    addProfilePanel(username);
                }
            });

            gbc.weighty = 0.1;
            gbc.gridx = 2;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.NORTHEAST;
            gbc.insets = new Insets(20, 0, 0, 20);
            profilePanel.add(iconLabel, gbc);
        }   

        if (othUsername && username != null) {
            ImageIcon reportIcon = new ImageIcon("." + File.separator + "icons" + File.separator + "block-icon.png");
            Image scaledReportImage = reportIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH); 
            ImageIcon scaledReportIcon = new ImageIcon(scaledReportImage);
            JLabel reportIconLabel = new JLabel(scaledReportIcon);
            reportIconLabel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent me) {
                    int confirmation = JOptionPane.showConfirmDialog(profilePanel, 
                                    (!isOtherUserBlocked) ? "Are you sure you would like to block this user?" : 
                                    "Are you sure you would like to unblock this user?");
                    
                    if (confirmation == 0) {
                        client.modifyUser(username, "6", "");
                        String[] thisUserFriendList = client.getThisUser().split(",", -1)[5].split("~");
                        for (String friend : thisUserFriendList) {
                            if (friend.equals(username)) {
                                client.modifyUser(username, "5", "");
                                break;
                            }
                        }
                        

                        resetFrame();
                        addHeaderPanel();
                        addProfilePanel(username);
                    }
                }
            });

            gbc.weighty = 0.1;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.insets = new Insets(20, 0, 0, 20);
            profilePanel.add(reportIconLabel, gbc);
        }   

        JLabel profileDisplayNameLabel = new JLabel(userData[0]);
        profileDisplayNameLabel.setFont(Constants.PROFILE_DISPLAYNAME_FONT);
        profileDisplayNameLabel.setForeground(lightestColor);
        profileDisplayNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(30, 20, 0, 0);
        profilePanel.add(profileDisplayNameLabel, gbc);
        
        String profileUsername = "@" + userData[0];
        JLabel profileUsernameLabel = new JLabel(profileUsername);
        profileUsernameLabel.setFont(Constants.PROFILE_USERNAME_FONT);
        profileUsernameLabel.setForeground(lightColor);
        profileUsernameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.insets = new Insets(65, 20, 0, 0);
        profilePanel.add(profileUsernameLabel, gbc);

        int numPosts = (posts[0] == null || posts[0].equals("")) ? 0 : posts.length;

        String statistics = numPosts + ((numPosts == 1) ? " post     " : " posts     ");
        JLabel statisticsLabel = new JLabel(statistics);
        statisticsLabel.setFont(Constants.PROFILE_STATISTIC_FONT);
        statisticsLabel.setForeground(lightestColor);
        statisticsLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.insets = new Insets(95, 20, 0, 0);
        profilePanel.add(statisticsLabel, gbc);

        bioLabel.setFont(Constants.PROFILE_BIO_FONT);
        bioLabel.setForeground(lightColor);
        bioLabel.setLineWrap(true);
        bioLabel.setWrapStyleWord(true);
        bioLabel.setEditable(isEditing);
        bioLabel.setBackground(profilePanel.getBackground());
        bioLabel.setColumns(38);
        bioLabel.setRows(5);
        gbc.insets = new Insets(120, 20, 0, 0);
        profilePanel.add(bioLabel, gbc);

        if (othUsername) {

            String[] thisUserData = client.getThisUser().split(",", -1);
            String[] thisUserFriendList = thisUserData[5].split("~");
            String[] otherUserBlockedList = client.getUser(username).split(",", -1)[6].split("~");
            isFriended = false;
            isBlocked = false;
            for (String friend : thisUserFriendList) {
                if (friend.equals(username)) {
                    isFriended = true;
                    break;
                }
            }
            for (String blocked : otherUserBlockedList) {
                if (blocked.equals(thisUserData[0])) {
                    isBlocked = true;
                    break;
                }
            }
            if (isOtherUserBlocked) {
                JLabel blockedLabel = new JLabel("User Blocked");
                blockedLabel.setFont(Constants.PROFILE_DISPLAYNAME_FONT);
                blockedLabel.setForeground(Color.RED.darker());
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.gridwidth = 2;
                gbc.anchor = GridBagConstraints.NORTHWEST;
                gbc.insets = new Insets(180, 140 - 40, 0, 0);
                profilePanel.add(blockedLabel, gbc);
            } else {
                if (!isBlocked) {
                    friendButton = createButton((isFriended ? "UNFRIEND" : "FRIEND"), null, new Dimension(100, 35), 2);
                    friendButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            client.modifyUser(username, "5", "");
        
                            resetFrame();
                            addHeaderPanel();
                            addProfilePanel(username);
                        }
                    });
                } else {
                    friendButton = createButton("Cannot Friend", null, new Dimension(170, 35), 2);
                }
                
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.gridwidth = 2;
                gbc.anchor = GridBagConstraints.NORTHWEST;
                gbc.insets = new Insets(180, 140 - 40, 0, 0);
                profilePanel.add(friendButton, gbc);
            }
        }

        JPanel postPanel = new JPanel();
        postPanel.setBackground(darkColor);
        postPanel.setLayout(new BoxLayout(postPanel, BoxLayout.PAGE_AXIS));
        postPanel.setBorder(null);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.gridheight = 2;
        gbc.insets = new Insets(15, 15, 0, 15);
        gbc.anchor = GridBagConstraints.NORTHWEST;

        if (posts.length >= 1 && !posts[0].equals("")) {
            for (int i = 0; i < posts.length; i++) {
                try {
                    JPanel post;
                    post = createPost("default-post", false, new Post(posts[i], null));
                    post.setAlignmentX(Component.LEFT_ALIGNMENT);
                    postPanel.add(post);
                    postPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                } catch (BadPostException e) {
                    e.printStackTrace();
                }    
            }
        }

        postPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(postPanel);
        scrollPane.setPreferredSize(new Dimension(400, 400));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBlockIncrement(100);
        scrollPane.setBorder(null);

        profilePanel.add(scrollPane, gbc);
        wrapperPanel.add(profilePanel);
        content.add(wrapperPanel, BorderLayout.CENTER);
    
        frame.revalidate();
        frame.repaint();
    }

    public void addPostWindow() {
        JFrame postFrame = new JFrame("New Post");
    
        postFrame.setSize(Constants.WINDOW_WIDTH / 2, Constants.WINDOW_HEIGHT / 2);
        postFrame.setLocationRelativeTo(null);
        postFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        postFrame.setResizable(false);
        postFrame.setVisible(true);
        postFrame.setAlwaysOnTop(getFocusTraversalKeysEnabled());
    
        frame.addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                postFrame.setLocation(frame.getX() + Constants.WINDOW_WIDTH / 4,
                                      frame.getY() + Constants.WINDOW_HEIGHT / 4);
            }
        });
    
        Container postFrameContent = postFrame.getContentPane();
        postFrameContent.setLayout(new BorderLayout());
        postFrameContent.setBackground(tonedColor);
    
        JLabel newPostLabel = new JLabel("New Post");
        newPostLabel.setForeground(lightestColor);
        newPostLabel.setFont(Constants.POST_HEADER_FONT);
        newPostLabel.setHorizontalAlignment(SwingConstants.CENTER);
        postFrameContent.add(newPostLabel, BorderLayout.NORTH);
    
        JPanel textAreasPanel = new JPanel();
        textAreasPanel.setLayout(new BoxLayout(textAreasPanel, BoxLayout.Y_AXIS));
        textAreasPanel.setBackground(tonedColor);
    
        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setForeground(lightestColor);
        titleLabel.setFont(Constants.POST_HEADER_FONT);
    
        JTextField titleField = new JTextField();
        titleField.setFont(Constants.SIGNUP_LOGIN_FIELD_FONT);
        titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        titleField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(lightestColor),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    
        JLabel contentLabel = new JLabel("Content:");
        contentLabel.setForeground(lightestColor);
        contentLabel.setFont(Constants.POST_HEADER_FONT);
    
        JTextArea newPostArea = new JTextArea();
        newPostArea.setFont(Constants.SIGNUP_LOGIN_FIELD_FONT);
        newPostArea.setLineWrap(true);
        newPostArea.setWrapStyleWord(true);
    
        JScrollPane scrollPane = new JScrollPane(newPostArea);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
    
        textAreasPanel.add(titleLabel);
        textAreasPanel.add(titleField);
        textAreasPanel.add(Box.createVerticalStrut(10));
        textAreasPanel.add(contentLabel);
        textAreasPanel.add(scrollPane);
    
        postFrameContent.add(textAreasPanel, BorderLayout.CENTER);

        JButton postButton = new JButton("Post");
        postButton.setFont(Constants.POST_HEADER_FONT);
        postButton.setBackground(tonedColor);
        postButton.setForeground(darkColor);
        postFrameContent.add(postButton, BorderLayout.SOUTH);
    
        postButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String postContent = newPostArea.getText().trim();
            if (title.isEmpty() || postContent.isEmpty()) {
                JOptionPane.showMessageDialog(postFrame, "Title and content cannot be empty!", 
                                              "Error", JOptionPane.ERROR_MESSAGE);
            } else if (title.length() > 100) {
                JOptionPane.showMessageDialog(postFrame, "Your title exceeds the 100 character limit!", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            } else if (postContent.length() > 500) {
                JOptionPane.showMessageDialog(postFrame, "Your post exceeds the 500 character limit!", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                client.post(title.replace(",", "\\|") + "<->" + 
                                        postContent.replace("\n", "\\n").replace(",", "\\|"), "text");
                JOptionPane.showMessageDialog(postFrame, "Your post was made successfully!", 
                                              "Published", JOptionPane.INFORMATION_MESSAGE);
                postFrame.dispose();
                resetFrame();
                addHeaderPanel();
                addPostPanel("", (inForYouPage ? "for you" : "following"));
            }
        });
    }

    public void addCommentWindow(String postId, Post currentPost) {
        JFrame postFrame = new JFrame("New Comment");
    
        postFrame.setSize(Constants.WINDOW_WIDTH / 2, Constants.WINDOW_HEIGHT / 2);
        postFrame.setLocationRelativeTo(null);
        postFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        postFrame.setResizable(false);
        postFrame.setVisible(true);
        postFrame.setAlwaysOnTop(getFocusTraversalKeysEnabled());
    
        frame.addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                postFrame.setLocation(frame.getX() + Constants.WINDOW_WIDTH / 4,
                                      frame.getY() + Constants.WINDOW_HEIGHT / 4);
            }
        });
    
        Container postFrameContent = postFrame.getContentPane();
        postFrameContent.setLayout(new BorderLayout());
        postFrameContent.setBackground(tonedColor);
    
        JLabel newPostLabel = new JLabel("New Comment");
        newPostLabel.setForeground(lightestColor);
        newPostLabel.setFont(Constants.POST_HEADER_FONT);
        newPostLabel.setHorizontalAlignment(SwingConstants.CENTER);
        postFrameContent.add(newPostLabel, BorderLayout.NORTH);
    
        JPanel textAreasPanel = new JPanel();
        textAreasPanel.setLayout(new BoxLayout(textAreasPanel, BoxLayout.Y_AXIS));
        textAreasPanel.setBackground(tonedColor);

        JLabel contentLabel = new JLabel("Write Comment Here: ");
        contentLabel.setForeground(lightestColor);
        contentLabel.setFont(Constants.POST_HEADER_FONT);
    
        JTextArea newPostArea = new JTextArea();
        newPostArea.setFont(Constants.SIGNUP_LOGIN_FIELD_FONT);
        newPostArea.setLineWrap(true);
        newPostArea.setWrapStyleWord(true);
    
        JScrollPane scrollPane = new JScrollPane(newPostArea);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        textAreasPanel.add(Box.createVerticalStrut(10));
        textAreasPanel.add(contentLabel);
        textAreasPanel.add(scrollPane);
    
        postFrameContent.add(textAreasPanel, BorderLayout.CENTER);

        JButton postButton = new JButton("Post");
        postButton.setFont(Constants.POST_HEADER_FONT);
        postButton.setBackground(tonedColor);
        postButton.setForeground(darkColor);
        postFrameContent.add(postButton, BorderLayout.SOUTH);
    
        postButton.addActionListener(e -> {
            if (newPostArea.getText().isEmpty()) {
                JOptionPane.showMessageDialog(postFrame, "Please enter a valid comment!", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            } else if (newPostArea.getText().length() > 300) {
                JOptionPane.showMessageDialog(postFrame, "Your comment exceeds the 300 character limit!", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                client.addComment(postId, 
                              newPostArea.getText().replace("\n", "\\n").replace(",", "\\|"), 
                              client.getThisUser().split(",", -1)[0]);
                JOptionPane.showMessageDialog(postFrame, "Your post was made successfully!", 
                                                "Published", JOptionPane.INFORMATION_MESSAGE);
                postFrame.dispose();
                resetFrame();
                addHeaderPanel();
                addPostPage(currentPost);
            }
        });
    }

    public void addPostPage(Post post) {
        String[] userData;
        String postCreator;
        String title;
        String message;
        String postId;
        ArrayList<String> comments;
        String postLikes;
        String postDislikes;

        userData = client.getUser(post.getPosterUsername()).split("<>", -1)[0].split(",", -1);
        postCreator = post.getPosterUsername();
        title = post.getMessage().split("<->", -1)[0];
        message = post.getMessage().split("<->", -1)[1];
        comments = post.getComments();
        postLikes = Integer.toString(post.getLikes());
        postDislikes = Integer.toString(post.getDislikes());
        postId = post.getPostID();

        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapperPanel.setBackground(tonedColor);
    
        JPanel profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setPreferredSize(new Dimension(Constants.WINDOW_WIDTH - 500, 
                                                    Constants.WINDOW_HEIGHT - Constants.HEADER_HEIGHT));
        profilePanel.setBackground(darkestColor);
    
        ImageIcon originalIcon;

        //encode image bytes to ImageIcon
        if (userData[7].length() > 1) {
            byte[] decodedBytes = Base64.getDecoder().decode(userData[7].trim());   
            originalIcon = new ImageIcon(decodedBytes);
        } else {
            originalIcon = new ImageIcon("." + File.separator + "icons" + 
                                        File.separator + "default-profile-picture.jpg");
        }

        Image scaledImage = originalIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH); 
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JButton postCreatorProfileButton = new JButton(scaledIcon);
        postCreatorProfileButton.setPreferredSize(new Dimension(50, 50));
        postCreatorProfileButton.setFocusPainted(false);
        postCreatorProfileButton.setBorderPainted(false);
        postCreatorProfileButton.setContentAreaFilled(false);
    
        postCreatorProfileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetFrame();
                addHeaderPanel();
                addProfilePanel(postCreator);
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weighty = 0.1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(15, 15, 0, 0);
        profilePanel.add(postCreatorProfileButton, gbc);

        JLabel postCreatorLabel = new JLabel("@" + postCreator);
        postCreatorLabel.setFont(Constants.PROFILE_USERNAME_FONT);
        postCreatorLabel.setForeground(lightestColor);
        postCreatorLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(80, 17, 0, 0);
        profilePanel.add(postCreatorLabel, gbc);

        JLabel titleLabel = new JLabel((title == null) ? "" : title.replace("\\n", "\n").replace("\\|", ","));
        titleLabel.setFont(Constants.POST_HEADER_FONT);
        titleLabel.setForeground(lightestColor);
        titleLabel.setBackground(profilePanel.getBackground());
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 15, 0, 0);
        profilePanel.add(titleLabel, gbc);

        JTextArea messageLabel = new JTextArea((message == null) ? "" : 
                                                message.replace("\\n", "\n").replace("\\|", ","));
        messageLabel.setFont(Constants.POST_CONTENT_FONT);
        messageLabel.setForeground(lightColor);
        messageLabel.setLineWrap(true);
        messageLabel.setWrapStyleWord(true);
        messageLabel.setBackground(profilePanel.getBackground());
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1; 
        gbc.weighty = 0; 
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.insets = new Insets(50, 10, 10, 10); 
        profilePanel.add(messageLabel, gbc);

        int numComments = (comments.isEmpty()) ? 0 : comments.size();

        JLabel numCommentsLabel = new JLabel((numComments != 1) ? numComments + " comments..." : "1 Comment...");
        numCommentsLabel.setFont(Constants.PROFILE_STATISTIC_FONT);
        numCommentsLabel.setForeground(lightestColor);
        numCommentsLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.insets = new Insets(50, 15, 0, 0);
        profilePanel.add(numCommentsLabel, gbc);

        JPanel commentPanel = new JPanel();
        commentPanel.setLayout(new BoxLayout(commentPanel, BoxLayout.PAGE_AXIS));
        commentPanel.setBackground(darkColor);
        commentPanel.setBorder(null);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.gridheight = 2;
        gbc.insets = new Insets(10, 5, 0, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;

        JButton createCommentButton = createButton("New Comment", null, new Dimension(200, 40), 0);
        createCommentButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                addCommentWindow(postId, post);
            }
        });
        
        commentPanel.add(createCommentButton);

        if (numComments > 0) {
            for (int i = 0; i < numComments; i++) {
                try {
                    String[] commentInfo = comments.get(i).split(";", -1);
                    JPanel comment;
                    comment = createComment(commentInfo, postId);
                    comment.setAlignmentX(Component.LEFT_ALIGNMENT);
                    commentPanel.add(comment);
                    commentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                } catch (Exception e) {
                    e.printStackTrace();
                }    
            }
        }

        commentPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(commentPanel);
        scrollPane.setPreferredSize(new Dimension(400, 400));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBlockIncrement(100);
        scrollPane.setBorder(null);
        
        wrapperPanel.add(profilePanel);
        profilePanel.add(scrollPane, gbc);
        content.add(wrapperPanel, BorderLayout.CENTER);
    
        frame.revalidate();
        frame.repaint();
    }
    
    public void changeColorThemes(String theme) {
        switch (theme) {

            case "Dark Mode":
                darkestColor = Constants.COLOR_DARKEST.darker();
                darkColor = Constants.COLOR_DARKEST;
                tonedColor = Constants.COLOR_TONED;
                midColor = Constants.COLOR_MID;
                lightColor = Constants.COLOR_LIGHT;
                lightestColor = Constants.COLOR_LIGHTEST;
                break;
            case "Light Mode":
                darkestColor = Constants.COLOR_LIGHTMODE_LIGHTEST;
                darkColor = Constants.COLOR_LIGHTEST;
                tonedColor = Constants.COLOR_MID;
                midColor = Constants.COLOR_TONED;
                lightColor = Constants.COLOR_DARKEST;
                lightestColor = Constants.COLOR_DARKEST.darker();
                break;
            case "Colored Mode":
                break;
            default:
                break;     
        }
    }

    public void addMenuPanel() {
        menuFrame = new JFrame("Preferences");
        menuFrame.isFocused();

        Container menuFrameContent = menuFrame.getContentPane();
        menuFrameContent.setLayout(new BorderLayout());
        menuFrame.setSize(Constants.WINDOW_WIDTH / 2, Constants.WINDOW_HEIGHT / 2);
        menuFrame.setLocationRelativeTo(null);
        menuFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        menuFrame.setResizable(false);
        menuFrame.setVisible(true);
        menuFrame.setAlwaysOnTop(getFocusTraversalKeysEnabled());

        frame.addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                menuFrame.setLocation(frame.getX() + Constants.WINDOW_WIDTH / 4,
                                      frame.getY() + Constants.WINDOW_HEIGHT / 4);
            }
        });

        String[] preferences = {"Display", "Account"};
        JList<String> sidebar = new JList<>(preferences);
        sidebar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sidebar.setBackground(tonedColor);
        sidebar.setForeground(lightestColor);
        sidebar.setFont(Constants.POST_HEADER_FONT);

        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(lightestColor);
        contentPanel.setLayout(new CardLayout()); 
        contentPanel.add(new JLabel("Select a preference to view settings"), "Default");

        for (String preference : preferences) {

            JPanel preferencePanel = new JPanel();
            preferencePanel.setLayout(new BoxLayout(preferencePanel, BoxLayout.PAGE_AXIS));
            preferencePanel.setBackground(darkColor);

            JScrollPane scrollPane = new JScrollPane(preferencePanel);
            scrollPane.setPreferredSize(new Dimension(300, 300));
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            JLabel headerLabel = new JLabel(preference + " Settings", JLabel.CENTER);
            headerLabel.setForeground(lightestColor);
            preferencePanel.add(headerLabel);

            switch (preference) {
                case "Display":

                    String[] themes = {"Dark Mode", "Light Mode", "Colored Mode"};
                    JComboBox<String> themeDropdown = new JComboBox<>(themes);
                    themeDropdown.setPreferredSize(new Dimension(200, 30));
                    themeDropdown.setBackground(darkestColor);
                    themeDropdown.setForeground(lightestColor);

                    themeDropdown.addActionListener(e -> {
                        String selectedTheme = (String) themeDropdown.getSelectedItem();
                        changeColorThemes(selectedTheme);
                    });

                    preferencePanel.add(themeDropdown);
                    break;
                case "Account":

                    String[] interests = client.getThisUser().split(",", -1)[4].split("~");

                    JPanel interestsGrid = new JPanel();
                    interestsGrid.setLayout(new GridLayout(0, 3, 10, 10)); 
                    interestsGrid.setBackground(darkColor);

                    for (String interest : interests) {
                        JLabel interestLabel = new JLabel(interest, JLabel.CENTER);
                        interestLabel.setForeground(lightestColor);
                        interestLabel.setOpaque(true);
                        interestLabel.setBackground(tonedColor);
                        interestLabel.setBorder(BorderFactory.createLineBorder(lightestColor, 1));
                        interestsGrid.add(interestLabel);
                    }

                    preferencePanel.add(interestsGrid);

                    logOffButton = createButton("Log Off", null, new Dimension(150, 40), 3);
                    logOffButton.addActionListener(actionListener);
                    preferencePanel.add(logOffButton);
                    break;    
                default:
                    break;
            }

            preferencePanel.add(Box.createVerticalGlue());

            JPanel outerPanel = new JPanel(new BorderLayout());
            outerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            outerPanel.setBackground(tonedColor);
            outerPanel.add(scrollPane, BorderLayout.CENTER);

            contentPanel.add(outerPanel, preference);
        }

        sidebar.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = sidebar.getSelectedValue();
                CardLayout cl = (CardLayout) contentPanel.getLayout();
                if (selected != null) {
                    cl.show(contentPanel, selected);
                }
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(sidebar), contentPanel);
        splitPane.setDividerLocation(menuFrame.getWidth() / 3); 
        splitPane.setDividerSize(5); 
        splitPane.setContinuousLayout(true); 

        menuFrameContent.add(splitPane, BorderLayout.CENTER);
    }


    public void fileChooser(String[] data) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Profile Picture");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", 
                                                                                    "jpg", "png", "gif", "jpeg"));
        
        int returnValue = fileChooser.showOpenDialog(frame);
        
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                byte[] fileBytes = new byte[(int) selectedFile.length()];
                try (FileInputStream fileInputStream = new FileInputStream(selectedFile)) {
                    fileInputStream.read(fileBytes);
                }

                String base64 = Base64.getEncoder().encodeToString(fileBytes);

                //set user's profile picture in base64
                client.modifyUser(base64.trim(), "7", "");

                resetFrame();
                addHeaderPanel();
                addProfilePanel(null);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Error loading image", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    ActionListener actionListener = new ActionListener() {
        
        /**
         * An actionListener method which responds to button calls
         * 
         * @param e an actionEvent event 
         */
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == homeButton) {
                resetFrame();
                addHeaderPanel();
                addPostPanel("", "for you");
                inForYouPage = true;
                isEditing = false;
            }
            if (e.getSource() == forYouButton) {
                resetFrame();
                addHeaderPanel();
                addPostPanel("", "for you");
                inForYouPage = true;
                isEditing = false;
            }
            if (e.getSource() == followingButton) {

                resetFrame();
                addHeaderPanel();
                addPostPanel("", "following");
                inForYouPage = false;
                isEditing = false;
            }
            if (e.getSource() == profileButton) {
                resetFrame();
                addHeaderPanel();
                addProfilePanel(null);
                isEditing = false;
            }
            if (e.getSource() == menuButton) {
                if (menuFrame == null || !menuFrame.isDisplayable()) {
                    addMenuPanel();
                }
            }
            if (e.getSource() == createPostButton) {
                addPostWindow();
            }
            if (e.getSource() == loginButton) {
                try {
                    if (client.logIn(usernameField.getText(), String.valueOf(passwordField.getPassword()))) {
                        resetFrame();
                        addHeaderPanel();
                        addPostPanel("", "for you");
                        inForYouPage = true;
                    } else {
                        JOptionPane.showMessageDialog(null, "User not found", "Error", 1);
                    }     
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(null, "Error Recieving Information", "Error", 1);
                }
                
            }
            if (e.getSource() == signupPageButtonOne) {

                try {
                    if (usernameField.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please enter a username", "Error", 1);
                    } else if (String.valueOf(passwordField.getPassword()).isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please enter a password", "Error", 1);
                    } else if (!client.acceptablePassword(String.valueOf(passwordField.getPassword()))) {
                        JOptionPane.showMessageDialog(null, "Password Doesn't Meet Requirements:" + 
                                                      "\n- No Spaces\n- One or More Capital Letters\n" + 
                                                      "- One or More Symbols\n- 7 or More Characters", "Error", 1);
                    } else {
                        resetFrame();
                        addInterestsPanel(); 
                    }
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(null, "Error Recieving Information", "Error", 1);
                }
                
            }
            if (e.getSource() == signupPageButtonFinal) {

                try {
                    if (client.signUp(usernameField.getText(), 
                                      String.valueOf(passwordField.getPassword()), 
                                      bioField.getText().replace("\n", "\\n").replace(",", "\\|"), 
                                      interestArray.toArray(new String[0]))) {
                        resetFrame();
                        addHeaderPanel();
                        addPostPanel("", "for you");
                        interestArray = new ArrayList<>();
                        inForYouPage = true;
                    } else {
                        JOptionPane.showMessageDialog(null, "Incorrect Information Given", "Error", 1);

                    }
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(null, "Error Recieving Information", "Error", 1);
                    resetFrame();
                    addSignupPanel();
                }          

            }
            if (e.getSource() == signupButton) {
                resetFrame();
                addSignupPanel();
            }
            if (e.getSource() == loginPageButton) {
                resetFrame();
                addLoginPanel();
            }
            if (e.getSource() == logOffButton) {
                client.logOut();
                menuFrame.dispose();
                menuFrame = null;
                resetFrame();
                addLoginPanel();
            }
        }
    };
}
