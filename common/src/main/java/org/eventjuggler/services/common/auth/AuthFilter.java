package org.eventjuggler.services.common.auth;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.picketlink.idm.IdentityManagerFactory;
import org.picketlink.idm.model.User;

public class AuthFilter implements Filter {

    private IdentityManagerFactory imf;

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        if (request instanceof HttpServletRequest) {
            String token = ((HttpServletRequest) request).getHeader("token");
            if (token != null) {
                User user = new SimpleAuthIdmUtil(imf.createIdentityManager()).getUser(token);
                if (user != null) {
                    Auth.set(user);
                }
            }
        }

        try {
            chain.doFilter(request, response);
        } finally {
            Auth.remove();
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        try {
            imf = (IdentityManagerFactory) new InitialContext().lookup("java:/picketlink/ExampleIMF");
        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }

}
