package xyz.wagyourtail.jsmacros.client.api.classes.render;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.api.math.Pos3D;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.access.IScreenInternal;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.wagyourgui.BaseScreen;

/**
 * just go look at {@link IScreen IScreen}
 * since all the methods are done through a mixin...
 *
 * @author Wagyourtail
 * @see IScreen
 * @since 1.0.5
 */
public class ScriptScreen extends BaseScreen {
    public boolean drawTitle;
    /**
     * @since 1.8.4
     * WARNING: this can break the game if you set it false and don't have a way to close the screen.
     */
    public boolean shouldCloseOnEsc = true;
    /**
     * @since 1.8.4
     */
    public boolean shouldPause = true;
    private final int bgStyle;
    @Nullable
    private MethodWrapper<Pos3D, DrawContext, Object, ?> onRender;

    public ScriptScreen(String title, boolean dirt) {
        super(Text.literal(title), null);
        this.bgStyle = dirt ? 0 : 1;
        this.drawTitle = true;
    }

    @Override
    protected void init() {
        BaseScreen prev = JsMacrosClient.prevScreen;
        super.init();
        JsMacrosClient.prevScreen = prev;
    }

    /**
     * @param parent parent screen to go to when this one exits.
     * @since 1.4.0
     */
    public void setParent(IScreen parent) {
        this.parent = (net.minecraft.client.gui.screen.Screen) parent;
    }

    /**
     * add custom stuff to the render function on the main thread.
     *
     * @param onRender pos3d elements are mousex, mousey, tickDelta
     * @since 1.4.0
     */
    public void setOnRender(@Nullable MethodWrapper<Pos3D, DrawContext, Object, ?> onRender) {
        this.onRender = onRender;
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if (drawContext == null) {
            return;
        }
        if (bgStyle == 0) {
            this.renderDarkening(drawContext);
        }

        if (drawTitle) {
            drawContext.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFFFF);
        }

        super.render(drawContext, mouseX, mouseY, delta);

        for (Element button : ImmutableList.copyOf(this.children())) {
            if (!(button instanceof Drawable)) {
                continue;
            }
            ((Drawable) button).render(drawContext, mouseX, mouseY, delta);
        }

        ((IScreenInternal) this).jsmacros_render(drawContext, mouseX, mouseY, delta);
        try {
            if (onRender != null) {
                onRender.accept(new Pos3D(mouseX, mouseY, delta), drawContext);
            }
        } catch (Throwable e) {
            JsMacrosClient.clientCore.profile.logError(e);
            onRender = null;
        }
    }

    @Override
    public void close() {
        openParent();
    }

    @Override
    public boolean shouldPause() {
        return shouldPause;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return shouldCloseOnEsc && super.shouldCloseOnEsc();
    }

}
