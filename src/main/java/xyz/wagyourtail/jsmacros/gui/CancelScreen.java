package xyz.wagyourtail.jsmacros.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IScriptThreadWrapper;
import xyz.wagyourtail.jsmacros.gui.containers.RunningThreadContainer;
import xyz.wagyourtail.jsmacros.gui.elements.Button;
import xyz.wagyourtail.jsmacros.gui.elements.Scrollbar;
import xyz.wagyourtail.jsmacros.runscript.RunScript;
import xyz.wagyourtail.jsmacros.runscript.RunScript.ScriptThreadWrapper;

public class CancelScreen extends Screen {
    protected Screen parent;
    private int topScroll;
    private Scrollbar s;
    private List<RunningThreadContainer> running = new ArrayList<>();

    public CancelScreen(Screen parent) {
        super(new LiteralText("Cancel"));
        this.parent = parent;
    }

    public void init() {
        super.init();
        topScroll = 10;
        running.clear();
        s = this.addButton(new Scrollbar(width - 12, 5, 8, height-10, 0, 0xFF000000, 0xFFFFFFFF, 1, this::onScrollbar));
        
        this.addButton(new Button(0, this.height - 12, this.width / 12, 12, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.back"), (btn) -> {
            this.onClose();
        }));
    }

    public void addContainer(ScriptThreadWrapper t) {
        running.add(new RunningThreadContainer(10, topScroll + running.size() * 15, width - 26, 13, textRenderer, this::addButton, this::removeContainer, t));
        Collections.sort(running, new RTCSort());
        s.setScrollPages(running.size() * 15 / (double)(height - 20));
    }

    public void removeContainer(RunningThreadContainer t) {
        for (AbstractButtonWidget b : t.getButtons()) {
            buttons.remove(b);
            children.remove(b);
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
    
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        s.mouseDragged(mouseX, mouseY, 0, 0, -amount * 2);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    
    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        if (matricies == null) return;
        this.renderBackground(matricies, 0);
        List<IScriptThreadWrapper> tl = RunScript.getThreads();
        
        for (RunningThreadContainer r : ImmutableList.copyOf(this.running)) {
            tl.remove(r.t);
            r.render(matricies, mouseX, mouseY, delta);
        }
        
        for (IScriptThreadWrapper t : tl) {
            addContainer((ScriptThreadWrapper) t);
        }

        for (AbstractButtonWidget b : ImmutableList.copyOf(this.buttons)) {
            ((Button) b).render(matricies, mouseX, mouseY, delta);
        }
    }

    public void removed() {
        client.keyboard.setRepeatEvents(false);
    }

    public void onClose() {
        client.openScreen(parent);
    }

    public static class RTCSort implements Comparator<RunningThreadContainer> {
        public int compare(RunningThreadContainer arg0, RunningThreadContainer arg1) {
            try {
            return arg0.t.t.getName().compareTo(arg1.t.t.getName());
            } catch(NullPointerException e) {
                return 0;
            }
        }

    }
}
