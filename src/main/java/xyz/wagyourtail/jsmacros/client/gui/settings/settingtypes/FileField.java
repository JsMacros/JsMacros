package xyz.wagyourtail.jsmacros.client.gui.settings.settingtypes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;
import xyz.wagyourtail.jsmacros.client.gui.overlays.FileChooser;
import xyz.wagyourtail.jsmacros.client.gui.settings.AbstractSettingGroupContainer;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;
import xyz.wagyourtail.jsmacros.core.Core;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class FileField extends AbstractSettingType<String> {
    
    public FileField(int x, int y, int width, TextRenderer textRenderer, AbstractSettingGroupContainer parent, SettingsOverlay.SettingField<String> field) {
        super(x, y, width, textRenderer.fontHeight + 2, textRenderer, parent, field);
    }
    
    private File getTopLevel() {
        for (String option : setting.option.type().options()) {
            if (option.startsWith("topLevel=")) {
                switch (option.replace("topLevel=", "")) {
                    case "MC":
                        return MinecraftClient.getInstance().runDirectory;
                    case "CONFIG":
                        return Core.instance.config.configFolder;
                    case "MACRO":
                    default:
                        return Core.instance.config.macroFolder;
                }
            }
        }
        //default
        return Core.instance.config.macroFolder;
    }
    
    private File getSelectedFile() throws InvocationTargetException, IllegalAccessException {
        return new File(getTopLevel(), setting.get());
    }
    
    @Override
    public void init() {
        super.init();
        try {
            this.addButton(new Button(x + width / 2, y, width / 2, height, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText(setting.get()), (btn) -> {
                SettingsOverlay parent = (SettingsOverlay) getFirstOverlayParent();
                try {
                    parent.openOverlay(new FileChooser(parent.x, parent.y, parent.width, parent.height, textRenderer, getTopLevel(), getSelectedFile(), parent, (file) -> {
                        try {
                            setting.set("." + file.getAbsolutePath().substring(getTopLevel().getAbsolutePath().length()).replaceAll("\\\\", "/"));
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }, file -> {}));
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        textRenderer.drawTrimmed(settingName, x, y + 1, width / 2, 0xFFFFFF);
    }
    
}
