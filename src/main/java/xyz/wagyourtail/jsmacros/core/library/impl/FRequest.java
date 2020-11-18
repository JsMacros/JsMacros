package xyz.wagyourtail.jsmacros.core.library.impl;

import xyz.wagyourtail.jsmacros.core.library.impl.classes.HTTPRequest;
import xyz.wagyourtail.jsmacros.core.library.impl.classes.HTTPRequest.Response;
import xyz.wagyourtail.jsmacros.core.library.impl.classes.Websocket;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Functions for getting and using raw java classes, methods and functions.
 * 
 * An instance of this class is passed to scripts as the {@code request} variable.
 * 
 * @since 1.1.8
 * 
 * @author Wagyourtail
 */
 @Library("request")
public class FRequest implements BaseLibrary {
    
    /**
     * create a HTTPRequest handler to the specified URL
     * 
     * @since 1.1.8
     * 
     * @see HTTPRequest
     * 
     * @param url
     * @return Request Wrapper
     * @throws IOException
     */
    public HTTPRequest create(String url) throws IOException {
        return new HTTPRequest(url);
    }
    
    /**
     * @since 1.1.8
     * 
     * @see FRequest#get(String, Map)
     * 
     * @param url
     * @return
     * @throws IOException
     */
    public Response get(String url) throws IOException {
        return get(url, null);
    }
    
    /**
     * send a GET request to the specified URL.
     * 
     * @since 1.1.8
     * 
     * @see HTTPRequest.Response
     * 
     * @param url
     * @param headers
     * @return Response Data
     * @throws IOException
     */
    public Response get(String url, Map<String, String> headers) throws IOException {
        HTTPRequest req = new HTTPRequest(url);
        if (headers != null) req.headers = new HashMap<>(headers);
        return req.get();
    }
    
    /**
     * @see FRequest#post(String, String, Map)
     * 
     * @since 1.1.8
     * 
     * @param url
     * @param data
     * @return
     * @throws IOException
     */
    public Response post(String url, String data) throws IOException {
        return post(url, data, null);
    }
    
    /**
     * send a POST request to the specified URL. 
     * 
     * @since 1.1.8
     * 
     * @param url
     * @param data
     * @param headers
     * @return Response Data
     * @throws IOException
     */
    public Response post(String url, String data, Map<String, String> headers) throws IOException {
        HTTPRequest req = new HTTPRequest(url);
        if (headers != null) req.headers = new HashMap<>(headers);
        return req.post(data);
    }
    
    /**
     * Create a Websocket handler.
     * 
     * @since 1.2.7
     * 
     * @see Websocket
     * 
     * @param url
     * @return
     * @throws IOException
     */
    public Websocket createWS(String url) throws IOException {
        return new Websocket(url);
    }
    
    /**
     * 
     * Create a Websocket handler.
     * 
     * @since 1.1.9
     * @deprecated 1.2.7
     * 
     * @param url
     * @return
     * @throws IOException
     */
    @Deprecated
    public Websocket createWS2(String url) throws IOException {
        return new Websocket(url);
    }
}
