package xyz.wagyourtail.jsmacros.client.gui.overlays.settings;

import com.google.common.collect.Lists;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.jsmacros.client.gui.containers.ListContainer;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;
import xyz.wagyourtail.jsmacros.client.gui.overlays.IOverlayParent;
import xyz.wagyourtail.jsmacros.client.gui.overlays.OverlayContainer;

public class SettingsOverlay extends OverlayContainer {
    private final Text title = new TranslatableText("jsmacros.settings");
    private ListContainer sections;
    public SettingsOverlay(int x, int y, int width, int height, TextRenderer textRenderer, IOverlayParent parent) {
        super(x, y, width, height, textRenderer, parent);
    }
    
    @Override
    public void init() {
        super.init();
        int w = width - 4;
    
        this.addButton(new Button(x + width - 12, y + 2, 10, 10, textRenderer, 0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("X"), (btn) -> this.close()));
        sections = new ListContainer(x + 2, y + 13, w / 3, height - 17, textRenderer, Lists.newArrayList(
        
        ), this, (selection) -> {
        
        });
        
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        int w = width - 4;
        
        textRenderer.drawTrimmed(title, x + 3, y + 3, width - 14, 0xFFFFFF);
        fill(matrices, x + 2, y + 12, x + width - 2, y + 13, 0xFFFFFFFF);
        
        //sep
        fill(matrices, x + w / 3, y + 13, x + w / 3 + 1, y + height, 0xFFFFFFFF);
        
        super.render(matrices, mouseX, mouseY, delta);
    }
}
