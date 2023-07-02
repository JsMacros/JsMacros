package xyz.wagyourtail.jsmacros.client.gui.settings;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import xyz.wagyourtail.wagyourgui.containers.MultiElementContainer;
import xyz.wagyourtail.wagyourgui.elements.Button;
import xyz.wagyourtail.wagyourgui.elements.Scrollbar;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CategoryTreeContainer extends MultiElementContainer<ICategoryTreeParent> implements ICategoryTreeParent {
    public final String category;
    public Scrollbar scroll;
    public Map<String, CategoryTreeContainer> children = new HashMap<>();
    private boolean showChildren;
    public Button expandBtn;
    public Button showBtn;
    public boolean isHead;
    public int topScroll;
    public int btnHeight;

    private CategoryTreeContainer(int x, int y, int width, int height, TextRenderer textRenderer, ICategoryTreeParent parent, String category) {
        super(x, y, width, height, textRenderer, parent);
        this.isHead = false;
        this.showChildren = false;
        this.category = category;
        this.btnHeight = textRenderer.fontHeight + 2;
    }

    public CategoryTreeContainer(int x, int y, int width, int height, TextRenderer textRenderer, ICategoryTreeParent parent) {
        super(x, y, width, height, textRenderer, parent);
        this.isHead = true;
        this.showChildren = true;
        this.category = "HEAD";
        this.btnHeight = 0;
    }

    public CategoryTreeContainer addCategory(String... category) {
        if (category.length > 0) {
            String[] childCategory = new String[category.length - 1];
            System.arraycopy(category, 1, childCategory, 0, childCategory.length);
            CategoryTreeContainer newChild = children.computeIfAbsent(category[0], (cat) -> new CategoryTreeContainer(x + (isHead ? 0 : btnHeight / 2), y, width - (isHead ? 8 : btnHeight / 2), btnHeight, textRenderer, this, cat)).addCategory(childCategory);
            if (isHead) {
                if (scroll != null) {
                    scroll.setScrollPages(children.values().stream().map(e -> e.height).reduce(Integer::sum).orElse(0) / (double) height);
                }
                updateOffsets();
            }
            return newChild;
        }
        return this;
    }

    @Override
    public void selectCategory(String... category) {
        if (isHead) {
            parent.selectCategory(category);
            return;
        }
        String[] childCategory = new String[category.length + 1];
        childCategory[0] = this.category;
        System.arraycopy(category, 0, childCategory, 1, category.length);
        parent.selectCategory(childCategory);
    }

    public void updateOffsets() {
        if (isHead) {
            int maxHeight = updateOffsets(y, y, y + height, showChildren);
            if (scroll != null) {
                scroll.setScrollPages((maxHeight + 4) / (double) height);
            }
        } else {
            getHead().updateOffsets();
        }
    }

    private int updateOffsets(int y, int minShow, int maxShow, boolean parentShowChildren) {
        this.y = y;
        if (this.expandBtn != null) {
            this.expandBtn.setY(y);
            if (y < minShow || y + btnHeight > maxShow) {
                this.expandBtn.visible = false;
            } else {
                this.expandBtn.visible = parentShowChildren;
            }
        }
        if (this.showBtn != null) {
            this.showBtn.setY(y);
            if (y < minShow || y + btnHeight > maxShow) {
                this.showBtn.visible = false;
            } else {
                this.showBtn.visible = parentShowChildren;
            }
        }
        if (!this.isHead) {
            this.height = btnHeight;
        }
        int top = isHead ? y - topScroll : y + btnHeight;
        Iterator<CategoryTreeContainer> iterator = children.keySet().stream().sorted().map(e -> children.get(e)).iterator();
        while (iterator.hasNext()) {
            CategoryTreeContainer child = iterator.next();

            int incr = child.updateOffsets(top, minShow, maxShow, showChildren && parentShowChildren);
            if (showChildren && parentShowChildren) {
                if (!this.isHead) {
                    this.height += child.height;
                }
                top += incr;
            }
        }
        return top - y + topScroll;
    }

    @Override
    public void init() {
        if (this.isHead) {
            topScroll = 0;
            scroll = addDrawableChild(new Scrollbar(x + width - 8, y, 8, height, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));
            for (CategoryTreeContainer child : children.values()) {
                child.initChild(showChildren);
            }
            updateOffsets();
        } else {
            throw new RuntimeException("attempted to init non-head of category tree");
        }
    }

    private void initChild(boolean show) {
        if (children.size() > 0) {
            expandBtn = addDrawableChild(new Button(x, y, btnHeight, btnHeight, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, Text.literal(">"), (btn) -> this.toggleExpand()));
            expandBtn.visible = show;
        }
        showBtn = addDrawableChild(new Button(x + btnHeight, y, width - btnHeight, btnHeight, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, Text.translatable(category), (btn) -> this.selectCategory()));
        showBtn.visible = show;
        showBtn.horizCenter = false;

        for (CategoryTreeContainer child : children.values()) {
            child.initChild(showChildren);
        }
    }

    private void toggleExpand() {
        showChildren = !showChildren;
        expandBtn.setMessage(Text.literal(showChildren ? "<" : ">"));
        updateOffsets();
    }

    private CategoryTreeContainer getHead() {
        if (!this.isHead) {
            return ((CategoryTreeContainer) parent).getHead();
        }
        return this;
    }

    public void onScrollbar(double page) {
        int newTopScroll = (int) (height * page);
        if (newTopScroll != topScroll) {
            topScroll = newTopScroll;
            updateOffsets();
        }
    }

    @Override
    public void render(MatrixStack drawContext, int mouseX, int mouseY, float delta) {

    }

}
