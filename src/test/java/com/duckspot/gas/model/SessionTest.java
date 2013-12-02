/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duckspot.gas.model;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Peter Dobson
 */
public class SessionTest {
    
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalTaskQueueTestConfig(), new LocalDatastoreServiceTestConfig());
    
    String token;

    public SessionTest() {
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
        User someone = new User("someone@somewhere.com");
        someone.setPassword(null, "password");
        token = someone.newSession("password", 1).getToken();        
    }
    
    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testGetSession() {
        System.out.println("getSession");
        Session result = Session.getSession(token);
        assertEquals("someone@somewhere.com", result.getUser().getEmail());        
    }

    @Test
    public void testGetToken() {
        System.out.println("getToken");
        Session instance = Session.getSession(token);
        String expResult = token;
        String result = instance.getToken();
        assertEquals(expResult, result);        
    }

    @Test
    public void testGetUser() {
        System.out.println("getUser");
        Session instance = Session.getSession(token);
        User result = instance.getUser();
        assertEquals("someone@somewhere.com", result.getEmail());        
    }

    @Test
    public void testExpireOldSessions() {
        System.out.println("expireOldSessions");
        Session.expireOldSessions();
    }

    @Test
    public void testGetUserKey() {
        // tested by testGetUser()
    }
}