/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.duckspot.gas;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Peter Dobson
 */
public class TestResponse implements HttpServletResponse {

    StringWriter out;
    String contentType;
    int statusCode = 200;
    String statusMessage;
    List<Cookie> cookies = new ArrayList<Cookie>();
    
    public void clear() {
        contentType = null;
        statusCode = 200;
        statusMessage = null;
        cookies.clear();
    }
    
    @Override
    public PrintWriter getWriter() throws IOException {
        out = new StringWriter();
        return new PrintWriter(out);
    }

    public String getOutput() {
        return out.getBuffer().toString();
    }
    
    @Override
    public void setContentType(String string) {
        contentType = string;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }    
    
    public Cookie getCookie(String name) {
        for (Cookie c: cookies) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }
    
    public boolean hasCookie(String name) {
        return getCookie(name) != null;
    }
    
    @Override
    public boolean containsHeader(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String encodeURL(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String encodeRedirectURL(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String encodeUrl(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String encodeRedirectUrl(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendError(int i, String message) throws IOException {
        statusCode = i;
        statusMessage = message;
    }

    @Override
    public void sendError(int i) throws IOException {
        statusCode = i;
    }

    @Override
    public void setStatus(int i) {
        statusCode = i;
    }

    @Override
    public void setStatus(int i, String string) {
        throw new UnsupportedOperationException("Deprecated."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        String result = String.format("response - %d: %s\n%s", 
                statusCode, statusMessage, getOutput());        
        return result;
    }
    
    @Override
    public void sendRedirect(String string) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDateHeader(String string, long l) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addDateHeader(String string, long l) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setHeader(String string, String string1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addHeader(String string, String string1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setIntHeader(String string, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addIntHeader(String string, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getCharacterEncoding() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCharacterEncoding(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setContentLength(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setBufferSize(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getBufferSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void flushBuffer() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void resetBuffer() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isCommitted() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLocale(Locale locale) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
            
}

