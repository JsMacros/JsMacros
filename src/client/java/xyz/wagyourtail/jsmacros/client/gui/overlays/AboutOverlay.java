package xyz.wagyourtail.jsmacros.client.gui.overlays;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import xyz.wagyourtail.wagyourgui.elements.Button;
import xyz.wagyourtail.wagyourgui.overlays.IOverlayParent;
import xyz.wagyourtail.wagyourgui.overlays.OverlayContainer;

import java.util.List;

public class AboutOverlay extends OverlayContainer {
    private List<OrderedText> text;
    private int lines;
    private int vcenter;

    public AboutOverlay(int x, int y, int width, int height, TextRenderer textRenderer, IOverlayParent parent) {
        super(x, y, width, height, textRenderer, parent);
    }

    @Override
    public void init() {
        super.init();
        int w = width - 4;
        this.addDrawableChild(new Button(x + width - 12, y + 2, 10, 10, textRenderer, 0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFFFF, Text.literal("X"), (btn) -> this.close()));

        this.addDrawableChild(new Button(x + 2, y + height - 14, w / 3, 12, textRenderer, 0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFFFF, Text.literal("Website"), (btn) -> Util.getOperatingSystem().open("https://jsmacros.wagyourtail.xyz")));

        this.addDrawableChild(new Button(x + w / 3 + 2, y + height - 14, w / 3, 12, textRenderer, 0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFFFF, Text.literal("Discord"), (btn) -> Util.getOperatingSystem().open("https://discord.gg/P6W58J8")));

        this.addDrawableChild(new Button(x + w * 2 / 3 + 2, y + height - 14, w / 3, 12, textRenderer, 0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFFFF, Text.literal("CurseForge"), (btn) -> Util.getOperatingSystem().open("https://www.curseforge.com/minecraft/mc-mods/jsmacros")));

        this.setMessage(Text.translatable("jsmacros.aboutinfo"));
    }

    public void setMessage(Text message) {
        this.text = textRenderer.wrapLines(message, width - 6);
        this.lines = Math.min(Math.max((height - 27) / textRenderer.fontHeight, 1), text.size());
        this.vcenter = ((height - 12) - (lines * textRenderer.fontHeight)) / 2;
    }

    protected void renderMessage(DrawContext drawContext) {
        for (int i = 0; i < lines; ++i) {
            int w = textRenderer.getWidth(text.get(i));
            drawContext.drawText(textRenderer, text.get(i), (int) (x + width / 2F - w / 2F), y + 2 + vcenter + (i * textRenderer.fontHeight), 0xFFFFFFFF, false);
        }
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        renderBackground(drawContext);

        drawContext.drawWrappedText(textRenderer, Text.translatable("jsmacros.about"), x + 3, y + 3, width - 14, 0xFFFFFFFF, false);
        renderMessage(drawContext);

        drawContext.fill(x + 2, y + 12, x + width - 2, y + 13, 0xFFFFFFFF);
        drawContext.fill(x + 2, y + height - 15, x + width - 2, y + height - 14, 0xFFFFFFFF);
        super.render(drawContext, mouseX, mouseY, delta);

    }

}
