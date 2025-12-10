package ro.finsiel.eunis.security;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XSSRequestWrapper extends HttpServletRequestWrapper {

    private static final Logger logger = Logger.getLogger(XSSRequestWrapper.class);

    private static final Pattern[] patterns = new Pattern[]{
            // Script fragments
            Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
            // src='...'
            Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // lonely script tags
            Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // eval(...)
            Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // expression(...)
            Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // javascript:...
            Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
            // vbscript:...
            Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
            // onload(...)=... onload(.*?)=(.*?)
            Pattern.compile("onload=prompt(.*?)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onload(.*?)=(.*?)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("\"(.*?)>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("'(.*?)>", Pattern.CASE_INSENSITIVE),
//            Pattern.compile("drop", Pattern.CASE_INSENSITIVE),
//            Pattern.compile("select", Pattern.CASE_INSENSITIVE),
//            Pattern.compile("insert", Pattern.CASE_INSENSITIVE),
//            Pattern.compile("update", Pattern.CASE_INSENSITIVE),
//            Pattern.compile("create", Pattern.CASE_INSENSITIVE),
//            Pattern.compile("union", Pattern.CASE_INSENSITIVE),
//            Pattern.compile("concat", Pattern.CASE_INSENSITIVE),
    };

    public XSSRequestWrapper(HttpServletRequest servletRequest) {
        super(servletRequest);
    }

    @Override
    public String getPathInfo() {
        final String getPathInfo = super.getPathInfo();
        if (getPathInfo == null || getPathInfo.isEmpty()) {
            return getPathInfo;
        }

        String cleanGetPathInfo = getPathInfo;

        // Search for and remove the values that match an XSS vulnerability pattern
        for (Pattern scriptPattern : patterns) {
            // logger.error("Pattern examined: " + scriptPattern);
            Matcher matcher = scriptPattern.matcher(cleanGetPathInfo);
            if (matcher.find()) {
                logger.error(String.format("XSS attack prevent: Value [%s] was removed from path [%s]",
                        matcher.toString(), cleanGetPathInfo));
                // Empty vulnerability pattern match
                cleanGetPathInfo = matcher.replaceAll("");
                // logger.error("Sanitized getPathInfo: " + cleanGetPathInfo);
            }
        }

        return cleanGetPathInfo;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> paramsMap = super.getParameterMap();
        if (paramsMap == null) {
            return null;
        }

        Map<String, String[]> newParamsMap = new HashMap<>();

        for (Map.Entry<String, String[]> entry : paramsMap.entrySet()) {
            // In HttpServletRequest, getParameterMap returns a Map of all query string parameters
            // and post data parameters. We will filter them to process only the query string parameters.
            if (getQueryStringParameters(super.getQueryString()).contains(entry.getKey())) {
                ArrayList<String> newValues = new ArrayList<>();
                for (String value : entry.getValue()) {
                    if (value != null && !value.isEmpty()) {
                        newValues.add(cleanXSS(value, entry.getKey()));
                    } else {
                        newValues.add(value);
                    }
                }
                String[] arrValues = new String[newValues.size()];
                newValues.toArray(arrValues);
                newParamsMap.put(entry.getKey(), arrValues);
            } else {
                newParamsMap.put(entry.getKey(), entry.getValue());
            }
        }

        return newParamsMap;
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] paramValues = super.getParameterValues(parameter);
        if (paramValues == null) {
            return null;
        }

        int count = paramValues.length;
        String[] cleanValues = new String[count];
        for (int i = 0; i < count; i++) {
            cleanValues[i] = cleanXSS(paramValues[i], parameter);
        }

        return cleanValues;
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        return cleanXSS(value, parameter);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return cleanXSS(value, name);
    }

    private String cleanXSS(String value, String paramName) {
        if (value != null && !value.isEmpty()) {
            // logger.error(String.format("Parsing %s...", value));
            value = value.replaceAll("\0", "");

            // Remove all values that match an XSS pattern
            for (Pattern scriptPattern : patterns) {
                Matcher matcher = scriptPattern.matcher(value);
                if (matcher.find()) {
                    String dangereousValue = value;
                    // Empty parameter value
                    value = "";
                    String queryString = super.getQueryString();
                    logger.error(String.format("XSS attack prevent: Value [%s] was replaced for parameter [%s] with [%s] in query [%s]",
                            dangereousValue, paramName, value, queryString));
                } else {
                    // logger.debug("Mismatch: " + scriptPattern + "vs. " + value);
                }
            }
        }

        return value;
    }

    private List<String> getQueryStringParameters(String queryString) {
        List<String> queryParameters = new ArrayList<>();

        if (queryString != null && !queryString.isEmpty()) {
            try {
                queryString = URLDecoder.decode(queryString, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                // Is never thrown
            }
            String[] parameters = queryString.split("&");
            for (String parameter : parameters) {
                String[] keyValuePair = parameter.split("=");
                if (!queryParameters.contains(keyValuePair[0])) {
                    queryParameters.add(keyValuePair[0]);
                }
            }
        }
        // logger.debug("getQueryStringParameters: " + queryParameters);
        return queryParameters;
    }

}
