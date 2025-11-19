package xyz.wagyourtail.jsmacros.client.gui.overlays;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.util.TranslationUtil;
import xyz.wagyourtail.wagyourgui.elements.Button;
import xyz.wagyourtail.wagyourgui.elements.Scrollbar;
import xyz.wagyourtail.wagyourgui.overlays.IOverlayParent;
import xyz.wagyourtail.wagyourgui.overlays.OverlayContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class EventChooser extends OverlayContainer {
    private String selected;
    private final List<EventObj> events = new ArrayList<>();
    private int topScroll;
    private final Consumer<String> setEvent;
    private final Text eventText;

    public EventChooser(int x, int y, int width, int height, TextRenderer textRenderer, String selected, IOverlayParent parent, Consumer<String> setEvent) {
        super(x, y, width, height, textRenderer, parent);
        this.selected = selected;
        this.setEvent = setEvent;
        this.eventText = Text.translatable("jsmacros.events");
    }

    public void selectEvent(String event) {
        this.selected = event;
        for (EventObj e : events) {
            if (event.equals(e.event)) {
                e.btn.setColor(0x7FFFFFFF);
            } else {
                e.btn.setColor(0);
            }
        }
    }

    @Override
    public void init() {
        super.init();
        int w = width - 4;
        topScroll = y + 13;
        this.addDrawableChild(new Button(x + width - 12, y + 2, 10, 10, textRenderer, 0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFFFF, Text.literal("X"), (btn) -> {
            this.close();
        }));
        scroll = this.addDrawableChild(new Scrollbar(x + width - 10, y + 13, 8, height - 28, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));
        this.addDrawableChild(new Button(x + 2, y + height - 14, w / 2, 12, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFFFF, Text.translatable("gui.cancel"), (btn) -> {
            this.close();
        }));
        this.addDrawableChild(new Button(x + w / 2 + 3, y + height - 14, w / 2, 12, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFFFF, Text.translatable("jsmacros.select"), (btn) -> {
            if (this.selected != null && this.setEvent != null) {
                this.setEvent.accept(this.selected);
                this.close();
            }
        }));

        List<String> events = new ArrayList<>(JsMacrosClient.clientCore.eventRegistry.events);
        Collections.sort(events);
        for (String e : events) {
            addEvent(e);
        }
        this.selectEvent(selected);
    }

    public void addEvent(String eventName) {
        EventObj e = new EventObj(eventName, new Button(x + 3 + (events.size() % 5 * (width - 12) / 5), topScroll + (events.size() / 5 * 12), (width - 12) / 5, 12, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFFFF, TranslationUtil.getTranslatedEventName(eventName), (btn) -> {
            selectEvent(eventName);
        }));

        e.btn.visible = topScroll + (events.size() / 5 * 12) >= y + 13 && topScroll + (events.size() / 5 * 12) <= y + height - 27;
        events.add(e);
        this.addDrawableChild(e.btn);
        scroll.setScrollPages((Math.ceil(events.size() / 5D) * 12) / (double) Math.max(1, height - 39));
    }

    public void updateEventPos() {
        for (int i = 0; i < events.size(); ++i) {
            EventObj e = events.get(i);
            e.btn.visible = topScroll + (i / 5 * 12) >= y + 13 && topScroll + (i / 5 * 12) <= y + height - 27;
            e.btn.setPos(x + 3 + (i % 5 * (width - 12) / 5), topScroll + (i / 5 * 12), (width - 12) / 5, 12);
        }
    }

    public void onScrollbar(double page) {
        topScroll = y + 13 - (int) (page * (height - 27));
        int i = 0;
        for (EventObj fi : events) {
            fi.btn.visible = topScroll + (i / 5 * 12) >= y + 13 && topScroll + (i / 5 * 12) <= y + height - 27;
            fi.btn.setPos(x + 3 + (i % 5 * (width - 12) / 5), topScroll + (i / 5 * 12), (width - 12) / 5, 12);
            ++i;
        }
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        renderBackground(drawContext);

        drawContext.drawWrappedText(textRenderer, eventText, x + 3, y + 3, width - 14, 0xFFFFFFFF, false);

        drawContext.fill(x + 2, y + 12, x + width - 2, y + 13, 0xFFFFFFFF);
        drawContext.fill(x + 2, y + height - 15, x + width - 2, y + height - 14, 0xFFFFFFFF);
//        textRenderer.draw(, mouseX, mouseY, color, shadow, matrix, vertexConsumers, seeThrough, backgroundColor, light)
        super.render(drawContext, mouseX, mouseY, delta);

        for (ClickableWidget b : ImmutableList.copyOf(this.buttons)) {
            if (b instanceof Button && ((Button) b).hovering && ((Button) b).cantRenderAllText()) {
                // border
                int width = textRenderer.getWidth(b.getMessage());
                drawContext.fill(mouseX - 3, mouseY, mouseX + width + 3, mouseY + 1, 0x7F7F7F7F);
                drawContext.fill(mouseX + width + 2, mouseY - textRenderer.fontHeight - 3, mouseX + width + 3, mouseY, 0x7F7F7F7F);
                drawContext.fill(mouseX - 3, mouseY - textRenderer.fontHeight - 3, mouseX - 2, mouseY, 0x7F7F7F7F);
                drawContext.fill(mouseX - 3, mouseY - textRenderer.fontHeight - 4, mouseX + width + 3, mouseY - textRenderer.fontHeight - 3, 0x7F7F7F7F);

                // fill
                drawContext.fill(mouseX - 2, mouseY - textRenderer.fontHeight - 3, mouseX + width + 2, mouseY, 0xFF000000);
                drawContext.drawTextWithShadow(textRenderer, b.getMessage(), mouseX, mouseY - textRenderer.fontHeight - 1, 0xFFFFFFFF);
            }
        }
    }

    public static class EventObj {
        String event;
        Button btn;

        public EventObj(String event, Button btn) {
            this.event = event;
            this.btn = btn;
        }

    }

}
