import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import java.util.*;

/**
 * Team Project -- Database Tests
 *
 * A framework to run public test cases for Database.
 *
 * @author Omkar Govil-Nair, lab sec 002
 * @author Quy Pham Ngo Thien, lab sec 002
 *
 * @version Nov 3, 2024
 */

public class DatabaseLocalTest {

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
     * Database Test Cases
     *
     * Contains test for methods which are only accessed through
     * Database. Methods which are used by User and Post are tested
     * in their respective Test classes.
     *
     * @version Nov 3, 2024
     */
    public static class TestCase implements DatabaseTestInterface {
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
        public void runTestDatabaseReadWriteLine() throws IOException {
            resetFiles();
            Database db = new Database(USERFILE, POSTFILE);

            db.writeLine(0, "user1,data", 0);
            db.writeLine(1, "user2,data", 0);
            db.writeLine(2, "user3,data", 0);

            db.writeLine(0, "post 1,a", 1);
            db.writeLine(1, "post 2,b", 1);
            db.writeLine(2, "post 3,c", 1);

            Assert.assertEquals("Reading user line index 0 failed!", "user1,data", db.readLine(0, 0));
            Assert.assertEquals("Reading user line index 1 failed!", "user2,data", db.readLine(1, 0));
            Assert.assertEquals("Reading user line index 2 failed!", "user3,data", db.readLine(2, 0));

            Assert.assertEquals("Reading post line index 0 failed!", "post 1,a", db.readLine(0, 1));
            Assert.assertEquals("Reading user line index 1 failed!", "post 2,b", db.readLine(1, 1));
            Assert.assertEquals("Reading user line index 2 failed!", "post 3,c", db.readLine(2, 1));
        }

        @Test(timeout = 1000)
        public void runTestDatabaseModifyLine() throws IOException {
            resetFiles();
            Database db = new Database(USERFILE, POSTFILE);

            db.writeLine(0, "user1,data", 0);
            db.writeLine(1, "user2,data", 0);
            db.writeLine(2, "user3,data", 0);

            db.writeLine(0, "post 1,a", 1);
            db.writeLine(1, "post 2,b", 1);
            db.writeLine(2, "post 3,c", 1);

            db.writeLine(1, "user2,data1", 0);
            db.writeLine(0, "user1,data2", 0);
            db.writeLine(2, "user4,data3", 0);
            db.writeLine(3, "user3,data4", 0);

            db.writeLine(1, "post 2,b1", 1);
            db.writeLine(0, "post 1,a2", 1);
            db.writeLine(2, "post 4,c3", 1);
            db.writeLine(3, "post 3,d4", 1);

            Assert.assertEquals("User line index 0 not modified correctly!", "user1,data2", db.readLine(0, 0));
            Assert.assertEquals("User line index 1 not modified correctly!", "user2,data1", db.readLine(1, 0));
            Assert.assertEquals("User line index 2 not modified correctly!", "user4,data3", db.readLine(2, 0));
            Assert.assertEquals("User line index 3 not added correctly!", "user3,data4", db.readLine(3, 0));

            Assert.assertEquals("Post line index 0 not modified correctly!", "post 1,a2", db.readLine(0, 1));
            Assert.assertEquals("Post line index 1 not modified correctly!", "post 2,b1", db.readLine(1, 1));
            Assert.assertEquals("Post line index 2 not modified correctly!", "post 4,c3", db.readLine(2, 1));
            Assert.assertEquals("Post line index 3 not added correctly!", "post 3,d4", db.readLine(3, 1));
        }

        // Added loadUser and loadPosts test case
        @Test(timeout = 1000)
        public void runTestDatabaseLoadUsers() throws IOException {
            Database db = new Database(USERFILE, POSTFILE);

            db.writeLine(0, "user1,data1", 0);
            db.writeLine(1, "user2,data2", 0);
            db.writeLine(2, "user3,data3", 0);

            db.loadUsers();

            Assert.assertEquals("User index 0 should be loaded correctly!", "user1,data1", db.readLine(0, 0));
            Assert.assertEquals("User index 1 should be loaded correctly!", "user2,data2", db.readLine(1, 0));
            Assert.assertEquals("User index 2 should be loaded correctly!", "user3,data3", db.readLine(2, 0));
        }

        @Test(timeout = 1000)
        public void runTestDatabaseLoadPosts() throws IOException {
            Database db = new Database(USERFILE, POSTFILE);

            db.writeLine(0, "post1,data1", 1);
            db.writeLine(1, "post2,data2", 1);
            db.writeLine(2, "post3,data3", 1);

            db.loadPosts();

            Assert.assertEquals("Post index 0 should be loaded correctly!", "post1,data1", db.readLine(0, 1));
            Assert.assertEquals("Post index 1 should be loaded correctly!", "post2,data2", db.readLine(1, 1));
            Assert.assertEquals("Post index 2 should be loaded correctly!", "post3,data3", db.readLine(2, 1));
        }

        @Test(timeout = 1000)
        public void runTestDataPersistence() throws IOException {
            resetFiles();
            Database db = new Database(USERFILE, POSTFILE);

            db.writeLine(0, "user1,data", 0);
            db.writeLine(1, "user2,data", 0);
            db.writeLine(2, "user3,data", 0);

            db.writeLine(0, "post 1,a", 1);
            db.writeLine(1, "post 2,b", 1);
            db.writeLine(2, "post 3,c", 1);

            db = new Database(USERFILE, POSTFILE);

            Assert.assertEquals("User line index 0 didnt persist properly!", "user1,data", db.readLine(0, 0));
            Assert.assertEquals("User line index 1 didnt persist properly!", "user2,data", db.readLine(1, 0));
            Assert.assertEquals("User line index 2 didnt persist properly!", "user3,data", db.readLine(2, 0));

            Assert.assertEquals("Post line index 0 didnt persist properly!", "post 1,a", db.readLine(0, 1));
            Assert.assertEquals("Post line index 1 didnt persist properly!", "post 2,b", db.readLine(1, 1));
            Assert.assertEquals("Post line index 2 didnt persist properly!", "post 3,c", db.readLine(2, 1));
        }

