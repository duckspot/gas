/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duckspot.gas;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Peter Dobson
 */
public class AbstractTest {        
    
    final static String templatePath = "src/main/webapp/templates";
    
    protected final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalTaskQueueTestConfig(), new LocalDatastoreServiceTestConfig());

    TestRequest testRequest;
    HttpServletRequest request;
    TestResponse testResponse;
    HttpServletResponse response;    
    
    public AbstractTest() {
    }
    
    public static void setUpClass() {
    }
    
    public static void tearDownClass() {
    }
    
    public void setUp() throws ServletException {
        helper.setUp();
        testRequest = new TestRequest();
        request = (HttpServletRequest)testRequest;
        testResponse = new TestResponse();
        response = (HttpServletResponse)testResponse;
    }
    
    public void tearDown() {
        helper.tearDown();
    }
}