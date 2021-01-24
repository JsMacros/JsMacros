package xyz.wagyourtail.jsmacros.client.gui.overlays;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;
import xyz.wagyourtail.jsmacros.client.gui.elements.Scrollbar;

import java.util.LinkedList;
import java.util.List;

public class StringListOverlay extends OverlayContainer {
    private final List<String> list;
    private final List<Button> listItems = new LinkedList<>();
    private final Text title;
    private int topScroll;
    private int selected = -1;
    
    public StringListOverlay(int x, int y, int width, int height, TextRenderer textRenderer, Text title, List<String> list, IOverlayParent parent) {
        super(x, y, width, height, textRenderer, parent);
        this.list = list;
        this.title = title;
    }
    
    public void init() {
        super.init();
        int w = width - 4;
        topScroll = y + 13;
        
        scroll = this.addButton(new Scrollbar(x + width - 10, y + 13, 8, height - 28, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));
        this.addButton(new Button(x + width - 12, y + 2, 10, 10, textRenderer, 0, 0x7FFFFFFF, 0x7FFFFFFF, 0xFFFFFF, new LiteralText("X"), (btn) -> {
            this.close();
        }));
        
        this.addButton(new Button(x + 2, y + height - 14, w / 3, 12, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.remove"), (btn) -> {
            if (selected >= 0 && selected < listItems.size()) {
                removeButton(listItems.remove(selected));
                list.remove(selected);
                setSelected(-1);
            }
        }));
        
        this.addButton(new Button(x + w / 3 + 2, y + height - 14, w / 3, 12, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.rename"), (btn) -> {
            if (selected >= 0 && selected < listItems.size()) {
                this.openOverlay(new TextPrompt(x + width / 2 - 100, y + height / 2 - 50, 200, 100, textRenderer, new TranslatableText("jsmacros.rename"), list.get(selected), this, (str) -> {
                    listItems.get(selected).setMessage(new LiteralText(str));
                    list.set(selected, str);
                }));
            }
        }));
        
        this.addButton(new Button(x + w * 2 / 3 + 2, y + height - 14, w / 3, 12, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.add"), (btn) -> {
            this.openOverlay(new TextPrompt(x + width / 2 - 100, y + height / 2 - 50, 200, 100, textRenderer, new TranslatableText("jsmacros.add"), list.get(selected), this, (str) -> {
                list.add(str);
                addItem(str);
            }));
        }));
        
        for (String element : list) {
            addItem(element);
        }
    }
    
    public void addItem(String name) {
        int index = listItems.size();
        listItems.add(new Button(x+3+(list.size() % 5 * (width - 12) / 5), topScroll + (list.size() / 5 * 12), (width - 12) / 5, 12, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new LiteralText(name), (btn) -> {
            setSelected(index);
        }));
    }
    
    public void setSelected(int index) {
        listItems.get(selected).forceHover = false;
        listItems.get(selected = index).forceHover = false;
    }
    
    public void onScrollbar(double page) {
        topScroll = y + 13 - (int) (page * (height - 27));
        int i = 0;
        for (Button btn : listItems) {
            btn.visible = topScroll + (i / 5 * 12) >= y + 13 && topScroll + (i / 5 * 12) <= y + height - 27;
            btn.setPos(x + 3 + (i % 5 * (width - 12) / 5), topScroll + (i / 5 * 12), (width - 12) / 5, 12);
            ++i;
        }
    }
    
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        
        textRenderer.drawTrimmed(title, x + 3, y + 3, width - 14, 0xFFFFFF);
        
        fill(matrices, x + 2, y + 12, x + width - 2, y + 13, 0xFFFFFFFF);
        fill(matrices, x + 2, y + height - 15, x + width - 2, y + height - 14, 0xFFFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
        
        for (AbstractButtonWidget b : ImmutableList.copyOf(this.buttons)) {
            if (b instanceof Button && ((Button) b).hovering && ((Button) b).cantRenderAllText()) {
                // border
                int width = textRenderer.getWidth(b.getMessage());
                fill(matrices, mouseX-3, mouseY, mouseX+width+3, mouseY+1, 0x7F7F7F7F);
                fill(matrices, mouseX+width+2, mouseY-textRenderer.fontHeight - 3, mouseX+width+3, mouseY, 0x7F7F7F7F);
                fill(matrices, mouseX-3, mouseY-textRenderer.fontHeight - 3, mouseX-2, mouseY, 0x7F7F7F7F);
                fill(matrices, mouseX-3, mouseY-textRenderer.fontHeight - 4, mouseX+width+3, mouseY-textRenderer.fontHeight - 3, 0x7F7F7F7F);
                
                // fill
                fill(matrices, mouseX-2, mouseY-textRenderer.fontHeight - 3, mouseX+width+2, mouseY, 0xFF000000);
                drawTextWithShadow(matrices, textRenderer, b.getMessage(), mouseX, mouseY-textRenderer.fontHeight - 1, 0xFFFFFF);
            }
        }
    }
}
