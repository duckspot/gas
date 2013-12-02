/* gas: Google Application Server
 * duckspot.com
 * 
 * mvn/src/main/java/com/duckspot/gas/AbstractServlet.java
 */
package com.duckspot.gas;

import com.duckspot.gas.model.Session;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONWriter;

/**
 * Allow new user to register.
 * 
 * @author Peter Dobson
 */
public abstract class AbstractServlet extends HttpServlet {

    protected boolean development = true;
    protected String templatesPath = "templates";    
    protected Handlebars handlebars;
    protected Map<String,Template> template = new HashMap<String,Template>();
    protected String mainTemplate;
            
    @Override
    public void init() {
        ServletConfig cfg = getServletConfig();
        if (cfg != null) {
            ServletContext ctx = cfg.getServletContext();
            if (ctx != null) {
                String dev = ctx.getInitParameter("development");
                if (dev != null) {
                    development = Boolean.parseBoolean(dev);
                }
            }            
        }
    }
        
    protected Handlebars getHandlebars() {
        if (handlebars == null) {            
            handlebars = new Handlebars(
                    new FileTemplateLoader(templatesPath,".html"));
        }
        return handlebars;
    }
    
    protected void loadTemplate(String name) {
        try {            
            template.put(name, getHandlebars().compile(name));        
        } catch (FileNotFoundException ex) {
            throw new Error(ex.getMessage()+": Not Found");
        } catch (IOException ex) {
            throw new Error("unexpected exception", ex);
        }
    }
    
    protected void reLoadTemplates() {
        try {
            for (String name: template.keySet()) {            
                loadTemplate(name);
            }
        } catch (Exception ex) {
            throw new Error("unexpected exception", ex);        
        }
    }
    
    protected String getCookie(HttpServletRequest request, String key) {
        Cookie[] cookie = request.getCookies();
        if (cookie != null) {
            for (int i=0; i<cookie.length; i++) {
                if (cookie[i].getName().equals("token")) {
                    return cookie[i].getValue();
                }
            }
        }
        return null;
    }
    
    protected Session getSession(HttpServletRequest request) {
        
        String token = getCookie(request, "token");
        if (token != null) {
            return Session.getSession(token);
        }
        return null;
    }
    
    protected void output(HttpServletRequest request, 
            HttpServletResponse response, Map<String,String> model) {
        
//        for (String key: model.keySet()) {
//            System.out.println("AbstractServlet: 109: model.get(\""+key+"\"): "+model.get(key));
//        }
        
        if (model.containsKey("token")) {
            response.addCookie(new Cookie("token", model.get("token")));
        }
        
        String accept = request.getHeader("accept");
        if (accept != null && accept.contains("json")) {
            
            outputJson(response, model);
        
        } else {            
            
            if (model.containsKey("redirect")) {
                outputHtml(response, model, "gas/redirect");                
            } else {
                outputHtml(response, model, mainTemplate);
            }
        }
    }
    
    protected void outputHtml(HttpServletResponse response, 
            Map<String,String> model, String name) 
    {
        if (development) {
            reLoadTemplates();
        }
        PrintWriter out = null;
        try {
            response.setContentType("text/html;charset=UTF-8");
            out = response.getWriter();
            template.get(name).apply(model, out);
            out.close();
        } catch (IOException ex) {
            throw new Error("unexpected exception", ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    protected void outputHtml(HttpServletResponse response, 
            Map<String,String> model) 
    {
        String name = template.keySet().iterator().next();
        outputHtml(response, model, name);
    }
    
    protected static void outputJson(HttpServletResponse response, 
            Map<String,String> model)
    {        
        if (model.containsKey("error")) {
            int statusCode = HttpServletResponse.SC_FORBIDDEN;
            if (model.containsKey("statusCode")) {
                statusCode = Integer.parseInt(model.get("statusCode"));
            }
            try {
                response.sendError(statusCode, model.get("error"));
            } catch (IOException ex) {
                throw new Error("unexpected exception", ex);
            }
        }
        PrintWriter out = null;
        try {
            response.setContentType("text/json");
            out = response.getWriter();
            JSONWriter jw = new JSONWriter(out).object();
            for (String key: model.keySet()) {
                jw.key(key).value(model.get(key));
            }
            jw.endObject();
        } catch (JSONException ex) {
            throw new Error("unexpected exception", ex);
        } catch (IOException ex) {
            throw new Error("unexpected exception", ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    protected static String trim(String s) {
        if (s == null) {
            return null;
        } else {
            return s.trim();
        }
    }
}
