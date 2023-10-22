package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.client.gui.DrawContext;
import xyz.wagyourtail.doclet.DocletIgnore;

@DocletIgnore
public interface IScreenInternal {
    void jsmacros_render(DrawContext drawContext, int mouseX, int mouseY, float delta);

    void jsmacros_mouseClicked(double mouseX, double mouseY, int button);

    void jsmacros_mouseReleased(double mouseX, double mouseY, int button);

    void jsmacros_mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY);

    void jsmacros_mouseScrolled(double mouseX, double mouseY, double horiz, double vert);

    void jsmacros_keyPressed(int keyCode, int scanCode, int modifiers);

    void jsmacros_charTyped(char chr, int modifiers);

}
