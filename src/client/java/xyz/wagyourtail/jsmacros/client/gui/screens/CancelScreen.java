package xyz.wagyourtail.jsmacros.client.gui.screens;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.config.ClientConfigV2;
import xyz.wagyourtail.jsmacros.client.gui.containers.RunningContextContainer;
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

    public CancelScreen(Screen parent) {
        super(Text.literal("Cancel"), parent);
    }

    @Override
    public void init() {
        super.init();
        // force gc all currently closed contexts
        System.gc();
        topScroll = 10;
        running.clear();
        s = this.addDrawableChild(new Scrollbar(width - 12, 5, 8, height - 10, 0xFFFFFFFF, 0xFF000000, 0x7FFFFFFF, 1, this::onScrollbar));

        this.addDrawableChild(new Button(0, this.height - 12, this.width / 12, 12, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFFFF, Text.translatable("jsmacros.back"), (btn) -> this.close()));
        services = this.addDrawableChild(new AnnotatedCheckBox(this.width / 12 + 5, this.height - 12, 200, 12, textRenderer, 0xFFFFFFFF, 0xFF000000, 0xFFFFFFFF, 0xFFFFFFFF, Text.translatable("jsmacros.showservices"), JsMacrosClient.clientCore.config.getOptions(ClientConfigV2.class).showRunningServices, btn -> JsMacrosClient.clientCore.config.getOptions(ClientConfigV2.class).showRunningServices = ((AnnotatedCheckBox) btn).value));
    }

    public void addContainer(BaseScriptContext<?> t) {
        if (t == null) {
            return;
        }
        if (!services.value && t.getTriggeringEvent() instanceof EventService) {
            return;
        }
        if (!t.isContextClosed()) {
            running.add(new RunningContextContainer(10, topScroll + running.size() * 15, width - 26, 13, textRenderer, this, t));
            running.sort(new RTCSort());
            s.setScrollPages(running.size() * 15 / (double) (height - 20));
        } else {
            JsMacros.LOGGER.warn("Closed context {} was still in list", t.getMainThread().getName());
        }
    }

    public void removeContainer(RunningContextContainer t) {
        for (ClickableWidget b : t.getButtons()) {
            remove(b);
        }
        running.remove(t);
        s.setScrollPages(running.size() * 15 / (double) (height - 20));
        updatePos();
    }

    private void onScrollbar(double page) {
        topScroll = 10 - (int) (page * (height - 20));
        updatePos();
    }

    public void updatePos() {
        for (int i = 0; i < running.size(); ++i) {
            if (topScroll + i * 15 < 10 || topScroll + i * 15 > height - 10) {
                running.get(i).setVisible(false);
            } else {
                running.get(i).setVisible(true);
                running.get(i).setPos(10, topScroll + i * 15, width - 26, 13);
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horiz, double vert) {
        s.mouseDragged(mouseX, mouseY, 0, 0, -vert * 2);
        return super.mouseScrolled(mouseX, mouseY, horiz, vert);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if (drawContext == null) {
            return;
        }

        List<BaseScriptContext<?>> tl = new ArrayList<>(JsMacrosClient.clientCore.getContexts());

        for (RunningContextContainer r : ImmutableList.copyOf(this.running)) {
            tl.remove(r.t);
            if (!services.value && r.service) {
                removeContainer(r);
            }
            r.render(drawContext, mouseX, mouseY, delta);
        }

        for (BaseScriptContext<?> t : tl) {
            addContainer(t);
        }

        for (Element b : ImmutableList.copyOf(this.children())) {
            if (!(b instanceof Drawable)) {
                continue;
            }
            ((Drawable) b).render(drawContext, mouseX, mouseY, delta);
        }
    }

    @Override
    public void removed() {
        assert client != null;
    }

    @Override
    public void close() {
        this.openParent();
    }

    public static class RTCSort implements Comparator<RunningContextContainer> {
        @Override
        public int compare(RunningContextContainer arg0, RunningContextContainer arg1) {
            try {
                return arg0.t.getMainThread().getName().compareTo(arg1.t.getMainThread().getName());
            } catch (NullPointerException e) {
                return 0;
            }
        }

    }

}
