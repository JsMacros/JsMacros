package xyz.wagyourtail.jsmacros.runscript.classes;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.BiConsumer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

@Deprecated
public class WebSocket extends WebSocketClient {
    public BiConsumer<WebSocket, ServerHandshake> onOpen;
    public BiConsumer<WebSocket, String> onMessage;
    public BiConsumer<WebSocket, closeData> onClose;
    public BiConsumer<WebSocket, String> onError;
    
    public WebSocket(String uri) throws URISyntaxException {
        super(new URI(uri));
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        if (onOpen != null) onOpen.accept(this, handshakedata);
    }

    @Override
    public void onMessage(String message) {
        if (onMessage != null) onMessage.accept(this, message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (onClose != null) onClose.accept(this, new closeData(code, reason, remote));
        
    }

    @Override
    public void onError(Exception ex) {
        if (onError != null) onError.accept(this, ex.toString());
    }
    
    public class closeData {
        public int code;
        public String reason;
        public boolean remote;
        
        public closeData(int code, String reason, boolean remote) {
            this.code = code;
            this.reason = reason;
            this.remote = remote;
        }
    }
}
