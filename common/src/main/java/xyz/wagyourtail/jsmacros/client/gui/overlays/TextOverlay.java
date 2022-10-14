package xyz.wagyourtail.jsmacros.client.gui.overlays;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import xyz.wagyourtail.wagyourgui.elements.Button;
import xyz.wagyourtail.wagyourgui.overlays.IOverlayParent;
import xyz.wagyourtail.wagyourgui.overlays.OverlayContainer;

public class TextOverlay extends OverlayContainer {
    private final IChatComponent text;
    public boolean centered = true;

    public TextOverlay(int x, int y, int width, int height, FontRenderer textRenderer, IOverlayParent parent, IChatComponent text) {
        super(x, y, width, height, textRenderer, parent);
        this.text = text;
    }

    @Override
    public void init() {
        super.init();

        addButton(new Button(x + 2, y + this.height - 12, this.width - 4, 10, this.textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentTranslation("jsmacros.confirm"), (btn) -> {
            this.close();
        }));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        renderBackground();
        int x = this.centered ? Math.max(this.x + 3, this.x + 3 + (this.width - 6) / 2 - this.textRenderer.getStringWidth(this.text.asFormattedString()) / 2) : this.x + 3;
        textRenderer.drawTrimmed(this.text.asFormattedString(), x, this.y + 5, width - 6, 0xFFFFFF);
        super.render(mouseX, mouseY, delta);
    }

}
