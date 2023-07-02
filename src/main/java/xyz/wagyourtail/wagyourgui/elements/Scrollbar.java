package xyz.wagyourtail.wagyourgui.elements;

import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class Scrollbar extends ClickableWidget {
    protected double scrollPages = 1;
    protected double scrollAmount = 0;
    protected double scrollbarHeight;
    protected double scrollDistance;
    protected int color;
    protected int borderColor;
    protected int highlightColor;
    protected Consumer<Double> onChange;

    public Scrollbar(int x, int y, int width, int height, int color, int borderColor, int highlightColor, double scrollPages, Consumer<Double> onChange) {
        super(x, y, width, height, Text.literal(""));
        this.color = color;
        this.borderColor = borderColor;
        this.highlightColor = highlightColor;
        this.onChange = onChange;
        this.setScrollPages(scrollPages);
    }

    public Scrollbar setPos(int x, int y, int width, int height) {
        this.setX(x);
        this.setY(y);
        this.width = width;
        this.height = height;
        this.scrollbarHeight = (height - 2) / (scrollPages + 1);
        double oldDistance = this.scrollDistance;
        this.scrollDistance = Math.max(height - 2 - this.scrollbarHeight, 1);
        this.scrollAmount = this.scrollAmount / oldDistance * scrollDistance;
        return this;
    }

    public void setScrollPages(double scrollPages) {
        this.scrollPages = Math.max(scrollPages - 1, 0);
        this.scrollbarHeight = (int) Math.ceil((height - 2) / Math.max(1, scrollPages));
        this.scrollDistance = Math.max(height - 2 - this.scrollbarHeight, 1);
        if (scrollPages < 1) {
            scrollAmount = 0;
            onChange();
            this.active = false;
            this.visible = false;
        } else {
            this.active = true;
            this.visible = true;
        }
    }

    public void scrollToPercent(double percent) {
        scrollAmount = scrollDistance * percent;
        onChange();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (this.active) {
            double mpos = mouseY - getY() - 1;
            if (mpos < scrollAmount) {
                scrollAmount = Math.max(mpos - (scrollbarHeight / 2), 0);
                onChange();
            }
            if (mpos > (scrollAmount + scrollbarHeight)) {
                scrollAmount = Math.min(mpos - (scrollbarHeight / 2), scrollDistance);
                onChange();
            }
        }
    }

    public void onChange() {
        if (onChange != null) {
            onChange.accept(scrollPages * scrollAmount / scrollDistance);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        scrollAmount += deltaY;
        if (scrollAmount > scrollDistance) {
            scrollAmount = scrollDistance;
        }
        if (scrollAmount < 0) {
            scrollAmount = 0;
        }
        onChange();
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void renderButton(MatrixStack drawContext, int mouseX, int mouseY, float delta) {
        // mainpart
        fill(drawContext, getX() + 1, (int) (getY() + 1 + scrollAmount), getX() + width - 1, (int) (getY() + 1 + scrollAmount + scrollbarHeight), highlightColor);

        // outline and back
        fill(drawContext, getX() + 1, getY() + 1, getX() + width - 1, getY() + height - 1, color);
        fill(drawContext, getX(), getY(), getX() + 1, getY() + height, borderColor);
        fill(drawContext, getX() + width - 1, getY(), getX() + width, getY() + height, borderColor);
        fill(drawContext, getX() + 1, getY(), getX() + width - 1, getY() + 1, borderColor);
        fill(drawContext, getX() + 1, getY() + height - 1, getX() + width - 1, getY() + height, borderColor);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

}
