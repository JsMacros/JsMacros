package xyz.wagyourtail.jsmacros.client.access;

public interface IScreenInternal {
    void jsmacros_render(int mouseX, int mouseY, float delta);

    void jsmacros_mouseClicked(double mouseX, double mouseY, int button);

    void jsmacros_mouseReleased(double mouseX, double mouseY, int button);

    void jsmacros_mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY);

    void jsmacros_mouseScrolled(double mouseX, double mouseY, double amount);

    void jsmacros_keyPressed(int keyCode, int scanCode, int modifiers);

    void jsmacros_mouseScrolled(int mouseX, int mouseY, int amount);
    //    void jsmacros_charTyped(char chr, int modifiers);

    GuiButton getFocused();

    boolean mouseScrolled(int mouseX, int mouseY, int amount);

    void clickBtn(GuiButton btn) throws IOException;
}