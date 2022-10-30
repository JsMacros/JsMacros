package xyz.wagyourtail.jsmacros.client.gui.settings.settingfields;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import xyz.wagyourtail.jsmacros.client.gui.overlays.FileChooser;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;
import xyz.wagyourtail.jsmacros.client.gui.settings.settingcontainer.AbstractSettingContainer;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.wagyourgui.BaseScreen;
import xyz.wagyourtail.wagyourgui.elements.Button;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class FileField extends AbstractSettingField<String> {

    public FileField(int x, int y, int width, TextRenderer textRenderer, AbstractSettingContainer parent, SettingsOverlay.SettingField<String> field) {
        super(x, y, width, textRenderer.fontHeight + 2, textRenderer, parent, field);
    }

    public static File getTopLevel(SettingsOverlay.SettingField<?> setting) {
        for (String option : setting.option.type().options()) {
            if (option.startsWith("topLevel=")) {
                switch (option.replace("topLevel=", "")) {
                    case "MC":
                        return MinecraftClient.getInstance().runDirectory;
                    case "CONFIG":
                        return Core.getInstance().config.configFolder;
                    case "MACRO":
                    default:
                        return Core.getInstance().config.macroFolder;
                }
            }
        }
        //default
        return Core.getInstance().config.macroFolder;
    }

    public static String relativize(SettingsOverlay.SettingField<?> setting, File file) {
        File top = getTopLevel(setting).getAbsoluteFile();
        return top.toPath().relativize(file.getAbsoluteFile().toPath()).toString();
    }

    @Override
    public void init() {
        super.init();
        try {
            this.addButton(new Button(x + width / 2, y, width / 2, height, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText(setting.get()), (btn) -> {
                try {
                    File current = new File(getTopLevel(setting), setting.get());
                    FileChooser fc = new FileChooser(parent.x, parent.y, parent.width, parent.height, textRenderer, current.getParentFile(), current, getFirstOverlayParent(), (file) -> {
                        try {
                            setting.set("./" + relativize(setting, file).replaceAll("\\\\", "/"));
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }, file -> {});
                    fc.root = getTopLevel(setting);
                    parent.openOverlay(fc);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setPos(int x, int y, int width, int height) {
        super.setPos(x, y, width, height);
        for (ButtonWidget btn : buttons) {
            btn.y = y;
        }
    }


    @Override
    public void render(int mouseX, int mouseY, float delta) {
        textRenderer.draw(BaseScreen.trimmed(textRenderer, settingName.asFormattedString(), width / 2), x, y + 1, 0xFFFFFF);
    }

}
