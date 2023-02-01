package xyz.wagyourtail.jsmacros.client.api.classes.render;

import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.access.IScreenInternal;
import xyz.wagyourtail.jsmacros.client.api.classes.math.Pos3D;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.wagyourgui.BaseScreen;

/**
 * just go look at {@link IScreen IScreen}
 * since all the methods are done through a mixin...
 * 
 * @author Wagyourtail
 * 
 * @since 1.0.5
 * 
 * @see IScreen
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
    private MethodWrapper<Pos3D, MatrixStack, Object, ?> onRender;
    
    public ScriptScreen(String title, boolean dirt) {
        super(new LiteralText(title), null);
        this.bgStyle = dirt ? 0 : 1;
        this.drawTitle = true;
    }

    @Override
    protected void init() {
        BaseScreen prev = JsMacros.prevScreen;
        super.init();
        JsMacros.prevScreen = prev;
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
    public void setOnRender(MethodWrapper<Pos3D, MatrixStack, Object, ?> onRender) {
        this.onRender = onRender;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        if (bgStyle == 0) this.renderDirtBackground(0);
        else if (bgStyle == 1) this.renderBackground(0);

        if (drawTitle) {
            drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 20, 0xFFFFFF);
        }

        super.render(mouseX, mouseY, delta);

        for (AbstractButtonWidget button : this.buttons) {
            button.render(mouseX, mouseY, delta);
        }

        ((IScreenInternal) this).jsmacros_render(mouseX, mouseY, delta);
        try {
            if (onRender != null) onRender.accept(new Pos3D(mouseX, mouseY, delta));
        } catch (Throwable e) {
            Core.getInstance().profile.logError(e);
            onRender = null;
        }
    }

    @Override
    public void close() {
        openParent();
    }

    @Override
    public boolean isPauseScreen() {
        return shouldPause;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return shouldCloseOnEsc && super.shouldCloseOnEsc();
    }
}
