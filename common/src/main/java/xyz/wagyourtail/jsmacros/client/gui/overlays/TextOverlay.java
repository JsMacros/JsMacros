package xyz.wagyourtail.jsmacros.client.gui.overlays;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.wagyourgui.elements.Button;
import xyz.wagyourtail.wagyourgui.overlays.IOverlayParent;
import xyz.wagyourtail.wagyourgui.overlays.OverlayContainer;

import java.util.List;

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

        addButton(new Button(x + 2, y + this.height - 12, this.width - 4, 10, this.textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.confirm"), (btn) -> {
            this.close();
        }));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        renderBackground();
        int x = this.centered ? Math.max(this.x + 3, this.x + 3 + (this.width - 6) / 2 - this.textRenderer.getStringWidth(this.text.getString()) / 2) : this.x + 3;
        textRenderer.drawTrimmed(this.text.getString(), x, this.y + 5, width - 6, 0xFFFFFF);
        super.render(mouseX, mouseY, delta);
    }

}
