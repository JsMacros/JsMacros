package xyz.wagyourtail.jsmacros.core.library.impl.classes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

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
 * @since 1.1.8
 */
@SuppressWarnings("unused")
public class HTTPRequest {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public Map<String, String> headers = new HashMap<>();
    public URL conn;
    public int connectTimeout;
    public int readTimeout;

    public HTTPRequest(String url) throws IOException {
        this.conn = new URL(url);
    }

    /**
     * @param key
     * @param value
     * @return
     * @since 1.1.8
     */
    public HTTPRequest addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    /**
     * @since 1.8.6
     * @return
     */
    public HTTPRequest setConnectTimeout(int timeout) {
        this.connectTimeout = timeout;
        return this;
    }

    /**
     * @since 1.8.6
     * @return
     */
    public HTTPRequest setReadTimeout(int timeout) {
        this.readTimeout = timeout;
        return this;
    }

    /**
     * @return
     * @throws IOException
     * @since 1.1.8
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
     * @param data
     * @return
     * @throws IOException
     * @since 1.1.8
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
     * @param data
     * @return
     * @throws IOException
     * @since 1.8.4
     */
    public Response post(byte[] data) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) this.conn.openConnection();
        for (Entry<String, String> e : headers.entrySet()) {
            conn.addRequestProperty(e.getKey(), e.getValue());
        }
        conn.addRequestProperty("Content-Length", Integer.toString(data.length));
        conn.setRequestMethod("POST");

        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        os.write(data);
        os.flush();
        os.close();

        InputStream stream = conn.getInputStream();
        return new Response(stream, conn.getResponseCode(), conn.getHeaderFields());
    }

    /**
     * @param data
     * @return
     * @throws IOException
     * @since 1.8.4
     */
    public Response put(String data) throws IOException {
        byte[] b = data.getBytes(StandardCharsets.UTF_8);
        HttpURLConnection conn = (HttpURLConnection) this.conn.openConnection();
        for (Entry<String, String> e : headers.entrySet()) {
            conn.addRequestProperty(e.getKey(), e.getValue());
        }
        conn.addRequestProperty("Content-Length", Integer.toString(b.length));
        conn.setRequestMethod("PUT");

        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        os.write(b);
        os.flush();
        os.close();

        InputStream stream = conn.getInputStream();
        return new Response(stream, conn.getResponseCode(), conn.getHeaderFields());
    }

    /**
     * @param data
     * @return
     * @throws IOException
     * @since 1.8.4
     */
    public Response put(byte[] data) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) this.conn.openConnection();
        for (Entry<String, String> e : headers.entrySet()) {
            conn.addRequestProperty(e.getKey(), e.getValue());
        }
        conn.addRequestProperty("Content-Length", Integer.toString(data.length));
        conn.setRequestMethod("PUT");

        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        os.write(data);
        os.flush();
        os.close();

        InputStream stream = conn.getInputStream();
        return new Response(stream, conn.getResponseCode(), conn.getHeaderFields());
    }

    /**
     * @since 1.8.6
     * @return
     */
    public Response send(String method) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) this.conn.openConnection();
        for (Entry<String, String> e : headers.entrySet()) {
            conn.addRequestProperty(e.getKey(), e.getValue());
        }
        conn.setRequestMethod(method);

        InputStream stream = conn.getInputStream();
        return new Response(stream, conn.getResponseCode(), conn.getHeaderFields());
    }

    /**
     * @param method
     * @param data
     * @return
     * @throws IOException
     * @since 1.8.4
     */
    public Response send(String method, String data) throws IOException {
        byte[] b = data.getBytes(StandardCharsets.UTF_8);
        HttpURLConnection conn = (HttpURLConnection) this.conn.openConnection();
        for (Entry<String, String> e : headers.entrySet()) {
            conn.addRequestProperty(e.getKey(), e.getValue());
        }
        conn.addRequestProperty("Content-Length", Integer.toString(b.length));
        conn.setRequestMethod(method);

        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        os.write(b);
        os.flush();
        os.close();

        InputStream stream = conn.getInputStream();
        return new Response(stream, conn.getResponseCode(), conn.getHeaderFields());
    }

    /**
     * @param method
     * @param data
     * @return
     * @throws IOException
     * @since 1.8.4
     */
    public Response send(String method, byte[] data) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) this.conn.openConnection();
        for (Entry<String, String> e : headers.entrySet()) {
            conn.addRequestProperty(e.getKey(), e.getValue());
        }
        conn.addRequestProperty("Content-Length", Integer.toString(data.length));
        conn.setRequestMethod(method);

        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        os.write(data);
        os.flush();
        os.close();

        InputStream stream = conn.getInputStream();
        return new Response(stream, conn.getResponseCode(), conn.getHeaderFields());
    }

    /**
     * @author Wagyourtail
     * @since 1.1.8
     */
    public static class Response {
        private final InputStream raw;
        @Nullable
        private String text;
        @Nullable
        public Map<String, List<String>> headers;
        public int responseCode;

        public Response(InputStream inputStream, int responseCode, @Nullable Map<String, List<String>> headers) {
            this.raw = inputStream;
            this.responseCode = responseCode;
            if (headers != null) {
                this.headers = new HashMap<>(headers);
            }
        }

        /**
         * @return
         * @since 1.1.8
         */
        public String text() {
            if (text != null) {
                return text;
            }
            text = new BufferedReader(
                    new InputStreamReader(raw, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            return text;
        }

        /**
         * Don't use this. Parse {@link HTTPRequest.Response#text()} in the guest language
         *
         * @return
         * @since 1.1.8
         * @deprecated
         */
        @Deprecated
        public Object json() {
            text();
            return null;
        }

        /**
         * @return
         * @throws IOException
         * @since 1.2.2
         */
        public byte[] byteArray() throws IOException {
            return IOUtils.toByteArray(raw);
        }

    }

}
