package xyz.wagyourtail.jsmacros.gui2.elements;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class MacroListTopbar extends MultiElementContainer {

    
    
    public MacroListTopbar(int x, int y, int width, int height, TextRenderer textRenderer) {
        super(x, y, width, height, textRenderer);
    }

    public void init() {
        super.init();
    }
    
    @Override
    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        fill(matricies, x, y, x+width, y+1, 0xFFFFFFFF);
        fill(matricies, x, y+height-1, x+width, y+height, 0xFFFFFFFF);
        fill(matricies, x, y+1, x+1, y+height-1, 0xFFFFFFFF);
        fill(matricies, x+width-1, y+1, x+width, y+height-1, 0xFFFFFFFF);
        int w = this.width - 12;
        drawCenteredString(matricies, this.textRenderer, "Enabled", x + (w / 24), y+2, 0xFFFFFF);
        fill(matricies, x+(w/12), y+1, x+(w/12)+1, y+height-1, 0xFFFFFFFF);
        drawCenteredString(matricies, this.textRenderer, "Key", x + (w / 6), y+2, 0xFFFFFF);
        fill(matricies, x+(w/4), y+1, x+(w/4)+1, y+height-1, 0xFFFFFFFF);
        drawCenteredString(matricies, this.textRenderer, "File", x + (w * 15 / 24), y+2, 0xFFFFFF);
        fill(matricies, x+width-14, y+1, x+width-13, y+height-1, 0xFFFFFFFF);
        drawCenteredString(matricies, this.textRenderer, "X", x + width - 6, y+2, 0xFFFFFF);
    }
}
