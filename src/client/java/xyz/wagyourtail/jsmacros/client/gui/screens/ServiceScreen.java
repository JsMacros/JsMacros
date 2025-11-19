package xyz.wagyourtail.jsmacros.client.gui.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.config.ClientConfigV2;
import xyz.wagyourtail.jsmacros.client.gui.containers.ServiceContainer;
import xyz.wagyourtail.jsmacros.client.gui.containers.ServiceListTopbar;
import xyz.wagyourtail.jsmacros.client.gui.overlays.FileChooser;
import xyz.wagyourtail.jsmacros.core.service.ServiceTrigger;
import xyz.wagyourtail.wagyourgui.containers.MultiElementContainer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ServiceScreen extends MacroScreen {

    public ServiceScreen(Screen parent) {
        super(parent);
    }

    @Override
    protected void init() {
        super.init();
        serviceScreen.setColor(0x4FFFFFFF);
        List<String> services = new ArrayList<>(JsMacrosClient.clientCore.services.getServices());

        services.sort(JsMacrosClient.clientCore.config.getOptions(ClientConfigV2.class).getServiceSortComparator());

        for (String service : services) {
            addService(service);
        }
    }

    public void addService(String service) {
        macros.add(new ServiceContainer(this.width / 12, topScroll + macros.size() * 16, this.width * 5 / 6, 14, this.textRenderer, this, service));
        macroScroll.setScrollPages(((macros.size() + 1) * 16) / (double) Math.max(1, this.height - 40));
    }

    @Override
    public void removeMacro(MultiElementContainer<MacroScreen> macro) {
        for (ClickableWidget b : macro.getButtons()) {
            remove(b);
        }
        JsMacrosClient.clientCore.services.unregisterService(((ServiceContainer) macro).service);
        macros.remove(macro);
        setMacroPos();
    }

    @Override
    public void setFile(MultiElementContainer<MacroScreen> macro) {
        ServiceTrigger m = ((ServiceContainer) macro).getTrigger();
        final File file;
        if (m.file.isAbsolute()) {
            file = m.file.toFile();
        } else {
            file = JsMacrosClient.clientCore.config.macroFolder.toPath().resolve(m.file).toFile();
        }
        File dir = JsMacrosClient.clientCore.config.macroFolder;
        if (!file.equals(JsMacrosClient.clientCore.config.macroFolder)) {
            dir = file.getParentFile();
        }
        openOverlay(new FileChooser(width / 4, height / 4, width / 2, height / 2, this.textRenderer, dir, file, this, ((ServiceContainer) macro)::setFile, this::editFile));
    }

    @Override
    protected MultiElementContainer<MacroScreen> createTopbar() {
        return (MultiElementContainer) new ServiceListTopbar(this, this.width / 12, 25, this.width * 5 / 6, 14, this.textRenderer);
    }

    @Override
    public void close() {
        JsMacrosClient.clientCore.services.save();
        super.close();
    }

}
