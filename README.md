# Social Media App (Copy)

This project has files for a Twitter-like social media app written in Java. So far the project only has classes establishing the database of the app. A key feature for our app is that each User can select certain interest categories and view a feed of Posts tagged with those categories.

## Submission
Omkar - Submitted Phase 1 & 3 to Vocareum Workspace and Submitted Presentation Video

Ryan - Submitted Phase 2 to Vocareum Workspace and Submitted Report

## Running the Project
For Phase 1, since the database methods are going to be used by the server to be implemented in Phase 2, we have not added a main method. To test the Classes, there are three test case files ([DatabaseLocalTest.java](DatabaseLocalTest.java), [UserLocalTest.java](UserLocalTest.java), [PostLocalTest.java](PostLocalTest.java)). First install JUnit using the jar files in [/lib](/lib), then use `javac` and `java` to compile and run the tests.

For Phase 2 & 3, compile and run [Server.java](Server.java) to start accepting clients, then compile and run [Client.java](Client.java) to simulate a User opening the app.

## Database Class
The Database class includes thread-safe methods that deal with file I/O in order to persistently store data for the User and Post objects (which serve as in-memory caches). To achieve this, Database uses two files (one for User and one for Post), where each line in these files stores a single User/Post object in a data String format. The data String formats are as follows:

- User:
    - `{username},{password},{bio},{dateCreated},{{interest1}~{interest2}~{interest3}},{{friend1}~{friend2}},{{blocked1}~{blocked2}},{Base64ProfilePicture}`
    - Notes:
        - dateCreated is a string in the format `yyyy-mm-dd`
        - GUI/Server must prevent interest categories from containing `~`
        - friends and blocked are User usernames
        - User's profile picture is stored in Base64 format at the end of their data file

- Post:
    - `{postId},{postType},{posterUsername},{title<>message},{timestamp},{views},{likes},{dislikes},{comment1},{comment2},{comment3}`
    - Notes:
        - timestamp is a string in the format `yyyy-mm-dd hh:mm:ss`
        - GUI/Server must escape commas in comments by replacing them with `|`
        - comments have the format: `{username};{commentId};{likes};{dislikes};{comment}`

Apart from methods to load files, read a line, write to/modify a line, and add a User/Post, Database has methods to modify specific elements of a Post since this entire process needs to be locked from other Threads as multiple Users may be liking/commenting/etc on a Post at the same time. Database only needs a single `modifyUser()` method since we assume Server will prevent multiple active instances of the same User, reducing the synchronization requirements. Additionally, the database has methods to search for Users and Posts using both search terms and (for Post) comparing the category of the Post to an array of a User’s interests and the poster to a list of a User’s friends.

To test Database, [DatabaseLocalTest.java](DatabaseLocalTest.java) includes tests for data persistence, the constructors and methods of Database, and cases where Database should throw an exception for bad data.

## User Class
The User class serves as an in-memory cache of a User object stored in the user file. Constructing a new User with all of User’s fields adds a new User to Database, constructing a new User with their username and password attempts to login, and constructing a new User using a User data string from Database fills User’s fields. This last constructor is meant to be used within Database to return User objects. Setters for User manipulates the User's data string and calls Database’s `modifyUser()`. If a User object is changed using a setter method, Server should reconstruct the User to update it. The class also supports managing friendships and interests, further enhancing user interactions.

To test User, [UserLocalTest.java](UserLocalTest.java) includes tests for all three constructors, all getters, all modifiers (ex. changing a user's friends list, changing a user's blocked list, and settimg a user's bio), as well as toString() and equals(). Every test includes input with bad data and correct data.

## Post Class
The Post class serves as an in-memory cache of a Post object, representing social media posts stored in the post file. Constructing a new Post with all of Post’s fields adds a new Post to Database, and constructing a new Post using a Post data string from Database fills Post’s fields. This second constructor is meant to be used within Database to return Post objects. Setters for Post call Database methods in order to change the Post’s data string in the post file. We chose this design because it is possible for many threads in Server to be trying to update a single Post at the same time (updating likes/comments/etc), and in order to make these changes correctly, Database must read the line for any recent updates and write the new changes in the same lock. The Post class also includes functionality for managing comments, tracking engagement metrics such as likes and dislikes, and maintaining a chronological order of interactions.

To test Post, [PostLocalTest.java](PostLocalTest.java) includes tests for both constructors, attempting to construct with bad data, and all updates (ex. Adding/removing like from Post, adding/removing dislike from Post, adding/removing comment from Post, adding/removing like/dislike from comment, incrementing views on a Post, etc).

## Server Class
The Server class serves to accept client connections and start new UserHandler threads, allowing concurrent connections. The main method starts the Server accepting clients and the program must be manually ended. Server initializes a public Database object to be used by all other classes. Because this class only performs network I/O, Server is not tested.

## UserHandler Class
The UserHandler class implements Runnable and handles network I/O with each individual client. It's run method uses a switch-case statement to accept 'commands' which can be sent by the client to perform various actions (create account, log in, publish a Post, search, get User info, modify User, react to a Post, add a comment to a Post, react to a comment, get Posts). UserHandler accesses Server's public Database object and relies on User, Post, and Database methods to perform these actions.

To test UserHandler, [UserHandlerLocalTest.java](UserHandlerLocalTest.java) includes test for the constructor and all helper methods, except for those which perform network I/O operations.

## Client Class
The Client class connects to the ClientInterface to provide a comprehensive suite of features for user interaction within a social media application. This includes methods for user account management, such as signing up, logging in, and modifying user details, allowing for authentication and profile updates. The client supports content creation and engagement through posting, commenting and reacting to posts and comments, in favor of dynamic user interaction. Additionally, the Client facilitates personalized content discovery with search and post-retrieval capabilities tailored to user interests. Utility functions, such as broadcasting messages and verifying password strength, further enhance the application’s security and communication mechanics. All of these features are requested through the Client class and returned via server-side UserHandler algorithms to allow for dynamic updating across multiple client instances. 

Since the Client class is heavily reliant on Socket IO communication, testing the non-helper Client methods is done through the JFrameHandler GUI application. Through the GUI the User can log in, sign in, view their or other user's profiles, add friends, block users, change their bio/username/profile picture, create posts, view posts, log out, and more. Errors are handled server-side, and GUI exceptions are handled through the JFrameHandler class. 
