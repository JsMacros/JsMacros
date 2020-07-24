package xyz.wagyourtail.jsmacros.runscript.classes;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

public class Websocket2 extends WebSocketAdapter {

    private WebSocket ws;
    public BiConsumer<WebSocket, Map<String, List<String>>> onConnect;
    public BiConsumer<WebSocket, String> onTextMessage;
    public BiConsumer<WebSocket, Disconnected> onDisconnect;
    public BiConsumer<WebSocket, WebSocketException> onError;
    public BiConsumer<WebSocket, WebSocketFrame> onFrame;

    public Websocket2(String address) throws IOException {
        ws = new WebSocketFactory().createSocket(address).addListener(this);
    }

    public Websocket2(URL address) throws URISyntaxException, IOException {
        this(address.toURI().toString());
    }

    public Websocket2 connect() throws WebSocketException {
        ws.connect();
        return this;
    }

    public WebSocket getWs() {
        return ws;
    }

    @Override
    public void onConnected(WebSocket ws, Map<String, List<String>> headers) throws Exception {
        if (onConnect != null) onConnect.accept(ws, headers);
    }

    @Override
    public void onDisconnected(WebSocket ws, WebSocketFrame serverFrame, WebSocketFrame clientFrame, boolean isServer) {
        if (onDisconnect != null) onDisconnect.accept(ws, new Disconnected(serverFrame, clientFrame, isServer));
    }

    @Override
    public void onConnectError(WebSocket websocket, WebSocketException exception) {
        onError(websocket, exception);
    }

    @Override
    public void onError(WebSocket websocket, WebSocketException ex) {
        if (onError != null) onError.accept(websocket, ex);
    }

    @Override
    public void onFrame(WebSocket ws, WebSocketFrame frame) {
        if (onFrame != null) onFrame.accept(ws, frame);
    }

    @Override
    public void onTextMessage(WebSocket ws, String text) {
        if (onTextMessage != null) onTextMessage.accept(ws, text);
    }

    public Websocket2 sendText(String text) {
        ws.sendText(text);
        return this;
    }

    public Websocket2 close() {
        ws.sendClose();
        return this;
    }

    public Websocket2 close(int closeCode) {
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
