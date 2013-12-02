/* gas: Google Application Server
 * duckspot.com
 * 
 * mvn/src/main/java/com/duckspot/gas/LoginServlet.java
 */
package com.duckspot.gas;

import com.duckspot.gas.model.Session;
import com.duckspot.gas.model.User;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Allow new user to login.
 * 
 * @author Peter Dobson
 */
public class LoginServlet extends AbstractServlet {

    @Override
    public void init() {
        super.init();
        mainTemplate = "gas/login";
        loadTemplate(mainTemplate);
        loadTemplate("gas/redirect");
    }
    
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, 
            HttpServletResponse response)
            throws ServletException, IOException
    {
        Session session = getSession(request);
        
        Map<String,String> model = new HashMap<String,String>();
        
        if (session == null) {
            model.put("isLoggedIn", "");
        } else {
            model.put("isLoggedIn", "true");
            model.put("token", session.getToken());
            model.put("redirect", "/");
        }
        
        output(request, response, model);
    }
    
    private static void login(Map<String,String> model, 
            HttpServletRequest request) {
        
        String email = trim(request.getParameter("email"));
        String hspw = request.getParameter("hspw");
        String password = trim(request.getParameter("password")); 

        Session session = null;
        User user = User.getByEmail(email);
        
        if (user != null) {
            if (hspw != null) {
                session = user.newSession(hspw, 1);
            } else if (password != null) {
                session = user.newSession(password, 1);
            }
        }
        if (session != null) {
            model.put("token", session.getToken());
            model.put("redirect", "/");
        } else {
            model.put("error", "Username or password incorrect");
            model.put("forgot", "true");
        }
    }
    
    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Map<String, String> model = new HashMap<String, String>();

        Session session = getSession(request);
        if (session != null) {

            // already logged in
            model.put("isLoggedIn", "true");
            model.put("token", session.getToken());
            model.put("redirect", "/");

        } else {
            
            login(model, request);
        }

        output(request, response, model);
    }
}
