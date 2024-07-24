package xyz.wagyourtail.jsmacros.core.library.impl;

import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;
import xyz.wagyourtail.jsmacros.core.library.impl.classes.HTTPRequest;
import xyz.wagyourtail.jsmacros.core.library.impl.classes.HTTPRequest.Response;
import xyz.wagyourtail.jsmacros.core.library.impl.classes.Websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Functions for getting and using raw java classes, methods and functions.
 * <p>
 * An instance of this class is passed to scripts as the {@code Request} variable.
 *
 * @author Wagyourtail
 * @since 1.1.8
 */
@Library("Request")
public class FRequest extends BaseLibrary {

    public FRequest(Core<?, ?> runner) {
        super(runner);
    }

    /**
     * create a HTTPRequest handler to the specified URL
     *
     * @param url
     * @return Request Wrapper
     * @throws IOException
     * @see HTTPRequest
     * @since 1.1.8
     */
    public HTTPRequest create(String url) throws IOException {
        return new HTTPRequest(url);
    }

    /**
     * @param url
     * @return
     * @throws IOException
     * @see FRequest#get(String, Map)
     * @since 1.1.8
     */
    public Response get(String url) throws IOException {
        return get(url, null);
    }

    /**
     * send a GET request to the specified URL.
     *
     * @param url
     * @param headers
     * @return Response Data
     * @throws IOException
     * @see HTTPRequest.Response
     * @since 1.1.8
     */
    public Response get(String url, @Nullable Map<String, String> headers) throws IOException {
        HTTPRequest req = new HTTPRequest(url);
        if (headers != null) {
            req.headers = new HashMap<>(headers);
        }
        return req.get();
    }

    /**
     * @param url
     * @param data
     * @return
     * @throws IOException
     * @see FRequest#post(String, String, Map)
     * @since 1.1.8
     */
    public Response post(String url, String data) throws IOException {
        return post(url, data, null);
    }

    /**
     * send a POST request to the specified URL.
     *
     * @param url
     * @param data
     * @param headers
     * @return Response Data
     * @throws IOException
     * @since 1.1.8
     */
    public Response post(String url, String data, @Nullable Map<String, String> headers) throws IOException {
        HTTPRequest req = new HTTPRequest(url);
        if (headers != null) {
            req.headers = new HashMap<>(headers);
        }
        return req.post(data);
    }

    /**
     * Create a Websocket handler.
     *
     * @param url
     * @return
     * @throws IOException
     * @see Websocket
     * @since 1.2.7
     */
    public Websocket createWS(String url) throws IOException {
        return new Websocket(url);
    }

    /**
     * Create a Websocket handler.
     *
     * @param url
     * @return
     * @throws IOException
     * @since 1.1.9
     * @deprecated 1.2.7
     */
    @Deprecated
    public Websocket createWS2(String url) throws IOException {
        return new Websocket(url);
    }

}
