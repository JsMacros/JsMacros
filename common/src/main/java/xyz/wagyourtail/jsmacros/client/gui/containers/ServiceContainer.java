package xyz.wagyourtail.jsmacros.client.gui.containers;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import xyz.wagyourtail.jsmacros.client.gui.overlays.TextOverlay;
import xyz.wagyourtail.jsmacros.client.gui.screens.MacroScreen;
import xyz.wagyourtail.jsmacros.client.gui.screens.ServiceScreen;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.service.ServiceManager;
import xyz.wagyourtail.jsmacros.core.service.ServiceTrigger;
import xyz.wagyourtail.wagyourgui.containers.MultiElementContainer;
import xyz.wagyourtail.wagyourgui.elements.Button;
import xyz.wagyourtail.wagyourgui.overlays.TextPrompt;

import java.io.File;

import static xyz.wagyourtail.jsmacros.client.access.backports.TextBackport.literal;
import static xyz.wagyourtail.jsmacros.client.access.backports.TextBackport.translatable;

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
        nameBtn = addDrawableChild(new Button(x + 1, y + 1, w * 2 / 12 - 1, height - 2, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFF, literal(service), (btn) -> {
            openOverlay(new TextPrompt(parent.width / 4, parent.height / 4, parent.width / 2, parent.height / 2, textRenderer, literal("Enter new service name"), service, getFirstOverlayParent(), (newService) -> {
                if (!Core.getInstance().services.renameService(service, newService)) {
                    openOverlay(new TextOverlay(parent.width / 4, parent.height / 4, parent.width / 2, parent.height / 2, textRenderer, getFirstOverlayParent(), new LiteralText("Failed to rename service").styled(s -> s.setColor(Formatting.RED))));
                    return;
                }
                service = newService;
                btn.setMessage(new LiteralText(newService));
            }));
        }));

        fileBtn = addButton(new Button(x + w * 2 / 12 + 1, y + 1, w * 8 / 12 - 1, height - 2, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFF, new LiteralText("./"+getTrigger().file.replaceAll("\\\\", "/")), (btn) -> {
            parent.setFile(this);
        }));

        boolean enabled = getEnabled();
        boolean running = getRunning();

        enableBtn = addDrawableChild(new Button(x + w * 10 / 12 + 1, y + 1, w / 12, height - 2, textRenderer, enabled ? 0x7000FF00 : 0x70FF0000, 0xFF000000, 0x7F7F7F7F, 0xFFFFFF, translatable("jsmacros." + (enabled ? "enabled" : "disabled")), (btn) -> {
            if (getEnabled()) {
                Core.getInstance().services.disableService(service);
                btn.setColor(0x70FF0000);
                btn.setMessage(new TranslatableText("jsmacros.disabled"));
            } else {
                Core.getInstance().services.enableService(service);
                btn.setColor(0x7000FF00);
                btn.setMessage(new TranslatableText("jsmacros.enabled"));
            }
        }));

        runningBtn = addDrawableChild(new Button(x + w * 11 / 12 + 1, y + 1, w / 12, height - 2, textRenderer, running ? 0x7000FF00 : 0x70FF0000, 0xFF000000, 0x7F7F7F7F, 0xFFFFFF, translatable("jsmacros." + (running ? "running" : "stopped")), (btn) -> {
            if (getRunning()) {
                Core.getInstance().services.stopService(service);
            } else {
                Core.getInstance().services.startService(service);
            }
        }));

        delBtn = addDrawableChild(new Button(x + w - 1, y + 1, 12, height - 2, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, literal("X"), (btn) -> {
            parent.confirmRemoveMacro(this);
        }));

    }

    public boolean getEnabled() {
        ServiceManager.ServiceStatus status = Core.getInstance().services.status(service);
        return status == ServiceManager.ServiceStatus.ENABLED || status == ServiceManager.ServiceStatus.STOPPED;
    }

    public boolean getRunning() {
        ServiceManager.ServiceStatus status = Core.getInstance().services.status(service);
        return status == ServiceManager.ServiceStatus.ENABLED || status == ServiceManager.ServiceStatus.RUNNING;
    }

    public ServiceTrigger getTrigger() {
        return Core.getInstance().services.getTrigger(service);
    }

    public void setFile(File file) {
        getTrigger().file = Core.getInstance().config.macroFolder.getAbsoluteFile().toPath().relativize(file.getAbsoluteFile().toPath()).toString();
        Core.getInstance().services.disableReload(service);
        fileBtn.setMessage(literal("./"+getTrigger().file.replaceAll("\\\\", "/")));
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
    public void render(int mouseX, int mouseY, float delta) {
        int w = width - 12;
        //seperate
        fill(matrices, x + w * 2 / 12, y + 1, x + w * 2 / 12 + 1, y + height - 1, 0xFFFFFFFF);
        fill(matrices, x + w * 10 / 12, y + 1, x + w * 10 / 12 + 1, y + height - 1, 0xFFFFFFFF);
        fill(matrices, x + w * 11 / 12, y + 1, x + w * 11 / 12 + 1, y + height - 1, 0xFFFFFFFF);
        fill(matrices, x + width - 14, y + 1, x + width - 13, y + height - 1, 0xFFFFFFFF);
        
        // border
        fill(matrices, x, y, x + width, y + 1, 0xFFFFFFFF);
        fill(matrices, x, y + height - 1, x + width, y + height, 0xFFFFFFFF);
        fill(matrices, x, y + 1, x + 1, y + height - 1, 0xFFFFFFFF);
        fill(matrices, x + width - 1, y + 1, x + width, y + height - 1, 0xFFFFFFFF);
        
        if (getRunning()) {
            runningBtn.setColor(0x7000FF00);
            runningBtn.setMessage(new TranslatableText("jsmacros.running"));
        } else {
            runningBtn.setColor(0x70FF0000);
            runningBtn.setMessage(new TranslatableText("jsmacros.stopped"));
        }
    }
}