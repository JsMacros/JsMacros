package xyz.wagyourtail.jsmacros.runscript.classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class HTTPRequest {
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public HashMap<String, String> headers = new HashMap<>();
    public URL conn;
    
    public HTTPRequest(String url) throws IOException {
        this.conn = new URL(url);
    }
    
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }
    
    public response get() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) this.conn.openConnection();
        for (Entry<String, String> e : headers.entrySet()) {
            conn.addRequestProperty(e.getKey(), e.getValue());
        }
        conn.setRequestMethod("GET");
        InputStream stream = conn.getInputStream();
        return new response(stream, conn.getResponseCode(), conn.getHeaderFields());
    }
    
    public response post(String data) throws IOException {
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
        return new response(stream, conn.getResponseCode(), conn.getHeaderFields());
    }

    public static class response {
        private InputStream raw;
        public HashMap<String, List<String>> headers;
        public String text;
        public int responseCode;
        
        public response(InputStream inputStream, int responseCode, Map<String, List<String>> headers) {
            this.raw = inputStream;
            this.responseCode = responseCode;
            if (headers != null) this.headers = new HashMap<>(headers);
        }
        
        public String text() {
            if (text != null) return text;
            return new BufferedReader(
                    new InputStreamReader(raw, StandardCharsets.UTF_8))
                      .lines()
                      .collect(Collectors.joining("\n"));
        }
        
        public HashMap<?, ?> json() {
            text();
            return gson.fromJson(text, HashMap.class);
        }
    }
}
