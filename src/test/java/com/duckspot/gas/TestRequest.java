/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duckspot.gas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Peter Dobson
 */
public class TestRequest implements HttpServletRequest {
        
    Cookie[] cookies;
    Map<String,String[]> headers = new HashMap<String,String[]>();
    Map<String,String[]> parameters = new HashMap<String,String[]>();

    public void clear() {
        cookies = null;
        headers.clear();
        parameters.clear();
    }
    
    public void setCookies(Cookie[] cookies) {
        this.cookies = cookies;
    }

    @Override
    public Cookie[] getCookies() {            
        return cookies;
    }

    @Override
    public String getHeader(String name) {
        String[] ha = headers.get(name);
        if (ha == null) {
            return null;
        }
        return ha[0];
    }

    public void setHeader(String name, String value) {
        String[] ha = new String[1];
        ha[0] = value;
        headers.put(name, ha);
    }

    public void addHeader(String name, String value) {
        String[] ha = headers.get(name);
        if (ha == null) {
            ha = new String[1];
        } else {
            ha = Arrays.copyOf(ha, ha.length+1);
        }
        ha[ha.length-1] = value;
        headers.put(name, ha);
    }

    @Override
    public Enumeration getHeaders(String name) {
        return new ArrayEnumeration(headers.get(name));
    }

    @Override
    public Enumeration getHeaderNames() {
        return new ArrayEnumeration(headers.keySet().toArray());
    }

    @Override
    public int getIntHeader(String name) {
        return Integer.parseInt(getHeader(name));
    }

    @Override
    public Map getParameterMap() {
        return parameters;
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameters.get(name);
    }

    @Override
    public String getParameter(String name) {
        String[] values = getParameterValues(name);
        if (values == null) {
            return null;
        } else {
            return values[0];
        }
    }

    public void addParameter(String name, String value) {
        String[] newValues;
        if (parameters.containsKey(name)) {
            String[] oldValues = parameters.get(name);
            newValues = Arrays.copyOf(oldValues, oldValues.length+1);
        } else {
            newValues = new String[1];
        }
        newValues[newValues.length-1] = value;
        parameters.put(name, newValues);
    }
    
    public void setParameter(String name, Object value) {
        if (value instanceof String) {
            String[] newValues = { (String)value };
            parameters.put(name, newValues);
        } else {
            parameters.put(name, (String[])value);
        }
    }
    
    @Override
    public Enumeration<String> getParameterNames() {
        return new ArrayEnumeration<String>(
                (String[])(getParameterMap().keySet().toArray()));
    }

    private static void appendEnum(StringBuilder sb, Enumeration<String> values) {
        sb.append("(");
        String nextSep="";
        while (values.hasMoreElements()) {
            sb.append(nextSep).append(values.nextElement());
            nextSep=";";
        }
        sb.append(")");
    }
    private static void appendEnum(StringBuilder sb, String[] values) {
        appendEnum(sb, new ArrayEnumeration(values));
    }
    
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Test Request\n    Headers: ");
        String sep1="";
        for (Enumeration<String> e = getHeaderNames(); e.hasMoreElements(); ) {
            String name = e.nextElement();
            sb.append(sep1).append(name).append("=");
            appendEnum(sb, getHeaders(name));
            sep1=", ";
        }
        sb.append("\n    Parameters: ");
        Map<String,String> parameterMap = getParameterMap();
        sep1="";
        for (String name: parameterMap.keySet()) {
            sb.append(sep1).append(name).append("=");
            appendEnum(sb, getParameterValues(name));
            sep1=", ";
        }
        return sb.toString();        
    }
    
    @Override
    public long getDateHeader(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getAuthType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getMethod() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getPathInfo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getPathTranslated() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getContextPath() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getQueryString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getRemoteUser() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isUserInRole(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getRequestedSessionId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getRequestURI() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public StringBuffer getRequestURL() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getServletPath() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HttpSession getSession(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HttpSession getSession() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getAttribute(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration getAttributeNames() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getCharacterEncoding() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCharacterEncoding(String string) throws UnsupportedEncodingException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getContentLength() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getContentType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getProtocol() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getScheme() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getServerName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getServerPort() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BufferedReader getReader() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getRemoteAddr() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getRemoteHost() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAttribute(String string, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeAttribute(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Enumeration getLocales() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isSecure() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getRealPath(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getRemotePort() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getLocalName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getLocalAddr() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getLocalPort() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

