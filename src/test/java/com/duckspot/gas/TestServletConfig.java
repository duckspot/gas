package com.duckspot.gas;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class TestServletConfig implements ServletConfig {

    private ServletContext servletContext;
    private Map<String,String> initParameter = new HashMap<String,String>();
        
    @Override
    public ServletContext getServletContext() {
        if (servletContext == null) {
            servletContext = new TestServletContext();
        }
        return servletContext;
    }

    public void putInitParameter(String name, String value) {
        initParameter.put(name, value);
    }

    @Override
    public String getInitParameter(String name) {
        return initParameter.get(name);
    }

    @Override
    public Enumeration getInitParameterNames() {
        return new ArrayEnumeration(initParameter.keySet().toArray());
    }

    @Override
    public String getServletName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
