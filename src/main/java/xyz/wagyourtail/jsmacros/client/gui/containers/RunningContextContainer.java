package xyz.wagyourtail.jsmacros.client.gui.containers;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.apache.commons.lang3.time.DurationFormatUtils;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.gui.screens.CancelScreen;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;
import xyz.wagyourtail.jsmacros.core.service.EventService;
import xyz.wagyourtail.wagyourgui.containers.MultiElementContainer;
import xyz.wagyourtail.wagyourgui.elements.Button;

import static net.minecraft.client.gui.DrawableHelper.drawCenteredTextWithShadow;
import static net.minecraft.client.gui.DrawableHelper.fill;
import static xyz.wagyourtail.jsmacros.client.backport.TextBackport.literal;

public class RunningContextContainer extends MultiElementContainer<CancelScreen> {
    private Button cancelButton;
    public BaseScriptContext<?> t;
    public boolean service;

    public RunningContextContainer(int x, int y, int width, int height, TextRenderer textRenderer, CancelScreen parent, BaseScriptContext<?> t) {
        super(x, y, width, height, textRenderer, parent);
        this.t = t;
        this.service = this.t.getTriggeringEvent() instanceof EventService;
        init();
    }

    @Override
    public void init() {
        super.init();
        cancelButton = this.addDrawableChild(new Button(x + 1, y + 1, height - 2, height - 2, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, literal("X"), (btn) -> {
            BaseScriptContext<?> ctx = t;
            if (ctx != null && !ctx.isContextClosed()) {
                ctx.closeContext();
            }
            parent.removeContainer(this);
        }));
    }

    @Override
    public void setPos(int x, int y, int width, int height) {
        super.setPos(x, y, width, height);
        cancelButton.setPos(x + 1, y + 1, height - 2, height - 2);
    }

    @Override
    public void render(MatrixStack drawContext, int mouseX, int mouseY, float delta) {
        try {
            if (t != null) {
                if (t.isContextClosed()) {
                    JsMacros.LOGGER.warn("Closed context {} was still in list", t.getMainThread().getName());
                    parent.removeContainer(this);
                } else if (this.visible) {
                    drawCenteredTextWithShadow(drawContext, textRenderer, literal(textRenderer.trimToWidth(service ? ((EventService) t.getTriggeringEvent()).serviceName : t.getMainThread().getName(), width - 105 - height)).asOrderedText(), x + (width - 105 - height) / 2 + height + 4, y + 2, 0xFFFFFF);
                    drawCenteredTextWithShadow(drawContext, textRenderer, literal(textRenderer.trimToWidth(DurationFormatUtils.formatDurationHMS(System.currentTimeMillis() - t.startTime), 100)).asOrderedText(), x + width - 50 + height, y + 2, 0xFFFFFF);
                    fill(drawContext, x + width - 101, y, x + width - 100, y + height, 0xFFFFFFFF);
                    fill(drawContext, x + height, y, x + height + 1, y + height, 0xFFFFFFFF);
                    // border
                    fill(drawContext, x, y, x + width, y + 1, 0xFFFFFFFF);
                    fill(drawContext, x, y + height - 1, x + width, y + height, 0xFFFFFFFF);
                    fill(drawContext, x, y + 1, x + 1, y + height - 1, 0xFFFFFFFF);
                    fill(drawContext, x + width - 1, y + 1, x + width, y + height - 1, 0xFFFFFFFF);
                }
            } else {
                parent.removeContainer(this);
            }
        } catch (NullPointerException e) {
            parent.removeContainer(this);
        }
    }

}
