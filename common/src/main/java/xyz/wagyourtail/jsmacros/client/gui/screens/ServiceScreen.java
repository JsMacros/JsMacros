package xyz.wagyourtail.jsmacros.client.gui.screens;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import xyz.wagyourtail.jsmacros.client.gui.containers.ServiceContainer;
import xyz.wagyourtail.jsmacros.client.gui.containers.ServiceListTopbar;
import xyz.wagyourtail.jsmacros.client.gui.overlays.FileChooser;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.wagyourgui.containers.MultiElementContainer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ServiceScreen extends MacroScreen {

    public ServiceScreen(GuiScreen parent) {
        super(parent);
    }

    @Override
    public void init() {
        super.init();

        keyScreen.onPress = (btn) -> {
            this.openParent();
            if (this.parent instanceof EventMacrosScreen) ((MacroScreen) parent).openParent();
        };
        eventScreen.onPress = (btn) -> {
            this.openParent();
            if (this.parent instanceof KeyMacrosScreen) client.openScreen(new EventMacrosScreen(parent));
        };

        serviceScreen.onPress = null;

        List<String> services = new ArrayList<>(Core.getInstance().services.getServices());

        //TODO: sort services from topbar, for name let's just sort alphabetically
        services.sort(String::compareTo);

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
        for (GuiButton b : macro.getButtons()) {
            removeButton(b);
        }
        Core.getInstance().services.unregisterService(((ServiceContainer) macro).service);
        macros.remove(macro);
        setMacroPos();
    }

    @Override
    public void  setFile(MultiElementContainer<MacroScreen> macro) {
        File f = new File(Core.getInstance().config.macroFolder, ((ServiceContainer) macro).getTrigger().file);
        File dir = Core.getInstance().config.macroFolder;
        if (!f.equals(Core.getInstance().config.macroFolder)) dir = f.getParentFile();
        openOverlay(new FileChooser(width / 4, height / 4, width / 2, height / 2, this.textRenderer, dir, f, this, ((ServiceContainer) macro)::setFile, this::editFile));
    }

    @Override
    protected MultiElementContainer<MacroScreen> createTopbar() {
        return (MultiElementContainer) new ServiceListTopbar(this, this.width / 12, 25, this.width * 5 / 6, 14, this.textRenderer);
    }

    @Override
    public void onClose() {
        Core.getInstance().services.save();
        super.onClose();
    }

}
    