import org.junit.Test;
import org.junit.After;
import java.lang.reflect.Field;
import org.junit.Assert;
import org.junit.Before;
import org.junit.rules.Timeout;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import static org.junit.Assert.*;


/**
 * Team Project -- User Tests
 *
 * A framework to run public test cases for User.
 *
 * @author Ryan Jo, lab sec 002
 * @author Omkar Govil-Nair, lab sec 002
 *
 * @version Nov 3, 2024
 */

public class UserLocalTest {
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
     * User test cases
     *
     * Contains test for methods which are only accessed through User.
     *
     * @version Nov 3, 2024
    **/

    public static class TestCase implements UserLocalTestInterface  {
        private static final String USERFILE = "Users.txt";
        private static final String POSTFILE = "Posts.txt";
        private static boolean testBool = false;

        public void resetFiles() throws IOException {
            BufferedWriter bw1 = new BufferedWriter(new FileWriter(USERFILE, false));
            BufferedWriter bw2 = new BufferedWriter(new FileWriter(POSTFILE, false));

            bw1.write("");
            bw2.write("");

            bw1.close();
            bw2.close();            
        }

        @Test(timeout = 500)
        public void runTestUserConstructors() throws IOException, BadUserException {
            resetFiles();
            Database database = new Database(USERFILE, POSTFILE);
            
            database.writeLine(0, "Bob,Password1234!,a cool guy,2024-12-4,basketball~gaming~music,Dylan~Arlo,,", 0);
            database.writeLine(1, "Dylan,!4321drowssaP,not a cool guy,2024-3-6,coding stuff,Bob,,", 0);
            database.writeLine(2, "Arlo,Hello_World,just a guy,2024-3-2,food,Bob~Dylan,Bob,", 0);

            String time = java.time.LocalDateTime.now().toString();
            String timeFormatted = time.substring(0, time.indexOf("T"));

            //constructor 1
            User user1 = new User("Bob,Password1234!,a cool guy,2024-12-4," + 
                                "basketball~gaming~music,Dylan~Arlo,,", database);
            User user2 = new User("Dylan,!4321drowssaP,not a cool guy,2024-3-6,coding stuff,Bob,,", database);
            User user3 = new User("Arlo,Hello_World,just a guy,2024-3-2,food,Bob~Dylan,Bob,", database);

            try {
                testBool = false;
                User user4 = new User("Ryan,,just a guy,2024-6-2,programming,,Bob,,,,", database);
            } catch (BadUserException bue) {
                testBool = true;
            }

            Assert.assertEquals("Catching user constructor error failed!", true, testBool);
            Assert.assertEquals("Reading user line index 0 failed!", 
                        "Bob,Password1234!,a cool guy,2024-12-4,basketball~gaming~music,Dylan~Arlo,,", 
                        database.readLine(0, 0));
            Assert.assertEquals("Reading user line index 1 failed!", 
                    "Dylan,!4321drowssaP,not a cool guy,2024-3-6,coding stuff,Bob,,", database.readLine(1, 0));
            Assert.assertEquals("Reading user line index 2 failed!", 
                    "Arlo,Hello_World,just a guy,2024-3-2,food,Bob~Dylan,Bob,", database.readLine(2, 0));

            //constructor 2
            User user5 = new User("Bob", "Password1234!", database);
            try {
                testBool = false;
                user1 = new User("Dylan", "Wrong_password", database);
            } catch (BadUserException bue) {
                testBool = true;
            }

            Assert.assertEquals("Catching user constructor error failed!", true, testBool);

            try {
                testBool = false;
                user1 = new User("User_Doesn't_Exist", "Password1234!", database);
            } catch (BadUserException bue) {
                testBool = true;
            }

            Assert.assertEquals("Logging in user failed!", "Bob,Password1234!," + 
                                "a cool guy,2024-12-4,basketball~gaming~music,Dylan~Arlo,,", user5.toString());

            //constructor 3
            user1 = new User("Alice", "Hello_World",  
                        "A Purdue student", new String[] {"Drawing", "Reading"}, database);
            try {
                testBool = false;
                user2 = new User("John", "invalid_password",  
                            "A Purdue student", new String[] {"Drawing", "Reading"}, database);
            } catch (BadUserException bue) {
                testBool = true;
            }

            Assert.assertEquals("Catching user constructor error failed!", true, testBool);

            try {
                testBool = false;
                user2 = new User("Alice", "Hello_World",  
                            "A Purdue student", new String[] {"Drawing", "Reading"}, database);
            } catch (BadUserException bue) {
                testBool = true;
            }

            Assert.assertEquals("Catching user constructor error failed!", true, testBool);

            Assert.assertEquals("Creating new user failed!", "Alice,Hello_World,A Purdue student," + 
                                timeFormatted + ",Drawing~Reading,,,", database.readLine(3, 0));

        }

