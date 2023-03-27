package xyz.wagyourtail.wagyourgui.elements;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

import static xyz.wagyourtail.jsmacros.client.access.backports.TextBackport.literal;

/**
 * @author Etheradon
 * @since 1.8.4
 */
public class Slider extends AbstractButtonWidget {

    private int steps;
    private double value;
    private final Consumer<Slider> action;

    public Slider(int x, int y, int width, int height, Text text, double value, Consumer<Slider> action, int steps) {
        super(x, y, width, height, text.asFormattedString());
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
        setValue((mouseX - (double) (x + 4)) / (double) (width - 8));
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
    protected int getYImage(boolean hovered) {
        return 0;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float delta) {
        MinecraftClient lv = MinecraftClient.getInstance();
        lv.getTextureManager().bindTexture(WIDGETS_LOCATION);
        GlStateManager.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        int offset = (isHovered() ? 2 : 1) * 20;
        blit(x + (int) (value * (double) (width - 8)), y, 0, 46 + offset, 4, 20);
        blit(x + (int) (value * (double) (width - 8)) + 4, y, 196, 46 + offset, 4, 20);
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
    public void setMessage(String message) {
        setMessage(literal(message));
    }

    public void setMessage(Text message) {
        super.setMessage(message.asFormattedString());
    }

}