        @Test(timeout = 1000)
        public void runTestSearch() throws IOException, BadUserException, BadPostException {
            resetFiles();
            Database db = new Database(USERFILE, POSTFILE);

            User a = new User("alice", "Password123!", "bio1", new String[]{"books"}, db);
            User b = new User("birdFacts", "IloveB1rds!", "bio2", new String[]{"birds"}, db);
            User c = new User("cars", "CarsRC00l!", "bio3", new String[]{"cars"}, db);

            ArrayList<User> s1 = db.searchUsers("a");
            ArrayList<User> s1E = new ArrayList<User>();
            s1E.add(a);
            s1E.add(b);
            s1E.add(c);

            Assert.assertEquals("Search failed!", s1E, s1);

            ArrayList<User> s2 = db.searchUsers("BiRd");
            ArrayList<User> s2E = new ArrayList<User>();
            s2E.add(b);

            Assert.assertEquals("Search failed!", s2E, s2);

            ArrayList<User> s3 = db.searchUsers("school");
            ArrayList<User> s3E = new ArrayList<User>();

            Assert.assertEquals("Search failed!", s3E, s3);

            Post p = new Post("food", "user1", "Post about food.", db);
            Post q = new Post("news", "user2", "News post", db);
            Post r = new Post("automobiles", "cars", "Fun post", db);

            ArrayList<Post> ps1 = db.searchPosts("post", 0);
            ArrayList<Post> ps1E = new ArrayList<Post>();
            // order reversed, searchPosts() returns newest first
            ps1E.add(r);
            ps1E.add(q);
            ps1E.add(p);

            Assert.assertEquals("Search failed!", ps1E, ps1);

            ArrayList<Post> ps2 = db.searchPosts("car", 0);
            ArrayList<Post> ps2E = new ArrayList<Post>();
            ps2E.add(r);

            Assert.assertEquals("Search failed!", ps2E, ps2);

            ArrayList<Post> ps3 = db.searchPosts("school", 0);
            ArrayList<Post> ps3E = new ArrayList<Post>();

            Assert.assertEquals("Search failed!", ps3E, ps3);
        }

        @Test(timeout = 1000)
        public void runTestGetPosts() throws IOException, BadPostException {
            resetFiles();
            Database db = new Database(USERFILE, POSTFILE);

            Post p = new Post("food", "user1", "Post about food.", db);
            Post q = new Post("news", "user2", "News post", db);
            Post r = new Post("automobiles", "cars", "Fun post", db);
            Post s = new Post("news", "cars", "Bob won the race", db);

            ArrayList<Post> q1 = db.getPosts(new String[]{"food"});
            ArrayList<Post> q1E = new ArrayList<Post>();
            q1E.add(p);
            q1E.add(r);
            q1E.add(s);
            q1E.add(q);

            Assert.assertEquals("Category query failed!", q1E, q1);

            ArrayList<Post> q2 = db.getPosts(new String[]{"news"});
            ArrayList<Post> q2E = new ArrayList<Post>();
            q2E.add(q);
            q2E.add(s);
            q2E.add(r);
            q2E.add(p);

            Assert.assertEquals("Category query failed!", q2E, q2);

            ArrayList<String> friends = new ArrayList<String>();
            friends.add("user1");
            friends.add("user2");
            friends.add("cars");

            ArrayList<Post> q4 = db.getPostsByFriends(friends);
            ArrayList<Post> q4E = new ArrayList<Post>();
            q4E.add(s);
            q4E.add(r);
            q4E.add(q);
            q4E.add(p);

            Assert.assertEquals("Friends query failed!", q4E, q4);

            friends.remove("user1");

            ArrayList<Post> q5 = db.getPostsByFriends(friends);
            ArrayList<Post> q5E = new ArrayList<Post>();
            q5E.add(s);
            q5E.add(r);
            q5E.add(q);

            Assert.assertEquals("Friends query failed!", q5E, q5);

            friends.remove("cars");

            ArrayList<Post> q6 = db.getPostsByFriends(friends);
            ArrayList<Post> q6E = new ArrayList<Post>();
            q6E.add(q);

            Assert.assertEquals("Friends query failed!", q6E, q6);
        }

        @Test(timeout = 1000)
        public void runtTestThrows() throws IOException, BadUserException, BadPostException {
            resetFiles();
            Database db = new Database(USERFILE, POSTFILE);

            User a = new User("alice", "Password123!", "bio1", new String[]{"books"}, db);
            User b = new User("birdFacts", "IloveB1rds!", "bio2", new String[]{"birds"}, db);
            User c = new User("cars", "CarsRC00l!", "bio3", new String[]{"cars"}, db);

            Post p = new Post("food", "user1", "Post about food.", db);
            Post q = new Post("news", "user2", "News post", db);
            Post r = new Post("automobiles", "cars", "Fun post", db);

            boolean caught = false;

            try {
                db.getUser("non-existent");
            } catch (BadUserException e) {
                caught = true;
            }

            Assert.assertEquals("Didn't catch bad username!", true, caught);

            caught = false;

            try {
                db.getPost("-1");
            } catch (BadPostException e) {
                caught = true;
            }

            Assert.assertEquals("Didn't catch bad post ID!", true, caught);
        }
    }
}

