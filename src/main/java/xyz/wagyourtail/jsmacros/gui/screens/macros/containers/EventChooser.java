package xyz.wagyourtail.jsmacros.gui.screens.macros.containers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.jsmacros.gui.elements.Button;
import xyz.wagyourtail.jsmacros.gui.elements.OverlayContainer;
import xyz.wagyourtail.jsmacros.gui.elements.Scrollbar;
import xyz.wagyourtail.jsmacros.profile.Profile;

public class EventChooser extends OverlayContainer {
    private String selected;
    private List<EventObj> events = new ArrayList<>();
    private int topScroll;
    private Consumer<String> setEvent;
    private Text eventText;
    
    public EventChooser(int x, int y, int width, int height, TextRenderer textRenderer, String selected, Consumer<AbstractButtonWidget> addButton, Consumer<AbstractButtonWidget> removeButton, Consumer<OverlayContainer> close, Consumer<String> setEvent) {
        super(x, y, width, height, textRenderer, addButton, removeButton, close);
        this.selected = selected;
        this.setEvent = setEvent;
        this.eventText = new TranslatableText("jsmacros.events");
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
    
    public void init() {
        super.init();
        int w = width - 4;
        topScroll = y + 13;
        this.addButton(new Button(x + width - 12, y + 2, 10, 10, 0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("X"), (btn) -> {
            this.close();
        }));
        scroll = (Scrollbar) this.addButton(new Scrollbar(x + width - 10, y + 13, 8, height - 28, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));
        this.addButton(new Button(x + 2, y + height - 14, w / 2, 12, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("gui.cancel"), (btn) -> {
            this.close();
        }));
        this.addButton(new Button(x + w / 2 + 3, y + height - 14, w / 2, 12, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.select"), (btn) -> {
            if (this.selected != null && this.setEvent != null) {
                this.setEvent.accept(this.selected);
                this.close();
            }
        }));
        
        List<String> events = new ArrayList<>(Profile.registry.events);
        Collections.sort(events);
        for (String e : events) {
            addEvent(e);
        }
        this.selectEvent(selected);
    }
    
    public void addEvent(String eventName) {
        EventObj e = new EventObj(eventName, new Button(x+3+(events.size() % 5 * (width - 12) / 5), topScroll + (events.size() / 5 * 12), (width - 12) / 5, 12, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new LiteralText(eventName.replace("Event", "")), (btn) -> {
            selectEvent(eventName);
        }));

        if (topScroll + (events.size() / 5 * 12) < y + 13 || topScroll + (events.size() / 5 * 12) > y + height - 27) e.btn.visible = false;
        else e.btn.visible = true;
        events.add(e);
        this.addButton(e.btn);
        scroll.setScrollPages((Math.ceil(events.size() / 5D) * 12) /(double) Math.max(1, height - 39));
    }
    
    public void updateEventPos() {
        for (int i = 0; i < events.size(); ++i) {
            EventObj e = events.get(i);
            if (topScroll + (i / 5 * 12) < y + 13 || topScroll + (i / 5 * 12) > y + height - 27) e.btn.visible = false;
            else e.btn.visible = true;
            e.btn.setPos(x + 3 + (i % 5 * (width - 12) / 5), topScroll + (i / 5 * 12), (width - 12) / 5, 12);
        }
    }
    
    public void onScrollbar(double page) {
        topScroll = y + 13 - (int) (page * (height - 27));
        int i = 0;
        for (EventObj fi : events) {
            if (topScroll + (i / 5 * 12) < y + 13 || topScroll + (i / 5 * 12) > y + height - 27) fi.btn.visible = false;
            else fi.btn.visible = true;
            fi.btn.setPos(x + 3 + (i % 5 * (width - 12) / 5), topScroll + (i / 5 * 12), (width - 12) / 5, 12);
            ++i;
        }
    }
    
    public void render(MatrixStack matricies, int mouseX, int mouseY, float delta) {
        renderBackground(matricies);

        textRenderer.drawTrimmed(eventText, x + 3, y + 3, width - 14, 0xFFFFFF);

        fill(matricies, x + 2, y + 12, x + width - 2, y + 13, 0xFFFFFFFF);
        fill(matricies, x + 2, y + height - 15, x + width - 2, y + height - 14, 0xFFFFFFFF);
//        textRenderer.draw(, mouseX, mouseY, color, shadow, matrix, vertexConsumers, seeThrough, backgroundColor, light)
        super.render(matricies, mouseX, mouseY, delta);
        
        for (AbstractButtonWidget b : ImmutableList.copyOf(this.buttons)) {
            if (((Button) b).hovering && !((Button) b).canRenderAllText()) {
                // border
                int width = textRenderer.getWidth(b.getMessage());
                fill(matricies, mouseX-3, mouseY, mouseX+width+3, mouseY+1, 0x7F7F7F7F);
                fill(matricies, mouseX+width+2, mouseY-textRenderer.fontHeight - 3, mouseX+width+3, mouseY, 0x7F7F7F7F);
                fill(matricies, mouseX-3, mouseY-textRenderer.fontHeight - 3, mouseX-2, mouseY, 0x7F7F7F7F);
                fill(matricies, mouseX-3, mouseY-textRenderer.fontHeight - 4, mouseX+width+3, mouseY-textRenderer.fontHeight - 3, 0x7F7F7F7F);
                
                // fill
                fill(matricies, mouseX-2, mouseY-textRenderer.fontHeight - 3, mouseX+width+2, mouseY, 0xFF000000);
                drawTextWithShadow(matricies, textRenderer, b.getMessage(), mouseX, mouseY-textRenderer.fontHeight - 1, 0xFFFFFF);
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
