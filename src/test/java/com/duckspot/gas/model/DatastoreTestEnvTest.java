/* gas: Google Application Server
 * duckspot.com
 * 
 * mvn/src/test/java/com/duckspot/gas/model/TestHelperTest.java
 */
package com.duckspot.gas.model;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
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
 * Tests the testing environment provided for Google App Engine's datastore.
 * 
 * @author Peter Dobson
 */
public class DatastoreTestEnvTest {
        
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
            new LocalTaskQueueTestConfig(), new LocalDatastoreServiceTestConfig());

    DatastoreService datastore;
    
    public DatastoreTestEnvTest() {
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
        datastore = DatastoreServiceFactory.getDatastoreService();        
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    private void addEntity() {
        Entity te = new Entity("test");        
        datastore.put(te);
    }
    
    private void checkEntityCount(int expResult) {
        Query query = new Query("test");
        PreparedQuery pq = datastore.prepare(query);
        int result = pq.countEntities(FetchOptions.Builder.withDefaults());
        assertEquals(expResult, result);
    }
    
    @Test
    public void makeFirstTestEntity() {
        System.out.println("make first 'test' entity");
        addEntity();
        checkEntityCount(1);
    }

    @Test
    public void makeSecondTestEntity() {
        System.out.println("make second 'test' entity");
        addEntity();
        checkEntityCount(1);
    }    
}