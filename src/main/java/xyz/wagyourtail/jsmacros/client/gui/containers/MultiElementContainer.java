package xyz.wagyourtail.jsmacros.client.gui.containers;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public abstract class MultiElementContainer<T extends IContainerParent> extends DrawableHelper {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected List<AbstractButtonWidget> buttons = new ArrayList<>();
    protected T parent;
    protected TextRenderer textRenderer;
    protected boolean visible = true;
    
    public MultiElementContainer(int x, int y, int width, int height, TextRenderer textRenderer, T parent) {
        this.textRenderer = textRenderer;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.parent = parent;
    }
    
    public void init() {
        buttons.clear();
    }
    

    public boolean getVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        for (AbstractButtonWidget btn : buttons) {
            btn.visible = visible;
            btn.active = visible;
        }
        this.visible = visible;
    }
    
    public <T extends AbstractButtonWidget> T addButton(T btn) {
        buttons.add(btn);
        parent.addButton(btn);
        return btn;
    }
    
    public List<AbstractButtonWidget> getButtons() {
        return buttons;
    }
    
    public void setPos(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public abstract void render(MatrixStack matrices, int mouseX, int mouseY, float delta);
    
}
