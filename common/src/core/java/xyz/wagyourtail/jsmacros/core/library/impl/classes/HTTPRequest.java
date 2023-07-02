package xyz.wagyourtail.jsmacros.core.library.impl.classes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * @author Wagyourtail
 *
 * @since 1.1.8
 *
 */
 @SuppressWarnings("unused")
public class HTTPRequest {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public Map<String, String> headers = new HashMap<>();
    public URL conn;
    
    public HTTPRequest(String url) throws IOException {
        this.conn = new URL(url);
    }
    
    /**
     * @since 1.1.8
     * 
     * @param key
     * @param value
     * @return
     */
    public HTTPRequest addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }
    
    /**
     * @since 1.1.8
     * 
     * @return
     * @throws IOException
     */
    public Response get() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) this.conn.openConnection();
        for (Entry<String, String> e : headers.entrySet()) {
            conn.addRequestProperty(e.getKey(), e.getValue());
        }
        conn.setRequestMethod("GET");
        InputStream stream = conn.getInputStream();
        return new Response(stream, conn.getResponseCode(), conn.getHeaderFields());
    }
    
    /**
     * @since 1.1.8
     * 
     * @param data
     * @return
     * @throws IOException
     */
    public Response post(String data) throws IOException {
        byte[] b = data.getBytes(StandardCharsets.UTF_8);
        HttpURLConnection conn = (HttpURLConnection) this.conn.openConnection();
        for (Entry<String, String> e : headers.entrySet()) {
            conn.addRequestProperty(e.getKey(), e.getValue());
        }
        conn.addRequestProperty("Content-Length", Integer.toString(b.length));
        conn.setRequestMethod("POST");
        
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        os.write(b);
        os.flush();
        os.close();
        
        
        InputStream stream = conn.getInputStream();
        return new Response(stream, conn.getResponseCode(), conn.getHeaderFields());
    }

    /**
     * @author Wagyourtail
     *
     * @since 1.1.8
     *
     */
    public static class Response {
        private final InputStream raw;
        private String text;
        public Map<String, List<String>> headers;
        public int responseCode;
        
        public Response(InputStream inputStream, int responseCode, Map<String, List<String>> headers) {
            this.raw = inputStream;
            this.responseCode = responseCode;
            if (headers != null) this.headers = new HashMap<>(headers);
        }
        
        /**
         * @since 1.1.8
         * 
         * @return
         */
        public String text() {
            if (text != null) return text;
            text = new BufferedReader(
                    new InputStreamReader(raw, StandardCharsets.UTF_8))
                      .lines()
                      .collect(Collectors.joining("\n"));
            return text;
        }
        
        /**
         * Don't use this. Parse {@link HTTPRequest.Response#text()} in the guest language
         * @since 1.1.8
         * @deprecated
         * @return
         */
         @Deprecated
        public Object json() {
            text();
            return null;
        }
        
        /**
         * @since 1.2.2
         * 
         * @return
         * @throws IOException
         */
        public byte[] byteArray() throws IOException {
            return IOUtils.toByteArray(raw);
        }
    }
}
