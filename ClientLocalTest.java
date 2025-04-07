import org.junit.Test;
import org.junit.After;
import java.lang.reflect.Field;
import org.junit.Assert;
import org.junit.Before;
import org.junit.rules.Timeout;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


/**
 * Team Project -- Client Tests
 *
 * A framework to run public test cases for Client.
 *
 * @author Ryan Jo, lab sec 002
 *
 * @version Nov 17, 2024
 */

public class ClientLocalTest {
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
     * Client test cases
     *
     * Contains test for non IO methods which are only accessed through Client.
     * Notice: This test relies on a Socket connection since the Client can only
     * be initialized correctly when connected to the Server or when the user 
     * dismisses the JOptionPane error message. To make this test run succesfully,
     * the JOptionPane error message must be exited so the password helper method can
     * run correctly.
     *
     * @version Nov 17, 2024
    **/

    public static class TestCase {

        @Test(timeout = 10000)
        public void runTestGetters() {
            
            Client client = new Client();

            boolean password1 = client.acceptablePassword("Password1234!");
            boolean password2 = client.acceptablePassword("IncorrectSymbol++");
            boolean password3 = client.acceptablePassword("No Spaces Allowed");
            boolean password4 = client.acceptablePassword("not7");
            boolean password5 = client.acceptablePassword("nocapitalletters");

            Assert.assertEquals("acceptablePassword returned an incorrect value", true, password1);
            Assert.assertEquals("acceptablePassword returned an incorrect value", false, password2);
            Assert.assertEquals("acceptablePassword returned an incorrect value", false, password3);
            Assert.assertEquals("acceptablePassword returned an incorrect value", false, password4);
            Assert.assertEquals("acceptablePassword returned an incorrect value", false, password5);
        }
    }
}
