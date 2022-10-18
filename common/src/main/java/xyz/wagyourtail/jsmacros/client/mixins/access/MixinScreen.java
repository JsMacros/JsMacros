package xyz.wagyourtail.jsmacros.client.mixins.access;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.LockButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.access.CustomClickEvent;
import xyz.wagyourtail.jsmacros.client.api.helpers.gui.ButtonWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.render.Draw2D;
import xyz.wagyourtail.jsmacros.client.api.helpers.gui.ClickableWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.item.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.gui.CheckBoxWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.gui.CyclingButtonWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.gui.LockButtonWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.gui.SliderWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.gui.TextFieldWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.classes.PositionCommon;
import xyz.wagyourtail.jsmacros.client.api.classes.PositionCommon.Pos2D;
import xyz.wagyourtail.jsmacros.client.api.classes.PositionCommon.Vec2D;
import xyz.wagyourtail.jsmacros.client.api.render.shared.classes.Draw2DElement;
import xyz.wagyourtail.jsmacros.client.api.render.shared.classes.ImageElement;
import xyz.wagyourtail.jsmacros.client.api.render.shared.classes.ItemElement;
import xyz.wagyourtail.jsmacros.client.api.render.shared.classes.LineElement;
import xyz.wagyourtail.jsmacros.client.api.render.shared.classes.RectElement;
import xyz.wagyourtail.jsmacros.client.api.render.shared.classes.RenderCommon;
import xyz.wagyourtail.jsmacros.client.api.render.shared.classes.TextElement;
import xyz.wagyourtail.jsmacros.client.api.render.shared.interfaces.IDraw2D;
import xyz.wagyourtail.jsmacros.client.api.render.shared.interfaces.IScreen;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.wagyourgui.elements.CheckBox;
import xyz.wagyourtail.wagyourgui.elements.Slider;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

@Mixin(Screen.class)
@Implements(@Interface(iface = IScreen.class, prefix = "soft$"))
public abstract class MixinScreen extends AbstractParentElement implements IScreen {
    @Unique private final Set<RenderCommon.RenderElement> elements = new LinkedHashSet<>();
    @Unique private MethodWrapper<PositionCommon.Pos2D, Integer, Object, ?> onMouseDown;
    @Unique private MethodWrapper<PositionCommon.Vec2D, Integer, Object, ?> onMouseDrag;
    @Unique private MethodWrapper<PositionCommon.Pos2D, Integer, Object, ?> onMouseUp;
    @Unique private MethodWrapper<PositionCommon.Pos2D, Double, Object, ?> onScroll;
    @Unique private MethodWrapper<Integer, Integer, Object, ?> onKeyPressed;
    @Unique private MethodWrapper<IScreen, Object, Object, ?> onInit;
    @Unique private MethodWrapper<String, Object, Object, ?> catchInit;
    @Unique private MethodWrapper<IScreen, Object, Object, ?> onClose;
    
    @Shadow public int width;
    @Shadow public int height;
    @Shadow @Final protected Text title;
    @Shadow protected MinecraftClient client;
    @Shadow protected TextRenderer textRenderer;
    
