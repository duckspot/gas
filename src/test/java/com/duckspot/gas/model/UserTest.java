/* gas: Google Application Server
 * duckspot.com
 * 
 * mvn/src/test/java/com/duckspot/gas/model/UserTest.java
 */
package com.duckspot.gas.model;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import java.util.regex.Pattern;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class UserTest {
    
    Pattern hex12 = Pattern.compile("[0-9a-f]{12}");
    User someone;
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalTaskQueueTestConfig(), new LocalDatastoreServiceTestConfig());

    public UserTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        helper.setUp();
        someone = new User("someone@somewhere.com");
        someone.setPassword(null, "password");
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testMakeSalt() {
        System.out.println("makeSalt");
        for (int i=0; i<30; i++) {
            String result = User.makeSalt();
            assertTrue(hex12.matcher(result).matches());
        }
    }

    @Test
    public void testGetByEmail() {
        System.out.println("getByEmail");
        String email = "noone@nowhere.com";
        User expResult = null;
        User result = User.getByEmail(email);
        assertEquals(expResult, result);
        email = "someone@somewhere.com";
        result = User.getByEmail(email);
        assertTrue(result != null);
    }

    @Test
    public void testIsEmailInUse() {
        System.out.println("isEmailInUse");
        String email = "noone@nowhere.com";
        boolean expResult = false;
        boolean result = User.isEmailInUse(email);
        assertEquals(expResult, result);
        email = "someone@somewhere.com";
        expResult = true;
        result = User.isEmailInUse(email);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetKey() {
        System.out.println("getKey");
        User instance = new User("noone@nowhere.com");
        Key result = instance.getKey();
        assertTrue(result != null);
    }

    @Test
    public void testGetSalt() {
        System.out.println("getSalt");
        User instance = new User("noone@nowhere.com");
        String expResult = instance.getSalt();
        String result = instance.getSalt();
        assertEquals(expResult, result);        
    }

    @Test
    public void testNextSalt() {
        System.out.println("nextSalt");
        User instance = new User("noone@nowhere.com");
        String expResult = instance.nextSalt();
        String result = instance.nextSalt();
        assertEquals(expResult, result);     
    }

    @Test
    public void testSetPassword() {
        System.out.println("setPassword");
        String oldPassword = null;
        String newPassword = "password";
        User instance =  new User("newuser@somewhere.com");
        boolean expResult = true;
        boolean result = instance.setPassword(oldPassword, newPassword);
        assertEquals(expResult, result);       
    }

    @Test
    public void testCheckPassword() {
        System.out.println("checkPassword");
        // setup user with password        
        String password = "password";
        User instance =  new User("newuser@somewhere.com");
        instance.setPassword(null, password);
        // check correct password        
        boolean expResult = true;
        boolean result = instance.checkPassword(password);
        assertEquals(expResult, result);
        // check wrong password        
        password = "wrong_password";
        expResult = false;
        result = instance.checkPassword(password);
        assertEquals(expResult, result);
    }

    @Test
    public void testNewSession() {
        System.out.println("newSession");
        String password = "password";
        int days = 1;
        User instance = User.getByEmail("someone@somewhere.com");
        Session result = instance.newSession(password, days);
        assertTrue(result instanceof Session);        
    }

    @Test
    public void testGetEmail() {
        System.out.println("getEmail");
        User instance = User.getByEmail("someone@somewhere.com");
        String expResult = "someone@somewhere.com";
        String result = instance.getEmail();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetEmail() {
        System.out.println("setEmail");
        User instance = User.getByEmail("someone@somewhere.com");
        String email = "someone@elsewhere.com";
        instance.setEmail(email);
        String expResult = email;
        String result = instance.getEmail();
        assertEquals(expResult, result);
    }

    @Test
    public void testSave() {
        System.out.println("save");
        User instance = User.getByEmail("someone@somewhere.com");
        String email = "someone@elsewhere.com";
        instance.setEmail(email);
        instance.save();
        instance = User.getByEmail(email);
        String expResult = email;
        String result = instance.getEmail();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetHashSaltPw() {
        // tested by setPassword()
    }
}