package xyz.wagyourtail.wagyourgui.containers;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.IChatComponent;
import xyz.wagyourtail.wagyourgui.elements.Button;
import xyz.wagyourtail.wagyourgui.elements.Scrollbar;
import xyz.wagyourtail.wagyourgui.overlays.IOverlayParent;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class ListContainer extends MultiElementContainer<IContainerParent> {
    private final List<IChatComponent> list;
    private final List<Button> listItems = new LinkedList<>();
    private Scrollbar scroll;
    private int topScroll;
    private int selected = -1;
    public Consumer<Integer> onSelect;
    
    public ListContainer(int x, int y, int width, int height, FontRenderer textRenderer, List<IChatComponent> list, IOverlayParent parent, Consumer<Integer> onSelect) {
        super(x, y, width, height, textRenderer, parent);
        this.list = list;
        this.onSelect = onSelect;
    }
    
    @Override
    public void init() {
        super.init();
        int w = width - 4;
        topScroll = y + 13;
        
        scroll = this.addButton(new Scrollbar(x + width - 10, y + 13, 8, height - 28, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));
        
        for (IChatComponent element : list) {
            addItem(element);
        }
    }
    
    public void addItem(IChatComponent name) {
        int index = listItems.size();
        listItems.add(this.addButton(new Button(x + 3 + (index % 5 * (width - 12) / 5), topScroll + (index / 5 * 12), (width - 12) / 5, 12, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFF, name, (btn) -> setSelected(index))));
    }
    
    public void setSelected(int index) {
        if (selected > -1)
            listItems.get(selected).forceHover = false;
        listItems.get(selected = index).forceHover = true;
        onSelect.accept(selected);
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
    
    @Override
    public void render(int mouseX, int mouseY, float delta) {
        
        for (GuiButton b : ImmutableList.copyOf(this.buttons)) {
            if (b instanceof Button && ((Button) b).hovering && ((Button) b).cantRenderAllText()) {
                // border
                int width = textRenderer.getStringWidth(b.message);
                fill(mouseX - 3, mouseY, mouseX + width + 3, mouseY + 1, 0x7F7F7F7F);
                fill(mouseX+width+2, mouseY-textRenderer.fontHeight - 3, mouseX + width + 3, mouseY, 0x7F7F7F7F);
                fill(mouseX-3, mouseY-textRenderer.fontHeight - 3, mouseX-2, mouseY, 0x7F7F7F7F);
                fill(mouseX-3, mouseY-textRenderer.fontHeight - 4, mouseX+width+3, mouseY - textRenderer.fontHeight - 3, 0x7F7F7F7F);
                
                // fill
                fill(mouseX-2, mouseY - textRenderer.fontHeight - 3, mouseX+width+2, mouseY, 0xFF000000);
                drawWithShadow(textRenderer, b.message, mouseX, mouseY-textRenderer.fontHeight - 1, 0xFFFFFF);
            }
        }
    }
}
