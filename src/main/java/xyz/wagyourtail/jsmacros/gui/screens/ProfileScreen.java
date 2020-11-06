package xyz.wagyourtail.jsmacros.gui.screens;

import xyz.wagyourtail.jsmacros.JsMacros;
import xyz.wagyourtail.jsmacros.gui.BaseScreen;
import xyz.wagyourtail.jsmacros.gui.containers.CheckBoxContainer;
import xyz.wagyourtail.jsmacros.gui.containers.ConfirmOverlay;
import xyz.wagyourtail.jsmacros.gui.containers.ProfileContainer;
import xyz.wagyourtail.jsmacros.gui.containers.TextPrompt;
import xyz.wagyourtail.jsmacros.gui.elements.Button;
import xyz.wagyourtail.jsmacros.gui.elements.Scrollbar;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.jsmacros.gui.screens.macros.EventMacrosScreen;

public class ProfileScreen extends BaseScreen {
    private int topScroll;
    private final List<ProfileContainer> profiles = new ArrayList<>();
    private ProfileContainer selected;
    private Scrollbar profileScroll;
    private CheckBoxContainer disableInGui;
    private Text profText;
    private Text defText;

    public ProfileScreen(Screen parent) {
        super(new TranslatableText("jsmacros.title"), parent);
    }

    protected void init() {
        super.init();
        profText = new TranslatableText("jsmacros.profile");
        defText = new TranslatableText("jsmacros.default");
        
        profiles.clear();
        topScroll = 35;

        client.keyboard.setRepeatEvents(true);
        this.addButton(new Button(0, 0, this.width / 6 - 1, 20, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.keys"), (btn) -> {
            this.openParent();
        }));