        @Test(timeout = 500)
        public void runTestAcceptablePassword() throws IOException, BadUserException {
            resetFiles();
            Database database = new Database(USERFILE, POSTFILE);

            database.writeLine(0, "Bob,Password1234!,a cool guy," + 
                                "2024-12-4,basketball~gaming~music,Dylan~Arlo,,", 0);


            User user1 = new User("Bob,Password1234!,a cool guy,2024-12-4," + 
                                "basketball~gaming~music,Dylan~Arlo,,", database);

            testBool = user1.acceptablePassword("HelloWorld");
            Boolean testBool2 = user1.acceptablePassword("CorrectPassword!");
            Boolean testBool3 = user1.acceptablePassword("Spaces not allowed!");

            Assert.assertEquals("AcceptablePassword returned an incorrect value", false, testBool);
            Assert.assertEquals("AcceptablePassword returned an incorrect value", true, testBool2);
            Assert.assertEquals("AcceptablePassword returned an incorrect value", false, testBool3);
        }

        @Test(timeout = 500)
        public void runTestGetters() throws IOException, BadUserException {
            resetFiles();
            Database database = new Database(USERFILE, POSTFILE);
            
            database.writeLine(0, "Bob,Password1234!,a cool guy,2024-12-4,basketball~gaming~music,Dylan,Arlo,", 0);
            database.writeLine(1, "Dylan,!4321drowssaP,not a cool guy,2024-3-6,coding stuff,Bob,,", 0);
            database.writeLine(2, "Arlo,Hello_World,just a guy,2024-3-2,food,Bob~Dylan,Bob,", 0);

            User user1 = new User("Bob,Password1234!,a cool guy,2024-12-4," + 
                                "basketball~gaming~music,Dylan,Arlo,", database);
            String expectedUser = "Bob,Password1234!,a cool guy,2024-12-4," +
                                "basketball~gaming~music,Dylan,Arlo,";
            ArrayList<String> expectedInterests = new ArrayList<>();
            expectedInterests.add("basketball");
            expectedInterests.add("gaming");
            expectedInterests.add("music");

            ArrayList<String> expectedFriends = new ArrayList<>();
            expectedFriends.add("Dylan");

            ArrayList<String> expectedBlocked = new ArrayList<>();
            expectedBlocked.add("Arlo");

            Assert.assertEquals("getUsername returned an incorrect value", "Bob", user1.getUsername());
            Assert.assertEquals("getPassword returned an incorrect value", "Password1234!", user1.getPassword());
            Assert.assertEquals("getBio returned an incorrect value", "a cool guy", user1.getBio());
            Assert.assertEquals("getDateCreated returned an incorrect value", "2024-12-4", user1.getDateCreated());
            Assert.assertEquals("getInterests returned an incorrect value", expectedInterests, user1.getInterests());
            Assert.assertEquals("getFriends returned an incorrect value", expectedFriends, user1.getFriends());
            Assert.assertEquals("getBlocked returned an incorrect value", expectedBlocked, user1.getBlocked());
        }

        @Test(timeout = 500)
        public void runTestModifyFriend() throws IOException, BadUserException {
            resetFiles();
            Database database = new Database(USERFILE, POSTFILE);
            
            database.writeLine(0, "Bob,Password1234!,a cool guy,2024-12-4,basketball~gaming~music,Dylan,Arlo,", 0);
            database.writeLine(1, "Dylan,!4321drowssaP,not a cool guy,2024-3-6,coding stuff,Bob,,", 0);
            database.writeLine(2, "Arlo,Hello_World,just a guy,2024-3-2,food,Bob~Dylan,Bob,", 0);

            String time = java.time.LocalDateTime.now().toString();
            String timeFormatted = time.substring(0, time.indexOf("T"));

            User user1 = new User("Bob,Password1234!,a cool guy,2024-12-4," + 
                                "basketball~gaming~music,Dylan,Arlo,", database);
            User user2 = new User("Jacob", "This_Is_My_Password", 
                            "I like to code", new String[] {"driving"}, database);          

            user1.modifyFriend("Dylan");
            user1.modifyFriend("Jacob");

            user2.modifyFriend("UserDoesNotExist");
            user2.modifyFriend("Bob");
            user2.modifyFriend("Dylan");

            Assert.assertEquals("User1's friends are incorrect", "Bob,Password1234!,a cool guy," + 
                                "2024-12-4,basketball~gaming~music,Jacob,Arlo,", database.readLine(0, 0));
            Assert.assertEquals("User1's friends are incorrect", "Jacob,This_Is_My_Password,I like to code," + 
                                timeFormatted + ",driving,Bob~Dylan,,", database.readLine(3, 0));
        }

