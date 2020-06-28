package xyz.wagyourtail.jsmacros.gui2.containers;

import java.util.ArrayList;
import java.util.function.Consumer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.gui2.elements.Button;
import xyz.wagyourtail.jsmacros.gui2.elements.OverlayContainer;

public class ConfirmOverlay extends OverlayContainer {
    private Consumer<ConfirmOverlay> accept;
    private Text message;
    private ArrayList<StringRenderable> text;
    private int lines;
    private int vcenter;
    private MinecraftClient mc;
    
    public ConfirmOverlay(int x, int y, int width, int height, TextRenderer textRenderer, Text message, Consumer<AbstractButtonWidget> addButton, Consumer<AbstractButtonWidget> removeButton, Consumer<OverlayContainer> close, Consumer<ConfirmOverlay>accept) {
        super(x, y, width, height, textRenderer, addButton, removeButton, close);
        this.mc = MinecraftClient.getInstance();
        this.setMessage(message);
        this.accept = accept;
    }
    
    public void setMessage(Text message) {
        this.text = new ArrayList<>(this.mc.textRenderer.wrapLines(message, width - 6));
        this.lines = Math.min(Math.max((height - 14) / mc.textRenderer.fontHeight, 1), text.size());
        this.vcenter = ((height - 14) - (lines * mc.textRenderer.fontHeight)) / 2;
    }
    
    public void init() {
        super.init();
        
        this.addButton(new Button(x + 2, y+height-12, (width - 4) / 2, 10, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("cancel"), (btn) -> {
            this.close();
        }));
        
        this.addButton(new Button(x + (width - 4) / 2 + 2, y+height-12, (width - 4) / 2, 10, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("confirm"), (btn) -> {
            if (this.accept != null) this.accept.accept(this);
            this.close();
        }));
        
    }
    
    protected void renderMessage(MatrixStack matricies) {
        for (int i = 0; i < lines; ++i) {
            drawCenteredText(matricies, textRenderer, text.get(i), x + width / 2, y + 2 + vcenter + (i * mc.textRenderer.fontHeight), 0xFFFFFF);
        }
    }
    
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.renderMessage(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }

}
