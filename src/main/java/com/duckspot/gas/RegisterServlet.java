/* gas: Google Application Server
 * duckspot.com
 * 
 * mvn/src/main/java/com/duckspot/gas/RegisterServlet.java
 */
package com.duckspot.gas;

import static com.duckspot.gas.AbstractServlet.trim;
import com.duckspot.gas.model.User;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Allow new user to register.
 * 
 * @author Peter Dobson
 */
public class RegisterServlet extends AbstractServlet {

    protected String passwordRegex;
    String passwordError;
    private Pattern matchPassword;
    
    @Override
    public void init() {
        super.init();
        mainTemplate = "gas/register";
        loadTemplate(mainTemplate);
        loadTemplate("gas/redirect");
        passwordRegex = getInitParameter("passwordRegex");
        passwordError = getInitParameter("passwordError");
        if (passwordRegex == null) passwordRegex = "^\\S{4,}$";
        if (passwordError == null) passwordError = "password must have 4 or "
                + "more characters";
        matchPassword = Pattern.compile(passwordRegex);  
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
        Map<String,String> model = new HashMap<String,String>();

        model.put("passwordRegex", passwordRegex);
        model.put("passwordError", passwordError);
        
        output(request, response, model);
    }
    
    private static Pattern matchEmail = Pattern.compile(
            "[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}", 
            Pattern.CASE_INSENSITIVE);
    
    private static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        } else {
            return matchEmail.matcher(email).matches();
        }
    }
            
    private boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        } else {
            boolean result = matchPassword.matcher(password).matches();
            return result;
        }
    }
    
    /**
     * Gets a user that is not registered, or creates a new unregistered user.
     * 
     * @param email
     * @return 
     */
    private static User prepareUser(String email) {
        User result = User.getByEmail(email);
        if (result == null) {
            result = new User(email);
        }
        return result;
    }

    private void ajaxRegistration(Map<String,String> model, 
            HttpServletRequest request) {
        
        String email = trim(request.getParameter("email"));
        User user = prepareUser(email);
            
        String hspw = request.getParameter("hspw");
        if (hspw != null) {
            if (user.setHashSaltPw(null, hspw)) {
                model.put("token", user.newSession(hspw, 1).getToken());
                model.put("forward", "/");
            } else {
                throw new Error("expected error setting password");
            }
        } else {
            model.put("salt", user.nextSalt());                
            model.put("passwordRegex", passwordRegex);
            model.put("passwordError", passwordError);
        }
    }
    private void htmlFormRegistration(Map<String,String> model, 
            HttpServletRequest request) {
        
        String email = trim(request.getParameter("email"));
        User user = prepareUser(email);
            
        String password = trim(request.getParameter("password"));
        if (!isValidPassword(password)) {
            model.put("error", passwordError);
            model.put("statusCode", "400"); // bad request
        } else {
            if (user.setPassword(null, password)) {
                model.put("token", user.newSession(password, 1).getToken());
                model.put("forward", "/");
            } else {
                throw new Error("unexpected error setting password");
            }
        }
    }
    
    private void registerUser(Map<String,String> model, 
            HttpServletRequest request) {
        
        String accept = request.getHeader("accept");
        String hspw = request.getParameter("hspw");        
        if (accept != null && accept.contains("json") && hspw != null) {
            ajaxRegistration(model, request);
        } else {
            htmlFormRegistration(model, request);
        }
    }

    private void dumpModel(Map<String, String> model) {        
        for (String name: model.keySet()) {
            System.out.println("name: "+model.get(name));
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
        
        String email = trim(request.getParameter("email"));
        if (!isValidEmail(email)) {            
            model.put("error", "invalid email address");
            model.put("statusCode", "400"); // bad request        
        }
        else if (User.isEmailInUse(email)) {            
            model.put("error", "email already registered: "+email);
            // statusCode defaults to 403 Forbidden        
        }
        else {            
            registerUser(model, request);            
        }
System.out.println("RegisterServlet: 185:");
dumpModel(model);
        output(request, response, model);
    }
}
