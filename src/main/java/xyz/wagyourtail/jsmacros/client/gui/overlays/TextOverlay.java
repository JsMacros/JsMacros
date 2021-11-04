package xyz.wagyourtail.jsmacros.client.gui.overlays;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.wagyourgui.elements.Button;
import xyz.wagyourtail.wagyourgui.overlays.IOverlayParent;
import xyz.wagyourtail.wagyourgui.overlays.OverlayContainer;

public class TextOverlay extends OverlayContainer {
    private final Text text;
    public boolean centered = true;

    public TextOverlay(int x, int y, int width, int height, TextRenderer textRenderer, IOverlayParent parent, Text text) {
        super(x, y, width, height, textRenderer, parent);
        this.text = text;
    }

    @Override
    public void init() {
        super.init();

        addDrawableChild(new Button(x + 2, y + this.height - 12, this.width - 4, 10, this.textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.confirm"), (btn) -> {
            this.close();
        }));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        int x = this.centered ? Math.max(this.x + 3, this.x + 3 + (this.width - 6) / 2 - this.textRenderer.getWidth(this.text) / 2) : this.x + 3;
        textRenderer.drawTrimmed(this.text, x, this.y + 5, width - 6, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }

}
