package xyz.wagyourtail.jsmacros.gui2.containers;

import java.util.function.Consumer;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import xyz.wagyourtail.jsmacros.gui2.elements.Button;
import xyz.wagyourtail.jsmacros.gui2.elements.MultiElementContainer;

public class ProfileContainer extends MultiElementContainer {
    private Button selectButton;
    private Button defaultButton;
    public String pName;
    private Consumer<ProfileContainer> setSelected;
    private Consumer<ProfileContainer> setDefault;
    private boolean defaultProfile;

    public ProfileContainer(int x, int y, int width, int height, TextRenderer textRenderer, String pName, String defaultProfile, Consumer<AbstractButtonWidget> addButton, Consumer<ProfileContainer> setSelected, Consumer<ProfileContainer> setDefault) {
        super(x, y, width, height, textRenderer, addButton);
        this.pName = pName;
        this.setSelected = setSelected;
        this.setDefault = setDefault;
        this.defaultProfile = defaultProfile.equals(pName);
        this.init();
    }

    public void init() {
        selectButton = (Button) this.addButton(new Button(x + 1, y + 1, width * 3 / 4 - 2, height - 2, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText(pName), (btn) -> {
            if (setSelected != null) setSelected.accept(this);
        }));

        defaultButton = (Button) this.addButton(new Button(x + width * 3 / 4 - 1, y + 1, width / 4, height - 2, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText(defaultProfile ? "X" : ""), (btn) -> {
            if (setDefault != null) setDefault.accept(this);
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

    public void setPos(int x, int y, int width, int height) {
        super.setPos(x, y, width, height);
        selectButton.setPos(x + 1, y + 1, width * 3 / 4 - 2, height - 2);
        defaultButton.setPos(x + width * 3 / 4 - 1, y + 1, width / 4, height - 2);
    }

    @Override
    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            // border
            fill(matricies, x, y, x + width, y + 1, 0xFFFFFFFF);
            fill(matricies, x, y + height - 1, x + width, y + height, 0xFFFFFFFF);
            fill(matricies, x, y + 1, x + 1, y + height - 1, 0xFFFFFFFF);
            fill(matricies, x + width - 1, y + 1, x + width, y + height - 1, 0xFFFFFFFF);
        }
    }

}
