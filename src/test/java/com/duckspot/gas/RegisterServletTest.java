/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duckspot.gas;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
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
public class RegisterServletTest extends AbstractTest {
    
    RegisterServlet instance;
    
    public RegisterServletTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws ServletException {
        super.setUp();
        instance = new RegisterServlet();
        // fix templatesPath to work in test context
        instance.templatesPath = "src/main/webapp/templates";        
        ServletConfig config = new TestServletConfig();
        instance.init(config);
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testInit() {
        System.out.println("init");
        instance.init();
    }

    @Test
    public void testDoGet() throws Exception {
        System.out.println("doGet");
        instance.init();
        instance.doGet(request, response);
        assertTrue(testResponse.getOutput().contains("</html>"));        
    }

    @Test
    public void testDoPost() throws Exception {
        System.out.println("doPost");
        String userEmail = "someone@somewhere.com";
        String userEmail2 = "otherone@elsewhere.com";
        String alreadyRegistered = "email already registered";
        String emailError = "invalid email address";
        String passwordError = "password must have 4 or more characters";
        
        // check POST no email in request, HTML response
        instance.init();
        testResponse.clear();
        instance.doPost(request, response);
        assertEquals(200, testResponse.statusCode);        
        assertTrue(testResponse.getOutput().contains("</html>"));
        assertTrue(testResponse.getOutput().contains(emailError));
                
        // check POST with email but no password, HTML response
        testRequest.addParameter("email", userEmail);
        testResponse.clear();
        instance.doPost(request, response);
        assertEquals(200, testResponse.statusCode);
        assertTrue(testResponse.getOutput().contains("</html>"));
        assertTrue(testResponse.getOutput().contains(passwordError));
        
        // check POST with email & password, HTML response
        testRequest.addParameter("password", "abcd");
        testResponse.clear();
        instance.doPost(request, response);
        assertEquals(200, testResponse.statusCode);
        assertTrue(testResponse.getOutput().contains("</html>"));
        assertTrue(testResponse.hasCookie("token"));
        
        // check duplicate registration post POST, HTML response
        testRequest.addParameter("password", "abcd");
        testResponse.clear();
        instance.doPost(request, response);
        assertEquals(200, testResponse.statusCode);
        assertTrue(testResponse.getOutput().contains("</html>"));
        assertTrue(testResponse.getOutput().contains(alreadyRegistered));
        
        // check POST no email in request, JSON response
        testRequest.clear();
        testRequest.addHeader("accept", "json");
        testResponse.clear();
        instance.doPost(request, response);
        assertEquals(400, testResponse.statusCode);
        assertEquals("invalid email address", testResponse.statusMessage);
        
        // check POST with same email but no password, JSON response
        testRequest.setParameter("email", userEmail);
        testResponse.clear();
        instance.doPost(request, response);
        assertEquals(403, testResponse.statusCode);
        assertTrue(testResponse.statusMessage.contains(alreadyRegistered));
        
        // check POST with email2 but no password, JSON response
        testRequest.setParameter("email", userEmail2);
        testResponse.clear();
        instance.doPost(request, response);
        assertEquals(400, testResponse.statusCode);
        assertEquals(passwordError, testResponse.statusMessage);
        
        // check POST w. email2 & password
        testRequest.setParameter("password", "abcd");
        testResponse.clear();
        instance.doPost(request, response);
        assertEquals(200, testResponse.statusCode);
        assertTrue(testResponse.hasCookie("token"));        
    }
}