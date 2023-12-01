package xyz.wagyourtail.jsmacros.client.gui.screens;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.client.config.ClientConfigV2;
import xyz.wagyourtail.jsmacros.client.gui.containers.MacroContainer;
import xyz.wagyourtail.jsmacros.client.gui.containers.MacroListTopbar;
import xyz.wagyourtail.jsmacros.client.gui.overlays.AboutOverlay;
import xyz.wagyourtail.jsmacros.client.gui.overlays.EventChooser;
import xyz.wagyourtail.jsmacros.client.gui.overlays.FileChooser;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.wagyourgui.BaseScreen;
import xyz.wagyourtail.wagyourgui.containers.MultiElementContainer;
import xyz.wagyourtail.wagyourgui.elements.Button;
import xyz.wagyourtail.wagyourgui.elements.Scrollbar;
import xyz.wagyourtail.wagyourgui.overlays.ConfirmOverlay;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static xyz.wagyourtail.jsmacros.client.backport.TextBackport.literal;
import static xyz.wagyourtail.jsmacros.client.backport.TextBackport.translatable;

public class MacroScreen extends BaseScreen {
    protected MultiElementContainer<MacroScreen> topbar;
    protected Scrollbar macroScroll;
    protected List<MultiElementContainer<MacroScreen>> macros = new ArrayList<>();
    protected int topScroll;
    protected Button keyScreen;
    protected Button eventScreen;
    protected Button serviceScreen;
    protected Button runningBtn;
    protected Button aboutBtn;

    public MacroScreen(Screen parent) {
        super(translatable("jsmacros.title"), parent);
    }

    @Override
    protected void init() {
        super.init();
        macros.clear();
        keyScreen = this.addDrawableChild(new Button(0, 0, this.width / 6 - 1, 20, textRenderer, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, translatable("jsmacros.keys"), btn -> {
            assert client != null;
            if (client.currentScreen.getClass() != KeyMacrosScreen.class) {
                client.setScreen(new KeyMacrosScreen(this));
            }
        }));

        eventScreen = this.addDrawableChild(new Button(this.width / 6 + 1, 0, this.width / 6 - 1, 20, textRenderer, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, translatable("jsmacros.events"), btn -> {
            assert client != null;
            if (client.currentScreen.getClass() != EventMacrosScreen.class) {
                client.setScreen(new EventMacrosScreen(this));
            }
        }));

        serviceScreen = this.addDrawableChild(new Button(2 * this.width / 6 + 2, 0, this.width / 6 - 1, 20, textRenderer, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, translatable("jsmacros.services"), btn -> {
            assert client != null;
            if (client.currentScreen.getClass() != ServiceScreen.class) {
                client.setScreen(new ServiceScreen(this));
            }
        }));

        this.addDrawableChild(new Button(this.width * 5 / 6 + 1, 0, this.width / 6 - 1, 20, textRenderer, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, translatable("jsmacros.settings"), (btn) -> {
            openOverlay(new SettingsOverlay(this.width / 4, this.height / 4, this.width / 2, this.height / 2, textRenderer, this));
        }));

        topbar = createTopbar();

        topScroll = 40;
        macroScroll = this.addDrawableChild(new Scrollbar(this.width * 23 / 24 - 4, 50, 8, this.height - 75, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));

        runningBtn = this.addDrawableChild(new Button(0, this.height - 12, this.width / 12, 12, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, translatable("jsmacros.running"), (btn) -> {
            assert client != null;
            client.setScreen(new CancelScreen(this));
        }));

        aboutBtn = this.addDrawableChild(new Button(this.width * 11 / 12, this.height - 12, this.width / 12, 12, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, translatable("jsmacros.about"), (btn) -> this.openOverlay(new AboutOverlay(this.width / 4, this.height / 4, this.width / 2, this.height / 2, textRenderer, this))));
    }

