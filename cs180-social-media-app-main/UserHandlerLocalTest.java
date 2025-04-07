import org.junit.Test;
import org.junit.Assert;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.io.*;
import java.net.Socket;

/**
 * Team Project -- UserHandler Tests
 *
 * A framework to run public test cases for UserHandler.
 * Checks helper methods.
 *
 * @author Charles Pittman, lab sec 002
 * @author Omkar Govil-Nair, lab sec 002
 * @author Quy Pham Ngo Thien, lab sec 002
 *
 * @version Nov 17, 2024
 *
 */

public class UserHandlerLocalTest  {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestCase.class);
        if (result.wasSuccessful()) {
            System.out.println("Excellent - Test ran successfully");
        } else {
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        }
    }
    /**
     * UserHandler test cases
     *
     * Contains test for methods which are only accessed through UserHandler.
     *
     * @version Nov 16, 2024
     **/
    public static class TestCase implements UserHandlerTestInterface {

        private static boolean testBool = false;
        Socket socket;
        BufferedWriter bw;
        BufferedReader br;
        Server server;

        private static final String USERFILE = Constants.USERFILE;
        private static final String POSTFILE = Constants.POSTFILE;

        public void resetFiles() throws IOException {
            BufferedWriter bw1 = new BufferedWriter(new FileWriter(USERFILE));
            BufferedWriter bw2 = new BufferedWriter(new FileWriter(POSTFILE));

            bw1.write("");
            bw2.write("");

            bw1.close();
            bw2.close();
        }

        @Test(timeout = 1000)
        public void runTestUserHandleConstructors() throws BadUserException {
            try {
                server = new Server();
                socket = new Socket();
                bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            //constructor 1
            UserHandler u;
            try {
                u = new UserHandler(socket, bw, br, server);
                testBool = false;

                server.closeServerSocket();
                u.closeEverything();
            } catch (Exception e) {
                testBool = true;
            }

            Assert.assertFalse("Bad UserHandler Data in Constructor", testBool);
        }


        @Test(timeout = 500)
        public void runTestCreateAccPublishPostGetUser() throws BadUserException, BadPostException, IOException {
            resetFiles();

            try {
                server = new Server();
                socket = new Socket();
                bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            UserHandler userA = new UserHandler(socket, bw, br, server);
            UserHandler userB = new UserHandler(socket, bw, br, server);
            UserHandler userC = new UserHandler(socket, bw, br, server);

            String[] bobInterest = "basketball~gaming~music".split("~");
            String[] larryInterest = "Hate~Unhappy~Emo".split("~");
            String[] barryInterest = "Speed~Fast~Racing".split("~");

            User user1  = userA.createAccount("Bob", "Password1234!", "a cool guy", bobInterest);
            Post p = userA.publishPost("Post about food.", "food");
            Post p2 = userA.publishPost("Post about realness.", "Real");

            User user2  = userB.createAccount("Larry", "Password5678!", "a lame guy", larryInterest);
            Post p3 = userB.publishPost("Post about bad food.", "bad food");
            Post p4 = userB.publishPost("Post about unrealness.", "not Real");

            User user3  = userC.createAccount("Barry", "Password2233!", "a fast guy", barryInterest);
            Post p5 = userC.publishPost("Post about speed.", "Real");
            Post p6 = userC.publishPost("Post about Racing.", "Racing");

            String expectedUser = user1.toString() + "<>" + p2.toString() + "><" + p.toString() + "><";
            Assert.assertEquals("getUser returned an incorrect User", expectedUser, userA.getUser("Bob"));

            expectedUser = user2.toString() + "<>" + p4.toString() + "><" + p3.toString() + "><";
            Assert.assertEquals("getUser returned an incorrect User", expectedUser, userB.getUser("Larry"));

            expectedUser = user3.toString() + "<>" + p6.toString() + "><" + p5.toString() + "><";
            Assert.assertEquals("getUser returned an incorrect User", expectedUser, userC.getUser("Barry"));

            server.closeServerSocket();
            userA.closeEverything();
            userB.closeEverything();
            userC.closeEverything();
        }

        @Test(timeout = 500)
        public void runTestReactPost() throws  BadUserException, BadPostException, IOException {
            resetFiles();

            try {
                server = new Server();
                socket = new Socket();
                bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            UserHandler userA = new UserHandler(socket, bw, br, server);
            UserHandler userB = new UserHandler(socket, bw, br, server);
            UserHandler userC = new UserHandler(socket, bw, br, server);

//          database.writeLine(0, "Bob,Password1234!,a cool guy,2024-12-4,basketball~gaming~music,Dylan,Arlo,", 0);
            String[] bobInterest = "basketball~gaming~music".split("~");
            String[] larryInterest = "Hate~Unhappy~Emo".split("~");
            String[] barryInterest = "Speed~Fast~Racing".split("~");

            User user1  = userA.createAccount("Bob", "Password1234!", "a cool guy", bobInterest);
            Post p = userA.publishPost("Post about food.", "food");
            Post p2 = userA.publishPost("Post about realness.", "Real");

            User user2  = userB.createAccount("Larry", "Password5678!", "a lame guy", larryInterest);
            Post p3 = userB.publishPost("Post about bad food.", "bad food");
            Post p4 = userB.publishPost("Post about unrealness.", "not Real");

            User user3  = userC.createAccount("Barry", "Password2233!", "a fast guy", barryInterest);
            Post p5 = userC.publishPost("Post about speed.", "Real");
            Post p6 = userC.publishPost("Post about Racing.", "Racing");

            boolean test1 = userA.reactPost(p5.getPostID(), true, true);
            boolean test2 = userB.reactPost(p5.getPostID(), false, true);
            boolean test3 = userA.reactPost(p5.getPostID(), true, false);
            boolean test4 = userA.reactPost(p5.getPostID(), false, false);
            boolean test5 = userB.reactPost("6583465892", true, false);

            Assert.assertEquals("ReactPost does not function properly", test1, true);
            Assert.assertEquals("ReactPost does not function properly", test2, true);
            Assert.assertEquals("ReactPost does not function properly", test3, true);
            Assert.assertEquals("ReactPost does not function properly", test4, true);
            Assert.assertEquals("ReactPost does not function properly", test5, false);

            server.closeServerSocket();
            userA.closeEverything();
            userB.closeEverything();
            userC.closeEverything();
        }

        @Test(timeout = 500)
        public void runTestSearch() throws BadUserException, BadPostException, IOException {
            resetFiles();

            try {
                server = new Server();
                socket = new Socket();
                bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            UserHandler userA = new UserHandler(socket, bw, br, server);
            UserHandler userB = new UserHandler(socket, bw, br, server);
            UserHandler userC = new UserHandler(socket, bw, br, server);

            String[] bobInterest = "basketball~gaming~music".split("~");
            String[] larryInterest = "Hate~Unhappy~Emo".split("~");
            String[] barryInterest = "Speed~Fast~Racing".split("~");

            User user1  = userA.createAccount("Bob", "Password1234!", "a cool guy", bobInterest);
            Post p = userA.publishPost("Post about food.", "food");
            Post p2 = userA.publishPost("Post about realness.", "Real");

            User user2  = userB.createAccount("Larry", "Password5678!", "a lame guy", larryInterest);
            Post p3 = userB.publishPost("Post about bad food.", "bad food");
            Post p4 = userB.publishPost("Post bout unrealness.", "not Real");

            User user3  = userC.createAccount("Barry", "Password2233!", "a fast guy", barryInterest);
            Post p5 = userC.publishPost("Post bout speed.", "Real");
            Post p6 = userC.publishPost("Post about Racing.", "Racing");

            String compare = "<>" + p.toString() + "><" + p3.toString() + "><" + 
                            p6.toString() + "><" + p2.toString() + "><" + p4.toString() + "><" +
                            p5.toString();
            Assert.assertEquals("Search does not return the correct output", compare, userA.search("about"));

            compare = user3.toString() + "<>";
            compare += p6.toString() + "><" + p2.toString() + "><" + p3.toString() + 
                       "><" + p4.toString() + "><" + p.toString() + "><" + p5.toString();

            Assert.assertEquals("Search does not return the correct output", compare, userC.search("Barry"));

            server.closeServerSocket();
            userA.closeEverything();
            userB.closeEverything();
            userC.closeEverything();
        }

        // Test method for adding comments to posts
        @Test(timeout = 500)
        public void runTestAddComment() throws BadUserException, BadPostException, IOException {
            resetFiles();

            try {
                server = new Server();
                socket = new Socket();
                bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Create two user handlers
            UserHandler userA = new UserHandler(socket, bw, br, server);
            UserHandler userB = new UserHandler(socket, bw, br, server);

            // Create users and posts
            User user1 = userA.createAccount("Alice", "Password1234!", "Loves sports",  
                                            new String[]{"sports", "fitness"});
            Post post1 = userA.publishPost("Excited about the new game!", "gaming");

            // Add valid comment to post1
            boolean commentAdded = userA.addComment(post1.getPostID(), "Great game! Can't wait to play!", "");
            // Add an empty comment to test invalid comment handling
            boolean commentAddedEmpty = userA.addComment(post1.getPostID(), "", ""); // Invalid comment (empty)

            Assert.assertEquals("Comment adding failed", true, commentAdded); 
            Assert.assertEquals("Empty comment should not be added", false, commentAddedEmpty); 

            server.closeServerSocket();
            userA.closeEverything();
            userB.closeEverything();
        }


        // Test method for reacting to comments on posts
        @Test(timeout = 500)
        public void runTestReactComment() throws BadUserException, BadPostException, IOException {
            resetFiles();

            try {
                server = new Server();
                socket = new Socket();
                bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Create two user handlers
            UserHandler userA = new UserHandler(socket, bw, br, server);
            UserHandler userB = new UserHandler(socket, bw, br, server);

            // Create users, posts, and comments
            User user1 = userA.createAccount("Alice", "Password1234!", "Loves sports", 
                                            new String[]{"sports", "fitness"});
            Post post1 = userA.publishPost("Excited about the new game!", "gaming");
            userA.addComment(post1.getPostID(), "Great game! Can't wait to play!", ""); 

            // Get the comment text or index (assuming comment is at index 0 in the list)
            post1 = new Post(server.db.getPost(post1.getPostID()), server.db);
            String commentId = post1.getComments().get(0); // Using comment text for validation

            // React to the comment (like and dislike)
            boolean reactLike = userA.reactComment(post1.getPostID(), commentId, true, true); 
            boolean reactRemoveLike = userA.reactComment(post1.getPostID(), commentId, true, false); 
            boolean reactDislike = userA.reactComment(post1.getPostID(), commentId, false, true);
            boolean reactRemoveDislike = userA.reactComment(post1.getPostID(), commentId, false, false); 

            Assert.assertEquals("Like operation on comment failed", true, reactLike); 
            Assert.assertEquals("Remove like operation on comment failed", true, reactRemoveLike); 
            Assert.assertEquals("Dislike operation on comment failed", true, reactDislike);
            Assert.assertEquals("Remove dislike operation on comment failed", true, reactRemoveDislike);

            server.closeServerSocket();
            userA.closeEverything();
            userB.closeEverything();
        }

        @Test
        public void runTestModifyUser() throws BadUserException, IOException {
            resetFiles();

            String time = java.time.LocalDateTime.now().toString();
            String timeFormatted = time.substring(0, time.indexOf("T"));

            try {
                server = new Server();
                socket = new Socket();
                bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Create two user handlers
            UserHandler userA = new UserHandler(socket, bw, br, server);
            User user1 = userA.createAccount("Alice", "Password1234!", "Loves sports", 
                                            new String[]{"sports", "fitness"});

            boolean res = userA.modifyUser("Loves fitness", 2, "Alice");

            String expected = "Alice,Password1234!,Loves fitness," + timeFormatted + ",sports~fitness,,, ";
            Assert.assertEquals("Operation failed!", true, res);
            Assert.assertEquals("Bio wasn't changed!", expected, server.db.getUser("Alice"));

            server.closeServerSocket();
            userA.closeEverything();
        }
    }
}
