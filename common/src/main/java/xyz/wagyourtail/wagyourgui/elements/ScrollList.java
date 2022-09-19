package xyz.wagyourtail.wagyourgui.elements;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;

import com.mojang.blaze3d.systems.RenderSystem;

import java.util.List;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public abstract class ScrollList<T extends EntryListWidget.Entry<T>, U> extends EntryListWidget<T> {

    protected ScrollList(int x, int y, int width, int height, int itemHeight) {
        super(MinecraftClient.getInstance(), width, height, y - (height / 2), (y - (height / 2)) + height, itemHeight);
        //remove black edges at the top and bottom
        setRenderHorizontalShadows(false);
        setRenderBackground(false);
        setLeftPos(x);
    }

    public abstract void updateEntries(List<U> entries);

    @Override
    public int getRowWidth() {
        return width;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        double scale = client.getWindow().getScaleFactor();

        //remember y coordinate starts from the bottom and goes up
        //only render the part that is inside the bounds of this widget
        RenderSystem.enableScissor((int) (left * scale),
                (int) (client.getWindow().getFramebufferHeight() - ((top + height) * scale)),
                (int) (width * scale),
                (int) (height * scale));
        super.render(matrices, mouseX, mouseY, delta);
        RenderSystem.disableScissor();
    }

    @Override
    protected int getScrollbarPositionX() {
        return this.left + width - 12;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }
}
