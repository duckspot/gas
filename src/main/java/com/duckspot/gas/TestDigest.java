/* gas: Google Application Server
 * duckspot.com
 * 
 * mvn/src/main/java/com/duckspot/gas/TestDigest.java
 */
package com.duckspot.gas;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONWriter;

/**
 * Provides services that support JavaScript tests.
 * 
 * @author Peter Dobson
 */
public class TestDigest extends HttpServlet {

    private void outputHtmlHead(PrintWriter out) {
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Test Servlet</title>");            
        out.println("</head>");
        out.println("<body>");        
    }
    private void outputHtmlFoot(PrintWriter out) {
        out.println("</body>");
        out.println("</html>");
    }
    private void outputHeadersParameters(PrintWriter out, HttpServletRequest request) {        
        out.println("<table>");
        out.println("<tr><td colspan=2>headers:</td></tr>\n");
        for (Enumeration<String> names = request.getHeaderNames(); 
                names.hasMoreElements(); ) 
        {
            String name = names.nextElement();
            String value = request.getHeader(name);
            out.printf("<tr><td>%s</td><td>%s</td></tr>\n", name, value);
        }
        out.println("<tr><td>&nbsp;</td></tr>");
        out.println("<tr><td colspan=2>parameters:</td></tr>");
        for (Enumeration<String> names = request.getParameterNames(); 
                names.hasMoreElements(); ) 
        {
            String name = names.nextElement();
            String value = request.getParameter(name);
            out.printf("<tr><td>%s</td><td>%s</td></tr>\n", name, value);
        }            
        out.println("</table>");        
    }
    
    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String message = request.getParameter("message");
        MessageDigest mda = DigestUtils.getSha256Digest();
        byte [] digesta = mda.digest(message.getBytes());
        String digest = Hex.encodeHexString(digesta);
        
        String accept = request.getHeader("Accept");
        
        if (accept.contains("text/html")) {
            
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                /* TODO output your page here. You may use following sample code. */
                outputHtmlHead(out);
                out.printf("message: %s<br />\ndigest: %s<br />\n", message, digest);
                out.println("<a href='/test/test.html'>test again</a>");
                outputHeadersParameters(out, request);
                outputHtmlFoot(out);
            } finally {            
                out.close();
            }
        }
        else if (accept.contains("application/json")) {
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            try {
                new JSONWriter(out)
                    .object()
                        .key("message").value(message)
                        .key("digest").value(digest)
                    .endObject();
            } catch (JSONException ex) {
                throw new Error("unexpected exception", ex);
            } finally {
                out.close();
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
