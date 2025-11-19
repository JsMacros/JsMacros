package xyz.wagyourtail.jsmacros.client.gui.containers;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.gui.overlays.TextOverlay;
import xyz.wagyourtail.jsmacros.client.gui.screens.MacroScreen;
import xyz.wagyourtail.jsmacros.client.gui.screens.ServiceScreen;
import xyz.wagyourtail.jsmacros.core.service.ServiceManager;
import xyz.wagyourtail.jsmacros.core.service.ServiceTrigger;
import xyz.wagyourtail.wagyourgui.containers.MultiElementContainer;
import xyz.wagyourtail.wagyourgui.elements.Button;
import xyz.wagyourtail.wagyourgui.overlays.TextPrompt;

import java.io.File;

public class ServiceContainer extends MultiElementContainer<MacroScreen> {
    public String service;
    protected Button nameBtn;
    protected Button fileBtn;
    protected Button enableBtn;
    protected Button runningBtn;
    protected Button delBtn;

    public ServiceContainer(int x, int y, int width, int height, TextRenderer textRenderer, ServiceScreen parent, String service) {
        super(x, y, width, height, textRenderer, parent);
        this.service = service;
        init();
    }

    @Override
    public void init() {
        super.init();

        int w = width - 12;
        nameBtn = addDrawableChild(new Button(x + 1, y + 1, w * 2 / 12 - 1, height - 2, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, Text.literal(service), (btn) -> {
            openOverlay(new TextPrompt(parent.width / 4, parent.height / 4, parent.width / 2, parent.height / 2, textRenderer, Text.literal("Enter new service name"), service, getFirstOverlayParent(), (newService) -> {
                if (!JsMacrosClient.clientCore.services.renameService(service, newService)) {
                    openOverlay(new TextOverlay(parent.width / 4, parent.height / 4, parent.width / 2, parent.height / 2, textRenderer, getFirstOverlayParent(), Text.literal("Failed to rename service").styled(s -> s.withColor(Formatting.RED))));
                    return;
                }
                service = newService;
                btn.setMessage(Text.literal(newService));
            }));
        }));

        fileBtn = addDrawableChild(new Button(x + w * 2 / 12 + 1, y + 1, w * 8 / 12 - 1, height - 2, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, Text.literal("./" + getTrigger().file.toString().replaceAll("\\\\", "/")), (btn) -> {
            parent.setFile(this);
        }));

        boolean enabled = getEnabled();
        boolean running = getRunning();

        enableBtn = addDrawableChild(new Button(x + w * 10 / 12 + 1, y + 1, w / 12, height - 2, textRenderer, enabled ? 0x7000FF00 : 0x70FF0000, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, Text.translatable("jsmacros." + (enabled ? "enabled" : "disabled")), (btn) -> {
            if (getEnabled()) {
                JsMacrosClient.clientCore.services.disableService(service);
                btn.setColor(0x70FF0000);
                btn.setMessage(Text.translatable("jsmacros.disabled"));
            } else {
                JsMacrosClient.clientCore.services.enableService(service);
                btn.setColor(0x7000FF00);
                btn.setMessage(Text.translatable("jsmacros.enabled"));
            }
        }));

        runningBtn = addDrawableChild(new Button(x + w * 11 / 12 + 1, y + 1, w / 12, height - 2, textRenderer, running ? 0x7000FF00 : 0x70FF0000, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, Text.translatable("jsmacros." + (running ? "running" : "stopped")), (btn) -> {
            if (getRunning()) {
                JsMacrosClient.clientCore.services.stopService(service);
            } else {
                JsMacrosClient.clientCore.services.startService(service);
            }
        }));

        delBtn = addDrawableChild(new Button(x + w - 1, y + 1, 12, height - 2, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, Text.literal("X"), (btn) -> {
            parent.confirmRemoveMacro(this);
        }));

    }

    public boolean getEnabled() {
        ServiceManager.ServiceStatus status = JsMacrosClient.clientCore.services.status(service);
        return status == ServiceManager.ServiceStatus.ENABLED || status == ServiceManager.ServiceStatus.STOPPED;
    }

    public boolean getRunning() {
        ServiceManager.ServiceStatus status = JsMacrosClient.clientCore.services.status(service);
        return status == ServiceManager.ServiceStatus.ENABLED || status == ServiceManager.ServiceStatus.RUNNING;
    }

    public ServiceTrigger getTrigger() {
        return JsMacrosClient.clientCore.services.getTrigger(service);
    }

    public void setFile(File file) {
        getTrigger().file = JsMacrosClient.clientCore.config.macroFolder.getAbsoluteFile().toPath().relativize(file.getAbsoluteFile().toPath());
        JsMacrosClient.clientCore.services.disableReload(service);
        fileBtn.setMessage(Text.literal("./" + getTrigger().file.toString().replaceAll("\\\\", "/")));
    }

    @Override
    public void setPos(int x, int y, int width, int height) {
        super.setPos(x, y, width, height);
        int w = width - 12;
        nameBtn.setPos(x + 1, y + 1, w * 2 / 12 - 1, height - 2);
        fileBtn.setPos(x + w * 2 / 12 + 1, y + 1, w * 8 / 12 - 1, height - 2);
        enableBtn.setPos(x + w * 10 / 12 + 1, y + 1, w / 12, height - 2);
        runningBtn.setPos(x + w * 11 / 12 + 1, y + 1, w / 12, height - 2);
        delBtn.setPos(x + w - 1, y + 1, 12, height - 2);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        int w = width - 12;
        //seperate
        drawContext.fill(x + w * 2 / 12, y + 1, x + w * 2 / 12 + 1, y + height - 1, 0xFFFFFFFF);
        drawContext.fill(x + w * 10 / 12, y + 1, x + w * 10 / 12 + 1, y + height - 1, 0xFFFFFFFF);
        drawContext.fill(x + w * 11 / 12, y + 1, x + w * 11 / 12 + 1, y + height - 1, 0xFFFFFFFF);
        drawContext.fill(x + width - 14, y + 1, x + width - 13, y + height - 1, 0xFFFFFFFF);

        // border
        drawContext.fill(x, y, x + width, y + 1, 0xFFFFFFFF);
        drawContext.fill(x, y + height - 1, x + width, y + height, 0xFFFFFFFF);
        drawContext.fill(x, y + 1, x + 1, y + height - 1, 0xFFFFFFFF);
        drawContext.fill(x + width - 1, y + 1, x + width, y + height - 1, 0xFFFFFFFF);

        if (getRunning()) {
            runningBtn.setColor(0x7000FF00);
            runningBtn.setMessage(Text.translatable("jsmacros.running"));
        } else {
            runningBtn.setColor(0x70FF0000);
            runningBtn.setMessage(Text.translatable("jsmacros.stopped"));
        }
    }

}
