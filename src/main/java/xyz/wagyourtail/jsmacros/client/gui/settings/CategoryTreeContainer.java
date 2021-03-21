package xyz.wagyourtail.jsmacros.client.gui.settings;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.jsmacros.client.gui.containers.MultiElementContainer;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;
import xyz.wagyourtail.jsmacros.client.gui.elements.Scrollbar;

import java.util.*;

public class CategoryTreeContainer extends MultiElementContainer<SettingsOverlay> {
    public Scrollbar scroll;
    private int topScroll;
    private final CategoryTree head = new CategoryTree("head");
    private final List<ButtonWrapper> options = new LinkedList<>();
    
    public CategoryTreeContainer(int x, int y, int width, int height, TextRenderer textRenderer, SettingsOverlay parent) {
        super(x, y, width, height, textRenderer, parent);
        init();
    }
    
    @Override
    public void init() {
        super.init();
        scroll = addButton(new Scrollbar(x + width - 8, y, 8, height, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));
        topScroll = 0;
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    
    }
    
    public void onScrollbar(double page) {
        topScroll -= page * height;
        updateButtonPositions();
    }
    
    public void addCategory(String... category) {
        if (!head.hasChild(category[0])) {
            head.addChild(category);
            toggleBtnForCategory(category[0]);
        } else {
            head.addChild(category);
        }
    }
    
    private void toggleBtnForCategory(String... category) {
        ButtonWrapper wrapper = new ButtonWrapper(category);
        int index = 0;
        for (ButtonWrapper w : options) {
            int result = Objects.compare(w.category, category, new StringArrayComparator());
            if (result > 0) {
                break;
            } else if (result == 0) {
                removeButton(w.btn);
                options.remove(w);
                updateButtonPositions();
                return;
            }
            ++index;
        }
        StringBuilder start = new StringBuilder();
        for (int i = 1; i < category.length; ++i) {
            start.append(">");
        }
        wrapper.btn = addButton(new Button(x, y + topScroll, width - 8, textRenderer.fontHeight + 3, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new LiteralText(start.toString()).append(new TranslatableText(category[category.length - 1])), (btn) -> {
            parent.selectCategory(category);
            for (String child : head.getCategory(category).children.keySet()) {
                String[] childCategory = new String[category.length + 1];
                System.arraycopy(category, 0, childCategory, 0, category.length);
                childCategory[category.length] = child;
                toggleBtnForCategory(childCategory);
            }
        }));
        wrapper.btn.horizCenter = false;
        options.add(index, wrapper);
        scroll.setScrollPages(options.size() * (float) (textRenderer.fontHeight + 3) / (float) height);
        updateButtonPositions();
    }
    
    private void updateButtonPositions() {
        int i = 0;
        for (ButtonWrapper btn : options) {
            int scrollAmt = topScroll + i * (textRenderer.fontHeight + 3);
            btn.btn.visible = scrollAmt >= 0 && scrollAmt <= height;
            btn.btn.setPos(x, y + scrollAmt, width - 8, textRenderer.fontHeight + 3);
            ++i;
        }
    }
    
    static class ButtonWrapper {
        String[] category;
        Button btn;
        
        ButtonWrapper(String... category) {
            this.category = category;
        }
    }
    
    static class CategoryTree {
        String option;
        Map<String, CategoryTree> children = new HashMap<>();
        
        CategoryTree(String category) {
            option = category;
        }
        
        boolean hasChildren() {
            return !children.isEmpty();
        }
        
        void addChild(String... child) {
            if (child.length > 0) {
                String[] computeChild = new String[child.length - 1];
                System.arraycopy(child, 1, computeChild, 0, child.length - 1);
                children.computeIfAbsent(child[0], CategoryTree::new).addChild(computeChild);
            }
        }
        
        boolean hasChild(String... child) {
            if (children.containsKey(child[0])) {
                if (child.length > 1) {
                    String[] computeChild = new String[child.length - 1];
                    System.arraycopy(child, 1, computeChild, 0, child.length - 1);
                    return children.get(child[0]).hasChild(computeChild);
                }
                return true;
            }
            return false;
        }
        
        CategoryTree getCategory(String ...child) {
            if (children.containsKey(child[0])) {
                if (child.length > 1) {
                    String[] computeChild = new String[child.length - 1];
                    System.arraycopy(child, 1, computeChild, 0, child.length - 1);
                    return children.get(child[0]).getCategory(computeChild);
                }
                return children.get(child[0]);
            }
            return null;
        }
    }
    
    static class StringArrayComparator implements Comparator<String[]> {
    
        @Override
        public int compare(String[] o1, String[] o2) {
            int min = Math.min(o1.length, o2.length);
            for (int i = 0; i < min; ++i) {
                int j = o1[i].compareTo(o2[i]);
                if (j != 0) return j;
            }
            return o1.length - o2.length;
        }
    
    }
    
}
