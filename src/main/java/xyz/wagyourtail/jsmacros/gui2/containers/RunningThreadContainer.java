package xyz.wagyourtail.jsmacros.gui2.containers;

import java.util.function.Consumer;

import org.apache.commons.lang3.time.DurationFormatUtils;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import xyz.wagyourtail.jsmacros.gui2.elements.Button;
import xyz.wagyourtail.jsmacros.gui2.elements.MultiElementContainer;
import xyz.wagyourtail.jsmacros.runscript.RunScript;
import xyz.wagyourtail.jsmacros.runscript.RunScript.thread;

public class RunningThreadContainer extends MultiElementContainer {
    private Consumer<RunningThreadContainer> removeContainer;
    private Button cancelButton;
    public thread t;
    
    public RunningThreadContainer(int x, int y, int width, int height, TextRenderer textRenderer, Consumer<AbstractButtonWidget> addButton, Consumer<RunningThreadContainer> removeContainer, thread t) {
        super(x, y, width, height, textRenderer, addButton);
        this.removeContainer = removeContainer;
        this.t = t;
        init();
    }
    
    @SuppressWarnings("deprecation")
    public void init() {
        super.init();
        cancelButton = (Button) this.addButton(new Button(x+1, y+1, height - 2, height - 2, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("X"), (btn) -> {
            t.t.stop();
            RunScript.removeThread(t);
            if (this.removeContainer != null) this.removeContainer.accept(this);
        }));
    }
    
    public void setPos(int x, int y, int width, int height) {
        super.setPos(x, y, width, height);
        cancelButton.setPos(x+1, y+1, height - 2, height - 2);
    }

    @Override
    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        if (t.t.isAlive()) {
            if (this.visible) {
                drawCenteredString(matricies, textRenderer, textRenderer.trimToWidth(t.t.getName(), width - 105 - height), x + (width - 105 - height) / 2 + height + 4, y+2, 0xFFFFFF);
                drawCenteredString(matricies, textRenderer, textRenderer.trimToWidth(DurationFormatUtils.formatDurationHMS(System.currentTimeMillis() - t.startTime), 100), x+width - 50 + height, y+2, 0xFFFFFF);
                fill(matricies, x+width-101, y, x+width-100, y+height, 0xFFFFFFFF);
                fill(matricies, x+height, y, x+height+1, y+height, 0xFFFFFFFF);
                // border
                fill(matricies, x, y, x + width, y + 1, 0xFFFFFFFF);
                fill(matricies, x, y + height - 1, x + width, y + height, 0xFFFFFFFF);
                fill(matricies, x, y + 1, x + 1, y + height - 1, 0xFFFFFFFF);
                fill(matricies, x + width - 1, y + 1, x + width, y + height - 1, 0xFFFFFFFF);
            }
        } else {
            if (this.removeContainer != null) this.removeContainer.accept(this);
        }
    }
}
