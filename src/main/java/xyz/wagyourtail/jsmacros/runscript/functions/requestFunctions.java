package xyz.wagyourtail.jsmacros.runscript.functions;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import xyz.wagyourtail.jsmacros.runscript.classes.HTTPRequest;
import xyz.wagyourtail.jsmacros.runscript.classes.HTTPRequest.response;
import xyz.wagyourtail.jsmacros.runscript.classes.WebSocket;

public class requestFunctions {
    public HTTPRequest create(String url) throws IOException {
        return new HTTPRequest(url);
    }
    
    public response get(String url) throws IOException {
        return get(url, null);
    }
    
    public response get(String url, Map<String, String> headers) throws IOException {
        HTTPRequest req = new HTTPRequest(url);
        if (headers != null) req.headers = new HashMap<>(headers);
        return req.get();
    }
    
    public response post(String url, String data) throws IOException {
        return post(url, data, null);
    }
    
    public response post(String url, String data, Map<String, String> headers) throws IOException {
        HTTPRequest req = new HTTPRequest(url);
        if (headers != null) req.headers = new HashMap<>(headers);
        return req.post(data);
    }
    
    public WebSocket createWS(String uri) throws URISyntaxException {
        return new WebSocket(uri);
    }
}
