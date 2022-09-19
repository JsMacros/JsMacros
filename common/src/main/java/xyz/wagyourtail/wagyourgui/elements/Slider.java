package xyz.wagyourtail.wagyourgui.elements;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import com.mojang.blaze3d.systems.RenderSystem;

import java.util.function.Consumer;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public class Slider extends ClickableWidget {

    private int steps;
    private double value;
    private final Consumer<Slider> action;

    public Slider(int x, int y, int width, int height, Text text, double value, Consumer<Slider> action, int steps) {
        super(x, y, width, height, text);
        this.action = action;
        this.steps = (steps > 1 ? steps : 2) - 1;
        this.value = roundValue(value);
    }

    public Slider(int x, int y, int width, int height, Text text, double value, Consumer<Slider> action) {
        super(x, y, width, height, text);
        this.value = value;
        this.action = action;
        this.steps = Integer.MAX_VALUE;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean bl = keyCode == 263;
        if (bl || keyCode == 262) {
            float f = bl ? -1.0F : 1.0F;
            setValue(value + (double) (f / steps));
        }

        return false;
    }

    public double roundValue(double value) {
        return (double) Math.round(value * steps) / steps;
    }

    private void setValueFromMouse(double mouseX) {
        setValue((mouseX - (double) (x + 4)) / (double) (width - 8));
    }

    private void applyValue() {
        action.accept(this);
    }

    public void setMessage(String message) {
        setMessage(Text.literal(message));
    }

    public double getValue() {
        return value;
    }

    public void setValue(double mouseX) {
        double temp = value;
        value = roundValue(MathHelper.clamp(mouseX, 0.0D, 1.0D));
        if (temp != value) {
            applyValue();
        }
    }

    public int getSteps() {
        return steps + 1;
    }

    public void setSteps(int steps) {
        this.steps = steps - 1;
    }

    @Override
    protected int getYImage(boolean hovered) {
        return 0;
    }

    @Override
    protected void renderBackground(MatrixStack matrices, MinecraftClient client, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int offset = (isHovered() ? 2 : 1) * 20;
        drawTexture(matrices, x + (int) (value * (double) (width - 8)), y, 0, 46 + offset, 4, 20);
        drawTexture(matrices, x + (int) (value * (double) (width - 8)) + 4, y, 196, 46 + offset, 4, 20);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        setValueFromMouse(mouseX);
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.playDownSound(MinecraftClient.getInstance().getSoundManager());
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        setValueFromMouse(mouseX);
        super.onDrag(mouseX, mouseY, deltaX, deltaY);
    }

    @Override
    public void setMessage(Text message) {
        super.setMessage(message);
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }

}
