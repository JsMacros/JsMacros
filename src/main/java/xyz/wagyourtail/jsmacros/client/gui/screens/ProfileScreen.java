package xyz.wagyourtail.jsmacros.client.gui.screens;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.config.ClientConfigV2;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;
import xyz.wagyourtail.jsmacros.client.gui.elements.Scrollbar;
import xyz.wagyourtail.jsmacros.client.gui.containers.CheckBoxContainer;
import xyz.wagyourtail.jsmacros.client.gui.containers.ProfileContainer;
import xyz.wagyourtail.jsmacros.client.gui.overlays.ConfirmOverlay;
import xyz.wagyourtail.jsmacros.client.gui.overlays.TextPrompt;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.CoreConfigV2;

import java.util.ArrayList;
import java.util.List;

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
        assert client != null;
        profText = new TranslatableText("jsmacros.profile");
        defText = new TranslatableText("jsmacros.default");
        
        profiles.clear();
        topScroll = 35;
    
        client.keyboard.setRepeatEvents(true);
        this.addButton(new Button(0, 0, this.width / 6 - 1, 20, textRenderer,0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.keys"), (btn) -> this.openParent()));

        this.addButton(new Button(this.width / 6 + 1, 0, this.width / 6 - 1, 20, textRenderer, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.events"), (btn) -> client.openScreen(new EventMacrosScreen(parent))));

        Button profile = this.addButton(new Button(this.width * 5 / 6 + 1, 0, this.width / 6 - 1, 20, textRenderer,0x4FFFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.profile"), null));
        profile.active = false;

        this.addButton(new Button(0, this.height - 20, this.width / 6, 20, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.addprofile"), (btn) -> this.openOverlay(new TextPrompt(width / 2 - 100, height / 2 - 50, 200, 100, textRenderer, new TranslatableText("jsmacros.profilename"), "", this, (str) -> {
            addProfile(str);
            if (!Core.instance.config.getOptions(CoreConfigV2.class).profiles.containsKey(str)) Core.instance.config.getOptions(CoreConfigV2.class).profiles.put(str, new ArrayList<>());
            Core.instance.config.saveConfig();
        }))));

        this.addButton(new Button(this.width / 6, this.height - 20, this.width / 6, 20, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.renameprofile"), (btn) -> {
            if (!selected.pName.equals(Core.instance.config.getOptions(CoreConfigV2.class).defaultProfile)) this.openOverlay(new TextPrompt(width / 2 - 100, height / 2 - 50, 200, 100, textRenderer, new TranslatableText("jsmacros.profilename"), selected.pName, this, (str) -> {
                Core.instance.config.getOptions(CoreConfigV2.class).profiles.remove(selected.pName);
                Core.instance.profile.renameCurrentProfile(str);
                Core.instance.profile.saveProfile();
                selected.setProfName(str);
            }));
        }));

        this.addButton(new Button(this.width / 3, this.height - 20, this.width / 6, 20, textRenderer,0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.deleteprofile"), (btn) -> {
            if (!selected.pName.equals(Core.instance.config.getOptions(CoreConfigV2.class).defaultProfile)) this.openOverlay(new ConfirmOverlay(width / 2 - 100, height / 2 - 50, 200, 100, textRenderer, new TranslatableText("jsmacros.deleteprofile").append(new LiteralText(" \"" + Core.instance.profile.getCurrentProfileName()+"\"")), this, (cf) -> {
                removeProfile(selected);
                Core.instance.profile.loadOrCreateProfile(Core.instance.config.getOptions(CoreConfigV2.class).defaultProfile);
                for (ProfileContainer p : profiles) {
                    if (p.pName.equals(Core.instance.config.getOptions(CoreConfigV2.class).defaultProfile)) {
                        setSelected(p);
                        break;
                    }
                }
            }));
        }));

        profileScroll = this.addButton(new Scrollbar(this.width / 2 - 8, 33, 8, this.height - 53, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));
        disableInGui = new CheckBoxContainer(this.width / 2 + 10, 50, this.width / 2 - 20, 12, this.textRenderer, JsMacros.core.config.getOptions(ClientConfigV2.class).disableKeyWhenScreenOpen, new TranslatableText("jsmacros.disablewithscreen"), this, (state) -> JsMacros.core.config.getOptions(ClientConfigV2.class).disableKeyWhenScreenOpen = state);
        
        for (String k : Core.instance.config.getOptions(CoreConfigV2.class).profiles.keySet()) {
            addProfile(k);
        }
    }

    public void addProfile(String pName) {
        ProfileContainer pc = new ProfileContainer(20, topScroll + profiles.size() * 22, this.width / 2 - 40, 20, this.textRenderer, pName, Core.instance.config.getOptions(CoreConfigV2.class).defaultProfile, this);
        profiles.add(pc);
        profileScroll.setScrollPages((topScroll + profiles.size() * 22) / (double) Math.max(1, this.height - 53));
        if (pName.equals(Core.instance.profile.getCurrentProfileName())) setSelected(pc);
    }

    public void removeProfile(ProfileContainer prof) {
        Core.instance.config.getOptions(CoreConfigV2.class).profiles.remove(prof.pName);
        for (AbstractButtonWidget b : prof.getButtons()) {
            this.buttons.remove(b);
            this.children.remove(b);
        }
        profiles.remove(prof);
        profileScroll.setScrollPages((topScroll + profiles.size() * 22) / (double) Math.max(1, this.height - 53));
    }

    public void setSelected(ProfileContainer profile) {
        this.selected = profile;
        Core.instance.profile.saveProfile();
        Core.instance.profile.loadOrCreateProfile(profile.pName);
        for (ProfileContainer p : profiles) {
            p.setSelected(profile);
        }
    }

    public void setDefault(ProfileContainer profile) {
        Core.instance.config.getOptions(CoreConfigV2.class).defaultProfile = profile.pName;
        Core.instance.config.saveConfig();
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

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (matrices == null) return;
        this.renderBackground(matrices, 0);
        
        for (ProfileContainer p : ImmutableList.copyOf(this.profiles)) {
            p.render(matrices, mouseX, mouseY, delta);
        }

        for (AbstractButtonWidget b : ImmutableList.copyOf(this.buttons)) {
            b.render(matrices, mouseX, mouseY, delta);
        }

        // plist topbar
        int w = this.width / 2 - 40;
        drawCenteredText(matrices, textRenderer, profText, w * 3 / 8 + 20, 24, 0xFFFFFF);
        if (textRenderer.getWidth(defText) > w / 4) {
            textRenderer.drawTrimmed(defText, w * 3 / 4 + 10, 24, w / 4, 0xFFFFFF);
        } else {
            drawCenteredText(matrices, this.textRenderer, defText, w * 7 / 8 + 20, 24, 0xFFFFFF);
        }
        fill(matrices, 20, 33, this.width / 2 - 20, 34, 0xFFFFFFFF);

        // pname
        drawCenteredString(matrices, this.textRenderer, Core.instance.profile.getCurrentProfileName(), this.width * 7 / 12, 5, 0x7F7F7F);

        // middle bar
        fill(matrices, this.width / 2, 22, this.width / 2 + 1, this.height - 1, 0xFFFFFFFF);

        disableInGui.render(matrices, mouseX, mouseY, delta);
        
        // top stuff
        fill(matrices, this.width * 5 / 6 - 1, 0, this.width * 5 / 6 + 1, 20, 0xFFFFFFFF);
        fill(matrices, this.width / 6 - 1, 0, this.width / 6 + 1, 20, 0xFFFFFFFF);
        fill(matrices, this.width / 6 * 2, 0, this.width / 6 * 2 + 2, 20, 0xFFFFFFFF);
        fill(matrices, 0, 20, width, 22, 0xFFFFFFFF);

        super.render(matrices, mouseX, mouseY, delta);
    }

    public void removed() {
        assert client != null;
        client.keyboard.setRepeatEvents(false);
    }

    public void onClose() {
        Core.instance.config.saveConfig();
        super.onClose();
    }
}
