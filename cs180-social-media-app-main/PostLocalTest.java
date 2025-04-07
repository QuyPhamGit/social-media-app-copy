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

import java.util.Date;
import java.util.ArrayList;

/**
 * Team Project -- Post Tests
 *
 * A framework to run public test cases for Post.
 * NOTE: it may be possible for tests to erroneously fail because of
 * delay/bad timing in getting current timestamp.
 *
 * @author Omkar Govil-Nair, lab sec 002
 *
 * @version Nov 3, 2024
 */
public class PostLocalTest {

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
     * Post Test Cases
     *
     * Contains test for Post's constructors and methods.
     *
     * @version Nov 3, 2024
     */
    public static class TestCase implements PostTestInterface {
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

        @Test(timeout = 500)
        public void runTestPostConstructors() throws IOException, BadPostException {
            resetFiles();
            Database db = new Database(USERFILE, POSTFILE);

            Post p = new Post("food", "user1", "Post about food.", db);
            String timestamp = p.getDateString(new Date());

            String fileLine = db.readLine(0, 1);

            String expected = "0,food,user1,Post about food.," + timestamp + ",0,0,0";

            Assert.assertEquals("Post wasn't written to file correctly!", expected, fileLine);
            Assert.assertEquals("Post didn't store values correctly!", expected, p.toString());

            Post p2 = new Post(fileLine, db);

            Assert.assertEquals("Post DB constructor failed!", p.toString(), p2.toString());

            Post p3 = new Post("news", "user2", "News post", db);
            timestamp = p3.getDateString(new Date());
            expected = "1,news,user2,News post," + timestamp + ",0,0,0";

            Assert.assertEquals("Constructing multiple Posts failed!", expected, db.readLine(1, 1));
        }

        @Test(timeout = 1000)
        public void runTestPostViewsAndGetPostFromID() throws IOException, BadPostException {
            resetFiles();
            Database db = new Database(USERFILE, POSTFILE);

            Post p = new Post("food", "user1", "Post about food.", db);
            String timestamp = p.getDateString(new Date());
            String id = p.getPostID();
            String expected = "0,food,user1,Post about food.," + timestamp + ",1,0,0";

            p.incrementViews();
            Assert.assertEquals("Views not incrementing correctly!", expected, db.readLine(0, 1));

            p.incrementViews();
            Post p2 = new Post(db.getPost(id), db);
            expected = "0,food,user1,Post about food.," + timestamp + ",2,0,0";
            Assert.assertEquals("Views not incrementing correctly!", expected, p2.toString());
        }

        @Test(timeout = 1000)
        public void runTestPostLikesDislikes() throws IOException, BadPostException {
            resetFiles();
            Database db = new Database(USERFILE, POSTFILE);

            Post p = new Post("food", "user1", "Post about food.", db);
            String timestamp = p.getDateString(new Date());
            String id = p.getPostID();

            // Add likes
            p.changePostRatio(true, true);
            p.changePostRatio(true, true);
            p.changePostRatio(true, true);
            p.changePostRatio(true, false);

            // Add dislikes
            p.changePostRatio(false, true);
            p.changePostRatio(false, true);
            p.changePostRatio(false, false);

            Post p2 = new Post(db.getPost(id), db);
            Assert.assertEquals("Likes not updated correctly!", 2, p2.getLikes());
            Assert.assertEquals("Dislikes not updated correctly!", 1, p2.getDislikes());
        }

        @Test(timeout = 1000)
        public void runTestComments() throws IOException, BadPostException {
            resetFiles();
            Database db = new Database(USERFILE, POSTFILE);

            Post p = new Post("food", "user1", "Post about food.", db);
            String timestamp = p.getDateString(new Date());
            String id = p.getPostID();

            p.addComment("comment1", "");
            p.addComment("comment2", "");
            p.addComment("comment3", "");

            Post p2 = new Post(db.getPost(id), db);
            ArrayList<String> comments = p2.getComments();

            Assert.assertEquals("Failed to add comment!", ";0;0;0;comment1", comments.get(0));
            Assert.assertEquals("Failed to add comment!", ";1;0;0;comment2", comments.get(1));
            Assert.assertEquals("Failed to add comment!", ";2;0;0;comment3", comments.get(2));

            p.changeCommentRatio("0", true, true);
            p.changeCommentRatio("0", true, true);
            p.changeCommentRatio("0", true, false);

            p.changeCommentRatio("1", true, true);
            p.changeCommentRatio("1", true, true);

            p.changeCommentRatio("2", true, true);
            p.changeCommentRatio("2", true, true);
            p.changeCommentRatio("2", true, true);

            p.changeCommentRatio("0", false, true);
            p.changeCommentRatio("0", false, true);
            p.changeCommentRatio("0", false, true);

            p.changeCommentRatio("1", false, true);
            p.changeCommentRatio("1", false, true);

            p.changeCommentRatio("2", false, true);
            p.changeCommentRatio("2", false, true);
            p.changeCommentRatio("2", false, false);

            p2 = new Post(db.getPost(id), db);
            comments = p2.getComments();

            Assert.assertEquals("Failed to update comment like/dislike!", ";0;1;3;comment1", comments.get(0));
            Assert.assertEquals("Failed to update comment like/dislike!", ";1;2;2;comment2", comments.get(1));
            Assert.assertEquals("Failed to update comment like/dislike!", ";2;3;1;comment3", comments.get(2));

            p.removeComment("1");

            p2 = new Post(db.getPost(id), db);
            comments = p2.getComments();
            String result = comments.size() + "-" + comments.get(1);

            Assert.assertEquals("Failed to remove comment!", "2-;2;3;1;comment3", result);
        }

        @Test(timeout = 1000)
        public void runTestHidePost() throws IOException, BadPostException {
            resetFiles();
            Database db = new Database(USERFILE, POSTFILE);

            Post p = new Post("food", "user1", "Post about food.", db);
            String timestamp = p.getDateString(new Date());
            String id = p.getPostID();

            p.hidePost();

            Assert.assertEquals("Failed to set post type to hidden!", "hidden", p.getPostType());

            Post p2 = new Post(db.getPost(id), db);
            Assert.assertEquals("Failed to set post type to hidden!", "hidden", p2.getPostType());
        }

        @Test(timeout = 1000)
        public void runTestBadData() throws IOException {
            resetFiles();
            Database db = new Database(USERFILE, POSTFILE);
            boolean threwException = false;

            try {
                Post p = new Post("a,b,c,d,e,f,g,h,j,k,l", db);
            } catch (BadPostException e) {
                threwException = true;
            }

            Assert.assertEquals("Failed to throw exception!", true, threwException);
            threwException = false;

            try {
                Post p = new Post(null, db);
            } catch (BadPostException e) {
                threwException = true;
            }

            Assert.assertEquals("Failed to throw exception!", true, threwException);
        }
    }
}

