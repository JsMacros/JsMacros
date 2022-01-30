package xyz.wagyourtail.jsmacros.client.gui.containers;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.jsmacros.client.gui.screens.ServiceScreen;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.service.ServiceTrigger;
import xyz.wagyourtail.wagyourgui.containers.MultiElementContainer;
import xyz.wagyourtail.wagyourgui.elements.Button;
import xyz.wagyourtail.wagyourgui.overlays.TextPrompt;

public class ServiceListTopbar extends MultiElementContainer<ServiceScreen> {

    public ServiceListTopbar(ServiceScreen parent, int x, int y, int width, int height, TextRenderer textRenderer) {
        super(x, y, width, height, textRenderer, parent);
        init();
    }

    @Override
    public void init() {
        super.init();

        int w = width - 12;

        addDrawableChild(new Button(x + 1, y + 1, w * 2 / 12 - 1, height - 3, textRenderer, 0x3FFFFFFF, 0xFF000000, 0x7F7F7F7F, 0xFFFFFF, new TranslatableText("jsmacros.servicename"), (btn) -> {
            //TODO: sort by name
            parent.reload();
        }));

        addDrawableChild(new Button(x + w * 2 / 12 + 1, y + 1, w * 8 / 12 - 1, height - 3, textRenderer, 0x3FFFFFFF, 0xFF000000, 0x7F7F7F7F, 0xFFFFFF, new TranslatableText("jsmacros.file"), (btn) -> {
            //TODO: sort by file
            parent.reload();
        }));

        addDrawableChild(new Button(x + w * 10 / 12 + 1, y + 1, w / 12 - 1, height - 3, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFF, new TranslatableText("jsmacros.enabledstatus"), (btn) -> {
            //TODO: sort by enabled
            parent.reload();
        }));

        addDrawableChild(new Button(x + w * 11 / 12 + 1, y + 1, w / 12 - 1, height - 3, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFF, new TranslatableText("jsmacros.runningstatus"), (btn) -> {
            //TODO: sort by running
            parent.reload();
        }));

        addDrawableChild(new Button(x + w - 1, y+1, 11, height - 3, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new LiteralText("+"), (btn) -> {
            openOverlay(new TextPrompt(parent.width / 4, parent.height / 4, parent.width / 2, parent.height / 2, textRenderer, new TranslatableText("jsmacros.servicename"), "", getFirstOverlayParent(), (name) -> {
                if (Core.getInstance().services.registerService(name, new ServiceTrigger(Core.getInstance().config.macroFolder, false)))
                    parent.addService(name);
            }));
        }));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

    }

}
