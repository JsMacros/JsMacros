package xyz.wagyourtail.wagyourgui.overlays;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import xyz.wagyourtail.wagyourgui.elements.Button;
import xyz.wagyourtail.wagyourgui.elements.TextInput;

import java.util.function.Consumer;

public class TextPrompt extends OverlayContainer {
    private final Text message;
    private final Consumer<String> accept;
    public TextInput ti;
    private final String defText;

    public TextPrompt(int x, int y, int width, int height, TextRenderer textRenderer, Text message, String defaultText, IOverlayParent parent, Consumer<String> accept) {
        super(x, y, width, height, textRenderer, parent);
        this.message = message;
        this.accept = accept;
        this.defText = defaultText == null ? "" : defaultText;
    }

    @Override
    public void init() {
        super.init();
        int w = width - 4;

        ti = this.addDrawableChild(new TextInput(x + 3, y + 25, w - 2, 14, textRenderer, 0xFF101010, 0, 0xFF4040FF, 0xFFFFFFFF, defText, null, null));

        this.addDrawableChild(new Button(x + 2, y + height - 14, w / 2, 12, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFFFF, Text.translatable("gui.cancel"), (btn) -> close()));

        this.addDrawableChild(new Button(x + w / 2, y + height - 14, w / 2, 12, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFFFF, Text.translatable("jsmacros.confirm"), (btn) -> {
            if (this.accept != null) {
                this.accept.accept(ti.content);
            }
            close();
        }));

        setFocused(ti);
        ti.setSelected(true);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        this.renderBackground(drawContext);
        int lineNum = 0;
        for (OrderedText line : textRenderer.wrapLines(message, width - 4)) {
            drawContext.drawText(textRenderer, line, (int) (x + width / 2F - textRenderer.getWidth(line) / 2F), y + 5 + (lineNum++) * textRenderer.fontHeight, 0xFFFFFFFF, false);
        }
        super.render(drawContext, mouseX, mouseY, delta);
    }

}
