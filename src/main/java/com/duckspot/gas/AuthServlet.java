/* gas: Google Application Server
 * duckspot.com
 * 
 * mvn/src/main/java/com/duckspot/gas/AuthServlet.java
 */
package com.duckspot.gas;

import com.duckspot.gas.model.Session;
import com.duckspot.gas.model.User;
import com.github.jknack.handlebars.Template;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Authentication.  Return information about current login status, provide 
 * login and/or register forms.  Process POST requests for login and 
 * registration.
 * 
 * @author Peter Dobson
 */
public class AuthServlet extends AbstractServlet {

    Template authTemplate;
    
    @Override
    public void init() {
        super.init();        
        loadTemplate("gas/signin");
        loadTemplate("gas/redirect");
    }
    
    /**
     * Returns name of currently logged in user, or HTML form allowing login 
     * or register.
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
            User user = session.getUser();
            model.put("userEmail", user.getEmail());
            model.put("userName", user.getName());
        }
        
        String accept = request.getHeader("accept");        
        if (accept != null && accept.contains("json")) {
            outputJson(response, model);
        } else {
            outputHtml(response, model, "gas/signin");
        }
    }
    
    private static Pattern matchEmail = Pattern.compile(
            "[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}", 
            Pattern.CASE_INSENSITIVE);
    
    private static boolean isValidEmail(String email) {
        return matchEmail.matcher(email).matches();
    }
    
    /**
     * Accepts new user registration, login, logout, change password, and 
     * change UserName requests.  Request parameters are interpreted as:
     * <ul>
     * <li>register request - 
     * </ul>
     * 
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Map<String,String> model = new HashMap<String,String>();
        
        String email = request.getParameter("email").trim();
        String hspw = request.getParameter("hspw");
        String password = request.getParameter("password").trim();
        String action = request.getParameter("action");
        
        if (!isValidEmail(email)) {
            model.put("error", "invalid email address");
        } 
        else if (action != null && action.equals("register")) {
            if (User.isEmailInUse(email)) {
                model.put("error", "email address already in use");
            } else {
                User user = User.getByEmail(email);
                if (user == null) {
                    user = new User(email);
                }        
                if (hspw != null) {
                    user.setHashSaltPw(null, hspw);
                    model.put("token",user.newSession(hspw, 1).getToken());
                } 
                else if (password != null) {
                    if (user.setPassword(null, password)) {
                        model.put("token", 
                                user.newSession(password, 1).getToken());
                    }
                }
                else {
                    model.put("salt",user.nextSalt());
                }
            } // if User.isEmailInUse(email)
        } // if action.equals("register")
        else {
            // login
            User user = User.getByEmail(email);
            Session session = null;
            if (user != null) {
                if (hspw != null) {            
                    session = user.newSession(hspw, 1);
                } 
                else if (password != null) {
                    session = user.newSession(password, 1);
                }
            }
            if (session != null) {            
                response.addCookie(new Cookie("token", session.getToken()));
                model.put("token", session.getToken());
                model.put("redirect", "/");
            } else {            
                model.put("error", "Username or password incorrect");
                model.put("forgot", "true");
            }
        }
        
        String accept = request.getHeader("accept");        
        if (accept.contains("json")) {
            if (model.containsKey("error")) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        model.get("error"));
            } else {
                outputJson(response, model);
            }
        } else {            
//            for(String mn: model.keySet()) {
//                System.out.printf("%s: %s\n", mn, model.get(mn));
//            }
            if (model.containsKey("redirect")) {
                outputHtml(response, model, "gas/redirect");
            } else {
                outputHtml(response, model, "gas/signin");
            }
        }
    }
}
