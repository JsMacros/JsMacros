package xyz.wagyourtail.jsmacros.gui.elements;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.function.Consumer;

public class Scrollbar extends Button {
    public double scrollPages = 1;
    public double scrollAmmount = 0;
    public double scrollbarHeight;
    public double scrollDistance;
    public Consumer<Double> onChange;

    public Scrollbar(int x, int y, int width, int height, int color, int borderColor, int hilightColor, double scrollPages, Consumer<Double> onChange) {
        super(x, y, width, height, color, borderColor, hilightColor, 0, new LiteralText(""), null);
        this.onChange = onChange;
        this.setScrollPages(scrollPages);
    }
    
    @Override
    public Scrollbar setPos(int x, int y, int width, int height) {
        super.setPos(x, y, width, height);
        this.scrollbarHeight = (height - 2) / (scrollPages + 1);
        double oldDistance = this.scrollDistance;
        this.scrollDistance = Math.max(height - 2 - this.scrollbarHeight, 1);
        this.scrollAmmount = this.scrollAmmount / oldDistance * scrollDistance;
        return this;
    }

    public void setScrollPages(double scrollPages) {
        this.scrollPages = Math.max(scrollPages - 1, 0);
        this.scrollbarHeight = (int) Math.ceil((height - 2) / Math.max(1, scrollPages));
        this.scrollDistance = Math.max(height - 2 - this.scrollbarHeight, 1);
        if (scrollPages < 1) {
            scrollAmmount = 0;
            onChange();
            this.active = false;
            this.visible = false;
        } else {
            this.active = true;
            this.visible = true;
        }
    }
    
    public void scrollToPercent(double percent) {
        scrollAmmount = scrollDistance * percent;
        onChange();
    }
    
    public void onClick(double mouseX, double mouseY) {
        if (this.active) {
            double mpos = mouseY - y - 1;
            if (mpos < scrollAmmount) {
                scrollAmmount = Math.max(mpos - (scrollbarHeight / 2), 0);
                onChange();
            }
            if (mpos > (scrollAmmount + scrollbarHeight)) {
                scrollAmmount = Math.min(mpos - (scrollbarHeight / 2), scrollDistance);
                onChange();
            }
        }
    }
    
    public void onChange() {
        if (onChange != null) onChange.accept(scrollPages * scrollAmmount / scrollDistance);
    }
    
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        scrollAmmount += deltaY;
        if (scrollAmmount > scrollDistance) scrollAmmount = scrollDistance;
        if (scrollAmmount < 0) scrollAmmount = 0;
        onChange();
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            // mainpart
            fill(matrices, x + 1, (int) (y + 1 + scrollAmmount), x + width - 1, (int) (y + 1 + scrollAmmount + scrollbarHeight), hilightColor);

            // outline and back
            fill(matrices, x + 1, y + 1, x + width - 1, y + height - 1, color);
            fill(matrices, x, y, x + 1, y + height, borderColor);
            fill(matrices, x + width - 1, y, x + width, y + height, borderColor);
            fill(matrices, x + 1, y, x + width - 1, y + 1, borderColor);
            fill(matrices, x + 1, y + height - 1, x + width - 1, y + height, borderColor);
        }
    }
    
}
