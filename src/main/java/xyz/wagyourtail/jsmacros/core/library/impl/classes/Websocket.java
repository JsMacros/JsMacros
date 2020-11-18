package xyz.wagyourtail.jsmacros.core.library.impl.classes;

import com.neovisionaries.ws.client.*;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * @author Wagyourtail, R3alCl0ud
 *
 */
public class Websocket {

    private final WebSocket ws;
    /**
     * calls your method as a {@link java.util.function.Consumer BiConsumer}&lt;{@link WebSocket}, {@link List}&lt;{@link String}&gt;&gt;
     */
    public MethodWrapper<WebSocket, Map<String, List<String>>, Object> onConnect;
    /**
     * calls your method as a {@link java.util.function.BiConsumer BiConsumer}&lt;{@link WebSocket}, {@link String}&gt;
     */
    public MethodWrapper<WebSocket, String, Object> onTextMessage;
    /**
     * calls your method as a {@link java.util.function.BiConsumer BiConsumer}&lt;{@link WebSocket}, {@link Disconnected}&gt;
     */
    public MethodWrapper<WebSocket, Disconnected, Object> onDisconnect;
    /**
     * calls your method as a {@link java.util.function.BiConsumer BiConsumer}&lt;{@link WebSocket}, {@link WebSocketException}&gt;
     */
    public MethodWrapper<WebSocket, WebSocketException, Object> onError;
    /**
     * calls your method as a {@link java.util.function.BiConsumer BiConsumer}&lt;{@link WebSocket}, {@link WebSocketFrame}&gt;
     */
    public MethodWrapper<WebSocket, WebSocketFrame, Object> onFrame;

    public Websocket(String address) throws IOException {
        ws = new WebSocketFactory().createSocket(address).addListener(new WebSocketAdapter() {
            @Override
            public void onConnected(WebSocket ws, Map<String, List<String>> headers) throws Exception {
                if (onConnect != null)
                    onConnect.accept(ws, headers);
            }

            @Override
            public void onDisconnected(WebSocket ws, WebSocketFrame serverFrame, WebSocketFrame clientFrame, boolean isServer) {
                if (onDisconnect != null)
                    onDisconnect.accept(ws, new Disconnected(serverFrame, clientFrame, isServer));
            }

            @Override
            public void onConnectError(WebSocket websocket, WebSocketException exception) {
                onError(websocket, exception);
            }

            @Override
            public void onError(WebSocket websocket, WebSocketException ex) {
                if (onError != null)
                    onError.accept(websocket, ex);
            }

            @Override
            public void onFrame(WebSocket ws, WebSocketFrame frame) {
                if (onFrame != null)
                    onFrame.accept(ws, frame);
            }

            @Override
            public void onTextMessage(WebSocket ws, String text) {
                if (onTextMessage != null)
                    onTextMessage.accept(ws, text);
            }
        });
    }

    public Websocket(URL address) throws URISyntaxException, IOException {
        this(address.toURI().toString());
    }

    /**
     * @since 1.1.9
     * 
     * @return
     * @throws WebSocketException
     */
    public Websocket connect() throws WebSocketException {
        ws.connect();
        return this;
    }

    /**
     * @since 1.1.9
     * 
     * @return
     */
    public WebSocket getWs() {
        return ws;
    }

    /**
     * @since 1.1.9
     * 
     * @param text
     * @return
     */
    public Websocket sendText(String text) {
        ws.sendText(text);
        return this;
    }

    /**
     * @since 1.1.9
     * 
     * @return
     */
    public Websocket close() {
        ws.sendClose();
        return this;
    }

    /**
     * @since 1.1.9
     * 
     * @param closeCode
     * @return
     */
    public Websocket close(int closeCode) {
        ws.sendClose(closeCode);
        return this;
    }

    /**
     * @author Perry "R3alCl0ud" Berman
     *
     */
    public static class Disconnected {
        public WebSocketFrame serverFrame;
        public WebSocketFrame clientFrame;
        public boolean isServer;

        /**
         * @param serverFrame
         * @param clientFrame
         * @param isServer
         */
        public Disconnected(WebSocketFrame serverFrame, WebSocketFrame clientFrame, boolean isServer) {
            this.serverFrame = serverFrame;
            this.clientFrame = clientFrame;
            this.isServer = isServer;
        }
    }
}
