package xyz.wagyourtail.jsmacros.gui2;

import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.gui2.containers.TextPrompt;
import xyz.wagyourtail.jsmacros.gui2.elements.Button;
import xyz.wagyourtail.jsmacros.gui2.elements.OverlayContainer;
import xyz.wagyourtail.jsmacros.gui2.elements.Scrollbar;

import java.util.HashMap;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class ProfileScreen extends Screen {
    private Screen parent;
    private int topScroll;
    private HashMap<String, Button> profiles = new HashMap<>();
    private Scrollbar profileScroll;
    protected OverlayContainer overlay;
    
    public ProfileScreen(Screen parent) {
        super(new TranslatableText("jsmacros.title"));
        this.parent = parent;
    }
    
    protected void init() {
        super.init();
        profiles.clear();
        topScroll = 25;
        
        client.keyboard.enableRepeatEvents(true);
        this.addButton(new Button(0, 0, this.width / 6 - 1, 20, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("Keys"), (btn) -> {
            client.openScreen(parent);
        }));
        
        this.addButton(new Button(this.width / 6 + 1, 0, this.width / 6 - 1, 20, 0x00FFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("Events"), (btn) -> {
            client.openScreen(new EventMacrosScreen(parent));
        }));
        
        Button profile = this.addButton(new Button(this.width * 5 / 6 + 1, 0, this.width / 6 - 1, 20, 0x4FFFFFFF, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("Profile"), null));
        profile.active = false;
        
        this.addButton(new Button(0, this.height - 20, this.width / 6, 20, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("Add Profile"), (btn) -> {
            this.openOverlay(new TextPrompt(width / 2 - 100, height / 2 - 50, 200, 100, textRenderer, new LiteralText("Profile Name."), this::addButton, this::removeButton, this::closeOverlay, (str) -> {
                addProfile(str);
            }));
        }));
        
        profileScroll = this.addButton(new Scrollbar(this.width / 2 - 8, 23, this.width / 2, this.height - 43, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));
        
        for (String k : jsMacros.config.options.profiles.keySet()) {
            addProfile(k);
        }
        
        setSelected(jsMacros.profile.profileName);
    }
    
    public void addProfile(String pName) {
        profiles.put(pName, this.addButton(new Button(20, topScroll + profiles.size() * 22, this.width / 2 - 40, 20, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText(pName), (btn) -> {
            jsMacros.profile.saveProfile();
            jsMacros.profile.loadOrCreateProfile(pName);
            
            setSelected(jsMacros.profile.profileName);
        })));
        
        profileScroll.setScrollPages((topScroll + profiles.size() * 22) / (double) Math.max(1, this.height - 43));
    }
    
    public void setSelected(String pName) {
        for (Button b : profiles.values()) {
            b.setColor(0);
        }
        profiles.get(pName).setColor(0x7FFFFFFF);
    }
    
    public void updateBtnPos() {
        Button[] a = (Button[]) profiles.values().toArray();
        for (int i = 0; i < profiles.size(); ++i) {
            a[i].setPos(20, topScroll + i * 22, this.width / 2 - 40, 20);
            if (topScroll + i * 22 < 23 || topScroll + i * 22 > height - 42) a[i].visible = false;
            else a[i].visible = true;
        }
    }
    
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (overlay == null) {
            profileScroll.mouseDragged(mouseX, mouseY, 0, 0, -amount * 2);
        } else {
            if (overlay.scroll != null) overlay.scroll.mouseDragged(mouseX, mouseY, 0, 0, -amount * 2);
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    
    private void onScrollbar(double page) {
        topScroll = 25 - (int) (page * (height - 60));
        updateBtnPos();
    }
    
    public void openOverlay(OverlayContainer overlay) {
        for (AbstractButtonWidget b : buttons) {
            overlay.savedBtnStates.put(b, b.active);
            b.active = false;
        }
        this.overlay = overlay;
        overlay.init();
    }

    public void removeButton(AbstractButtonWidget btn) {
        buttons.remove(btn);
        children.remove(btn);
    }
    
    public void closeOverlay(OverlayContainer overlay) {
        if (overlay == null) return;
        for (AbstractButtonWidget b : overlay.getButtons()) {
            removeButton(b);
        }
        for (AbstractButtonWidget b : overlay.savedBtnStates.keySet()) {
            b.active = overlay.savedBtnStates.get(b);
        }
        if (this.overlay == overlay) this.overlay = null;
    }
    
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            if (overlay != null) {
                this.overlay.closeOverlay(this.overlay.getChildOverlay());
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        this.renderBackground(matricies, 0);
        
        for(AbstractButtonWidget b : buttons) {
            ((Button)b).render(matricies, mouseX, mouseY, delta);
        }
        
        drawCenteredString(matricies, this.textRenderer, jsMacros.profile.profileName, this.width * 7 / 12, 5, 0x7F7F7F);
        
//        drawCenteredString(matricies, this.textRenderer, "Not Yet Implemented", this.width / 2, 50, 0xFFFFFFFF);
        
        fill(matricies, this.width / 2, 22, this.width / 2 + 1, this.height - 1, 0xFFFFFFFF);
        
        fill(matricies, this.width * 5 / 6 - 1, 0, this.width * 5 / 6 + 1, 20, 0xFFFFFFFF);
        fill(matricies, this.width / 6 - 1, 0, this.width / 6 + 1, 20, 0xFFFFFFFF);
        fill(matricies, this.width / 6 * 2, 0, this.width / 6 * 2 + 2, 20, 0xFFFFFFFF);
        fill(matricies, 0, 20, width, 22, 0xFFFFFFFF);
        
        if (overlay != null) overlay.render(matricies, mouseX, mouseY, delta);
    }
    
    public void removed() {
        client.keyboard.enableRepeatEvents(false);
    }
    
    public boolean shouldCloseOnEsc() {
        return this.overlay == null;
    }
    
    public void onClose() {
        client.openScreen(parent);
    }
}
