package xyz.wagyourtail.jsmacros.client.gui.containers;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import scala.tools.nsc.interpreter.Formatting;
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

public class ServiceContainer extends MultiElementContainer<MacroScreen> {
    public String service;
    protected Button fileBtn;
    protected Button runningBtn;

    public ServiceContainer(int x, int y, int width, int height, FontRenderer textRenderer, ServiceScreen parent, String service) {
        super(x, y, width, height, textRenderer, parent);
        this.service = service;
        init();
    }

    @Override
    public void init() {
        super.init();

        int w  = width - 12;
        addButton(new Button(x + 1, y + 1, w * 2 / 12 - 1, height - 2, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFF, new ChatComponentText(service), (btn) -> {
            openOverlay(new TextPrompt(parent.width / 4, parent.height / 4, parent.width / 2, parent.height / 2, textRenderer, new ChatComponentText("Enter new service name"), service, getFirstOverlayParent(), (newService) -> {
                if (!Core.getInstance().services.renameService(service, newService)) {
                    openOverlay(new TextOverlay(parent.width / 4, parent.height / 4, parent.width / 2, parent.height / 2, textRenderer, getFirstOverlayParent(), new ChatComponentText("Failed to rename service").setStyle(new ChatStyle().setColor(
                        EnumChatFormatting.RED))));
                    return;
                }
                service = newService;
                btn.setMessage(new ChatComponentText(newService));
            }));
        }));

        fileBtn = addButton(new Button(x + w * 2 / 12 + 1, y + 1, w * 8 / 12 - 1, height - 2, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFF, new ChatComponentText("./"+getTrigger().file.replaceAll("\\\\", "/")), (btn) -> {
            parent.setFile(this);
        }));

        boolean enabled = getEnabled();
        boolean running = getRunning();

        addButton(new Button(x + w * 10 / 12 + 1, y + 1, w / 12 - 1, height - 2, textRenderer, enabled ? 0x7000FF00 : 0x70FF0000, 0xFF000000, 0x7F7F7F7F, 0xFFFFFF, new ChatComponentTranslation("jsmacros." + (enabled ? "enabled" : "disabled")), (btn) -> {
            if (getEnabled()) {
                Core.getInstance().services.disableService(service);
                btn.setColor(0x70FF0000);
                btn.setMessage(new ChatComponentTranslation("jsmacros.disabled"));
            } else {
                Core.getInstance().services.enableService(service);
                btn.setColor(0x7000FF00);
                btn.setMessage(new ChatComponentTranslation("jsmacros.enabled"));
            }
        }));

        runningBtn = addButton(new Button(x + w * 11 / 12 + 1, y + 1, w / 12 - 1, height - 2, textRenderer, running ? 0x7000FF00 : 0x70FF0000, 0xFF000000, 0x7F7F7F7F, 0xFFFFFF, new ChatComponentTranslation("jsmacros." + (running ? "running" : "stopped")), (btn) -> {
            if (getRunning()) {
                Core.getInstance().services.stopService(service);
            } else {
                Core.getInstance().services.startService(service);
            }
        }));

        addButton(new Button(x + w - 1, y + 1, 12, height - 2, textRenderer, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new ChatComponentText("X"), (btn) -> {
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
        fileBtn.setMessage(new ChatComponentText("./"+getTrigger().file.replaceAll("\\\\", "/")));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        if (getRunning()) {
            runningBtn.setColor(0x7000FF00);
            runningBtn.setMessage(new ChatComponentTranslation("jsmacros.running"));
        } else {
            runningBtn.setColor(0x70FF0000);
            runningBtn.setMessage(new ChatComponentTranslation("jsmacros.stopped"));
        }
    }
}
