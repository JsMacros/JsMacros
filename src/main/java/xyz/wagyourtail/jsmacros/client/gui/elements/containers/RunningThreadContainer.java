package xyz.wagyourtail.jsmacros.client.gui.elements.containers;

import xyz.wagyourtail.jsmacros.core.config.ScriptThreadWrapper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.apache.commons.lang3.time.DurationFormatUtils;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;

import java.util.function.Consumer;

public class RunningThreadContainer extends MultiElementContainer {
    private Consumer<RunningThreadContainer> removeContainer;
    private Button cancelButton;
    public ScriptThreadWrapper t;
    
    public RunningThreadContainer(int x, int y, int width, int height, TextRenderer textRenderer, Consumer<AbstractButtonWidget> addButton, Consumer<RunningThreadContainer> removeContainer, ScriptThreadWrapper t) {
        super(x, y, width, height, textRenderer, addButton);
        this.removeContainer = removeContainer;
        this.t = t;
        init();
    }
    
    @SuppressWarnings("deprecation")
    public void init() {
        super.init();
        cancelButton = (Button) this.addButton(new Button(x+1, y+1, height - 2, height - 2, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("X"), (btn) -> {
                if (t.t != null)
                    t.t.stop();
                Core.instance.removeThread(t);
                if (this.removeContainer != null) this.removeContainer.accept(this);
        }));
    }
    
    public void setPos(int x, int y, int width, int height) {
        super.setPos(x, y, width, height);
        cancelButton.setPos(x+1, y+1, height - 2, height - 2);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        try {
            if (t.t != null && t.t.isAlive()) {
                if (this.visible) {
                    drawCenteredString(matrices, textRenderer, textRenderer.trimToWidth(t.t.getName(), width - 105 - height), x + (width - 105 - height) / 2 + height + 4, y+2, 0xFFFFFF);
                    drawCenteredString(matrices, textRenderer, textRenderer.trimToWidth(DurationFormatUtils.formatDurationHMS(System.currentTimeMillis() - t.startTime), 100), x+width - 50 + height, y+2, 0xFFFFFF);
                    fill(matrices, x+width-101, y, x+width-100, y+height, 0xFFFFFFFF);
                    fill(matrices, x+height, y, x+height+1, y+height, 0xFFFFFFFF);
                    // border
                    fill(matrices, x, y, x + width, y + 1, 0xFFFFFFFF);
                    fill(matrices, x, y + height - 1, x + width, y + height, 0xFFFFFFFF);
                    fill(matrices, x, y + 1, x + 1, y + height - 1, 0xFFFFFFFF);
                    fill(matrices, x + width - 1, y + 1, x + width, y + height - 1, 0xFFFFFFFF);
                }
            } else {
                if (this.removeContainer != null) this.removeContainer.accept(this);
            }
        } catch(NullPointerException e) {
            if (this.removeContainer != null) this.removeContainer.accept(this);
        }
    }
}
