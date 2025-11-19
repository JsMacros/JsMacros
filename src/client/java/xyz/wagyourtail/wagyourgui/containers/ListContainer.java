package xyz.wagyourtail.wagyourgui.containers;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import xyz.wagyourtail.wagyourgui.elements.Button;
import xyz.wagyourtail.wagyourgui.elements.Scrollbar;
import xyz.wagyourtail.wagyourgui.overlays.IOverlayParent;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class ListContainer extends MultiElementContainer<IContainerParent> {
    private final List<Text> list;
    private final List<Button> listItems = new LinkedList<>();
    private Scrollbar scroll;
    private int topScroll;
    private int selected = -1;
    public Consumer<Integer> onSelect;

    public ListContainer(int x, int y, int width, int height, TextRenderer textRenderer, List<Text> list, IOverlayParent parent, Consumer<Integer> onSelect) {
        super(x, y, width, height, textRenderer, parent);
        this.list = list;
        this.onSelect = onSelect;
    }

    @Override
    public void init() {
        super.init();
        int w = width - 4;
        topScroll = y + 13;

        scroll = this.addDrawableChild(new Scrollbar(x + width - 10, y + 13, 8, height - 28, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));

        for (Text element : list) {
            addItem(element);
        }
    }

    public void addItem(Text name) {
        int index = listItems.size();
        listItems.add(this.addDrawableChild(new Button(x + 3 + (index % 5 * (width - 12) / 5), topScroll + (index / 5 * 12), (width - 12) / 5, 12, textRenderer, 0, 0, 0x7FFFFFFF, 0xFFFFFFFF, name, (btn) -> setSelected(index))));
    }

    public void setSelected(int index) {
        if (selected > -1) {
            listItems.get(selected).forceHover = false;
        }
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
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {

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

}
