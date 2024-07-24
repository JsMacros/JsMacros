package xyz.wagyourtail.jsmacros.client.api.text;

import xyz.wagyourtail.jsmacros.client.access.CustomClickEvent;
import xyz.wagyourtail.jsmacros.client.api.classes.TextBuilder;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;

public class ClientTextBuilder extends TextBuilder {

    /**
     * custom click event.
     *
     * @param action
     * @return
     * @since 1.3.0
     */
    public TextBuilder withCustomClickEvent(MethodWrapper<Object, Object, Object, ?> action) {
        self.styled(style -> style.withClickEvent(new CustomClickEvent(() -> {
            try {
                action.run();
            } catch (Throwable ex) {
                BaseScriptContext<?> ctx = action.getCtx();
                if (ctx != null) {
                    ctx.runner.profile.logError(ex);
                } else {
                    ex.printStackTrace();
                }
            }
        })));
        return this;
    }

}
