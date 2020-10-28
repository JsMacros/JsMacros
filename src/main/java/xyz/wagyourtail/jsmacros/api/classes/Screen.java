package xyz.wagyourtail.jsmacros.api.classes;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

/**
 * just go look at {@link xyz.wagyourtail.jsmacros.api.sharedinterfaces.IScreen IScreen}
 * 
 * @author Wagyourtail
 * 
 * @since 1.0.5
 * 
 * @see xyz.wagyourtail.jsmacros.api.sharedinterfaces.IScreen
 */
public class Screen extends net.minecraft.client.gui.screen.Screen {
    private final int bgStyle;
    
    
    public Screen(String title, boolean dirt) {
        super(new LiteralText(title));
        this.bgStyle = dirt ? 0 : 1;
    }
    
    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        if (matricies == null) return;
        if (bgStyle == 0) this.renderBackgroundTexture(0);
        else if (bgStyle == 1) this.renderBackground(matricies, 0);
        
        drawCenteredText(matricies, this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        
        super.render(matricies, mouseX, mouseY, delta);
    }
}
