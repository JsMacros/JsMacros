package xyz.wagyourtail.jsmacros.client.gui.screens;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.gui.containers.RunningContextContainer;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;
import xyz.wagyourtail.jsmacros.core.service.EventService;
import xyz.wagyourtail.wagyourgui.BaseScreen;
import xyz.wagyourtail.wagyourgui.elements.AnnotatedCheckBox;
import xyz.wagyourtail.wagyourgui.elements.Button;
import xyz.wagyourtail.wagyourgui.elements.Scrollbar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CancelScreen extends BaseScreen {
    private int topScroll;
    private Scrollbar s;
    private AnnotatedCheckBox services;
    private final List<RunningContextContainer> running = new ArrayList<>();

    public CancelScreen(GuiScreen parent) {
        super(new ChatComponentText("Cancel"), parent);
    }

    @Override
    public void init() {
        super.init();
        // force gc all currently closed contexts
        System.gc();
        topScroll = 10;
        running.clear();
        s = this.addButton(new Scrollbar(width - 12, 5, 8, height-10, 0xFFFFFFFF, 0xFF000000, 0x7FFFFFFF, 1, this::onScrollbar));
        
        this.addButton(new Button(0, this.height - 12, this.width / 12, 12, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentTranslation("jsmacros.back"), (btn) -> this.onClose()));
        services = this.addButton(new AnnotatedCheckBox(this.width / 12 + 5, this.height - 12, 200, 12, textRenderer, 0, 0xFF000000, 0xFFFFFFFF, 0xFFFFFF, new ChatComponentTranslation("jsmacros.showservices"), false, null));
    }

    public void addContainer(BaseScriptContext<?> t) {
        if (t == null) {
            return;
        }
        if (!services.value && t.getTriggeringEvent() instanceof EventService) {
            return;
        }
        if (!t.isContextClosed()) {
            running.sort(new RTCSort());
            s.setScrollPages(running.size() * 15 / (double) (height - 20));
        } else {
            JsMacros.LOGGER.warn("Closed context {} was still in list", t.getMainThread().getName());
        }
            running.add(new RunningContextContainer(10, topScroll + running.size() * 15, width - 26, 13, textRenderer, this, t));
    }

    public void removeContainer(RunningContextContainer t) {
        for (GuiButton b : t.getButtons()) {
            buttons.remove(b);
        }
        running.remove(t);
        s.setScrollPages(running.size() * 15 / (double)(height - 20));
        updatePos();
    }

    private void onScrollbar(double page) {
        topScroll = 10 - (int) (page * (height - 20));
        updatePos();
    }

    public void updatePos() {
        for (int i = 0; i < running.size(); ++i) {
            if (topScroll + i * 15 < 10 || topScroll + i * 15 > height - 10) running.get(i).setVisible(false);
            else {
                running.get(i).setVisible(true);
                running.get(i).setPos(10, topScroll + i * 15, width - 26, 13);
            }
        }
    }
    
    @Override
    public boolean mouseScrolled(int mouseX, int mouseY, int amount) {
        s.mouseDragged(mouseX, mouseY, 0, 0, -amount * 2);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    
    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground(0);
        List<BaseScriptContext<?>> tl = new ArrayList<>(Core.getInstance().getContexts());
        
        for (RunningContextContainer r : ImmutableList.copyOf(this.running)) {
            tl.remove(r.t);
            if (!services.value && r.service) removeContainer(r);
            r.render(mouseX, mouseY, delta);
        }
        
        for (BaseScriptContext<?> t : tl) {
            addContainer(t);
        }
        
        for (GuiButton b : ImmutableList.copyOf(this.buttons)) {
            b.render(client, mouseX, mouseY);
        }
    }

    @Override
    public void removed() {
        assert client != null;
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void onClose() {
        this.openParent();
    }

    public static class RTCSort implements Comparator<RunningContextContainer> {
        @Override
        public int compare(RunningContextContainer arg0, RunningContextContainer arg1) {
            try {
                return arg0.t.getMainThread().getName().compareTo(arg1.t.getMainThread().getName());
            } catch(NullPointerException e) {
                return 0;
            }
        }

    }
}
