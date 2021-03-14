package xyz.wagyourtail.jsmacros.client.gui.containers;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;
import xyz.wagyourtail.jsmacros.client.gui.screens.ProfileScreen;

public class ProfileContainer extends MultiElementContainer<ProfileScreen> {
    private Button selectButton;
    private Button defaultButton;
    public String pName;
    private boolean defaultProfile;

    public ProfileContainer(int x, int y, int width, int height, TextRenderer textRenderer, String pName, String defaultProfile, ProfileScreen parent) {
        super(x, y, width, height, textRenderer, parent);
        this.pName = pName;
        this.defaultProfile = defaultProfile.equals(pName);
        this.init();
    }

    @Override
    public void init() {
        selectButton = this.addButton(new Button(x + 1, y + 1, width * 3 / 4 - 2, height - 2, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText(pName), (btn) -> {
            parent.setSelected(this);
        }));

        defaultButton = this.addButton(new Button(x + width * 3 / 4 - 1, y + 1, width / 4, height - 2, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText(defaultProfile ? "X" : ""), (btn) -> {
            parent.setDefault(this);
        }));
    }

    public void setProfName(String pName) {
        this.selectButton.setMessage(new LiteralText(pName));
    }

    public void setDefault(ProfileContainer prof) {
        this.defaultProfile = this.equals(prof);
        this.defaultButton.setMessage(new LiteralText(defaultProfile ? "X" : ""));
    }

    public void setSelected(ProfileContainer prof) {
        if (this.equals(prof)) {
            this.selectButton.active = false;
            this.selectButton.setColor(0x40FFFFFF);
        } else {
            this.selectButton.active = true;
            this.selectButton.setColor(0);
        }
    }

    @Override
    public void setPos(int x, int y, int width, int height) {
        super.setPos(x, y, width, height);
        selectButton.setPos(x + 1, y + 1, width * 3 / 4 - 2, height - 2);
        defaultButton.setPos(x + width * 3 / 4 - 1, y + 1, width / 4, height - 2);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            // border
            fill(matrices, x, y, x + width, y + 1, 0xFFFFFFFF);
            fill(matrices, x, y + height - 1, x + width, y + height, 0xFFFFFFFF);
            fill(matrices, x, y + 1, x + 1, y + height - 1, 0xFFFFFFFF);
            fill(matrices, x + width - 1, y + 1, x + width, y + height - 1, 0xFFFFFFFF);
        }
    }

}
