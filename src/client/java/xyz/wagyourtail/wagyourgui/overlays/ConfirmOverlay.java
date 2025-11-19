package xyz.wagyourtail.wagyourgui.overlays;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import xyz.wagyourtail.wagyourgui.elements.Button;

import java.util.List;
import java.util.function.Consumer;

public class ConfirmOverlay extends OverlayContainer {
    private final Consumer<ConfirmOverlay> accept;
    private List<OrderedText> text;
    private int lines;
    public boolean hcenter = true;
    private int vcenter;

    public ConfirmOverlay(int x, int y, int width, int height, TextRenderer textRenderer, Text message, IOverlayParent parent, Consumer<ConfirmOverlay> accept) {
        super(x, y, width, height, textRenderer, parent);
        this.setMessage(message);
        this.accept = accept;
    }

    public ConfirmOverlay(int x, int y, int width, int height, boolean hcenter, TextRenderer textRenderer, Text message, IOverlayParent parent, Consumer<ConfirmOverlay> accept) {
        this(x, y, width, height, textRenderer, message, parent, accept);
        this.hcenter = hcenter;
    }

    public void setMessage(Text message) {
        this.text = textRenderer.wrapLines(message, width - 6);
        this.lines = Math.min(Math.max((height - 15) / textRenderer.fontHeight, 1), text.size());
        this.vcenter = ((height - 15) - (lines * textRenderer.fontHeight)) / 2;
    }

    @Override
    public void init() {
        super.init();

        this.addDrawableChild(new Button(x + 2, y + height - 12, (width - 4) / 2, 10, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFFFF, Text.translatable("gui.cancel"), (btn) -> {
            this.close();
        }));

        this.addDrawableChild(new Button(x + (width - 4) / 2 + 2, y + height - 12, (width - 4) / 2, 10, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFFFF, Text.translatable("jsmacros.confirm"), (btn) -> {
            if (this.accept != null) {
                this.accept.accept(this);
            }
            this.close();
        }));

    }

    protected void renderMessage(DrawContext drawContext) {
        for (int i = 0; i < lines; ++i) {
            int w = textRenderer.getWidth(text.get(i));
            int centeredX = (int) (hcenter ? x + width / 2F - w / 2F : x + 3);
            drawContext.drawText(textRenderer, text.get(i), centeredX, y + 2 + vcenter + (i * textRenderer.fontHeight), 0xFFFFFFFF, false);
        }
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        this.renderBackground(drawContext);
        drawContext.fill(x + 1, y + height - 13, x + width - 1, y + height - 12, 0xFFFFFFFF);
        this.renderMessage(drawContext);
        super.render(drawContext, mouseX, mouseY, delta);
    }

}
