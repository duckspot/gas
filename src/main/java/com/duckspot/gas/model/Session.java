/* gas: Google Application Server
 * duckspot.com
 * 
 * mvn/src/java/com/duckspot/gas/model/User.java
 */ 
package com.duckspot.gas.model;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

/**
 * Represents a logged in user session.  Session token is stored in cookie on 
 * client, and used to return to the session at a later time.
 * 
 * @author Peter Dobson
 */
public class Session {
    
    private static DatastoreService datastore 
            = DatastoreServiceFactory.getDatastoreService();

    public static Session getSession(String token) {
        Query.Filter filter = new Query.FilterPredicate("token",
                Query.FilterOperator.EQUAL, token);
        Query query = new Query("Session").setFilter(filter);
        PreparedQuery pq = datastore.prepare(query);
        Entity entity;
        try {
            entity = pq.asSingleEntity();            
        } catch (PreparedQuery.TooManyResultsException ex) {
            throw new Error("unexpected exception", ex);
        }
        if (entity == null) {
            return null;
        } else {
            return (new Session(pq.asSingleEntity()));
        }
    }
    
    public static void expireOldSessions() {
        Calendar now = Calendar.getInstance();
        Query.Filter filter = new Query.FilterPredicate("expires",
                Query.FilterOperator.LESS_THAN, now.getTime());
        Query query = new Query("Session").setFilter(filter);
        PreparedQuery pq = datastore.prepare(query);
        int qn = pq.countEntities(FetchOptions.Builder.withDefaults());
        Key[] keys = new Key[qn];
        int i=0;
        for (Iterator<Entity> qi = pq.asIterable().iterator();qi.hasNext();i++) {
            keys[i] = qi.next().getKey();
        }            
        datastore.delete(keys);
    }
    
    private Entity entity;
    
    Session(User user, int minutes) {
        Date expires = new Date();
        long expiresMs = expires.getTime() + 1000L * 60L * minutes; 
        expires.setTime(expiresMs);
        String token = Long.toHexString(expiresMs) + user.getSalt();
        entity = new Entity("Session", token);
        entity.setProperty("token", token);
        entity.setProperty("expires", expires);
        entity.setProperty("userKey", user.getKey());
        datastore.put(entity);
    }
    
    private Session(Entity entity) {
        this.entity = entity;
    }
    
    public String getToken() {
        return (String)entity.getProperty("token");
    }
    
    Key getUserKey() {
        return (Key)entity.getProperty("userKey");
    }
    
    public User getUser() {
        try {
            return new User(datastore.get(getUserKey()));
        } catch (EntityNotFoundException ex) {
            throw new Error("unexpected exception", ex);
        }
    }
}