    @Shadow(aliases = {"method_37063", "m_142416_"}) protected abstract <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement);
    @Shadow(aliases = {"close", "method_25419", "m_7379_"}) public abstract void onClose();
    @Shadow protected abstract void init();

    
    @Shadow public abstract void tick();
    
    @Shadow public abstract boolean shouldCloseOnEsc();

    @Shadow @Final private List<Element> children;

    @Shadow protected abstract void renderTextHoverEffect(MatrixStack matrices, @Nullable Style style, int x, int y);

    @Shadow public abstract boolean handleTextClick(@Nullable Style style);

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public List<Draw2DElement> getDraw2Ds() {
        List<Draw2DElement> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof Draw2DElement) {
                    list.add((Draw2DElement) e);
                }
            }
        }
        return list;
    }

    @Override
    public Draw2DElement addDraw2D(Draw2D draw2D, int x, int y, int width, int height) {
        return addDraw2D(draw2D, x, y, width, height, 0);
    }

    @Override
    public Draw2DElement addDraw2D(Draw2D draw2D, int x, int y, int width, int height, int zIndex) {
        if (draw2D == null) {
            return null;
        }
        Draw2DElement d = new Draw2DElement(draw2D, x, y, width, height, zIndex, 1, 0);
        synchronized (elements) {
            elements.add(d);
        }
        return d;
    }

    @Override
    public IScreen removeDraw2D(Draw2DElement draw2D) {
        synchronized (elements) {
            elements.remove(draw2D);
        }
        return this;
    }

    @Override
    public List<LineElement> getLines() {
        List<LineElement> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof LineElement) {
                    list.add((LineElement) e);
                }
            }
        }
        return list;
    }
    

    @Override
    public List<TextElement> getTexts() {
        List<TextElement> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof TextElement) list.add((TextElement) e);
            }
        }
        return list;
    }

    @Override
    public List<RectElement> getRects() {
        List<RectElement> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof RectElement) list.add((RectElement) e);
            }
        }
        return list;
    }

    @Override
    public List<ItemElement> getItems() {
        List<ItemElement> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof ItemElement) list.add((ItemElement) e);
            }
        }
        return list;
    }

    @Override
    public List<ImageElement> getImages() {
        List<ImageElement> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof ImageElement) list.add((ImageElement) e);
            }
        }
        return list;
    }

    @Override
    public List<TextFieldWidgetHelper> getTextFields() {
        Map<TextFieldWidget, TextFieldWidgetHelper> btns = new LinkedHashMap<>();
        for (RenderCommon.RenderElement el : elements) {
            if (el instanceof TextFieldWidgetHelper) {
                btns.put(((TextFieldWidgetHelper) el).getRaw(), (TextFieldWidgetHelper) el);
            }
        }
        synchronized (children) {
            for (Element e : children) {
                if (e instanceof TextFieldWidget && !btns.containsKey(e)) {
                    btns.put((TextFieldWidget) e, new TextFieldWidgetHelper((TextFieldWidget) e));
                }
            }
        }
        return ImmutableList.copyOf(btns.values());
    }
    
    @Override
    public List<ClickableWidgetHelper<?, ?>> getButtonWidgets() {
        Map<ClickableWidget, ClickableWidgetHelper<?, ?>> btns = new LinkedHashMap<>();
        for (RenderCommon.RenderElement el : elements) {
            if (el instanceof ClickableWidgetHelper) {
                btns.put(((ClickableWidgetHelper<?, ?>) el).getRaw(), (ClickableWidgetHelper<?, ?>) el);
            }
        }
        synchronized (children) {
            for (Element e : children) {
                if ((e instanceof ButtonWidget) && !btns.containsKey(e)) {
                    btns.put((ClickableWidget) e, new ClickableWidgetHelper<>((ClickableWidget) e));
                }
            }
        }
        return ImmutableList.copyOf(btns.values());
    }
    
    @Override
    public List<RenderCommon.RenderElement> getElements() {
        return ImmutableList.copyOf(elements);
    }
    
    @Override
    public IScreen removeElement(RenderCommon.RenderElement e) {
        synchronized (elements) {
            elements.remove(e);
            if (e instanceof ClickableWidgetHelper) children.remove(((ClickableWidgetHelper<?, ?>) e).getRaw());
        }
        return this;
    }

    @Override
    public <T extends RenderCommon.RenderElement> T reAddElement(T e) {
        synchronized (elements) {
            elements.add(e);
            if (e instanceof ClickableWidgetHelper) children.add(((ClickableWidgetHelper<?, ?>) e).getRaw());
        }
        return e;
    }
    
    @Override
    public TextElement addText(String text, int x, int y, int color, boolean shadow) {
        return addText(text, x, y, color, 0, shadow, 1, 0);
    }
    
    @Override
    public TextElement addText(String text, int x, int y, int color, int zIndex, boolean shadow) {
        return addText(text, x, y, color, zIndex, shadow, 1, 0);
    }
    
    @Override
    public TextElement addText(String text, int x, int y, int color, boolean shadow, double scale, double rotation) {
        return addText(text, x, y, color, 0, shadow, scale, rotation);
    }
    
    @Override
    public TextElement addText(String text, int x, int y, int color, int zIndex, boolean shadow, double scale, double rotation) {
        TextElement t = new TextElement(text, x, y, color, zIndex, shadow, scale, (float) rotation);
        synchronized (elements) {
            elements.add(t);
        }
        return t;
    }
    
    
    @Override
    public TextElement addText(TextHelper text, int x, int y, int color, boolean shadow) {
        return addText(text, x, y, color, 0, shadow, 1, 0);
    }
    
    @Override
    public TextElement addText(TextHelper text, int x, int y, int color, int zIndex, boolean shadow) {
        return addText(text, x, y, color, zIndex, shadow, 1, 0);
    }
    
    @Override
    public TextElement addText(TextHelper text, int x, int y, int color, boolean shadow, double scale, double rotation) {
        return addText(text, x, y, color, 0, shadow, scale, rotation);
    }
    
    @Override
    public TextElement addText(TextHelper text, int x, int y, int color, int zIndex, boolean shadow, double scale, double rotation) {
        TextElement t = new TextElement(text, x, y, color, zIndex, shadow, scale, (float) rotation);
        synchronized (elements) {
            elements.add(t);
        }
        return t;
    }
    
    @Override
    public IScreen removeText(TextElement t) {
        synchronized (elements) {
            elements.remove(t);
        }
        return this;
    }

    @Override
    public ImageElement addImage(int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth,
                                 int regionHeight, int textureWidth, int textureHeight) {
        return addImage(x, y, width, height, 0, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, 0);
    }
    
    @Override
    public ImageElement addImage(int x, int y, int width, int height, int zIndex, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        return addImage(x, y, width, height, zIndex, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, 0);
    }
    
    @Override
    public ImageElement addImage(int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth,
                                 int regionHeight, int textureWidth, int textureHeight, double rotation) {
        return addImage(x, y, width, height, 0, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, rotation);
    }


    /**
     * @since 1.4.0
     * @see IDraw2D#addImage(int, int, int, int, int, String, int, int, int, int, int, int, double)
     */
    @Override
    public ImageElement addImage(int x, int y, int width, int height, int zIndex, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation) {
        return addImage(x, y, width, height, zIndex, 0xFFFFFFFF, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, rotation);
    }

    /**
     * @since 1.6.5
     * @see IDraw2D#addImage(int, int, int, int, int, int, String, int, int, int, int, int, int, double)
     */
    @Override
    public ImageElement addImage(int x, int y, int width, int height, int zIndex, int color, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation) {
        ImageElement i = new ImageElement(x, y, width, height, zIndex, color, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, (float) rotation);
        synchronized (elements) {
            elements.add(i);
        }
        return i;
    }
    /**
     * @since 1.6.5
     * @see IDraw2D#addImage(int, int, int, int, int, int, int, String, int, int, int, int, int, int, double)
     */
    @Override
    public ImageElement addImage(int x, int y, int width, int height, int zIndex, int alpha, int color, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation) {
        ImageElement i = new ImageElement(x, y, width, height, zIndex, alpha, color, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, (float) rotation);
        synchronized (elements) {
            elements.add(i);
        }
        return i;
    }
    
    @Override
    public IScreen removeImage(ImageElement i) {
        synchronized (elements) {
            elements.remove(i);
        }
        return this;
    }

    @Override
    public RectElement addRect(int x1, int y1, int x2, int y2, int color) {
        RectElement r = new RectElement(x1, y1, x2, y2, color, 0F, 0);
        synchronized (elements) {
            elements.add(r);
        }
        return r;
    }

    @Override
    public RectElement addRect(int x1, int y1, int x2, int y2, int color, int alpha) {
        return addRect(x1, y1, x2, y2, color, alpha, 0, 0);
    }
    
    
    @Override
    public RectElement addRect(int x1, int y1, int x2, int y2, int color, int alpha, double rotation) {
        return addRect(x1, y1, x2, y2, color, alpha, rotation, 0);
    }
    
    @Override
    public RectElement addRect(int x1, int y1, int x2, int y2, int color, int alpha, double rotation, int zIndex) {
        RectElement r = new RectElement(x1, y1, x2, y2, color, alpha, (float) rotation, zIndex);
        synchronized (elements) {
            elements.add(r);
        }
        return r;
    }

    @Override
    public IScreen removeRect(RectElement r) {
        synchronized (elements) {
            elements.remove(r);
        }
        return this;
    }

    @Override
    public LineElement addLine(int x1, int y1, int x2, int y2, int color) {
        return addLine(x1, y1, x2, y2, color, 0);
    }

    @Override
    public LineElement addLine(int x1, int y1, int x2, int y2, int color, int zIndex) {
        return addLine(x1, y1, x2, y2, color, zIndex, 1);
    }

    @Override
    public LineElement addLine(int x1, int y1, int x2, int y2, int color, double width) {
        return addLine(x1, y1, x2, y2, color, 0, width);
    }

    @Override
    public LineElement addLine(int x1, int y1, int x2, int y2, int color, int zIndex, double width) {
        return addLine(x1, y1, x2, y2, color, zIndex, width, 0);
    }

    @Override
    public LineElement addLine(int x1, int y1, int x2, int y2, int color, double width, double rotation) {
        return addLine(x1, y1, x2, y2, color, 0, width, rotation);
    }

    @Override
    public LineElement addLine(int x1, int y1, int x2, int y2, int color, int zIndex, double width, double rotation) {
        LineElement r = new LineElement(x1, y1, x2, y2, color, (float) rotation, (float) width, zIndex);
        synchronized (elements) {
            elements.add(r);
        }
        return r;
    }

    @Override
    public IScreen removeLine(LineElement l) {
        synchronized (elements) {
            elements.remove(l);
        }
        return this;
    }

    @Override
    public ItemElement addItem(int x, int y, String id) {
        return addItem(x, y, 0, id, true, 1, 0);
    }
    
    @Override
    public ItemElement addItem(int x, int y, int zIndex, String id) {
        return addItem(x, y, zIndex, id, true, 1, 0);
    }
    
    @Override
    public ItemElement addItem(int x, int y, String id, boolean overlay) {
        return addItem(x, y, 0, id, overlay, 1, 0);
    }
    
    @Override
    public ItemElement addItem(int x, int y, int zIndex, String id, boolean overlay) {
        return addItem(x, y, zIndex, id, overlay, 1, 0);
    }
    
    @Override
    public ItemElement addItem(int x, int y, String id, boolean overlay, double scale, double rotation) {
        return addItem(x, y, 0, id, overlay, scale, rotation);
    }
    
    @Override
    public ItemElement addItem(int x, int y, int zIndex, String id, boolean overlay, double scale, double rotation) {
        ItemElement i = new ItemElement(x, y, zIndex, id, overlay, scale, (float) rotation);
        synchronized (elements) {
            elements.add(i);
        }
        return i;
    }
    
    @Override
    public ItemElement addItem(int x, int y, ItemStackHelper item) {
        return addItem(x, y, 0, item, true, 1, 0);
    }
    
    @Override
    public ItemElement addItem(int x, int y, int zIndex, ItemStackHelper item) {
        return addItem(x, y, zIndex, item, true, 1, 0);
    }
    
    @Override
    public ItemElement addItem(int x, int y, ItemStackHelper item, boolean overlay) {
        return addItem(x, y, 0, item, overlay, 1, 0);
    }
    
    @Override
    public ItemElement addItem(int x, int y, int zIndex, ItemStackHelper item, boolean overlay) {
        return addItem(x, y, zIndex, item, overlay, 1, 0);
    }
    
    @Override
    public ItemElement addItem(int x, int y, ItemStackHelper item, boolean overlay, double scale, double rotation) {
        return addItem(x, y, 0, item, overlay, scale, rotation);
    }
    
    @Override
    public ItemElement addItem(int x, int y, int zIndex, ItemStackHelper item, boolean overlay, double scale, double rotation) {
        ItemElement i = new ItemElement(x, y, zIndex, item, overlay, scale, (float) rotation);
        synchronized (elements) {
            elements.add(i);
        }
        return i;
    }

    @Override
    public IScreen removeItem(ItemElement i) {
        synchronized (elements) {
            elements.remove(i);
        }
        return this;
    }
    
    @Override
    public String getScreenClassName() {
        return IScreen.super.getScreenClassName();
    }
    
    @Override
    public String getTitleText() {
        return title.getString();
    }

    @Override
    public ClickableWidgetHelper<?, ?> addButton(int x, int y, int width, int height, String text,
                                              MethodWrapper<ClickableWidgetHelper<?, ?>, IScreen, Object, ?> callback) {
        return addButton(x, y, width, height, 0, text, callback);
    }
    
    @Override
    public ClickableWidgetHelper<?, ?> addButton(int x, int y, int width, int height, int zIndex, String text, MethodWrapper<ClickableWidgetHelper<?, ?>, IScreen, Object, ?> callback) {
        AtomicReference<ClickableWidgetHelper<?, ?>> b = new AtomicReference<>(null);
        ButtonWidget button = new ButtonWidget(x, y, width, height, Text.literal(text), (btn) -> {
            try {
                callback.accept(b.get(), this);
            } catch (Throwable e) {
                Core.getInstance().profile.logError(e);
            }
        });
        b.set(new ClickableWidgetHelper<>(button, zIndex));
        synchronized (elements) {
            elements.add(b.get());
            children.add(button);
        }
        return b.get();
    }


    @Override
    public CheckBoxWidgetHelper addCheckbox(int x, int y, int width, int height, String text, boolean checked, boolean showMessage, MethodWrapper<CheckBoxWidgetHelper, IScreen, Object, ?> callback) {
        return addCheckbox(x, y, width, height, 0, text, checked, showMessage, callback);
    }

    @Override
    public CheckBoxWidgetHelper addCheckbox(int x, int y, int width, int height, String text, boolean checked, MethodWrapper<CheckBoxWidgetHelper, IScreen, Object, ?> callback) {
        return addCheckbox(x, y, width, height, 0, text, checked, callback);
    }

    @Override
    public CheckBoxWidgetHelper addCheckbox(int x, int y, int width, int height, int zIndex, String text, boolean checked, MethodWrapper<CheckBoxWidgetHelper, IScreen, Object, ?> callback) {
        return addCheckbox(x, y, width, height, zIndex, text, checked, true, callback);
    }

    @Override
    public CheckBoxWidgetHelper addCheckbox(int x, int y, int width, int height, int zIndex, String text, boolean checked, boolean showMessage, MethodWrapper<CheckBoxWidgetHelper, IScreen, Object, ?> callback) {
        AtomicReference<CheckBoxWidgetHelper> ref = new AtomicReference<>(null);

        CheckBox checkbox = new CheckBox(x, y, width, height, Text.literal(text), checked, showMessage, (btn) -> {
            try {
                callback.accept(ref.get(), this);
            } catch (Exception e) {
                Core.getInstance().profile.logError(e);
            }
        });

        ref.set(new CheckBoxWidgetHelper(checkbox, zIndex));
        synchronized (elements) {
            elements.add(ref.get());
            children.add(checkbox);
        }
        return ref.get();
    }

    @Override
    public SliderWidgetHelper addSlider(int x, int y, int width, int height, String text, double value, MethodWrapper<SliderWidgetHelper, IScreen, Object, ?> callback, int steps) {
        return addSlider(x, y, width, height, 0, text, value, callback, steps);
    }

    @Override
    public SliderWidgetHelper addSlider(int x, int y, int width, int height, int zIndex, String text, double value, MethodWrapper<SliderWidgetHelper, IScreen, Object, ?> callback) {
        return addSlider(x, y, width, height, zIndex, text, value, callback, Integer.MAX_VALUE);
    }

    @Override
    public SliderWidgetHelper addSlider(int x, int y, int width, int height, String text, double value, MethodWrapper<SliderWidgetHelper, IScreen, Object, ?> callback) {
        return addSlider(x, y, width, height, 0, text, value, callback);
    }

    @Override
    public SliderWidgetHelper addSlider(int x, int y, int width, int height, int zIndex, String text, double value, MethodWrapper<SliderWidgetHelper, IScreen, Object, ?> callback, int steps) {
        AtomicReference<SliderWidgetHelper> ref = new AtomicReference<>(null);

        Slider slider = new Slider(x, y, width, height, Text.literal(text), value, (btn) -> {
            try {
                callback.accept(ref.get(), this);
            } catch (Exception e) {
                Core.getInstance().profile.logError(e);
            }
        }, steps);

        ref.set(new SliderWidgetHelper(slider, zIndex));
        synchronized (elements) {
            elements.add(ref.get());
            children.add(slider);
        }
        return ref.get();
    }

    @Override
    public ButtonWidgetHelper<TexturedButtonWidget> addTexturedButton(int x, int y, int width, int height, int textureStartX, int textureStartY, String texture, MethodWrapper<ButtonWidgetHelper<TexturedButtonWidget>, IScreen, Object, ?> callback) {
        return addTexturedButton(x, y, width, height, 0, textureStartX, textureStartY, height, texture, 256, 256, callback);
    }

    @Override
    public ButtonWidgetHelper<TexturedButtonWidget> addTexturedButton(int x, int y, int width, int height, int zIndex, int textureStartX, int textureStartY, String texture, MethodWrapper<ButtonWidgetHelper<TexturedButtonWidget>, IScreen, Object, ?> callback) {
        return addTexturedButton(x, y, width, height, zIndex, textureStartX, textureStartY, height, texture, 256, 256, callback);
    }
    
    @Override
    public ButtonWidgetHelper<TexturedButtonWidget> addTexturedButton(int x, int y, int width, int height, int textureStartX, int textureStartY, int hoverOffset, String texture, int textureWidth, int textureHeight, MethodWrapper<ButtonWidgetHelper<TexturedButtonWidget>, IScreen, Object, ?> callback) {
        return addTexturedButton(x, y, width, height, 0, textureStartX, textureStartY, hoverOffset, texture, textureWidth, textureHeight, callback);
    }

    @Override
    public ButtonWidgetHelper<TexturedButtonWidget> addTexturedButton(int x, int y, int width, int height, int zIndex, int textureStartX, int textureStartY, int hoverOffset, String texture, int textureWidth, int textureHeight, MethodWrapper<ButtonWidgetHelper<TexturedButtonWidget>, IScreen, Object, ?> callback) {
        AtomicReference<ButtonWidgetHelper<TexturedButtonWidget>> ref = new AtomicReference<>(null);

        TexturedButtonWidget texturedButton = new TexturedButtonWidget(x, y, width, height, textureStartX, textureStartY, hoverOffset, new Identifier(texture), textureWidth, textureHeight, (btn) -> {
            try {
                callback.accept(ref.get(), this);
            } catch (Exception e) {
                Core.getInstance().profile.logError(e);
            }
        });

        ref.set(new ButtonWidgetHelper<>(texturedButton, zIndex));
        synchronized (elements) {
            elements.add(ref.get());
            children.add(texturedButton);
        }
        return ref.get();
    }

    @Override
    public LockButtonWidgetHelper addLockButton(int x, int y, MethodWrapper<LockButtonWidgetHelper, IScreen, Object, ?> callback) {
        return addLockButton(x, y, 0, callback);
    }

    @Override
    public LockButtonWidgetHelper addLockButton(int x, int y, int zIndex, MethodWrapper<LockButtonWidgetHelper, IScreen, Object, ?> callback) {
        AtomicReference<LockButtonWidgetHelper> ref = new AtomicReference<>(null);
        LockButtonWidget lockButton = new LockButtonWidget(x, y, (btn) -> {
            try {
                callback.accept(ref.get(), this);
            } catch (Exception e) {
                Core.getInstance().profile.logError(e);
            }
        });
        ref.set(new LockButtonWidgetHelper(lockButton, zIndex));
        synchronized (elements) {
            elements.add(ref.get());
            children.add(lockButton);
        }
        return ref.get();
    }

    @Override
    public CyclingButtonWidgetHelper<?> addCyclingButton(int x, int y, int width, int height, MethodWrapper<CyclingButtonWidgetHelper<?>, IScreen, Object, ?> callback, String[] values, String initial) {
        return addCyclingButton(x, y, width, height, 0, callback, values, initial);
    }

    @Override
    public CyclingButtonWidgetHelper<?> addCyclingButton(int x, int y, int width, int height, int zIndex, MethodWrapper<CyclingButtonWidgetHelper<?>, IScreen, Object, ?> callback, String[] values, String initial) {
        return addCyclingButton(x, y, width, height, 0, callback, values, null, initial, null);
    }

    @Override
    public CyclingButtonWidgetHelper<?> addCyclingButton(int x, int y, int width, int height, int zIndex, MethodWrapper<CyclingButtonWidgetHelper<?>, IScreen, Object, ?> callback, String[] values, String[] alternatives, String initial, String prefix) {
        return addCyclingButton(x, y, width, height, 0, callback, values, alternatives, initial, prefix, null);
    }

    @Override
    public CyclingButtonWidgetHelper<?> addCyclingButton(int x, int y, int width, int height, int zIndex, MethodWrapper<CyclingButtonWidgetHelper<?>, IScreen, Object, ?> callback, String[] values, String[] alternatives, String initial, String prefix, MethodWrapper<?, ?, Boolean, ?> alternateToggle) {
        AtomicReference<CyclingButtonWidgetHelper<?>> ref = new AtomicReference<>(null);
        CyclingButtonWidget<String> cyclingButton;
        CyclingButtonWidget.Builder<String> builder = CyclingButtonWidget.builder(Text::literal);
        if (alternatives != null) {
            BooleanSupplier supplier = alternateToggle == null ? Screen::hasAltDown : alternateToggle::get;
            builder.values(supplier, Arrays.asList(values), Arrays.asList(alternatives));
        } else {
            builder.values(values);
        }
        builder.initially(initial);

        if (prefix == null || prefix.isBlank()) {
            builder.omitKeyText();
        }

        cyclingButton = builder.build(x, y, width, height, Text.literal(prefix), (btn, val) -> {
            try {
                callback.accept(ref.get(), this);
            } catch (Exception e) {
                Core.getInstance().profile.logError(e);
            }
        });
        ref.set(new CyclingButtonWidgetHelper<>(cyclingButton, zIndex));
        synchronized (elements) {
            elements.add(ref.get());
            children.add(cyclingButton);
        }
        return ref.get();
    }
    
    @Override
    public IScreen removeButton(ClickableWidgetHelper<?, ?> btn) {
        synchronized (elements) {
            elements.remove(btn);
            this.children.remove(btn.getRaw());
        }
        return this;
    }

    @Override
    public TextFieldWidgetHelper addTextInput(int x, int y, int width, int height, String message,
        MethodWrapper<String, IScreen, Object, ?> onChange) {
        return addTextInput(x, y, width, height, 0, message, onChange);
    }
    
    @Override
    public TextFieldWidgetHelper addTextInput(int x, int y, int width, int height, int zIndex, String message, MethodWrapper<String, IScreen, Object, ?> onChange) {
        TextFieldWidget field = new TextFieldWidget(this.textRenderer, x, y, width, height, Text.literal(message));
        if (onChange != null) {
            field.setChangedListener(str -> {
                try {
                    onChange.accept(str, this);
                } catch (Throwable e) {
                    Core.getInstance().profile.logError(e);
                }
            });
        }
        TextFieldWidgetHelper w = new TextFieldWidgetHelper(field, zIndex);
        synchronized (elements) {
            elements.add(w);
            children.add(field);
        }
        return w;
    }
    
    @Override
    public IScreen removeTextInput(TextFieldWidgetHelper inp) {
        synchronized (elements) {
            elements.remove(inp);
            children.remove(inp.getRaw());
        }
        return this;
    }

    @Intrinsic
    public void soft$close() {
        onClose();
    }

    @Override
    public IScreen setOnMouseDown(MethodWrapper<Pos2D, Integer, Object, ?> onMouseDown) {
        this.onMouseDown = onMouseDown;
        return this;
    }

    @Override
    public IScreen setOnMouseDrag(MethodWrapper<Vec2D, Integer, Object, ?> onMouseDrag) {
        this.onMouseDrag = onMouseDrag;
        return this;
    }

    @Override
    public IScreen setOnMouseUp(MethodWrapper<Pos2D, Integer, Object, ?> onMouseUp) {
        this.onMouseUp = onMouseUp;
        return this;
    }

    @Override
    public IScreen setOnScroll(MethodWrapper<Pos2D, Double, Object, ?> onScroll) {
        this.onScroll = onScroll;
        return this;
    }

    @Override
    public IScreen setOnKeyPressed(MethodWrapper<Integer, Integer, Object, ?> onKeyPressed) {
        this.onKeyPressed = onKeyPressed;
        return this;
    }

    @Override
    public IScreen setOnInit(MethodWrapper<IScreen, Object, Object, ?> onInit) {
        this.onInit = onInit;
        return this;
    }

    @Override
    public IScreen setOnFailInit(MethodWrapper<String, Object, Object, ?> catchInit) {
        this.catchInit = catchInit;
        return this;
    }
    
    @Override
    public IScreen setOnClose(MethodWrapper<IScreen, Object, Object, ?> onClose) {
        this.onClose = onClose;
        return this;
    }
    
    @Override
    public IScreen reloadScreen() {
        client.execute(() -> client.setScreen((Screen) (Object) this));
        return this;
    }


    @Override
    public ButtonWidgetHelper.ButtonBuilder buttonBuilder() {
        return new ButtonWidgetHelper.ButtonBuilder(this);
    }

    @Override
    public CheckBoxWidgetHelper.CheckBoxBuilder checkBoxBuilder() {
        return new CheckBoxWidgetHelper.CheckBoxBuilder(this);
    }

    @Override
    public CyclingButtonWidgetHelper.CyclicButtonBuilder<?> cyclicButtonBuilder(MethodWrapper<Object, ?, TextHelper, ?> valueToText) {
        return new CyclingButtonWidgetHelper.CyclicButtonBuilder<>(this, valueToText);
    }

    @Override
    public LockButtonWidgetHelper.LockButtonBuilder lockButtonBuilder() {
        return new LockButtonWidgetHelper.LockButtonBuilder(this);
    }

    @Override
    public SliderWidgetHelper.SliderBuilder sliderBuilder() {
        return new SliderWidgetHelper.SliderBuilder(this);
    }

    @Override
    public TextFieldWidgetHelper.TextFieldBuilder textFieldBuilder() {
        return new TextFieldWidgetHelper.TextFieldBuilder(this, textRenderer);
    }

    @Override
    public ButtonWidgetHelper.TexturedButtonBuilder texturedButtonBuilder() {
        return new ButtonWidgetHelper.TexturedButtonBuilder(this);
    }

    @Override
    public void onRenderInternal(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (matrices == null) return;

        synchronized (elements) {
            Iterator<RenderCommon.RenderElement> iter = elements.stream().sorted(Comparator.comparingInt(RenderCommon.RenderElement::getZIndex)).iterator();
            TextElement hoverText = null;

            while (iter.hasNext()) {
                RenderCommon.RenderElement e = iter.next();
                e.render(matrices, mouseX, mouseY, delta);
                if (e instanceof TextElement) {
                    TextElement t = (TextElement) e;
                    if (mouseX > t.x && mouseX < t.x + t.width && mouseY > t.y && mouseY < t.y + textRenderer.fontHeight) {
                        hoverText = t;
                    }
                }
            }

            if (hoverText != null) {
                renderTextHoverEffect(matrices, textRenderer.getTextHandler().getStyleAt(hoverText.text, mouseX - hoverText.x), mouseX, mouseY);
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "render")
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info) {
        onRenderInternal(matrices, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (onMouseDown != null) try {
            onMouseDown.accept(new PositionCommon.Pos2D(mouseX, mouseY), button);
        } catch (Throwable e) {
            Core.getInstance().profile.logError(e);
        }
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        TextElement hoverText = null;

        synchronized (elements) {
            for (RenderCommon.RenderElement e : elements) {
                if (e instanceof TextElement) {
                    TextElement t = (TextElement) e;
                    if (mouseX > t.x && mouseX < t.x + t.width && mouseY > t.y && mouseY < t.y + textRenderer.fontHeight) {
                        hoverText = t;
                    }
                }
            }
        }

        if (hoverText != null) {
            return handleTextClick(textRenderer.getTextHandler().getStyleAt(hoverText.text, (int) mouseX - hoverText.x));
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (onMouseDrag != null) try {
            onMouseDrag.accept(new PositionCommon.Vec2D(mouseX, mouseY, deltaX, deltaY), button);
        } catch (Throwable e) {
            Core.getInstance().profile.logError(e);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (onMouseUp != null) try {
            onMouseUp.accept(new PositionCommon.Pos2D(mouseX, mouseY), button);
        } catch (Throwable e) {
            Core.getInstance().profile.logError(e);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Inject(at = @At("HEAD"), method = "keyPressed")
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> info) {
        if (onKeyPressed != null) try {
            onKeyPressed.accept(keyCode, modifiers);
        } catch (Throwable e) {
            Core.getInstance().profile.logError(e);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (onScroll != null) try {
            onScroll.accept(new PositionCommon.Pos2D(mouseX, mouseY), amount);
        } catch (Throwable e) {
            Core.getInstance().profile.logError(e);
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    
    @Inject(at = @At("RETURN"), method = "init()V")
    protected void init(CallbackInfo info) {
        synchronized (elements) {
            elements.clear();
        }
        client.keyboard.setRepeatEvents(true);
        if (onInit != null) {
            try {
                onInit.accept(this);
            } catch (Throwable e) {
                try {
                    if (catchInit != null) catchInit.accept(e.toString());
                    else throw e;
                } catch (Throwable f) {
                    Core.getInstance().profile.logError(f);
                }
            }
        }
        getDraw2Ds().forEach(e -> e.getDraw2D().init());
    }
    
    //TODO: switch to enum extention with mixin 9.0 or whenever Mumfrey gets around to it
    @Inject(at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V", remap = false), method = "handleTextClick", cancellable = true)
    public void handleCustomClickEvent(Style style, CallbackInfoReturnable<Boolean> cir) {
        ClickEvent clickEvent = style.getClickEvent();
        if (clickEvent instanceof CustomClickEvent) {
            ((CustomClickEvent) clickEvent).getEvent().run();
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Override
    public MethodWrapper<IScreen, Object, Object, ?> getOnClose() {
        return onClose;
    }

    @Override
    public int getZIndex() {
        return 0;
    }
    
}
