/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duckspot.gas;

import static com.duckspot.gas.AbstractTest.templatePath;
import com.duckspot.gas.model.Session;
import com.duckspot.gas.model.User;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
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
public class AbstractServletTest extends AbstractTest {        
    
    public class AbstractServletImpl extends AbstractServlet {                
    }
    
    AbstractServlet instance;
    
    public AbstractServletTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    @Override
    public void setUp() throws ServletException {
        super.setUp();
        instance = new AbstractServletImpl();
        instance.templatesPath = templatePath;
    }
    
    @After
    @Override
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void testLoadTemplate() {
        System.out.println("loadTemplate");
        String name = "gas/auth";
        instance.loadTemplate(name);
        assertTrue(instance.template != null);
    }

    @Test
    public void testGetCookie() {
        System.out.println("getCookie");
        testRequest.setCookies(new Cookie[0]);
        String key = "token";
        String expResult = null;
        String result = instance.getCookie(request, key);
        assertEquals(expResult, result);
        expResult = "test value";        
        testRequest.setCookies(new Cookie[] { new Cookie("token", expResult) });
        result = instance.getCookie(request, key);
        assertEquals(expResult, result);        
    }
    
    @Test
    public void testGetSession() {
        System.out.println("getSession");
        Session expResult = null;
        Session result = instance.getSession(request);
        assertEquals(expResult, result);
        testRequest.setCookies(new Cookie[] { new Cookie("token", "foo") });
        result = instance.getSession(request);
        assertEquals(expResult, result);
        String userEmail = "someone@somewhere.com";
        User someone = new User(userEmail);
        someone.setPassword(null, "password");
        String token = someone.newSession("password", 1).getToken();
        testRequest.setCookies(new Cookie[] { new Cookie("token", token) });
        result = instance.getSession(request);
        assertEquals(userEmail, result.getUser().getEmail());        
    }

    @Test
    public void testOutputHtml() {
        System.out.println("outputHtml");                
        Map<String, String> model = new HashMap<String, String>();
        instance.loadTemplate("gas/home");
        instance.outputHtml(response, model);
        String output = testResponse.getOutput();
        assertEquals("</html>", output.substring(output.length()-7));
    }

    @Test
    public void testOutputJson() {
        System.out.println("outputJson");
        Map<String, String> model = new HashMap<String, String>();
        model.put("one","1");
        model.put("two","2");
        instance.outputJson(response, model);
        String output = testResponse.getOutput().replaceAll("\\s", "");        
        String v1 = "{\"one\":\"1\",\"two\":\"2\"}";
        String v2 = "{\"two\":\"2\",\"one\":\"1\"}";
        assertTrue(output.equals(v1) || output.equals(v2));
    }
}