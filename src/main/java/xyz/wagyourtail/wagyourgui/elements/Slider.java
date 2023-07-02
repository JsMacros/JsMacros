package xyz.wagyourtail.wagyourgui.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

/**
 * @author Etheradon
 * @since 1.8.4
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
        this(x, y, width, height, text, value, action, 2);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT) {
            setValue(value + (double) (1 / steps));
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            setValue(value - (double) (1 / steps));
        }
        return false;
    }

    public double roundValue(double value) {
        return (double) Math.round(value * steps) / steps;
    }

    private void setValueFromMouse(double mouseX) {
        setValue((mouseX - (double) (getX() + 4)) / (double) (width - 8));
    }

    private void applyValue() {
        action.accept(this);
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
    public void renderButton(MatrixStack drawContext, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int offset = (isHovered() ? 2 : 1) * 20;
        drawTexture(drawContext, getX() + (int) (value * (double) (width - 8)), getY(), 0, 46 + offset, 4, 20);
        drawTexture(drawContext, getX() + (int) (value * (double) (width - 8)) + 4, getY(), 196, 46 + offset, 4, 20);
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

    public void setMessage(String message) {
        setMessage(Text.literal(message));
    }

    @Override
    public void setMessage(Text message) {
        super.setMessage(message);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

}
