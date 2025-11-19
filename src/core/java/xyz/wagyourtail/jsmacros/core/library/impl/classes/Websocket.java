package xyz.wagyourtail.jsmacros.core.library.impl.classes;

import com.neovisionaries.ws.client.*;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * @author Wagyourtail, R3alCl0ud
 */
@SuppressWarnings("unused")
public class Websocket {

    private final WebSocket ws;
    /**
     * calls your method as a {@link java.util.function.Consumer BiConsumer}&lt;{@link WebSocket}, {@link List}&lt;{@link String}&gt;&gt;
     */
    @Nullable
    public MethodWrapper<WebSocket, Map<String, List<String>>, Object, ?> onConnect;
    /**
     * calls your method as a {@link java.util.function.BiConsumer BiConsumer}&lt;{@link WebSocket}, {@link String}&gt;
     */
    @Nullable
    public MethodWrapper<WebSocket, String, Object, ?> onTextMessage;
    /**
     * calls your method as a {@link java.util.function.BiConsumer BiConsumer}&lt;{@link WebSocket}, {@link Disconnected}&gt;
     */
    @Nullable
    public MethodWrapper<WebSocket, Disconnected, Object, ?> onDisconnect;
    /**
     * calls your method as a {@link java.util.function.BiConsumer BiConsumer}&lt;{@link WebSocket}, {@link WebSocketException}&gt;
     */
    @Nullable
    public MethodWrapper<WebSocket, WebSocketException, Object, ?> onError;
    /**
     * calls your method as a {@link java.util.function.BiConsumer BiConsumer}&lt;{@link WebSocket}, {@link WebSocketFrame}&gt;
     */
    @Nullable
    public MethodWrapper<WebSocket, WebSocketFrame, Object, ?> onFrame;

    public Websocket(String address) throws IOException {
        ws = new WebSocketFactory().createSocket(address).addListener(new WebSocketAdapter() {
            @Override
            public void onConnected(WebSocket ws, Map<String, List<String>> headers) {
                if (onConnect != null) {
                    try {
                        onConnect.accept(ws, headers);
                    } catch (Throwable e) {
                        BaseScriptContext<?> ctx = onConnect.getCtx();
                        if (ctx != null) {
                            ctx.runner.profile.logError(e);
                        } else {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onDisconnected(WebSocket ws, WebSocketFrame serverFrame, WebSocketFrame clientFrame, boolean isServer) {
                if (onDisconnect != null) {
                    try {
                        onDisconnect.accept(ws, new Disconnected(serverFrame, clientFrame, isServer));
                    } catch (Throwable e) {
                        BaseScriptContext<?> ctx = onConnect.getCtx();
                        if (ctx != null) {
                            ctx.runner.profile.logError(e);
                        } else {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onConnectError(WebSocket websocket, WebSocketException exception) {
                onError(websocket, exception);
            }

            @Override
            public void onError(WebSocket websocket, WebSocketException ex) {
                if (onError != null) {
                    try {
                        onError.accept(websocket, ex);
                    } catch (Throwable e) {
                        BaseScriptContext<?> ctx = onConnect.getCtx();
                        if (ctx != null) {
                            ctx.runner.profile.logError(e);
                        } else {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFrame(WebSocket ws, WebSocketFrame frame) {
                if (onFrame != null) {
                    try {
                        onFrame.accept(ws, frame);
                    } catch (Throwable e) {
                        BaseScriptContext<?> ctx = onConnect.getCtx();
                        if (ctx != null) {
                            ctx.runner.profile.logError(e);
                        } else {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onTextMessage(WebSocket ws, String text) {
                if (onTextMessage != null) {
                    try {
                        onTextMessage.accept(ws, text);
                    } catch (Throwable e) {
                        BaseScriptContext<?> ctx = onConnect.getCtx();
                        if (ctx != null) {
                            ctx.runner.profile.logError(e);
                        } else {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public Websocket(URL address) throws URISyntaxException, IOException {
        this(address.toURI().toString());
    }

    /**
     * @return self
     * @throws WebSocketException
     * @since 1.1.9
     */
    public Websocket connect() throws WebSocketException {
        ws.connect();
        return this;
    }

    /**
     * @return
     * @since 1.1.9
     */
    public WebSocket getWs() {
        return ws;
    }

    /**
     * @param text
     * @return self
     * @since 1.1.9
     */
    public Websocket sendText(String text) {
        ws.sendText(text);
        return this;
    }

    /**
     * @return self
     * @since 1.1.9
     */
    public Websocket close() {
        ws.sendClose();
        return this;
    }

    /**
     * @param closeCode
     * @return self
     * @since 1.1.9
     */
    public Websocket close(int closeCode) {
        ws.sendClose(closeCode);
        return this;
    }

    /**
     * @author Perry "R3alCl0ud" Berman
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