        this.addButton(new Button(this.width / 6 + 1, 0, this.width / 6 - 1, 20, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.events"), (btn) -> {
            client.openScreen(new EventMacrosScreen(parent));
        }));

        Button profile = this.addButton(new Button(this.width * 5 / 6 + 1, 0, this.width / 6 - 1, 20, 0x4FFFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.profile"), null));
        profile.active = false;

        this.addButton(new Button(0, this.height - 20, this.width / 6, 20, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.addprofile"), (btn) -> {
            this.openOverlay(new TextPrompt(width / 2 - 100, height / 2 - 50, 200, 100, textRenderer, new TranslatableText("jsmacros.profilename"), "", this::addButton, this::removeButton, this::closeOverlay, this::setFocused, (str) -> {
                addProfile(str);
                if (!JsMacros.config.options.profiles.containsKey(str)) JsMacros.config.options.profiles.put(str, new ArrayList<>());
                JsMacros.config.saveConfig();
            }));
        }));

        this.addButton(new Button(this.width / 6, this.height - 20, this.width / 6, 20, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.renameprofile"), (btn) -> {
            if (!selected.pName.equals(JsMacros.config.options.defaultProfile)) this.openOverlay(new TextPrompt(width / 2 - 100, height / 2 - 50, 200, 100, textRenderer, new TranslatableText("jsmacros.profilename"), selected.pName, this::addButton, this::removeButton, this::closeOverlay, this::setFocused, (str) -> {
                JsMacros.config.options.profiles.remove(selected.pName);
                JsMacros.profile.profileName = str;
                JsMacros.profile.saveProfile();
                selected.setProfName(str);
            }));
        }));

        this.addButton(new Button(this.width / 3, this.height - 20, this.width / 6, 20, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.deleteprofile"), (btn) -> {
            if (!selected.pName.equals(JsMacros.config.options.defaultProfile)) this.openOverlay(new ConfirmOverlay(width / 2 - 100, height / 2 - 50, 200, 100, textRenderer, new TranslatableText("jsmacros.deleteprofile").append(new LiteralText(" \"" + JsMacros.profile.profileName+"\"")), this::addButton, this::removeButton, this::closeOverlay, (cf) -> {
                removeProfile(selected);
                JsMacros.profile.loadOrCreateProfile(JsMacros.config.options.defaultProfile);
                for (ProfileContainer p : profiles) {
                    if (p.pName.equals(JsMacros.config.options.defaultProfile)) {
                        setSelected(p);
                        break;
                    }
                }
            }));
        }));

        profileScroll = this.addButton(new Scrollbar(this.width / 2 - 8, 33, 8, this.height - 53, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));
        disableInGui = new CheckBoxContainer(this.width / 2 + 10, 50, this.width / 2 - 20, 12, this.textRenderer, JsMacros.config.options.disableKeyWhenScreenOpen, new TranslatableText("jsmacros.disablewithscreen"), this::addButton, (state) -> {
            JsMacros.config.options.disableKeyWhenScreenOpen = state;
        });
        
        for (String k : JsMacros.config.options.profiles.keySet()) {
            addProfile(k);
        }
    }

    public void addProfile(String pName) {
        ProfileContainer pc = new ProfileContainer(20, topScroll + profiles.size() * 22, this.width / 2 - 40, 20, this.textRenderer, pName, JsMacros.config.options.defaultProfile, this::addButton, this::setSelected, this::setDefault);
        profiles.add(pc);
        profileScroll.setScrollPages((topScroll + profiles.size() * 22) / (double) Math.max(1, this.height - 53));
        if (pName.equals(JsMacros.profile.profileName)) setSelected(pc);
    }

    public void removeProfile(ProfileContainer prof) {
        JsMacros.config.options.profiles.remove(prof.pName);
        for (AbstractButtonWidget b : prof.getButtons()) {
            this.buttons.remove(b);
            this.children.remove(b);
        }
        profiles.remove(prof);
        profileScroll.setScrollPages((topScroll + profiles.size() * 22) / (double) Math.max(1, this.height - 53));
    }

    public void setSelected(ProfileContainer profile) {
        this.selected = profile;
        JsMacros.profile.saveProfile();
        JsMacros.profile.loadOrCreateProfile(profile.pName);
        for (ProfileContainer p : profiles) {
            p.setSelected(profile);
        }
    }

    public void setDefault(ProfileContainer profile) {
        JsMacros.config.options.defaultProfile = profile.pName;
        JsMacros.config.saveConfig();
        for (ProfileContainer p : profiles) {
            p.setDefault(profile);
        }
    }

    public void updateBtnPos() {
        for (int i = 0; i < profiles.size(); ++i) {
            if (topScroll + i * 22 < 33 || topScroll + i * 22 > height - 42) profiles.get(i).setVisible(false);
            else {
                profiles.get(i).setVisible(true);
                profiles.get(i).setPos(20, topScroll + i * 22, this.width / 2 - 40, 20);
            }
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (overlay == null) {
            profileScroll.mouseDragged(mouseX, mouseY, 0, 0, -amount * 2);
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    private void onScrollbar(double page) {
        topScroll = 35 - (int) (page * (height - 60));
        updateBtnPos();
    }

    public void removeButton(AbstractButtonWidget btn) {
        buttons.remove(btn);
        children.remove(btn);
    }

    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        if (matricies == null) return;
        this.renderBackground(matricies, 0);
        
        for (ProfileContainer p : ImmutableList.copyOf(this.profiles)) {
            p.render(matricies, mouseX, mouseY, delta);
        }

        for (AbstractButtonWidget b : ImmutableList.copyOf(this.buttons)) {
            b.render(matricies, mouseX, mouseY, delta);
        }

        // plist topbar
        int w = this.width / 2 - 40;
        drawCenteredText(matricies, textRenderer, profText, w * 3 / 8 + 20, 24, 0xFFFFFF);
        drawCenteredText(matricies, this.textRenderer, (Text) textRenderer.trimToWidth(defText, w / 4), w * 7 / 8 + 20, 24, 0xFFFFFF);
        fill(matricies, 20, 33, this.width / 2 - 20, 34, 0xFFFFFFFF);

        // pname
        drawCenteredString(matricies, this.textRenderer, JsMacros.profile.profileName, this.width * 7 / 12, 5, 0x7F7F7F);

        // middle bar
        fill(matricies, this.width / 2, 22, this.width / 2 + 1, this.height - 1, 0xFFFFFFFF);

        disableInGui.render(matricies, mouseX, mouseY, delta);
        
        // top stuff
        fill(matricies, this.width * 5 / 6 - 1, 0, this.width * 5 / 6 + 1, 20, 0xFFFFFFFF);
        fill(matricies, this.width / 6 - 1, 0, this.width / 6 + 1, 20, 0xFFFFFFFF);
        fill(matricies, this.width / 6 * 2, 0, this.width / 6 * 2 + 2, 20, 0xFFFFFFFF);
        fill(matricies, 0, 20, width, 22, 0xFFFFFFFF);

        super.render(matricies, mouseX, mouseY, delta);
    }

    public void removed() {
        client.keyboard.setRepeatEvents(false);
    }

    public void onClose() {
        JsMacros.config.saveConfig();
        super.onClose();
    }
}
