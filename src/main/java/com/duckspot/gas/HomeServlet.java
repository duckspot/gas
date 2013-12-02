/* gas: Google Application Server
 * duckspot.com
 * 
 * mvn/src/main/java/com/duckspot/gas/HomeServlet.java
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
public class HomeServlet extends AbstractServlet {

    Template authTemplate;
    
    @Override
    public void init() {
        // TODO: 9 consider making development a server context setting
        development = true;
        loadTemplate("gas/home");        
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
            outputHtml(response, model, "gas/home");
        }
    }        
}
