package eionet.eunis.util;

import info.aduna.io.IOUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import static org.apache.http.conn.ssl.SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class Discodata {
    // short timeout
    private final static int timeout = 2;

    public static String encodeQuery(String website, String query) {
        try {
            return website + URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }

    public static JSONObject readJson(String url)  {
        if(url != null) {
            try {

                SSLContext sslContext = SSLContexts.custom().useTLS().build();

                SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                        sslContext,
                        new String[]{"TLSv1.1", "TLSv1.2"},
                        null,
                        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

                RequestConfig config = RequestConfig.custom()
                        .setConnectTimeout(timeout * 1000)
                        .setConnectionRequestTimeout(timeout * 1000)
                        .setSocketTimeout(timeout * 1000).build();

                org.apache.http.client.HttpClient httpClient = HttpClients.custom()
                        .setSSLSocketFactory(f).setDefaultRequestConfig(config).build();

                HttpGet get = new HttpGet(url);

                HttpResponse response = httpClient.execute(get);

                String result = IOUtil.readString(response.getEntity().getContent());
                return (JSONObject) JSONSerializer.toJSON(result);
            } catch (Exception e) {
                return new JSONObject();
            }
        }
        return new JSONObject();
    }
}
