package xyz.wagyourtail.jsmacros.gui2.elements;

import java.util.function.Consumer;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class Scrollbar extends Button {
    public double scrollPages = 1;
    public double scrollAmmount = 0;
    public double scrollbarHeight;
    public double scrollDistance;
    public Consumer<Double> onChange;

    public Scrollbar(int x, int y, int width, int height, int color, int borderColor, int hilightColor, double scrollPages, Consumer<Double> onChange) {
        super(x, y, width, height, color, borderColor, hilightColor, 0, new LiteralText(""), null);
        this.onChange = onChange;
        this.scrollPages = scrollPages;
        this.scrollbarHeight = (int) Math.ceil((height - 2) / scrollPages);
        this.scrollDistance = height - 2 - this.scrollbarHeight;
    }

    public void setPos(int x, int y, int width, int height) {
        super.setPos(x, y, width, height);
        this.scrollbarHeight = (height - 2) / scrollPages;
        this.scrollDistance = height - 2 - this.scrollbarHeight;
    }

    public void setScrollPages(double scrollPages) {
        this.scrollPages = scrollPages;
        this.scrollbarHeight = (int) Math.ceil((height - 2) / scrollPages);
        this.scrollDistance = height - 2 - this.scrollbarHeight;
    }
    
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double mpos = mouseY - y - 1;
        if (mpos < scrollAmmount) {
            scrollAmmount = Math.max(mpos- (scrollbarHeight / 2), 0);
            if (onChange != null) onChange.accept(scrollAmmount / scrollDistance);
        }
        if (mpos > (scrollAmmount + scrollbarHeight)) {
            scrollAmmount = Math.min(mpos- (scrollbarHeight / 2), scrollDistance);
            if (onChange != null) onChange.accept(scrollAmmount / scrollDistance);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        scrollAmmount += deltaY;
        if (scrollAmmount > scrollDistance) scrollAmmount = scrollDistance;
        if (scrollAmmount < 0) scrollAmmount = 0;
        if (onChange != null) onChange.accept(scrollAmmount / scrollDistance);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        if (scrollPages > 1) {
            // mainpart
            fill(matricies, x + 1, (int) (y + 1 + scrollAmmount), x + width - 1, (int) (y + 1 + scrollAmmount + scrollbarHeight), hilightColor);

            // outline and back
            fill(matricies, x + 1, y + 1, x + width - 1, y + height - 1, color);
            fill(matricies, x, y, x + 1, y + height, borderColor);
            fill(matricies, x + width - 1, y, x + width, y + height, borderColor);
            fill(matricies, x + 1, y, x + width - 1, y + 1, borderColor);
            fill(matricies, x + 1, y + height - 1, x + width - 1, y + height, borderColor);
        }
    }
}
