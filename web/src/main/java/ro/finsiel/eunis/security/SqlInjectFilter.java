package ro.finsiel.eunis.security;

import org.apache.log4j.Logger;
import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SQL injection filter
 *
 * @author CSDN: seesun2012
 */
public class SqlInjectFilter implements Filter {

    private String[] filters;
    private static Logger logger = Logger.getLogger(SqlInjectFilter.class);

    /**
     * Filers request parameters for SQL injection attempts
     * @param request HTTP request
     * @param response HTTP response
     * @param chain Filter chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httprequest = (HttpServletRequest) request;
        Enumeration<?> params = httprequest.getParameterNames();
        StringBuilder sql = new StringBuilder();
        while(params.hasMoreElements()) {
            String name = params.nextElement().toString();
            String[] value = httprequest.getParameterValues(name);
            for (String s : value) {
                sql.append(s);
            }
        }

        if(containsFilteredChars(sql.toString())) {
            logger.error("Blocked possible injection in " + sql);
            HttpServletResponse r = (HttpServletResponse) response;
            r.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } else{
            chain.doFilter(request, response);
        }
    }

    private boolean containsFilteredChars(String str) {
        for (String badStr : filters) {
            if (str.contains(badStr)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String sqlInjectStrList = filterConfig.getInitParameter("sqlInjectStrList");
        filters = sqlInjectStrList.split("\\|");
    }

    @Override
    public void destroy() {
    }
}