        @Test(timeout = 500)
        public void runTestModifyBlocked() throws IOException, BadUserException {
            resetFiles();
            Database database = new Database(USERFILE, POSTFILE);
            
            database.writeLine(0, "Bob,Password1234!,a cool guy,2024-12-4,basketball~gaming~music,Dylan,Arlo,", 0);
            database.writeLine(1, "Dylan,!4321drowssaP,not a cool guy,2024-3-6,coding stuff,Bob,,", 0);
            database.writeLine(2, "Arlo,Hello_World,just a guy,2024-3-2,food,Bob~Dylan,Bob,", 0);

            String time = java.time.LocalDateTime.now().toString();
            String timeFormatted = time.substring(0, time.indexOf("T"));

            User user1 = new User("Bob,Password1234!,a cool guy,2024-12-4," + 
                                "basketball~gaming~music,Dylan,Arlo,", database);
            User user2 = new User("Jacob", "This_Is_My_Password", 
                            "I like to code", new String[] {"driving"}, database);          

            user1.modifyBlocked("Dylan");
            user1.modifyBlocked("Arlo");
            user1.modifyBlocked("UserDoesNotExist");
            user1.modifyBlocked("Jacob");

            user2.modifyBlocked("Bob");
            user2.modifyBlocked("Bob");
            user2.modifyBlocked("Arlo");

            Assert.assertEquals("User1's friends are incorrect", "Bob,Password1234!,a cool guy," +
                                "2024-12-4,basketball~gaming~music,Dylan,Dylan~Jacob,", database.readLine(0, 0));
            Assert.assertEquals("User1's friends are incorrect", "Jacob,This_Is_My_Password," +
                                "I like to code," + timeFormatted + ",driving,,Arlo,", database.readLine(3, 0));
        }

        @Test(timeout = 500)
        public void runTestEquals() throws IOException, BadUserException {
            resetFiles();
            Database database = new Database(USERFILE, POSTFILE);

            User user1 = new User("Bob,Password1234!,a cool guy,2024-12-4," + 
                                "basketball~gaming~music,Dylan,Arlo,", database);
            User user2 = new User("Bob,Password1234!,a cool guy,2024-12-4,,,,", database);   
            User user3 = new User("Bobby,Password1234!,a cool guy,2024-12-4," + 
                                "basketball~gaming~music,Dylan,Arlo,", database);      

            Assert.assertEquals("User's equals method returns an incorrect value!", true, user1.equals(user2));
            Assert.assertEquals("User's equals method returns an incorrect value!", false, user1.equals(user3));
            Assert.assertEquals("User's equals method returns an incorrect value!", true, user3.equals(user3));
        }

        @Test(timeout = 500)
        public void runTestToString() throws IOException, BadUserException {
            resetFiles();
            Database database = new Database(USERFILE, POSTFILE);
            
            database.writeLine(0, "Bob,Password1234!,a cool guy,2024-12-4,basketball~gaming~music,Dylan,Arlo,", 0);
            database.writeLine(1, "Dylan,!4321drowssaP,not a cool guy,2024-3-6,coding stuff,Bob,,", 0);
            database.writeLine(2, "Arlo,Hello_World,just a guy,2024-3-2,food,Bob~Dylan,Bob,", 0);

            String time = java.time.LocalDateTime.now().toString();
            String timeFormatted = time.substring(0, time.indexOf("T"));

            User user1 = new User("Bob,Password1234!,a cool guy,2024-12-4," + 
                                   "basketball~gaming~music,Dylan,Arlo,", database);
            User user2 = new User("Jake", "pAssWorD12!", "", new String[] {}, database);

            Assert.assertEquals("User's toString method returns an incorrect value!", 
                      "Bob,Password1234!,a cool guy,2024-12-4,basketball~gaming~music,Dylan,Arlo,", user1.toString());
            Assert.assertEquals("User's toString method returns an incorrect value!", 
                               "Jake,pAssWorD12!,," + timeFormatted + ",,,,", user2.toString());
        }
    }
}
