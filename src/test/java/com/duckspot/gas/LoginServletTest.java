/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duckspot.gas;

import com.duckspot.gas.model.User;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import org.json.JSONObject;
import org.json.JSONTokener;
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
public class LoginServletTest extends AbstractTest {
    
    LoginServlet instance;
    
    public LoginServletTest() {
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
        instance = new LoginServlet();
        instance.templatesPath = "src/main/webapp/templates";
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testInit() throws ServletException {
        System.out.println("init");
        instance.init();        
    }

    @Test
    public void testDoGet() throws Exception {
        System.out.println("doGet");
        JSONObject model;
        instance.init();
        instance.doGet(request, response);
        assertTrue(testResponse.getOutput().contains("</html>"));
        testRequest.addHeader("accept","json");        
        instance.doGet(request, response);
        model = new JSONObject(new JSONTokener(testResponse.getOutput()));
        assertEquals("", model.get("isLoggedIn"));
        String userEmail = "someone@somewhere.com";        
        User someone = new User(userEmail);
        someone.setPassword(null, "password");
        String token = someone.newSession("password", 1).getToken();
        testRequest.setCookies(new Cookie[] { new Cookie("token", token) });
        instance.doGet(request, response);
        model = new JSONObject(new JSONTokener(testResponse.getOutput()));
        assertEquals("true", model.get("isLoggedIn"));
        assertEquals("/", model.get("redirect"));
        assertTrue(model.has("token"));
    }
    
    @Test
    public void testDoPost() throws Exception {
        System.out.println("doPost");
        JSONObject model;
        instance.init();
        instance.doPost(request, response);
        assertTrue(testResponse.getOutput().contains("</html>"));
        testRequest.addHeader("accept","json");
        instance.doPost(request, response);
        assertEquals(403, testResponse.statusCode);
        assertEquals("Username or password incorrect", testResponse.statusMessage);
        String userEmail = "someone@somewhere.com";
        User someone = new User(userEmail);
        someone.setPassword(null, "password");
        String token = someone.newSession("password", 1).getToken();
        testRequest.setCookies(new Cookie[] { new Cookie("token", token) });
        instance.doPost(request, response);
        System.out.println(testResponse);
        model = new JSONObject(new JSONTokener(testResponse.getOutput()));
        assertEquals("true", model.get("isLoggedIn"));
        assertEquals("/", model.get("redirect"));
        assertTrue(model.has("token"));
    }
}