/* gas: Google Application Server
 * duckspot.com
 * 
 * mvn/src/main/java/com/duckspot/gas/model/User.java
 */
package com.duckspot.gas.model;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import java.security.MessageDigest;
import java.security.SecureRandom;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Represents a registered (or partly registered) user of the system.  Manages
 * user authentication using salted and hashed passwords.
 * 
 * 
 * @author Peter Dobson
 */
public class User {
    
    static final SecureRandom rand = new SecureRandom();
    static DatastoreService datastore 
            = DatastoreServiceFactory.getDatastoreService();    
    
    /**
     * @return string of 12 random hex digits.
     */
    static String makeSalt() {
        byte[] bytes = new byte[6];
        synchronized(rand) {
            rand.setSeed(System.currentTimeMillis());
            rand.nextBytes(bytes);
        }
        return Hex.encodeHexString(bytes);
    }
    
    public static User getByEmail(String email) {
        Query.Filter filter = new Query.FilterPredicate("email",
                Query.FilterOperator.EQUAL, email);
        Query query = new Query("User").setFilter(filter);
        PreparedQuery pq = datastore.prepare(query);
        if (pq.countEntities(FetchOptions.Builder.withDefaults()) <= 0) {
            return null;
        }
        try {
            return (new User(pq.asSingleEntity()));
        } catch (PreparedQuery.TooManyResultsException ex) {
            throw new Error("unexpected exception", ex);
        }
    }
    
    /**
     * Returns true if email is already registered.
     * 
     * @param email
     * @return 
     */
    public static boolean isEmailInUse(String email) {
        System.err.println("User.java: 68: isEmailInUse(\""+email+"\")");
        Query.Filter filter = CompositeFilterOperator.and(
                new Query.FilterPredicate("email",
                    Query.FilterOperator.EQUAL, email),
                new Query.FilterPredicate("isRegistered",
                    Query.FilterOperator.EQUAL, true));
        Query query = new Query("User").setFilter(filter);
        PreparedQuery pq = datastore.prepare(query);
        // Changing FetchOptions below may increase performance slightly
        return (pq.countEntities(FetchOptions.Builder.withDefaults()) > 0);
    }
    
    private Entity entity;    
    private MessageDigest mda;
    
    public User(Entity entity) {
        this.entity = entity;
    }
    
    public User(String email) {
        entity = new Entity("User", email);
        entity.setProperty("email", email);
    }
        
    public synchronized Key getKey() {
        return entity.getKey();
    }
    
    public synchronized String getEmail() {
        return (String)entity.getProperty("email");
    }
    
    public synchronized void setEmail(String email) {
        entity.setProperty("email", email);
    }
    
    public synchronized String getName() {
        if (entity.hasProperty("name")) {
            return (String)entity.getProperty("name");
        } else {
            return (String)entity.getProperty("email");
        }
    }
    
    public synchronized void setName(String name) {
        entity.setProperty("name", name);
    }
    
    public synchronized void save() {
        datastore.put(entity);
    }
    
    public synchronized String getSalt() {
        if (!entity.hasProperty("salt")) {
            entity.setProperty("salt", makeSalt());
            if (!entity.hasProperty("nextSalt")) {
                entity.setProperty("nextSalt", makeSalt());
            }
            save();
        }
        return (String)entity.getProperty("salt");
        
    }
    
    public synchronized String nextSalt() {
        if (!entity.hasProperty("nextSalt")) {
            entity.setProperty("nextSalt", makeSalt());
            datastore.put(entity);
        }
        return (String)entity.getProperty("nextSalt");
    }
    
    private synchronized String hashSaltPw(String salt, String password) {
        if (mda == null) {
            mda = DigestUtils.getSha256Digest();
        }
        String data = salt + password;        
        return Hex.encodeHexString(mda.digest(data.getBytes()));
    }
    
    /**
     * Constant time string comparison, to ensure timing attack can't find
     * hashed password.
     * 
     * @param a
     * @param b
     * @return true of a and b are both String and have the same value.
     */
    private static boolean compare(Object a, Object b) {
        String sa="a", sb="b";
        if (a instanceof String && b instanceof String) {
            sa = (String)a; sb=(String)b;
        }
        int diff = (sa.length() ^ sb.length());
        int minLen = Math.min(sa.length(), sb.length());
        for (int i=0; i<minLen; i++) {
            diff |= (sa.charAt(i) ^ sb.charAt(i));
        }
        return diff == 0;
    }
    
    public boolean setPassword(String oldPassword, String newPassword) {                    
        return setHashSaltPw(
                hashSaltPw(getSalt(), oldPassword), 
                hashSaltPw(nextSalt(), newPassword)
        );
    }
    
    public boolean checkPassword(String password) {
        String hash = hashSaltPw(getSalt(), password);
        return compare(getHashSaltPw(),hash);
    }
    
    private synchronized String getHashSaltPw() {
        return (String)entity.getProperty("hashSaltPw");
    }
    
    public synchronized boolean setHashSaltPw(
            String oldHashSaltPw, String hashSaltPw) {
System.err.println("User.java: 187: setHashSaltPw()");
        if (entity.hasProperty("hashSaltPw") &&
                !compare(oldHashSaltPw,entity.getProperty("hashSaltPw"))) {
            return false;
        }
        entity.setProperty("salt", nextSalt());
        entity.setProperty("nextSalt", makeSalt());        
        entity.setProperty("hashSaltPw", hashSaltPw);
System.err.println("User.java: 195: entity.getKey().getName():"+entity.getKey().getName());
System.err.println("User.java: 196: entity.setProperty(\"isRegistered\", true)");
        entity.setProperty("isRegistered", true);        
        save();
        return true;
    }
    
    public synchronized Session newSession(String password, int days) {
System.err.println("User.java: 202: newSession()");
        if (!compare(password,entity.getProperty("hashSaltPw"))) {
            // try again after salting and hashing
            String hspw = hashSaltPw(getSalt(), password);
            if (!compare(hspw,entity.getProperty("hashSaltPw"))) {
                return null;
            }            
        }
System.err.println("User.java: 210: return new Session();");
        return new Session(this, days);
    }
}