    protected MultiElementContainer<MacroScreen> createTopbar() {
        return new MacroListTopbar(this, this.width / 12, 25, this.width * 5 / 6, 14, this.textRenderer, ScriptTrigger.TriggerType.KEY_RISING);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (overlay == null) {
            macroScroll.mouseDragged(mouseX, mouseY, 0, 0, -amount * 2);
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    public void addMacro(ScriptTrigger macro) {
        macros.add(new MacroContainer(this.width / 12, topScroll + macros.size() * 16, this.width * 5 / 6, 14, this.textRenderer, macro, this));
        macroScroll.setScrollPages(((macros.size() + 1) * 16) / (double) Math.max(1, this.height - 40));
    }

    public void setFile(MultiElementContainer<MacroScreen> macro) {
        File f = new File(Core.getInstance().config.macroFolder, ((MacroContainer) macro).getRawMacro().scriptFile);
        File dir = Core.getInstance().config.macroFolder;
        if (!f.equals(Core.getInstance().config.macroFolder)) {
            dir = f.getParentFile();
        }
        openOverlay(new FileChooser(width / 4, height / 4, width / 2, height / 2, this.textRenderer, dir, f, this, ((MacroContainer) macro)::setFile, this::editFile));
    }

    public void setEvent(MacroContainer macro) {
        openOverlay(new EventChooser(width / 4, height / 4, width / 2, height / 2, this.textRenderer, macro.getRawMacro().event, this, macro::setEventType));
    }

    public void runFile() {
        openOverlay(new FileChooser(width / 4, height / 4, width / 2, height / 2, this.textRenderer, Core.getInstance().config.macroFolder, null, this, (file) -> {
            Core.getInstance().exec(new ScriptTrigger(ScriptTrigger.TriggerType.EVENT, "", file, true, false), null);
        }, this::editFile));
    }

    public void confirmRemoveMacro(MultiElementContainer<MacroScreen> macro) {
        openOverlay(new ConfirmOverlay(width / 2 - 100, height / 2 - 50, 200, 100, this.textRenderer, translatable("jsmacros.confirmdeletemacro"), this, (conf) -> removeMacro(macro)));
    }

    public void removeMacro(MultiElementContainer<MacroScreen> macro) {
        Core.getInstance().eventRegistry.removeScriptTrigger(((MacroContainer) macro).getRawMacro());
        for (ClickableWidget b : macro.getButtons()) {
            remove(b);
        }
        macros.remove(macro);
        setMacroPos();
    }

    private void onScrollbar(double page) {
        topScroll = 40 - (int) (page * (height - 40));
        setMacroPos();
    }

    public void setMacroPos() {
        int i = 0;
        for (MultiElementContainer<MacroScreen> m : macros) {
            m.setVisible(topScroll + i * 16 >= 40);
            m.setPos(this.width / 12, topScroll + (i++) * 16, this.width * 5 / 6, 14);
        }
    }

    public void editFile(File file) {
        if (file != null && file.exists() && file.isFile()) {
            if (Core.getInstance().config.getOptions(ClientConfigV2.class).externalEditor) {
                String[] args = Core.getInstance().config.getOptions(ClientConfigV2.class).externalEditorCommand.split("\\s+");
                for (int i = 0; i < args.length; ++i) {
                    args[i] = args[i]
                            .replace("%File", file.getAbsolutePath())
                            .replace("%MacroFolder", Core.getInstance().config.macroFolder.getAbsolutePath())
                            .replace("%Folder", file.getParentFile().getAbsolutePath());
                    File f = file;
                    if (args[i].startsWith("%Parent")) {
                        for (int j = Integer.getInteger(args[i].replace("%Parent", "")); j > 0; --j) {
                            f = f.getParentFile();
                        }
                        args[i] = f.getAbsolutePath();
                    }
                }
                try {
                    new ProcessBuilder(args).start();
                    return;
                } catch (IOException ignored) {
                }
                if (System.getenv("PATHEXT") != null && !args[0].contains(".")) {
                    String arg0 = args[0];
                    for (String ext : System.getenv("PATHEXT").split(";")) {
                        args[0] = arg0 + ext;
                        try {
                            new ProcessBuilder(args).start();
                            return;
                        } catch (IOException ignored) {
                        }
                    }
                }
                System.out.println(System.getenv("PATH"));
                System.out.printf("Failed to run cmd '%s'", Core.getInstance().config.getOptions(ClientConfigV2.class).externalEditorCommand);
            }
            assert client != null;
            client.setScreen(new EditorScreen(this, file));
        }
    }

    @Override
    public void render(MatrixStack drawContext, int mouseX, int mouseY, float delta) {
        if (drawContext == null) {
            return;
        }
        this.renderBackground(drawContext);

        topbar.render(drawContext, mouseX, mouseY, delta);

        for (Element b : ImmutableList.copyOf(this.children())) {
            if (b instanceof Drawable) {
                ((Drawable) b).render(drawContext, mouseX, mouseY, delta);
            }
        }

        for (MultiElementContainer<MacroScreen> macro : ImmutableList.copyOf(this.macros)) {
            macro.render(drawContext, mouseX, mouseY, delta);
        }

        drawCenteredTextWithShadow(drawContext, this.textRenderer, literal(Core.getInstance().profile.getCurrentProfileName()).asOrderedText(), this.width * 8 / 12, 5, 0x7F7F7F);

        fill(drawContext, this.width * 5 / 6 - 1, 0, this.width * 5 / 6 + 1, 20, 0xFFFFFFFF);
        fill(drawContext, this.width / 6 - 1, 0, this.width / 6 + 1, 20, 0xFFFFFFFF);
        fill(drawContext, this.width / 6 * 2, 0, this.width / 6 * 2 + 2, 20, 0xFFFFFFFF);
        fill(drawContext, this.width / 6 * 3 + 1, 0, this.width / 6 * 3 + 3, 20, 0xFFFFFFFF);
        fill(drawContext, 0, 20, width, 22, 0xFFFFFFFF);

        super.render(drawContext, mouseX, mouseY, delta);
    }

    @Override
    public void updateSettings() {
        reload();
    }

    @Override
    public void removed() {
        Core.getInstance().profile.saveProfile();
        super.removed();
    }
}
